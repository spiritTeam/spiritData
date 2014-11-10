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
</head>
<body>
<center>
 <div style="border:1px solid #ABCDEF;width: 1000px;height: 500px;">
  <div style="float: left;border:1px solid #ABCDEF;width: 700px;height: 500px;">
  <div style="margin-top: 50px;"></div>
    <form action="">
      <table width="580px;" >
        <tr >
          <td align="right" width="100px;" ><span style="width: 150px;font-size: 16px;">登录名&nbsp;&nbsp;</span></td>
          <td colspan="2" width="230px;" >
            <input style="width:235px;height:35px;color:#999;font-size: 12px;" id="loginName" name="loginName"  tabindex="1" type="text" value="" onmouseover=this.focus();this.select();
              onclick="onClick(loginName);" onBlur="validateLoginName('loginName');"/>
          </td>
          <td width="250px;"><div id="loginNameCheck"></div></td>
        </tr>
        <tr><td ><div style="height: 20px;"></div></td></tr>
        <tr>
          <td align="right"><span style="width: 150px;font-size: 16px;">姓名&nbsp;&nbsp;</span></td>
          <td colspan="2" rowspan="1">
          <input style="width:235px;height:35px;color:#999;font-size: 12px;" id="userName" name="userName"  tabindex="1" type="text" value="" onmouseover=this.focus();this.select();
             onclick="onClick(userName);" onBlur="validateUserName('userName');"/>
          </td>
          <td><div id="userNameCheck"></div></td></tr>
        <tr><td ><div style="height: 20px;"></div></td></tr>
        <tr >
          <td align="right"><span style="width: 150px;font-size: 16px;">密码&nbsp;&nbsp;</span></td>
          <td colspan="2" rowspan="1">
            <input style="width:235px;height:35px;color:#999;font-size: 12px" id="password" name="password"  tabindex="2" type="password" value="" onmouseover=this.focus();this.select();
              onclick="onClick(password);" onBlur="validatePassword('password');" />
          </td>
          <td><div id="passwordCheck"></div></td></tr>
        <tr><td ><div style="height: 20px;"></div></td></tr>
        <tr >
          <td align="right"><span style="width: 150px;font-size: 16px;">确认密码&nbsp;&nbsp;</span></td>
          <td colspan="2" rowspan="1">
            <input style="width:235px;height:35px;color:#999;font-size: 12px" id="confirmPassword" name="confirmPassword"  tabindex="2" type="password" value="" onmouseover=this.focus();this.select();
              onclick="onClick(confirmPassword);" onBlur="validateConfirmPassword('confirmPassword');" />
          </td><td><div id="confirmPasswordCheck"></div></td></tr>
        <tr><td ><div style="height: 20px;"></div></td></tr>
        <tr>
          <td align="right"><span style="width: 150px;font-size: 16px;">邮箱&nbsp;&nbsp;</span></td>
          <td colspan="1" width="130px;"><input style="width:130px;height:35px;color:#999;font-size: 16px;"  id="mail" name="mail"  tabindex="3" type="text"  onmouseover=this.focus();this.select(); 
             onclick="onClick(mail);" onBlur="validateMail('mail');"/></td>
          <td  align="left"><input style="width: 100px;height: 37px;font-size: 12px;" id="mailEndStr" name="mailEndStr" ></td>
          <td><div id="mailCheck"></div></td>
        </tr>
           <tr><td ><div style="height: 20px;"></div></td></tr>
        <tr>
        <td align="right"><span style="width: 150px;font-size: 16px;">验证码&nbsp;&nbsp;</span></td>
        <td colspan="1" width="130px;"><input style="width:130px;height:35px;color:#999;font-size: 16px;"  id="checkCode" name="checkCode"  tabindex="3" type="text" value="请输入验证码" onmouseover=this.focus();this.select(); onclick="onClick(checkCode);" onBlur="validateValidateCode('checkCode');" /></td>
          <td  align="left"><img style="width: 100px;" title="点击更换" onclick="javascript:refresh(this);" src="<%=path%>/getValidateCode.do"></td><td><div id="checkCodeCheck"></div></td></tr>
        <tr><td ><div style="height: 20px;"></div></td><td></td><td></td></tr>
        <tr><td colspan="4" align="center" ><img id="register" name="register" src="register.png" onclick="saveRegister();"></td></tr>
      </table>
    </form>
  </div>
  <div style="float: right;border:1px solid #ABCDEF;width: 296px;height: 500px;">
  </div>
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
	    	  alert('注册成功3秒后跳转到登录页面');
	    	  setTimeout("jumpLogin();", 3000 );
	      }
	    }
	  });
	}else{
		alert("您的注册信息某些地方有误，请完善您的注册信息");
		return ;
	}
}
function checkVal(obj){
	obj.focus();obj.select();
	if(""+obj=="checkCode"){
		alert(true);
	}
}
function jumpLogin(){
	window.location.href="http://localhost:8080/sa/login/login.jsp";
}
function checkVal(obj){
  obj.focus();obj.select();
  if(""+obj=="checkCode"){
    alert(true);
  }
}
$(function() {
  $('#mailEndStr').combobox({    
    url:'mailEndStr.json',    
    valueField:'id',    
    textField:'text',   
    editable:false
  });  
});
function refresh(obj) {
  obj.src = "<%=path%>/getValidateCode.do?"+Math.random();
}
function validatePassword(eleId){
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null){
     $('#'+eleId+'Check').html('<img src="cross.png"><span style="font-size: 12px;color:red;">密码不能为空!</span>');
     psV = false;
   }else{
     if(!checkStr(ele.val())){
       $('#'+eleId+'Check').html('<img src="cross.png"><span style="font-size: 12px;color:red;">密码应由5~12位的字母、数字、下划线组成,且首字母不为数字!</span>');
       psV = false;
     }else{
       $('#'+eleId+'Check').html('<img src="accept.png">');
       psV = true;
     }
   }
}
function validateLoginName(eleId){
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null){
    $('#'+eleId+'Check').html('<img src="cross.png"><span style="font-size: 12px;color:red;">登录名不能为空!</span>');
    lnV = false;
  }else{
    if(checkStr(ele.val())){
      var vsMsg = checkLoginName(ele.val());
      if(vsMsg==true){
        $('#'+eleId+'Check').html('<img src="accept.png"><span style="font-size: 12px;color:green;">该登录名可以使用!</span>');
        lnV = true;
      }else{
        $('#'+eleId+'Check').html('<img src="cross.png"><span style="font-size: 12px;color:red;">该登录名已被使用!</span>');
        lnV = false;
      }
    }else{
      $('#'+eleId+'Check').html('<img src="cross.png"><span style="font-size: 12px;color:red;">登录名应由5~12位的字母、数字、下划线组成,且首字母不为数字!</span>');
      lnV = false;
    }
  }
}
function validateConfirmPassword(eleId){
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null){
    $('#'+eleId+'Check').html('<img src="cross.png"><span style="font-size: 12px;color:red;">请确认密码!</span>');
    cpsV =false;
  }else{
    if($('#password').val()!=ele.val()){
      $('#'+eleId+'Check').html('<img src="cross.png"><span style="font-size: 12px;color:red;">密码不一致!</span>');
      cpsV =false;
    }else{
      $('#'+eleId+'Check').html('<img src="accept.png">');
      cpsV =true;
    }
  }
}
function validateMail(eleId){
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null){
    $('#'+eleId+'Check').html('<img src="cross.png"><span style="font-size: 12px;color:red;">邮箱不能为空!</span>');
    maV = false;
  }else{
    if(checkStr(ele.val())){
      var mailStr = ele.val() +$('#mailEndStr').combobox('getText');
      var vsMsg = checkMail(mailStr);
      if(vsMsg==true){
        $('#'+eleId+'Check').html('<img src="accept.png">');
        maV = true;
      }else{
        $('#'+eleId+'Check').html('<img src="cross.png"><span style="font-size: 12px;color:red;">该邮箱已被使用!</span>');
        maV = false;
      }
    }else{
      $('#'+eleId+'Check').html('<img src="cross.png"><span style="font-size: 12px;color:red;">邮箱应由5~12位的字母、数字、下划线组成,且首字母不为数字!</span>');
      maV = false;
    }
  }
}
function validateValidateCode(eleId){
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null){
    $('#'+eleId+'Check').html('<img src="cross.png"><span style="font-size: 12px;color:red;">验证码不能为空!</span>');
    vcV = false;
  }else{
    var vsMsg = verificationCheckCode(ele.val());
    if(vsMsg==true){
      $('#'+eleId+'Check').html('<img src="accept.png">');
      vcV = true;
    }else{
      $('#'+eleId+'Check').html('<img src="cross.png"><span style="font-size: 12px;color:red;">验证码错误!</span>');
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
