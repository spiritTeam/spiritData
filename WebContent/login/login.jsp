<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
  String path = request.getContextPath();
  String sid = request.getSession().getId();
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
    <div style="margin-top: 15px; margin-left: 25px;"align="left"><span style="font-size: 20px;color: #999999;">账号登录</span></div>
    <div style="height:10px; width:400px;border-top: 1px solid  #999999;"></div>
    <form  style="margin-top: 15px;" action="">
      <table width="430px;" >
        <tr><td></td><td colspan="2"><div style="height: 20px;" id="checkResult"></div></td></tr>
        <tr>
          <td align="right"><span>账号&nbsp;&nbsp;</span></td>
          <td colspan="2" rowspan="1" width="280px;">
	        <input id="loginName" name="loginName"  tabindex="1" type="text"  value="账号/QQ/手机号" onmouseover=this.focus();this.select();
	              onclick="onClick(loginName);" onBlur="validateLoginName('loginName');"/>
					</td>
				</tr>
        <tr><td></td><td align="left"><div style="height: 30px;" id="loginNameCheck"></div></td><td></td></tr>
        <tr >
          <td align="right"><span>密码&nbsp;&nbsp;</span></td>
          <td colspan="2" rowspan="1" >
            <input style="width:280px;" id="password" name="password"  tabindex="2" type="text" value="密码" onmouseover=this.focus();this.select();
                onclick="onClick(password);" onBlur="validatePassword('password');" />
          </td>
        </tr>
        <tr><td></td><td align="left"><div style="height: 30px;" id="passwordCheck"></div></td><td></td></tr>
        <tr>
          <td align="right"><span>验证码&nbsp;&nbsp;</span></td>
          <td width="180px;"><input style="width:195px;" id="checkCode" name="checkCode"  tabindex="3" type="text" value="验证码" onmouseover=this.focus();this.select();
                onclick="onClick(checkCode);" onBlur="validateValidateCode('checkCode');" /></td>
          <td align="left"><div style="border: 1px solid  #999999;width: 80px;"><img title="点击更换" onclick="javascript:refresh(this);" src="<%=path%>/login/getValidateCode.do"></div></td>
        </tr>
        <tr><td></td><td align="left"><div style="height: 30px;" id="checkCodeCheck"></div></td><td></td><td></td></tr>
        <tr>
          <td colspan="3" align="center" >
            <div style="width:280px; background-image:url(img/loginb.png); padding-left:2px;">
              <img id="register" name="register" src="img/login.png" onclick="loginF();"/>
            </div>
          </td>
        </tr>
      </table>
    </form>
    <div align="right" style="width: 400px;margin-top: 10px;" ><span style="font-size: 12px;" onclick="tregister()">&nbsp;注册</span><span onclick="activeAgain()" style="font-size: 12px;">&nbsp;激活</span><span onclick="tregister()" style="font-size: 12px;">&nbsp;忘记密码</span></div>
  </div>
