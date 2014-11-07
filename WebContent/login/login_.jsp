<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.Map"%>
<%@page import="com.gmteam.framework.FConstants"%>
<%@page import="com.gmteam.framework.core.cache.SystemCache"%>
<%@page import="com.gmteam.framework.component.login.pojo.UserLogin"%>
<%@page import="com.gmteam.framework.core.cache.CacheEle"%>
<%@page import="com.gmteam.framework.UGA.UgaUser"%>
<%
  String path = request.getContextPath();
  CacheEle<Map<String, UserLogin>> mc = (CacheEle<Map<String, UserLogin>>)SystemCache.getCache(FConstants.USERSESSIONMAP);
  Map<String, UserLogin> userSessionMap = mc.getContent();
  UgaUser u = (UgaUser)session.getAttribute(FConstants.SESSION_USER);
  UserLogin uli = null;
  if (u!=null) uli=userSessionMap.get(u.getUserId());
  boolean sessionIsMe=(u==null?false:(uli==null?false:(session.getId().equals(uli.getSessionId()))));
%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title><%=FConstants.PLATFORM_NAME%></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="cache-control" content="no-cache"/>
<meta http-equiv="pragma" content="no-cache"/>
<meta http-equiv="expires" content="0"/>
<meta content="MSHTML 6.00.2800.1106" name=GENERATOR/>
<jsp:include page="/common/sysInclude.jsp" flush="true"/>

