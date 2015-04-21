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
    height:48px;
    font:18px "Microsoft YaHei","Microsoft JhengHei","黑体";
    color:#d8d8d8;
    text-align:center;
    width:90px;
    line-height:48px
}

</style>
<body style="background-color:#FFFFFF">
<!-- 头部:悬浮 -->
<div id="topSegment">
  <div style="padding:5px;border:0px solid #ddd;">
    <table>
      <tr>
        <td style="width:10%;">
          <a href="#" class="def-nav" style="width:120px;">
            <img src="<%=path%>/resources/images/logo/logo_op.jpg" style="height:100%;width:120px;" onclick="clickLogo()" alt="公司LOGO"/>
          </a>
        </td>    
        <td>
          <a href="#" class="def-nav" style="width:15%;" onclick="showMainSeg('ReportView/main.jsp');">报告</a>
          <a href="#" class="def-nav" style="width:10%;"  onclick="showMainSeg('FileView/main.jsp');">文件</a>
          <a href="#" class="def-nav" style="width:70%;" onclick="showMainSeg('');">文件上传并分析</a>
        </td>    
        <td style="width:6%;">
          <a href="#" class="def-nav" onclick="showMainSeg();">用户处理</a>
        </td>
    </table>
  </div>
</div>

<!-- 脚部:悬浮 
<div id="footSegment" style="padding:10px 10px 0 10px;display:none;"></div>
-->

<!-- 中间主框架 -->
<iframe id="mainSegment">
</iframe>

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
  top_height: 60,
  top_peg: false,
  myInit: initPos,
  myResize: initPos,
  win_min_width: 640, //页面最小的高度。当窗口高度小于这个值，不对界面位置及尺寸进行调整。主体部分宽度也照此设置
  win_min_height: 480 //页面最小的高度。当窗口高度小于这个值，不对界面位置及尺寸进行调整。主体部分高度也照此设置
};

//主函数
$(function() {
  var initStr = $.spiritPageFrame(INIT_PARAM);
  if (initStr) {
    $.messager.alert("页面初始化失败", initStr, "error");
    return ;
  };

  $('#topSegment').css({'border':'1px solid #95b8e7','border-bottom':'0','background':'#32C52F','overflow':'hidden','border':'0','border-bottom':'0px'});
  //$('#footSegment').css({'border':'1px solid #32C52F','background':'#32C52F','opacity':'1'});
});
function initPos() {
  //$("#mainFrame").spiritUtils("setWidthByViewWidth", $("body").width()-$("#sideFrame").spiritUtils("getViewWidth"));
  //$("#sideFrame").css("left", $("#reportFrame").width());
}

/**
 * mainSegment中显示跳转页面
 * 当点击菜单项时，跳转到指定路径的主页面
 */
var fileroot = "<%=path%>/analApp/"; 
function showMainSeg(filepath){
  if(typeof(filepath) == "undefined" || !filepath || filepath==""){
    filepath="/constructing.jsp";
  }
  var fileurl = fileroot+filepath;
  $("#mainSegment").attr("src",fileurl);
}

//点击logo图片
function clickLogo(){
  alert("click logo");
}
</script>
</html>