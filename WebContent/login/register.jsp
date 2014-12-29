<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
  String path = request.getContextPath();
  String sid = request.getSession().getId();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>注册</title>
<jsp:include page="/common/sysInclude.jsp" flush="true"/>
<link rel="stylesheet" type="text/css" href="<%=path%>/login/css/login.css">
</head>
<body>
<center>
  <!-- 遮罩层 -->
  <div id="mask" style="display:none; position:absolute;vertical-align:middle;text-align:center; align:center;">
    <img align="middle" src="<%=path%>/resources/images/waiting_circle.gif"/><br/><br/>
    <span style="font-weight:bold;" id="maskTitle">请稍候，注册中...</span>
  </div>
  <div id="mainDiv" style="width:330px;height:400px;">
    <div style="margin-top:15px; margin-left:15px;"align="left"><span style="font-size:16px;color:#999999;">注册</span></div>
    <div style="height:1px; width:300px;border-top:1px solid  #999999;"></div>
    <div id="rstDiv" style="text-align:left;margin-left:80px;height:20px;padding-top:5px;"><span id="checkResult"></span></div>
    <form>
      <table width="300px;" style="margin-right:-15px;">
        <tr style="height:35px; valign:top;">
          <td align="right" width="56px;"><span class="loginspan">账　号</span></td>
          <td colspan="2" width="200px;">
            <div style="float:left">
              <input id="loginName" name="loginName" tabindex="1" type="text" onmouseover=this.focus();this.select();
                onclick="onClick(loginName);" onBlur="validateLoginName('loginName');" value="用户账号"/></div>
            <div style="float:left;width:20px;height:25px;padding-top:8px;margin-left:-2px;" align="center" id='vLN'></div>
          </td>
        </tr>
        <tr><td><div style="height:8px;width:5px;"></div></td></tr>
        <tr style="height:50px; valign:top;">
          <td align="right"><span class="loginspan">邮　箱</span></td>
          <td colspan="2" width="130px;">
            <div style="float:left">
              <input id="mail" name="mail" tabindex="2" type="text" onmouseover=this.focus();this.select(); 
                onclick="onClick(mail);" onBlur="validateMail('mail');" value="您的邮箱"/></div>
            <div class="intro" style="float:left;margin-left:-3px;">
              <input id="mailEndStr" name="mailEndStr"/></div>
            <div style="float:left;width:20px;height:25px;padding-top:8px;margin-left:-2px;" align="center" id='vMail'></div>
          </td>
        </tr>
        <tr style="height:50px; valign:top;">
          <td align="right"><span class="loginspan">密　码</span></td>
          <td colspan="2" rowspan="1">
            <div style="float:left">
              <input id="password" name="password" tabindex="3" type="password" onmouseover="pwdMouseOver();"
                onclick="onClick(password);" onBlur="validatePassword('password');"/></div>
            <div style="float:left;width:20px;height:25px;padding-top:8px;margin-left:-2px;" align="center" id='vPwd'></div>
            <div id="pwDiv" style="float:left;width:25px;height:25px;padding-top:10px;margin-left:-220px;" align="center">
              <span id="pwdSpan" style="color:#ABCDEF;font-size:12px;">密码</span></div>
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
        <tr style="height:50px; valign:top;">
          <td align="right"><span class="loginspan">验证码</span></td>
          <td colspan="2">
            <div style="float:left">
              <input id="checkCode" name="checkCode" tabindex="5" type="text" value="请输入验证码" onmouseover=this.focus();this.select(); onclick="onClick(checkCode);" onBlur="validateValidateCode('checkCode');"/>
            </div>
            <div style="float:left;border:1px solid #999999;width:83px;margin-left:-3px;border-left:0px;"><img style="height:35px;" id="vcimg" title="点击更换" onclick="javascript:refresh(this);" src="<%=path%>/login/getValidateCode.do"></div>
            <div style="float:left;width:20px;height:25px;padding-top:8px;margin-left:-2px;" align="center" id='vVC'></div>
          </td>
        </tr>
        <tr>
          <td colspan="3" align="left" style="height:50px;padding-top:10px;" valign="top">
            <div style="width:5px;height:5px;"></div>
            <div tabindex="6" id="commitButton" style="background-image:url(images/registerb.png);">
              <a id="register" name="register" onclick="saveRegister();" href="#"><img src="images/register.png"/></a>
            </div>
          </td>
        </tr>
      </table>
    </form>
  </div>
