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
     *
     * @param directory
     * @param keywords
     * @param getResultNo
     * @throws IOException
     * @throws InvalidTokenOffsetsException
     */
    public Map<String, String> search(Directory directory, List<String> keywords, int getResultNo) throws IOException, InvalidTokenOffsetsException {
        //
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
            TopDocs topdocs = searcher.search(booleanQuery, getResultNo);//

            //
            Formatter formatter = new SimpleHTMLFormatter("<font color='red'>", "</font>");//
            Fragmenter fragmenter = new SimpleFragmenter(100);//
            org.apache.lucene.search.highlight.Scorer score = new QueryScorer(booleanQuery);//
            Highlighter highlighter = new Highlighter(formatter, score);//
            highlighter.setTextFragmenter(fragmenter);//
//            System.out.println("Total hits" + topdocs.totalHits);
            ScoreDoc[] docs = topdocs.scoreDocs;
            for (ScoreDoc doc : docs) {
                Document d = searcher.doc(doc.doc);
                String content = d.get(LuceneIndexModel.TEXT);
                TokenStream tokenStream = analyzer.tokenStream(LuceneIndexModel.TEXT, new StringReader(content));
                String text = highlighter.getBestFragment(tokenStream, content);//
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
