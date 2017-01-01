package com.kc.apollo.index;

import com.kc.apollo.util.Constants;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;

/**
 * @COPYRIGHT (C) 2017 Schenker AG
 * <p>
 * All rights reserved
 */

public class LuceneIndexHolder {
    private static LuceneIndexHolder ourInstance = new LuceneIndexHolder();

    public static LuceneIndexHolder getInstance() {
        return ourInstance;
    }

    private LuceneIndexHolder() {
    }

    private static Directory directory = null;

    //获得Lucene工作目录
    public Directory getDirectory(){
        if(directory == null){
            try {
                directory = FSDirectory.open(new File(Constants.LUCENE_INDEX_FOLDER));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory;
    }
}
