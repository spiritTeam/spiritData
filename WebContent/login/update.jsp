<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
  String path = request.getContextPath();
  String sid = request.getSession().getId();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>修改</title>
<jsp:include page="/common/sysInclude.jsp" flush="true"/>
<link rel="stylesheet" type="text/css" href="<%=path%>/login/css/login.css">
<script type="text/javascript" src="<%=path %>/login/js/login.js"></script>
</head>
<body>
<!-- 遮罩层 -->
<div id="mask" style="display:none; position:absolute;vertical-align:middle;text-align:center; align:center;">
  <img id="waittingImg" align="middle" src="<%=path%>/resources/images/waiting_circle.gif"/><br/><br/>
  <span id="waittingText" style="font-weight:bold;" id="maskTitle">请稍候，数据提交中...</span>
</div>
<center>
  <div id="mainDiv" style="width:330px;height:400px;">
    <div id="rstDiv" style="text-align:left;margin-left:80px;height:20px;padding-top:5px;margin-top: 10px;"><span id="checkResult"></span></div>
    <form>
      <table width="300px;" style="margin-right:-40px;">
        <tr style="height:35px; valign:top;">
          <td align="right" width="56px;"><span class="loginspan">账　号</span></td>
          <td colspan="2" width="200px;">
            <div style="float:left">
              <input id="loginName" name="loginName" tabindex="1" type="text" onclick="onClick(loginName);" onBlur="" value="用户账号" disabled="disabled"/></div>
            <div style="float:left;width:20px;height:25px;padding-top:8px;margin-left:-2px;" align="center" id='vLN'></div>
          </td>
        </tr>
        <tr><td><div style="height:8px;width:5px;"></div></td></tr>
        <tr style="height:50px; valign:top;">
          <td align="right"><span class="loginspan">邮　箱</span></td>
          <td colspan="2" width="130px;">
            <div style="float:left">
              <input id="mail" name="mail" tabindex="2" type="text" onclick="onClick(mail);" value="您的邮箱" disabled="disabled" style="width:195px;"/></div>
            <div style="float:left;width:20px;height:25px;padding-top:8px;margin-left:-2px;" align="center" id='vMail'></div>
          </td>
        </tr>
        <tr style="height:50px; valign:top;">
          <td align="right"><span class="loginspan">新密码</span></td>
          <td colspan="2" rowspan="1">
            <div style="float:left">
              <input id="password" name="password" tabindex="3" type="password" onmouseover="pwdMouseOver();"
                onclick="onClick(password);" onBlur="validatePassword('password');"/></div>
            <div style="float:left;width:20px;height:25px;padding-top:8px;margin-left:-2px;" align="center" id='vPwd'></div>
            <div id="pwDiv" style="float:left;width:39px;height:25px;padding-top:10px;margin-left:-220px;" align="center">
              <span id="pwdSpan" style="color:#ABCDEF;font-size:12px;">新密码</span></div>
          </td>
        </tr>
        <tr style="height:50px; valign:top;">
          <td align="right"><span class="loginspan" style="font-size:12px;margin-right:-5px;">确认密码</span></td>
          <td colspan="2">
            <div style="float:left">
              <input id="confirmPassword" name="confirmPassword" tabindex="4" type="password" onmouseover="cpwdMouseOver();"
                onclick="onClick(confirmPassword);" onBlur="validateConfirmPassword('confirmPassword');"/></div>
            <div style="float:left;width:20px;height:25px;padding-top:8px;margin-left:-2px;" align="center" id='vCPwd'></div>
            <div id="cpwDiv" style="float:left;width:50px;height:25px;padding-top:10px;margin-left:-220px;" align="center">
              <span id="cpwdSpan" style="color:#ABCDEF;font-size:12px;">确认密码</span></div>
          </td>
        </tr>
        <tr>
          <td colspan="3" align="left" style="height:50px;padding-top:10px;" valign="top">
            <div style="width:5px;height:5px;"></div>
            <a id="register" name="register" onclick="updateUser();" href="#"><div tabindex="6" id="commitButton" style="background-image:url(images/bg.png);">
              <img src="images/register.png"/>
            </div></a>
          </td>
        </tr>
      </table>
    </form>
  </div>
