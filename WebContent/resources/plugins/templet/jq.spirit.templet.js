/**
 *框架流程:
 *1、接收templet,
 *2、分析templet中的element，并且按照showType分类
 *3、解析<d>中的属性，如value。。
 *4、根据showType选择处理文件的方法。
 */
(function($){
  $.templetJD = function(jsonTempletObj){
  	//定义变量
  	var segTree;
    //默认宽高
    var defaultViewWidth = "800px;",defaultViewHeight = "0px;";
    //传入高和宽的值
    var viewWidth,viewHeight;
    //level
    var level=0;
    
    //从jsonTemplet中得到参数
    var templetJD = jsonTempletObj.templetJD;
    if(jsonTempletObj.viewWidth!=""&&jsonTempletObj.viewWidth!=null) viewWidth = jsonTempletObj.viewWidth;
    else viewWidth = defaultViewWidth;
    if(jsonTempletObj.viewHeight!=""&&jsonTempletObj.viewHeight!=null) viewHeight = jsonTempletObj.viewHeight;
    else viewHeight = defaultViewHeight;
    var templetJD = jsonTempletObj.templetData;
    var _TEMPLET = templetJD._TEMPLET;
    
    //构建主要布局(3个div)
    var mainDiv = $('<div></div>');
    mainDiv.attr('id','mainDiv');
    mainDiv.addClass('mainClass');
    mainDiv.css('width',viewWidth);
    mainDiv.css('height',viewHeight);
    //viewDiv
    var viewDiv = $('<div></div>');
    viewDiv.attr('id','viewDiv');
    viewDiv.addClass('viewDiv');
    //treeDiv
    var treeView = $('<div></div>');
    treeDiv.attr('id','treeDiv');
    treeDiv.addClass('treeDiv');
    viewDiv.appendTo(mainDiv);
    treeDiv.appendTo(mainDiv);
    mainDiv.appendTo('body');
    
    /**
     * 建立segmentGroup组,同时生成树
     * viewDiv 
     * _TEMPLET
     * level
     */
    buildSegmentGroup(viewDiv, _TEMPLET, level, this, 0);
    //画树

    treeDiv.tree({animate:true});
    treeDiv.tree("loadData", tree);
  };
  
  /**
   * 画seg结构,同时生成树
   */
  function buildSegmentGroup(jObj, segArray, treeLevel, self, parentTreeId) {
    //判断segArray
    if(segArray==null||segArray=="") return "segArry 为空!";
    //判断eleId
    if(jObj==null) return "未知的eleId";
    //mianDiv
    var segGroup = $("<div id='segGroup_"+treeLevel+"'/>");
    if(treeLevel!=0) segGroup.attr('class','borderCss');
    for (var i=0; i<segArray.length; i++) {
      var segDiv=$("<div class='' id='segLevel_"+i+"'/>");
      segGroup.append(segDiv);
      if(segArray[i].title){
        var titleDiv=$("<div class='borderCss' id='title_"+segArray[i].id+"' style='border-bottom-width:0px;'></div>");
        titleDiv.html(segArray[i].title);
        segDiv.append(titleDiv);
      }else if(segArray[i].content){
        var contentDiv=$("<div class='' id='title_"+segArray[i].id+"'></div>");
        var content = segArray[i].content;
        if(content){
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
      //处理树
    	var treeNode = {};
    	if (segArray[i].name) treeNode.text = segArray[i].name;
    	else if (segArray[i].title) {
    		treeNode.text=$(segArray[i].title).html();
    	}
    	if (!treeNode.text&&treeNode.text!="") {
      	treeNode.id = "_tree_"+segArray[i].id;
      	treeNode.segId = segArray[i].id;

      	var parent = null;
      	if (parentTreeId==0) parent=self.segTree;
      	else parent=findNode(self.segTree, parentTreeId);

      	if (parent) parent.children[i]=treeNode;
    	}
      buildSegmentGroup(segDiv, subSegs, treeLevel+1, self, treeNode.id);
    }
    jObj.append(segGroup);
    
    return segGroup;
  }

  function findNode(tree, id) {
  	//??
  	return null;
  }

  function getTreeData(segArray,treeLevel){
    var treeData = new Array;
    if(segArray!=null&&segArray!=""){
      for(var i=0;i<segArray.length;i++){
        var treeNode={
          id:'',
          text:'',
          children:''
        };
        //id
        treeNode.id = treeLevel;
        //text
          if(segArray[i].name)treeNode.text = segArray[i].name;
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
})(jQuery);