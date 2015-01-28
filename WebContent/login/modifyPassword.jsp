<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.spiritdata.framework.FConstants"%>
<%@page import="com.spiritdata.dataanal.UGA.pojo.User"%>
<%
  String path = request.getContextPath();
  String sid = request.getSession().getId();
  String modifyType = request.getParameter("modifyType");
  User user = ((User)session.getAttribute(FConstants.SESSION_USER));
  String loginName = "";
  loginName = request.getParameter("loginName");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>修改密码</title>
<jsp:include page="/common/sysInclude.jsp" flush="true"/>
<link rel="stylesheet" type="text/css" href="<%=path%>/login/css/login.css" />
<script type="text/javascript" src="<%=path %>/login/js/login.js"></script>
</head>
<body>
<center>
  <div style="width:330px;height:400px;">
    <div id="rstDiv" style="text-align:left;margin-left:86px;height:20px;padding-top:5px;"><span id="checkResult"></span></div>
    <form>
      <table width="300px;" style="margin-right:-5px;margin-top:5px;">
        <tr style="height:35px;valign:top;">
          <td align="right" width="56px;"><span class="loginspan">账　号</span></td>
          <td colspan="2" width="200px;" style="text-align:left;">
            <div style="float:left;">
              <input id="loginName" name="loginName" tabindex="1" type="text" value="账号" onmouseover=this.focus();this.select();
                onclick="onClick(loginName);" onBlur="validateLoginName('loginName');" />
            </div>
            <div style="float:left;width:25px;height:25px;padding-top:8px;margin-left:-2px;" align="center" id='vLN'></div>
          </td>
        </tr>
        <tr><td><div style="height:8px;width:5px;"></div></td></tr>
        <tr style="height:50px;valign:top;">
          <td align="right"><span class="loginspan">新密码</span></td>
          <td colspan="2" style="text-align:left;">
            <div style="float:left;">
              <input id="password" name="password" tabindex="2" type="password" onmouseover="pwdMouseOver();"
                onclick="onClick(password);" onBlur="validatePassword('password');"/>
            </div>
            <div style="float:left;width:25px;height:25px;padding-top:8px;margin-left:-2px;" align="center" id='vPwd'></div>
            <div id="pwDiv" style="float:left;width:40px;height:25px;padding-top:10px;margin-left:-223px;" align="center">
            <span id="pwdSpan" style="color:#ABCDEF;font-size:12px;">新密码</span></div>
          </td>
        </tr>
        <tr style="height:50px; valign:top;" >
          <td align="right"><span class="loginspan" style="font-size:12px;margin-right:-4px;">确认密码</span></td>
          <td colspan="2">
            <div style="float:left;">
              <input id="confirmPassword" name="confirmPassword" tabindex="4" type="password" onmouseover="cpwdMouseOver();"
                onclick="onClick(confirmPassword);" onBlur="validateConfirmPassword('confirmPassword');"/>
            </div>
            <div style="float:left;width:25px;height:25px;padding-top:8px;margin-left:-2px;" align="center" id='vCPwd'></div>
            <div id="cpwDiv" style="float:left;width:50px;height:25px;padding-top:10px;margin-left:-223px;" align="center">
              <span id="cpwdSpan" style="color:#ABCDEF;font-size:12px;">确认密码</span>
            </div>
          </td>
        </tr>
      </table>
    </form>
    <div style="margin-top:50px;width: 100%">
    <a id="saveButton"  onclick="modifyPassword();" href="#">
      <div id="modifyButton" style="background-image:url(images/bg.png);border-radius:5px">
        <span style="font-size:16px;color:#FFFFFF;font-weight: bold;">修改密码</span>
      </div>
    </a></div>
  </div>
