package com.kc.apollo;

import com.google.gson.Gson;
import com.kc.apollo.cache.HotSearchKeyCache;
import com.kc.apollo.cache.NewsCache;
import com.kc.apollo.model.NewsModel;
import com.kc.apollo.model.SearchObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lijunying on 16/12/19.
 */
@Controller
public class InformationController {

    Log logger = LogFactory.getLog(InformationController.class);

    @RequestMapping(value="/news",method = RequestMethod.GET)
    @ResponseBody
    public String news() throws SQLException, IOException {
        List<NewsModel> news = NewsCache.getInstance().getListCache();
        int size = news.size();
        int a = 0;
        int b = 0;
        java.util.Random random=new java.util.Random();// 定义随机类
        a =random.nextInt(size);
        for(;;){
            b = random.nextInt(size);
            if(a!=b) break;
        }
        NewsModel s1 = news.get(a);
        NewsModel s2 = news.get(b);
        return new Gson().toJson(Arrays.asList(s1,s2));
    }

    @RequestMapping(value="/searchKey",method = RequestMethod.GET)
    @ResponseBody
    public String searchKey() throws SQLException, IOException {
        List<String> news = HotSearchKeyCache.getInstance().getListCache();
        return new Gson().toJson(news);
    }
}
