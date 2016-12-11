package com.kc.apollo.spider.worker;

/**
 * Created by lijunying on 16/10/17.
 */
public abstract class HtmlParentWorker implements HtmlWorker{

    public boolean checkUrl(String url) throws NullPointerException {
        if(url==null) throw new NullPointerException("HTML路径不可为空");
        return false;
    }


    /**
     * 判断URL是否是一个合法的内部链接
     * @param str
     * @return
     */
    public boolean isInternalSiteUrlLinkValid(String str){

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
