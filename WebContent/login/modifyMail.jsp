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
<title>重置邮箱</title>
<jsp:include page="/common/sysInclude.jsp" flush="true"/>
<link rel="stylesheet" type="text/css" href="<%=path%>/login/css/login.css">
<script type="text/javascript" src="<%=path %>/login/js/login.js"></script>
</head>
<body>
<!-- 遮罩层 -->
<div id="mask" style="display:none; position:absolute;vertical-align:middle;text-align:center; align:center;">
  <img id="waittingImg" align="middle" src="<%=path%>/resources/images/waiting_circle.gif"/><br/><br/>
  <span id="waittingText" style="font-weight:bold;">请稍候，数据提交中...</span>
</div>
<center><div id="mainDiv">
  <form><table>
    <tr>
      <td class="labelTd">账　号</td>
      <td class="inputTd">
        <div class="alertInput-Text">
          <input class="alertInputComp" id="loginName" name="loginName" tabindex="1" type="text" disabled="disabled" /><input id="oldPwd" type="hidden" >
          <div class="maskTitle">请输入您的账号</div>
          <div class="alertImg"></div>
        </div>
      </td>
    </tr>
    <tr>
      <td class="labelTd">邮　箱</td>
      <td class="inputTd">
        <div class="alertInput-mail">
          <div id="mailPrefix"><input class="alertInputComp" id="mail" name="mail" tabindex="2" type="text" onBlur="validateMail()"/></div>
          <div id="mailSuffix"><input id="mailSel" name="mailSel"/></div>
          <div class="alertImg"></div>
          <div class="maskTitle">请输入您的邮箱</div>
        </div>
      </td>
    </tr>
    <tr>
      <td class="labelTd" style="height:35px; line-height:35px;">新密码</td>
      <td class="inputTd" style="height:35px; line-height:35px;">
        <div class="alertInput-Text">
          <input id="password" class="alertInputComp" name="password" tabindex="3" type="password" onBlur="validatePassword();"/>
          <div class="alertImg"></div>
          <div class="maskTitle">请输入新密码</div>
        </div>
      </td>
    </tr>
    <tr>
      <td class="labelTd" >确　认</td>
      <td class="inputTd" >
        <div class="alertInput-Text">
          <input id="confirmPassword" class="alertInputComp" name="confirmPassword" tabindex="4" type="password" onBlur="comfirmPassword();"/>
          <div class="alertImg"></div>
          <div class="maskTitle">请再次输入密码以确认</div>
        </div>
      </td>
    </tr>
    <tr>
      <td class="labelTd" >验证码</td>
      <td class="inputTd" >
        <div class="alertInput-vCode" style="margin-top:1px;">
          <div id="vCodeInput"><input id="checkCode" class="alertInputComp" name="checkCode" tabindex="5" type="text" onBlur="validateCheckCode();"/></div>
          <div id="vCodeImg"><img id="vcimg" title="点击更换" onclick="javascript:refresh('<%=path%>');" src=""></div>
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
var vdInfoAry = ['原密码不能为空','密码不能为空','确认密码不能为空','验证码不能为空'];
/**
 * 主函数
 */
