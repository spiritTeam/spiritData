package com.spiritdata.dataanal.tomht.html2mht.web;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;

import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.dataanal.tomht.html2mht.service.Html2MhtService;

/** 
 * 这个controller用于吧jqplot保存成图片生成图片，
 * @author mht
 * @version  
 */
public class Html2MhtController {
    @Resource
    Html2MhtService h2mService;
    /**
     * 从前台获取jqplot信息
     * @param request
     * @param response
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value="/getImage.do")
    public void getImage(HttpServletRequest request,HttpServletResponse response) throws Exception{
        String url = request.getParameter("thisUrl");
        /**获取前台的img信息，imgStr，对应的jqplot的divId*/
        Map<String, String[]> requestMap = request.getParameterMap();
        /**处理对应的img信息 key==divId，value==imgStr*/
        Map<String,String> imgInfoMap = h2mService.getImgInfo(requestMap);
        /**从缓存中获取realPath*/
        CacheEle<?> briefCacheEle = SystemCache.getCache(FConstants.APPOSPATH);
        String realPath = (String) briefCacheEle.getContent();
        if(imgInfoMap==null){
            /**没有需要生成的图片*/
            h2mService.createMht(url, realPath);
        }else{
            /**有需要生成的图片*/
            //key=domEleId,Value=imgBase64Str
            h2mService.createMht(url, imgInfoMap, realPath);
        }
    }
}
