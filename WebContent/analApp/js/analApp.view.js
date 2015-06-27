
//变量定义

var _urlPath;

//查询应用调用的JS FUNC
/**
 * 查看文件详细信息
 * 查询结果中，当点击了某个文件，触发此操作
 */
function showFile(fileId,fileName){
  var winOption={
    url:_urlPath+"/analApp/FileView/FileDatagrid.jsp?fileId="+fileId+"&fileName="+fileName,
    title:"文件详情",
    height:600,
    width:1000,
    iframeScroll:"yes"
  };
  openSWinInMain(winOption);
};

//模态显示报告信息
/**
 * 显示报告详细信息
 * reportId--报告的ID
 * unReadId--未读报告小圆点标识的ID，用于查看报告后隐藏该小圆点，在主页面上显示的未读报告总数-1，未读报告JSON中减去REPORTID所对应的记录
 */
function showReport(reportId, unReadId) {
  //显示report内容
  var winOption={
    title:"报告详情",
    height:600,
    width:1000,
    iframeScroll:"yes"
  };
  ReportSerialShow.showById(reportId, winOption);

  //记录日志
  var param = new Object();
  param.reportId=reportId;
  visitLog_REPORT(param);  
  
  //如果是未读报告，则隐藏未读小红点标志，并把未读总数-1
  if (unReadId) {
    $("#"+unReadId).css("visibility","hidden");
    var delRep = new Object();
    delRep.reportId=reportId;
    var mPage=getMainPage();
    if (mPage) mPage.incremeNoVisitReports(1, delRep);
  }
};

/**
 * 显示报告的关联信息
 * 查询结果中，当点击了某个文件，触发此操作
 */
function showRelation(reportId){
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
