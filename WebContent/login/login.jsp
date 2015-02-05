<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
  String path = request.getContextPath();
  String sid = request.getSession().getId();
  String uT = request.getParameter("uT");
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
//var service = locator.ConnectServer();
var MACAddr;
var IPAddr;
var DomainAddr;
var sDNSName;
//uT=1,表示激活成功过来的账号，uT=2表示修改密码成功后跳转的，ut=3表示正常的跳转的
//uTMessage表示要提示的信息
var uT=<%=uT%>,uTMessage = "";
if(uT==1){
  uTMessage = "激活成功!";
  $.messager.alert('提示',uTMessage,'info');
}else if(uT==2) {
  uTMessage = "修改密码成功!";
  $.messager.alert('提示',uTMessage,'info');
}

//service.Security_.ImpersonationLevel=3;
//service.InstancesOfAsync(foo, 'Win32_NetworkAdapterConfiguration');
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
  <span id="waittingText" style="font-weight:bold;" id="maskTitle">请稍候，登录中...</span>
</div>
<center><div id="mainDiv">
  <form><table>
    <tr>
      <td class="labelTd">账　号</td>
      <td class="inputTd">
        <div class="alertInput-Text">
          <input class="alertInputComp" id="loginName" name="loginName" tabindex="1" type="text" onBlur="validateLoginName();"/>
          <div class="maskTitle">请输入您的账号</div>
          <div class="alertImg"></div>
        </div>
      </td>
    </tr>
    <tr>
      <td class="labelTd">密　码</td>
      <td class="inputTd">
        <div class="alertInput-Text">
          <input id="password" class="alertInputComp" name="password" tabindex="2" type="password" onBlur="validatePassword();"/>
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
          <div id="vCodeImg"><img id="vcimg" title="点击更换" onclick="javascript:refresh(this);" src="<%=path%>/login/getValidateCode.do"></div>
          <div class="alertImg"></div>
          <div class="maskTitle">按右图输入验证码</div>
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
    <a onclick="toRegister()" href="#">没有账号?</a>&nbsp;|&nbsp;
    <a onclick="activeUserAgain()" href="#">激活</a>&nbsp;|&nbsp;
    <a onclick="modifyPassword()" href="#">忘记密码?</a>
  </div>
</div></center>
</body>
<script type="text/javascript">
var win;
var mainPage;
var winId;
//用于判断是否可以提交
var lnV=false,psV=false,vcV=false;
/**
 * 主函数
 */
