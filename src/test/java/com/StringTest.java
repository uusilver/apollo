package com;

import org.junit.Test;

/**
 * Created by lijunying on 16/12/17.
 */
public class StringTest {

    @Test
    public void testString(){
        String link = "./13123.html";
        if(link.startsWith("."))
            link = link.substring(1,link.length());
        System.out.println(link);
    }
}
