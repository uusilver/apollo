package com.kc.apollo.lucene;

import com.kc.apollo.model.LuceneIndexModel;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @COPYRIGHT (C) 2017 Schenker AG
 * <p>
 * All rights reserved
 */

public class LuceneSearchManager {
    private static LuceneSearchManager ourInstance = new LuceneSearchManager();

    public static LuceneSearchManager getInstance() {
        return ourInstance;
    }

    private LuceneSearchManager() {
    }

    /**
     * 搜索索引文件，提供多关键词搜索功能，且高亮关键词
     * @param directory 索引区
     * @param keywords 检索关键词
     * @param getResultNo 获取结果数量
     * @throws IOException
     * @throws InvalidTokenOffsetsException
     */
    public Map<String, String> search(Directory directory, List<String> keywords, int getResultNo) throws IOException, InvalidTokenOffsetsException {
        //搜索
        Map<String, String> map = new HashMap<>();
        Analyzer analyzer = new IKAnalyzer();

        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(indexReader);

        QueryParser parser = new QueryParser(Version.LUCENE_47, LuceneIndexModel.TEXT, analyzer);
        parser.setDefaultOperator(QueryParser.AND_OPERATOR);
        try {
            BooleanQuery booleanQuery = new BooleanQuery();
            for(String keyword : keywords){
                Query query = parser.parse(keyword);
                booleanQuery.add(query, BooleanClause.Occur.MUST);
            }
            TopDocs topdocs = searcher.search(booleanQuery, getResultNo);// 查询前100条

            // 高亮
            Formatter formatter = new SimpleHTMLFormatter("<font color='red'>", "</font>");// 高亮html格式
            Fragmenter fragmenter = new SimpleFragmenter(100);// 设置最大片断为100
            org.apache.lucene.search.highlight.Scorer score = new QueryScorer(booleanQuery);// 检索评份
            Highlighter highlighter = new Highlighter(formatter, score);// 高亮显示类
            highlighter.setTextFragmenter(fragmenter);// 设置格式
//            System.out.println("查询结果总数---" + topdocs.totalHits);
            ScoreDoc[] docs = topdocs.scoreDocs;
            for (ScoreDoc doc : docs) {
                Document d = searcher.doc(doc.doc);
                String content = d.get(LuceneIndexModel.TEXT);
                TokenStream tokenStream = analyzer.tokenStream(LuceneIndexModel.TEXT, new StringReader(content));
                String text = highlighter.getBestFragment(tokenStream, content);// 得到高亮显示后的内容
                map.put(d.get(LuceneIndexModel.UUID), text);
            }
        }
        catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally {
            if (indexReader != null) {
                try {
                    indexReader.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (directory != null) {
                try {
//                    directory.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }
}
