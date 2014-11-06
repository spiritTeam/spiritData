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
      <table width="480px;" >
        <tr>
          <td align="right" width="100px;" ><span style="width: 150px;font-size: 20px;">登录名:</span></td>
          <td colspan="2" width="230px;" >
          <input style="width:230px;height:35px;color:#999;font-size: 20px;" class="input_1 required" id="loginName" name="loginName"  tabindex="1" type="text" value="" onmouseover=this.focus();this.select();
                onclick="onClick(loginName);" onBlur="onBlur(loginName);"/>
          </td><td width="150px;"><div id="loginNameCheck"></div></td></tr>
         <tr><td ><div style="height: 20px;"></div></td></tr>
          <tr>
          <td align="right"><span style="width: 150px;font-size: 20px;">用户名:</span></td>
          <td colspan="2" rowspan="1">
          <input style="width:230px;height:35px;color:#999;font-size: 20px;" class="input_1 required" id="userName" name="userName"  tabindex="1" type="text" value="" onmouseover=this.focus();this.select();
                onclick="onClick(userName);" onBlur="onBlur(userName);"/>
          </td><td><div id="userNameCheck"></div></td></tr>
        <tr><td ><div style="height: 20px;"></div></td></tr>
        <tr >
          <td align="right"><span style="width: 150px;font-size: 20px;">密码:</span></td>
          <td colspan="2" rowspan="1">
            <input style="width:230px;height:35px;color:#999;font-size: 20px" class="input_1 required" id="password" name="password"  tabindex="2" type="text" value="" onmouseover=this.focus();this.select();
                onclick="onClick(password);" onBlur="onBlur(password);" />
          </td><td><div id="passwordCheck"></div></td></tr>
          <tr><td ><div style="height: 20px;"></div></td></tr>
        <tr >
          <td align="right"><span style="width: 150px;font-size: 20px;">确认密码:</span></td>
          <td colspan="2" rowspan="1">
            <input style="width:230px;height:35px;color:#999;font-size: 20px" class="input_1 required" id="confirmPassword" name="confirmPassword"  tabindex="2" type="text" value="" onmouseover=this.focus();this.select();
                onclick="onClick(confirmPassword);" onBlur="onBlur(confirmPassword);" />
          </td><td><div id="confirmPasswordCheck"></div></td></tr>
        <tr><td ><div style="height: 20px;"></div></td></tr>
          <tr>
            <td align="right"><span style="width: 150px;font-size: 20px;">邮箱:</span></td>
            <td colspan="1" width="130px;"><input style="width:130px;height:35px;color:#999;font-size: 20px;"  id="mail" name="mail"  tabindex="3" type="text"  onmouseover=this.focus();this.select(); onclick="onClick(mail);" onBlur="onBlur(mail);"/></td>
            <td  align="left"><input style="width: 100px;height: 30px;font-size: 20px;" id="mailEndStr" name="mailEndStr" ></td>
            <td><div id="mailCheck"></div></td>
          </tr>
              <tr><td ><div style="height: 20px;"></div></td></tr>
          <tr>
          <td align="right"><span style="width: 150px;font-size: 20px;">验证码:</span></td>
          <td colspan="1" width="130px;"><input style="width:130px;height:35px;color:#999;font-size: 20px;" class="input_1 required" id="checkCode" name="checkCode"  tabindex="3" type="text" value="请输入验证码"
            onmouseover=this.focus();this.select(); onclick="onClick(checkCode);" onBlur="onBlur(checkCode);" /></td>
              <td  align="left"><img style="width: 100px;" title="点击更换" onclick="javascript:refresh(this);" src="check.do"></td><td><div id="checkCodeCheck"></div></td></tr>
          <tr><td ><div style="height: 20px;"></div></td><td></td><td></td></tr>
          <tr><td colspan="4" align="center" ><img id="register" name="register" src="register.png" onclick="saveRegisterInfo();"></td></tr>
      </table>
    </form>
  </div>
  <div style="float: right;border:1px solid #ABCDEF;width: 296px;height: 500px;">
  </div>
 </div>
</center>
</body>
<script type="text/javascript">
function checkVal(obj){
	obj.focus();obj.select();
	if(""+obj=="checkCode"){
		alert(true);
	}
}
function saveRegisterInfo(){
	alert(111);
}
$(function() {
	$('#mailEndStr').combobox({    
    url:'mailEndStr.json',    
    valueField:'id',    
    textField:'text'   
	});  
});
function refresh(obj) {
  obj.src = "check.do?"+Math.random();
}
function onBlur(obj){
  if(obj.value==""){
    obj.value=obj.defaultValue;
    obj.style.color='#999';
  }
}
function onClick(obj){
  if(obj.value==obj.defaultValue){
    obj.value='';obj.style.color='#000';
  }
}
function loginF() {
    var url="<%=path%>/login.do";
    var pData={
      "loginName":$("#loginName").val(),
      "password":$("#password").val(),
      "checkCode":$("#checkCode").val(),
      "browser":getBrowserVersion()
    };
    $.ajax({type:"post", async:false, url:url, data:pData, dataType:"json",
      success: function(json) {
        if (json.type==1) {
          alert("loginOk");
          return;
        } else if (json.type==2) {
          $.messager.alert("错误", "登录失败："+json.data, "error", function(){
            $("#loginname").focus();
            $("#mask").hide();
            //setBodyEnter(true);
          });
        } else {
          $.messager.alert("错误", "登录异常："+json.data, "error", function(){
            $("#loginname").focus();
            $("#mask").hide();
            setBodyEnter(true);
          });
        }
      },
      error: function(errorData) {
        if (errorData) {
          $.messager.alert("错误", "登录异常：未知！", "error", function(){
            $("#loginname").focus();
            $("#mask").hide();
            setBodyEnter(true);
          });
        } else {
          $("#mask").hide();
          setBodyEnter(true);
        }
      }
    });
  }
</script>
</html>