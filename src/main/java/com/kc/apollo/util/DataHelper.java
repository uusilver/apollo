package com.kc.apollo.util;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by lijunying on 16/11/9.
 */
public class DataHelper {

    public static Timestamp getCurrentTimeStamp(){
        Date date = new Date();
        return  new Timestamp(date.getTime());
    }
}
