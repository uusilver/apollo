package com.kc.apollo;

import com.kc.apollo.util.FileUtils;
import org.junit.Test;

/**
 * Created by lijunying on 16/12/18.
 */
public class FileUtilsTest {

    @Test
    public void testDeleteFile(){
        FileUtils.delectFile("RevertIndex.run");
    }
}
