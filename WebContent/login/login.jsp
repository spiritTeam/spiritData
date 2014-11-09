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
</head>
<body>
<center>
 <div style="border:1px solid #ABCDEF;width: 800px;height: 500px;">
  <div style="float: left;border:1px solid #ABCDEF;width: 500px;height: 500px;">
  <div style="margin-top: 50px;"></div>
    <form action="">
      <table width="450px;" border="1px;" bordercolor="red">
        <tr>
          <td colspan="2" rowspan="1">
	        <input style="width:280px;height:40px;color:#999;font-size: 20px;" class="input_1 required" id="loginName" name="loginName"  tabindex="1" type="text" value="支持邮箱/企鹅/手机号" onmouseover=this.focus();this.select();
	              onclick="onClick(loginName);" onBlur="onBlur(loginName);"/>
					</td><td></td>
				</tr>
        <tr><td ><div style="height: 20px;"></div></td><td></td></tr>
        <tr >
          <td colspan="2" rowspan="1">
            <input style="width:280px;height:40px;color:#999;font-size: 20px" class="input_1 required" id="password" name="password"  tabindex="2" type="text" value="密码" onmouseover=this.focus();this.select();
                onclick="onClick(password);" onBlur="onBlur(password);" />
          </td><td></td>
        </tr>
        <tr><td ><div style="height: 20px;"></div></td><td></td></tr>
        <tr>
          <td colspan="1" width="170px;"><input style="width:140px;height:40px;color:#999;font-size: 20px;" class="input_1 required" id="checkCode" name="checkCode"  tabindex="3" type="text" value="请输入验证码" onmouseover=this.focus();this.select();
                onclick="onClick(checkCode);" onBlur="onBlur(checkCode);" /></td>
          <td  align="left"><img title="点击更换" onclick="javascript:refresh(this);" src="<%=path%>/getValidateCode.do"></td>
        </tr>
        <tr><td ><div style="height: 20px;"></div></td><td></td></tr>
        <tr>
          <td colspan="2"><input type="button" value="登录" onclick="loginF()" /><input type="button" value="注册" onclick="" /><input type="button" value="忘记密码" onclick="" /><input type="button" value="从新发送验证信息" onclick="" /></td><td></td>
        </tr>
      </table>
    </form>
  </div>
  <div style="float: right;border:1px solid #ABCDEF;width: 296px;height: 500px;">
  </div>
 </div>
</center>
</body>
<script type="text/javascript">
function refresh(obj) {
  obj.src = "<%=path%>/getValidateCode.do?"+Math.random();
}
function onBlur(obj){
	if(obj.value==""){
		obj.value=obj.defaultValue;
		obj.style.color='#999';
	}
}
function onClick(obj){
	if(obj.value==obj.defaultValue){
		obj.value='';obj.style.color='#000';
	}
}
function loginF() {
	  var url="<%=path%>/login.do";
	  var pData={
	    "loginName":$("#loginName").val(),
	    "password":$("#password").val(),
	    "checkCode":$("#checkCode").val(),
	    "browser":getBrowserVersion()
	  };
	  $.ajax({type:"post", async:false, url:url, data:pData, dataType:"json",
	    success: function(json) {
	      if (json.type==1) {
	        alert("loginOk");
	        return;
	      } else if (json.type==2) {
	        $.messager.alert("错误", "登录失败："+json.data, "error", function(){
	          $("#loginname").focus();
	          $("#mask").hide();
	          //setBodyEnter(true);
	        });
	      } else {
	        $.messager.alert("错误", "登录异常："+json.data, "error", function(){
	          $("#loginname").focus();
	          $("#mask").hide();
	          //setBodyEnter(true);
	        });
	      }
	    },
	    error: function(errorData) {
	      if (errorData) {
	        $.messager.alert("错误", "登录异常：未知！", "error", function(){
	          $("#loginname").focus();
	          $("#mask").hide();
	          setBodyEnter(true);
	        });
	      } else {
	        $("#mask").hide();
	        setBodyEnter(true);
	      }
	    }
	  });
	}
</script>
</html>