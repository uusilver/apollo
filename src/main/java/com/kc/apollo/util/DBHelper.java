package com.kc.apollo.util;

import com.kc.apollo.types.DBTypes;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lijunying on 16/11/9.
 * 单例
 * 这个类 用于封装所有的数据库增删改查等操作，尽量不再需要操作别的类
 */
public class DBHelper {

    Log logger = LogFactory.getLog(DBHelper.class);

    private static DBHelper instance = null;

    static {
        instance = new DBHelper();
    }

    private DBHelper(){
    }

    public static  DBHelper getInstance(){
        if(instance != null){
            return instance;
        }
        return new DBHelper();
    }

    public void insertTable(String sql, List<DBTypes> types, Object[] objects) throws Exception {
        if (types.size() != objects.length){
            throw new Exception("Types and Objects must have same number");
        }
        logger.info("数据库执行插入操作:"+SqlStringFormater.formatSql(sql, objects));
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = DBUtil.getConnection();
            ps = connection.prepareStatement(sql);
            ps = setPreparementValuesBasedOnTypes(types, objects, ps);
            ps.execute();
        }catch (Exception e){
            logger.error(e.getMessage());
            throw new Exception("Operation Error");
        }finally {
            DBUtil.closeConnect(null, ps, connection);
        }

    }

    /**
     * 更新数据
     * @param sql
     * @param types
     * @param objects
     * @return
     * @throws Exception
     */
    public void updateTable(String sql, List<DBTypes> types, Object[] objects) throws Exception {
        if (types.size() != objects.length){
            throw new Exception("Types and Objects must have same number");
        }
        logger.info("数据库执行更新操作:"+SqlStringFormater.formatSql(sql, objects));
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = DBUtil.getConnection();
            ps = connection.prepareStatement(sql);
            ps = setPreparementValuesBasedOnTypes(types, objects, ps);
            ps.execute();
        }catch (Exception e){
            logger.error(e.getMessage());
        }finally {
            DBUtil.closeConnect(null, ps, connection);
        }
    }

    public boolean isExistData(String sql, List<DBTypes> types, Object[] objects) throws Exception {
        if (types.size() != objects.length){
            throw new Exception("Types and Objects must have same number");
        }
        logger.info("数据库执行查询操作:"+SqlStringFormater.formatSql(sql, objects));
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = DBUtil.getConnection();
            ps = connection.prepareStatement(sql);
            ps = setPreparementValuesBasedOnTypes(types, objects, ps);
            rs = ps.executeQuery();
            return rs.next() ? true : false;
        }catch (Exception e){
            logger.error(e.getMessage());
        }finally {
            DBUtil.closeConnect(rs, ps, connection);
        }
        return false;
    }


    /**
     * 每批次只处理100条数据
     * @return
     */
    public Object[][] loadApolloHtmlTableDataWithTop100(){
        String sql = "select uuid, title, original_url, body_content from apollo_html_content_collection where invert_index_flag='N' limit 100";
        logger.info("数据库执行查询操作:"+sql);
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = DBUtil.getConnection();
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();
            Object[][] result = new Object[100][4];
            int index = 0;
            while (rs.next()){
                Object[] row = new Object[4];
                row[0] = rs.getString("uuid");
                row[1] = rs.getString("title");
                row[2] = rs.getString("original_url");
                row[3] = rs.getString("body_content");
                result[index] = row;
                index++;
            }

            return result;
        }catch (Exception e){
            logger.error(e.getMessage());
        }finally {
            DBUtil.closeConnect(rs, ps, connection);
        }
        return null;
    }



    //Prepare Statement can not be changed
    private PreparedStatement setPreparementValuesBasedOnTypes(List<DBTypes> types, Object[] objects, final PreparedStatement ps)
            throws SQLException {
        for(int i = 1; i<=objects.length; i++){
            DBTypes type = types.get(i-1); //start from 1, then -1 get right index
            switch (type){
                case STRING:
                    ps.setString(i, (String)objects[i-1]);
                    break;

                case DATE:
                    ps.setTimestamp(i, (Timestamp) objects[i - 1]);
                    break;

                case INTEGER:
                    ps.setInt(i, (Integer) objects[i - 1]);
                    break;

                default: break;
            }
        }

        return ps;
    }
}
