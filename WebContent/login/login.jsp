<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
  String path = request.getContextPath();
  String sid = request.getSession().getId();
  String uT = request.getParameter("uT");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>登录</title>
<jsp:include page="/common/sysInclude.jsp" flush="true"/>
<link rel="stylesheet" type="text/css" href="<%=path%>/login/css/login.css" />
<script type="text/javascript" src="<%=path %>/login/js/login.js"></script>
<style type="text/css">
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
//var service = locator.ConnectServer();
var MACAddr;
var IPAddr;
var DomainAddr;
var sDNSName;
//uT=1,表示激活成功过来的账号，uT=2表示修改密码成功后跳转的，ut=3表示正常的跳转的
//uTMessage表示要提示的信息
var uT=<%=uT%>,uTMessage = "";
if(uT==1){
  uTMessage = "激活成功!";
  $.messager.alert('提示',uTMessage,'info');
}else if(uT==2) {
  uTMessage = "修改密码成功!";
  $.messager.alert('提示',uTMessage,'info');
}

//service.Security_.ImpersonationLevel=3;
//service.InstancesOfAsync(foo, 'Win32_NetworkAdapterConfiguration');
</script>

<script language="JScript" event="OnCompleted(hResult,pErrorObject, pAsyncContext)" for="foo">
fooForm.txtMACAddr.value=unescape(MACAddr);
fooForm.txtIPAddr.value=unescape(IPAddr);
fooForm.txtDNSName.value=unescape(sDNSName);
</script>
<script language="JScript" event="OnObjectReady(objObject,objAsyncContext)" for="foo">
if(objObject.IPEnabled != null && objObject.IPEnabled != "undefined" && objObject.IPEnabled == true ){
  if(objObject.MACAddress != null && objObject.MACAddress != "undefined" )MACAddr = objObject.MACAddress;
  if(objObject.IPEnabled && objObject.IPAddress(0 )!= null && objObject.IPAddress(0 )!= "undefined" )IPAddr = objObject.IPAddress(0);
  if(objObject.DNSHostName != null && objObject.DNSHostName != "undefined" )sDNSName = objObject.DNSHostName;
}
</script>
<!-- 遮罩层 -->
<div id="mask" style="border:1px;display:none; position:absolute;vertical-align:middle;text-align:center; align:center;">
  <img id="waittingImg" align="middle" src="<%=path%>/resources/images/waiting_circle.gif"/><br/><br/>
  <span id="waittingText" style="font-weight:bold;" id="maskTitle">请稍候，登录中...</span>
</div>

