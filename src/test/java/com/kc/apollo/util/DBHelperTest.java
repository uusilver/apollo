package com.kc.apollo.util;

import com.kc.apollo.types.DBTypes;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by lijunying on 16/11/9.
 */
public class DBHelperTest {

//    @Test
//    public void test() throws Exception {
//        String sql1 = "insert into apollo_visit_history (visited_url, date) values (?, ?)";
//
//        List<DBTypes> list = Arrays.asList(DBTypes.STRING, DBTypes.DATE);
//
//        Object[] objects = new Object[]{"http://www.baidu.com", DataHelper.getCurrentTimeStamp()};
//
//        DBHelper.getInstance().insertTable(sql1, list, objects);
//
//        String sql2 = "select visited_url from apollo_visit_history where visited_url=?";
//
//        List<DBTypes> list2 = Arrays.asList(DBTypes.STRING);
//
//        Object[] objects2 = new Object[]{"http://www.baidu.com"};
//
//        boolean flag = DBHelper.getInstance().isExistData(sql2, list2, objects2);
//        System.out.println(flag);
//
//        String sql3 = "update apollo_html_content_collection set invert_index_flag='N' where uuid = ?";
//
//        List<DBTypes> list3 = Arrays.asList(DBTypes.STRING);
//
//        Object[] objects3= new Object[]{"0208adbf-52ba-461f-a5cb-92bf23d912b3"};
//
//        DBHelper.getInstance().updateTable(sql3, list3, objects3);
//
//        Object[][] result = DBHelper.getInstance().loadApolloHtmlTableDataWithNumber(100);
//        for(Object[] row : result){
//            for(Object o : row){
//                System.out.print(o+ " | ");
//            }
//            System.out.println();
//        }
//    }

    @Test
    public void testQuery(){
        String sql  = "select * from apollo_hot_search";
        try {
            Object[][] result = DBHelper.getInstance().queryResultFromDatabase(sql, null, null);
            for(Object[] objects : result){
                for(Object o : objects){
                    System.out.print(o + " | ");
                }
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
