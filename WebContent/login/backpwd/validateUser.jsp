<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
  String path = request.getContextPath();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>login</title>
<jsp:include page="/common/sysInclude.jsp" flush="true"/>
<link rel="stylesheet" type="text/css" href="<%=path%>/login/css/login.css" />
</head>
<body>
<center>
  <div style="border:1px solid #ABCDEF;width: 450px;height: 500px;">
    <div style="margin-top: 15px; margin-left: 25px;"align="left"><span style="font-size: 20px;color: #999999;">找回密码</span></div>
    <div style="height:1px; width:400px;border-top: 1px solid  #999999;"></div>
    <form  style="margin-top: 15px;" action="">
      <table width="430px;" >
        <tr><td colspan="3"><div style="height: 30px;text-align: left;margin-left: 35px;" id="checkResult"></div></td></tr>
        <tr style="height:50px; valign:top;">
          <td align="right"><span class="myspan">账号&nbsp;&nbsp;</span></td>
          <td colspan="2" rowspan="1" width="280px;" style="text-align:left;">
          <input id="loginName" name="loginName"  tabindex="1" type="text"  value="请填写用户名" onmouseover=this.focus();this.select();
                onclick="onClick(loginName);" onBlur="validateLoginName('loginName');" /><img id="logRImg" src=""/>
          </td>
        </tr>
        <tr><td colspan="3" align="center"><input type="button" value="下一步" onclick="sendBackPwdMail();"/></td></tr>
      </table>
    </form>
  </div>
</center>
</body>
<script type="text/javascript">
var lnV =false;
function sendBackPwdMail(){
	if(lnV){
		$('#loginName')
		var url = '<%=path%>/login/sendBackPwdMail.do';
		var pData = {
			loginName:$('#loginName').val()
		};
		alert($('#loginName').val());
		$.ajax({type:"post", async:false, url:url, data:pData, dataType:"json",
		  success: function(json) {
			  if(json){
				  $.messager.alert('提示','已发送验证信息到注册邮箱,请登录到邮箱查看!');
			  }else{
				  $.messager.alert('提示','发送失败,请重试!');
			  }
		  }
	  });
	}
}
function onClick(obj){
  if(obj.value==obj.defaultValue){
    obj.value='';obj.style.color='#000';
  }
}
function validateLoginName(eleId){
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null||ele.val()==ele[0].defaultValue){
    ele.val(ele[0].defaultValue);
    ele.css('color','#ABCDEF');
    lnV = false;
  }else{
    var vsMsg = checkLoginName(ele.val());
    if(vsMsg==true){
      $('#checkResult').html('<div style="width:370;font-size: 12px;color:red;">&nbsp;&nbsp;&nbsp;&nbsp;<img src="../img/cross.png">登录名错误!</div>');
      lnV = false;
    }else{
    	$('#logRImg').attr('src','../img/accept.png');
      lnV = true;
    }
  }
}
function checkLoginName(val){
  var vfMsg =null;
  var pData={
    "loginName":val
  };
  var url="<%=path%>/login/validateLoginName.do";
  $.ajax({type:"post", async:false, url:url, data:pData, dataType:"json",
     success: function(json) {
       vfMsg = json;
     }
  });
  return vfMsg;
}
</script>
</html>
