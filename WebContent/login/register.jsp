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
        <div class="alertInput-Text" style="margin-top:-4px;">
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
        <div class="alertInput-vCode" style="margin-top:1px;">
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
//此数组有5个元素，分别代表5个需要验证的输入框
//0-null，1-true，2-规则不对，3-已被占用(邮箱和账号验证的时候会用到，其他的用0-2即可)，
var vdInfoAry = new Array(5);
//用于判断是否可以提交
var lnV=false,maV=false,psV=false,cpsV=false,vcV=false;
/**
 * 主函数
 */
$(function() {
  initPageParam();
  initMask();//初始化遮罩

  inputEffect();//设置input效果
  commitOverOutEffect();//设置按钮效果，鼠标划过
  maskTitleOverOutEffect();//mask效果，鼠标划过
  initMailSuffix("<%=path%>/login/js/mailAdress.json");//邮件地址后缀设置

  setCorrectPosition();//设置正确的位置
  setTimeout(initMaskTitle, 100); //初始化maskTitle
});
//=以下初验证=============================================
// 初始化页面全局参数
function initPageParam(){
  mainPage = getMainPage();
  winId = getUrlParam(window.location.href, "_winID");
  win=getSWinInMain(winId);
}
//密码验证，验证密码是否足够复杂
function validatePassword() {
  var val = $("#password").val();
  //密码验证信息，
  var vdInfoP = 0;
  //重复密码验证信息
  var vdInfoCP = 0;
  if (val) {
    $("#password").parent().find(".alertImg").show();
    var confirmVal = $("#confirmPassword").val();
    if(!checkPasswordStr(val)){
      win.setMessage({'msg':'密码应由5~12位的字母、数字、下划线组成!'});
      $("#password").parent().find(".alertImg").css("background-image", "url(images/cross.png)");
      psV = false;
      vdInfoP = 2;
      //与确认码比较
      if (confirmVal) {
        $("#confirmPassword").parent().find(".alertImg").show();
        if (confirmVal==val) {
          $("#confirmPassword").parent().find(".alertImg").css("background-image", "url(images/accept.png)");
          cpsV=true;
          vdInfoCP = 1;
        } else {
          $("#confirmPassword").parent().find(".alertImg").css("background-image", "url(images/cross.png)");
          cpsV=false;
          vdInfoCP = 2;
        }
      }else vdInfoCP = 0;
    } else {
      //提示文字
      win.setMessage({'msg':''});
      //提示图标
      $("#password").parent().find(".alertImg").css("background-image", "url(images/accept.png)");
      psV = true;
      vdInfoP = 1;
      //与确认码比较
      if (confirmVal) {
        $("#confirmPassword").parent().find(".alertImg").show();
        if (confirmVal==val) {
          $("#confirmPassword").parent().find(".alertImg").css("background-image", "url(images/accept.png)");
          cpsV=true;
          vdInfoCP = 1;
        } else {
          win.setMessage({'msg':'确认密码与密码不一致!'});
          $("#confirmPassword").parent().find(".alertImg").css("background-image", "url(images/cross.png)");
          cpsV=false;
          vdInfoCP = 2;
        }
      }else vdInfoCP = 0;
    }
  } else {
    $("#password").parent().find(".alertImg").hide();
    psV = false;
    vdInfoP = 0;
  }
  vdInfoAry[2] = vdInfoP;
  vdInfoAry[3] = vdInfoCP;
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
  var vdInfoCP = 0;
  if (val) {
    var pass = $("#password").val();
    $("#confirmPassword").parent().find(".alertImg").show();
    if (val!=pass) {
      //提示文字
      win.setMessage({'msg':'确认密码与密码不一致!'});
      $("#confirmPassword").parent().find(".alertImg").css("background-image", "url(images/cross.png)");
      cpsV=false;
      vdInfoCP = 2;
    } else {
      win.setMessage({'msg':''});
      $("#confirmPassword").parent().find(".alertImg").css("background-image", "url(images/accept.png)");
      cpsV=true;
      vdInfoCP = 1;
    }
  } else {
    $("#confirmPassword").parent().find(".alertImg").hide();
    cpsV=false;
    vdInfoCP = 0;
  }
  vdInfoAry[3] = vdInfoCP;
}
/**
 * 账号验证
 */
