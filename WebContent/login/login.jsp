<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
  String path = request.getContextPath();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>登录</title>
<jsp:include page="/common/sysInclude.jsp" flush="true"/>
<link rel="stylesheet" type="text/css" href="<%=path%>/login/css/login.css" />
<script type="text/javascript" src="<%=path %>/login/js/login.js"></script>
<style type="text/css">
#mainDiv {padding-top:30px;}
.labelTd{height:60px; line-height:60px;}
.commitBottonTd {height:90px;}
</style>
</head>
<body>
<object id="locator" classid="CLSID:76A64158-CB41-11D1-8B02-00600806D9B6" style="display:none;visibility:hidden"></object>
<object id="foo" classid="CLSID:75718C9A-F029-11d1-A1AC-00C04FB6C223" style="display:none;visibility:hidden"></object>
<form id="fooform" name="fooForm" style="display:none">
  <input type="hidden" name="txtMACAddr"/>
  <input type="hidden" name="txtIPAddr"/>
  <input type="hidden" name="txtDNSName"/>
</form>
<script>
var ignoreCheckCode = false; //是否忽略验证码. true-忽略验证码
var MACAddr;
var IPAddr;
var DomainAddr;
var sDNSName;
</script>

<script language="JScript" event="OnCompleted(hResult,pErrorObject, pAsyncContext)" for="foo">
fooForm.txtMACAddr.value=unescape(MACAddr);
fooForm.txtIPAddr.value=unescape(IPAddr);
fooForm.txtDNSName.value=unescape(sDNSName);
</script>
<script language="JScript" event="OnObjectReady(objObject,objAsyncContext)" for="foo">
if(objObject.IPEnabled != null && objObject.IPEnabled != "undefined" && objObject.IPEnabled == true ){
  if(objObject.MACAddress != null && objObject.MACAddress != "undefined" )MACAddr = objObject.MACAddress;
  if(objObject.IPEnabled && objObject.IPAddress(0 )!= null && objObject.IPAddress(0 )!= "undefined" )IPAddr = objObject.IPAddress(0);
  if(objObject.DNSHostName != null && objObject.DNSHostName != "undefined" )sDNSName = objObject.DNSHostName;
}
</script>
<!-- 遮罩层 -->
<div id="mask" style="border:1px;display:none; position:absolute;vertical-align:middle;text-align:center; align:center;">
  <img id="waittingImg" align="middle" src="<%=path%>/resources/images/waiting_circle.gif"/><br/><br/>
  <span id="waittingText" style="font-weight:bold;">请稍候，登录中...</span>
</div>
<center><div id="mainDiv">
  <form><table>
    <tr>
      <td class="labelTd">账　号</td>
      <td class="inputTd">
        <div class="alertInput-Text">
          <input class="alertInputComp" id="loginName" name="loginName" value="" tabindex="1" type="text" onBlur="validateLoginName();"/>
          <div class="maskTitle">请输入您的账号</div>
          <div class="alertImg"></div>
        </div>
      </td>
    </tr>
    <tr>
      <td class="labelTd">密　码</td>
      <td class="inputTd">
        <div class="alertInput-Text">
          <input id="password" class="alertInputComp" name="password" value="" tabindex="2" type="password" onBlur="validatePassword();"/>
          <div class="alertImg"></div>
          <div class="maskTitle">请输入密码</div>
        </div>
      </td>
    </tr>
    <tr>
      <td class="labelTd">验证码</td>
      <td class="inputTd">
        <div class="alertInput-vCode">
          <div id="vCodeInput"><input id="checkCode" class="alertInputComp" name="checkCode" tabindex="3" type="text" onBlur="validateCheckCode();"/></div>
          <div id="vCodeImg"><img id="vcimg" title="点击更换" onclick="javascript:refreshCCImg('<%=path %>');" src=""></div>
          <div class="alertImg"></div>
          <div class="maskTitle">输入验证码,见右图</div>
        </div>
      </td>
    </tr>
    <tr>
      <td colspan="2" class="commitBottonTd">
        <div id="commitButton" class="commitDiv" onclick="commit();">
          <span>登　录</span>
        </div>
      </td>
    </tr>
  </table></form>
  <div align="right" style="width:310px;margin-top:50px;margin-right:10px;">
    <a id="forgetPassword" onclick="forgetPassword();" href="#">忘记密码</a>&nbsp;|&nbsp;<a id="register" onclick="register();" href="#">注册</a><span id="delimiter">&nbsp;|&nbsp;</span><a id="activeUser" onclick="activeUser();" href="#">激活</a>
  </div>
