/**
 * 框架介绍:
 * 下载report工具
 */
(function(){
  //主函数
  $.download = function(reportId,deployName){
    var _url = deployName+"/expReport/expWord.do?";
    var form = $("<form>");
    $("body").append(form);
    $(form).attr({'id':'downLoad','action':_url,'method':'post'});
    $(form).append('<input id="reportId" name="reportId" type="text" value="'+reportId+'" />');
    $(form).submit();
  };
})(jQuery);