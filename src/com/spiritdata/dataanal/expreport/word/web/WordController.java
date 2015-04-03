package com.spiritdata.dataanal.expreport.word.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestMapping;

import com.spiritdata.dataanal.UGA.pojo.User;
import com.spiritdata.dataanal.expreport.word.service.WordService;
import com.spiritdata.framework.FConstants;
/**
 * 导出word Controller
 * @author mht
 */
public class WordController {
	/**
	 * service
	 */
	@Resource
	private WordService wordService;
	/**
	 * 主方法入口
	 * @param request
	 * @return
	 */
	@RequestMapping("/expDoxc/expWord.do")
	private Map<String,Object> expWord(HttpServletRequest request){
		//reportId
		String reportId = request.getParameter("reportId");
		//jsonDIdList
		List<String> jsonDIdList = null;
		//user
		HttpSession session = request.getSession();
		User userInfo = ((User)session.getAttribute(FConstants.SESSION_USER));
		Map<String,Object> retMap = wordService.expWord(reportId,userInfo,jsonDIdList);
		return retMap;
	}
}
