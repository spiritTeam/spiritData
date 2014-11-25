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
