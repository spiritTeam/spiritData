<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%String path = request.getContextPath();%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<jsp:include page="/common/sysInclude.jsp" flush="true"/>
<!-- pageFrame -->
<script type="text/javascript" src="<%=path%>/resources/plugins/spiritui/jq.spirit.pageFrame.js"></script>
<!-- plot -->
<script type="text/javascript" src="<%=path%>/resources/plugins/flot/excanvas.min.js"></script>
<script type="text/javascript" src="<%=path%>/resources/plugins/flot/jquery.flot.js"></script>
<script type="text/javascript" src="<%=path%>/resources/plugins/flot/jquery.flot.pie.js"></script>
<script type="text/javascript" src="<%=path%>/resources/plugins/flot/jquery.flot.categories.js"></script>
<!-- 雷达图，也可考虑用eChart -->
<script type="text/javascript" src="<%=path%>/resources/plugins/Chart.min.js"></script>
<!-- ECharts单文件引入 -->
<script src="<%=path%>/resources/plugins/echarts-2.2.1/echarts.js"></script>
<script type="text/javascript">
/**
 * echarts
 */
// 路径配置
require.config({
  paths: {
    echarts: '<%=path%>/resources/plugins/echarts-2.2.1',
    zrender: '<%=path%>/resources/plugins/echarts-2.2.1/zrender'
  }
});
</script>
<!-- report -->
<script type="text/javascript" src="<%=path%>/reportFrame/serial/js/spirit.report.parse.js"></script>
<link type="text/css" rel="stylesheet" href="<%=path%>/resources/plugins/report/css/report.css"/>
<title>序列化报告显示壳容器</title>
</head>
<style>
body {
  background-color:#fff;
}
</style>
<body>
</body>
<script type="text/javascript">
var getReportUrl = _PATH+"/report/getReport.do?";
$(function() {
  var rP = new Object();
  var reportUri=getUrlParam(window.location.href, "reportUri");
  if (reportUri&&$.trim(reportUri)!=""&&$.trim(reportUri)!="undefined") rP.reportUri = reportUri;
  var reportId = getUrlParam(window.location.href, "reportId");
  if (reportId&&$.trim(reportId)!=""&&$.trim(reportId)!="undefined") rP.reportId = reportId;
  var getUrl = getUrlParam(window.location.href, "getUrl");
  if (getUrl&&$.trim(getUrl)!=""&&$.trim(getUrl)!="undefined") rP.getUrl = getUrl;

  generateReport(rP);
});
</script>
</html>