<center>
  <div id="mainDiv" style="width:330px;height:400px;">
  <form>
    <table width="300px;" style="margin-right:-5px;margin-top:30px;">
      <tr style="height:50px; valign:top;">
        <td align="right" width="56px;"><span class="loginspan">账　号</span></td>
        <td colspan="2" rowspan="1"  style="text-align:left;">
          <div style="float:left;">
            <input id="loginName" name="loginName" tabindex="1" type="text"  value="账号/QQ/手机号" onmouseover=this.focus();this.select();
              onclick="changeLoginNameCss(loginName);" onBlur="validateLoginName('loginName');"/></div>
        </td>
      </tr>
      <tr style="height:50px;valign:top;">
        <td align="right" style="padding-top:15px;"><span class="loginspan">密　码</span></td>
        <td colspan="2" style="text-align:left;padding-top:15px;" id="pwTd">
          <div style="float:left;">
            <input id="password" name="password" tabindex="2" type="password" onmouseover="pwdMouseOver();"
             onclick="changePasswordCss(password);" onBlur="validatePassword('password');"/></div>
          <div id="pwDiv" style="float:left;width:25px;height:25px;padding-top:12spx;margin-left:-219px;" align="center">
            <span id="pwdSpan" style="color:#ABCDEF;font-size:12px;">密码</span></div>
        </td>
      </tr>
      <tr style="height:50px; valign:top;">
        <td align="right" style="padding-top: 22px;"><span class="loginspan">验证码</span></td>
        <td colspan="2" style="padding-top: 22px;">
          <div id="ccBorder" style="float:left;border:1px solid #ABADB3;margin-left:3px;">
            <input id="checkCode" style="float:left;border:0px;margin-left:-1px;border-left:1px solid #ABADB3;" type="text" name="checkCode" tabindex="3" value="验证码" onmouseover=this.focus();this.select();
            onclick="changeCheckCodeCss(checkCode);" onBlur="validateValidateCode('checkCode');"/>
            <div style="float:left;width:20px;height:25px;padding-top:8px;padding-left:2px;margin-left:-2px;border-top:none" align="center" id='vVC'></div>
          </div>
          <!-- 验证码验证提示图片位置 -->
          <div id="checkCodeDiv" style="float:left;border:1px solid #999999;width:83px;margin-left:-3px;">
            <img style="height:35px;" title="点击更换" id="vcimg" onclick="javascript:refresh(this);" src="<%=path%>/login/getValidateCode.do">
          </div>
        </td>
      </tr>
      <tr style="height:70px; valign:top;">
        <td colspan="3" align="center" style="padding-top:37px;">
          <a id="login" name="login" onclick="loginF();" >
            <div id="loginButton" tabindex="4" style="background-image:url(images/bg.png);border-radius:5px;">
              <span style="font-size:16px;color:#FFFFFF;font-weight: bold;">登　录</span>
            </div>
          </a>
        </td>
      </tr>
    </table>
  </form>
  <div align="right" style="width:310px;margin-top:80px;margin-right:10px;">
    <a onclick="toRegister()" href="#">注册</a>&nbsp;|&nbsp;
    <a onclick="activeUserAgain()" href="#">激活</a>&nbsp;|&nbsp;
    <a onclick="modifyPassword()" href="#">忘记密码?</a>
  </div>
  </div>
</center>
</body>
<script type="text/javascript">
//全局变量，用于判断input
var psV=false,lnV=false,vcV=false;
var mainPage = getMainPage(); 
var winId = mainPage.loginWinId;
var win = getSWinInMain(winId);
//未登录的忘记密码页面
function modifyPassword(){
  win.modify({title:"修改密码"});
  window.location.href="<%=path%>/login/forgetPassword.jsp?modType=2";
}
/**
 * 在点击的时候把颜色变成黑色
 */
