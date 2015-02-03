<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
  String path = request.getContextPath();
  String sid = request.getSession().getId();
  String pWinId = request.getParameter("pWinId");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>注册</title>
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
<center><div id="mainDiv">
  <form><table>
    <tr style="height:35px; valign:top;">
      <td class="labelTd">账　号</td>
      <td class="inputTd">
        <div class="alertInput-Text">
          <input id="loginName" name="loginName" tabindex="1" type="text" value="用户账号" onclick="onClick(loginName);" onBlur="validateLoginName();"/>
          <div class="alertImg"></div>
        </div>
      </td>
    </tr>
    <tr style="height:35px; valign:top;">
      <td class="labelTd">邮　箱</td>
      <td class="inputTd">
        <div class="alertInput-Text">
          <input id="loginName" name="loginName" tabindex="1" type="text" value="邮箱" onclick="onClick(loginName);" onBlur="validateLoginName();"/>
          <div class="alertImg"></div>
        </div>
      </td>
    </tr>
    <tr style="height:35px; valign:top;">
      <td class="labelTd">密　码</td>
      <td class="inputTd">
        <div class="alertInput-Text">
          <input id="loginName" name="loginName" tabindex="1" type="text" value="" onclick="onClick(loginName);" onBlur="validateLoginName();"/>
          <div class="alertImg"></div>
        </div>
      </td>
    </tr>
    <tr style="height:35px; valign:top;">
      <td class="labelTd">确　认</td>
      <td class="inputTd">
        <div class="alertInput-Text">
          <input id="loginName" name="loginName" tabindex="1" type="text" value="" onclick="onClick(loginName);" onBlur="validateLoginName();"/>
          <div class="alertImg"></div>
        </div>
      </td>
    </tr>
    <tr style="height:35px; valign:top;">
      <td class="labelTd">验证码</td>
      <td class="inputTd">
        <div class="alertInput-Text">
          <input id="loginName" name="loginName" tabindex="1" type="text" value="验证码" onclick="onClick(loginName);" onBlur="validateLoginName();"/>
          <div class="alertImg"></div>
        </div>
      </td>
    </tr>
    <tr>
      <td colspan="2" class="commitBottonTd">
        <a id="register" name="register" onclick="saveRegister();" href="#">
        <div tabindex="6" id="commitButton" class="commitDiv">
          <span style="font-size:16px;color:#FFFFFF;font-weight: bold;">注　册</span>
        </div>
        </a>
      </td>
    </tr>
  </table></form>
</div></center>
</body>
<script type="text/javascript">
var psV=false,cpsV=false,lnV=false,maV=false,vcV=false;
var mainPage = getMainPage();
var pWinId = '<%=pWinId%>';
var winId = mainPage.registerWinId;
if(winId==null||winId=="") winId = pWinId;
var win = getSWinInMain(winId);
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
          if(mainPage){
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
    "padding-top": ($(window).height()-95)/3,//设置图片位置
    "top": parseInt($("#mainDiv").css("top"))-10,
    "left": parseInt($("#mainDiv").css("left"))-10,
    "width": (parseInt($("#mainDiv").css("width"))+20)+"px",
    "height": (parseInt($("#mainDiv").css("height"))+40)+"px"
  });
  //解决combox的border颜色问题
  $(".combo").css('border-color','#FFF');
  $(".combo").css('border-left-color','#ABADB3');
  //针对浏览器问题
  var browserType = getBrowserVersion();
  var v = browserType.substring(0,browserType.lastIndexOf(' '));
  $("div.intro span").css({'border-color':'#999999','border-left':'0px'});
  if(v=='msie'){
    if($('#loginName')!=null) $('#loginName').css({"width":"215px"});
    if($('#mail')!=null) {
      $('#mail').css({"width":"130px"});
      $('#vMail').css({"left":"79px"});
    }
    if($('#mailEndStr')!=null) $('#mailEndStr').css({"width":"100px"});
    if($('#password')!=null) $('#password').css({"width":"215px"});
    if($('#confirmPassword')!=null) $('#confirmPassword').css({"width":"215px"});
    if($('#checkCode')!=null) {
      $('#checkCode').css({"width":"130px"});
      $('#vVC').css({"left":"79px"});
    }
    if($('#rstDiv')!=null) $('#rstDiv').css({"margin-left":"76px;"});
    if($('#commitButton')!=null) $('#commitButton').css({"width":"177px","padding-left":"100px","margin-left":"8px"});
  }else if(v=='chrome'){alert('chrome');
    if($('#loginName')!=null) {
      $('#loginName').css({"width":"225px"});
      $('#loginName').parent().css({"width":"225px"});
    }
    if($('#mail')!=null) {
      $('#mail').css({"width":"130px"});
      $('#vMail').css({"left":"79px"});
      ggMouse($('#mail'));
    }
    if($('#mailEndStr')!=null) $('#mailEndStr').css({"width":"100px"});
    if($('#password')!=null) $('#password').css({"width":"215px"});
    if($('#confirmPassword')!=null) $('#confirmPassword').css({"width":"215px"});
    if($('#checkCode')!=null) {
      $('#checkCode').css({"width":"130px"});
      $('#vVC').css({"left":"79px"});
    }
    if($('#rstDiv')!=null) $('#rstDiv').css({"margin-left":"76px;"});
    if($('#commitButton')!=null) $('#commitButton').css({"width":"177px","padding-left":"100px","margin-left":"8px"});
  }else{
    if($('#loginName')!=null) $('#loginName').css({"width":"215px"});
    if($('#mail')!=null) {
      $('#mail').css({"width":"130px"});
      $('#vMail').css({"left":"79px"});
    }
    if($('#mailEndStr')!=null) $('#mailEndStr').css({"width":"100px"});
    if($('#password')!=null) $('#password').css({"width":"215px"});
    if($('#confirmPassword')!=null) $('#confirmPassword').css({"width":"215px"});
    if($('#checkCode')!=null) {
      $('#checkCode').css({"width":"130px"});
      $('#vVC').css({"left":"79px"});
    }
    if($('#rstDiv')!=null) $('#rstDiv').css({"margin-left":"76px;"});
    if($('#commitButton')!=null) $('#commitButton').css({"width":"177px","padding-left":"100px","margin-left":"8px"});
  }
}
function ggMouse(jQobj){
  var jqP = jQobj.parent();
  jQobj.bind('mouseover',function(){
    jqP.css("border","1px red solid");
  });return;
  jQobj.mouseover(function(){
    jqP.focus();return;
    jqP.css("border","1px red solid");
  });
  jQobj.mouseout(function(){
    jqP.css("border","1px solid yellow");
  });
}
/**
 * input鼠标移入移出效果
 */
