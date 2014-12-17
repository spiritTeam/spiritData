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
<title>分析报告</title>
</head>
<body>
<div id="analReportArea" style="width:800px;height:700px;border: solid red 1px;float:left;margin-left:20px;"></div>
</body>
<script type="text/javascript">
$(function(){
  showAnalRst();
});
function showAnalRst(){
  var templetUrl = "<%=path%>/getTempletJson.do?templetId=";
  templetUrl = "<%=path%>/resources/plugins/templet/test/templet.json";
  templetUrl= "<%=path%>/templet/getTemplet.do";
  var pData = {templetId:"2323e"};
  $.ajax({type:"post",url:templetUrl,data:pData,dataType:"json",
    success:function(json){
      //var rst = str2JsonObj("jsonDats",json);不好使
      eval( "var jsonData="+json+";");
      if(jsonData.jsonType==1){
        var templetJD = jsonData.data;
        $.templetJD($("#analReportArea"),templetJD);
      }else{
    	$.messager.alert("提示",jsonData.message);
      }
    },error:function(errorData ){
      alert(errorData.length);
    }
  });
  //$.templetJD("aa");
}
</script>
</html>