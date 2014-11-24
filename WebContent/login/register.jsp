<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
  String path = request.getContextPath();
  String sid = request.getSession().getId();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>register</title>
<jsp:include page="/common/sysInclude.jsp" flush="true"/>
<link rel="stylesheet" type="text/css" href="<%=path%>/login/css/login.css">
</head>
<body>
<center>
  <div style="border:1px solid #ABCDEF;width: 450px;height: 500px;">
    <div style="margin-top: 15px; margin-left: 25px;"align="left"><span style="font-size: 20px;color: #999999;">注册账号</span></div>
    <div style="height:10px; width:400px;border-top: 1px solid  #999999;"></div>
    <form  style="" action="">
      <table  width="370px;" border="1px;" >
        <tr><td colspan="3"><div style="height: 30px;text-align: left;margin-left: 35px;" id="checkResult"></div></td></tr>
        <tr style="height:50px; valign:top;" >
          <td align="right" width="100px;" ><span class="myspan">账　号</span></td>
          <td colspan="2" width="200px;" >
            <div style="float: left">
              <input style="width:197px;" id="loginName" name="loginName"  tabindex="1" type="text" onmouseover=this.focus();this.select();
                onclick="onClick(loginName);" onBlur="validateLoginName('loginName');" value="用户账号" /></div>
            <div style="float: left;width: 25px;height: 25px;padding-top: 8px;margin-left: -2px;" align="center" id='vLN'></div>
          </td>
        </tr>
        <tr style="height:50px; valign:top;">
          <td align="right"><span class="myspan">邮　箱</span></td>
          <td colspan="2" width="130px;">
	          <div style="float: left">
	            <input  id="mail" name="mail"  tabindex="2" type="text"  onmouseover=this.focus();this.select(); 
	               onclick="onClick(mail);" onBlur="validateMail('mail');" value="您的邮箱" /></div>
	          <div style="float: left;margin-left: -2px;">
	             <input id="mailEndStr" name="mailEndStr"/></div>
	          <div style="float: left;width: 25px;height: 25px;padding-top: 8px;margin-left: -2px;" align="center" id='vMail'></div>
          </td>
        </tr>
        <tr style="height:50px; valign:top;">
          <td align="right"><span class="myspan">密　码</span></td>
          <td colspan="2" rowspan="1">
            <div style="float: left">
              <input id="password" name="password"  tabindex="3" type="password" onmouseover=this.focus();this.select();
                onclick="onClick(password);" onBlur="validatePassword('password');" /></div>
            <div style="float: left;width: 25px;height: 25px;padding-top: 8px;margin-left: -2px;" align="center" id='vPwd'></div>
          </td>
        </tr>
        <tr style="height:50px; valign:top;">
          <td align="right"><span class="myspan" style="font-size: 12px;">确认密码</span></td>
          <td colspan="2">
            <div style="float: left">
              <input id="confirmPassword" name="confirmPassword"  tabindex="4" type="password" onmouseover=this.focus();this.select();
                onclick="onClick(confirmPassword);" onBlur="validateConfirmPassword('confirmPassword');" /></div>
            <div style="float: left;width: 25px;height: 25px;padding-top: 8px;margin-left: -2px;" align="center" id='vCPwd'></div>
          </td>
        </tr>
        <tr style="height:50px; valign:top;">
          <td align="right"><span class="myspan">验证码</span></td>
          <td width="130px;">
            <div style="float: left">
              <input id="checkCode" name="checkCode"  tabindex="5" type="text" value="请输入验证码" onmouseover=this.focus();this.select(); onclick="onClick(checkCode);" onBlur="validateValidateCode('checkCode');" />
            </div>
          </td>
          <td  align="left">
            <div style="border: 1px solid  #999999;width: 80px;float: left;margin-left:-10px;"><img id="vcimg" title="点击更换" onclick="javascript:refresh(this);" src="<%=path%>/login/getValidateCode.do"></div>
            <div style="float: left;width: 25px;height: 25px;padding-top: 8px;margin-left: -2px;" align="center" id='vVC'></div>
          </td>
        </tr>
        <tr>
          <td colspan="3" style="" ><!--width:210px; background-image:url(img/registerb.png); padding-left:45px;margin-left: 48px;  -->
            <div tabindex="6" id="commitButton" style="width:250px; background-image:url(img/registerb.png); padding-left:8px;margin-left: 48px;">
              <img id="register" name="register" src="img/register.png" onclick="saveRegister();"/>
            </div>
          </td>
        </tr>
      </table>
    </form>
    <div align="right" style="width:400px; margin-top: 20px;"><span style="font-size: 12px;" onclick="returnLogin()">&nbsp;返回登录页面</span></div>
  </div>