function changeLoginNameCss(obj){
  if(obj.value==obj.defaultValue){
    obj.value='';obj.style.color='#000';
  }
  if($(obj).attr('id')+''=='password'){
    $('#pwdSpan').html('');
  }
}
function changePasswordCss(obj){
  if(obj.value==obj.defaultValue){
    obj.value='';obj.style.color='#000';
  }
  $('#pwdSpan').html('');
}
function changeCheckCodeCss(obj){
  if(obj.value==obj.defaultValue){
    obj.value='';obj.style.color='#000';
  }
}
function pwdOnActive(){
  //隐藏
  $("#pwdSpan").hide();
  //获得焦点和选择
  $("#password")[0].focus();
  $("#password")[0].select();
}
function pwdMouseOver(){
  $("#pwdSpan").toggleClass("addSelect");
}
$(function(){
  $("#checkCode").css('background-color','#FFFFFF');
  $("#pwdSpan").mouseover(function(){pwdOnActive();});
  $("#password").focus(function(){pwdOnActive();});
  $("#password").mouseover(function(){pwdOnActive();});
  $("#password").blur(function(){
    if ($(this).val()=="" ) $("#pwdSpan").show();
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
  $('#checkCode').mouseover(function(){
    $('#checkCode').css("border-color","#ABCDEF");
    $('#ccBorder').css("border-color","#ABCDEF");
  });
  $('#checkCode').mouseout(function(){
    $('#checkCode').css("border-color","#ABADB3");
    $('#ccBorder').css("border-color","#ABADB3");
  });
});
function setInputCss(){
  //遮罩层位置及样式
  $("#mask").css({
    "padding-top": ($(window).height()-95)/3,
    "top": parseInt($("#mainDiv").css("top"))-10,
    "left": parseInt($("#mainDiv").css("left"))-10,
    "width": $(window).width(),
    "height": $(window).height()
  });
  var browserType = getBrowserVersion();
  var v = browserType.substring(0,browserType.lastIndexOf(' '));
  if(v=='msie'){
    if($('#loginName')!=null )$('#loginName').css({"line-height":"35px", "height":"35px", "padding-top":"0px"});
    if($('#password')!=null )$('#password').css({"line-height":"35px", "height":"35px", "padding-top":"0px"});
    if($('#checkCode')!=null )$('#checkCode').css({"line-height":"35px", "height":"35px", "padding-top":"0px","width":"120px"});
    if($('#pwDiv')!=null )$('#pwDiv').css({"padding-top":"10px","margin-left":"-230px"});
    if($('#loginButton')!=null )$('#loginButton').css({"width":"278px","height":"28px","padding-top":"10px","padding-left":"0px","margin-left":"-6px"});
  }else if(v=='chrome'){
    if($('#loginName')!=null )$('#loginName').css({"line-height":"35px", "height":"32px", "padding-top":"0px"});
    if($('#password')!=null )$('#password').css({"line-height":"35px", "height":"32px", "padding-top":"0px"});
    if($('#checkCode')!=null )$('#checkCode').css({"line-height":"35px", "height":"35px", "padding-top":"0px","width":"122px"});
    if($('#pwDiv')!=null )$('#pwDiv').css({"padding-top":"10px","margin-left":"-230px"});
    if($('#loginButton')!=null )$('#loginButton').css({"width":"280px","height":"28px","padding-top":"10px","padding-left":"0px","margin-left":"-3px"});
  }else {
    $('#pwDiv').css({"padding-top":"11px","margin-left":"-217px"});
    //var ieVersion = browserType.substring(browserType.lastIndexOf(' '),browserType.length);
    //if(ieVersion==11.0){
      if($('#loginName')!=null )$('#loginName').css({"line-height":"35px", "height":"35px", "padding-top":"0px"});
      if($('#password')!=null )$('#password').css({"line-height":"35px", "height":"35px", "padding-top":"0px"});
      if($('#checkCode')!=null )$('#checkCode').css({"line-height":"35px", "height":"35px", "padding-top":"0px","width":"120px"});
      if($('#loginButton')!=null )$('#loginButton').css({"width":"278px","height":"28px","padding-top":"10px","padding-left":"0px","margin-left":"-6px"});
      if($('#pwDiv')!=null )$('#pwDiv').css({"padding-top":"10px","margin-left":"-230px"});
    //}
  }
}
//从新发送激活邮件到邮箱
function activeUserAgain(){
  var url="<%=path%>/login/activeUserAgain.do";
  var loginName = $("#loginName").val();
  if(loginName==null||loginName==""||loginName==$("#loginName")[0].defaultValue){
    $.messager.alert('提示信息',"您必须填写用户名，以便于向您的绑定邮箱发送验证!");
    return;
  }else{
    var pData={"loginName":$("#loginName").val()};
    $.ajax({type:"post", async:false, url:url, data:pData, dataType:"json",
      success:function(json ){
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
function toRegister(){
  var winId = getWinId(getMainPage());
  var win = getSWinInMain(winId);
  win.modify({title:"注册"});
  window.location.href="<%=path%>/login/register.jsp?pWinId="+winId;
}
//刷新验证码
function refresh(obj ){
  $('#checkCode').val('');
  obj.src = "<%=path%>/login/getValidateCode.do?"+Math.random();
}
//验证验证码
function validateValidateCode(eleId){
  $('#vCImg').remove();
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null||ele.val()==ele[0].defaultValue){
    ele.val(ele[0].defaultValue);
    ele.css('color','#ABCDEF');
    ele.css('border-color','#ABADB3');
    vcV = false;
  }else{
    var vsMsg = verificationCheckCode(ele.val());
    if(vsMsg==true){
      $('#vVC').append('<img id="vCImg" align="middle" src="images/accept.png">');
      vcV = true;
    }else{
      $('#vVC').append('<img id="vCImg" align="middle" src="images/cross.png">');
      vcV = false;
    }
  }
}
function verificationCheckCode(val){
  var vfMsg =null;
  var pData={"checkCode":val};
  var url="<%=path%>/login/validateValidateCode.do";
  $.ajax({type:"post", async:false, url:url, data:pData, dataType:"json",
    success:function(json ){
      vfMsg = json;
    }
  });
  return vfMsg;
}
//验证密码是否为空
function validatePassword(eleId){
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null||ele.val()==ele[0].defaultValue){
    $('#pwdSpan').html('密码');
    psV = false;
  }else psV = true;
}
// 验证loginName是否为空
function validateLoginName(eleId){
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null||ele.val()==ele[0].defaultValue){
    ele.val(ele[0].defaultValue);
    ele.css('color','#ABCDEF');
    lnV = false;
  }else lnV = true;
}
function loginF(){
  $('#login').attr('disabled',true);
  if(psV&&lnV&&vcV){
    $("#mask").show();
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
      success:function(json ){
        $("#mask").hide();
        $('#register').attr('disabled',false);
        $('#checkCode').val('');
        $('#vcimg')[0].src = "<%=path%>/login/getValidateCode.do?"+Math.random();
        var loginInfo = json.data;
        var retInfo = loginInfo.retInfo;
        if(json.type==-1){
          $.messager.alert('登录信息',retInfo,'info');
        }else if (json.type==1){
          var activeType = loginInfo.activeType;
          if(activeType==1){
            $.messager.alert('登录信息',retInfo,'info');
          }else if(activeType==2){
            if(mainPage) {
              var loginStatus = mainPage.document.getElementById("loginStatus");
              var loginName = mainPage.document.getElementById("loginName");
              $(loginStatus).val(1);
              $(loginName).val(pData.loginName);
              closeSWinInMain(winId);
              mainPage.$.messager.alert("登陆信息","登陆成功！",'info');
              cleanWinId();
            }else{
              $.messager.alert("登陆信息","登陆成功！",'info');
              window.location.href = "<%=path%>/asIndex.jsp";
            }
          }
        } else if(json.type==2 ){
          mainPage.$.messager.alert("登录信息", "登录失败："+json.data, "error");
        } else {
          mainPage.$.messager.alert("登录信息", "登录异常："+json.data, "error");
        }
      },
      error:function(errorData ){
        $('#register').attr('disabled',false);
        $('#checkCode').val('');
        $('#vcimg')[0].src = "<%=path%>/login/getValidateCode.do?"+Math.random();
        if (errorData ){
          mainPage.$.messager.alert("登录信息", "登录异常：未知！", "error");
        } else {
          mainPage.$("#mask").hide();
        }
      }
    });
  }else{
    $('#register').attr('disabled',false);
    if(lnV==false ){
      $('#lNImg').remove();
      $('#vLN').append('<img id="lNImg" align="middle" src="images/cross.png">');
    }
    if(psV==false){
      $('#pWImg').remove();
      $('#vPW').append('<img id="pWImg" align="middle" src="images/cross.png">');
    } 
    if(vcV==false){
      $('#vCImg').remove();
      $('#vVC').append('<img id="vCImg" align="middle" src="images/cross.png">');
    }
    mainPage.$.messager.alert("登录提示","您的登录信息某些地方有误，请完善您的登陆信息", 'info',function (){
      if(lnV==false){
        $('#loginName')[0].focus();
        $('#loginName')[0].select();
      }else if(psV==false){
        $('#password')[0].focus();
        $('#password')[0].select();
      }else{
        $('#checkCode')[0].focus();
        $('#checkCode')[0].select();
      }
    });
  }
}
</script>
</html>
