/**
 *框架流程:
 *1、接收templet,
 *2、分析templet中的element，并且按照showType分类
 *3、解析<d>中的属性，如value。。
 *4、根据showType选择处理文件的方法。
 */
(function($){
  $.templetJD = function(templetDiv,templetJD){
    var _TEMPLET = templetJD._TEMPLET;
    //level
    var level=0;
    //建立segmentGroup组
    buildSegmentGroup(templetDiv, _TEMPLET, level);
  }
  
  function buildSegmentGroup(jObj, segArray, treeLevel) {
    //判断segArray
    if(segArray==null||segArray=="") return "segArry 为空!";
    //判断eleId
    if(jObj==null) return "未知的eleId";
    //mianDiv
    var segGroup = $("<div id='segGroup_"+treeLevel+"' class='segGroup_"+treeLevel+"'/>");
    for(var i=0; i<segArray.length; i++){
      var segDiv=$("<div class='noBorderDiv' id='segLevel_"+i+"'/>");
      segGroup.append(segDiv);
      if(segArray[i].title){
        var titleDiv=$("<div class='borderDiv' id='title_"+segArray[i].id+"'></div>");
        titleDiv.html(segArray[i].title);
        segDiv.append(titleDiv);
      }else if(segArray[i].content){
        var contentDiv=$("<div class='' id='title_"+segArray[i].id+"'></div>");
        var content = segArray[i].content;
        //----------
        //var str="1 plus 2 equal 3";alert(str.match(/\d+/g));
        //-------
        if(content){
          content = content.replace(/<style/g, "<div");
          content = content.replace(/s="/g, "style=\"");
          content = content.replace(/<\/style>/g, "</\div>");
          //content = content.replace(/<d/g, "<div");
        }
        contentDiv.html(content);
        segDiv.append(contentDiv);
      }
      var contendEle = templetContentParse(segArray[i].content);
      segDiv.append(contendEle);
      var subSegs = segArray[i].subSeg;
      var segDivId = segDiv.attr("id");
      buildSegmentGroup(segDiv, subSegs, treeLevel+1);
    }
    jObj.append(segGroup);
    return segGroup;
  }
  
  function templetContentParse(content){
    return null;
  }
})(jQuery);

