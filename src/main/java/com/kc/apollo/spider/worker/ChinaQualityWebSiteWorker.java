package com.kc.apollo.spider.worker;

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
 */
public class ChinaQualityWebSiteWorker extends HtmlParentWorker {

    Log logger = LogFactory.getLog(ChinaQualityWebSiteWorker.class);

    private final String baseUrl = "http://www.chinatt315.org.cn/cpcc/listNews.aspx";
    private final String baseUrlPrefix = "http://www.chinatt315.org.cn";

    @Override
    public void retreveHyberLinkFromHtml(String baseHtmlAddress){
        try {
            Document document = Jsoup.connect(baseUrl).get();
            Element element = document.getElementsByClass("particular").get(0);
            Elements hyberLinkElements = element.select("a");
            for (Element ele : hyberLinkElements) {
                String urlPostFix = ele.attr("href");
                if (!urlPostFix.startsWith("javascript")) {
                    //访问地址
                    String urlAddress = baseUrlPrefix + urlPostFix;
                    //访问主题
                    String title = ele.attr("title");
                    //正文内容前100个字符
                    Document getBodyDocument = Jsoup.connect(urlAddress).get();
                    String body = getBodyDocument.getElementsByTag("body").text();
                    if(body.length()>500){
                        body =body.substring(0,499);
                    }

                    String findSql = "select visited_url from apollo_visit_history where visited_url=?";
                    List<DBTypes> typeList = Arrays.asList(DBTypes.STRING);
                    Object[] queryCondition = new Object[]{urlAddress};
                    if (!DBHelper.getInstance().isExistData(findSql, typeList, queryCondition)) { // 数据库不存在数据
                        //将URL存入数据库
                        String insertSql = "insert into apollo_visit_history (visited_url, date) values (?, ?)";
                        List<DBTypes> insetTypelist = Arrays.asList(DBTypes.STRING, DBTypes.DATE);
                        Object[] insertContent = new Object[]{urlAddress, DataHelper.getCurrentTimeStamp()};
                        DBHelper.getInstance().insertTable(insertSql, insetTypelist, insertContent);

                        //将数据正式保存入数据库
                        String persistIntoDb = "insert into apollo_html_content_collection " +
                                "(uuid, title, original_url, invert_index_flag, create_date, page_rank, active_flag, on_top_flag, advertisement_flag, body_content)" +
                                "values (?,?,?,?,?,?,?,?,?,?)";
                        List<DBTypes> keyTypes = Arrays.asList(DBTypes.STRING, DBTypes.STRING, DBTypes.STRING, DBTypes.STRING,
                                DBTypes.DATE, DBTypes.INTEGER, DBTypes.STRING, DBTypes.STRING, DBTypes.STRING, DBTypes.STRING);
                        Object[] values = new Object[]{UUID.randomUUID().toString(), title, urlAddress, "N", DataHelper.getCurrentTimeStamp(), 10, "Y", "N", "N",body};
                        DBHelper.getInstance().insertTable(persistIntoDb, keyTypes, values);
                    }
                }
            }
            logger.info(DataHelper.getCurrentTimeStamp()+":"+"爬取任务完成");
        }catch (Exception e){
            logger.error("Error happened:"+e.getMessage());
        }


    }

    //TODO 暂时不提供，附件种类太过烦杂，需要更多的时间开发
    @Override
    public void downloadRemoteFileAndPersist(String fileUrlAddress) throws Exception {
        //将爬去的HTML链接放去数据库比较,如果有记录 则不再爬取，如果没有记录则爬取内容
        String findSql = "select visited_url from apollo_visit_history where visited_url=?";
        List<DBTypes> typeList = Arrays.asList(DBTypes.STRING);
        Object[] queryCondition = new Object[]{fileUrlAddress};
        if(!DBHelper.getInstance().isExistData(findSql, typeList, queryCondition)){ // 数据库不存在数据
            //将URL存入数据库
            String insertSql = "insert into apollo_visit_history (visited_url, date) values (?, ?)";
            List<DBTypes> insetTypelist = Arrays.asList(DBTypes.STRING, DBTypes.DATE);
            Object[] insertContent = new Object[]{fileUrlAddress, DataHelper.getCurrentTimeStamp()};
            DBHelper.getInstance().insertTable(insertSql, insetTypelist, insertContent);
            //正式爬去数据
            Document document = Jsoup.connect("http://www.chinatt315.org.cn/cpcc/2016-8/10/17722.aspx").get();
            //选择 class=quicklink,下得a标签得href获取excel
            String excelFileLink = document.getElementsByClass("quicklink").get(0).select("a").attr("href");
            if(excelFileLink.endsWith("xls")){
                //excel 2003
                System.out.println(excelFileLink);
                URL url = new URL(excelFileLink);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                //设置超时间为3秒
                conn.setConnectTimeout(50*1000);
                //防止屏蔽程序抓取而返回403错误
                conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

                //得到输入流
                InputStream inputStream = conn.getInputStream();
                String[][] result = getData(inputStream, 3);
                for(String[] strings : result){
                    for(String str : strings){
                        System.out.print(str + " | ");
                    }
                    System.out.println("");
                }

            }else{
                //excel 2007
                //TODO 未来添加支持
            }
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
