package com.kc.apollo.util;

/**
 * Created by lijunying on 16/11/10.
 */
public class SqlStringFormater {

    public static String formatSql(String sql, Object[] objects){
        String a = null;
        for(Object o : objects){
            a = sql.replaceFirst("[?]","\""+String.valueOf(o)+"\"");
            sql = a;
        }
        return a;
    }

}
