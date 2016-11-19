package com.kc.apollo.util;

import org.ansj.domain.Term;
import org.ansj.recognition.NatureRecognition;
import org.ansj.splitWord.analysis.ToAnalysis;

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
    public List<Term> getWordListAfterSplit(String keywords){
        List<Term> termsList = null;
        termsList = ToAnalysis.parse(keywords);
        new NatureRecognition(termsList).recognition();

        return termsList;
    }

}
