package com.kc.apollo;

import com.google.gson.Gson;
import com.kc.apollo.index.LuceneIndexHolder;
import com.kc.apollo.lucene.LuceneSearchManager;
import com.kc.apollo.model.SearchObject;
import com.kc.apollo.model.SearchResult;
import com.kc.apollo.spider.fixer.BaiduRealTimeWorker;
import com.kc.apollo.types.DBTypes;
import com.kc.apollo.util.DBHelper;
import com.kc.apollo.util.DBUtil;
import com.kc.apollo.util.DataHelper;
import com.kc.apollo.util.WordSpliter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by lijunying on 16/10/19.
 */
@Controller
public class SearchController {

    Log logger = LogFactory.getLog(SearchController.class);

    //TODO 从Properties文件中读取
    private final int pageSize = 10;

    @RequestMapping(value="/search",method = RequestMethod.POST)
    @ResponseBody
    public String search(SearchObject searchObject) throws SQLException, IOException, InvalidTokenOffsetsException {
            long start = System.currentTimeMillis();

        String keywords = searchObject.getKeywords();
        int pageNo = searchObject.getPageNo();

        //是否保存搜索结果
        boolean saveSearchFlag = false;
        //结果是否存在 在315快查的数据库内
        String hasResult = "Y";

        List<String> keyWordsList = WordSpliter.getInstance().getWordListAfterSplit(keywords);

        Map<String, String> map = LuceneSearchManager.getInstance().search(LuceneIndexHolder.getInstance().getDirectory(), keyWordsList, 30);
        Set<String> set = map.keySet();
        String[] strings = new String[set.size()];
        String questionMark = "";
        String sql = null;
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        SearchResult searchResult = new SearchResult();
        int index = 0;
        for (String uuid : set) {
            strings[index] = uuid;
            questionMark+="?,";
            index++;
        }
        if(questionMark.length()>0) {
            questionMark = questionMark.substring(0, questionMark.length() - 1);

            int pageLimitStart = pageNo * pageSize;
            int pageLimitEnd = pageLimitStart + pageSize;

            sql = "select uuid, original_url, create_date, body_content from apollo_html_content_collection where uuid in(" + questionMark + ") limit " + pageLimitStart + "," + pageLimitEnd;

            logger.info("数据库执行查询操作:" + sql);
            connection = DBUtil.getConnection();
            ps = connection.prepareStatement(sql);
            for (int i = 0; i < strings.length; i++) {
                ps.setString(i + 1, strings[i]);
            }
            rs = ps.executeQuery();

            searchResult = new SearchResult();
            Set<SearchResult.SearchItem> searchItemSet = new HashSet<SearchResult.SearchItem>();
            while (rs.next()) {
                SearchResult.SearchItem item = new SearchResult.SearchItem();
                item.setTitle(map.get(rs.getString("uuid")));
                item.setUrl(rs.getString("original_url"));
                item.setCreate_date(rs.getTimestamp("create_date").toString());
                item.setBody_content(rs.getString("body_content"));
                item.setSource("315快查");
                searchItemSet.add(item);
            }
            searchResult.setSearchItemSet(searchItemSet);

            //结果集不包含条数才需要进行重新计算
            if(searchObject.getTotalResult()==0) {
                sql = "select count(uuid) as totalCount from apollo_html_content_collection where uuid in(" + questionMark + ")";
                ps = connection.prepareStatement(sql);
                for(int i =0 ; i<strings.length; i++){
                    ps.setString(i+1, strings[i]);
                }
                rs = ps.executeQuery();
                if (rs.next()) {
                    int count = rs.getInt("totalCount");
                    searchResult.setTotalResult(count);
                }

                saveSearchFlag = true;
            }
        }

        long end = System.currentTimeMillis();
        long timeCost = end-start;
        searchResult.setExecuteTime(timeCost);
        logger.info("\""+keywords+"\"的分词解析耗时:" + timeCost+"毫秒");

        //如果此处从315快查数据依然为空，我们则用fixer包下的搜索来进行结果替代


        //将搜索词保存到数据库内
        if(saveSearchFlag){
            insertSearchKeyWordsIntoDatabse(keywords, hasResult);
        }
        return new Gson().toJson(searchResult);

    }