function inputMouseEffect(){
  $(":input").bind('mouseover',function(){
    var _self = this;
    var parent = $(_self).parent();
    parent.css('border','2px #ABCDEF solid');
  });
  $(":input").bind('mouseout',function(){
    var _self = this;
    var parent = $(_self).parent();
    parent.css('border','1px #ABADB3 solid');
  });
}

/**
 * 主函数
 */
$(function(){
  inputMouseEffect();
  return;
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
    width:85,
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
      win.setMessage({'msg':'&nbsp;&nbsp;密码应是5~12位的字母、数字、下划线!'});
      $('#vPwd').append('<img id="pwdImg" align="middle" src="images/cross.png">');
      psV = false;
    }else{
      $('#vPwd').append('<img id="pwdImg" align="middle" src="images/accept.png">');
      win.setMessage({'msg':''});
      psV = true;
    }
  }
}
function validateLoginName(eleId){return;
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
        win.setMessage({'msg':''});
        lnV = true;
      }else{
        $('#vLN').append('<img id="lnImg" src="images/cross.png">');
        win.setMessage({'msg':'该登录名已被使用!'});
        $('#checkResult').html('<div style="width:370;font-size:12px;color:red;">&nbsp;&nbsp;该登录名已被使用!</div>');
        lnV = false;
      }
    }else{
      $('#vLN').append('<img id="lnImg" src="images/cross.png">');
      win.setMessage({'msg':'&nbsp;&nbsp;账号应为5~11位的字母、数字、下划线!'});
      $('#checkResult').html('<div style="width:370;height:40px; font-size:12px;color:red;">&nbsp;&nbsp;&nbsp;&nbsp;账号应为5~11位字母、数字、下划线!</div>');
      lnV = false;
    }
  }
}
/**
 * 验证确认密码
 */
function validateConfirmPassword(eleId){
  $("#cpwdImg").remove();
  $('#checkResult').html("");
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null||ele.val()==ele[0].defaultValue){
    cpsV =false;
  }else{
    if($('#password').val()!=ele.val()){
      $('#vCPwd').append('<img id="cpwdImg" src="images/cross.png">');
      win.setMessage({'msg':'&nbsp;&nbsp;账号应为5~11位的字母、数字、下划线!'});
      $('#checkResult').html('<div style="width:370;font-size:12px;color:red;">&nbsp;&nbsp;&nbsp;&nbsp;密码不一致!</div>');
      cpsV =false;
    }else{
      $('#vCPwd').append('<img id="cpwdImg" src="images/accept.png">');
      win.setMessage({'msg':''});
      cpsV =true;
    }
  }
}
/**
 * 验证邮箱
 */
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
      win.setMessage({'msg':''});
      maV = true;
    }else{
      win.setMessage({'msg':'&nbsp;&nbsp;&nbsp;&nbsp;该邮箱已被注册!'});
      $('#vMail').append('<img style="padding-left:5px;" id="mailImg" src="images/cross.png">');
      $('#checkResult').html('<div style="width:370;font-size:12px;color:red;">&nbsp;&nbsp;&nbsp;&nbsp;该邮箱已被注册!</div>');
      maV = false;
    }
  }
}
/**
 * 验证码
 */
function validateCheckCode(eleId){
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
      win.setMessage({'msg':''});
      vcV = true;
    }else{
      $("#vVC").append('<img style="padding-left:5px;" id="vcImg" src="images/cross.png">');
      win.setMessage({'msg':'&nbsp;&nbsp;验证码错误!'});
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
