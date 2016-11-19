package com.kc.apollo.util;

/**
 * Created by lijunying on 16/11/10.
 */
public class SqlStringFormatterTest {

    public static void main(String args[]){
        String sql2 = "select visited_url from apollo_visit_history where visited_url=?";
        Object[] objects2 = new Object[]{"http://www.baidu.com"};

        System.out.println(SqlStringFormater.formatSql(sql2, objects2));

        String sql1 = "insert into apollo_visit_history (visited_url, date) values (?, ?)";

        Object[] objects = new Object[]{"http://www.baidu.com", DataHelper.getCurrentTimeStamp()};

        System.out.println(SqlStringFormater.formatSql(sql1, objects));

    }
}
