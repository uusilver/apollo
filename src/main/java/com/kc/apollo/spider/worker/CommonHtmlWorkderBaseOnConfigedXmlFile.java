package com.kc.apollo.spider.worker;

import com.kc.apollo.model.SpiderXmlBean;
import com.kc.apollo.types.DBTypes;
import com.kc.apollo.util.DBHelper;
import com.kc.apollo.util.DataHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by lijunying on 16/10/17.
 * 根据定义好的XML文件，进行爬取工作的爬虫
 */
public class CommonHtmlWorkderBaseOnConfigedXmlFile extends HtmlParentWorker {

    Log logger = LogFactory.getLog(CommonHtmlWorkderBaseOnConfigedXmlFile.class);


    @Override
    public void retreveHyberLinkFromHtml(String base, String prefix, int maxDepth, int currentDepth){
        if(currentDepth == maxDepth)
            return;
        try {
            Document document = Jsoup.connect(base).get();
            //提取title
            String title = document.getElementsByTag("title").get(0).text();
            //提取全部的href链接
            Element body = document.body();
            //获得body的前500个字
            String bodyContent = body.text();
            if(bodyContent.length()>500){
                bodyContent =bodyContent.substring(0,499);
            }
            String findSql = "select visited_url from apollo_visit_history where visited_url=?";
                    List<DBTypes> typeList = Arrays.asList(DBTypes.STRING);
                    Object[] queryCondition = new Object[]{base};
                    if (!DBHelper.getInstance().isExistData(findSql, typeList, queryCondition)) { // 数据库不存在数据
                        //将URL存入数据库
                        String insertSql = "insert into apollo_visit_history (visited_url, date) values (?, ?)";
                        List<DBTypes> insetTypelist = Arrays.asList(DBTypes.STRING, DBTypes.DATE);
                        Object[] insertContent = new Object[]{base, DataHelper.getCurrentTimeStamp()};
                        DBHelper.getInstance().insertTable(insertSql, insetTypelist, insertContent);

                        //将数据正式保存入数据库
                        String persistIntoDb = "insert into apollo_html_content_collection " +
                                "(uuid, title, original_url, invert_index_flag, create_date, page_rank, active_flag, on_top_flag, advertisement_flag, body_content, remark)" +
                                "values (?,?,?,?,?,?,?,?,?,?,?)";
                        List<DBTypes> keyTypes = Arrays.asList(DBTypes.STRING, DBTypes.STRING, DBTypes.STRING, DBTypes.STRING,
                                DBTypes.DATE, DBTypes.INTEGER, DBTypes.STRING, DBTypes.STRING, DBTypes.STRING, DBTypes.STRING, DBTypes.STRING);
                        Object[] values = new Object[]{UUID.randomUUID().toString(), title, base, "N", DataHelper.getCurrentTimeStamp(), 10, "Y", "N", "N",bodyContent, "website"};
                        DBHelper.getInstance().insertTable(persistIntoDb, keyTypes, values);
                        downloadRemoteFileAndPersist(base, prefix);
                    }

            Elements allAEle =body.select("a");
            for (Iterator it = allAEle.iterator(); it.hasNext();) {
                Element e = (Element) it.next();
                String link = e.attr("href");
                //合法的内部链接
                if(isInternalSiteUrlLinkValid(link)){
                    retreveHyberLinkFromHtml(prefix+link, prefix, maxDepth, currentDepth+1);
                }

            }
        }catch (Exception e){
            logger.error("Error happened:"+e.getMessage());
        }
    }


    //TODO 暂时不提供，附件种类太过烦杂，需要更多的时间开发
    @Override
    public void downloadRemoteFileAndPersist(String fileUrlAddress, String prefix) {
        try {
            //正式爬去数据
            Document document = Jsoup.connect(fileUrlAddress).get();
            Element body = document.body();
            Elements allAEle = body.select("a");
            for (Iterator it = allAEle.iterator(); it.hasNext(); ) {
                Element e = (Element) it.next();
                String link = e.attr("href");
                //合法的内部链接
                if (isInternalSiteUrlLinkValid(link)) {
                    link = prefix + link;
                }

                if (link.endsWith("xls")) {
                    //excel 2003
                    URL url = new URL(link);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //设置超时间为3秒
                    conn.setConnectTimeout(3 * 1000);
                    //防止屏蔽程序抓取而返回403错误
                    conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

                    //得到输入流
                    InputStream inputStream = null;
                    try{
                        inputStream = conn.getInputStream();
                        String[][] result = getData(inputStream, 3);
                        for (String[] strings : result) {
                            StringBuilder sb = new StringBuilder();
                            for (String str : strings) {
                                sb.append(str + " ");
                            }
                            //将数据正式保存入数据库
                            String persistIntoDb = "insert into apollo_html_content_collection " +
                                    "(uuid, title, original_url, invert_index_flag, create_date, page_rank, active_flag, on_top_flag, advertisement_flag, body_content, remark)" +
                                    "values (?,?,?,?,?,?,?,?,?,?,?)";
                            List<DBTypes> keyTypes = Arrays.asList(DBTypes.STRING, DBTypes.STRING, DBTypes.STRING, DBTypes.STRING,
                                    DBTypes.DATE, DBTypes.INTEGER, DBTypes.STRING, DBTypes.STRING, DBTypes.STRING, DBTypes.STRING,DBTypes.STRING);
                            //标题为excel整行信息,不超过500个字,body现实文件链接名，Remark标记为excel文件
                            String title = sb.toString();
                            if(title.length()>500){
                                title =title.substring(0,499);
                            }
                            String bodyContent = title+"<br/>"+link;
                            Object[] values = new Object[]{UUID.randomUUID().toString(), title, fileUrlAddress, "N", DataHelper.getCurrentTimeStamp(), 10, "Y", "N", "N",bodyContent, "excel"};
                            DBHelper.getInstance().insertTable(persistIntoDb, keyTypes, values);

                        }
                    }catch (Exception e1){
                        logger.error("解析Excel出错:"+e1.getMessage());
                        return;
                    }finally {
                        inputStream.close();
                        return;
                    }
                } else {
                    //excel 2007
                    //TODO 未来添加支持
                }
            }
        }catch (Exception e){
            logger.warn("错误发生:"+e.getMessage());
        }
    }

