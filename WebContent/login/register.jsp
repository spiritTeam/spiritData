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
      <table  width="370px;" border="1px;" bordercolor="red">
        <tr><td colspan="3"><div style="height: 30px;text-align: left;margin-left: 35px;" id="checkResult"></div></td></tr>
        <tr >
          <td align="right" width="100px;" ><span class="myspan">登录名&nbsp;&nbsp;</span></td>
          <td colspan="2" width="200px;" >
            <div style="float: left">
	            <input style="width:197px;" id="loginName" name="loginName"  tabindex="1" type="text" onmouseover=this.focus();this.select();
	              onclick="onClick(loginName);" onBlur="validateLoginName('loginName');" value="登录名" /></div>
          </td>
        </tr>
        <tr><td></td><td colspan="2"><div style="height: 20px;" id="loginNameCheck"></div></td></tr>
        <tr>
          <td align="right"><span class="myspan">邮箱&nbsp;&nbsp;</span></td>
          <td colspan="2" width="130px;">
          <div style="float: left">
	          <input  id="mail" name="mail"  tabindex="2" type="text"  onmouseover=this.focus();this.select(); 
	             onclick="onClick(mail);" onBlur="validateMail('mail');" value="绑定邮箱,用于激活账号" /></div>
          <div style="float: left;margin-left: -2px;">
             <input id="mailEndStr" name="mailEndStr" /></div></td>
        </tr>
        <tr><td></td><td colspan="2"><div style="height: 20px;" id="mailCheck"></div></td></tr>
        <tr>
          <td align="right"><span class="myspan">密码&nbsp;&nbsp;</span></td>
          <td colspan="2" rowspan="1">
            <div style="float: left">
              <input id="password" name="password"  tabindex="3" type="password" onmouseover=this.focus();this.select();
                onclick="onClick(password);" onBlur="validatePassword('password');" /></div>
          </td>
        <tr><td></td><td colspan="2"><div style="height: 20px;" id="passwordCheck"></div></td></tr>
        <tr >
          <td align="right"><span class="myspan">确认密码&nbsp;&nbsp;</span></td>
          <td colspan="2">
            <div style="float: left"></div>
              <input id="confirmPassword" name="confirmPassword"  tabindex="4" type="password" onmouseover=this.focus();this.select();
                onclick="onClick(confirmPassword);" onBlur="validateConfirmPassword('confirmPassword');" /></div>
          </td></tr>
        <tr><td></td><td colspan="2"><div style="height: 20px;" id="confirmPasswordCheck"></div></td></tr>
        <tr>
          <td align="right"><span class="myspan">验证码&nbsp;&nbsp;</span></td>
          <td width="130px;">
            <div style="float: left">
              <input id="checkCode" name="checkCode"  tabindex="5" type="text" value="请输入验证码" onmouseover=this.focus();this.select(); onclick="onClick(checkCode);" onBlur="validateValidateCode('checkCode');" />
            </div>
          </td>
          <td  align="left"><div style="border: 1px solid  #999999;width: 80px;"><img  title="点击更换" onclick="javascript:refresh(this);" src="<%=path%>/login/getValidateCode.do"></div></td>
          </tr>
        <tr><td></td><td colspan="2"><div style="height: 20px;" id="checkCodeCheck"></div></td></tr>
        <tr>
          <td colspan="3" align="center" >
            <div tabindex="6" style="width:150px; background-image:url(img/registerb.png); padding-left:2px;">
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
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null||ele.val()==ele[0].defaultValue){
    psV = false;
  }else{
    if(!checkPasswordStr(ele.val())){
      $('#checkResult').html('<div style="width:370;font-size: 12px;color:red;">&nbsp;&nbsp;&nbsp;&nbsp;<img src="img/cross.png">密码应由5~12位的字母、数字、下划线组成,且首字母不为数字!</div>');
      psV = false;
    }else{
    	$('#checkResult').html("");
      psV = true;
    }
  }
}
function validateLoginName(eleId){
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null||ele.val()==ele[0].defaultValue){
	  ele.val(ele[0].defaultValue);
    ele.css('color','#ABCDEF');
    lnV = false;
  }else{
    if(checkStr(ele.val())){
      var vsMsg = checkLoginName(ele.val());
      if(vsMsg==true){
        $('#checkResult').html('<div style="width:370;font-size: 12px;color:green;">&nbsp;&nbsp;&nbsp;&nbsp;<img src="img/accept.png">该登录名可以使用!</div>');
        lnV = true;
      }else{
        $('#checkResult').html('<div style="width:370;font-size: 12px;color:red;">&nbsp;&nbsp;&nbsp;&nbsp;<img src="img/cross.png">该登录名已被使用!</div>');
        lnV = false;
      }
    }else{
      $('#checkResult').html('<div style="width:370; font-size: 12px;color:red;">&nbsp;&nbsp;&nbsp;&nbsp;<img src="img/cross.png">登录名应由5~12位的字母、数字、下划线组成,且首字母不为数字!</div>');
      lnV = false;
    }
  }
}
function validateConfirmPassword(eleId){
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null||ele.val()==ele[0].defaultValue){
    cpsV =false;
  }else{
    if($('#password').val()!=ele.val()){
      $('#checkResult').html('<div style="width:370;font-size: 12px;color:red;">&nbsp;&nbsp;&nbsp;&nbsp;<img src="img/cross.png">密码不一致!</div>');
      cpsV =false;
    }else{
    	$('#checkResult').html("");
      cpsV =true;
    }
  }
}
function validateMail(eleId,index){
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
      $('#checkResult').html('<div style="width:370;font-size: 12px;color:green;">&nbsp;&nbsp;&nbsp;&nbsp;<img src="img/accept.png">邮箱可以使用!</div>');
      maV = true;
    }else{
      $('#checkResult').html('<div style="width:370;font-size: 12px;color:red;">&nbsp;&nbsp;&nbsp;&nbsp;<img src="img/cross.png">该邮箱已被使用!</div>');
      maV = false;
    }
  }
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
      $('#checkResult').html('<div style="width:370;font-size: 12px;color:green;">&nbsp;&nbsp;&nbsp;&nbsp;<img src="img/accept.png">验证码正确!</div>');
      vcV = true;
    }else{
      $('#checkResult').html('<div style="width:370;font-size: 12px;color:red;">&nbsp;&nbsp;&nbsp;&nbsp;<img src="img/cross.png">验证码错误!</div>');
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
