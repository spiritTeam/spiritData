<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
  String path = request.getContextPath();
  String sid = request.getSession().getId();
  String reportId = request.getParameter("reportId");
  String reportUrl = request.getParameter("reportUrl");
  
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<jsp:include page="/common/sysInclude.jsp" flush="true"/>
<!-- report -->
<script type="text/javascript" src="<%=path %>/resources/plugins/report/jq.spirit.report.js"></script>
<script type="text/javascript" src="<%=path %>/resources/plugins/report/jq.spirit.report.util.js"></script>
<link rel="stylesheet" type="text/css" href="<%=path%>/resources/plugins/report/css/report.css"/>
<!-- pageFrame -->
<script type="text/javascript" src="<%=path%>/resources/plugins/spiritui/jq.spirit.pageFrame.js"></script>
<!-- plot -->
<script type="text/javascript" src="<%=path%>/resources/plugins/flot/excanvas.min.js"></script>
<script type="text/javascript" src="<%=path%>/resources/plugins/flot/jquery.flot.js"></script>
<script type="text/javascript" src="<%=path%>/resources/plugins/flot/jquery.flot.pie.js"></script>
<script type="text/javascript" src="<%=path%>/resources/plugins/flot/jquery.flot.categories.js"></script>
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
<title>分析报告</title>
</head>
<style>
body {
  background-color:#fff;
}
</style>
<body>
</body>
<script type="text/javascript">
$(function(){
  //reportId和report有初始值，如果能从url中取到，就不用默认值
  var reportId = "asdaw";
  var reportUrl = "<%=path%>/report/getReport.do?uri=demo\\reportDemo\\report.json";
  if ("<%=reportId%>"!=null&&"<%=reportId%>"!="") reportId = "<%=reportId%>";
  if ("<%=reportUrl%>"!=null&&"<%=reportUrl%>"!="") reportUrl = "<%=reportUrl%>";
  ///SpiritAnalysis/WebContent/DataCenter/report/ee9a9666cc184575b74b0b12fc5a6e66/afterImport(IMPFID-f268804f936e4720a78f7b7db46e0fb1).json
  //demo\\reportDemo\\report.json
  //alert("show report url="+reportUrl);
  generateReport(reportUrl,reportId);
});
</script>
</html>