</center>
</body>
<script type="text/javascript">
//各个input的变量，true代表有值，且符合标准，false则相反
var psV=false,cpsV=false,maV=false;

//以下方法为重复密码和密码的鼠标点击效果
function pwdMouseOver(){
  $("#pwdSpan").toggleClass("addSelect");
}
function cpwdMouseOver(){
  $("#cpwdSpan").toggleClass("addSelect");
}
function pwdOnActive() {
  //隐藏
  $("#pwdSpan").hide();
  //获得焦点和选择
  $("#password")[0].focus();
  $("#password")[0].select();
}
function cpwdOnActive() {
  //confirmPassword
  $("#cpwdSpan").hide();
  //获得焦点和选择
  $("#confirmPassword")[0].focus();
  $("#confirmPassword")[0].select();
}
//以上方法为重复密码和密码的鼠标点击效果

//更新用户
function updateUser(){
  $('#register').attr("disabled",true); 
  if(psV&&cpsV&&lnV&&maV&&vcV){
    var pData={
      "loginName":$("#loginName").val(),
      "password":$("#password").val(),
      "userName":$("#userName").val(),
      "mailAdress":$("#mail").val()
    };
    $("#mask").show();
    var url="<%=path%>/login/update.do";
    $.ajax({type:"post",async:false,url:url,data:pData,dataType:"json",
      success:function(json) {
        $("#mask").hide();
        if(json.success){
          var mainPage = getMainPage();
          if(mainPage){
            var winId = getWinId(mainPage);
            closeSWinInMain(winId);
            mainPage.$.messager.alert('注册提示',json.retInfo,'info');
            cleanWinId(mainPage);
          }else{
            $.messager.alert('注册提示',json.retInfo,'info');
            window.location.href = "<%=path%>/asIndex.jsp";
          }
          $('#register').attr("disabled",false); 
        }else{
          $.messager.alert('提示',json.retInfo,'info');
          $('#register').attr("disabled",false);
        }
      }
    });
  }else{
    $('#register').attr("disabled",false); 
    if(cpsV==false){
      $('#cpwdImg').remove();
      $('#vCPwd').append('<img id="cpwdImg" align="middle" src="images/cross.png">');
    }
    if(maV==false){
      $('#mailImg').remove();
      $('#vMail').append('<img id="mailImg" align="middle" src="images/cross.png">');
    }
    if(psV==false){
      $('#pwdImg').remove();
      $('#vPwd').append('<img id="pwdImg" align="middle" src="images/cross.png">');
    } 
    $.messager.alert('注册提示',"您的注册信息某些地方有误，请完善您的注册信息",'info',function () {
      if(psV==false){
        $('#password')[0].focus();
        $('#password')[0].select();
      }else if(cpsV=false){
        $('#confirmPassword')[0].focus();
        $('#confirmPassword')[0].select();
      }
    });
  }
}
//如果不是ie浏览器，从新初始化inputcsss
function setInputCss(){
  //遮罩层位置及样式
  $("#mask").css({
    "padding-top": ($(window).height()-95)/3,//设置图片位置
    "top": parseInt($("#mainDiv").css("top"))-10,
    "left": parseInt($("#mainDiv").css("left"))-10,
    "width": (parseInt($("#mainDiv").css("width"))+20)+"px",
    "height": (parseInt($("#mainDiv").css("height"))+40)+"px"
  });
  var browserType = getBrowserVersion();
  var v = browserType.substring(0,browserType.lastIndexOf(' '));
  $('#pwDiv').css({"padding-top":"11px","margin-left":"-217px"});
  $('#cpwDiv').css({"padding-top":"11px","margin-left":"-217px"});
  $("div.intro span").css({'border-color':'#999999','border-left':'0px'});
  if(v!='msie'){
    if($('#loginName')!=null) $('#loginName').css({"line-height":"35px","height":"35px","padding-top":"0px"});
    if($('#password')!=null) $('#password').css({"line-height":"35px","height":"35px","padding-top":"0px"});
    if($('#mail')!=null) $('#mail').css({"line-height":"35px","height":"35px","padding-top":"0px"});
    if($('#confirmPassword')!=null) $('#confirmPassword').css({"line-height":"35px","height":"35px","padding-top":"0px"});
    if($('#commitButton')!=null) $('#commitButton').css({"width":"210px","padding-left":"38px","margin-left":"9px"});
    if($('#rstDiv')!=null) $('#rstDiv').css({"margin-left":"76px;"});
  }else{
    if($('#loginName')!=null) $('#loginName').css({"line-height":"35px", "height":"35px", "padding-top":"0px"});
    if($('#password')!=null) $('#password').css({"line-height":"35px", "height":"35px", "padding-top":"0px"});
    if($('#mail')!=null) $('#mail').css({"line-height":"35px","height":"35px","padding-top":"0px"});
    if($('#confirmPassword')!=null) $('#confirmPassword').css({"line-height":"35px","height":"35px","padding-top":"0px"});
    if($('#commitButton')!=null) $('#commitButton').css({"width":"210px","padding-left":"38px","margin-left":"12px"});
    if($('#rstDiv')!=null) $('#rstDiv').css({"margin-left":"70px"});
  }
}
$(function(){
  $("#pwdSpan").mouseover(function(){pwdOnActive();});
  $("#password").focus(function(){pwdOnActive();});
  $("#password").mouseover(function(){pwdOnActive();});
  $("#password").blur(function(){
    if ($(this).val()=="") $("#pwdSpan").show();
  });
  $("#cpwdSpan").mouseover(function(){cpwdOnActive();});
  $("#confirmPassword").focus(function(){cpwdOnActive();});
  $("#confirmPassword").mouseover(function(){cpwdOnActive();});
  $("#confirmPassword").blur(function(){
    if ($(this).val()=="") $("#cpwdSpan").show();
  });
  if($('#mail').val()==$('#mail')[0].defaultValue){
    $('#mail').css('color','#ABCDEF');
  }
  if($('#loginName').val()==$('#loginName')[0].defaultValue){
    $('#loginName').css('color','#ABCDEF');
  }
  if($('#password').val()==$('#password')[0].defaultValue){
    $('#password').css('color','#ABCDEF');
  }
  setInputCss();
});
function refresh(obj) {
  obj.src = "<%=path%>/login/getValidateCode.do?"+Math.random();
  $('#checkCode').val('');
}
function validatePassword(eleId){
  $('#checkResult').html("");
  $('#pwdImg').remove();
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null||ele.val()==ele[0].defaultValue){
    psV = false;
  }else{
    if(!checkPasswordStr(ele.val())){
      $('#checkResult').html('<div style="width:370;font-size:12px;color:red;">&nbsp;&nbsp;&nbsp;&nbsp;密码应是5~12位的字母、数字、下划线!</div>');
      $('#vPwd').append('<img id="pwdImg" align="middle" src="images/cross.png">');
      psV = false;
    }else{
      $('#vPwd').append('<img id="pwdImg" align="middle" src="images/accept.png">');
      psV = true;
    }
  }
}
function validateConfirmPassword(eleId){
  $("#cpwdImg").remove();
  $('#checkResult').html("");
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null||ele.val()==ele[0].defaultValue){
    cpsV =false;
  }else{
    if($('#password').val()!=ele.val()){
      $('#vCPwd').append('<img id="cpwdImg" src="images/cross.png">');
      $('#checkResult').html('<div style="width:370;font-size:12px;color:red;">&nbsp;&nbsp;&nbsp;&nbsp;密码不一致!</div>');
      cpsV =false;
    }else{
      $('#vCPwd').append('<img id="cpwdImg" src="images/accept.png">');
      cpsV =true;
    }
  }
}
function checkPasswordStr(str){
  var re = /[0-9a-zA-z]\w{4,11}$/;
  if(re.test(str)){
    return true;
  }else{
    return false;
  }       
}
function onClick(obj){
  if(obj.value==obj.defaultValue){
    obj.value='';obj.style.color='#000';
  }
}
</script>
</html>
