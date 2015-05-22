<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
  String path = request.getContextPath();
%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>相同用户已在其他客户端登录</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="cache-control" content="no-cache"/>
<meta http-equiv="pragma" content="no-cache"/>
<meta http-equiv="expires" content="0"/>
<jsp:include page="/common/sysInclude.jsp" flush="true"/>
</head>
<body>
<script>
$(function(){
  var topWin = getTopWin();
  var url=window.location.href;
  var ip = getUrlParam(url, "clientIp");
  var mac = getUrlParam(url, "clientMacAddr");
  var browser = decodeURI(getUrlParam(url, "browser"));
  var mainPage = getMainPage();
  if (!mainPage) {
    var url="<%=path%>/logout.do";
    $.ajax({type:"post", async:false, url:url, data:null, dataType:"json",
      success: function(json) {
        var msg = "您已经在["+ip+"("+mac+")]客户端用["+browser+"]浏览器重新登录了，当前登录失效！<br/>请重新登录！";
        if ((!ip&&!mac)||(ip+mac=="")) msg = "您已经在另一客户端用["+browser+"]浏览器重新登录了，当前登录失效！<br/>请重新登录！";
        $.messager.alert("提示", msg, "info", function(){
        	//topWin.location.href="<%=path%>/asIndex.jsp";
        	topWin.location.href = _MAIN_PAGE;
        });
      },
      error: function(errorData) {
        $.messager.alert("错误", "注销失败：</br>"+(errorData?errorData.responseText:"")+"！<br/>返回登录页面。", "error", function(){
      	  //topWin.location.href="<%=path%>/asIndex.jsp?noAuth";
        	topWin.location.href=_MAIN_PAGE+"?noAuth";
        });
      }
    });
  } else mainPage.onlyLogout(ip, mac, browser);
});
</script>
</body>
</html>