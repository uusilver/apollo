package com.kc.apollo.util;

import com.kc.apollo.model.NewsModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lijunying on 16/12/19.
 */
public class NewsGenerator {

    private static Log logger = LogFactory.getLog(NewsGenerator.class);

    /**
     * 获得数据库中表apollo_html_content_collection内的关键字，内容摘要以及链接
     * @return
     */
    public  static List<NewsModel> getNewsContent(){
        List<NewsModel> list = new ArrayList<>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = DBUtil.getConnection();
            String sql = "select title, body_content, original_url,create_date from apollo_html_content_collection where title is not null and body_content is not null and remark='website' limit 10";
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                String keywords = rs.getString("title");
                String body_Content = rs.getString("body_Content");
                String original_url = rs.getString("original_url");
                String createDate = rs.getString("create_date");
                NewsModel newsModel = new NewsModel(keywords, original_url, body_Content, createDate);
                list.add(newsModel);
            }

        }catch (Exception e){
            logger.error(e.getMessage());
        }finally {
            DBUtil.closeConnect(rs, ps, connection);
        }
        return list;

    }
}