</center>
</body>
<script type="text/javascript">
$(function(){
	if($('#loginName').val()==$('#loginName')[0].defaultValue){
		$('#loginName').css('color','#ABCDEF');
	}
	if($('#password').val()==$('#password')[0].defaultValue){
    $('#password').css('color','#ABCDEF');
  }
	if($('#checkCode').val()==$('#checkCode')[0].defaultValue){
    $('#checkCode').css('color','#ABCDEF');
  }
});
var psV=false,lnV=false,vcV=false;
function activeAgain(){
	var url="<%=path%>/login/activeAgain.do";
	var loginName = $("#loginName").val();
	if(loginName==null||loginName==""){
		$.messager.alert('提示信息',"您必须填写用户名，以便于向您的绑定邮箱发送验证!");
		return;
	}else{
		var pData={
	    "loginName":$("#loginName").val()
	  };
	  $.ajax({type:"post", async:false, url:url, data:pData, dataType:"json",
	    success: function(json) {
	      if(json.success==true){
	    	  $.messager.alert('提示信息',json.retInfo);
	      }else{
	    	  $.messager.alert('提示信息',json.retInfo);
	      }
	    }
    });
	}
}
function checkStr(str){
  var re = /^[a-zA-z]\w{4,11}$/;
  if(re.test(str)){
    return true;
  }else{
    return false;
  }       
}
function tregister(){
	window.location.href="<%=path%>/login/register.jsp";
}
function refresh(obj) {
  obj.src = "<%=path%>/login/getValidateCode.do?"+Math.random();
}
function validateValidateCode(eleId){
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null||ele.val()==ele[0].defaultValue){
	  ele.val(ele[0].defaultValue);
	  ele.css('color','#ABCDEF');
    vcV = false;
  }else{
    var vsMsg = verificationCheckCode(ele.val());
    if(vsMsg==true){
    	$('#checkResult').html('<div style="width:275px;font-size: 12px;color:green;">&nbsp;&nbsp;&nbsp;&nbsp;<img src="img/accept.png">验证码正确!</div>');
      vcV = true;
    }else{
      $('#checkResult').html('<div style="font-size: 12px;color:red;">&nbsp;&nbsp;&nbsp;&nbsp;<img src="img/cross.png">验证码错误!</div>');
      vcV = false;
    }
  }
}
function verificationCheckCode(val){
  var vfMsg =null;
    var pData={
      "checkCode":val
    };
    var url="<%=path%>/login/validateValidateCode.do";
    $.ajax({type:"post", async:false, url:url, data:pData, dataType:"json",
      success: function(json) {
        vfMsg = json;
      }
    });
    return vfMsg;
}
function validatePassword(eleId){
	var ele = $('#'+eleId);
	if(ele.val()==''||ele.val()==null||ele.val()==ele[0].defaultValue){
		ele.val(ele[0].defaultValue);
    ele.css('color','#ABCDEF');
		psV = false;
	}else{
		psV = true;
	}
}
function validateLoginName(eleId){
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null||ele.val()==ele[0].defaultValue){
	  ele.val(ele[0].defaultValue);
    ele.css('color','#ABCDEF');
    lnV = false;
  }else{
    $('#checkResult').html('<img src="img/accept.png">');
    lnV = true;
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
function onClick(obj){
	if(obj.value==obj.defaultValue){
		obj.value='';obj.style.color='#000';
	}
}
function loginF() {
	if(psV&&lnV&&vcV){
		var url="<%=path%>/login/login.do";
	  var pData={
	    "loginName":$("#loginName").val(),
	    "password":$("#password").val(),
	    "checkCode":$("#checkCode").val(),
	    "clientMacAddr":fooForm.txtMACAddr.value?(fooForm.txtMACAddr.value=="undefined"?"":fooForm.txtMACAddr.value):"",
 	    "clientIp":fooForm.txtIPAddr.value?(fooForm.txtIPAddr.value=="undefined"?"":fooForm.txtIPAddr.value):"",
 	    "browser":getBrowserVersion()
	  };
	  $.ajax({type:"post", async:false, url:url, data:pData, dataType:"json",
	    success: function(json) {
	    	if(json.type==-1){
	    		var loginInfo = json.data;
	    		var retInfo = loginInfo.retInfo;
	    		$.messager.alert('登录信息',retInfo);
	    	}else if (json.type==1) {
	    		$.messager.alert('登录信息',json.data);
	        return;
	      } else if (json.type==2) {
	        $.messager.alert("登录信息", "登录失败："+json.data, "error");
	      } else {
	        $.messager.alert("登录信息", "登录异常："+json.data, "error");
	      }
	    },
	    error: function(errorData) {
	      if (errorData) {
	        $.messager.alert("登录信息", "登录异常：未知！", "error");
	      } else {
	        $("#mask").hide();
	        setBodyEnter(true);
	      }
	    }
	  });
	}else{
    $.messager.alert("登录信息","您的登录信息某些地方有误，请完善您的注册信息");
    return ;
  }
}
</script>
</html>
