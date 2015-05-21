<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.spiritdata.framework.FConstants"%>
<%@page import="com.spiritdata.dataanal.UGA.pojo.User"%>
<%
  String path = request.getContextPath();
  String modifyType = request.getParameter("modifyType");
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
      <td class="labelTd">新密码</td>
      <td class="inputTd">
        <div class="alertInput-Text">
          <input id="password" class="alertInputComp" name="password" tabindex="4" type="password" onBlur="validatePassword();"/>
          <div class="alertImg"></div>
          <div class="maskTitle">请输入新密码</div>
        </div>
      </td>
    </tr>
    <tr>
      <td class="labelTd" style="height:35px; line-height:35px;">确　认</td>
      <td class="inputTd" style="height:35px; line-height:35px;">
        <div class="alertInput-Text">
          <input id="confirmPassword" class="alertInputComp" name="confirmPassword" tabindex="5" type="password" onBlur="comfirmPassword();"/>
          <div class="alertImg"></div>
          <div class="maskTitle">请再次输入密码以确认</div>
        </div>
      </td>
    </tr>
    <tr>
      <td colspan="2" class="commitBottonTd">
        <div id="commitButton" style="margin-top: 30px;" class="commitDiv" onclick="commit();">
          <span>修改密码</span>
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
var vdInfoAry = ['密码不能为空','确认密码不能为空'];
/**
 * 主函数
 */
$(function() {
  initMask();
  initPageParam();//初始化页面全局参数

  inputEffect();//设置input效果，鼠标划过
  commitOverOutEffect();//设置按钮效果，鼠标划过
  maskTitleOverOutEffect();//mask效果，鼠标划过

  setCorrectPosition();//设置正确的位置
  setTimeout(initMaskTitle, 100);//初始化maskTitle
});

//=以下初始化设置=============================================
// 初始化页面全局参数
function initPageParam(){
  mainPage = getMainPage();
  winId = getUrlParam(window.location.href, "_winID");
  win=getSWinInMain(winId);
  var modifyType = <%=modifyType%>;
  var loginName = '<%=loginName%>';
  if(modifyType==1){
    $('#loginName').val(loginName);
    $('#loginName').attr('disabled',true);
  }
}
//=以上初始化设置=============================================

//=以下初验证=============================================
//密码验证，验证密码是否足够复杂
//密码验证，验证密码是否足够复杂
function validatePassword() {
  var val = $("#password").val();
  if (val) {
    $("#password").parent().find(".alertImg").show();
    $("#password").parent().find(".alertImg").css("background-image", "url(images/accept.png)");
    win.setMessage({'msg':''});
    vdInfoAry[0] = "";
    if (!checkPasswordStr(val)) {
      vdInfoAry[0] = "密码应由5~12位的字母、数字、下划线组成";
      win.setMessage({'msg':vdInfoAry[0]});
      $("#password").parent().find(".alertImg").css("background-image", "url(images/cross.png)");
    }
    comfirmPassword();
  } else {
    vdInfoAry[0] = "密码为必填项";
    $("#password").parent().find(".alertImg").hide();
  }
  ma = getMainAlert($("#password"));
  ma.find(".alertImg").attr("title", vdInfoAry[0]);
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
    vdInfoAry[1] = "";
    var pass = $("#password").val();
    if (val!=pass) {
      vdInfoAry[1] = "确认密码与密码不一致";
      win.setMessage({'msg': vdInfoAry[1]});
      $("#confirmPassword").parent().find(".alertImg").css("background-image", "url(images/cross.png)");
    } else {
      win.setMessage({'msg':''});
      $("#confirmPassword").parent().find(".alertImg").css("background-image", "url(images/accept.png)");
    }
  } else {
    $("#confirmPassword").parent().find(".alertImg").hide();
    vdInfoAry[1] = "确认密码不能为空";
  }
  ma = getMainAlert($("#confirmPassword"));
  ma.find(".alertImg").attr("title", vdInfoAry[1]);
}
//=以上初验证=============================================
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
        $('#password')[0].focus();
        $('#password')[0].select();
      } else {
        $('#confirmPassword')[0].focus();
        $('#confirmPassword')[0].select();
      }
    });
    else $.messager.alert('修改提示', msgs,'info',function(){
      for (var i=0; i<vdInfoAry.length; i++) {
        if (vdInfoAry[i]&&vdInfoAry[i].length>0) break;
      }
      if (i==0) {
        $('#password')[0].focus();
        $('#password')[0].select();
      } else {
        $('#confirmPassword')[0].focus();
        $('#confirmPassword')[0].select();
      }
    });
  } else {
    $('#mask').show();
    var pData = {
      loginName:$('#loginName').val(),
      password:$('#password').val()
    };
    var url = '<%=path%>/login/modifyPassword.do';
    $.ajax({type:"post", async:false, url:url, data:pData, dataType:"json",
      success:function(json){
        $('#mask').show();
        if(json){
          if(mainPage){
            mainPage.$.messager.alert('修改提示',json.retInfo,'info',function(){closeSWinInMain(winId);});
          }else{
            $.messager.alert('修改提示',json.retInfo,'info');
            //window.location.href = "<%=path%>/asIndex.jsp";
            window.location.href = _MAIN_PAGE;
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
