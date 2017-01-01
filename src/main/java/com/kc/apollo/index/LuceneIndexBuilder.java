package com.kc.apollo.index;

import com.kc.apollo.model.LuceneIndexModel;
import com.kc.apollo.types.DBTypes;
import com.kc.apollo.util.Constants;
import com.kc.apollo.util.DBHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @COPYRIGHT (C) 2017 Schenker AG
 * <p>
 * All rights reserved
 */

public class LuceneIndexBuilder {

    private Log logger = LogFactory.getLog(LuceneIndexBuilder.class);
    private static LuceneIndexBuilder ourInstance = new LuceneIndexBuilder();

    public static LuceneIndexBuilder getInstance() {
        return ourInstance;
    }

    private LuceneIndexBuilder() {
    }

    public void buildIndex() {
        //没有则创建文件夹
        File folder = new File(Constants.LUCENE_INDEX_FOLDER);
        if(!folder.exists()){
            folder.mkdirs();
        }
        int unIndexData = DBHelper.getInstance().countUnIndexData();
        if (unIndexData > 0) {
            int loopTimes = unIndexData / 200 + 1;
            List<LuceneIndexModel> list = new ArrayList<>();
            for (int t = 0; t < loopTimes; t++) {
                Object[][] result = DBHelper.getInstance().loadApolloHtmlTableDataWithNumber(200);
                for (Object[] row : result) {
                    String uuid = (String) row[0];
                    String title = (String) row[1];//获取待分词内容
                    list.add(new LuceneIndexModel(title, uuid));
                }
                if(buildIndex(false, Constants.LUCENE_INDEX_FOLDER,list)){
                    //索引加载完毕，更新数据库的对应字段
                    for (Object[] row : result) {
                        String uuid = (String) row[0];
                        //更新结果到主表
                        String sql3 = "update apollo_html_content_collection set index_flag='Y' where uuid = ?";
                        List<DBTypes> list3 = Arrays.asList(DBTypes.STRING);
                        Object[] objects3= new Object[]{uuid};
                        DBHelper.getInstance().updateTable(sql3, list3, objects3);
                    }
                }
            }//完成加载待索引数据

        }
        logger.info("索引建立完毕");
    }

    //生成Lucene索引文件
    private boolean buildIndex(boolean ramIndex, String indexPath, List<LuceneIndexModel> modelList) {
        //建立索引
        Analyzer analyzer = new IKAnalyzer();
        Directory directory = null;
        IndexWriter indexWriter = null;
        try {

            if (ramIndex) {
                directory = new RAMDirectory();
            }
            else {
                directory = FSDirectory.open(new File(Constants.LUCENE_INDEX_FOLDER));
            }
            IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LUCENE_47, analyzer); // 创建索引的配置信息
            indexWriter = new IndexWriter(directory, writerConfig);
            indexWriter.forceMerge(10); // 当小文件达到多少个时，就自动合并多个小文件为一个大文件

            for (LuceneIndexModel model : modelList) {
                indexWriter.addDocument(objToDocument(model));
            }
            return true;

        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }
        finally {
            try {
                indexWriter.close();
                directory.close();
            }
            catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        return false;
    }

    /**
     * LongField	主要处理Long类型的字段的存储，排序使用SortField.Type.Long,如果进行范围查询或过滤利用NumericRangeQuery.newLongRange()，LongField常用来进行时间戳的排序，保存System
     * .currentTimeMillions()
     * FloatField	对Float类型的字段进行存储，排序采用SortField.Type.Float,范围查询采用NumericRangeQuery.newFloatRange()
     * BinaryDocVluesField	只存储不共享值，如果需要共享值可以用SortedDocValuesField
     * NumericDocValuesField	用于数值类型的Field的排序(预排序)，需要在要排序的field后添加一个同名的NumericDocValuesField
     * SortedDocValuesField	用于String类型的Field的排序，需要在StringField后添加同名的SortedDocValuesField
     * StringField	用户String类型的字段的存储，StringField是只索引不分词
     * TextField	对String类型的字段进行存储，TextField和StringField的不同是TextField既索引又分词
     * StoredField	存储Field的值，可以用IndexSearcher.doc和IndexReader.document来获取此Field和存储的值
     *
     * @param model
     * @return
     */
    private static Document objToDocument(LuceneIndexModel model) {
        Document document = new Document();
        document.add(new TextField(LuceneIndexModel.TEXT, model.getText(), Field.Store.YES));
        document.add(new StringField(LuceneIndexModel.UUID, model.getUuid(), Field.Store.YES));
//        FieldType ft = new FieldType();
//        ft.setStored(true);
//        ft.setIndexed(true);
//        ft.setTokenized(true);
        return document;
    }
}