$(function() {
  mainPage = getMainPage();
  winId = getUrlParam(window.location.href, "_winID");
  win=getSWinInMain(winId);

  inputOverOutEffect();//设置input效果，鼠标划过
  commitOverOutEffect();//设置按钮效果，鼠标划过
  maskTitleOverOutEffect();//mask效果，鼠标划过

  setCorrectPosition();//设置正确的位置
  setTimeout(initMaskTitle, 100); //初始化maskTitle
});
//=以下初始化设置=============================================
//设置input效果，鼠标划过
function inputOverOutEffect() {
  //对focus处理,使tab隐藏maskTitle
  $(".alertInputComp").bind('focus',function(){
    var mainAlert=$(this).parent();
    if (!$(mainAlert).attr("class")||$(mainAlert).attr("class").indexOf("alertInput-Text")!=-1) mainAlert=$(mainAlert).parent();
    mainAlert.find(".maskTitle").hide();
  });
  //对blur处理,使tab隐藏maskTitle
  $(".alertInputComp").bind('blur',function(){
    var mainAlert=$(this).parent();
    if (!$(mainAlert).attr("class")||$(mainAlert).attr("class").indexOf("alertInput-Text")!=-1) mainAlert=$(mainAlert).parent();
    mainAlert.find(".maskTitle").show();
  });
  $(".alertInputComp").bind('mouseover',function(){
    $(this).focus();
    $(this)[0].select();
    var width = parseFloat($(this).css("width"));
    var height = parseFloat($(this).css("height"));
    var lineheight = parseFloat($(this).css("line-height"));
    var paddingLeft = parseFloat($(this).css("padding-left"));

    $(this).css({"width":(width-1)+"px", "height":(height-2)+"px", "border": "2px #ABCDEF solid"});
    if (lineheight) $(this).css({"line-height":(lineheight-2)+"px"});
    if (paddingLeft) $(this).css({"padding-left":(paddingLeft-1)+"px"});
    var mainAlert=$(this).parent();
    if (!$(mainAlert).attr("class")||$(mainAlert).attr("class").indexOf("alertInput-Text")!=-1) mainAlert=$(mainAlert).parent();
    $(mainAlert).find(".maskTitle").hide();
  }).bind('mouseout',function(){
    var width = parseFloat($(this).css("width"));
    var height = parseFloat($(this).css("height"));
    var lineheight = parseFloat($(this).css("line-height"));
    var paddingLeft = parseFloat($(this).css("padding-left"));

    $(this).css({"width":(width+1)+"px", "height":(height+2)+"px", "border": "1px #ABADB3 solid"});
    if (lineheight) $(this).css({"line-height":(lineheight+2)+"px"});
    if (paddingLeft) $(this).css({"padding-left":(paddingLeft+1)+"px"});
    if (!$(this).val()) {
      var mainAlert=$(this).parent();
      if (!$(mainAlert).attr("class")||$(mainAlert).attr("class").indexOf("alertInput-Text")!=-1) mainAlert=$(mainAlert).parent();
      $(mainAlert).find(".maskTitle").show();
    }
  });
}
//设置按钮效果，鼠标划过
function commitOverOutEffect() {
  $("#commitButton").bind('mouseover',function(){
    $(this).css({"background-color":"#81FC6A"});
    $(this).find("span").css("color", "yellow");
  }).bind('mouseout',function(){
    $(this).css({"background-image":"url(images/bg.png)"});
    $(this).find("span").css("color", "#fff");
  });
}
//mask效果，鼠标划过
function maskTitleOverOutEffect() {
  $(".maskTitle").bind('mouseover',function(){
    $(this).hide();
  });
}
//设置提示图标位置，根据不同的控件
function setCorrectPosition() {
  //1-提示图标
  var imgList = $(".alertImg");
  var i=0, len=imgList.length;
  for (;i<len; i++) {
    var top = $(imgList[i]).parent().offset().top;
    var left = $(imgList[i]).parent().offset().left;
    var pWidth = parseFloat($(imgList[i]).parent().css("width"));
    $(imgList[i]).css("top", (top-(parseFloat($(imgList[i]).css("height"))/2)));
    $(imgList[i]).css("left", (left+pWidth-(parseFloat($(imgList[i]).css("width"))/2)));
  }
  //2-提示文字
  var maskTitleList = $(".maskTitle");
  var i=0, len=maskTitleList.length;
  for (;i<len; i++) {
    var top = $(maskTitleList[i]).parent().offset().top;
    var left = $(maskTitleList[i]).parent().offset().left;
    $(maskTitleList[i]).css("top", top);
    $(maskTitleList[i]).css("left", left);
  }
}
//初始化maskTitle
function initMaskTitle() {
  var flagCompList = $(".alertInputComp");
  var i=0, len=flagCompList.length;
  for (;i<len; i++) {
    var _this = flagCompList[i];
    if ($(_this).val()&&$(_this).val().trim()!="") {
      var mainAlert=$(_this).parent();
      if (!$(mainAlert).attr("class")||$(mainAlert).attr("class").indexOf("alertInput-Text")!=-1) mainAlert=$(mainAlert).parent();
      mainAlert.find(".maskTitle").hide();
    }
  }
}
//=以上初始化设置=============================================

//=以下初验证=============================================
//验证密码是否为空
function validatePassword() {
  $("#password").parent().find(".alertImg").show();
  var val = $("#password").val();
  if(val){
    psV = true;
    $("#password").parent().find(".alertImg").css("background-image", "url(images/accept.png)");
  }else{
    psV = false;
    $("#password").parent().find(".alertImg").hide();
  }
}
/**
 * 账号验证
 */
function validateLoginName(){
  var val = $('#loginName').val();
  $("#loginName").parent().find(".alertImg").show();
  //验证loginName是否为空
  if(val){
    lnV = true;
    $("#loginName").parent().find(".alertImg").css("background-image", "url(images/accept.png)");
  }else {
    lnV = false;
    $("#loginName").parent().find(".alertImg").hide();
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
      win.setMessage({'msg':'&nbsp;&nbsp;验证码填写错误!'});
      $("#checkCode").parent().parent().find(".alertImg").css("background-image", "url(images/cross.png)");
      vcV=false;
    }
  }else{
    $("#checkCode").parent().parent().find(".alertImg").hide();
    vcV=false;
  }
}
//=以上初验证=============================================

