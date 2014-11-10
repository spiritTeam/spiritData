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
<link rel="stylesheet" type="text/css" href="<%=path%>/login/login.css" />
</head>
<body>
<center>
 <div style="border:1px solid #ABCDEF;width: 800px;height: 500px;">
  <div style="float: left;border:1px solid #ABCDEF;width: 600px;height: 500px;">
  <div style="margin-top: 50px;"></div>
    <form action="">
      <table width="430px;" >
        <tr>
          <td align="right"><span>账号&nbsp;&nbsp;</span></td>
          <td colspan="2" rowspan="1" width="280px;">
	        <input id="loginName" name="loginName"  tabindex="1" type="text" value="支持邮箱/企鹅/手机号" onmouseover=this.focus();this.select();
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
        <tr><td></td><td align="left"><div style="height: 25px;" id="passwordCheck"></div></td><td></td></tr>
        <tr>
          <td align="right"><span>验证码&nbsp;&nbsp;</span></td>
          <td width="180px;"><input style="width:200px;" id="checkCode" name="checkCode"  tabindex="3" type="text" value="请输入验证码" onmouseover=this.focus();this.select();
                onclick="onClick(checkCode);" onBlur="validateValidateCode('checkCode');" /></td>
          <td align="left"><img title="点击更换" onclick="javascript:refresh(this);" src="<%=path%>/getValidateCode.do"></td>
        </tr>
        <tr><td></td><td align="left"><div style="height: 25px;" id="checkCodeCheck"></div></td><td></td><td></td></tr>
        <tr>
          <td colspan="2"></td><td></td><td></td>
        </tr>
      </table>
      <div><input type="button" value="登录" onclick="loginF()"  /><input type="button" value="注册" onclick="register();" /><input type="button" value="忘记密码" onclick="" /><input type="button" value="从新发送验证信息" onclick="activeAgain();" /></div>
    </form>
  </div>
  <div style="float: right;border:1px solid #ABCDEF;width: 196px;height: 500px;">
  </div>
 </div>
</center>
</body>
<script type="text/javascript">
var psV=false,lnV=false,vcV=false;
function activeAgain(){
	var url="<%=path%>/activeAgain.do";
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
function register(){
	window.location.href="http://localhost:8080/sa/login/register.jsp";
}
function refresh(obj) {
  obj.src = "<%=path%>/getValidateCode.do?"+Math.random();
}
function validateValidateCode(eleId){
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null){
    $('#'+eleId+'Check').html('<img src="cross.png"><span style="font-size: 12px;color:red;">验证码不能为空!</span>');
    vcV = false;
  }else{
    var vsMsg = verificationCheckCode(ele.val());
    if(vsMsg==true){
      $('#'+eleId+'Check').html('<img src="accept.png">');
      vcV = true;
    }else{
      $('#'+eleId+'Check').html('<img src="cross.png"><span style="font-size: 12px;color:red;">验证码错误!</span>');
      vcV = false;
    }
  }
}
function verificationCheckCode(val){
  var vfMsg =null;
    var pData={
      "checkCode":val
    };
    var url="<%=path%>/validateValidateCode.do";
    $.ajax({type:"post", async:false, url:url, data:pData, dataType:"json",
      success: function(json) {
        vfMsg = json;
      }
    });
    return vfMsg;
}
function validatePassword(eleId){
	var ele = $('#'+eleId);
	if(ele.val()==''||ele.val()==null){
		psV = false;
		$('#'+eleId+'Check').html('<img src="cross.png"><span style="font-size: 12px;color:red;">密码为空!</span>');
	}else{
		psV = true;
	}
}
function validateLoginName(eleId){
  var ele = $('#'+eleId);
  if(ele.val()==''||ele.val()==null){
    $('#'+eleId+'Check').html('<img src="cross.png"><span style="font-size: 12px;color:red;">登录名不能为空!</span>');
    lnV = false;
  }else{
    $('#'+eleId+'Check').html('<img src="accept.png">');
    lnV = true;
  }
}
function checkLoginName(val){
  var vfMsg =null;
  var pData={
    "loginName":val
  };
  var url="<%=path%>/validateLoginName.do";
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
		var url="<%=path%>/login.do";
	  var pData={
	    "loginName":$("#loginName").val(),
	    "password":$("#password").val(),
	    "checkCode":$("#checkCode").val(),
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
	    $.message.alert("登录信息","您的登录信息某些地方有误，请完善您的注册信息");
	    return ;
	  }
}
</script>
</html>