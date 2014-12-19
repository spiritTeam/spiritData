<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
  String errorJsonStr=(String)request.getAttribute("errorJson");
  System.out.println(errorJsonStr);
  String path = request.getContextPath();
%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>错误页面</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="cache-control" content="no-cache"/>
<meta http-equiv="pragma" content="no-cache"/>
<meta http-equiv="expires" content="0"/>
<jsp:include page="/common/sysInclude.jsp" flush="true"/>
</head>
<body>
系统出现错误！
<script>
var errInfo=<%=errorJsonStr%>;

$(function(){
  var mainPage=getMainPage();
  var noLoginUrl = "<%=path%>/login/login.jsp?noAuth";
  if (!errInfo) {
    $.messager.alert("未知情况", "未知情况", "error");
  } else {
    var jumpTo = errInfo.nextPage;
    var alertType = errInfo.type;
    var alertTitle = errInfo.title;
    var alertMsg = errInfo.message;
    $("body").html(alertMsg);
    if (mainPage) {
      mainPage.$.messager.alert(alertTitle, alertMsg+(jumpTo?"<br/>将跳转到"+jumpTo+"页面！":""), alertType, function() {
        if (jumpTo) mainPage.location.href=noLoginUrl;
      });
    } else {
      $.messager.alert(alertTitle, alertMsg+(jumpTo?"<br/>将跳转到"+jumpTo+"页面！":""), alertType, function() {
        var topWin = getTopWin();
        if (jumpTo) topWin.location.href=noLoginUrl;
      });
    }
  }
});
</script>
</body>
</html>