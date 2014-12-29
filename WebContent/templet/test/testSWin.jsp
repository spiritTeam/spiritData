<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%
  String path = request.getContextPath();
%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="cache-control" content="no-cache"/>
<meta http-equiv="pragma" content="no-cache"/>
<meta http-equiv="expires" content="0"/>
<jsp:include page="/common/sysInclude.jsp" flush="true"/>

<style>
</style>
</head>

<body>
窗口ID:<input id="thisWinId" type="text" style="width:200px"></input><input type="button" value="关闭窗口" onclick="testCloseWin()"/>
修改title:<input id="thisTitle" type="text" style="width:200px"></input><input type="button" value="修改" onclick="updateWin()"/>
</body>
<script>
$(function(){
  $("#thisWinId").val(getUrlParam(window.location.href, "_winID"));
});
function testCloseWin() {
  var mp = getMainPage();
  mp.$.messager.alert("提示", "测试", "info", function(){closeSWinInMain($("#thisWinId").val());});
}
function updateWin() {
  var win = getSWinInMain($("#thisWinId").val());
  win.modify({title: ""+$("#thisTitle").val()})
}
</script>
</html>