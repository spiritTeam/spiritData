<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.gmteam.framework.FConstants"%>
<%@page import="com.gmteam.spiritdata.UGA.pojo.User"%>
<%
  String path = request.getContextPath();
  String sid = request.getSession().getId();
  String modType = request.getParameter("modType");
  User user = ((User)session.getAttribute(FConstants.SESSION_USER));
  String loginName = "";
  if (user!=null) loginName = user.getLoginName();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>修改密码</title>
<jsp:include page="/common/sysInclude.jsp" flush="true"/>
<link rel="stylesheet" type="text/css" href="<%=path%>/login/css/login.css" />
<script type="text/javascript" src="<%=path %>/login/js/login.js"></script>
<style type="text/css">
#loginName{padding-top:8px; height:25px; font-size:14px;}
#password{padding-top:8px; height:25px; font-size:14px;}
#checkCode{width: 115px; padding-top:8px; height:25px; font-size:14px;}
#confirmPassword{width: 115px; padding-top:8px; height:25px; font-size:14px;}
</style>
</head>
<body>
<center>
  <div style="border:1px solid #ABCDEF;width:330px;height:400px;">
    <div style="margin-top: 15px; margin-left: 15px;"align="left"><span style="font-size: 16px;color: #999999;">修改密码</span></div>
    <div style="height:1px; width:300px;border-top: 1px solid  #999999;"></div>
    <div id="rstDiv" style="text-align: left;margin-left: 86px;height: 20px;padding-top: 5px;"><span id="checkResult"></span></div>
    <form action="">
      <table width="300px;" style="margin-right:-35px;">
        <tr style="height:35px; valign:top;">
          <td align="right"><span class="myspan">账　号</span></td>
          <td colspan="2" rowspan="1" width="197px;" style="text-align:left;">
          <div style="float: left;">
            <input id="loginName"  name="loginName"  tabindex="1" type="text"  value="账号" onmouseover=this.focus();this.select();
                onclick="onClick(loginName);" onBlur="validateLoginName('loginName');" />
          </div>
          <div style="float: left;width: 25px;height: 25px;padding-top: 8px;margin-left: -2px;" align="center" id='vLN'></div>
          </td>
        </tr>
        <tr><td><div style="height: 8px;width: 5px;"></div></td></tr>
        <tr style="height:50px; valign:top;">
          <td align="right"><span class="myspan">新密码</span></td>
          <td colspan="2" style="text-align:left;">
	          <div style="float: left;">
	            <input style="width:197px;" id="password" name="password"  tabindex="2" type="password" value="" onselect="" onmouseover="pwdMouseOver();"
	                onclick="onClick(password);" onBlur="validatePassword('password');" /></div>
	          <div style="float: left;width: 25px;height: 25px;padding-top: 8px;margin-left: -2px;" align="center" id='vPwd'></div>
	          <div id="pwDiv" style="float: left;width: 25px;height: 25px;padding-top: 10px;margin-left: -225px;" align="center" >
              <span id="pwdSpan" style="color: #ABCDEF;font-size: 12px;">密码</span></div>
          </td>
        </tr>
        <tr style="height:50px; valign:top;" >
          <td align="right"><span class="myspan" style="font-size: 12px;padding-right: 0px;">确认密码</span></td>
          <td colspan="2">
            <div style="float: left;">
              <input style="width:197px;" id="confirmPassword" name="confirmPassword"  tabindex="4" type="password" onmouseover="cpwdMouseOver();"
                onclick="onClick(confirmPassword);" onBlur="validateConfirmPassword('confirmPassword');" /></div>
            <div style="float: left;width: 25px;height: 25px;padding-top: 8px;margin-left: -2px;" align="center" id='vCPwd'></div>
            <div id="cpwDiv" style="float: left;width: 50px;height: 25px;padding-top: 10px;margin-left: -225px;" align="center" >
              <span id="cpwdSpan" style="color: #ABCDEF;font-size: 12px;">确认密码</span></div>
          </td></tr>
        <tr><td colspan="3" align="right"><input id="saveButton" style="margin-top: 50px;margin-right: 20px;" type="button" value="确认修改" onclick="modPwd();"/></td></tr>
      </table>
    </form>
  </div>
