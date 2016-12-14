package com.kc.apollo.util; /**
 * @COPYRIGHT (C) 2016 Schenker AG
 * <p>
 * All rights reserved
 */



import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.cfg.DefaultConfig;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO The class IkAnal is supposed to be documented...
 *
 * @author Vani Li
 */
public class IkAnal {
    public static void main(String[] args) throws IOException {
        String s = "'上海东升焊接集团有限公司 上海市 直流弧焊机 国家中小电机质量监督检验中心";
        queryWords(s);
    }

    public static void queryWords(String query) throws IOException {
        Configuration cfg = DefaultConfig.getInstance();
        System.out.println(cfg.getMainDictionary()); // 系统默认词库
        System.out.println(cfg.getQuantifierDicionary());
        List<String> list = new ArrayList<String>();
        StringReader input = new StringReader(query.trim());
        IKSegmenter ikSeg = new IKSegmenter(input, true);   // true 用智能分词 ，false细粒度
        for (Lexeme lexeme = ikSeg.next(); lexeme != null; lexeme = ikSeg.next()) {
            System.out.print(lexeme.getLexemeText() + "|");
        }
    }
}
