<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%
  String path = request.getContextPath();
%>
<html>
<head>
<script  src="<%=path %>/resources/plugins/jqplot/jquery.min.js" type="text/javascript"></script>
</head>
<body>
  <form action="">
    <input type="button" onclick="jsp2Mht();" value="2mht">
    url:<input id="url" type="text" onblur="getUrl();"/>
       <input type="button" value="预览" onclick="preview();" />
  </form>
  <iframe id="briefIframe" name="briefIframe" height="99%" width="100%" scrolling="auto"  src=""></iframe>
</body>
<script type="text/javascript">
var url = "";
function getUrl(){
	url = $('#url').val();
}
function preview(){
	briefIframe.location.href=url;
}
function jsp2Mht(){
	var pData={
     "url":url
   };
	$.ajax({type:"post", async:false, url:"<%=path%>/jsp2Mht.do", data:pData, dataType:"json",
    success: function(json) {
    	if(json==true){
    		alert("转换成功!");
    	}else{
    		alert("转换失败!");
    	}
    }
	});
}
</script>
</html>