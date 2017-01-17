package com.kc.apollo.spider;


import com.kc.apollo.cache.BrandNamesCache;
import com.kc.apollo.model.POSITION;
import com.kc.apollo.model.SpiderTask;
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

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lijunying on 16/10/6.
 */
public class HtmlAnalyzer implements Runnable {

    Log logger = LogFactory.getLog(HtmlAnalyzer.class);


    BlockingQueue<SpiderTask> queue;

    ConcurrentHashMap<String, String> map;

    public HtmlAnalyzer(BlockingQueue<SpiderTask> queue, ConcurrentHashMap<String, String> map) {
        this.queue = queue;
        this.map = map;
    }

    @Override
    public void run() {
            try {
                SpiderTask task = queue.poll(100, TimeUnit.MILLISECONDS);
                if(task!=null) {
                    String url = task.getUrl();
                    //放入完成列表内
                    map.put(task.getUrl(), "s");
                    //analysis URL
                    Document doc = Jsoup.connect(url).get();
                    Elements links = doc.select("a[href]");
                    for (Element link : links) {
                        String absLink = link.attr("abs:href");
                        if (absLink.length() > 0 && absLink.contains(task.getHost()) && !map.containsKey(absLink)) {
                            dealWithValidUrl(absLink);
                        }
                        //添加新任务
                        SpiderTask cTask = new SpiderTask();
                        cTask.setHost(task.getHost());
                        cTask.setUrl(absLink);
                        queue.offer(cTask, 100, TimeUnit.MILLISECONDS);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


    }

    //处理每一个具体的链接的
    private void dealWithValidUrl(String url){
        try {
            String findSql = "select visited_url from apollo_visit_history where visited_url=?";
            List<DBTypes> typeList = Arrays.asList(DBTypes.STRING);
            Object[] queryCondition = new Object[]{url};
            if (!DBHelper.getInstance().isExistData(findSql, typeList, queryCondition)) {
                //将URL存入数据库
                String insertSql = "insert into apollo_visit_history (visited_url, date) values (?, ?)";
                List<DBTypes> insetTypelist = Arrays.asList(DBTypes.STRING, DBTypes.DATE);
                Object[] insertContent = new Object[]{url, DataHelper.getCurrentTimeStamp()};
                DBHelper.getInstance().insertTable(insertSql, insetTypelist, insertContent);

                //合法的html或者htm结尾的URL
                Document document = null;

                document = Jsoup.connect(url).get();
                //提取title
                Elements elements = document.getElementsByTag("title");
                if(elements!=null && elements.size()>0){
                    String title = elements.get(0).text();
                    //提取全部的href链接
                    Element body = document.body();
                    String bodyContent = body.text();
                    if(bodyContent.length()>500){
                        bodyContent =bodyContent.substring(0,499);
                    }
                    String keywords = null;
                    String description = null;
                    Elements metas = document.head().select("meta");
                    for (Element meta : metas) {
                        String metaContent = meta.attr("content");
                        if ("keywords".equalsIgnoreCase(meta.attr("name"))) {
                            keywords = metaContent;
                        }
                        if ("description".equalsIgnoreCase(meta.attr("name"))) {
                            description = metaContent;
                        }
                    }
                    //链接不为空，且标题内含有品牌词信息
                    if(title !=null && BrandNamesCache.getInstance().containsBrand(title)) {
                        //TODO 保存进数据库
                        //将数据正式保存入数据库
                        String persistIntoDb = "insert into apollo_html_content_collection " +
                                "(uuid, title, original_url, index_flag, create_date, page_rank, active_flag, on_top_flag, advertisement_flag, body_content, remark, keywords, description)" +
                                "values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
                        List<DBTypes> keyTypes = Arrays.asList(DBTypes.STRING, DBTypes.STRING, DBTypes.STRING, DBTypes.STRING,
                                DBTypes.DATE, DBTypes.INTEGER, DBTypes.STRING, DBTypes.STRING, DBTypes.STRING, DBTypes.STRING, DBTypes.STRING, DBTypes.STRING, DBTypes.STRING);
                        Object[] values = new Object[]{UUID.randomUUID().toString(), title, url, "N", DataHelper.getCurrentTimeStamp(), 10, "Y", "N", "N", bodyContent, "website", keywords, description};
                        DBHelper.getInstance().insertTable(persistIntoDb, keyTypes, values);
                        downloadRemoteFileAndPersist(url);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width-1) + ".";
        else
            return s;
    }

    public void downloadRemoteFileAndPersist(String fileUrlAddress) {
        try {
            //正式爬去数据
            Document document = Jsoup.connect(fileUrlAddress).get();
            Element body = document.body();
            Elements links = body.select("a[href]");
            for (Element link : links) {
                String absLink = link.attr("abs:href");


                if (absLink.endsWith("xls")) {
                    //excel 2003
                    URL url = new URL(absLink);
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
                                    "(uuid, title, original_url, index_flag, create_date, page_rank, active_flag, on_top_flag, advertisement_flag, body_content, remark)" +
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

    private boolean isInternalSiteUrlLinkValid(String str){

        //为空
        if(str.length()==0 || str == null){
            return false;
        }
        //外部链接
        if(str.startsWith("http")||str.startsWith("HTTP")){
            return false;
        }
        //JS
        if(str.contains("javascript")||str.contains("JAVASRIPT")){
            return false;
        }
        if(str.contains("js")||str.contains("JS")){
            return false;
        }
        //CSS
        if(str.contains("css")||str.contains("CSS")){
            return false;
        }
        return true;
    }

}
