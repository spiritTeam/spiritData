<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
  String path = request.getContextPath();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>找回密码</title>
<jsp:include page="/common/sysInclude.jsp" flush="true"/>
<link rel="stylesheet" type="text/css" href="<%=path%>/login/css/login.css"/>
<script type="text/javascript" src="<%=path %>/login/js/login.js"></script>
<!--样式处理  -->
<style type="text/css">
.prompt{text-align:left;width:250px;margin-top:20px;margin-left:10px;font-size:12px;color:#999999;}
</style>
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
      <td colspan="2">
        <div id="infoDiv" class="prompt">
                    　　您在输入完账号后，请点击“发送邮件”按钮，我们将为您从新发送一封邮件到您的邮箱。请注意查收。
        </div>
      </td>
    </tr>
    <tr>
      <td colspan="2" class="commitBottonTd">
        <div id="commitButton" class="commitDiv" onclick="commit();">
          <span>发送邮件</span>
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
var vdInfoAry = ['账号不能为空'];
/**
 * 主函数
 */
$(function() {
  initPageParam();//初始化页面全局参数
  initMask();

  inputEffect();//设置input效果，鼠标划过
  commitOverOutEffect();//设置按钮效果，鼠标划过
  maskTitleOverOutEffect();//mask效果，鼠标划过

  setCorrectPosition();//设置正确的位置
  setTimeout(initMaskTitle, 100);//初始化maskTitle
});

// 初始化页面全局参数
function initPageParam(){
  mainPage = getMainPage();
  winId = getUrlParam(window.location.href, "_winID");
  win=getSWinInMain(winId);
}

//=以下为验证=============================================
//账号验证
function validateLoginName(){
  var val = $('#loginName').val();
  $("#loginName").parent().find(".alertImg").show();
  if(val){
    $("#loginName").parent().find(".alertImg").css("background-image", "url(images/accept.png)");
    vdInfoAry[0] = "";
  }else{
    $("#loginName").parent().find(".alertImg").hide();
  }
}
//=以上为验证=============================================

//提交信息
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
    if (mainPage) mainPage.$.messager.alert('提示', msgs,'info',function(){
      for (var i=0; i<vdInfoAry.length; i++) {
        if (vdInfoAry[i]&&vdInfoAry[i].length>0) break;
      }
      $('#loginName')[0].focus();
      $('#loginName')[0].select();
    });
    else $.messager.alert('提示', msgs,'info',function(){
      for (var i=0; i<vdInfoAry.length; i++) {
        if (vdInfoAry[i]&&vdInfoAry[i].length>0) break;
      }
      $('#loginName')[0].focus();
      $('#loginName')[0].select();
    });
  } else {
    $('#mask').show();
    var val = $('#loginName').val();
    var url = '<%=path%>/login/sendBackPasswordMail.do';
    var pData = {
      loginName:val
    };
    $.ajax({type:"post", async:false, url:url, data:pData, dataType:"json",
      success:function(json) {
        $('#mask').hide();
        if(json.success){
          if(mainPage) mainPage.$.messager.alert('提示',json.retInfo,'info',function(){closeSWinInMain(winId);});
          else $.messager.alert('提示',json.retInfo,'info',function(){closeSWinInMain(winId);});
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
