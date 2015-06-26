<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
  String path = request.getContextPath();
%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>未登录</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="cache-control" content="no-cache"/>
<meta http-equiv="pragma" content="no-cache"/>
<meta http-equiv="expires" content="0"/>
<jsp:include page="/common/sysInclude.jsp" flush="true"/>
</head>
<body>
<script type="text/javascript">
//var noLoginUrl="<%=path%>/asIndex.jsp?nolog";
var noLoginUrl = _MAIN_PAGE+"?nolog";

var mainPage=getMainPage();
winId = getUrlParam(window.location.href, "_winID");
if (winId) {
  showAlert("提示", "请先登录！", "info", function() {
    mainPage.location.href=noLoginUrl+"&type=1";
  });
} else {
  if (mainPage) {
    mainPage.location.href=noLoginUrl+"&type=2";
  } else {
    window.location.href=noLoginUrl+"&type=2";
  }
}
</script>
</body>
</html>