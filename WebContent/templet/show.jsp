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
<div style="border-width: "></div>
</body>
<script type="text/javascript">
$(function(){
  var templetUrl = "<%=path%>/templet/getTemplet.do";
  var templetId = "2323e";
  var jsonDId = "<%=path%>/jsonD/getJsonD.do?uri=\demo\templetDemo\metedataInfo550E8400-E29B-11D4-A716-446655440000.jsond";
  $.templetJD(templetUrl,templetId);
});
</script>
</html>
