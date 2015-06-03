//变量定义

var _urlPath;

//查询应用调用的JS FUNC
/**
 * 查看文件详细信息
 * 查询结果中，当点击了某个文件，触发此操作
 */
function showFile(fileId,fileName){
  //alert("您点击了：fileId="+fileId+" fileName="+fileName);
  //alert(_urlPath+"/analApp/FileView/FileDatagrid.jsp?fileId="+fileId+"&fileName="+fileName);
  var winOption={
    url:_urlPath+"/analApp/FileView/FileDatagrid.jsp?fileId="+fileId+"&fileName="+fileName,
    title:"文件详情",
    height:600,
    width:1000,
    iframeScroll:"yes"
  };
  //alert(winOption.url);
  openSWinInMain(winOption);
};

//模态显示报告信息
/**
 * 显示报告详细信息
 * unRead--是否未读报告
 */
function showReport(reportId) {
	//记录日志
	var param = new Object();
	param.reportId=reportId;
	visitLog_REPORT(param);
	//显示report内容
  var reportUrl = _urlPath+"/reportShell/showWithPC.jsp?reportId="+reportId+"";
  var winOption={
    url:reportUrl,
    title:"报告详情",
    height:600,
    width:1000,
    iframeScroll:"yes"
  };
  //visitLog(repotId);
  openSWinInMain(winOption);  
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
function highlightStr(astr,searchStr){
	var retStr = astr;
	if (astr&&searchStr&&searchStr.trim().length>0) {
		retStr = astr.replace(new RegExp(searchStr,'gm'),"<span class=\"highlight\">"+searchStr+"</span>");
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
