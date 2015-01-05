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
</style>
<body>
<span id="test"></span>
</body>
<script type="text/javascript">
var k=10000;
$(function(){
  var templetId = "2323e";
  var templetUrl = "<%=path%>/templet/getTemplet.do";
  //$.templetJD(templetUrl,templetId);
});
setInterval('test()',1000);
function test(){
	k=++k;
	$('#test').html(k);
}
<html>
<body>

<input type="text" id="clock" size="35" />
<script language=javascript>
var int=self.setInterval("clock()",50)
function clock()
  {
  var t=new Date()
  document.getElementById("clock").value=t
  }
</script>
<button onclick="int=window.clearInterval(int)">Stop interval</button>

</body>
</html>
</script>
</html>
