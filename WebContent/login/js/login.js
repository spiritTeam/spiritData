function initPwdInputCss(inputId,spanId){
  var span = $('#'+spanId);
  var input = $('#'+inputId);
  span.mouseover(function(){pwdInputOnActive(inputId,spanId);});
  input.focus(function(){pwdInputOnActive(inputId,spanId);});
  input.mouseover(function(){pwdInputOnActive(inputId,spanId);});
  input.blur(function(){if ($(this).val()=="") span.show();});
}
function pwdInputOnActive(inputId,spanId) {
  //隐藏
  $("#"+spanId).hide();
  //获得焦点和选择
  $("#"+inputId)[0].focus();
  $("#"+inputId)[0].select();
}
function setInputCss(){
  var browserType = getBrowserVersion();
  alert(browserType);
  browserType = browserType.substring(0,browserType.lastIndexOf(' '));
  if(browserType!='msie'){
    if($('#loginName')!=null) $('#loginName').css({"line-height":"35px", "height":"35px", "padding-top":"0px"});
    if($('#password')!=null) $('#password').css({"line-height":"35px", "height":"35px", "padding-top":"0px"});
    if($('#confirmPassword')!=null) $('#confirmPassword').css({"line-height":"35px", "height":"35px", "padding-top":"0px"});  
  }
}

