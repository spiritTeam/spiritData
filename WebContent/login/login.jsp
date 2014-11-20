<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
  String path = request.getContextPath();
  String sid = request.getSession().getId();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>login</title>
<jsp:include page="/common/sysInclude.jsp" flush="true"/>
<link rel="stylesheet" type="text/css" href="<%=path%>/login/css/login.css" />
<style type="text/css">
#loginName{padding-top:8px; height:25px; font-size:14px;}
#password{padding-top:8px; height:25px; font-size:14px;}
#checkCode{width: 115px; padding-top:8px; height:25px; font-size:14px;}
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
<center>
  <div style="border:1px solid #ABCDEF;width: 450px;height: 500px;">
    <div style="margin-top: 15px; margin-left: 25px;"align="left"><span style="font-size: 20px;color: #999999;">账号登录</span></div>
    <div style="height:2px; width:400px;border-top: 1px solid  #999999;"></div>
    <form  style="margin-top: 15px;" action="">
      <table width="370px;" border="1px;">
        <tr style="height:50px; valign:top;">
          <td align="right" width="100px;"><span class="myspan">账号&nbsp;&nbsp;</span></td>
          <td colspan="2" rowspan="1" width="200px;" style="text-align:left;">
            <div style="float: left;">
              <input id="loginName" name="loginName" tabindex="1" type="text"  value="账号/QQ/手机号" 
                onclick="onClick(loginName);" onBlur="validateLoginName('loginName');"/>
            </div>
            <div style="float: left;width: 25px;height: 25px;padding-top: 8px;margin-left: -2px;" align="center" id='vLN'></div>
          </td>
        </tr>
        <tr style="height:50px; valign:top;">
          <td align="right"><span class="myspan">密码&nbsp;&nbsp;</span></td>
          <td colspan="2" style="text-align:left;" id="pwTd">
	          <div style="float: left;">
	            <input id="password" name="password" tabindex="2" type="password" value=""  onselect="" onmouseover="pwdMouseOver();"
	                onclick="onClick(password);" onBlur="validatePassword('password');" /></div>
	          <div style="float: left;width: 25px;height: 25px;padding-top: 8px;margin-left: -2px;" align="center" id='vPW'></div>
	          <div id="pwDiv" style="float: left;width: 25px;height: 25px;padding-top: 10px;margin-left: -225px;" align="center" >
	            <span id="pwdSpan" style="color: #ABCDEF;font-size: 12px;">密码</span></div>
          </td>
        </tr>
        <tr style="height:50px; valign:top;">
          <td align="right"><span class="myspan">验证码&nbsp;&nbsp;</span></td>
          <td colspan="2">
            <div style="float: left;">
              <input type="text" id="checkCode" name="checkCode" tabindex="3" value="验证码" 
                onclick="onClick(checkCode);" onBlur="validateValidateCode('checkCode');" />
            </div>
            <div style="float: left;border: 1px solid #999999;width: 80px;margin-left: -3px;" >
              <img title="点击更换" onclick="javascript:refresh(this);" src="<%=path%>/login/getValidateCode.do">
            </div>
            <div style="float: left;width: 25px;height: 25px;padding-top: 8px;" align="center" id='vVC'></div>
          </td>
        </tr>
        <tr style="height:70px; valign:top;">
          <td colspan="3" align="center" >
            <div tabindex="4" style="width:280px; background-image:url(img/loginb.png); padding-left:2px;">
              <img id="register" name="register"  src="img/login.png" onclick="loginF();" />
            </div>
          </td>
        </tr>
      </table>
    </form>
    <div align="right" style="width: 400px;margin-top: 50px;" ><span class="myspan" style="font-size: 12px;" onclick="tregister()">&nbsp;注册</span><span onclick="activeAgain()" style="font-size: 12px;">&nbsp;激活</span><span onclick="modPwd()" style="font-size: 12px;">&nbsp;忘记密码</span></div>
  </div>
