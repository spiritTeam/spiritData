<%@page import="java.io.Writer"%>
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
<title>register</title>
<jsp:include page="/common/sysInclude.jsp" flush="true"/>
</head>
<body>
<center>
 <div style="border:1px solid #ABCDEF;width: 1000px;height: 500px;">
  <div style="float: left;border:1px solid #ABCDEF;width: 700px;height: 500px;">
  <div style="margin-top: 50px;"></div>
    <form action="">
      <table width="480px;" >
        <tr>
          <td align="right" width="100px;" ><span style="width: 150px;font-size: 20px;">登录名:</span></td>
          <td colspan="2" width="230px;" >
          <input style="width:230px;height:35px;color:#999;font-size: 20px;" class="input_1 required" id="loginName" name="loginName"  tabindex="1" type="text" value="" onmouseover=this.focus();this.select();
            onclick="onClick(loginName);" onBlur="onBlur('loginName');"/>
          </td><td width="150px;"><div id="loginNameCheck"></div></td></tr>
         <tr><td ><div style="height: 20px;"></div></td></tr>
          <tr>
          <td align="right"><span style="width: 150px;font-size: 20px;">姓名:</span></td>
          <td colspan="2" rowspan="1">
          <input style="width:230px;height:35px;color:#999;font-size: 20px;" class="input_1 required" id="userName" name="userName"  tabindex="1" type="text" value="" onmouseover=this.focus();this.select();
             onclick="onClick(userName);" onBlur="onBlur('userName');"/>
          </td><td><div id="userNameCheck"></div></td></tr>
        <tr><td ><div style="height: 20px;"></div></td></tr>
        <tr >
          <td align="right"><span style="width: 150px;font-size: 20px;">密码:</span></td>
          <td colspan="2" rowspan="1">
            <input style="width:230px;height:35px;color:#999;font-size: 20px" class="input_1 required" id="password" name="password"  tabindex="2" type="password" value="" onmouseover=this.focus();this.select();
              onclick="onClick(password);" onBlur="onBlur('password');" />
          </td><td><div id="passwordCheck"></div></td></tr>
          <tr><td ><div style="height: 20px;"></div></td></tr>
        <tr >
          <td align="right"><span style="width: 150px;font-size: 20px;">确认密码:</span></td>
          <td colspan="2" rowspan="1">
            <input style="width:230px;height:35px;color:#999;font-size: 20px" class="input_1 required" id="confirmPassword" name="confirmPassword"  tabindex="2" type="password" value="" onmouseover=this.focus();this.select();
              onclick="onClick(confirmPassword);" onBlur="onBlur('confirmPassword');" />
          </td><td><div id="confirmPasswordCheck"></div></td></tr>
        <tr><td ><div style="height: 20px;"></div></td></tr>
          <tr>
            <td align="right"><span style="width: 150px;font-size: 20px;">邮箱:</span></td>
            <td colspan="1" width="130px;"><input style="width:130px;height:35px;color:#999;font-size: 20px;"  id="mail" name="mail"  tabindex="3" type="text"  onmouseover=this.focus();this.select(); 
               onclick="onClick(mail);" onBlur="onBlur('mail');"/></td>
            <td  align="left"><input style="width: 100px;height: 30px;font-size: 20px;" id="mailEndStr" name="mailEndStr" ></td>
            <td><div id="mailCheck"></div></td>
          </tr>
              <tr><td ><div style="height: 20px;"></div></td></tr>
          <tr>
          <td align="right"><span style="width: 150px;font-size: 20px;">验证码:</span></td>
          <td colspan="1" width="130px;"><input style="width:130px;height:35px;color:#999;font-size: 20px;" class="input_1 required" id="checkCode" name="checkCode"  tabindex="3" type="text" value="请输入验证码"
            onmouseover=this.focus();this.select(); onclick="onClick(checkCode);" onBlur="onBlur('checkCode');" /></td>
              <td  align="left"><img style="width: 100px;" title="点击更换" onclick="javascript:refresh(this);" src="<%=path%>/getCheckCode.do"></td><td><div id="checkCodeCheck"></div></td></tr>
          <tr><td ><div style="height: 20px;"></div></td><td></td><td></td></tr>
          <tr><td colspan="4" align="center" ><img id="register" name="register" src="register.png" onclick="saveRegister();"></td></tr>
      </table>
    </form>
  </div>
  <div style="float: right;border:1px solid #ABCDEF;width: 296px;height: 500px;">
  </div>
 </div>