function validateLoginName(){return;
  var val = $('#loginName').val();
  var vdInfoL = 0;
  if(val){
    $("#loginName").parent().find(".alertImg").show();
    if(checkLoginNameStr(val)){
      if(checkLoginName(val)){
        win.setMessage({'msg':''});
        $("#loginName").parent().find(".alertImg").css("background-image", "url(images/accept.png)");
        vdInfoL = 1;
        lnV = true;
      }else{
        win.setMessage({'msg':'该账号已被使用!'});
        $("#loginName").parent().find(".alertImg").css("background-image", "url(images/cross.png)");
        lnV = false;
        vdInfoL = 3;
      }
    }else{
      win.setMessage({'msg':'账号应由5~11位的字母、数字、下划线组成!'});
      $("#loginName").parent().find(".alertImg").css("background-image", "url(images/cross.png)");
      lnV = false;
      vdInfoL = 2;
    }
  }else{
    $("#loginName").parent().find(".alertImg").hide();
    lnV = false;
    vdInfoL = 0;
  }
  vdInfoAry[0] = vdInfoL;
  //验证账号是否符合规则
  function checkLoginNameStr(str){
    var re = /^[a-zA-z]\w{4,11}$/;
    if(re.test(str)) return true;
    else return false;
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
function validateMail(eleId,index){return;
  var a = $('#mailSel').combobox('getData');
  var vdInfoM = 0;
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
        vdInfoM = 1;
      }else{
        //提示文字
        win.setMessage({'msg':'该邮箱已被注册!'});
        //提示图标
        $("#mail").parent().parent().find(".alertImg").css("background-image", "url(images/cross.png)");
        maV = false;
        vdInfoM = 3;
      }
    }else{
      //提示文字
      win.setMessage({'msg':'不正确的邮箱格式!'});
      //提示图标
      $("#mail").parent().parent().find(".alertImg").css("background-image", "url(images/cross.png)");
      maV = false;
      vdInfoM = 2;
    }
  }else{
    $("#mail").parent().parent().find(".alertImg").hide();
    maV = false;
  }
  vdInfoAry[1] = vdInfoM;
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
// 验证码验证
function validateCheckCode(){
  var val = $('#checkCode').val();
  var vdInfoC = 0;
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
      vdInfoC = 1;
    }else{
      win.setMessage({'msg':'验证码填写错误!'});
      $("#checkCode").parent().parent().find(".alertImg").css("background-image", "url(images/cross.png)");
      vcV=false;
      vdInfoC = 2;
    }
  }else{
    $("#checkCode").parent().parent().find(".alertImg").hide();
    vcV=false;
  }
  vdInfoAry[4] = vdInfoC;
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
    var url="<%=path%>/login/commitButton.do";
    $.ajax({type:"post",async:false,url:url,data:pData,dataType:"json",
      success:function(json) {
        $("#mask").hide();
        if(json.success){
          if(mainPage) mainPage.$.messager.alert('注册提示',json.retInfo,'info',function(){closeSWinInMain(winId);});
          else $.messager.alert('注册提示',json.retInfo,'info',function(){window.location.href = "<%=path%>/asIndex.jsp";});
          $('#commitButton').attr("disabled",false); 
        }else $.messager.alert('提示',json.retInfo,'info',function(){$('#commitButton').attr("disabled",false);});
      }
    });
  }else{
    $('#commitButton').attr("disabled",false);
    var alertMessage = "您的";
    if(lnV==false) {
      if(vdInfoAry[0]==0) alertMessage = alertMessage + "账号未填写、";
      if(vdInfoAry[0]==2) alertMessage = alertMessage + "账号应由5~11位的字母、数字、下划线组成、";
      if(vdInfoAry[0]==3) alertMessage = alertMessage + "账号已被使用、";
    }
    if(maV==false){
      if(vdInfoAry[1]==0) alertMessage = alertMessage + "邮箱为填写、";
      if(vdInfoAry[1]==2) alertMessage = alertMessage + "邮箱格式不正确、";
      if(vdInfoAry[1]==3) alertMessage = alertMessage + "邮箱已被注册、";
    }
    if(psV==false){
      if(vdInfoAry[2]==0) alertMessage = alertMessage + "密码未填写、";
      if(vdInfoAry[2]==2) alertMessage = alertMessage + "密码应由5~12位的字母、数字、下划线组成、";
    } 
    if(cpsV==false){
      if(vdInfoAry[3]==0) alertMessage = alertMessage + "确认密码未填写、";
      if(vdInfoAry[3]==2) alertMessage = alertMessage + "确认密码与密码不一致、";
    }
    if(vcV==false){
      if(vdInfoAry[4]==0) alertMessage = alertMessage + "验证码未填写、";
      if(vdInfoAry[4]==2) alertMessage = alertMessage + "验证码不正确、";
    }
    alertMessage = alertMessage.substring(0,alertMessage.lastIndexOf("、"));
    mainPage.$.messager.alert('注册提示',alertMessage+"请检查！",'info',function () {
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