</center>
<div id="maskPwd">密码
</div>
</body>
<script type="text/javascript">
//全局变量，用于判断input
var psV=false,lnV=false,vcV=false;
//未登录的忘记密码页面
function modPwd(){
  window.location.href="<%=path%>/login/backpwd/validateUser.jsp?modType=2";
}
//如果不是ie浏览器，从新初始化inputcsss
function setInputCss() {
	var browserType = getBrowserVersion();
  browserType = browserType.substring(0,browserType.lastIndexOf(' '));
  if(browserType!='msie'){
	  $('#loginName').css({"line-height":"35px", "height":"35px", "padding-top":"0px"});
	  $('#password').css({"line-height":"35px", "height":"35px", "padding-top":"0px"});
	  $('#checkCode').css({"line-height":"35px", "height":"35px", "padding-top":"0px"});
  }
}
function pwdMouseOver(){
	 $("#pwdSpan").toggleClass("addSelect");
}
$(function(){
	$("#maskPwd").css({"position":"absolute", "border":"solid red 1px","top":"100px", "left":"100px","color":"#ABCDEF"})
	.click(function(){
		$("#maskPwd").hide();
	});
	setInputCss();
  if($('#loginName').val()==$('#loginName')[0].defaultValue){
    $('#loginName').css('color','#ABCDEF');
  }
  if($('#password').val()==$('#password')[0].defaultValue){
    $('#password').css('color','#ABCDEF');
  }
  if($('#checkCode').val()==$('#checkCode')[0].defaultValue){
    $('#checkCode').css('color','#ABCDEF');
  }
});
//从新发送激活邮件到邮箱
function activeAgain(){
  var url="<%=path%>/login/activeAgain.do";
  var loginName = $("#loginName").val();
  if(loginName==null||loginName==""||loginName==$("#loginName")[0].defaultValue){
    $.messager.alert('提示信息',"您必须填写用户名，以便于向您的绑定邮箱发送验证!");
    return;
  }else{
    var pData={
      "loginName":$("#loginName").val()
    };
    $.ajax({type:"post", async:false, url:url, data:pData, dataType:"json",
      success: function(json) {
        if(json.success==true){
          $.messager.alert('提示信息',json.retInfo);
        }else{
          $.messager.alert('提示信息',json.retInfo);
        }
      }
    });
  }
}
//跳转到注册页面
function tregister(){
  window.location.href="<%=path%>/login/register.jsp";
}
//刷新验证码
function refresh(obj) {
  obj.src = "<%=path%>/login/getValidateCode.do?"+Math.random();
}
//验证验证码
function validateValidateCode(eleId){
	$('#vCImg').remove();
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null||ele.val()==ele[0].defaultValue){
    ele.val(ele[0].defaultValue);
    ele.css('color','#ABCDEF');
    vcV = false;
  }else{
    var vsMsg = verificationCheckCode(ele.val());
    if(vsMsg==true){
    	$('#vVC').append('<img id="vCImg" align="middle" src="img/accept.png">');
      vcV = true;
    }else{
      vcV = false;
    }
  }
}
function verificationCheckCode(val){
  var vfMsg =null;
    var pData={
      "checkCode":val
    };
    var url="<%=path%>/login/validateValidateCode.do";
    $.ajax({type:"post", async:false, url:url, data:pData, dataType:"json",
      success: function(json) {
        vfMsg = json;
      }
    });
    return vfMsg;
}
function validatePassword(eleId){
  $('#pWImg').remove();
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null||ele.val()==ele[0].defaultValue){
	  $('#pwdSpan').html('密码');
    psV = false;
  }else{
		$('#vPW').append('<img id="pWImg" align="middle" src="img/accept.png">'); 
    psV = true;
  }
}
function validateLoginName(eleId){
	$('#lNImg').remove();
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null||ele.val()==ele[0].defaultValue){
    ele.val(ele[0].defaultValue);
    ele.css('color','#ABCDEF');
    lnV = false;
  }else{
	  $('#vLN').append('<img id="lNImg" align="middle" src="img/accept.png">');
    lnV = true;
  }
}
function checkLoginName(val){
  var vfMsg =null;
  var pData={
    "loginName":val
  };
  var url="<%=path%>/login/validateLoginName.do";
  $.ajax({type:"post", async:false, url:url, data:pData, dataType:"json",
     success: function(json) {
       vfMsg = json;
     }
  });
  return vfMsg;
}
function onClick(obj){
  if(obj.value==obj.defaultValue){
    obj.value='';obj.style.color='#000';
  }
  if($(obj).attr('id')+''=='password'){
	  $('#pwdSpan').html('');
  }
}
function loginF() {
  if(psV&&lnV&&vcV){
    var url="<%=path%>/login.do";
    var pData={
      "loginName":$("#loginName").val(),
      "password":$("#password").val(),
      "checkCode":$("#checkCode").val(),
      "clientMacAddr":fooForm.txtMACAddr.value?(fooForm.txtMACAddr.value=="undefined"?"":fooForm.txtMACAddr.value):"",
       "clientIp":fooForm.txtIPAddr.value?(fooForm.txtIPAddr.value=="undefined"?"":fooForm.txtIPAddr.value):"",
       "browser":getBrowserVersion()
    };
    $.ajax({type:"post", async:false, url:url, data:pData, dataType:"json",
      success: function(json) {
        var loginInfo = json.data;
        var retInfo = loginInfo.retInfo;
        if(json.type==-1){
          $.messager.alert('登录信息',retInfo);
        }else if (json.type==1) {
          var activeType = loginInfo.activeType;
          if(activeType==1){
            $.messager.alert('登录信息',retInfo);
          }else if(activeType==2){
            $.messager.alert('登录信息',retInfo,"info",function(){
              window.location.href="<%=path%>/asIndexTemp.jsp";
            });
          }
          return;
        } else if (json.type==2) {
          $.messager.alert("登录信息", "登录失败："+json.data, "error");
        } else {
          $.messager.alert("登录信息", "登录异常："+json.data, "error");
        }
      },
      error: function(errorData) {
        if (errorData) {
          $.messager.alert("登录信息", "登录异常：未知！", "error");
        } else {
          $("#mask").hide();
          setBodyEnter(true);
        }
      }
    });
  }else{
	  if(lnV==false)$('#vLN').append('<img id="lNImg" align="middle" src="img/cross.png">');
    if(psV==false)$('#vPW').append('<img id="pWImg" align="middle" src="img/cross.png">'); 
    if(vcV==false)$('#vVC').append('<img id="vCImg" align="middle" src="img/cross.png">'); 
    $.messager.alert("登录信息","您的登录信息某些地方有误，请完善您的注册信息");
    return ;
  }
}
</script>
</html>
