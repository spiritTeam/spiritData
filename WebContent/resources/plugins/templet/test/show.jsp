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
<title>分析报告</title>
</head>
<body>
<div id="analReportArea"></div>
</body>
<script type="text/javascript">
$(function(){
  showAnalRst();
});
function showAnalRst(){
  var templetUrl = "<%=path%>/getTempletJson.do?templetId=";
  templetUrl = "<%=path%>/resources/plugins/templet/test/templet.json"
  var pData = {};
  $.ajax({type:"post",async:false,url:templetUrl,data:pData,dataType:"json",
    success:function(json){
    alert(json);  
   //   $.templetJD($("#analReportArea"),templetJson);
    },error:function(errorData ){
      alert(errorData.length);
    }
  });
  //$.templetJD("aa");
}
</script>
</html>