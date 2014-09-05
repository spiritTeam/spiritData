<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.gmteam.framework.FConstants"%>
<!DOCTYPE html>
<html>
<head>
<title>导引页</title>
<meta http-equiv="pragma" content="no-cache"/>
<meta http-equiv="cache-control" content="no-cache"/>
<meta http-equiv="expires" content="0"/>
</head>
<body></body>
<script>
var isOpenWin=true;//是否弹出窗口
var isOpenWinIndex="yes";//主界面是否弹出窗口

isOpenWin=false;
isOpenWinIndex="no";

if (isOpenWin) {
  var screenH=screen.Height, screenW=screen.Width;
  var winH=618, winW=1000;
  var wTop=0, wLeft=0;
  if (screenH>winH) wTop =parseInt((screenH-winH)/2);
  if (screenW>winW) wLeft=parseInt((screenW-winW)/2);
  window.open(
    "login/login.jsp",
    "<%=FConstants.PLATFORM_NAME%>",
    "alwaysRaised=yes,menubar=no,location=no,resizable=yes,scrollbars=yes,status=no,height="+winH+", width="+winW+", top="+wTop+", left="+wLeft
  );
  window.opener=null;
  self.close();
} else {
  window.location.href="login/login.jsp?openWindow="+isOpenWinIndex;
}
</script>
</html>