</div></center>
</body>
<script type="text/javascript">
var win;
var mainPage;
var winId;
var checkCode="";
//用于记录未激活用户的信息
var userInfo = null;
//此数组有5个元素，分别代表5个需要验证的输入框
var vdInfoAry = ['账号为必填项','密码为必填项','验证码为必填项'];
var _userName=null, _ma=null;
/**
 * 主函数
 */
$(function() {
  initPageParam();//初始化全局参数
  initMask();//初始化遮罩

  inputEffect();//设置input效果，鼠标划过
  commitOverOutEffect();//设置按钮效果，鼠标划过
  maskTitleOverOutEffect();//mask效果，鼠标划过

  setCorrectPosition();//设置正确的位置
  setTimeout(initMaskTitle, 100);//初始化maskTitle
  refreshCCImg('<%=path%>');
});
//初始化页面全局参数
function initPageParam(){
  //打开窗口后获得焦点
  $('#loginName').focus();
  mainPage = getMainPage();
  winId = getUrlParam(window.location.href, "_winID");
  win=getSWinInMain(winId);
  $('#delimiter').css('display','none');
  $('#activeUser').css('display','none');
}
//=以下为验证=============================================
//验证密码是否为空
function validatePassword() {
  var val = $("#password").val();
  if (val) vdInfoAry[1] = "";
  else vdInfoAry[1] = "密码为必填项";
}
// 账号验证
function validateLoginName(){
  var val = $('#loginName').val();
  //验证loginName是否为空
  if (val) vdInfoAry[0] = "";
  else vdInfoAry[0] = "账号为必填项";
}
//验证码验证
function validateCheckCode(){
  var val = ($('#checkCode').val()).toUpperCase();
  if (val) {
    win.setMessage({'msg':''});
    vdInfoAry[2] = "";
    if(val!=checkCode){
      vdInfoAry[2] = "验证码填写错误";
    }
  }else{
    $("#checkCode").parent().parent().find(".alertImg").hide();
    vdInfoAry[2] = "验证码为必填项";
    //用于测试，等正式上线后需去掉！！！
    if(ignoreCheckCode){
      vdInfoAry[2] = "";
    }
  }
  ma = getMainAlert($("#checkCode"));
  ma.find(".alertImg").attr("title", vdInfoAry[2]);
}
//=以上为验证=============================================

//以下为页面跳转部分============
//跳转到注册页面
function register(){
  var winId = getUrlParam(window.location.href, "_winID");
  win.modify({title:"注册"});
  window.location.href="<%=path%>/login/register.jsp?_winID="+winId+"&_from=loginPage";
}
//未登录的忘记密码页面
function forgetPassword() {
//  showAlert('忘记密码', "请用您的注册邮箱给我公司QQ邮箱1794595752@qq.com发送您的帐号信息，我们会随后与您联系！", 'info');
//  return;
  var _url="<%=path%>/login/forgetPassword.jsp?modType=2";
  var winOption={
    url:_url,
    title:"忘记密码",
    height:'330',
    width:'330',
    modal:true
  };
  openSWinInMain(winOption);
}
//重新发送激活邮件到邮箱
function activeUser() {
  var _url="<%=path%>/login/activeUser.jsp?userName="+_userName+"&mailAddress="+_ma;
  var winOption={
    url:_url,
    title:"重发激活码",
    height:'330',
    width:'330',
    modal:true
  };
  openSWinInMain(winOption);
}
//以上为页面跳转部分============

