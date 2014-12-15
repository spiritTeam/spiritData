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
    var int level=0;
    buildSegmentGroup(templetDivId, _TEMPLET, level);
    //整个templet的id
    var mainDivId = "mainDivId";
    
    //title
    var title = _TEMPLET.title;
    $('#'+templetDivId).append('<div id="titleDiv">title\'s div</div>');
    //root,可能是多个，也可能是一个
    $('#'+templetDivId).append('<div id="'+mainDivId+'">main\'s div</div>');
    for(var i=0;i<_TEMPLET.length;i++){
      var rootId = _TEMPLET[i].id;
      var rootDiv = '<div id="'+rootId+'">root'+i+'\'s div</div>';
      $('#'+mainDivId).append(rootDiv);
      //subSeg
      var subSegAry = _TEMPLET[i].subSeg;
      for(var k=0;k<subSegAry.length;k++){
        var subSeg = subSegAry[k];
        var subSegId = subSeg.id;
        var subSegDiv = '<div id="'+subSegId+'">subSeg'+k+'\'s div</div>';
        $('#'+rootId).append(subSegDiv)
        //subSegContent
        var subSegContent = subSeg.content;
        var contentId = subSegId+"content";
        var contentDiv = '<div id="'+contentId+'">'+subSegId+'\'s content</div>';
        $('#'+subSegId).append(contentDiv);
      }
    }
  }

  function drowContent(eleId, segArray, treeLevel) {
    //判断segArray???
    //判断eleId???
    var segGroup = $("<div id='segGroup_"+i+"'/>");
    for (var i=0; i<segArray.length; i++) {
      var segDiv=$("<div id='segLevel_"+i+"'/>");
      var titleDiv=$("<div class='segLevel' id='title_"+segArray[i].id+"'>"+segArray[i].title+"</div>");
      segDiv.append(titleDiv);
      var contendEle = templetContentParse(segArray[i].content);
      segDiv.append(contendEle);
      segGroup.append(segDiv);
      var subSegs = segArray[i].subSeg;
      drowContent(segDiv, subSegs, treeLevel+1);
    }
    return segGroup;
  }
})(jQuery);

