<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%
  String path = request.getContextPath();
  String searchStr = request.getParameter("searchStr");
  //System.out.println("searchStr="+searchStr);
%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<!-- 通用查询显示结果页面 -->
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="cache-control" content="no-cache"/>
<meta http-equiv="pragma" content="no-cache"/>
<meta http-equiv="expires" content="0"/>

<jsp:include page="/common/sysInclude.jsp" flush="true"/>

<!-- 加载ZUI - 开源HTML5跨屏框架 -->
<link href="<%=path%>/resources/plugins/zui/css/zui.min.css" rel="stylesheet">
<link href="<%=path%>/resources/plugins/zui/css/example.css" rel="stylesheet">
<script src="<%=path%>/resources/plugins/zui/js/zui.min.js"></script>

<title>列表显示查询结果主界面</title>
</head>
<style>
.padding_top5{padding-top:5px;}
.font_size15{font-size:15px;}
.font_size18{font-size:18px;}
</style>
<body class="padding_top5" style="background-color:#FFFFFF">
  <div class="list" style="width:1200px;margin:0 auto;">
    <section class="items items-hover">
      <div class="item">
        <div class="item-heading">
          <div class="pull-right"><a href="###"><i class="icon-list"></i>浏览</a> &nbsp;<a href="#"><i class="icon-building"></i>报告</a></div>
          <h4><span class="label label-success font_size15">XLS</span>&nbsp; <a href="###" class="font_size18">文件1</a></h4>
        </div>
        <div class="item-content">
          <div class="media pull-left">
            <div class="media-place-holder" style="width:200px;height:100px;line-height:100px">200x100</div>
          </div>
          <div class="text font_size15">文件1的简要描述信息在此显示!<br>上传人：XXX，上传时间：XXX</div>
        </div>
        <div class="item-footer">
        </div>
      </div>
      <div class="item">
        <div class="item-heading">
          <div class="pull-right"><a href="###"><i class="icon-list"></i>浏览</a> &nbsp;<a href="#"><i class="icon-building"></i>关系</a></div>
          <h4><span class="label label-success font_size15">RPT</span>&nbsp; <a href="###" class="font_size18">报告1</a></h4>
        </div>
        <div class="item-content">
          <div class="media pull-left">
            <div class="media-place-holder" style="width:200px;height:100px;line-height:100px">200x100</div>
          </div>
          <div class="text font_size15">报告1的简要描述信息在此显示!<br>上传人：XXX，上传时间：XXX</div>
        </div>
        <div class="item-footer">
        </div>
      </div>
    </section>
  </div>
</body>
<script>
//主函数
$(function() {
  
});

</script>
</html>