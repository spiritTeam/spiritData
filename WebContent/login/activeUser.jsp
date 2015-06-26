<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.spiritdata.framework.FConstants"%>
<%@page import="com.spiritdata.dataanal.UGA.pojo.User"%>
<%
  String path = request.getContextPath();
  String sid = request.getSession().getId();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>激活账号</title>
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
          <input class="alertInputComp" id="loginName" name="loginName" tabindex="1" type="text" disabled="disabled" />
          <div class="maskTitle">请输入您的账号</div>
        </div>
      </td>
    </tr>
    <tr>
      <td class="labelTd">邮　箱</td>
      <td class="inputTd">
        <div class="alertInput-Text">
          <input class="alertInputComp" id="mail" name="mail" tabindex="2" type="text" onblur="validateMail();" />
        </div>
        <div id="mailAlertImg" style="width:16px;height:16px;position:absolute;left:295px;top:70px;display:block;"></div>
      </td>
    </tr>
    <tr>
      <td class="labelTd" >验证码</td>
      <td class="inputTd" >
        <div class="alertInput-vCode" style="margin-top:1px;">
          <div id="vCodeInput"><input id="checkCode" class="alertInputComp" name="checkCode" tabindex="5" type="text" onBlur="validateCheckCode();"/></div>
          <div id="vCodeImg"><img id="vcimg" title="点击更换" onclick="javascript:refreshCCImg('<%=path%>');" src=""></div>
          <div class="alertImg"></div>
          <div class="maskTitle">按右图输入验证码</div>
        </div>
      </td>
    </tr>
    <tr>
      <td colspan="2" class="commitBottonTd">
        <div id="commitButton" class="commitDiv" onclick="commit();">
          <span>激　活</span>
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
var vdInfoAry = ['邮箱不能为空','验证码不能为空'];
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
  refreshCCImg('<%=path%>');
});
//=以下初始化设置=============================================
// 初始化页面全局参数
function initPageParam(){
  mainPage = getMainPage();
  winId = getUrlParam(window.location.href, "_winID");
  win=getSWinInMain(winId);
  var _user = getUrlParam(window.location.href, "_userInfo");
  if (_user!=null&&_user!="") {
    var s = new Array();
    s = _user.split(",");
    $('#loginName').val(s[0]);
    $('#mail').val(s[1]);
    $('#password').val(s[2]);
    validateMail();
  }
}
//=以上初始化设置=============================================

//=以下为验证=============================================
//验证邮箱
function validateMail() {
  var val = $('#mail').val();
  if(val){
    $("#mailAlertImg").show();
    win.setMessage({'msg':''});
    vdInfoAry[0] = "";
    var mailAdress = val;
    if (!checkMailAdress(mailAdress)) {
      vdInfoAry[0] = "邮箱格式不正确";
      win.setMessage({'msg': vdInfoAry[0]});
      $("#mailAlertImg").css("background-image", "url(images/cross.png)");
    }else $("#mailAlertImg").css({"background-image":"url(images/accept.png)"});
  }else{
    $("#mailAlertImg").hide();
    vdInfoAry[0] = "邮箱不能为空";
  }
  var ma = getMainAlert($("#mail"));
  ma.find(".alertImg").attr("title", vdInfoAry[0]);
  //验证邮箱是否符合规则
  function checkMailAdress(mailAdress){
    var reg = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/; 
    return reg.test(mailAdress); 
  }
}
//验证码验证
function validateCheckCode(){
  var val = ($('#checkCode').val()).toUpperCase();
  if (val) {
    $("#checkCode").parent().parent().find(".alertImg").show();
    win.setMessage({'msg':''});
    vdInfoAry[1] = "";
    if(val!=checkCode){
      vdInfoAry[1] = "验证码填写错误";
      win.setMessage({'msg':vdInfoAry[1]});
      $("#checkCode").parent().parent().find(".alertImg").css("background-image", "url(images/cross.png)");
    } else $("#checkCode").parent().parent().find(".alertImg").css("background-image", "url(images/accept.png)");
  }else{
    $("#checkCode").parent().parent().find(".alertImg").hide();
    vdInfoAry[1] = "验证码为必填项";
  }
  var ma = getMainAlert($("#checkCode"));
  ma.find(".alertImg").attr("title", vdInfoAry[1]);
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
    showAlert('修改提示', msgs,'info',function() {
      for (var i=0; i<vdInfoAry.length; i++) {
        if (vdInfoAry[i]&&vdInfoAry[i].length>0) break;
      }
      if (i==0) {
        $('#mail')[0].focus();
        $('#mail')[0].select();
      } else if (i==1) {
        $('#checkCode')[0].focus();
        $('#checkCode')[0].select();
      }
  	});
  } else {
  	showConfirm('确认对话框', '请仔细检查邮箱，如果邮箱不正确，将不会收到激活邮件!', function() {
      if (r) {
        var mailAdress = $("#mail").val();
        var pData={
          "loginName":$("#loginName").val(),
          "mailAdress":mailAdress
        };
        $("#mask").show();
        var _url="<%=path%>/login/activeUserAgain.do";
        $.ajax({type:"post",async:false,url:_url,data:pData,dataType:"json",
          success:function(json) {
            $("#mask").hide();
            if(json.success){
              mainPage.$.messager.alert('修改提示',json.retInfo,'info',function(){closeSWinInMain(winId);});
            }else{
              mainPage.$.messager.alert('提示',json.retInfo,'info');
            }
          }
        });
      } else {
        $('#mail')[0].focus();
        $('#mail')[0].select();
        refreshCCImg('<%=path%>');
      }
    });
  }
}
</script>
</html>
