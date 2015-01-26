package com.spiritdata.dataanal.tomht.html2mht.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/** 
 *  用于分析数据，返回到显示页面
 * @author 
 * @version  
 */
@Controller
public class GetAnalysisDataController {
    @RequestMapping("/getAnalysusResult.do")
    public @ResponseBody Map<String,List<Object>> getAnalysisResult(HttpServletRequest request,HttpServletResponse response){
        /**
         * 伪数据
         */
        Map<String,List<Object>> resultMap = new HashMap<String,List<Object>>();
        //标签
        List<Object> labelList = new ArrayList<Object>();
        labelList.add("数学");
        labelList.add("语文");
        labelList.add("英语");
        resultMap.put("labelList", labelList);
        List<Object> tickList = new ArrayList<Object>();
        tickList.add("优秀");
        tickList.add("良好");
        tickList.add("及格");
        tickList.add("不及格");
        resultMap.put("tickList", tickList);
        List<Object> showDataList = new ArrayList<Object>();
        List<String> rDatas0 = new ArrayList<String>();
        rDatas0.add("10");
        rDatas0.add("30");
        rDatas0.add("40");
        rDatas0.add("20");
        showDataList.add(rDatas0);
        List<String> rDatas1 = new ArrayList<String>();
        rDatas1.add("12");
        rDatas1.add("28");
        rDatas1.add("38");
        rDatas1.add("22");
        showDataList.add(rDatas1);
        List<String> rDatas2 = new ArrayList<String>();
        rDatas2.add("8");
        rDatas2.add("32");
        rDatas2.add("42");
        rDatas2.add("18");
        showDataList.add(rDatas2);
        resultMap.put("showDataList", showDataList);
        return resultMap;
    }
  //@RequestMapping("/getAnalysusResult.do")
  //public ModelAndView getAnalysisResult(HttpServletRequest request,HttpServletResponse response){
  //  try {
//        request.setAttribute("A", 80);
//        request.setAttribute("B", 75);
//        request.setAttribute("C", "79%");
//        request.getRequestDispatcher("/apps/briefView/viewA.jsp").forward(request, response);
  //  } catch (ServletException e) {
//        e.printStackTrace();
  //  } catch (IOException e) {
//        e.printStackTrace();
  //  }
  //  return null;
  //}
}
