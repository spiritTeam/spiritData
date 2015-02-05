<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
  String path = request.getContextPath();
  String sid = request.getSession().getId();
  String pWinId = request.getParameter("pWinId");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>注册</title>
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
          <input class="alertInputComp" id="loginName" name="loginName" tabindex="1" type="text" onBlur="validateLoginName();"/>
          <div class="maskTitle">请输入您的账号</div>
          <div class="alertImg"></div>
        </div>
      </td>
    </tr>
    <tr>
      <td class="labelTd">邮　箱</td>
      <td class="inputTd">
        <div class="alertInput-mail">
          <div id="mailPrefix"><input class="alertInputComp" id="mail" name="mail" tabindex="2" type="text" onBlur="validateMail('mail')"/></div>
          <div id="mailSuffix"><input id="mailSel" name="mailSel"/></div>
          <div class="alertImg"></div>
          <div class="maskTitle">请输入您的邮箱</div>
        </div>
      </td>
    </tr>
    <tr>
      <td class="labelTd">密　码</td>
      <td class="inputTd">
        <div class="alertInput-Text">
          <input id="password" class="alertInputComp" name="password" tabindex="3" type="password" onBlur="validatePassword();"/>
          <div class="alertImg"></div>
          <div class="maskTitle">请输入密码</div>
        </div>
      </td>
    </tr>
    <tr>
      <td class="labelTd" style="height:35px; line-height:35px;">确　认</td>
      <td class="inputTd" style="height:35px; line-height:35px;">
        <div class="alertInput-Text">
          <input id="confirmPassword" class="alertInputComp" name="confirmPassword" tabindex="4" type="password" onBlur="comfirmPassword();"/>
          <div class="alertImg"></div>
          <div class="maskTitle">请再次输入密码以确认</div>
        </div>
      </td>
    </tr>
    <tr>
      <td class="labelTd" style="height:64px; line-height:64px;">验证码</td>
      <td class="inputTd" style="height:64px; line-height:64px;">
        <div class="alertInput-vCode">
          <div id="vCodeInput"><input id="checkCode" class="alertInputComp" name="checkCode" tabindex="5" type="text" onBlur="validateCheckCode();"/></div>
          <div id="vCodeImg"><img id="vcimg" title="点击更换" onclick="javascript:refresh(this);" src="<%=path%>/login/getValidateCode.do"></div>
          <div class="alertImg"></div>
          <div class="maskTitle">按右图输入验证码</div>
        </div>
      </td>
    </tr>
    <tr>
      <td colspan="2" class="commitBottonTd">
        <div id="commitButton" class="commitDiv" onclick="commit();">
          <span>注　册</span>
        </div>
      </td>
    </tr>
  </table></form>
</div></center>
</body>
<script type="text/javascript">
//win
var win;
var mainPage;
var winId;
//用于判断是否可以提交
var lnV=false,maV=false,psV=false,cpsV=false,vcV=false;
/**
 * 主函数
 */
$(function() {
  mainPage = getMainPage();
  winId = getUrlParam(window.location.href, "_winID");
  win=getSWinInMain(winId);

  initMailSuffix();//邮件地址后缀设置
  inputEffect();//设置input效果，鼠标划过
  commitOverOutEffect();//设置按钮效果，鼠标划过
  maskTitleOverOutEffect();//mask效果，鼠标划过

  setCorrectPosition();//设置正确的位置
  setTimeout(initMaskTitle, 100); //初始化maskTitle
});

