package com.kc.apollo.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijunying on 16/12/19.
 */
public class HotSearchKeyGenerator {

    private static Log logger = LogFactory.getLog(HotSearchKeyGenerator.class);

    /**
     * 获得数据库中表apollo_hot_search内的热门关键字按id排序
     * @return
     */
    public  static List<String> getHotSearchKey(){
        List<String> list = new ArrayList<>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = DBUtil.getConnection();
            String sql = "select search_keywords from apollo_hot_search order by id limit 5";
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                String keywords = rs.getString("search_keywords");
                list.add(keywords);
            }

        }catch (Exception e){
            logger.error(e.getMessage());
        }finally {
            DBUtil.closeConnect(rs, ps, connection);
        }
        return list;

    }
}