</center>
</body>
<script type="text/javascript">
var psV=false,cpsV=false,lnV=false,maV=false,vcV=false;
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
function saveRegister(){
  $('#register').attr("disabled",true); 
  $('#vcimg')[0].src = "<%=path%>/login/getValidateCode.do?"+Math.random();
  $('#checkCode').val('');
  if(psV&&cpsV&&lnV&&maV&&vcV){
    var pData={
      "loginName":$("#loginName").val(),
      "password":$("#password").val(),
      "userName":$("#userName").val(),
      "mailAdress":$("#mail").val()+$('#mailEndStr').combobox('getText'),
    };
    $("#mask").show();
    var url="<%=path%>/login/register.do";
    $.ajax({type:"post",async:false,url:url,data:pData,dataType:"json",
      success:function(json) {
        $("#mask").hide();
        if(json.success){
          var mainPage = getMainPage();
          if(mainPage){
            if(mainPage.registerWinId!=null&&mainPage.registerWinId!="") closeSWinInMain(mainPage.registerWinId);
            if(mainPage.modifyWinId!=null&&mainPage.modifyWinId!="") closeSWinInMain(mainPage.modifyWinId);
            if(mainPage.loginWinId!=null&&mainPage.loginWinId!="") closeSWinInMain(mainPage.loginWinId);
            mainPage.$.messager.alert('注册提示',json.retInfo,'info');
            mainPage.registerWinId = "";
            mainPage.modifyWinId = "";
            mainPage.loginWinId = "";
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
    if(lnV==false) {
      $('#lnImg').remove();
      $('#vLN').append('<img id="lnImg" align="middle" src="images/cross.png">');
    }
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
    if(vcV==false){
      $('#vcImg').remove();
      $('#vVC').append('<img id="vcImg" align="middle" src="images/cross.png">');
    } 
    $.messager.alert('注册提示',"您的注册信息某些地方有误，请完善您的注册信息",'info',function () {
      if(lnV==false){
        $('#loginName')[0].focus();
        $('#loginName')[0].select();
      }else if(maV=false){
        $('#mail')[0].focus();
        $('#mail')[0].select();
      }else if(psV==false){
        $('#password')[0].focus();
        $('#password')[0].select();
      }else if(cpsV=false){
        $('#confirmPassword')[0].focus();
        $('#confirmPassword')[0].select();
      }else{
        $('#checkCode')[0].focus();
        $('#checkCode')[0].select();
      }
    });
  }
}
//如果不是ie浏览器，从新初始化inputcsss
function setInputCss(){
  //遮罩层位置及样式
  $("#mask").css({
    "padding-top": 25,
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
    if($('#checkCode')!=null) $('#checkCode').css({"line-height":"35px","height":"35px","padding-top":"0px"});
    if($('#mail')!=null) $('#mail').css({"line-height":"35px","height":"35px","padding-top":"0px"});
    if($('#confirmPassword')!=null) $('#confirmPassword').css({"line-height":"35px","height":"35px","padding-top":"0px"});
    if($('#mailEndStr')!=null) $('#mailEndStr').css({"width":"84px"});
    if($('#commitButton')!=null) $('#commitButton').css({"width":"210px","padding-left":"38px","margin-left":"9px"});
    if($('#rstDiv')!=null) $('#rstDiv').css({"margin-left":"76px;"});
  }else{
    if($('#loginName')!=null) $('#loginName').css({"line-height":"35px", "height":"35px", "padding-top":"0px"});
    if($('#password')!=null) $('#password').css({"line-height":"35px", "height":"35px", "padding-top":"0px"});
    if($('#checkCode')!=null) $('#checkCode').css({"line-height":"35px", "height":"35px", "padding-top":"0px"});
    if($('#mail')!=null) $('#mail').css({"line-height":"35px","height":"35px","padding-top":"0px"});
    if($('#confirmPassword')!=null) $('#confirmPassword').css({"line-height":"35px","height":"35px","padding-top":"0px"});
    if($('#mailEndStr')!=null) $('#mailEndStr').css({"width":"84px"});
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
  if($('#checkCode').val()==$('#checkCode')[0].defaultValue){
    $('#checkCode').css('color','#ABCDEF');
  }
  $('#mailEndStr').combobox({    
    url:'<%=path%>/login/js/mailAdress.json',   
    valueField:'id',   
    textField:'text',
    height:37,
    onChange:function (index,o) {
      var eleId = 'mail';
      validateMail(eleId,index);
    },
    editable:false
  });
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
function validateLoginName(eleId){
  $('#lnImg').remove();
  $('#checkCode').html('');
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null||ele.val()==ele[0].defaultValue){
    ele.val(ele[0].defaultValue);
    ele.css('color','#ABCDEF');
    lnV = false;
  }else{
    if(checkStr(ele.val())){
      var vsMsg = checkLoginName(ele.val());
      if(vsMsg==true){
        $('#vLN').append('<img id="lnImg" src="images/accept.png">');
        lnV = true;
      }else{
        $('#vLN').append('<img id="lnImg" src="images/cross.png">');
        $('#checkResult').html('<div style="width:370;font-size:12px;color:red;">&nbsp;&nbsp;&nbsp;&nbsp;该登录名已被使用!</div>');
        lnV = false;
      }
    }else{
      $('#vLN').append('<img id="lnImg" src="images/cross.png">');
      $('#checkResult').html('<div style="width:370;height:40px; font-size:12px;color:red;">&nbsp;&nbsp;&nbsp;&nbsp;账号应为5~11位字母、数字、下划线!</div>');
      lnV = false;
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
function validateMail(eleId,index){
  $('#mailImg').remove();
  $('#checkResult').html("");
  var a = $('#mailEndStr').combobox('getData');
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null||ele.val()==ele[0].defaultValue){
    ele.val(ele[0].defaultValue);
    ele.css('color','#ABCDEF');
    maV = false;
  }else{
    var mailStr;
    if(index!=null) mailStr = ele.val() +a[index-1].text;
    else  mailStr = ele.val() +$('#mailEndStr').combobox('getText');
    var vsMsg = checkMail(mailStr);
    if(vsMsg==true){
      $('#vMail').append('<img style="padding-left:5px;" id="mailImg" src="images/accept.png">');
      maV = true;
    }else{
      $('#vMail').append('<img style="padding-left:5px;" id="mailImg" src="images/cross.png">');
      $('#checkResult').html('<div style="width:370;font-size:12px;color:red;">&nbsp;&nbsp;&nbsp;&nbsp;该邮箱已被注册!</div>');
      maV = false;
    }
  }
}
function validateValidateCode(eleId){
  $('#vcImg').remove();
  $('#checkResult').html('');
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null||ele.val()==ele[0].defaultValue){
    ele.val(ele[0].defaultValue);
    ele.css('color','#ABCDEF');
    vcV = false;
  }else{
    var vsMsg = verificationCheckCode(ele.val());
    if(vsMsg==true){
      $("#vVC").append('<img style="padding-left:5px;" id="vcImg" src="images/accept.png">');
      vcV = true;
    }else{
      $("#vVC").append('<img style="padding-left:5px;" id="vcImg" src="images/cross.png">');
      $('#checkResult').html('<div style="width:370;font-size:12px;color:red;">&nbsp;&nbsp;&nbsp;&nbsp;验证码错误!</div>');
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
  $.ajax({type:"post",async:false,url:url,data:pData,dataType:"json",
    success:function(json) {
      vfMsg = json;
    }
  });
  return vfMsg;
}
function checkMail(val){
  var vfMsg =null;
  var pData={
    "mail":val
  };
  var url="<%=path%>/login/validateMail.do";
  $.ajax({type:"post",async:false,url:url,data:pData,dataType:"json",
    success:function(json) {
      vfMsg = json;
    }
  });
  return vfMsg;
}
function checkStr(str){
  var re = /^[a-zA-z]\w{4,11}$/;
  if(re.test(str)){
    return true;
  }else{
    return false;
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
function checkLoginName(val){
  var vfMsg =null;
  var pData={"loginName":val};
  var url="<%=path%>/login/validateLoginName.do";
  $.ajax({type:"post",async:false,url:url,data:pData,dataType:"json",
     success:function(json) {
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
</script>
</html>
