package com.kc.apollo.util;

import java.util.List;

/**
 * Created by lijunying on 16/11/10.
 */
public class SqlStringFormatterTest {

    public static void main(String args[]){
//        String sql2 = "select visited_url from apollo_visit_history where visited_url=?";
//        Object[] objects2 = new Object[]{"http://www.baidu.com"};
//
//        System.out.println(SqlStringFormater.formatSql(sql2, objects2));
//
//        String sql1 = "insert into apollo_visit_history (visited_url, date) values (?, ?)";
//
//        Object[] objects = new Object[]{"http://www.baidu.com", DataHelper.getCurrentTimeStamp()};
//
//        System.out.println(SqlStringFormater.formatSql(sql1, objects));

        List<String> list = WordSpliter.getInstance().getWordListAfterSplit("GC16440204448 125 中山市荔景饮料有限公司贝特爱思  中山市坦洲镇前进村枝埔队荔景街11号 中山市荔景饮料有限公司 广东 永怡饮用纯净水 18.9升/瓶 2016-09-10 饮料 2016年第55期 总89期 2016.12.15 广东/总局国抽");
        for(String str : list){
            System.out.println(str);

        }
    }
}
