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
  };
  $.templetJD.catalogTree = function(catalogTreeDiv,templetJD){
    var _TEMPLET = templetJD._TEMPLET;
    var level = 1;
    var tree = getTreeData(_TEMPLET,level);
    catalogTreeDiv.tree({animate:true});
    catalogTreeDiv.tree("loadData", tree);
  };
  function buildSegmentGroup(jObj, segArray, treeLevel) {
    var templetTreeData = new Array;
    //判断segArray
    if(segArray==null||segArray=="") return "segArry 为空!";
    //判断eleId
    if(jObj==null) return "未知的eleId";
    //mianDiv
    var segGroup = $("<div id='segGroup_"+treeLevel+"'/>");
    if(treeLevel!=0) segGroup.attr('class','borderCss');
    for(var i=0; i<segArray.length; i++){
      var segDiv=$("<div class='' id='segLevel_"+i+"'/>");
      segGroup.append(segDiv);
      if(segArray[i].title){
        var titleDiv=$("<div class='borderCss' id='title_"+segArray[i].id+"' style='border-bottom-width:0px;'></div>");
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
  
  function getTreeData(segArray,treeLevel){
    var treeData = new Array;
    if(segArray!=null&&segArray!=""){
      for(var i=0;i<segArray.length;i++){
        var treeNode={
          id:'',
          text:'',
          children:''
        }
        //id
        treeNode.id = treeLevel;
        //text
        //if(segArray[i].title){
         // treeNode.text = segArray[i].title;
        //}else{
          if(segArray[i].name)treeNode.text = segArray[i].name;
        //}
        //children
        treeNode.children=getTreeData(segArray[i].subSeg,treeLevel+1);
        treeData[i] = treeNode;
      } 
    }
    return treeData;
  }
  function templetContentParse(content){
    return null;
  }
  
  var treeData=[{
    "id":1, "text":"上传数据",
    "children":[{"id":11, "text":"结构分析"},{"id":12, "text":"单项指标分析"},{"id":13, "text":"类别指标分析"},{"id":14, "text":"数值指标分析"},{"id":15, "text":"时间指标分析"}]
  },{
    "id":2, "text":"积累数据",
    "children":[{"id":21, "text":"单项指标分析"},{"id":22, "text":"类别指标分析"},{"id":23, "text":"数值指标分析"},{"id":24, "text":"时间指标分析"}]
  },{
    "id":3, "text":"高级",
    "children":[{"id":31, "text":"数据质量及结构"},{"id":32, "text":"时间关联分析"},{"id":33, "text":"分布统计"},{"id":34, "text":"关联分析"}]
  }];
  $.templetJD.tree = function(treeDiv){
    var _TEMPLET = templetJD._TEMPLET;
    //level
    var level=0;
    //建立segmentGroup组
    buildSegmentGroup(templetDiv, _TEMPLET, level);
  }
})(jQuery);

