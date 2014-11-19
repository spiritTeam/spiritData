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
      <table width="430px;" >
        <tr><td colspan="3"><div style="height: 30px;text-align: left;margin-left: 35px;" id="checkResult"></div></td></tr>
        <tr style="height:50px; valign:top;">
          <td align="right"><span class="myspan">账号&nbsp;&nbsp;</span></td>
          <td colspan="2" rowspan="1" width="280px;" style="text-align:left;">
	        <input id="loginName" name="loginName"  tabindex="1" type="text"  value="账号/QQ/手机号" onmouseover=this.focus();this.select();
	              onclick="onClick(loginName);" onBlur="validateLoginName('loginName');"/>
					</td>
				</tr>
        <tr style="height:50px; valign:top;">
          <td align="right"><span class="myspan">密码&nbsp;&nbsp;</span></td>
          <td colspan="2" style="text-align:left;">
            <input style="width:280px;" id="password" name="password"  tabindex="2" type="password" value="" onmouseover=this.focus();this.select();
                onclick="onClick(password);" onBlur="validatePassword('password');" />
          </td>
        </tr>
        <tr style="height:50px; valign:top;">
          <td align="right"><span class="myspan">验证码&nbsp;&nbsp;</span></td>
          <td width="180px;"><input style="width:195px;" id="checkCode" name="checkCode"  tabindex="3" type="text" value="验证码" onmouseover=this.focus();this.select();
                onclick="onClick(checkCode);" onBlur="validateValidateCode('checkCode');" /></td>
          <td align="left">
            <div style="border: 1px solid  #999999;width: 80px;"><img title="点击更换" onclick="javascript:refresh(this);" src="<%=path%>/login/getValidateCode.do"></div>
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
</body>
<script type="text/javascript">
function modPwd(){
	window.location.href="<%=path%>/login/modPwd.jsp?modType=2";
}
$(function(){
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
var psV=false,lnV=false,vcV=false;
function activeAgain(){
  var url="<%=path%>/login/activeAgain.do";
  var loginName = $("#loginName").val();
  if(loginName==null||loginName==""){
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
function checkStr(str){
  var re = /^[a-zA-z]\w{4,11}$/;
  if(re.test(str)){
    return true;
  }else{
    return false;
  }       
}
function tregister(){
  window.location.href="<%=path%>/login/register.jsp";
}
function refresh(obj) {
  obj.src = "<%=path%>/login/getValidateCode.do?"+Math.random();
}
function validateValidateCode(eleId){
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null||ele.val()==ele[0].defaultValue){
    ele.val(ele[0].defaultValue);
    ele.css('color','#ABCDEF');
    vcV = false;
  }else{
    var vsMsg = verificationCheckCode(ele.val());
    if(vsMsg==true){
      $('#checkResult').html('<div style="width:370px;font-size: 12px;color:green;">&nbsp;&nbsp;&nbsp;&nbsp;<img src="img/accept.png">验证码正确!</div>');
      vcV = true;
    }else{
      $('#checkResult').html('<div style="font-size: 12px;color:red;">&nbsp;&nbsp;&nbsp;&nbsp;<img src="img/cross.png">验证码错误!</div>');
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
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null||ele.val()==ele[0].defaultValue){
    ele.val(ele[0].defaultValue);
    ele.css('color','#ABCDEF');
    psV = false;
  }else{
    psV = true;
  }
}
function validateLoginName(eleId){
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null||ele.val()==ele[0].defaultValue){
    ele.val(ele[0].defaultValue);
    ele.css('color','#ABCDEF');
    lnV = false;
  }else{
    $('#checkResult').html('<img src="img/accept.png">');
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
	    	if(json.type==-1){
	    		var loginInfo = json.data;
	    		var retInfo = loginInfo.retInfo;
	    		$.messager.alert('登录信息',retInfo);
	    	}else if (json.type==1) {
	    		$.messager.alert('登录信息',json.data.retInfo,"info",function(){
   	          window.location.href="<%=path%>/asIndexTemp.jsp";
           });
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
    $.messager.alert("登录信息","您的登录信息某些地方有误，请完善您的注册信息");
    return ;
  }
}
</script>
</html>
