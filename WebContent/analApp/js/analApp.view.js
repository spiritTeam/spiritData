//变量定义

var _urlPath;

//查询应用调用的JS FUNC
/**
 * 查看文件详细信息
 * 查询结果中，当点击了某个文件，触发此操作
 */
function showFile(fileId){
	alert("您点击了："+fileId);
};

//模态显示报告信息
/**
 * 显示报告详细信息
 * unRead--是否未读报告
 */
function showReport(reportId,unRead) {
  //alert("showReport() reportId="+reportId+" , unRead="+unRead);
  //弹出窗口显示报告详情
  var winOption={
    url:_urlPath+"/demo/Rd/resultRdEchart.jsp",
    title:"报告详情",
    height:600,
    width:1000,
    iframeScroll:"yes"
  };
  openSWinInMain(winOption);
  //如果是未读报告，则通知后台已经看了该报告，后台修改查看状态标记为已读@@@
  if(typeof(unRead)!="undefined"){
    if(typeof(unRead)=="boolean" && unRead){
    	
    }else if(typeof(unRead)=="string" && unRead.toUpperCase()=="TRUE"){
    	
    }	  
  }
};

/**
 * 显示报告的关联信息
 * 查询结果中，当点击了某个文件，触发此操作
 */
function showRelation(reportId){
  //alert("您点击了："+reportId);  
  var winOption={
    url:_urlPath+"/analApp/ReportView/reportRelation.jsp?reportId="+reportId,
    title:"报告关系",
    height:600,
    width:500,
    iframeScroll:"yes"
  };
  openSWinInMain(winOption);
};

/**
 * 高亮显示指定的字符串
 */
function highlightStr(astr,token){
	var retStr = "";
	if(typeof(astr)=='undefined' || typeof(astr)!='string' || astr==null || astr.length==0){
		retStr = astr;
		return retStr;
	}
	while(true){
	  var idx = astr.indexOf(token);
		if(idx>-1){
		  retStr += astr.substring(0,idx)+"<span class=\"highlight\">"+token+"</span>";
		  astr = astr.substring(idx+token.length);
		}else{
			retStr += astr;
			break;
		}
	}
	return retStr;
};

/**
 * 返回字符串，如果aStr不存在或为空，则使用默认的字符串
 * @param aStr 指定的字符串
 * @param defaultStr 默认的返回字符串
 */
function getStr(aStr,defaultStr){
  var retStr = defaultStr;
  if(typeof(aStr)=="string" && aStr!=null && aStr.length>0){
    retStr = aStr;
  }
  return retStr;
};