    @RequestMapping(value="/advice",method = RequestMethod.POST)
    @ResponseBody
    public String advice(SearchObject searchObject) throws SQLException, IOException, InvalidTokenOffsetsException {

        String keywords = searchObject.getKeywords();

        String result = "<div style=' margin-left:30px;width:520px;border:1px solid #e3e3e3;padding:8px 9px 14px 7px;margin-bottom:20px;'>\n" +
                " <link href='../pinpai/a01/laonichi.css' rel='stylesheet' type='text/css'/>\n" +
                "\n" +
                "                                             <div id='lnc'>\n" +
                "                            <div class='wppn5w6' data-time='1472626871221'>\n" +
                "                                <div class='rolje0e rolje0e-0' id='w-3y6oiw'>\n" +
                "                                    <div class='lxrvgut lxrvgut-h1'>\n" +
                "                                        <h2>\n" +
                "                                            <a target='_blank' class='huw0j5w-header-title' data-is-main-url='true'\n" +
                "                                               href='http://www.laonichi.com/'><em>老泥池</em>酒业官方网站</a><a\n" +
                "                                                class='huw0j5w-official-site' target='_blank'\n" +
                "                                                href='http://www.laonichi.com/'>官网</a>\n" +
                "                                        </h2>\n" +
                "\n" +
                "                                        <div class='huw0j5w-cont'>\n" +
                "                                            <a class='huw0j5w-logo' target='_blank' href='http://www.laonichi.com/' title2='logo'><img\n" +
                "                                                    src='../pinpai/a01/logo.jpg' width='121' height='121'\n" +
                "                                                    style='border:0;display:block;width:121px;height:121px'></a>\n" +
                "\n" +
                "                                            <div  style='margin-left:20px;display:block; float:left;line-height:18px;width:380px;overflow:hidden;zoom:1;text-indent:2em;line-height:21px;'>\n" +
                "                                                <a target='_blank' href='http://www.laonichi.com/'>老泥池</a>'泸南宻境'——薄刀岭下玉琼浆泸县，汉设江阳郡，梁（南北朝）初置泸州，位于万里长江上游岸边。历史悠久、人文荟萃，交通便利、物产丰富，历来被称为川南重镇。因其位于北纬28°54′40″～29°20′00″之间，正处在神秘的北纬29°地带，孕育出了神奇产物——'泸南宻境'老泥池酒。\n" +
                "\n" +
                "                                                <div class='huw0j5w-site' style='text-indent:0'>http://www.laonichi.com/ 2016-12\n" +
                "                                                    <div id='tools_w-3y6oiw-ec' class='c-tools'\n" +
                "                                                         data-tools='{&quot;title&quot;:&quot;老泥池集团官方网站&quot;,&quot;url&quot;:&quot;http://www.laonichi.com/.cn&quot;}'>\n" +
                "                                                        <a class='c-tip-icon'><i\n" +
                "                                                                class='c-icon c-icon-triangle-down-g'></i></a>\n" +
                "                                                    </div>\n" +
                "                                                    - <a href='javascript:;' class='huw0j5w-brand'\n" +
                "                                                         id='w-3y6oiw-brand-text'>广告</a></div>\n" +
                "                                            </div>\n" +
                "                                        </div>\n" +
                "                                    </div>\n" +
                "\n" +
                "                                </div>\n" +
                "                                <div class='rolje0e rolje0e-1' id='w-gn06p7'>\n" +
                "                                    <div class='lxrvgut lxrvgut-colorlist'>\n" +
                "                                        <ul>\n" +
                "                                            <li  style='float:left;width:420px;line-height:24px;overflow:hidden;clear:both;'><span\n" +
                "                                                    style='display:inline-block;overflow:hidden;width:10px;height:10px;vertical-align:middle;background-color:#1FA4FF;margin-right:4px' class='huw0j5w-color-green'></span><a\n" +
                "                                                    href='http://www.laonichi.com/Products.asp?lei=1' target='_blank'\n" +
                "                                                    title2='彩色链接1'>泸南密境老泥池</a>\n" +
                "\n" +
                "                                                <p class='huw0j5w-colorlist-desc'><a target='_blank'\n" +
                "                                                                                     class='huw0j5w-colorlist-desctext' style='font-size:12px; color:#000; '\n" +
                "                                                                                     title2='彩色链接描述1'\n" +
                "                                                                                     href='http://www.laonichi.com/Products.asp?lei=1'>窖香浓郁、味醇甜厚，口感丰富、绵长、净爽</a>\n" +
                "                                                </p>\n" +
                "                                            </li>\n" +
                "                                            <li style='float:left;width:420px;line-height:24px;overflow:hidden;clear:both;'><span\n" +
                "                                                    style='display:inline-block;overflow:hidden;width:10px;height:10px;vertical-align:middle;background-color:#FE820E;margin-right:4px'></span><a\n" +
                "                                                    href='http://www.laonichi.com/Products.asp?lei=2' target='_blank'\n" +
                "                                                    title2='彩色链接2'>泸南密境私藏原浆酒</a>\n" +
                "\n" +
                "                                                <p class='huw0j5w-colorlist-desc'><a target='_blank'\n" +
                "                                                                                     class='huw0j5w-colorlist-desctext' style='font-size:12px; color:#000;'\n" +
                "                                                                                     title2='彩色链接描述2'\n" +
                "                                                                                     href='http://www.laonichi.com/Products.asp?lei=2'>系中高档酒，口感绵软味道醇厚，饮后不上头</a>\n" +
                "                                                </p>\n" +
                "                                            </li>\n" +
                "                                            <li style='float:left;width:420px;line-height:24px;overflow:hidden;clear:both;'><span\n" +
                "                                                    style='display:inline-block;overflow:hidden;width:10px;height:10px;vertical-align:middle;background-color:#53C178;margin-right:4px' class='huw0j5w-color-red'></span><a\n" +
                "                                                    href='http://www.laonichi.com/Products.asp?lei=3' target='_blank'\n" +
                "                                                    title2='彩色链接3'>泸南密境生态原浆礼盒</a>\n" +
                "\n" +
                "                                                <p class='huw0j5w-colorlist-desc'><a target='_blank'\n" +
                "                                                                                     class='huw0j5w-colorlist-desctext' style='font-size:12px; color:#000;'\n" +
                "                                                                                     title2='彩色链接描述3'\n" +
                "                                                                                     href='http://www.laonichi.com/Products.asp?lei=3'>绵甜劲爽，回味悠长，好酒！</a>\n" +
                "                                                </p>\n" +
                "                                            </li>\n" +
                "                                        </ul>\n" +
                "                                    </div>\n" +
                "\n" +
                "                                </div>\n" +
                "                                <div id='w-og7wps'>\n" +
                "                                    <div class=' lxrvgut-button-group' style='background:#9e9e9e; color:#FFF;'>\n" +
                "                                        <table id='w-og7wps-table' border='0' cellspacing='0' cellpading='0'\n" +
                "                                               width='100%'>\n" +
                "                                            <tr>\n" +
                "                                                <td class='huw0j5w-first' width='20%'>\n" +
                "                                                    <a target='_blank' href='http://www.laonichi.com/Products.asp?lei=1'\n" +
                "                                                       title2='按钮1'>老泥池系列</a>\n" +
                "                                                </td>\n" +
                "                                                <td class=' width='20%'>\n" +
                "                                                    <a target='_blank' href='http://www.laonichi.com/Products.asp?lei=2'\n" +
                "                                                       title2='按钮2'>私藏原浆系列</a>\n" +
                "                                                </td>\n" +
                "                                                <td class=' width='20%'>\n" +
                "                                                    <a target='_blank' href='http://www.laonichi.com/Products.asp?lei=3'\n" +
                "                                                       title2='按钮3'>生态原浆礼盒</a>\n" +
                "                                                </td>\n" +
                "                                                <td class=' width='20%'>\n" +
                "                                                    <a target='_blank' href='http://www.laonichi.com/Products.asp?lei=4'\n" +
                "                                                       title2='按钮4'>酒神酒</a>\n" +
                "                                                </td>\n" +
                "                                                <td class='huw0j5w-last' width='20%'>\n" +
                "                                                    <a target='_blank' href='http://www.laonichi.com/Products.asp?lei=5'\n" +
                "                                                       title2='按钮5'>手工老窖酒</a>\n" +
                "                                                </td>\n" +
                "                                            </tr>\n" +
                "                                        </table>\n" +
                "                                    </div>\n" +
                "\n" +
                "                                </div>\n" +
                "                            </div>\n" +
                "                        </div>\n" +
                "                    </div>";
        return result;
    }



    private void insertSearchKeyWordsIntoDatabse(String keywords, String resultFlag){
        String sql1 = "insert into apollo_user_search_history (UUID, search_keywords, search_date, result_flag) values (?, ?, ?, ?)";

        List<DBTypes> list = Arrays.asList(DBTypes.STRING, DBTypes.STRING, DBTypes.DATE, DBTypes.STRING);

        Object[] objects = new Object[]{UUID.randomUUID().toString(), keywords,DataHelper.getCurrentTimeStamp(), resultFlag};

        try {
            DBHelper.getInstance().insertTable(sql1, list, objects);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
