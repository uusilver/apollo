package com.kc.apollo.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wltea.analyzer.core.Lexeme;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 创建字典文件，用于分词搜索
 * Created by lijunying on 16/12/16.
 */
public class DicFileGenerator {

    private static Log logger = LogFactory.getLog(DicFileGenerator.class);

    public static void main(String[] args) throws IOException {
        generateDicFile(getDicContent(), "315kc.dic");

    }


    /**
     * 获得数据库中表apollo_brand_company_collection内的品牌名和生成厂家名
     * @return
     */
    public  static Set<String> getDicContent(){
        Set<String> set = new HashSet<>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = DBUtil.getConnection();
            String sql = "select brand_name, brand_company from apollo_brand_company_collection";
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                String brand = rs.getString("brand_name");
                String company = rs.getString("brand_company");
                if(brand!=null && brand.length()>0)
                    set.add(brand);
                if(company!=null && company.length()>0)
                    set.add(company);
            }

        }catch (Exception e){
            logger.error(e.getMessage());
        }finally {
            DBUtil.closeConnect(rs, ps, connection);
        }
        return set;

    }
    public static void generateDicFile(Set<String> set, String dicName){
        FileWriter writer = null;
        try {
            writer = new FileWriter(dicName);
            for(String str : set){
                writer.write(str);
                writer.write("\r\n");//写入换行
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                writer.flush();
                writer.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("315kc字典生成完毕!");
    }
}