</center>
</body>
<script type="text/javascript">
var psV=false,cpsV=false,lnV=false,maV=false,vcV=false;
function saveRegister(){
	$('#vcimg')[0].src = "<%=path%>/login/getValidateCode.do?"+Math.random();
  if(psV&&cpsV&&lnV&&maV&&vcV){
    var pData={
      "loginName":$("#loginName").val(),
      "password":$("#password").val(),
      "userName":$("#userName").val(),
      "mailAdress":$("#mail").val()+$('#mailEndStr').combobox('getText'),
    };
    var url="<%=path%>/login/register.do";
    $.ajax({type:"post", async:false, url:url, data:pData, dataType:"json",
      success: function(json) {
        vfMsg = json;
        if(vfMsg){
          $.messager.show({
            title:'注册提示',
            msg:'消息将在3秒后跳转到登录页面!',
            timeout:3000,
            showType:'slide'
          });
          jumpLogin();
          //setTimeout("jumpLogin();", 3000 );
        }
      }
    });
  }else{
	  if(lnV==false) {
      $('#lnImg').remove();
      $('#vLN').append('<img id="lnImg" align="middle" src="img/cross.png">');
    }
	  if(cpsV==false){
		  $('#cpwdImg').remove();
      $('#vCPwd').append('<img id="cpwdImg" align="middle" src="img/cross.png">');
	  }
	  if(maV==false){
		  $('#mailImg').remove();
      $('#vMail').append('<img id="mailImg" align="middle" src="img/cross.png">');
	  }
    if(psV==false){
      $('#pwdImg').remove();
      $('#vPwd').append('<img id="pwdImg" align="middle" src="img/cross.png">');
    } 
    if(vcV==false){
      $('#vcImg').remove();
      $('#vVC').append('<img id="vcImg" align="middle" src="img/cross.png">');
    } 
    $.messager.alert('注册提示',"您的注册信息某些地方有误，请完善您的注册信息");
    return ;
  }
}
function returnLogin(){
  window.location.href="<%=path%>/login/login.jsp";
}
function jumpLogin(){
  window.location.href="http://localhost:8080/sa/login/login.jsp";
}
//如果不是ie浏览器，从新初始化inputcsss
function setInputCss() {
  var browserType = getBrowserVersion();
  browserType = browserType.substring(0,browserType.lastIndexOf(' '));
  if(browserType!='msie'){
    $('#loginName').css({"line-height":"35px", "height":"35px", "padding-top":"0px"});
    $('#password').css({"line-height":"35px", "height":"35px", "padding-top":"0px"});
    $('#checkCode').css({"line-height":"35px", "height":"35px", "padding-top":"0px"});
    $('#mail').css({"line-height":"35px", "height":"35px", "padding-top":"0px"});
    $('#confirmPassword').css({"line-height":"35px", "height":"35px", "padding-top":"0px"});
    $('#checkCode').css({"line-height":"35px", "height":"35px", "padding-top":"0px"});
    $('#commitButton').css({"width":"210px","padding-left":"45px"});
  }
}
$(function() {
  setInputCss();
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
    url:'mailEndStr.json',    
    valueField:'id',    
    textField:'text',
    height:35,
    onChange:function (index,o) {
      var eleId = 'mail';
      validateMail(eleId,index);
    },
    editable:false
  });  
});
function refresh(obj) {
  obj.src = "<%=path%>/login/getValidateCode.do?"+Math.random();
}
function validatePassword(eleId){
  $('#checkResult').html("");
	$('#pwdImg').remove();
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null||ele.val()==ele[0].defaultValue){
    psV = false;
  }else{
    if(!checkPasswordStr(ele.val())){
      $('#checkResult').html('<div style="width:370;font-size: 12px;color:red;">&nbsp;&nbsp;&nbsp;&nbsp;密码应由5~12位的字母、数字、下划线组成!</div>');
      psV = false;
    }else{
    	$('#vPwd').append('<img id="pwdImg" align="middle" src="img/accept.png">');
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
    	  $('#vLN').append('<img id="lnImg" src="img/accept.png">');
        lnV = true;
      }else{
    	  $('#vLN').append('<img id="lnImg" src="img/cross.png">');
        $('#checkResult').html('<div style="width:370;font-size: 12px;color:red;">&nbsp;&nbsp;&nbsp;&nbsp;该登录名已被使用!</div>');
        lnV = false;
      }
    }else{
    	$('#vLN').append('<img id="lnImg" src="img/cross.png">');
      $('#checkResult').html('<div style="width:370;height:40px; font-size: 12px;color:red;">&nbsp;&nbsp;&nbsp;&nbsp;账号应为5~11位字母、数字、下划线组成。</div>');
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
    	$('#vCPwd').append('<img id="cpwdImg" src="img/cross.png">');
      $('#checkResult').html('<div style="width:370;font-size: 12px;color:red;">&nbsp;&nbsp;&nbsp;&nbsp;密码不一致!</div>');
      cpsV =false;
    }else{
    	$('#vCPwd').append('<img id="cpwdImg" src="img/accept.png">');
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
    	$('#vMail').append('<img id="mailImg" src="img/accept.png">');
      maV = true;
    }else{
    	$('#vMail').append('<img id="mailImg" src="img/cross.png">');
      $('#checkResult').html('<div style="width:370;font-size: 12px;color:red;">&nbsp;&nbsp;&nbsp;&nbsp;该邮箱已被注册!</div>');
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
    	$("#vVC").append('<img id="vcImg" src="img/accept.png">');
      vcV = true;
    }else{
    	$("#vVC").append('<img id="vcImg" src="img/cross.png">');
      $('#checkResult').html('<div style="width:370;font-size: 12px;color:red;">&nbsp;&nbsp;&nbsp;&nbsp;验证码错误!</div>');
      vcV = false;
    }
  }
}
function validateUserName(eleId){
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
function checkMail(val){
  var vfMsg =null;
  var pData={
    "mail":val
  };
  var url="<%=path%>/login/validateMail.do";
  $.ajax({type:"post", async:false, url:url, data:pData, dataType:"json",
    success: function(json) {
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
  var re = /[0-9a-zA-z]\w{5,11}$/;
  if(re.test(str)){
    return true;
  }else{
    return false;
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
</script>
</html>
