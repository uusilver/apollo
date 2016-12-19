package com.kc.apollo.extra;

import com.kc.apollo.types.DBTypes;
import com.kc.apollo.util.DBHelper;
import com.kc.apollo.util.DataHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by lijunying on 16/12/14.
 */
public class BrandWorker {
    public static void main(String args[]) {
            String s[] = new String[]{"i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","shu"};
            Integer i[] = new Integer[]{1,7,5,8,7,2,2,2,4,2,9,5,1,1,4,6,10,5,1};
            if(s.length == i.length){
                for(int index=0; index<s.length;index++){
                    canSub(s[index], i[index]);
                }
            }
        System.out.println("Done");
    }

    private static void canSub(String midFix, int num){
        String brandName = "未知";
        String companyName = "未知";
        String nextLink = null;
        try {
            int a = num;
            for (int i = 1; i <= a; i++) {
                /*
                    Done: a,b,c,d,e,f,g
                          h

                 */
                String site = "http://i.paizi.com/dp-"+midFix+"-" + a;
                Document document = Jsoup.connect(site).timeout(5000).get();
                Elements elements = document.select("ul > li");

                for (Element e : elements) {
                    brandName = e.text().replace("▪", "").trim() + " ";
                    if (brandName != null || brandName.length() > 0) {

                        nextLink = e.select("a").attr("href");
                        if (nextLink.startsWith("http")) {
                            String findSql = "select visited_url from apollo_visit_history where visited_url=?";
                            List<DBTypes> typeList = Arrays.asList(DBTypes.STRING);
                            Object[] queryCondition = new Object[]{nextLink};
                            if (!DBHelper.getInstance().isExistData(findSql, typeList, queryCondition)) { // 数据库不存在数据
                                //将URL存入数据库
                                String insertSql = "insert into apollo_visit_history (visited_url, date) values (?, ?)";
                                List<DBTypes> insetTypelist = Arrays.asList(DBTypes.STRING, DBTypes.DATE);
                                Object[] insertContent = new Object[]{nextLink, DataHelper.getCurrentTimeStamp()};
                                DBHelper.getInstance().insertTable(insertSql, insetTypelist, insertContent);

                                Document subDoc = Jsoup.connect(nextLink).timeout(10000).get();
                                Elements eles = subDoc.getElementsByTag("p");
                                for (Element ele : eles) {
                                    String eleText = ele.text();
                                    if (eleText.indexOf("主体单位") != -1) {
                                        companyName = eleText.split("\\：")[1].trim();
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    //TODO
                    if (brandName!=null && companyName != null) {
                        System.out.println(brandName + " : " + companyName);

                        String sql1 = "insert into apollo_brand_company_collection (uuid, brand_name, brand_company, source, active_flag, create_date) values (?, ?, ?, ?, ?, ?)";

                        List<DBTypes> list = Arrays.asList(DBTypes.STRING, DBTypes.STRING, DBTypes.STRING, DBTypes.STRING, DBTypes.STRING, DBTypes.DATE);

                        Object[] objects = new Object[]{UUID.randomUUID().toString(), brandName, companyName, "315kc", "Y", DataHelper.getCurrentTimeStamp()};

                        DBHelper.getInstance().insertTable(sql1, list, objects);
                    }
                }
            }
        }catch(Exception e){
            System.out.println(e.getMessage()+":"+brandName +" "+ nextLink);
        }
    }
}
