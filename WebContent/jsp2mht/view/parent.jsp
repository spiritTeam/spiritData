<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%
  String path = request.getContextPath();
%>
<html>
<head>
<script  src="<%=path %>/resources/plugins/jqplot/jquery.min.js" type="text/javascript"></script>
</head>
<body>
  <form action="">
    <input type="button" onclick="saveToMht();" value="点击保存成mht文件">
  </form>
  <iframe id="briefIframe" name="briefIframe" height="99%" width="100%" scrolling="auto"  src="cc.jsp"></iframe>
</body>
<script type="text/javascript">
//jquery获取iframe中所有dom
var iframeDoms="";
function getIframeDoms(){
	//获得iframeDoms
	iframeDoms = window.frames["briefIframe"].document;
}
function saveToMht(){
	getIframeDoms();
	$(iframeDoms).find("#imgInfoForm").submit();
}
</script>
</html>