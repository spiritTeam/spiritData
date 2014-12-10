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
var noLoginUrl="<%=path%>/asIndex.jsp?nolog";

var mainPage=getMainPage();
if (mainPage) mainPage.location.href=noLoginUrl;
else {
  var topWin = getTopWin();
  topWin.location.href=noLoginUrl;
}
</script>
</body>
</html>