</center>
</body>
<script type="text/javascript">
var modType=<%=modType%>,psV=false,lnV=false,cpsV=false;
var loginName = '<%=loginName%>';
function pwdMouseOver(){
  $("#pwdSpan").toggleClass("addSelect");
}
function cpwdMouseOver(){
  $("#cpwdSpan").toggleClass("addSelect");
}
function modPwd(){
	$("#saveButton").attr('disabled',true);
  if(psV&&lnV&&cpsV){
    var pData = {
      loginName:$('#loginName').val(),
      password:$('#password').val()
    };
    var url = '<%=path%>/login/modifyPwd.do';
    $.ajax({type:"post", async:false, url:url, data:pData, dataType:"json",
      success: function(json) {
        if(json) $.messager.alert('提示','修改成功!');
        else $.messager.alert('提示','修改失败!');
      }
    });
  }else{
    $.messager.alert("提示","您还有未完善的信息!");
  }
  $("#saveButton").attr('disabled',false);
}
//如果不是ie浏览器，从新初始化inputcsss
function setInputCss() {
  var browserType = getBrowserVersion();
  browserType = browserType.substring(0,browserType.lastIndexOf(' '));
  if(browserType!='msie'){
    $('#loginName').css({"line-height":"35px", "height":"35px", "padding-top":"0px"});
    $('#password').css({"line-height":"35px", "height":"35px", "padding-top":"0px"});
    $('#confirmPassword').css({"line-height":"35px", "height":"35px", "padding-top":"0px"});
  }
}
$(function(){
	initPwdInputCss('password','pwdSpan');
	//initCPwdInputCss();
	initPwdInputCss('confirmPassword','cpwdSpan');
  setInputCss();
  if(modType==1){
    $('#loginName').val(loginName);
    $('#loginName').attr('disabled',true);
    lnV = true;
  }else if(modType==2){
    //$('#loginName').attr(validateLoginName('loginName'));
  }
  if($('#loginName').val()==$('#loginName')[0].defaultValue){
    $('#loginName').css('color','#ABCDEF');
  }
  if($('#password').val()==$('#password')[0].defaultValue){
    $('#password').css('color','#ABCDEF');
  }
});
function validateConfirmPassword(eleId){
	$('#checkResult').html('');
	$('#cpwdImg').remove();
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null||ele.val()==ele[0].defaultValue){
    cpsV =false;
  }else{
    if($('#password').val()!=ele.val()){
      $('#checkResult').html('<div style="width:370;font-size: 12px;color:red;">密码不一致!</div>');
      $('#vCpwd').append('<img id="cpwdImg" align="middle" src="img/cross.png">');
      cpsV =false;
    }else{
      $('#vCpwd').append('<img id="cpwdImg" align="middle" src="img/accept.png">');
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
function validatePassword(eleId){
	$('#checkResult').html('');
	$('#pwdImg').remove();
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null||ele.val()==ele[0].defaultValue){
    psV = false;
  }else{
    if(!checkPasswordStr(ele.val())){
      $('#checkResult').html('<div style="width:370;font-size: 12px;color:red;">密码应由6~12位的字母、数字、下划线组成!</div>');
      $('#vPwd').append('<img id="pwdImg" align="middle" src="img/cross.png">');
      psV = false;
    }else{
    	$('#vPwd').append('<img id="pwdImg" align="middle" src="img/accept.png">');
      psV = true;
    }
  }
}
function validateLoginName(eleId){
	$('#checkResult').html('');
	$('#lnImg').remove();
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null||ele.val()==ele[0].defaultValue){
    ele.val(ele[0].defaultValue);
    ele.css('color','#ABCDEF');
    lnV = false;
  }else{
    var vsMsg = checkLoginName(ele.val());
    if(vsMsg==true){
      $('#checkResult').html('<div style="width:370;font-size: 12px;color:red;">账号错误!</div>');
      $('#vLN').append('<img id="lnImg" align="middle" src="img/cross.png">');
      lnV = false;
    }else{
      $('#vLN').append('<img id="lnImg" align="middle" src="img/accept.png">');
      lnV = true;
    }
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