    /**
     * 读取Excel的内容，第一维数组存储的是一行中格列的值，二维数组存储的是多少个行
     * @param inputStream 读取数据的源Excel
     * @param ignoreRows 读取数据忽略的行数，比喻行头不需要读入 忽略的行数为1
     * @return 读出的Excel中数据的内容
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String[][] getData(InputStream inputStream, int ignoreRows)
            throws FileNotFoundException, IOException {
        List<String[]> result = new ArrayList<String[]>();
        int rowSize = 0;
        BufferedInputStream in = new BufferedInputStream(inputStream);
        // 打开HSSFWorkbook
        POIFSFileSystem fs = new POIFSFileSystem(in);
        HSSFWorkbook wb = new HSSFWorkbook(fs);
        HSSFCell cell = null;
        for (int sheetIndex = 0; sheetIndex < wb.getNumberOfSheets(); sheetIndex++) {
            HSSFSheet st = wb.getSheetAt(sheetIndex);
            // 第一行为标题，不取
            for (int rowIndex = ignoreRows; rowIndex <= st.getLastRowNum(); rowIndex++) {
                HSSFRow row = st.getRow(rowIndex);
                if (row == null) {
                    continue;
                }
                int tempRowSize = row.getLastCellNum() + 1;
                if (tempRowSize > rowSize) {
                    rowSize = tempRowSize;
                }
                String[] values = new String[rowSize];
                Arrays.fill(values, "");
                boolean hasValue = false;
                for (short columnIndex = 0; columnIndex <= row.getLastCellNum(); columnIndex++) {
                    String value = "";
                    cell = row.getCell(columnIndex);
                    if (cell != null) {
                        // 注意：一定要设成这个，否则可能会出现乱码
                        switch (cell.getCellType()) {
                            case HSSFCell.CELL_TYPE_STRING:
                                value = cell.getStringCellValue();
                                break;
                            case HSSFCell.CELL_TYPE_NUMERIC:
                                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                    Date date = cell.getDateCellValue();
                                    if (date != null) {
                                        value = new SimpleDateFormat("yyyy-MM-dd")
                                                .format(date);
                                    } else {
                                        value = "";
                                    }
                                } else {
                                    value = new DecimalFormat("0").format(cell
                                            .getNumericCellValue());
                                }
                                break;
                            case HSSFCell.CELL_TYPE_FORMULA:
                                // 导入时如果为公式生成的数据则无值
                                if (!cell.getStringCellValue().equals("")) {
                                    value = cell.getStringCellValue();
                                } else {
                                    value = cell.getNumericCellValue() + "";
                                }
                                break;
                            case HSSFCell.CELL_TYPE_BLANK:
                                break;
                            case HSSFCell.CELL_TYPE_ERROR:
                                value = "";
                                break;
                            case HSSFCell.CELL_TYPE_BOOLEAN:
                                value = (cell.getBooleanCellValue() == true ? "Y"
                                        : "N");
                                break;
                            default:
                                value = "";
                        }
                    }
                    if (columnIndex == 0 && value.trim().equals("")) {
                        break;
                    }
                    values[columnIndex] = rightTrim(value);
                    hasValue = true;
                }

                if (hasValue) {
                    result.add(values);
                }
            }
        }
        in.close();
        String[][] returnArray = new String[result.size()][rowSize];
        for (int i = 0; i < returnArray.length; i++) {
            returnArray[i] = (String[]) result.get(i);
        }
        return returnArray;
    }

    /**
     * 去掉字符串右边的空格
     * @param str 要处理的字符串
     * @return 处理后的字符串
     */
    public static String rightTrim(String str) {
        if (str == null) {
            return "";
        }
        int length = str.length();
        for (int i = length - 1; i >= 0; i--) {
            if (str.charAt(i) != 0x20) {
                break;
            }
            length--;
        }
        return str.substring(0, length);
    }
}