<style type="text/css">
html {background:#0F402B url(<%=path%>/resources/images/login/grass.jpg) no-repeat; overflow:auto; background-position:top center;}

#loginDiv {position:absolute; width:450px; height:95px; left:100px; top:100px;}
#loginDiv {display:none; background:#0F402B; border:1px solid red;}

#userTable {position:absolute; width:380px; left:20px; top:10px;}
#userTable label {float:right; font-weight:bold; font-size:20px; width:90px; font-family:SimHei; color:#fff}
#userTable td {line-height:24px; height:35px;}
#userTable input {width:120px; height:20px; background:#458C4A; }

#footDiv {position:absolute; text-align:center; bottom:5px; width:360px; height:40px;display:yes;}
#footDiv ul li {color:#3399FF;}
#footDiv {border:1px solid blue;}

#commitButton {position:relative; background:#fff url(<%=path%>/resources/images/login/button_dl.jpg) no-repeat;}
#commitButton {width:92px; height:57px;}
#commitButton {border:1px;}

#mask {border-radius:10px; -moz-border-radius:10px; filter:alpha(opacity=70); opacity:0.7; background:#E7F1F5; z-index:1000;}
</style>
</head>
<body>

<object id="locator" classid="CLSID:76A64158-CB41-11D1-8B02-00600806D9B6" style="display:none;visibility:hidden"></object>
<object id="foo" classid="CLSID:75718C9A-F029-11d1-A1AC-00C04FB6C223" style="display:none;visibility:hidden"></object>
<form id="fooform" name="fooForm" style="display:none">
  <input type="hidden" name="txtMACAddr"/>
  <input type="hidden" name="txtIPAddr"/>
  <input type="hidden" name="txtDNSName"/>
</form>
<script>
var service = locator.ConnectServer();
var MACAddr;
var IPAddr;
var DomainAddr;
var sDNSName;
service.Security_.ImpersonationLevel=3;
service.InstancesOfAsync(foo, 'Win32_NetworkAdapterConfiguration');
</script>

<script language="JScript" event="OnCompleted(hResult,pErrorObject, pAsyncContext)" for="foo">
fooForm.txtMACAddr.value=unescape(MACAddr);
fooForm.txtIPAddr.value=unescape(IPAddr);
fooForm.txtDNSName.value=unescape(sDNSName);
</script>
<script language="JScript" event="OnObjectReady(objObject,objAsyncContext)" for="foo">
if(objObject.IPEnabled != null && objObject.IPEnabled != "undefined" && objObject.IPEnabled == true) {
  if(objObject.MACAddress != null && objObject.MACAddress != "undefined") MACAddr = objObject.MACAddress;
  if(objObject.IPEnabled && objObject.IPAddress(0) != null && objObject.IPAddress(0) != "undefined") IPAddr = objObject.IPAddress(0);
  if(objObject.DNSHostName != null && objObject.DNSHostName != "undefined") sDNSName = objObject.DNSHostName;
}
</script>

<!-- 遮罩层 -->
<div id="mask" style="display:none; position:absolute;vertical-align:middle;text-align:center; align:center;">
  <img align="middle" src="<%=path%>/resources/images/waiting_circle.gif"/><br/><br/>
  <span style="font-weight:bold;" id="maskTitle">请稍候，登录中...</span>
</div>

<div id="loginDiv">
  <table id="userTable">
    <tr>
      <td style="padding-top:2px"><label for="loginName">登录名：</label></td>
      <td style="padding-top:2px"><input type="text" id='loginName' tabindex="1"/></td><!-- 注意tabindex属性，不要漏掉了 -->
      <td rowspan="2" style="width:160px;height:70px;align:center;padding-left:30px;"><div id="commitButton"></div></td>
    </tr>
    <tr>
      <td style="padding-bottom:2px"><label for="password">密　码：</label></td>
      <td style="padding-bottom:2px"><input type="password" id='password' tabindex="2"/></td>
    </tr>
  </table>
</div>
<div id="footDiv">
<ul>
  <li style="font-weight:bold">请保管好您的登录名及密码!</li>
  <li style="float:left;margin-top:3px;margin-bottom:10px;"><span style=" color:#F00;">版权所有：</span><span style=" margin-left:3px;color:#3399FF;">权所有版权所有</span></li>
  <li style="float:left;margin-top:3px;margin-left:20px;margin-bottom:10px;">请使用IE8.0或以上版本的IE浏览器</li>
</ul>
</div>

<script>
$(function() {
  setElementPosition();
  setBodyEnter(true);
  $("#commitButton")
  .mouseover(function(){
    $("#commitButton").css({
      "border":"1px solid #042B19"
    });
  })
  .mouseout(function(){
    $("#commitButton").css({
      "border":"0px solid #042B19",
      "top": "0px",
      "left":"0px"
    });
  })
  .mousedown(function(){
    $("#commitButton").css({
      "top": "2px",
      "left":"2px"
    });
  })
  .mouseup(function(){
    $("#commitButton").css({
      "top": "0px",
      "left":"30px"
    });
    loginF();
  });

  var url = window.location.href;
  if (url.indexOf("?noAuth")>0) $("#loginDiv").show();
  else if (url.indexOf("?nolog")>0) {
    $.messager.alert("提示", "请先登录！", "info", function(){
      $("#loginDiv").show();
    });
  } else {
    if (<%=sessionIsMe%>&&("<%=u%>"!="null")) {
      $.messager.alert("提示", "您[<%=(u==null?"":u.getUserName())%>]已登录，请先注销！<br/>现返回主页。", "info", function(){
        window.location.href="<%=path%>/mainPage.jsp";
      });
    } else $("#loginDiv").show();
  }
  $(window).resize(setElementPosition);
});

//设置登录功能区域的位置，包括登录区域loginDiv/遮罩区域mask/提示区域footDiv
function setElementPosition() {
  //登陆区域位置及样式
  $("#loginDiv").css({
    "top":"500px"
  });
  $("#loginDiv").css({
    "left":($(window).height()>600?((($(window).width()-$("#loginDiv").width())/2)+330)+"px":"620px")
  });

  //提示页脚位置及样式
  $("#footDiv").css({
    "left":(($("#loginDiv").width()-$("#footDiv").width())/2),
    "top": 600,
    "left": 830
  });

  //遮罩层位置及样式
  $("#mask").css({
    "padding-top": 25,
    "top": parseInt($("#loginDiv").css("top"))-10,
    "left": parseInt($("#loginDiv").css("left"))-10,
    "width": (parseInt($("#loginDiv").css("width"))+20)+"px",
    "height": (parseInt($("#loginDiv").css("height"))+40)+"px"
  });
}

//登录方法
function loginF(){
  setBodyEnter(false);
  var checkOk = false;
  if ($("#loginName").val()=="") {
    var alertStr = "请输入登录名！";
    if ($("#password").val()=="") alertStr+="<br/>密码不能为空，请输入密码！";
    $.messager.alert("提示", alertStr, "warning", function(){
      $("#loginName").focus();
      setBodyEnter(true);
    });
  } else if ($("#password").val()=="") {
    $.messager.alert("提示", "密码不能为空，请输入密码！", "warning", function(){
      $("#password").focus();
      setBodyEnter(true);
    });
  } else checkOk=true;
  if (!checkOk) return;

  $("#mask").show();
  var url="<%=path%>/login.do";
  var pData={
    "loginName":$("#loginName").val(),
    "password":$("#password").val(),
    "clientMacAddr":fooForm.txtMACAddr.value?(fooForm.txtMACAddr.value=="undefined"?"":fooForm.txtMACAddr.value):"",
    "clientIp":fooForm.txtIPAddr.value?(fooForm.txtIPAddr.value=="undefined"?"":fooForm.txtIPAddr.value):"",
    "browser":getBrowserVersion()
  };

  $.ajax({type:"post", async:false, url:url, data:pData, dataType:"json",
    success: function(json) {
      if (json.type==1) {
        var openWindow=getUrlParam(window.location.href, "openWindow");
        url = "<%=path%>/mainPage.jsp";
        if (openWindow==null||openWindow==""||openWindow!="yes") {
          window.location.href= url;
        } else {
          var screenH=screen.Height, screenW=screen.Width;
          var winH=618, winW=1000;
          var wTop=0, wLeft=0;
          if (screenH>winH) wTop =parseInt((screenH-winH)/2);
          if (screenW>winW) wLeft=parseInt((screenW-winW)/2);
          window.open(url, "<%=FConstants.PLATFORM_NAME%>",
            "alwaysRaised=yes,menubar=no,location=no,resizable=yes,scrollbars=yes,status=no,height="+winH+", width="+winW+", top="+wTop+", left="+wLeft
          );
          window.opener=null;
          self.close();
        }
      } else if (json.type==2) {
        $.messager.alert("错误", "登录失败："+json.data, "error", function(){
          $("#loginname").focus();
          $("#mask").hide();
          setBodyEnter(true);
        });
      } else {
        $.messager.alert("错误", "登录异常："+json.data, "error", function(){
          $("#loginname").focus();
          $("#mask").hide();
          setBodyEnter(true);
        });
      }
    },
    error: function(errorData) {
      if (errorData) {
        $.messager.alert("错误", "登录异常：未知！", "error", function(){
          $("#loginname").focus();
          $("#mask").hide();
          setBodyEnter(true);
        });
      } else {
        $("#mask").hide();
        setBodyEnter(true);
      }
    }
  });
}

//设置body的按下enter键的事件，flag为true设置为登录，flag为false清除enter事件
function setBodyEnter(flag) {
  if (flag) {
    setTimeout(function(){
      $("body").bind("keydown", function(e) {
        if (e.keyCode==13) {
          $("#commitButton").css({
            "border":"1px solid #042B19",
            "top": "2px",
            "left":"2px"
          });
        }
      }).bind("keyup", function(e) {
        if (e.keyCode==13) {
          $("#commitButton").css({
            "border":"0px solid #042B19",
            "top": "0px",
            "left":"0px"
          });
          loginF();
        }
      });
    }, 200);
  } else {
    $("body").unbind("keydown").unbind("keyup");
  }
}
</script>
</body>
</html>