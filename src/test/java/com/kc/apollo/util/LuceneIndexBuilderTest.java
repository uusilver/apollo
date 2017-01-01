package com.kc.apollo.util;
/**
 * @COPYRIGHT (C) 2017 Schenker AG
 * <p>
 * All rights reserved
 */


import com.kc.apollo.index.LuceneIndexBuilder;
import org.junit.Test;

/**
 * TODO The class LuceneIndexBuilderTest is supposed to be documented...
 *
 * @author Vani Li
 */
public class LuceneIndexBuilderTest {
   @Test
    public void buildIndexTest(){
       LuceneIndexBuilder.getInstance().buildIndex();
   }
}
