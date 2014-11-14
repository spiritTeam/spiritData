<%@page import="java.io.Writer"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
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
    <form  style="margin-top: 15px;" action="">
      <table  width="430px;" >
        <tr >
          <td align="right" width="100px;" ><span>登录名&nbsp;&nbsp;</span></td>
          <td colspan="2" >
            <input style="width:280px;" id="loginName" name="loginName"  tabindex="1" type="text" value="" onmouseover=this.focus();this.select();
              onclick="onClick(loginName);" onBlur="validateLoginName('loginName');"/>
          </td>
        </tr>
        <tr><td></td><td colspan="2"><div style="height: 30px;" id="loginNameCheck"></div></td></tr>
        <tr>
          <td align="right"><span >邮箱&nbsp;&nbsp;</span></td>
          <td colspan="2" width="130px;"><input style="width:150px;"  id="mail" name="mail"  tabindex="2" type="text"  onmouseover=this.focus();this.select(); 
             onclick="onClick(mail);" onBlur="validateMail('mail');"/><input style="width: 125px;height: 37px;font-size: 12px;" id="mailEndStr" name="mailEndStr" /></td>
        </tr>
        <tr><td></td><td colspan="2"><div style="height: 30px;" id="mailCheck"></div></td></tr>
        <tr>
          <td align="right"><span>密码&nbsp;&nbsp;</span></td>
          <td colspan="2" rowspan="1">
            <input style="width:280px;" id="password" name="password"  tabindex="3" type="password" value="" onmouseover=this.focus();this.select();
              onclick="onClick(password);" onBlur="validatePassword('password');" />
          </td>
        <tr><td></td><td colspan="2"><div style="height: 30px;" id="passwordCheck"></div></td></tr>
        <tr >
          <td align="right"><span >确认密码&nbsp;&nbsp;</span></td>
          <td colspan="2">
            <input style="width:280px;" id="confirmPassword" name="confirmPassword"  tabindex="4" type="password" value="" onmouseover=this.focus();this.select();
              onclick="onClick(confirmPassword);" onBlur="validateConfirmPassword('confirmPassword');" />
          </td></tr>
        <tr><td></td><td colspan="2"><div style="height: 30px;" id="confirmPasswordCheck"></div></td></tr>
        <tr>
          <td align="right"><span >验证码&nbsp;&nbsp;</span></td>
          <td width="130px;"><input style="width:195px;"  id="checkCode" name="checkCode"  tabindex="5" type="text" value="请输入验证码" onmouseover=this.focus();this.select(); onclick="onClick(checkCode);" onBlur="validateValidateCode('checkCode');" /></td>
          <td  align="left"><div style="border: 1px solid  #999999;width: 80px;"><img  title="点击更换" onclick="javascript:refresh(this);" src="<%=path%>/getValidateCode.do"></div></td>
          </tr>
        <tr><td></td><td colspan="2"><div style="height: 30px;" id="checkCodeCheck"></div></td></tr>
        <tr>
          <td colspan="3" align="center" >
            <div style="width:150px; background-image:url(img/registerb.png); padding-left:2px;">
              <img id="register" name="register" src="img/register.png" onclick="saveRegister();"/>
            </div>
          </td>
        </tr>
      </table>
    </form>
    <div align="right" style="width:400px; margin-top: 20px;"><input  type="button" value="返回登录页面" onclick="returnLogin();"></div>
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
	  var url="<%=path%>/Register.do";
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
		$.message.alert('注册提示',"您的注册信息某些地方有误，请完善您的注册信息");
		return ;
	}
}
function returnLogin(){
	window.location.href="<%=path%>/login/login.jsp";
}
function jumpLogin(){
	window.location.href="http://localhost:8080/sa/login/login.jsp";
}
$(function() {
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
  obj.src = "<%=path%>/getValidateCode.do?"+Math.random();
}
function validatePassword(eleId){
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null||ele.val()==ele[0].defaultValue){
     $('#'+eleId+'Check').html('<img src="img/cross.png"><span style="font-size: 12px;color:red;">密码不能为空!</span>');
     psV = false;
   }else{
     if(!checkStr(ele.val())){
       $('#'+eleId+'Check').html('<img src="img/cross.png"><span style="font-size: 12px;color:red;">密码应由5~12位的字母、数字、下划线组成,且首字母不为数字!</span>');
       psV = false;
     }else{
       $('#'+eleId+'Check').html('<img src="img/accept.png">');
       psV = true;
     }
   }
}
function validateLoginName(eleId){
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null||ele.val()==ele[0].defaultValue){
    $('#'+eleId+'Check').html('<img src="img/cross.png"><span style="font-size: 12px;color:red;">登录名不能为空!</span>');
    lnV = false;
  }else{
    if(checkStr(ele.val())){
      var vsMsg = checkLoginName(ele.val());
      if(vsMsg==true){
        $('#'+eleId+'Check').html('<img src="img/accept.png"><span style="font-size: 12px;color:green;">该登录名可以使用!</span>');
        lnV = true;
      }else{
        $('#'+eleId+'Check').html('<img src="img/cross.png"><span style="font-size: 12px;color:red;">该登录名已被使用!</span>');
        lnV = false;
      }
    }else{
      $('#'+eleId+'Check').html('<img src="img/cross.png"><span style="font-size: 12px;color:red;">登录名应由5~12位的字母、数字、下划线组成,且首字母不为数字!</span>');
      lnV = false;
    }
  }
}
function validateConfirmPassword(eleId){
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null||ele.val()==ele[0].defaultValue){
    $('#'+eleId+'Check').html('<img src="img/cross.png"><span style="font-size: 12px;color:red;">请确认密码!</span>');
    cpsV =false;
  }else{
    if($('#password').val()!=ele.val()){
      $('#'+eleId+'Check').html('<img src="img/cross.png"><span style="font-size: 12px;color:red;">密码不一致!</span>');
      cpsV =false;
    }else{
      $('#'+eleId+'Check').html('<img src="img/accept.png">');
      cpsV =true;
    }
  }
}
function validateMail(eleId,index){
  var a = $('#mailEndStr').combobox('getData');
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null||ele.val()==ele[0].defaultValue){
    $('#'+eleId+'Check').html('<img src="img/cross.png"><span style="font-size: 12px;color:red;">邮箱不能为空!</span>');
    maV = false;
  }else{
    if(checkStr(ele.val())){
    	var mailStr;
    	if(index!=null) mailStr = ele.val() +a[index-1].text;
    	else  mailStr = ele.val() +$('#mailEndStr').combobox('getText');
      var vsMsg = checkMail(mailStr);
      if(vsMsg==true){
        $('#'+eleId+'Check').html('<img src="img/accept.png">');
        maV = true;
      }else{
        $('#'+eleId+'Check').html('<img src="img/cross.png"><span style="font-size: 12px;color:red;">该邮箱已被使用!</span>');
        maV = false;
      }
    }else{
      $('#'+eleId+'Check').html('<img src="img/cross.png"><span style="font-size: 12px;color:red;">邮箱应由5~12位的字母、数字、下划线组成,且首字母不为数字!</span>');
      maV = false;
    }
  }
}
function validateValidateCode(eleId){
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null||ele.val()==ele[0].defaultValue){
    $('#'+eleId+'Check').html('<img src="img/cross.png"><span style="font-size: 12px;color:red;">验证码不能为空!</span>');
    vcV = false;
  }else{
    var vsMsg = verificationCheckCode(ele.val());
    if(vsMsg==true){
      $('#'+eleId+'Check').html('<img src="img/accept.png">');
      vcV = true;
    }else{
      $('#'+eleId+'Check').html('<img src="img/cross.png"><span style="font-size: 12px;color:red;">验证码错误!</span>');
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
    var url="<%=path%>/validateValidateCode.do";
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
  var url="<%=path%>/validateMail.do";
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
function checkLoginName(val){
  var vfMsg =null;
  var pData={
    "loginName":val
  };
  var url="<%=path%>/validateLoginName.do";
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
