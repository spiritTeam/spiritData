<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%
  String path = request.getContextPath();
%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<!-- DEMO页面  用户登录后显示的主页面，包括文件查询、上传、分析，用户管理等功能 -->
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="cache-control" content="no-cache"/>
<meta http-equiv="pragma" content="no-cache"/>
<meta http-equiv="expires" content="0"/>
<jsp:include page="/common/sysInclude.jsp" flush="true"/>

<link rel="stylesheet" type="text/css" href="<%=path%>/resources/css/mySpiritUi/pageFrame.css"/>
<script type="text/javascript" src="<%=path%>/resources/plugins/spiritui/jq.spirit.pageFrame.js"></script>

<title>用户主界面DEMO</title>
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
}.def-nav-right{
    display:block;
    float:right;
    height:48px;
    font:18px "Microsoft YaHei","Microsoft JhengHei","黑体";
    color:#d8d8d8;
    text-align:center;
    width:90px;
    line-height:48px
}.def-nav-sequence{
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
            <img src="./img/logo_op.jpg" style="height:100%;width:120px;" onclick="clickLogo()" alt="公司LOGO"/>
          </a>
        </td>    
        <td>
          <a href="#" class="def-nav" style="width:15%;">报告</a>
          <a href="#" class="def-nav" style="width:10%;">文件</a>
          <a href="#" class="def-nav" style="width:70%;">文件上传并分析</a>
        </td>    
        <td style="width:6%;">
          <a href="#" class="def-nav">用户处理</a>
        </td>
    </table>
  </div>
</div>

<!-- 脚部:悬浮 -->
<div id="footSegment" style="padding:10px 10px 0 10px;display:none;"></div>

<!-- 中间主框架 -->
<div id="mainSegment">
  <div style="padding:5px;border:1px solid #ddd;">
    <table style="width:100%;">
      <tr>
        <td style="width:20%;">          
        </td>    
        <td style="width:70%;">            
          <input id="idSearchFile" type="text" style="height:30px;width:500px;"></input>
          <input id="idSubmitSearchFile" type="submit"  style="height:30px;width:60px;cursor:pointer;font:18px Microsoft YaHei,Microsoft JhengHei,黑体;text-align:center;border-style:none;background:#32C52F;" value="搜索"></input> 
        </td>    
        <td style="text-align:right;">
          <a href="#" class="">
            <img src="./img/file_list.png" style="height:45px;width:45px;" onclick="alert('列表预览')" title="列表预览" alt="列表预览"/>
          </a>
          <a href="#" class="">
            <img src="./img/file_thumb.png" style="height:45px;width:45px;" onclick="alert('缩略图预览')" title="缩略图预览" alt="缩略图预览"/>
          </a>
        </td>
    </table>
  </div>
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
  top_height: 60,
  top_peg: false,
  myInit: initPos,
  myResize: initPos,
  win_min_width: 640, //页面最小的高度。当窗口高度小于这个值，不对界面位置及尺寸进行调整。主体部分宽度也照此设置
  win_min_height: 480 //页面最小的高度。当窗口高度小于这个值，不对界面位置及尺寸进行调整。主体部分高度也照此设置
};

function initPos() {
  //$("#mainFrame").spiritUtils("setWidthByViewWidth", $("body").width()-$("#sideFrame").spiritUtils("getViewWidth"));
  //$("#sideFrame").css("left", $("#reportFrame").width());
}
//主函数
$(function() {
  var initStr = $.spiritPageFrame(INIT_PARAM);
  if (initStr) {
    $.messager.alert("页面初始化失败", initStr, "error");
    return ;
  };

  $('#topSegment').css({'border':'1px solid #95b8e7','border-bottom':'0','background':'#32C52F','overflow':'hidden','border':'0','border-bottom':'0px'});
  //$('#footSegment').css({'border':'1px solid #32C52F','background':'#32C52F','opacity':'1'});
  
  initSubmitBt();
  initSearchFileInput();
});

//初始化查询输入框
var searchTxt = "请输入查询文件名";
function initSearchFileInput(){
  var _objSearch = $("#idSearchFile");
  _objSearch.val(searchTxt);
  _objSearch.css("color","grey");
  _objSearch.focus(function(){
    if($("#idSearchFile").val()==searchTxt){
      $(this).val("");
    }
  }).keydown(function(e){
    if(e.keyCode == 13){
      alert("您输入了："+getInputSearchFileStr());
    }
  });
}

//初始化查询提交按钮
function initSubmitBt(){
  $("#idSubmitSearchFile").css("margin-left","-6px");
  $("#idSubmitSearchFile").mouseover(function(){
    $(this).css("color","#CC0000");
  }).mouseout(function(){
    $(this).css("color","#000000");
  }).click(function(){      
    alert("您输入了："+getInputSearchFileStr());
  });
}

//获得输入的查询内容
function getInputSearchFileStr(){
  var searchedStr = ($("#idSearchFile").val()==searchTxt)?"":$("#idSearchFile").val();
  return searchedStr;
}

function clickLogo(){
  alert("click logo");
}
</script>
</html>