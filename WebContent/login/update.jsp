<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.spiritdata.framework.FConstants"%>
<%@page import="com.spiritdata.dataanal.UGA.pojo.User"%>
<%
  String path = request.getContextPath();
  String sid = request.getSession().getId();
  String modifyType = request.getParameter("modifyType");
  User user = ((User)session.getAttribute(FConstants.SESSION_USER));
  String userMail;
  String loginName;
  String oldPwd;
  if(user==null||user.equals("")){
      userMail = "";
      loginName = "";
      oldPwd = "";
  }else{
      userMail = user.getMailAdress();
      loginName = user.getLoginName();
      oldPwd = user.getPassword();
  }
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>修改</title>
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
    <tr>
      <td class="labelTd">账　号</td>
      <td class="inputTd">
        <div class="alertInput-Text">
          <input class="alertInputComp" id="loginName" name="loginName" tabindex="1" type="text" disabled="disabled"/><input id="oldPwd" type="hidden" >
          <div class="maskTitle">请输入您的账号</div>
          <div class="alertImg"></div>
        </div>
      </td>
    </tr>
    <tr>
      <td class="labelTd">邮　箱</td>
      <td class="inputTd">
        <div class="alertInput-Text">
          <input class="alertInputComp" id="mail" name="mail" tabindex="2" type="text" disabled="disabled"/>
        </div>
      </td>
    </tr>
    <tr>
      <td class="labelTd">原密码</td>
      <td class="inputTd">
        <div class="alertInput-Text" style="margin-top:1px;">
          <input id="oldPassword" class="alertInputComp" name="oldPassword" tabindex="3" type="password" onBlur="validateOldPassword();"/>
          <div class="alertImg"></div>
          <div class="maskTitle">请输入原密码</div>
        </div>
      </td>
    </tr>
    <tr>
      <td class="labelTd" style="height:35px; line-height:35px;">新密码</td>
      <td class="inputTd" style="height:35px; line-height:35px;">
        <div class="alertInput-Text">
          <input id="password" class="alertInputComp" name="password" tabindex="4" type="password" onBlur="validatePassword();"/>
          <div class="alertImg"></div>
          <div class="maskTitle">请输入新密码</div>
        </div>
      </td>
    </tr>
    <tr>
      <td class="labelTd" >确　认</td>
      <td class="inputTd" >
        <div class="alertInput-Text">
          <input id="confirmPassword" class="alertInputComp" name="confirmPassword" tabindex="5" type="password" onBlur="comfirmPassword();"/>
          <div class="alertImg"></div>
          <div class="maskTitle">请再次输入密码以确认</div>
        </div>
      </td>
    </tr>
    <tr>
      <td class="labelTd" >验证码</td>
      <td class="inputTd" >
        <div class="alertInput-vCode" style="margin-top:1px;">
          <div id="vCodeInput"><input id="checkCode" class="alertInputComp" name="checkCode" tabindex="6" type="text" onBlur="validateCheckCode();"/></div>
          <div id="vCodeImg"><img id="vcimg" title="点击更换" onclick="javascript:refresh(this);" src="<%=path%>/login/getValidateCode.do"></div>
          <div class="alertImg"></div>
          <div class="maskTitle">按右图输入验证码</div>
        </div>
      </td>
    </tr>
    <tr>
      <td colspan="2" class="commitBottonTd">
        <div id="commitButton" class="commitDiv" onclick="commit();">
          <span>提　交</span>
        </div>
      </td>
    </tr>
  </table></form>
</div></center>
</body>
<script type="text/javascript">
var win;
var mainPage;
var winId;
//各个input的变量，true代表有值，且符合标准，false则相反
var psV=false,cpsV=false,opsV=false,vcV=false;
/**
 * 主函数
 */