</center>
</body>
<script type="text/javascript">
function saveRegister(){
  var pData={
	  "loginName":$("#loginName").val(),
    "password":$("#password").val(),
    "userName":$("#userName").val(),
    "mailAdress":$("#mail").val()+$('#mailEndStr').combobox('getText'),
  };
  var url="<%=path%>/saveRegisterInfo.do";
  $.ajax({type:"post", async:false, url:url, data:pData, dataType:"json",
    success: function(json) {
      vfMsg = json;
    }
  });
}
function checkVal(obj){
	obj.focus();obj.select();
	if(""+obj=="checkCode"){
		alert(true);
	}
}
$(function() {
	$('#mailEndStr').combobox({    
    url:'mailEndStr.json',    
    valueField:'id',    
    textField:'text',   
    editable:false
	});  
});
function refresh(obj) {
  obj.src = "<%=path%>/getCheckCode.do?"+Math.random();
}
function onBlur(id){
	var nbsp = '&nbsp;&nbsp;&nbsp;&nbsp;';
	var ele = $('#'+id);
	if(id=="loginName"){
		if(ele.val()==''||ele.val()==null){
      $('#'+id+'Check').html('<img src="cross.png"><span style="font-size: 12px;color:red;">登录名不能为空!</span>');
		}else{
			if(checkStr(ele.val())){
				var vsMsg = checkLoginName(ele.val());
				if(vsMsg==true){
					$('#'+id+'Check').html(nbsp+'<img src="accept.png">');
				}else{
					$('#'+id+'Check').html('<img src="cross.png"><span style="font-size: 12px;color:red;">该登录名已被使用!</span>');
				}
			}else{
				$('#'+id+'Check').html('<img src="cross.png"><span style="font-size: 12px;color:red;">登录名应由5~12位的字母数字下划线组成,且首字母不为数字!</span>');
			}
		}
	}else if(id=="userName"){
  }else if(id=="password"){
	  if(ele.val()==''||ele.val()==null){
      $('#'+id+'Check').html('<img src="cross.png"><span style="font-size: 12px;color:red;">密码不能为空!</span>');
    }else{
    	if(!checkStr(ele.val())){
    		$('#'+id+'Check').html('<img src="cross.png"><span style="font-size: 12px;color:red;">密码应由5~12位的字母数字下划线组成!</span>');
    	}else{
    		$('#'+id+'Check').html('<img src="accept.png">');
    	}
    }
  }else if(id=="confirmPassword"){
	  if(ele.val()==''||ele.val()==null){
      $('#'+id+'Check').html('<img src="cross.png"><span style="font-size: 12px;color:red;">请确认密码!</span>');
    }else{
    	if($('#password').val()!=ele.val()){
    		$('#'+id+'Check').html('<img src="cross.png"><span style="font-size: 12px;color:red;">密码不一致!</span>');
    	}else{
    		$('#'+id+'Check').html('<img src="accept.png">');
    	}
    }
  }else if(id=="mail"){
	  if(ele.val()==''||ele.val()==null){
      $('#'+id+'Check').html('<img src="cross.png"><span style="font-size: 12px;color:red;">邮箱不能为空!</span>');
    }else{
    	if(checkStr(ele.val())){
    		var mailStr = ele.val() +$('#mailEndStr').combobox('getText');
    		alert(mailStr);
        var vsMsg = checkMail(mailStr);
        if(vsMsg==true){
          $('#'+id+'Check').html(nbsp+'<img src="accept.png">');
        }else{
          $('#'+id+'Check').html('<img src="cross.png"><span style="font-size: 12px;color:red;">该邮箱已被使用!</span>');
        }
      }else{
        $('#'+id+'Check').html('<img src="cross.png"><span style="font-size: 12px;color:red;">邮箱应由5~12位的字母数字下划线组成,且首字母不为数字!</span>');
      }
    }
  }else if(id=="checkCode"){
	  if(ele.val()==''||ele.val()==null){
      $('#'+id+'Check').html('<span style="font-size: 12px;color:red;">'+nbsp+'验证码不能为空!</span>');
    }else{
    	var vsMsg = verificationCheckCode(ele.val());
    	if(vsMsg==true){
        $('#'+id+'Check').html(nbsp+'<img src="accept.png">');
      }else{
        $('#'+id+'Check').html('<img src="cross.png"><span style="font-size: 12px;color:red;">验证码错误!</span>');
      }
    }
  }
}
function verificationCheckCode(val){
	var vfMsg =null;
	  var pData={
	    "checkCode":val
	  };
	  var url="<%=path%>/verificationCheckCode.do";
	  $.ajax({type:"post", async:false, url:url, data:pData, dataType:"json",
	    success: function(json) {
	      vfMsg = json;
	    }
	  });
	  return vfMsg;
}
function checkMail(val){
	var vfMsg =null;
  var pData={
    "mail":val
  };
  var url="<%=path%>/verificationMail.do";
  $.ajax({type:"post", async:false, url:url, data:pData, dataType:"json",
    success: function(json) {
      vfMsg = json;
    }
  });
  return vfMsg;
}
function checkStr(str){
  var re = /^[a-zA-z]\w{4,11}$/;
  if(re.test(str)){
    return true;
  }else{
	  return false;
  }       
}
function checkLoginName(val){
	var vfMsg =null;
	var pData={
    "loginName":val
  };
	var url="<%=path%>/verificationLoginName.do";
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
</script>
</html>