$(function() {
  initPageParam();
  initMask();//初始化遮罩

  inputEffect();//设置input效果，鼠标划过
  commitOverOutEffect();//设置按钮效果，鼠标划过
  maskTitleOverOutEffect();//mask效果，鼠标划过
  initMailSuffix("<%=path%>/login/js/mailAdress.json");//邮件地址后缀设置

  setCorrectPosition();//设置正确的位置
  setTimeout(initMaskTitle, 100);//初始化maskTitle
  refresh('<%=path%>');
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

//=以下为验证=============================================
function validateOldPassword(){
  //oldPassword为用户输入的原密码，
  var val = $('#oldPassword').val();
  //oldPwd为session中密码
  var oldPwd = $('#oldPwd').val();
  $("#oldPassword").parent().find(".alertImg").show();
  if(val){
    if(val!=oldPwd){
      $("#oldPassword").parent().find(".alertImg").css("background-image", "url(images/cross.png)");
      vdInfoAry[0] = '原密码填写错误';
      win.setMessage({'msg':vdInfoAry[0]});
    }else {
      $("#oldPassword").parent().find(".alertImg").css("background-image", "url(images/accept.png)");
      vdInfoAry[0] = "";
      win.setMessage({'msg':''});
    }
  }else $("#oldPassword").parent().find(".alertImg").hide();
}
//验证邮箱
function validateMail() {
  var val = $('#mail').val();
  if(val){
    $("#mail").parent().parent().find(".alertImg").show();
    $("#mail").parent().parent().find(".alertImg").css("background-image", "url(images/accept.png)");
    win.setMessage({'msg':''});
    vdInfoAry[1] = "";
    var mailAdress = val;
    if (val.lastIndexOf('@')==-1) mailAdress = val+$('#mailSel').combobox('getText');

    if (!checkMailAdress(mailAdress)) {
      vdInfoAry[1] = "邮箱格式不正确";
      win.setMessage({'msg': vdInfoAry[1]});
      $("#mail").parent().parent().find(".alertImg").css("background-image", "url(images/cross.png)");
    }
  }else{
    $("#mail").parent().parent().find(".alertImg").hide();
    vdInfoAry[1] = "邮箱为必填项";
  }
  ma = getMainAlert($("#mail"));
  ma.find(".alertImg").attr("title", vdInfoAry[1]);
  //验证邮箱是否符合规则
  function checkMailAdress(mailAdress){
    var reg = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/; 
    return reg.test(mailAdress); 
  }
}
//密码验证，验证密码是否足够复杂
function validatePassword() {
  var val = $("#password").val();
  if (val) {
    $("#password").parent().find(".alertImg").show();
    $("#password").parent().find(".alertImg").css("background-image", "url(images/accept.png)");
    win.setMessage({'msg':''});
    vdInfoAry[1] = "";
    if (!checkPasswordStr(val)) {
      vdInfoAry[1] = "密码应由5~12位的字母、数字、下划线组成";
      win.setMessage({'msg':vdInfoAry[1]});
      $("#password").parent().find(".alertImg").css("background-image", "url(images/cross.png)");
    }
    comfirmPassword();
  } else {
    vdInfoAry[1] = "密码为必填项";
    $("#password").parent().find(".alertImg").hide();
  }
  ma = getMainAlert($("#password"));
  ma.find(".alertImg").attr("title", vdInfoAry[1]);
  //验证密码是否复合规则
  function checkPasswordStr(str) {
    var re = /[0-9a-zA-z]\w{4,11}$/;
    if(re.test(str)) return true;
    else return false;
  }
}
// 确认密码和密码是否一致
function comfirmPassword() {
  var val = $("#confirmPassword").val();
  if (val) {
    $("#confirmPassword").parent().find(".alertImg").show();
    $("#confirmPassword").parent().find(".alertImg").css("background-image", "url(images/accept.png)");
    win.setMessage({'msg':''});
    vdInfoAry[2] = "";
    var pass = $("#password").val();
    if (val!=pass) {
      vdInfoAry[2] = "确认密码与密码不一致";
      win.setMessage({'msg': vdInfoAry[2]});
      $("#confirmPassword").parent().find(".alertImg").css("background-image", "url(images/cross.png)");
    } else {
      win.setMessage({'msg':''});
      $("#confirmPassword").parent().find(".alertImg").css("background-image", "url(images/accept.png)");
    }
  } else {
    $("#confirmPassword").parent().find(".alertImg").hide();
    vdInfoAry[2] = "确认密码不能为空";
  }
  ma = getMainAlert($("#confirmPassword"));
  ma.find(".alertImg").attr("title", vdInfoAry[2]);
}

//验证码验证
function validateCheckCode(){
  var val = ($('#checkCode').val()).toUpperCase();
  if (val) {
    $("#checkCode").parent().parent().find(".alertImg").show();
    $("#checkCode").parent().find(".alertImg").css("background-image", "url(images/accept.png)");
    win.setMessage({'msg':''});
    vdInfoAry[3] = "";
    if(val!=checkCode){
      vdInfoAry[3] = "验证码填写错误";
      win.setMessage({'msg':vdInfoAry[3]});
      $("#checkCode").parent().parent().find(".alertImg").css("background-image", "url(images/cross.png)");
    }
  }else{
    $("#checkCode").parent().parent().find(".alertImg").hide();
    vdInfoAry[3] = "验证码为必填项";
  }
  ma = getMainAlert($("#checkCode"));
  ma.find(".alertImg").attr("title", vdInfoAry[3]);
}
//=以上为验证=============================================
//提交方法
function commit(){
  var msgs = "";
  for (var i=0; i<vdInfoAry.length; i++) {
    if (vdInfoAry[i]&&vdInfoAry[i].length>0) msgs+="<br/>"+vdInfoAry[i]+"；";
  }
  if (msgs.length>0) {
    msgs = msgs.substr(5);
    msgs = "<div style='margin-left:40px;'>"+msgs+"</div>";
  }
  if (msgs.length>0) {
    if(mainPage) mainPage.$.messager.alert('修改提示', msgs,'info',function(){
      for (var i=0; i<vdInfoAry.length; i++) {
        if (vdInfoAry[i]&&vdInfoAry[i].length>0) break;
      }
      if (i==0) {
        $('#oldPassword')[0].focus();
        $('#oldPassword')[0].select();
      }else if (i==1) {
        $('#password')[0].focus();
        $('#password')[0].select();
      }else if (i==2) {
        $('#confirmPassword')[0].focus();
        $('#confirmPassword')[0].select();
      } else {
        $('#checkCode')[0].focus();
        $('#checkCode')[0].select();
      }
    });
    else $.messager.alert('修改提示', msgs,'info',function(){
      for (var i=0; i<vdInfoAry.length; i++) {
        if (vdInfoAry[i]&&vdInfoAry[i].length>0) break;
      }
      if (i==0) {
        $('#oldPassword')[0].focus();
        $('#oldPassword')[0].select();
      }else if (i==1) {
        $('#password')[0].focus();
        $('#password')[0].select();
      }else if (i==2) {
        $('#confirmPassword')[0].focus();
        $('#confirmPassword')[0].select();
      } else {
        $('#checkCode')[0].focus();
        $('#checkCode')[0].select();
      }
    });
  } else {
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
           mainPage.$.messager.alert('修改提示',json.retInfo,'info',function(){closeSWinInMain(winId);});
         }else{
           $.messager.alert('修改提示',json.retInfo,'info');
           window.location.href = "<%=path%>/asIndex.jsp";
         }
       }else{
         if(mainPage) mainPage.$.messager.alert('提示',json.retInfo,'info');
         else $.messager.alert('提示',json.retInfo,'info');
       }
     }
   });
  }
}
</script>
</html>