$(function() {
  initPageParam();

  inputEffect();//设置input效果，鼠标划过
  commitOverOutEffect();//设置按钮效果，鼠标划过
  maskTitleOverOutEffect();//mask效果，鼠标划过

  setCorrectPosition();//设置正确的位置
  setTimeout(initMaskTitle, 100); //初始化maskTitle
});
//=以下初始化设置=============================================
// 初始化页面全局参数
function initPageParam(){
  mainPage = getMainPage();
  winId = getUrlParam(window.location.href, "_winID");
  win=getSWinInMain(winId);
  $('#loginName').val('<%=loginName%>');
  $('#mail').val('<%=userMail%>');
  $('#oldPwd').val('<%=oldPwd%>');
}
//=以上初始化设置=============================================

//=以下初验证=============================================
function validateOldPassword(){
  //oldPassword为用户输入的原密码，
  var val = $('#oldPassword').val();
  //oldPwd为session中密码
  var oldPwd = $('#oldPwd').val();
  $("#oldPassword").parent().find(".alertImg").show();
  if(val){
    if(val!=oldPwd){
      $("#oldPassword").parent().find(".alertImg").css("background-image", "url(images/cross.png)");
      win.setMessage({'msg':'原密码填写错误!'});
      opsV = false;
    }else {
      $("#oldPassword").parent().find(".alertImg").css("background-image", "url(images/accept.png)");
      win.setMessage({'msg':''});
      opsV = true;
    }
  }else $("#oldPassword").parent().find(".alertImg").hide();
}
//密码验证，验证密码是否足够复杂
function validatePassword() {
  var val = $("#password").val();
  if (val) {
    $("#password").parent().find(".alertImg").show();
    var confirmVal = $("#confirmPassword").val();
    if(!checkPasswordStr(val)){
      win.setMessage({'msg':'密码应由5~12位的字母、数字、下划线组成!'});
      $("#password").parent().find(".alertImg").css("background-image", "url(images/cross.png)");
      psV = false;
      //与确认码比较
      if (confirmVal) {
        $("#confirmPassword").parent().find(".alertImg").show();
        if (confirmVal==val) {
          $("#confirmPassword").parent().find(".alertImg").css("background-image", "url(images/accept.png)");
          cpsV=true;
        } else {
          $("#confirmPassword").parent().find(".alertImg").css("background-image", "url(images/cross.png)");
          cpsV=false;
        }
      }
    } else {
      //提示文字
      win.setMessage({'msg':''});
      //提示图标
      $("#password").parent().find(".alertImg").css("background-image", "url(images/accept.png)");
      psV = true;
      //与确认码比较
      if (confirmVal) {
        $("#confirmPassword").parent().find(".alertImg").show();
        if (confirmVal==val) {
          $("#confirmPassword").parent().find(".alertImg").css("background-image", "url(images/accept.png)");
          cpsV=true;
        } else {
          win.setMessage({'msg':'确认密码与密码不一致!'});
          $("#confirmPassword").parent().find(".alertImg").css("background-image", "url(images/cross.png)");
          cpsV=false;
        }
      }
    }
  } else {
    $("#password").parent().find(".alertImg").hide();
    psV = false;
  }
  //验证密码是否复合规则
  function checkPasswordStr(str) {
    var re = /[0-9a-zA-z]\w{4,11}$/;
    if(re.test(str)){
      return true;
    }else{
      return false;
    }       
  }
}
/*
 * 确认密码和密码是否一致
 */
function comfirmPassword() {
  var val = $("#confirmPassword").val();
  if (val) {
    var pass = $("#password").val();
    $("#confirmPassword").parent().find(".alertImg").show();
    if (val!=pass) {
      win.setMessage({'msg':'确认密码与密码不一致!'});
      $("#confirmPassword").parent().find(".alertImg").css("background-image", "url(images/cross.png)");
      cpsV=false;
    } else {
      win.setMessage({'msg':''});
      $("#confirmPassword").parent().find(".alertImg").css("background-image", "url(images/accept.png)");
      cpsV=true;
    }
  } else {
    $("#confirmPassword").parent().find(".alertImg").hide();
    cpsV=true;
  }
}

