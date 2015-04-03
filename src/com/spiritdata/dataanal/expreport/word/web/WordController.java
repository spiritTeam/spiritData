package com.spiritdata.dataanal.expreport.word.web;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ServletContextAware;

import com.spiritdata.dataanal.expreport.word.service.WordService;
/**
 * 导出word Controller
 * @author mht
 */
@Controller
public class WordController implements ServletContextAware{
    // service
    @Resource
    private WordService wordService;
    //Spring这里是通过实现ServletContextAware接口来注入ServletContext对象  
    private ServletContext servletContext; 
    /**
     * 主方法入口
     * @param request
     * @return
     * @throws IOException 
     */
    @RequestMapping("/expReport/expWord.do")
    private void expWord(HttpServletRequest request,HttpServletResponse response) throws IOException {
        //获取网站部署路径(通过ServletContext对象)，用于确定下载文件位置，从而实现下载 
        String path = servletContext.getRealPath("/");
  
        //1.设置文件ContentType类型，这样设置，会自动判断下载文件类型  
        response.setContentType("multipart/form-data");
        //2.设置文件头：最后一个参数是设置下载文件名  
        response.setHeader("Content-Disposition", "attachment;fileName="+"textDown.docx");
        ServletOutputStream out;
        //通过文件路径获得File对象(假如此路径中有一个download.pdf文件)
        File file = new File(path + "report/reportDownLoad/" + "textDown.docx");
  
        try {
            FileInputStream inputStream = new FileInputStream(file);
  
            //3.通过response获取ServletOutputStream对象(out)
            out = response.getOutputStream();
  
            int b = 0;
            byte[] buffer = new byte[512];
            while (b != -1){
                b = inputStream.read(buffer);
                //4.写到输出流(out)中 
                out.write(buffer,0,b);
            }
            inputStream.close();
            out.close();
            out.flush();
  
        } catch (IOException e) {
            e.printStackTrace();
        }  
        //TODO 以下为要完成代码
//        //reportId
//        String reportId = request.getParameter("reportId");
//        //jsonDIdList
//        List<String> jsonDIdList = null;
//        //user
//        HttpSession session = request.getSession();
//        User userInfo = ((User)session.getAttribute(FConstants.SESSION_USER));
//        Map<String,Object> retMap = wordService.expWord(reportId,userInfo,jsonDIdList);
        //return retMap;
    }
    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