</center>
</body>
<script type="text/javascript">
var mainPage = getMainPage(); 
var winId;
if(mainPage!=null) winId = mainPage.modifyWinId;
var win = getSWinInMain(winId);
var modifyType=<%=modifyType%>,psV=false,lnV=false,cpsV=false;
var loginName = '<%=loginName%>';
function pwdMouseOver(){
  $("#pwdSpan").toggleClass("addSelect");
}
function cpwdMouseOver(){
  $("#cpwdSpan").toggleClass("addSelect");
}
function modifyPassword(){
  $("#saveButton").attr('disabled',true);
  if(psV&&lnV&&cpsV){
    var pData = {
      loginName:$('#loginName').val(),
      password:$('#password').val()
    };
    var url = '<%=path%>/login/modifyPassword.do';
    $.ajax({type:"post", async:false, url:url, data:pData, dataType:"json",
      success:function(json){
        if(json){
          var mainPage = getMainPage();
          if(mainPage){
            closeSWinInMain(winId);
            mainPage.$.messager.alert('修改提示',json.retInfo,'info');
            cleanWinId(mainPage);
          }else{
            $.messager.alert('修改提示',json.retInfo,'info');
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
    if(lnV==false){
      $('#lnImg').remove();
      $('#vLN').append('<img id="lnImg" align="middle" src="images/cross.png">');
    }
    if(psV==false){
      $('#pwdImg').remove();
      $('#vPwd').append('<img id="pwdImg" align="middle" src="images/cross.png">');
    }
    if(cpsV==false){
      $('#cpwdImg').remove();
      $('#vCPwd').append('<img id="cpwdImg" align="middle" src="images/cross.png">');
    }
    mainPage.$.messager.alert("提示","您还有未完善的信息!",'info',function (){
      if(lnV==false){
        $('#loginName')[0].focus();
        $('#loginName')[0].select();
      }else if(psV==false){
        $('#password')[0].focus();
        $('#password')[0].select();
      }else if(cpsV=false){
        $('#confirmPassword')[0].focus();
        $('#confirmPassword')[0].select();
      }
    });
  }
  $("#saveButton").attr('disabled',false);
}
function setInputCss(){
  var browserType = getBrowserVersion();
  var v = browserType.substring(0,browserType.lastIndexOf(' '));
  if(v=='msie'){
    if($('#rstDiv')!=null) $('#rstDiv').css({"margin-left":"78px"});
    if($('#loginName')!=null) $('#loginName').css({"line-height":"35px", "height":"35px", "padding-top":"0px"});
    if($('#password')!=null) $('#password').css({"line-height":"35px", "height":"35px", "padding-top":"0px"});
    if($('#confirmPassword')!=null) $('#confirmPassword').css({"line-height":"35px", "height":"35px", "padding-top":"0px"});
    if($('#modifyButton')!=null) $('#modifyButton').css({"height":"28px","padding-top":"10px","margin-left":"-12px","width":"248px"});
  }else if(v=='chrome'){
    //var ieVersion = browserType.substring(browserType.lastIndexOf(' '),browserType.length);
    //if(ieVersion!=8.0){
      if($('#rstDiv')!=null) $('#rstDiv').css({"margin-left":"89px"});
      if($('#loginName')!=null) $('#loginName').css({"line-height":"35px", "height":"35px", "padding-top":"0px"});
      if($('#password')!=null) $('#password').css({"line-height":"35px", "height":"35px", "padding-top":"0px"});
      if($('#confirmPassword')!=null) $('#confirmPassword').css({"line-height":"35px", "height":"35px", "padding-top":"0px"});
      if($('#modifyButton')!=null) $('#modifyButton').css({"height":"28px","padding-top":"10px","margin-left":"-28px","width":"249px"});
    //}
  }else{
    if($('#rstDiv')!=null) $('#rstDiv').css({"margin-left":"89px"});
    if($('#loginName')!=null) $('#loginName').css({"line-height":"35px", "height":"35px", "padding-top":"0px"});
    if($('#password')!=null) $('#password').css({"line-height":"35px", "height":"35px", "padding-top":"0px"});
    if($('#confirmPassword')!=null) $('#confirmPassword').css({"line-height":"35px", "height":"35px", "padding-top":"0px"});
    if($('#modifyButton')!=null) $('#modifyButton').css({"height":"28px","padding-top":"10px","margin-left":"-34px","width":"250px"});
  }
}
//如果不是ie浏览器，从新初始化inputcsss
$(function(){
  initPwdInputCss('password','pwdSpan');
  initPwdInputCss('confirmPassword','cpwdSpan');
  setInputCss();
  //modifyType=1，为修改密码，为2，则是忘记密码
  if(modifyType==1){
    $('#loginName').val(loginName);
    $('#loginName').attr('disabled',true);
    lnV = true;
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
      $('#checkResult').html('<div style="width:370;font-size:12px;color:red;">密码不一致!</div>');
      win.setMessage({'msg':'&nbsp;&nbsp;密码不一致!'});
      $('#vCPwd').append('<img id="cpwdImg" align="middle" src="images/cross.png">');
      cpsV =false;
    }else{
      $('#vCPwd').append('<img id="cpwdImg" align="middle" src="images/accept.png">');
      win.setMessage({'msg':''});
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
      $('#checkResult').html('<div style="width:370;font-size:12px;color:red;">密码应是6~12位字母、数字、下划线!</div>');
      win.setMessage({'msg':'&nbsp;&nbsp;密码应是6~12位字母、数字、下划线!'});
      $('#vPwd').append('<img id="pwdImg" align="middle" src="images/cross.png">');
      psV = false;
    }else{
      $('#vPwd').append('<img id="pwdImg" align="middle" src="images/accept.png">');
      win.setMessage({'msg':''});
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
      $('#checkResult').html('<div style="width:370;font-size:12px;color:red;">该用户不存在!</div>');
      win.setMessage({'msg':'&nbsp;&nbsp;该用户不存在!'});
      $('#vLN').append('<img id="lnImg" align="middle" src="images/cross.png">');
      lnV = false;
    }else{
      $('#vLN').append('<img id="lnImg" align="middle" src="images/accept.png">');
      win.setMessage({'msg':''});
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
    success:function(json){
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
