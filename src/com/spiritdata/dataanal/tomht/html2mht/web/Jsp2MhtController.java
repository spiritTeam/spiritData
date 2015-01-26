package com.spiritdata.dataanal.tomht.html2mht.web;

import java.net.MalformedURLException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.dataanal.tomht.html2mht.service.Html2MhtService;

/**
 * @author admi
 */
@Controller
public class Jsp2MhtController {
    @Resource
    Html2MhtService h2mService;
    @RequestMapping(value="/jsp2Mht.do")
    public @ResponseBody boolean jsp2Mht(HttpServletRequest request){
        String url = request.getParameter("url");
        /**从缓存中获取realPath*/
        CacheEle<?> briefCacheEle = SystemCache.getCache(FConstants.APPOSPATH);
        String realPath = (String) briefCacheEle.getContent();
        try {
            h2mService.createMht(url, realPath);
            return true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        }
       
    }
}
