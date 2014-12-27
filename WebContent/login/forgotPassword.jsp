<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
  String path = request.getContextPath();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>找回密码</title>
<jsp:include page="/common/sysInclude.jsp" flush="true"/>
<link rel="stylesheet" type="text/css" href="<%=path%>/login/css/login.css"/>
</head>
<body>
<center>
  <div style="border:1px solid #ABCDEF;width:330px;height:400px;">
    <div style="margin-top:15px;margin-left:14px;" align="left"><span style="font-size:16px;color:#999999;">找回密码</span></div>
    <div style="height:1px;width:300px;border-top:1px solid  #999999;"></div>
    <div id="rstDiv" style="text-align:left;margin-left:75px;height:20px;padding-top:5px;"><span id="checkResult"></span></div>
    <form>
      <table width="300px;" style="margin-right:-35px;">
        <tr style="height:50px; valign:top;">
          <td align="right"><span class="loginspan">用户名</span></td>
          <td colspan="2" rowspan="1" style="text-align:left;">
          <div style="float:left">
            <input id="loginName" name="loginName" tabindex="1" style="color:#ABCDEF;" type="text" value="请填写用户名" onmouseover=this.focus();this.select();
              onclick="onClick(loginName);" onBlur="validateLoginName('loginName');"/></div>
          <div style="float:left;width:25px;height:25px;padding-top:8px;margin-left:-2px;" align="center" id='vLN'></div>
          </td>
        </tr>
        <tr><td colspan="3" align="center"></td></tr>
      </table>
      <div style="width:40px;margin-top:10px;"><a id="sendButton" onclick="sendBackPwdMail();" href="#">下一步</a></div>
      <div id="infoDiv" style="text-align:left;width:250px;margin-top:40px;margin-left:-6px;">
        <h2>重置密码流程：</h2>
        <span>1、填写用户名,系统校验用户名。</span><br/>
        <span>2、成功后点击下一步,将发送邮件至您的邮箱。</span><br/>
        <span>3、登录邮箱,根据提示重置密码。</span><br/>
      </div>
      <div style="width:50px;float:right;margin-right:45px;margin-top:50px;">
      	<a id="jumpLogin" onclick="jumpLogin();" href="#">返回登陆</a></div>
    </form>
  </div>
</center>
</body>
<script type="text/javascript">
var lnV =false;
function jumpLogin(){
  window.location.href="<%=path%>/login/login.jsp";
}
function setInputCss(){
  var browserType = getBrowserVersion();
  browserType = browserType.substring(0,browserType.lastIndexOf(' '));
  if(browserType!='msie'){
    $('#loginName').css({"line-height":"35px", "height":"35px", "padding-top":"0px"});
  }else{
    var ieVersion = browserType.substring(browserType.lastIndexOf(' '),browserType.length);
    if(ieVersion!=8.0){
      $('#loginName').css({"line-height":"35px", "height":"35px", "padding-top":"0px"});  
    }
  }
}
$(function(){
  setInputCss();
});
function sendBackPwdMail(){
  $('#lnImg').remove();
  $('#checkResult').html('');
  $('#sendButton').attr('disabled',true);
  $('#jumpLogin').attr('disabled',true);
  if(lnV){
    $('#loginName')
    var url = '<%=path%>/login/sendBackPwdMail.do';
    var pData = {
      loginName:$('#loginName').val()
    };
    $.ajax({type:"post", async:false, url:url, data:pData, dataType:"json",
      success:function(json) {
        if(json.success){
          $.messager.alert('提示',json.retInfo,'info');
        }else{
          $.messager.alert('提示',json.retInfo,'info');
        }
      }
    });
  }else{
    $.messager.alert('提示','您的账号填写有误!');
    $('#vLN').append('<img id="lnImg" align="middle" src="<%=path%>/login/images/cross.png">');
    $('#checkResult').html('<div style="width:370;font-size:12px;color:red;">&nbsp;&nbsp;&nbsp;&nbsp;登录名错误!</div>');
  }
  $('#sendButton').attr('disabled',false);
  $('#jumpLogin').attr('disabled',false);
}
function onClick(obj){
  if(obj.value==obj.defaultValue){
    obj.value='';obj.style.color='#000';
  }
}
function validateLoginName(eleId){
  $('#lnImg').remove();
  $('#checkResult').html('');
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null||ele.val()==ele[0].defaultValue){
    ele.val(ele[0].defaultValue);
    ele.css('color','#ABCDEF');
    lnV = false;
  }else{
    var vsMsg = checkLoginName(ele.val());
    if(vsMsg==true){
      $('#checkResult').html('<div style="width:370;font-size:12px;color:red;">&nbsp;&nbsp;&nbsp;&nbsp;你输入的用户名有误！</div>');
      $('#vLN').append('<img id="lnImg" align="middle" src="<%=path%>/login/images/cross.png">');
      lnV = false;
    }else{
      $('#vLN').append('<img id="lnImg" align="middle" src="<%=path%>/login/images/accept.png">');
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
     success:function(json) {
       vfMsg = json;
     }
  });
  return vfMsg;
}
</script>
</html>