//=以下初验证=============================================
//密码验证，验证密码是否足够复杂
function validatePassword() {
  var val = $("#password").val();
  if (val) {
    $("#password").parent().find(".alertImg").show();
    var confirmVal = $("#confirmPassword").val();
    if(!checkPasswordStr(val)){
      win.setMessage({'msg':'&nbsp;&nbsp;密码应是5~12位的字母、数字、下划线!'});
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
          win.setMessage({'msg':'&nbsp;&nbsp;确认密码与密码不一致!'});
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
      //提示文字
      win.setMessage({'msg':'&nbsp;&nbsp;确认密码与密码不一致!'});
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
 * 账号验证
 */
function validateLoginName(){
  var val = $('#loginName').val();
  if(val){
    $("#loginName").parent().find(".alertImg").show();
    if(checkLoginNameStr(val)){
      if(checkLoginName(val)){
        win.setMessage({'msg':''});
        $("#loginName").parent().find(".alertImg").css("background-image", "url(images/accept.png)");
        lnV = true;
      }else{
        win.setMessage({'msg':'&nbsp;&nbsp;该账号已被使用!'});
        $("#loginName").parent().find(".alertImg").css("background-image", "url(images/cross.png)");
        lnV = false;
      }
    }else{
      win.setMessage({'msg':'&nbsp;&nbsp;账号应为5~11位的字母、数字、下划线组成!'});
      $("#loginName").parent().find(".alertImg").css("background-image", "url(images/cross.png)");
      lnV = false;
    }
  }else{
    $("#loginName").parent().find(".alertImg").hide();
    lnV = false;
  }
  //验证账号是否符合规则
  function checkLoginNameStr(str){
    var re = /^[a-zA-z]\w{4,11}$/;
    if(re.test(str)){
      return true;
    }else{
      return false;
    }       
  }
  //后台验证账号是否重复
  function checkLoginName(str){
    var vfMsg =null;
    var pData={"loginName":str};
    var url="<%=path%>/login/validateLoginName.do";
    $.ajax({type:"post",async:false,url:url,data:pData,dataType:"json",
       success:function(json) {
         vfMsg = json;
       }
    });
    return vfMsg;
  }
}
/**
 * 验证邮箱
 */
function validateMail(eleId,index){
  var a = $('#mailSel').combobox('getData');
  var val = $('#mail').val();
  if(val){
    var mailStr;
    $("#mail").parent().parent().find(".alertImg").show();
    if(val.lastIndexOf('@')!=-1) mailStr = val;
    else{
      if(index!=null) mailStr = val +a[index-1].text;
      else mailStr = val +$('#mailSel').combobox('getText');
    }
    if(checkMailStr(mailStr)){
      if(checkMail(mailStr)){
        win.setMessage({'msg':''});
        $("#mail").parent().parent().find(".alertImg").css("background-image", "url(images/accept.png)");
        maV = true;
      }else{
        //提示文字
        win.setMessage({'msg':'&nbsp;&nbsp;该邮箱已被注册!'});
        //提示图标
        $("#mail").parent().parent().find(".alertImg").css("background-image", "url(images/cross.png)");
        maV = false;
      }
    }else{
      //提示文字
      win.setMessage({'msg':'&nbsp;&nbsp;不正确的邮箱格式!'});
      //提示图标
      $("#mail").parent().parent().find(".alertImg").css("background-image", "url(images/cross.png)");
      maV = false;
    }
  }else{
    $("#mail").parent().parent().find(".alertImg").hide();
    maV = false;
  }
  //验证邮箱是否符合规则
  function checkMailStr(str){
    var reg = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/; 
    return reg.test(str); 
  }
  //验证邮箱是否已经注册
  function checkMail(str){
    var vfMsg =null;
    var pData={
      "mail":str
    };
    var url="<%=path%>/login/validateMail.do";
    $.ajax({type:"post",async:false,url:url,data:pData,dataType:"json",
      success:function(json) {
        vfMsg = json;
      }
    });
    return vfMsg;
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

//刷新验证码
function refresh(obj) {
  obj.src = "<%=path%>/login/getValidateCode.do?"+Math.random();
  $('#checkCode').val('');
}

//提交注册信息
function commit(){
  $('#vcimg')[0].src = "<%=path%>/login/getValidateCode.do?"+Math.random();
  $('#checkCode').val('');
  if(psV&&cpsV&&lnV&&maV&&vcV){
    var mailAdress = $("#mail").val();
    if(mailAdress.lastIndexOf("@")==-1) mailAdress = mailAdress+$('#mailSel').combobox('getText');
    var pData={
      "loginName":$("#loginName").val(),
      "password":$("#password").val(),
      "userName":$("#userName").val(),
      "mailAdress":mailAdress,
    };
    $("#mask").show();
    var url="<%=path%>/login/register.do";
    $.ajax({type:"post",async:false,url:url,data:pData,dataType:"json",
      success:function(json) {
        $("#mask").hide();
        if(json.success){
          if(mainPage){
            closeSWinInMain(winId);
            mainPage.$.messager.alert('注册提示',json.retInfo,'info');
            cleanWinId(mainPage);
          }else{
            $.messager.alert('注册提示',json.retInfo,'info');
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
    $('#register').attr("disabled",false); 
    if(lnV==false) {
      $("#loginName").parent().find(".alertImg").css("background-image", "url(images/cross.png)");
      $("#loginName").parent().find(".alertImg").show();
    }
    if(cpsV==false){
      $("#confirmPassword").parent().find(".alertImg").css("background-image", "url(images/cross.png)");
      $("#confirmPassword").parent().find(".alertImg").show();
    }
    if(maV==false){
      $("#mail").parent().parent().find(".alertImg").css("background-image", "url(images/cross.png)");
      $("#mail").parent().parent().find(".alertImg").show();
    }
    if(psV==false){
      $("#password").parent().find(".alertImg").css("background-image", "url(images/cross.png)");
      $("#password").parent().find(".alertImg").show();
    } 
    if(vcV==false){
      $("#checkCode").parent().parent().find(".alertImg").css("background-image", "url(images/cross.png)");
      $("#checkCode").parent().parent().show();
    } 
    mainPage.$.messager.alert('注册提示',"您的注册信息某些地方有误，请完善您的注册信息",'info',function () {
      if(lnV==false){
        $('#loginName')[0].focus();
        $('#loginName')[0].select();
      }else if(maV=false){
        $('#mail')[0].focus();
        $('#mail')[0].select();
      }else if(psV==false){
        $('#password')[0].focus();
        $('#password')[0].select();
      }else if(cpsV=false){
        $('#confirmPassword')[0].focus();
        $('#confirmPassword')[0].select();
      }else{
        $('#checkCode')[0].focus();
        $('#checkCode')[0].select();
      }
    });
  }
}

</script>
</html>
