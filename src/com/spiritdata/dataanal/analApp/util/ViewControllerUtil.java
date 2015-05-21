package com.spiritdata.dataanal.analApp.util;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.spiritdata.dataanal.common.model.Owner;
import com.spiritdata.dataanal.common.util.SessionUtils;

/**
 * 控制器中用到的公用方法
 * @author yfo
 *
 */
public class ViewControllerUtil {
	
	/**
	 * 设置当前查询的ownerId和ownerType
	 * @param req
	 * @param paramMap
	 */
	public static void setSearchOwnerInfo(HttpServletRequest req,Map<String,Object> paramMap){
		Owner owner = SessionUtils.getOwner(req.getSession());
		String ownerId = owner.getOwnerId();
		paramMap.put("ownerId", ownerId);
		int ownerType = owner.getOwnerType();
		paramMap.put("ownerType", new Integer(ownerType));		
	}
}
