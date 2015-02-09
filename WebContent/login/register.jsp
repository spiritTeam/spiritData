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
  //初始化验证数组
  for(var i=0;i<vdInfoAry.length;i++){
    var vdInfo = new Object();
    vdInfo.vd = false;
    vdInfo.message = "";
    vdInfoAry[i] = vdInfo; 
  }
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
      vdInfoAry[2].message = "密码应由5~12位的字母、数字、下划线组成、";
      //与确认码比较
      if (confirmVal) {
        $("#confirmPassword").parent().find(".alertImg").show();
        if (confirmVal==val) {
          $("#confirmPassword").parent().find(".alertImg").css("background-image", "url(images/accept.png)");
          vdInfoAry[3].vd = true;
        } else {
          $("#confirmPassword").parent().find(".alertImg").css("background-image", "url(images/cross.png)");
          vdInfoAry[3].message = "确认密码与密码不一致、";
        }
      }else vdInfoAry[3].message = "确认密码未必填项、";
    } else {
      //提示文字
      win.setMessage({'msg':''});
      //提示图标
      $("#password").parent().find(".alertImg").css("background-image", "url(images/accept.png)");
      vdInfoAry[2].vd = true;
      //与确认码比较
      if (confirmVal) {
        $("#confirmPassword").parent().find(".alertImg").show();
        if (confirmVal==val) {
          $("#confirmPassword").parent().find(".alertImg").css("background-image", "url(images/accept.png)");
          vdInfoAry[3].vd = true;
        } else {
          win.setMessage({'msg':'确认密码与密码不一致!'});
          $("#confirmPassword").parent().find(".alertImg").css("background-image", "url(images/cross.png)");
          vdInfoAry[3].message = "确认密码与密码不一致、";
        }
      }else vdInfoAry[3].message = "确认密码未必填项、";
    }
  } else {
    $("#password").parent().find(".alertImg").hide();
    vdInfoAry[2].vd = false;
    vdInfoAry[2].message = "密码为必填项、";
  }
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
    var pass = $("#password").val();
    $("#confirmPassword").parent().find(".alertImg").show();
    if (val!=pass) {
      //提示文字
      win.setMessage({'msg':'确认密码与密码不一致!'});
      $("#confirmPassword").parent().find(".alertImg").css("background-image", "url(images/cross.png)");
      vdInfoAry[3].message = "重复密码与密码不一致、";
    } else {
      win.setMessage({'msg':''});
      $("#confirmPassword").parent().find(".alertImg").css("background-image", "url(images/accept.png)");
      vdInfoAry[3].vd = true;
    }
  } else {
    $("#confirmPassword").parent().find(".alertImg").hide();
    vdInfoAry[3].message = "重复密码不能为空、";
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
        vdInfoAry[0].vd = true;
      }else{
        win.setMessage({'msg':'该账号已被使用!'});
        $("#loginName").parent().find(".alertImg").css("background-image", "url(images/cross.png)");
        vdInfoAry[0].message = "该账号已被使用、";
      }
    }else{
      win.setMessage({'msg':'账号应由5~11位的字母、数字、下划线组成!'});
      $("#loginName").parent().find(".alertImg").css("background-image", "url(images/cross.png)");
      vdInfoAry[0].message = "账号应由5~11位的字母、数字、下划线组成、";
    }
  }else{
    $("#loginName").parent().find(".alertImg").hide();
    vdInfoAry[0].message = "账号为必填项、";
  }
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
        vdInfoAry[1].vd = true;
      }else{
        //提示文字
        win.setMessage({'msg':'该邮箱已被注册!'});
        //提示图标
        $("#mail").parent().parent().find(".alertImg").css("background-image", "url(images/cross.png)");
        vdInfoAry[1].message = "该邮箱已被注册、";
      }
    }else{
      //提示文字
      win.setMessage({'msg':'不正确的邮箱格式!'});
      //提示图标
      $("#mail").parent().parent().find(".alertImg").css("background-image", "url(images/cross.png)");
      vdInfoAry[1].message = "邮箱格式不正确、";
    }
  }else{
    $("#mail").parent().parent().find(".alertImg").hide();
    vdInfoAry[1].message = "邮箱为必填项、";
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
// 验证码验证
function validateCheckCode(){
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
      vdInfoAry[4].vd = true;
    }else{
      win.setMessage({'msg':'验证码填写错误!'});
      $("#checkCode").parent().parent().find(".alertImg").css("background-image", "url(images/cross.png)");
      vdInfoAry[4].message = "验证码填写错误、";
    }
  }else{
    $("#checkCode").parent().parent().find(".alertImg").hide();
    vdInfoAry[4].message = "验证码为必填项、";
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
  if(vdInfoAry[0].vd&&vdInfoAry[1].vd&&vdInfoAry[2].vd&&vdInfoAry[3].vd&&vdInfoAry[4].vd){
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
    if(vdInfoAry[0].vd==false) {
      alertMessage = alertMessage + vdInfoAry[0].message;
    }
    if(vdInfoAry[1].vd==false){
      alertMessage = alertMessage + vdInfoAry[1].message;
    }
    if(vdInfoAry[2].vd==false){
      alertMessage = alertMessage + vdInfoAry[2].message;
    } 
    if(vdInfoAry[3].vd==false){
      alertMessage = alertMessage + vdInfoAry[3].message;
    }
    if(vdInfoAry[4].vd==false){
      alertMessage = alertMessage + vdInfoAry[4].message;
    }
    alertMessage = alertMessage.substring(0,alertMessage.lastIndexOf("、"));
    mainPage.$.messager.alert('注册提示',alertMessage+"请检查！",'info',function () {
      if(vdInfoAry[0].vd==false){
        $('#loginName')[0].focus();
        $('#loginName')[0].select();
      }else if(vdInfoAry[1].vd=false){
        $('#mail')[0].focus();
        $('#mail')[0].select();
      }else if(vdInfoAry[2].vd==false){
        $('#password')[0].focus();
        $('#password')[0].select();
      }else if(vdInfoAry[3].vd=false){
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
