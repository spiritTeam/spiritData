/**
 *框架流程:
 *1、接收templet,
 *2、分析templet中的element，并且按照showType分类
 *3、解析<d>中的属性，如value。。
 *4、根据showType选择处理文件的方法。
 */
(function($){
  $.templetJD = function(templetDivId,templetJD){
    var _TEMPLET = templetJD._TEMPLET;
    //整个templet的id
    var templetJDId = _TEMPLET.id; 
    //title
    var title = _TEMPLET.title;
    $('#'+templetDivId).append('<div id="titleDiv" style="width:50px;height:30px;border: solid red 1px;">title\'s div</div>');
    //根,可能是多个，也可能是一个
    var rootAry = _TEMPLET.subSeg;
    var root;
    for(var i=0;i<rootAry.length;i++){
      var rootId = templetJDId+rootAry[i].id;
      var rootdiv = '<div id="'+rootId+'" style="width:50px;height:30px;border: solid red 1px;">title\'s div</div>';
    }
  }
})(jQuery);

