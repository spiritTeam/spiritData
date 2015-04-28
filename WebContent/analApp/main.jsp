<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%
  String path = request.getContextPath();
%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<!-- 用户登录后显示的主页面，包括文件查询、上传、分析，用户管理等功能 -->
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="cache-control" content="no-cache"/>
<meta http-equiv="pragma" content="no-cache"/>
<meta http-equiv="expires" content="0"/>
<jsp:include page="/common/sysInclude.jsp" flush="true"/>

<link rel="stylesheet" type="text/css" href="<%=path%>/resources/plugins/spiritui/themes/default/pageFrame.css"/>
<script type="text/javascript" src="<%=path%>/resources/plugins/spiritui/jq.spirit.pageFrame.js"></script>
<script type="text/javascript" src="<%=path%>/resources/js/mainPage.utils.js"></script>

<!-- 加载ZUI - 开源HTML5跨屏框架 -->
<link href="<%=path%>/resources/plugins/zui/css/zui.min.css" rel="stylesheet">
<link href="<%=path%>/resources/plugins/zui/css/example.css" rel="stylesheet">
<script src="<%=path%>/resources/plugins/zui/js/zui.min.js"></script>

<title>用户主界面</title>
</head>
<style>
body {
  background-color:#fff;
}
#sideFrame {
  width:270px; padding-left:20px; position:fixed;
}
#rTitle {
  height:40px; padding:10px; padding-left: 30px; font-size:36px; font-weight:bold;
}
.rptSegment {
  border:1px solid #E6E6E6;
  margin-bottom:15px;
}
.segTitle {
  height:30px; border-bottom:1px solid #E6E6E6;padding: 3px 0 0 5px;
}
.segTitle span {
  width:40px; font-size:24px; font-weight:bold;
}
.subTitle {
  font-size:18px; font-weight:bold; padding:3px;
}
.segContent{padding-left:5px;padding-right:5px;}

.def-nav{
    display:block;
    float:left;
    height:45px;
    font:18px "Microsoft YaHei","Microsoft JhengHei","黑体";
    color:#d8d8d8;
    text-align:center;
    width:90px;
    line-height:42px
}

</style>
<body style="background-color:#FFFFFF">
<!-- 头部:悬浮 -->
<div id="topSegment">
  <div style="padding:5px;border:0px solid #ddd;">
    <table style="width:100%;">
      <tr>
        <td style="width:120px;text-align:center;">
          <div>
          <a href="#" class="def-nav" style="">
            <img src="<%=path%>/resources/images/logo/logo_op.jpg" style="height:88%;width:120px;" onclick="clickLogo()" alt="公司LOGO"/>
          </a>
          </div>
        </td>    
        <td style="width:100px;text-align:center;">
          <a id="a_report" href="#" class="def-nav" onclick="showMainSeg('ReportView/main.jsp');">报告<span id="id_report_new" class="label label-badge label-danger" style="z-index:99;">12</span></a>
        </td>      
        <td style="width:100px;text-align:center;">
          <a href="#" class="def-nav" onclick="showMainSeg('FileView/main.jsp');">文件</a>
        </td>        
        <td style="width:380px;text-align:center;">
          <div class="input-group" style="display:block;margin-bottom:5px;">
            <input id="idSearchFile" class="form-control"  type="text" style="height:25px;" placeholder="请输入查询内容...">
            <span class="input-group-btn">
              <button id="idSubmitSearchFile" onclick="startSearch();" class="btn btn-default" type="button" style="font:18px Microsoft YaHei,Microsoft JhengHei,黑体;">搜索</button>
            </span>
          </div>  
        </td>      
        <td style="width:100px;text-align:center;">
        </td>
        <td style="width:100px;text-align:center;">
          <a href="#" class="def-nav" style="" onclick="showMainSeg('');">数据上传</a>
        </td>    
        <td style="width:100px;text-align:center;">
          <a href="#" class="def-nav" onclick="showMainSeg();">用户处理</a>
        </td>
      </tr>
    </table>
  </div>
</div>

<!-- 脚部:悬浮 
<div id="footSegment" style="padding:10px 10px 0 10px;display:none;"></div>
-->

<!-- 中间主框架 -->
<div id="mainSegment">
<iframe id="mainSegmentIframe">
</iframe>
</div>

</body>
<script>
//主窗口参数
var INIT_PARAM = {
  pageObjs: {
    topId: "topSegment",
    mainId: "mainSegment"
  },
  page_width: -1,
  page_height: -1,
  top_shadow_color:"#E6E6E6",
  top_height: 45,
  top_peg: false,
  win_min_width: 640, //页面最小的高度。当窗口高度小于这个值，不对界面位置及尺寸进行调整。主体部分宽度也照此设置
  win_min_height: 480, //页面最小的高度。当窗口高度小于这个值，不对界面位置及尺寸进行调整。主体部分高度也照此设置
  myInit: initPosition,
  myResize: myResize
};

/**变量定义区**/
//记录新增报表信息
var newReportArr=[];
//主函数
$(function() {
  var initStr = $.spiritPageFrame(INIT_PARAM);
  if (initStr) {
    $.messager.alert("页面初始化失败", initStr, "error");
    return ;
  };
  
  initSearchFileInput();

  $('#topSegment').css({'border':'1px solid #95b8e7','border-bottom':'0','background':'#32C52F','overflow':'hidden','border':'0','border-bottom':'0px'});
  //$('#footSegment').css({'border':'1px solid #32C52F','background':'#32C52F','opacity':'1'});
  
  //默认进入报告查询页面
  showMainSeg('ReportView/main.jsp');
  
  //新报告点击事件
  $("#id_report_new").css("visibility","hidden");
  $("#id_report_new").click(function(){
    showModelNewReportList();
  });
  
  //定时查询是否有新报告
  searchNewReport();
  setInterval(searchNewReport,10*1000);
});

