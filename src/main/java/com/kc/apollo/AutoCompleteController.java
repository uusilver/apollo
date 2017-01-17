package com.kc.apollo;

import com.google.gson.Gson;
import com.kc.apollo.cache.BrandNamesCache;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by lijunying on 16/12/10.
 */
@Controller
public class AutoCompleteController {

    @RequestMapping(value="/autoComplete",method = RequestMethod.POST)
    @ResponseBody
    public String search(String query) throws SQLException, IOException {
        return new Gson().toJson(BrandNamesCache.getInstance().getListCache(query));
    }
}
