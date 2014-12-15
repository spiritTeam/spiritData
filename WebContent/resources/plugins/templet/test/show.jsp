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
<title>分析报告</title>
</head>
<body>
<input type="button" value="显示分析结果" onclick="showAnalRst();"/>
<input id="templetId" type="hidden" value=""/>
<div id="analReportArea"></div>
</body>
<script type="text/javascript">
//aaa=window.href::templetId
$(function() {
  var templetId = getUrlParam(window.location.href, "templetId");
  $('#templetId').val(templetId);
});

function showAnalRst(){
  var templetId = $('#templetId').val();
  var templetUrl = "<%=path%>/getTempletJson.do?templetId="+templetId;
  $.templetJD($("#analReportArea"), templetUrl);
}
</script>
</html>