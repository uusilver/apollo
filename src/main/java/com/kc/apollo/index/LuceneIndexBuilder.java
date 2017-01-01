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
        //û���򴴽��ļ���
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
                    String title = (String) row[1];//��ȡ���ִ�����
                    list.add(new LuceneIndexModel(title, uuid));
                }
                if(buildIndex(false, Constants.LUCENE_INDEX_FOLDER,list)){
                    //����������ϣ��������ݿ�Ķ�Ӧ�ֶ�
                    for (Object[] row : result) {
                        String uuid = (String) row[0];
                        //���½��������
                        String sql3 = "update apollo_html_content_collection set index_flag='Y' where uuid = ?";
                        List<DBTypes> list3 = Arrays.asList(DBTypes.STRING);
                        Object[] objects3= new Object[]{uuid};
                        DBHelper.getInstance().updateTable(sql3, list3, objects3);
                    }
                }
            }//��ɼ��ش���������

        }
        logger.info("�����������");
    }

    //����Lucene�����ļ�
    private boolean buildIndex(boolean ramIndex, String indexPath, List<LuceneIndexModel> modelList) {
        //��������
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
            IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LUCENE_47, analyzer); // ����������������Ϣ
            indexWriter = new IndexWriter(directory, writerConfig);
            indexWriter.forceMerge(10); // ��С�ļ��ﵽ���ٸ�ʱ�����Զ��ϲ����С�ļ�Ϊһ�����ļ�

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
     * LongField	��Ҫ����Long���͵��ֶεĴ洢������ʹ��SortField.Type.Long,������з�Χ��ѯ���������NumericRangeQuery.newLongRange()��LongField����������ʱ��������򣬱���System
     * .currentTimeMillions()
     * FloatField	��Float���͵��ֶν��д洢���������SortField.Type.Float,��Χ��ѯ����NumericRangeQuery.newFloatRange()
     * BinaryDocVluesField	ֻ�洢������ֵ�������Ҫ����ֵ������SortedDocValuesField
     * NumericDocValuesField	������ֵ���͵�Field������(Ԥ����)����Ҫ��Ҫ�����field�����һ��ͬ����NumericDocValuesField
     * SortedDocValuesField	����String���͵�Field��������Ҫ��StringField�����ͬ����SortedDocValuesField
     * StringField	�û�String���͵��ֶεĴ洢��StringField��ֻ�������ִ�
     * TextField	��String���͵��ֶν��д洢��TextField��StringField�Ĳ�ͬ��TextField�������ִַ�
     * StoredField	�洢Field��ֵ��������IndexSearcher.doc��IndexReader.document����ȡ��Field�ʹ洢��ֵ
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
