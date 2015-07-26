<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!--
版本说明：这里说明每个版本的情况，用倒序方式进行说明
 -->

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="cache-control" content="no-cache"/>
<meta http-equiv="pragma" content="no-cache"/>
<meta http-equiv="expires" content="0"/>
<jsp:include page="/common/sysInclude.jsp" flush="true"/>

<title>版本说明</title>

<style type="text/css">
#leftArea {
  position:absolute;
  padding-top:20px;
  width:200px;
  border-right:1px solid #067239;
}
#mainArea {
  position:absolute;
  padding:20px;
}
#_vList div {
  color:blue;
  padding:10px;
  margin-left:20px;
  htight:24px;
  font-size:16px;
}
.oneVer {
  border-top:1px solid #ccc;
  margin-bottom:20px;
}
.verTitle {
  padding:5px;
  padding-top:10px;
  font-size:16px;
}
.verNum {
  font-weight:bold;
  font-size:20px;
  display:inline;
  margin-right:20px;
}
li {font-size:14px;}
</style>

</head>
<body>
<!-- 左侧版本列表 -->
<div>
<div id="leftArea">
  <div id="_vList">
    <div>v0.0.1(2015/07/02)</div>
    <div>v0.0.2(2015/07/26)</div>
  </div>
  <div id="_vCommon">
  </div>
</div>
<!-- 右侧的版本说明 -->
<div id="mainArea">
  <div id="v0.0.1" class="oneVer">
  <div class="verTitle"><div class="verNum"> v0.0.1</div> 发布时间-2015年07月02日</div>
  <ul>
    <li>内部测试版上线，提供最基本的excel上传，分析其数据，给出分析报告的功能</li>
  </ul>
  </div>

  <div id="v0.0.2" class="oneVer">
  <div class="verTitle"><span class="verNum"> v0.0.2</span> 发布时间-2015年07月26日</div>
  <ul>
    <li>1-报告生成后使得界面可以同步</li>
    <li>2-用户忘记密码后可以通过注册的邮箱找回密码</li>
    <li>3-验证码图形从内存读取，提升用户管理相关页面的性能</li>
    <li>4-实现了文件和报告的分页功能</li>
    <li>5-报告支持多图形共图显示，提升报告的可阅读性</li>
    <li><img src="V_0.0.2_001.jpg" style="width:400px;height:200px;"></img></li>
    <li>6-数据文件存储实现与中间件目录分离，提高数据安全性</li>
  </ul>
  </div>

</div>
</body>
<script>
$(function() {
  var _vers = $(".oneVer");
  var i=0;
  var len=_vers.length;
  for (; i<len; i++) {
    var _ver = _vers[i];
    $(_ver).css({
      "border-top":"1px solid #ccc"
    });
  }
  adjustWin();
  $(window).resize(adjustWin);
  $(window).scroll(onScroll);
});
function adjustWin() {
  $("#leftArea").height($(window).height()-21);
  $("#mainArea").css({"top":0,"left":$("#leftArea").width()+1});
  $("#mainArea").width($(window).width()-$("#leftArea").width()-41);
}
function onScroll() {
  $("#leftArea").css({"top":$(document).scrollTop()});
}
</script>
</html>