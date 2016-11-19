package com.kc.apollo;

import com.google.gson.Gson;
import com.kc.apollo.model.SearchObject;
import com.kc.apollo.model.SearchResult;
import com.kc.apollo.util.DBUtil;
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lijunying on 16/10/19.
 */
@Controller
public class SearchController {

    Log logger = LogFactory.getLog(SearchController.class);

    //TODO 从Properties文件中读取
    private int pageSize = 10;

    @RequestMapping(value="/search",method = RequestMethod.POST)
    @ResponseBody
    public String search(SearchObject searchObject) throws SQLException {
        long start = System.currentTimeMillis();

        String keywords = searchObject.getKeywords();
        int pageNo = searchObject.getPageNo();

        List<Term> termsList = null;
        termsList = ToAnalysis.parse(keywords);
        new NatureRecognition(termsList).recognition();

        String[] strings = new String[termsList.size()];
        String questionMark = "";
        for (int i=0; i<termsList.size(); i++) {
            strings[i] = termsList.get(i).getName();
            questionMark+="?,";
        }
        questionMark = questionMark.substring(0,questionMark.length()-1);
        System.out.println();
        long end = System.currentTimeMillis();
        logger.info("\""+keywords+"\"的分词解析耗时:" + (end - start)+"毫秒");

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
        Set<SearchResult> set = new HashSet<SearchResult>();
        while (rs.next()){
            SearchResult result = new SearchResult();
            result.setTitle(rs.getString("full_text"));
            result.setUrl(rs.getString("url_address"));
            result.setCreate_date(rs.getTimestamp("create_date").toString());
            result.setBody_content(rs.getString("body_content"));
            set.add(result);
        }
        return new Gson().toJson(set);

    }
}