//以下为页面跳转部分============
//跳转到注册页面
function toRegister(){
  var winId = getUrlParam(window.location.href, "_winID");
  var win = getSWinInMain(winId);
  win.modify({title:"注册"});
  window.location.href="<%=path%>/login/register.jsp?_winID="+winId;
}
//从新发送激活邮件到邮箱
function activeUserAgain(){
  var url="<%=path%>/login/activeUserAgain.do";
  var loginName = $("#loginName").val();
  if(loginName){
    mainPage.$.messager.alert('提示信息',"您必须填写账号，以便于向您的绑定邮箱发送验证!",'info');
    return;
  }else{
    var pData={"loginName":$("#loginName").val()};
    $.ajax({type:"post", async:false, url:url, data:pData, dataType:"json",
      success:function(json ){
        if(json.success==true){
          mainPage.$.messager.alert('提示信息',json.retInfo,'info');
        }else{
          mainPage.$.messager.alert('提示信息',json.retInfo,'info');
        }
      }
    });
  }
}
//未登录的忘记密码页面
function modifyPassword(){
  win.modify({title:"修改密码"});
  window.location.href="<%=path%>/login/forgetPassword.jsp?modType=2";
}
//以上为页面跳转部分============

//刷新验证码
function refresh(obj) {
  obj.src = "<%=path%>/login/getValidateCode.do?"+Math.random();
  $('#checkCode').val('');
}

//提交登陆信息
function commit(){
  $('#commitButton').attr('disabled',false);
  if(psV&&lnV&&vcV){
    $("#mask").show();
    var url="<%=path%>/login.do";
    var pData={
      "loginName":$("#loginName").val(),
      "password":$("#password").val(),
      "checkCode":$("#checkCode").val(),
      "clientMacAddr":fooForm.txtMACAddr.value?(fooForm.txtMACAddr.value=="undefined"?"":fooForm.txtMACAddr.value):"",
      "clientIp":fooForm.txtIPAddr.value?(fooForm.txtIPAddr.value=="undefined"?"":fooForm.txtIPAddr.value):"",
      "browser":getBrowserVersion()
    };
    $.ajax({type:"post", async:false, url:url, data:pData, dataType:"json",
      success:function(json){
        $("#mask").hide();
        $('#commitButton').attr('disabled',false);
        $('#checkCode').val('');
        $('#vcimg')[0].src = "<%=path%>/login/getValidateCode.do?"+Math.random();
        var loginInfo = json.data;
        var retInfo = loginInfo.retInfo;
        if(json.type==-1){
          $.messager.alert('登录信息',retInfo,'info');
        }else if (json.type==1){
          var activeType = loginInfo.activeType;
          if(activeType==1){
            $.messager.alert('登录信息',retInfo,'info');
          }else if(activeType==2){
            if(mainPage) {
              var loginStatus = mainPage.document.getElementById("loginStatus");
              var loginName = mainPage.document.getElementById("loginName");
              $(loginStatus).val(1);
              $(loginName).val(pData.loginName);
              closeSWinInMain(winId);
              mainPage.$.messager.alert("登陆信息","登陆成功！",'info');
              cleanWinId();
            }else{
              $.messager.alert("登陆信息","登陆成功！",'info');
              window.location.href = "<%=path%>/asIndex.jsp";
            }
          }
        } else if(json.type==2 ){
          mainPage.$.messager.alert("登录信息", "登录失败："+json.data, "error");
        } else {
          mainPage.$.messager.alert("登录信息", "登录异常："+json.data, "error");
        }
      },
      error:function(errorData ){
        $('#commitButton').attr('disabled',false);
        $('#checkCode').val('');
        $('#vcimg')[0].src = "<%=path%>/login/getValidateCode.do?"+Math.random();
        if (errorData ){
          mainPage.$.messager.alert("登录信息", "登录异常：未知！", "error");
        } else {
          mainPage.$("#mask").hide();
        }
      }
    });
  }else{
    $('#commitButton').attr('disabled',false);
    if(lnV==false ){
      $('#lNImg').remove();
      $('#vLN').append('<img id="lNImg" align="middle" src="images/cross.png">');
    }
    if(psV==false){
      $('#pWImg').remove();
      $('#vPW').append('<img id="pWImg" align="middle" src="images/cross.png">');
    } 
    if(vcV==false){
      $('#vCImg').remove();
      $('#vVC').append('<img id="vCImg" align="middle" src="images/cross.png">');
    }
    mainPage.$.messager.alert("登录提示","您的登录信息某些地方有误，请完善您的登陆信息", 'info',function (){
      if(lnV==false){
        $('#loginName')[0].focus();
        $('#loginName')[0].select();
      }else if(psV==false){
        $('#password')[0].focus();
        $('#password')[0].select();
      }else{
        $('#checkCode')[0].focus();
        $('#checkCode')[0].select();
      }
    });
  }
}
</script>
</html>
