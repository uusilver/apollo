package com.kc.apollo.index;

import com.kc.apollo.types.DBTypes;
import com.kc.apollo.util.DBHelper;
import com.kc.apollo.util.DataHelper;
import com.kc.apollo.util.WordSpliter;
import org.ansj.domain.Term;

import javax.xml.crypto.Data;
import java.util.*;

/**
 * 建立倒排索引，以供搜索引擎使用
 * Created by lijunying on 16/10/29.
 */
public class RevertIndex {



    public void revertIndex(Object[][] container) throws Exception {
        if(container == null)
            throw new NullPointerException("待索引内容不可为空");

            for(Object[] row : container){
                String uuid = (String)row[0];
                String title = (String)row[1];//获取待分词内容
                String url = (String)row[2]; //链接地址
                String body_content = (String)row[3]; //正文预览
                //数据有效
                if(uuid!=null){
                    List<Term> termList = WordSpliter.getInstance().getWordListAfterSplit(title);
                //解析title
                    //获取分词结果
                    try {
                        for (Term term : termList) {
                            String termContent = term.getName();
                            //将分词结果保存进数据表
                            String insertSql = "insert into apollo_invert_index values(?,?,?,?,?,?)";
                            List<DBTypes> types = Arrays.asList(DBTypes.STRING, DBTypes.STRING, DBTypes.STRING,DBTypes.STRING,DBTypes.DATE, DBTypes.STRING);
                            //将分词标红
                            String markTermsIntitle = title.replace(termContent, "<span class=\"keyWord\">"+termContent+"</span>");
                            Object[] values = new Object[]{UUID.randomUUID().toString(), termContent, url, markTermsIntitle, DataHelper.getCurrentTimeStamp(), body_content};
                            DBHelper.getInstance().insertTable(insertSql, types, values);
                        }//end of for
                        //更新结果到主表
                        String sql3 = "update apollo_html_content_collection set invert_index_flag='Y' where uuid = ?";
                        List<DBTypes> list3 = Arrays.asList(DBTypes.STRING);
                        Object[] objects3= new Object[]{uuid};
                        DBHelper.getInstance().updateTable(sql3, list3, objects3);
                    }catch (Exception e){
                        //保证表记录不被更改
                        String sql3 = "update apollo_html_content_collection set invert_index_flag='N' where uuid = ?";
                        List<DBTypes> list3 = Arrays.asList(DBTypes.STRING);
                        Object[] objects3= new Object[]{uuid};
                        DBHelper.getInstance().updateTable(sql3, list3, objects3);
                    }

                }

            }


    }

    public static void main(String[] args) throws Exception {
        RevertIndex revertIndex = new RevertIndex();
        Object[][] result = DBHelper.getInstance().loadApolloHtmlTableDataWithTop100();
        revertIndex.revertIndex(result);
    }

}