//初始化界面
function initPosition() {
	$("#mainSegmentIframe").width($("#mainSegment").width());
	$("#mainSegmentIframe").height($("#mainSegment").height());
};
//当界面尺寸改变
function myResize() {
  $("#mainSegmentIframe").width($("#mainSegment").width());
  $("#mainSegmentIframe").height($("#mainSegment").height());
};

//初始化查询输入框
var searchTxt = "请输入查询内容...";
function initSearchFileInput(){
  var _objSearch = $("#idSearchFile");
  _objSearch.keydown(function(e){
    if(e.keyCode == 13){
      startSearch();
    }
  });
}

//取出输入条件，提交查询
function startSearch(){
  var searchStr = ($("#idSearchFile").val()==searchTxt)?"":$("#idSearchFile").val();
  //alert("您输入了："+ searchStr);
  var fileName = "listView/main.jsp?searchStr="+searchStr;
  showMainSeg(fileName);
}

/**
 * 异步请求后台，查找是否有新的报告生成
 */
function searchNewReport(){
	//异步查询是否有新增报表   
	var searchParam={"searchType":"fectchNewReport","searchStr":""};
	  var url="<%=path%>/reportview/searchNewReport.do";
	  $.ajax({type:"post", async:true, url:url, data:searchParam, dataType:"json",
	    success:function(jsonData){
	      try{
          var total = jsonData.total;
          var data = jsonData.data;
          for(var i=0;i<total;i++){
        	  newReportArr.push(data[i]);  
          }   
          if(newReportArr.length>0){
        	  refreshNewReportDIV();
          }
	      }catch(e){
	        $.messager.alert("解析新报告异常", "查询结果解析成JSON失败：</br>"+(e.message)+"！<br/>", "error", function(){});
	      }
	    },
	    error:function(errorData){
	      $.messager.alert("查询新报告异常", "查询失败：</br>"+(errorData?errorData.responseText:"")+"！<br/>", "error", function(){});
	    }
	  }); 	
}

/**
 * 刷新是否有新报告生成的DIV
 */
function refreshNewReportDIV(){
	var objAReport=$("#a_report");
	var objShowCount = $("#id_report_new");
	if(newReportArr.length>0){
		objShowCount.html(newReportArr.length>99?"...":newReportArr.length);
		objShowCount.css("visibility","visible");
		//当显示新增条数时，禁用报告按钮
    //objAReport.removeAttr("href");//去掉a标签中的href属性
    objAReport.removeAttr("onclick");//去掉a标签中的onclick事件
    setTimeout(function(){objAReport.attr("onclick","showMainSeg('ReportView/main.jsp');");},500);
	}else{
		objShowCount.css("visibility","hidden");
	  //objAReport.addAttr("href","###");//加上a标签中的href属性
	  objAReport.attr("onclick","showMainSeg('ReportView/main.jsp');");//加上a标签中的onclick事件
	}
}

/**
 * 显示指定的报表内容，点击事件触发此方法
 */
function showReport(reportName){
	alert("show reoprt:"+reportName);
}

/**
 * mainSegmentIframe中显示跳转页面
 * 当点击菜单项时，跳转到指定路径的主页面
 */
var fileroot = "<%=path%>/analApp/"; 
function showMainSeg(filepath){
	//alert("showMainSeg():"+filepath);
  if(typeof(filepath) == "undefined" || !filepath || filepath==""){
    filepath="constructing.jsp";
  }
  var fileurl = fileroot+filepath;
  $("#mainSegmentIframe").attr("src",fileurl);
}

/**
 * 模态显示新报告列表
 * 当点击新报告数量图标时，触发此方法
 */
function showModelNewReportList(){
	if(newReportArr.length<=0){
		return;
	}
	var contentHtml ="<div style='height:98%;overflow-y:auto;'>";
	var tableStyle = "style='width:96%;overflow-y:scroll;border:1px solid #DDD;background-color:transparent;margin:0px auto;'";
	contentHtml += "<table "+ tableStyle +">";
	var thStyle0 = "style='width:240px;height:18px;background-color:#F1F1F1;border:1px solid #DDD;font-size:12px;font-weight:bold;text-align:center;'";
	var thStyle1 = "style='width:60px;background-color:#F1F1F1;border:1px solid #DDD;font-size:12px;font-weight:bold;text-align:center;'";
	var thStyle2 = "style='width:100px;background-color:#F1F1F1;border:1px solid #DDD;font-size:12px;font-weight:bold;text-align:center;'";
	contentHtml +="<tr><th "+thStyle0+">报告名</th><th "+thStyle1+">大小</th><th "+thStyle2+">生成日期</th></tr>";
	var tdStyle0 = "style='background-color:#F9F9F9;border:1px solid #DDD;font-size:12px;text-align:left;'";
	  var tdStyle1 = "style='background-color:#ffffff;border:1px solid #DDD;font-size:12px;text-align:left;'";
	for(var i=0;i<newReportArr.length;i++){
		var tdStyle = i%2==0?tdStyle0:tdStyle1;
		contentHtml += "<tr'><td "+tdStyle+">"+newReportArr[i]+"</td><td "+tdStyle+">1M</td><td "+tdStyle+">2014-01-02</td></tr>";
	}
	contentHtml+="</table>";
	contentHtml+="</div>";
	
	var winOption={
	  //url:"<%=path%>/analApp/demoData/force1.jsp",
	  content:contentHtml,
	  title:"未读报告列表",
	  height:600,
	  width:500,
	  iframeScroll:"yes"
	};
	openSWinInMain(winOption);
}

//点击logo图片
function clickLogo(){
  alert("click logo");
}
</script>
</html>