//提交登录信息
function commit() {
  validatePassword();
  validateLoginName();
  validateCheckCode();
  var msgs = "";
  for (var i=0; i<vdInfoAry.length; i++) {
    if (vdInfoAry[i]&&vdInfoAry[i].length>0) msgs+="<br/>"+vdInfoAry[i]+"；";
  }
  if (msgs.length>0) {
    msgs = msgs.substr(5);
    msgs = "<div style='margin-left:40px;'>"+msgs+"</div>";
  }
  if (msgs.length>0) {
    showAlert('登录提示', msgs, 'info', function() {
      for (var i=0; i<vdInfoAry.length; i++) if (vdInfoAry[i]&&vdInfoAry[i].length>0) break;
      if (i==0) {
        $('#loginName')[0].focus();
        $('#loginName')[0].select();
      } else if (i==1) {
        $('#password')[0].focus();
        $('#password')[0].select();
      } else {
        $('#checkCode')[0].focus();
        $('#checkCode')[0].select();
      }
    });
  } else {
    var pData={
      "loginName":$("#loginName").val(),
      "password":$("#password").val(),
      "checkCode":$("#checkCode").val(),
      "clientMacAddr":fooForm.txtMACAddr.value?(fooForm.txtMACAddr.value=="undefined"?"":fooForm.txtMACAddr.value):"",
      "clientIp":fooForm.txtIPAddr.value?(fooForm.txtIPAddr.value=="undefined"?"":fooForm.txtIPAddr.value):"",
      "browser":getBrowserVersion()
    };
    $("#mask").show();
    login(pData);
  }
}

//用于提交
function login(pData) {
  $("#waittingText").html("登录中，请稍候...");
  var url="<%=path%>/login.do";
  $.ajax({type:"post", async:false, url:url, data:pData, dataType:"json",
    success:function(json){
      $("#mask").hide();
      refreshCCImg('<%=path%>');
      $('#checkCode').val('');
      var loginInfo = json.data;
      _userName=loginInfo.userLoginName;
      _ma=loginInfo.userMail;

      var retInfo = loginInfo.retInfo;
      var activeFlag = loginInfo.activeFlag;

      errMsg ='登录失败'+(retInfo?("："+retInfo):"！")+"<br/>请到所注册邮箱获得‘激活连接’，或点击右下角[激活]功能，重新发送激活邮件。";
      if (json.type==-1) {
        if (activeFlag==0) {//未激活
          refreshCCImg('<%=path%>');
          $('#checkCode').val('');
          showAlert('登录信息', errMsg, 'error');
//          $('#activeUser').show();
  //        $('#delimiter').show();
          $("#checkCode").val("");
          if (mainPage) mainPage.$("#mask").hide();
        } else　showAlert('登录信息', '登录失败'+(retInfo?("："+retInfo):"！"), 'error');
      } else if (json.type==1) {//登录成功
        if (activeFlag==0) {//未激活
          refreshCCImg('<%=path%>');
          $('#checkCode').val('');
          showAlert('登录信息', errMsg, 'error');
    //      $('#activeUser').show();
      //    $('#delimiter').show();
          $("#checkCode").val("");
          if (mainPage) mainPage.$("#mask").hide();
        }
        else if (activeFlag==1) {//已激活
          showAlert("登录信息", "登录成功！", "info", function() {
            if (mainPage) mainPage.setLogined(pData.loginName);
            else window.location.href = window.location.href;
            closeSWinInMain(winId);
          });
        }
      }
      else showAlert("登录信息", "登录失败："+json.data, "error");
    },
    error:function(errorData ){
      if (errorData ) {
        showAlert("登录信息", "登录异常：未知！", "error");
      }
    }
  });
}
</script>
</html>