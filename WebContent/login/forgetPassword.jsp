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
.prompt{text-align:left;width:270px;margin-top:40px;margin-left:0px;}
</style>
</head>
<body>
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
      <td colspan="2" class="commitBottonTd">
        <div id="commitButton" class="commitDiv" onclick="commit();">
          <span>下一步</span>
        </div>
      </td>
    </tr>
  </table></form>
  <div id="infoDiv" class="prompt">
    <h2>重置密码流程：</h2>
    <span>1、填写用户名,系统校验用户名。</span><br/>
    <span>2、成功后点击下一步,将发送邮件至您的邮箱。</span><br/>
    <span>3、登录邮箱,根据提示重置密码。</span><br/>
  </div>
</div></center>
</body>
<script type="text/javascript">
var win;
var mainPage;
var winId;
var lnV =false;/**
 * 主函数
 */
$(function() {
  initPageParam();//初始化页面全局参数

  inputEffect();//设置input效果，鼠标划过
  commitOverOutEffect();//设置按钮效果，鼠标划过
  maskTitleOverOutEffect();//mask效果，鼠标划过

  setCorrectPosition();//设置正确的位置
  setTimeout(initMaskTitle, 100); //初始化maskTitle
});

// 初始化页面全局参数
function initPageParam(){
  mainPage = getMainPage();
  winId = getUrlParam(window.location.href, "_winID");
  win=getSWinInMain(winId);
}

//=以下初验证=============================================
//账号验证
function validateLoginName(){
  var val = $('#loginName').val();
  if(val){
    $("#loginName").parent().find(".alertImg").show();
    if(checkLoginName(val)){
      win.setMessage({'msg':'您输入的用户名有误'});
      $("#loginName").parent().find(".alertImg").css("background-image", "url(images/cross.png)");
      lnV = false;
    }else{
      $("#loginName").parent().find(".alertImg").css("background-image", "url(images/accept.png)");
      lnV = true;
    }
  }else{
    $("#loginName").parent().find(".alertImg").hide();
    lnV = false;
  }
  function checkLoginName(str){
    var vfMsg =null;
    var pData={
      "loginName":str
    };
    var url="<%=path%>/login/validateLoginName.do";
    $.ajax({type:"post", async:false, url:url, data:pData, dataType:"json",
       success:function(json) {
         vfMsg = json;
       }
    });
    return vfMsg;
  }
}
//=以上初验证=============================================

//提交信息
function commit(){
  $('#commitButton').attr('disabled',true);
  if(lnV){
    var val = $('#loginName').val();
    if(val){
      var url = '<%=path%>/login/sendBackPasswordMail.do';
      var pData = {
        loginName:val
      };
      $.ajax({type:"post", async:false, url:url, data:pData, dataType:"json",
        success:function(json) {
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
  }else{
    var alertMessage = "您的";
    if(lnV==false){
      if($('#loginName').val()) alterMessage = alterMessage + "原密码填写有误、";
      else alertMessage = alertMessage + "原密码还未填写、";
    }
    alertMessage = alertMessage.substring(0, alertMessage.lastIndexOf("、"));
    if(mainPage) mainPage.$.messager.alert("提示",alertMessage+"请检查!",'info',function (){
      if(lnV==false){
        $('#loginName')[0].focus();
        $('#loginName')[0].select();
      }
    });
    else $.messager.alert("提示","您还有未完善的信息!",'info',function (){
      if(lnV==false){
        $('#loginName')[0].focus();
        $('#loginName')[0].select();
      }
    });
  }
  $('#sendButton').attr('disabled',false);
}
</script>
</html>
