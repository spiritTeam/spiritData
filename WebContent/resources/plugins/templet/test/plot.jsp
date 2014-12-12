<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java"%>
<%
  String path = request.getContextPath();
%>
<html>
<head>
<jsp:include page="/common/sysInclude.jsp" flush="true"/>
<link rel="stylesheet" type="text/css" href="<%=path%>/resources/css/mySpiritUi/pageFrame.css"/>
<script type="text/javascript" src="<%=path%>/resources/plugins/spiritui/jq.spirit.utils.js"></script>
<script type="text/javascript" src="<%=path%>/resources/plugins/spiritui/jq.spirit.pageFrame.js"></script>
<script type="text/javascript" src="<%=path%>/resources/js/mainPage.utils.js"></script>
<script type="text/javascript" src="<%=path%>/resources/plugins/flot/excanvas.min.js"></script>
<script type="text/javascript" src="<%=path%>/resources/plugins/flot/jquery.flot.js"></script>
<script type="text/javascript" src="<%=path%>/resources/plugins/flot/jquery.flot.pie.js"></script>
<script type="text/javascript" src="<%=path%>/resources/plugins/flot/jquery.flot.categories.js"></script>
<script type="text/javascript" src="<%=path%>/resources/plugins/Chart.min.js"></script>
<script type="text/javascript" src="<%=path %>/resources/js/brief.jqplotToImg.js"></script>
<script type="text/javascript" src="plot.js"></script>
</head>
<body>
<input type="button" value="test" onclick="test();">
<input type="button" value="折线图" onclick="showPolt(line)">
<input type="button" value="饼图" onclick="showPolt(pie)">
<input type="button" value="柱图" onclick="showPolt(bars)">
<input type="button" value="全部" onclick="showPolt(all)">
<div  class="jqplot" id="flotDemo1" style="width:400px;height:170px;"></div>
<div class="jqplot" id="flotDemo2" style="width:300px;height:200px;"></div>
<div class="jqplot" id="flotDemo3" style="width:300px;height:200px;"></div>
</body>
<script type="text/javascript">
function showPolt(showMethod){
  var pData={"showMethod":'bars'};
  var url = '<%=path%>/plot/testData.json'
  $.ajax({type:"post", async:false, url:url, data:pData, dataType:"json",
    success:function(json){
      var id = "flotDemo1";
      entrance(id,showMethod,json);
      bars("flotDemo1",json, yseis="sex", xseis="number");
      $("#flotDemo1").bar(data=json,yseis="sex", xseis="number");
      pie("flotDemo2",json);
      line("flotDemo3",json);
    }
  });
}
//方式一
function test(){
  aa.b("ABC");
}
var aa={
  b:function(c) {alert(c);}
};
//方式二
function a(){
  a.prototype.b=function (c) {
    alert(c);
  }
};
var _a=new a();
_a.b("hello");

</script>
</html>