package com.kc.apollo.util;

import org.ansj.domain.Term;
import org.ansj.recognition.NatureRecognition;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.cfg.DefaultConfig;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 提供分词工具，当前根据ansj-来进行分词
 * Created by lijunying on 16/10/29.
 */

public class WordSpliter {

    private static class SingletonHolder {
        private static final WordSpliter INSTANCE = new WordSpliter();
    }
    private WordSpliter(){}

    public static final WordSpliter getInstance(){
        return SingletonHolder.INSTANCE;
    }

    /**
     *
     * @param keywords 待分词数据
     * @return 分词结果
     */
    public List<String> getWordListAfterSplit(String keywords){
        Configuration cfg = DefaultConfig.getInstance();
        System.out.println(cfg.getMainDictionary()); // 系统默认词库
        System.out.println(cfg.getQuantifierDicionary());
        List<String> list = new ArrayList<String>();
        StringReader input = new StringReader(keywords.trim());
        IKSegmenter ikSeg = new IKSegmenter(input, false);   // true 用智能分词 ，false细粒度
        try {
            for (Lexeme lexeme = ikSeg.next(); lexeme != null; lexeme = ikSeg.next()) {
                list.add(lexeme.getLexemeText());
            }
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

}
