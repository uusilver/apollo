package com.kc.apollo;

import com.google.gson.Gson;
import com.kc.apollo.index.LuceneIndexHolder;
import com.kc.apollo.lucene.LuceneSearchManager;
import com.kc.apollo.model.SearchObject;
import com.kc.apollo.model.SearchResult;
import com.kc.apollo.spider.fixer.BaiduRealTimeWorker;
import com.kc.apollo.types.DBTypes;
import com.kc.apollo.util.DBHelper;
import com.kc.apollo.util.DBUtil;
import com.kc.apollo.util.DataHelper;
import com.kc.apollo.util.WordSpliter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by lijunying on 16/10/19.
 */
@Controller
public class SearchController {

    Log logger = LogFactory.getLog(SearchController.class);

    //TODO 从Properties文件中读取
    private final int pageSize = 10;

    @RequestMapping(value="/search",method = RequestMethod.POST)
    @ResponseBody
    public String search(SearchObject searchObject) throws SQLException, IOException, InvalidTokenOffsetsException {
            long start = System.currentTimeMillis();

        String keywords = searchObject.getKeywords();
        int pageNo = searchObject.getPageNo();

        //是否保存搜索结果
        boolean saveSearchFlag = false;
        //结果是否存在 在315快查的数据库内
        String hasResult = "Y";

        List<String> keyWordsList = WordSpliter.getInstance().getWordListAfterSplit(keywords);

        Map<String, String> map = LuceneSearchManager.getInstance().search(LuceneIndexHolder.getInstance().getDirectory(), keyWordsList, 30);
        Set<String> set = map.keySet();
        String[] strings = new String[set.size()];
        String questionMark = "";
        String sql = null;
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        SearchResult searchResult = new SearchResult();
        int index = 0;
        for (String uuid : set) {
            strings[index] = uuid;
            questionMark+="?,";
            index++;
        }
        if(questionMark.length()>0) {
            questionMark = questionMark.substring(0, questionMark.length() - 1);

            int pageLimitStart = pageNo * pageSize;
            int pageLimitEnd = pageLimitStart + pageSize;

            sql = "select uuid, original_url, create_date, body_content from apollo_html_content_collection where uuid in(" + questionMark + ") limit " + pageLimitStart + "," + pageLimitEnd;

            logger.info("数据库执行查询操作:" + sql);
            connection = DBUtil.getConnection();
            ps = connection.prepareStatement(sql);
            for (int i = 0; i < strings.length; i++) {
                ps.setString(i + 1, strings[i]);
            }
            rs = ps.executeQuery();

            searchResult = new SearchResult();
            Set<SearchResult.SearchItem> searchItemSet = new HashSet<SearchResult.SearchItem>();
            while (rs.next()) {
                SearchResult.SearchItem item = new SearchResult.SearchItem();
                item.setTitle(map.get(rs.getString("uuid")));
                item.setUrl(rs.getString("original_url"));
                item.setCreate_date(rs.getTimestamp("create_date").toString());
                item.setBody_content(rs.getString("body_content"));
                item.setSource("315快查");
                searchItemSet.add(item);
            }
            searchResult.setSearchItemSet(searchItemSet);

            //结果集不包含条数才需要进行重新计算
            if(searchObject.getTotalResult()==0) {
                sql = "select count(uuid) as totalCount from apollo_html_content_collection where uuid in(" + questionMark + ")";
                ps = connection.prepareStatement(sql);
                for(int i =0 ; i<strings.length; i++){
                    ps.setString(i+1, strings[i]);
                }
                rs = ps.executeQuery();
                if (rs.next()) {
                    int count = rs.getInt("totalCount");
                    searchResult.setTotalResult(count);
                }

                saveSearchFlag = true;
            }
        }

        long end = System.currentTimeMillis();
        long timeCost = end-start;
        searchResult.setExecuteTime(timeCost);
        logger.info("\""+keywords+"\"的分词解析耗时:" + timeCost+"毫秒");

        //如果此处从315快查数据依然为空，我们则用fixer包下的搜索来进行结果替代


        //将搜索词保存到数据库内
        if(saveSearchFlag){
            insertSearchKeyWordsIntoDatabse(keywords, hasResult);
        }

        return new Gson().toJson(searchResult);

    }



    private void insertSearchKeyWordsIntoDatabse(String keywords, String resultFlag){
        String sql1 = "insert into apollo_user_search_history (UUID, search_keywords, search_date, result_flag) values (?, ?, ?, ?)";

        List<DBTypes> list = Arrays.asList(DBTypes.STRING, DBTypes.STRING, DBTypes.DATE, DBTypes.STRING);

        Object[] objects = new Object[]{UUID.randomUUID().toString(), keywords,DataHelper.getCurrentTimeStamp(), resultFlag};

        try {
            DBHelper.getInstance().insertTable(sql1, list, objects);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