/**
 * 验证码验证
 */
function validateCheckCode(eleId){
  var val = $('#checkCode').val();
  if(val){
    $("#checkCode").parent().parent().find(".alertImg").show();
    var vMsg =null;
    var pData={
      "checkCode":val
    };
    var url="<%=path%>/login/validateValidateCode.do";
    $.ajax({type:"post",async:false,url:url,data:pData,dataType:"json",
      success:function(json) {
        vMsg = json;
      }
    });
    if(vMsg){
      win.setMessage({'msg':''});
      $("#checkCode").parent().parent().find(".alertImg").css("background-image", "url(images/accept.png)");
      vcV=true;
    }else{
      win.setMessage({'msg':'验证码填写错误!'});
      $("#checkCode").parent().parent().find(".alertImg").css("background-image", "url(images/cross.png)");
      vcV=false;
    }
  }else{
    $("#checkCode").parent().parent().find(".alertImg").hide();
    vcV=false;
  }
}
//=以上初验证=============================================
//刷新验证码
function refresh(obj) {
  obj.src = "<%=path%>/login/getValidateCode.do?"+Math.random();
  $('#checkCode').val('');
}
//提交方法
function commit(){
  $('#commitButton').attr("disabled",true); 
  if(opsV&&psV&&cpsV){
    var pData={
      "loginName":$("#loginName").val(),
      "password":$("#password").val(),
      "userName":$("#userName").val(),
      "mailAdress":$("#mail").val()
    };
    $("#mask").show();
    var _url="<%=path%>/login/update.do";
    $.ajax({type:"post",async:false,url:_url,data:pData,dataType:"json",
      success:function(json) {
        $("#mask").hide();
        if(json.success){
          if(mainPage){
            mainPage.$.messager.alert('注册提示',json.retInfo,'info',function(){closeSWinInMain(winId);});
          }else{
            $.messager.alert('注册提示',json.retInfo,'info');
            window.location.href = "<%=path%>/asIndex.jsp";
          }
        }else{
          if(mainPage) mainPage.$.messager.alert('提示',json.retInfo,'info');
          else $.messager.alert('提示',json.retInfo,'info');
        }
        $('#commitButton').attr("disabled",false);
      }
    });
  }else{
    $('#commitButton').attr("disabled",false);
    var alterMessage = "您的";
    if(opsV==false){
      //$('#oldPassword').parent().find(".alertImg").css("background-image", "url(images/cross.png)");
      if($('#oldPassword').val()) alterMessage = alterMessage + "原密码填写有误、";
      else alterMessage = alterMessage + "原密码还未填写、";
    }
    if(psV==false){
      //$("#password").parent().find(".alertImg").css("background-image", "url(images/cross.png)");
      if($('#password').val()) alterMessage = alterMessage + "密码应由5~12位的字母、数字、下划线组成!、";
      else alterMessage = alterMessage + "原密码还未填写、";
    } 
    if(cpsV==false){
      //$('#confirmPassword').parent().find(".alertImg").css("background-image", "url(images/cross.png)");
      if($('#confirmPassword').val()) alterMessage = alterMessage + "密码与确认密码不一致、";
      else alterMessage = alterMessage + "确认密码还未填写、";
    }
    if(vcV==false){
      //$("#checkCode").parent().parent().find(".alertImg").css("background-image", "url(images/cross.png)");
      if($('#checkCode').val()) alterMessage = alterMessage + "验证码错误、";
      else alterMessage = alterMessage + "验证码还未填写、";
    }
    alterMessage = alterMessage.substring(0,alterMessage.lastIndexOf("、"));
    $.messager.alert('注册提示',alterMessage+"请检查!",'info',function () {
      if(psV==false){
        $('#password')[0].focus();
        $('#password')[0].select();
      }else if(cpsV=false){
        $('#confirmPassword')[0].focus();
        $('#confirmPassword')[0].select();
      }
    });
  }
}
</script>
</html>
