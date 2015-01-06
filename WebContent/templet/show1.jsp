<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
  String path = request.getContextPath();
  String sid = request.getSession().getId();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<jsp:include page="/common/sysInclude.jsp" flush="true"/>
<script type="text/javascript" src="<%=path %>/resources/plugins/templet/jq.spirit.templet.js"></script>
<link rel="stylesheet" type="text/css" href="<%=path%>/resources/plugins/templet/css/templet.css"/>
<!-- pageFrame -->
<script type="text/javascript" src="<%=path%>/resources/plugins/spiritui/jq.spirit.pageFrame.js"></script>
<title>分析报告</title>
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
</style>
<body>
<!-- 头部:悬浮 -->
<div id="topSegment">
  <div id="rTitle"></div>
</div>

<!-- 脚部:悬浮 -->
<div id="mainSegment" style="padding:10px 10px 0 10px;">

<div id="sideFrame">
  <div id="catalogTree" style="border:1px solid #E6E6E6; width:258px; "></div>
</div>
<div id="reportFrame">
</div></div>
</body>
<script type="text/javascript">
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
  myResize: initPos
};
function initPos() {
  $("#reportFrame").spiritUtils("setWidthByViewWidth", $("body").width()-$("#sideFrame").spiritUtils("getViewWidth"));
  $("#sideFrame").css("left", $("#reportFrame").width());
}
$(function(){
  var initStr = $.spiritPageFrame(INIT_PARAM);
  if (initStr) {
    $.messager.alert("页面初始化失败", initStr, "error");
    return ;
  };
  showAnalRst();
});
function showAnalRst(){
  var templetUrl= "<%=path%>/templet/getTemplet.do";
  var pData = {templetId:"2323e"};
  $.ajax({type:"post",url:templetUrl,data:pData,dataType:"json",
    success:function(json){
      rst=str2JsonObj("jsonData",json);
      if(rst.jsonType==1){
      var templetJD = rst.data;
      	//暂时宽和高无用，因为用了晖哥的
        var jsonTempletObj={
          viewWidth:'',
          viewHeight:'',
          templetData:templetJD
        };
        $.templetJD(jsonTempletObj);
      }else{
      $.messager.alert("提示",jsonData.message,'info');
      }
    },error:function(errorData ){
    }
  });
}
</script>
</html>