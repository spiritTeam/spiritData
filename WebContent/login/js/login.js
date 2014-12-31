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
/**
 * 得到mainPage中打开窗口的winId
 */
function getWinId(mainPage){
  var winId = "";
  if(mainPage.registerWinId!=null&&mainPage.registerWinId!="") winId = mainPage.registerWinId;
  if(mainPage.modifyWinId!=null&&mainPage.modifyWinId!="") winId = mainPage.modifyWinId;
  if(mainPage.loginWinId!=null&&mainPage.loginWinId!="") winId = mainPage.loginWinId;
  return winId;
}
/**
 * 清除mainPage中打开窗口的winId
 */
function cleanWinId(mainPage){
  mainPage.loginWinId = "";
  mainPage.modifyWinId = "";
  mainPage.registerWinId = "";
}

