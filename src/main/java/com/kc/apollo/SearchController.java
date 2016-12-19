package com.kc.apollo;

import com.google.gson.Gson;
import com.kc.apollo.model.SearchObject;
import com.kc.apollo.model.SearchResult;
import com.kc.apollo.spider.fixer.BaiduRealTimeWorker;
import com.kc.apollo.types.DBTypes;
import com.kc.apollo.util.DBHelper;
import com.kc.apollo.util.DBUtil;
import com.kc.apollo.util.DataHelper;
import com.kc.apollo.util.WordSpliter;
import org.ansj.domain.Term;
import org.ansj.recognition.NatureRecognition;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
    public String search(SearchObject searchObject) throws SQLException, IOException {
            long start = System.currentTimeMillis();

        String keywords = searchObject.getKeywords();
        int pageNo = searchObject.getPageNo();

        //是否保存搜索结果
        boolean saveSearchFlag = false;
        //结果是否存在 在315快查的数据库内
        String hasResult = "Y";

        List<String> list = WordSpliter.getInstance().getWordListAfterSplit(keywords);

        String[] strings = new String[list.size()];
        String questionMark = "";
        for (int i=0; i<list.size(); i++) {
            strings[i] = list.get(i);
            questionMark+="?,";
        }
        questionMark = questionMark.substring(0,questionMark.length()-1);

        int pageLimitStart = pageNo*pageSize;
        int pageLimitEnd = pageLimitStart+pageSize;

        String sql = "select full_text, url_address, create_date, body_content from apollo_invert_index where hot_word in("+questionMark+") limit "+ pageLimitStart +","+pageLimitEnd;
        logger.info("数据库执行查询操作:"+sql);
        Connection connection = DBUtil.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql);
        for(int i =0 ; i<strings.length; i++){
            ps.setString(i+1, strings[i]);
        }
        ResultSet rs = ps.executeQuery();

        SearchResult searchResult = new SearchResult();
        Set<SearchResult.SearchItem> searchItemSet = new HashSet<SearchResult.SearchItem>();
        while (rs.next()){
            SearchResult.SearchItem item = new SearchResult.SearchItem();
            item.setTitle(rs.getString("full_text"));
            item.setUrl(rs.getString("url_address"));
            item.setCreate_date(rs.getTimestamp("create_date").toString());
            item.setBody_content(rs.getString("body_content"));
            item.setSource("315快查");
            searchItemSet.add(item);
        }
        searchResult.setSearchItemSet(searchItemSet);

        //结果集不包含条数才需要进行重新计算
        if(searchObject.getTotalResult()==0) {
            sql = "select count(uuid) as totalCount from apollo_invert_index where hot_word in(" + questionMark + ")";
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
        long end = System.currentTimeMillis();
        long timeCost = end-start;
        searchResult.setExecuteTime(timeCost);
        logger.info("\""+keywords+"\"的分词解析耗时:" + timeCost+"毫秒");

        //如果此处从315快查数据依然为空，我们则用fixer包下的搜索来进行结果替代
        if(searchResult.getSearchItemSet()==null || searchResult.getSearchItemSet().size()== 0){
            searchResult = new BaiduRealTimeWorker().baiduWorker(keywords);

            hasResult = "N";
        }


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
