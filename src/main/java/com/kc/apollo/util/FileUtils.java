package com.kc.apollo.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by lijunying on 16/12/18.
 * 文件操作类
 */
public class FileUtils {

    private static Log logger = LogFactory.getLog(FileUtils.class);

    public static void createFile(String fileName) {
        File file = new File(fileName);
        //File file=new File("D:\\IO\\file01.txt"); Windows下可以使用
        try {
            if(file.exists()){
                throw new RuntimeException(fileName+"执行文件存在，请检查");
            }
            file.createNewFile();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public static void delectFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        } else {
            logger.info("文件不存在");
        }
    }
}
