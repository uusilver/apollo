package com.kc.apollo.util;
/**
 * @COPYRIGHT (C) 2017 Schenker AG
 * <p>
 * All rights reserved
 */
import com.kc.apollo.index.LuceneIndexBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * TODO The class LuceneIndexServlet is supposed to be documented...
 *
 * @author Vani Li
 */
public class LuceneIndexServlet extends  HttpServlet {
    @Override
    public void init() throws ServletException {
        // TODO Auto-generated method stub
        super.init();
        LuceneIndexBuilder.getInstance().buildIndex();
    }
}
