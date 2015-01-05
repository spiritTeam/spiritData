/**
 *框架流程:
 *1、接收templet,
 *2、分析templet中的element，并且按照showType分类
 *3、解析<d>中的属性，如value。。
 *4、根据showType选择处理文件的方法。
 */
(function($){
  //定义变量
  //树。。
  var segTree=[];
  //level
  var level=0;
  function initPageFrame(){
    //1、画pageFrame
    //1-1:topSegment    
    var topSegment =$('<div id="topSegment"><div id="rTitle"></div></div>');
    $("body").append(topSegment);
    //1-2mainSegment
    var mainSegment = $('<div id="mainSegment"></div>');
    $("body").append(mainSegment);
    //1-2-1sideFrame
    var sideFrame = $('<div id="sideFrame"></div>');
    //catalogTree
    var catalogTree = $('<div id="catalogTree" style="border:1px solid #E6E6E6; width:258px; "></div>');
    sideFrame.append(catalogTree);
    //1-2-2reportFrame
    var reportFrame = $('<div id="reportFrame"></div>');
    mainSegment.append(sideFrame);
    mainSegment.append(reportFrame);
    var INIT_PARAM = {
    pageObjs: {
      topId: "topSegment",
      mainId: "mainSegment"
    },
    page_width: -1,
    page_height: -1,
    top_shadow_color:"#E6E6E6",
    top_height: 60,
    top_peg: false,
    myInit: initPos,
    myResize: initPos
  };
  function initPos() {
    $("#reportFrame").spiritUtils("setWidthByViewWidth", $("body").width()-$("#sideFrame").spiritUtils("getViewWidth"));
    $("#sideFrame").css("left", $("#reportFrame").width());
  }
  var initStr = $.spiritPageFrame(INIT_PARAM);
  if (initStr) {
    $.messager.alert("页面初始化失败", initStr, "error");
    return ;
  };
  }
  /**
   * 主函数入口
   */
  $.templetJD = function(templetUrl,templetId){
    //1,画pageFrame
    initPageFrame();
    //2，从后台请求数据
    var pData = {'templetId':templetId};
    $.ajax({type:"post",url:templetUrl,data:pData,dataType:"json",
      success:function(json){
        rst=str2JsonObj("jsonData",json);
        if(rst.jsonType==1){
          var templetJD = rst.data;
          //3，根据templetD构造出树和框
          var _TEMPLET = templetJD._TEMPLET;
          //标题
          var _HEAD = templetJD._HEAD;
          $('#rTitle').html(_HEAD.reportName);
          buildSegmentGroup($('#reportFrame'), _TEMPLET, level, null);
          //显示树的部分
          $('#catalogTree').tree({animate:true});
          $('#catalogTree').tree("loadData", segTree);
        }else{
          $.messager.alert("提示",jsonData.message,'info');
        }
      },error:function(errorData ){
        $.messager.alert("提示","未知错误！",'info');
      }
    });
  };
  
  /**
   * 通过遍历实现树和主界面的构建
   * jQbj:jQuery对象，指代的是根节点
   * segArray：数据数组，
   * treeLevel：层数,
   * segTree:一个数组，用于储存节点
   * parent:可以是空也可以是null表示第一次遍历，无parent,
   * 结构：jObj[rptSegment:{segTitle,segContent,subSegs},
   *     rptSegment:{segTitle,segContent,subSegs},
   *     rptSegment:{segTitle,segContent,subSegs}]
   */
  function buildSegmentGroup(jObj, segArray, treeLevel, parent) {
    //判断segArray
    if(segArray==null||segArray=="") return "segArry 为空!";
    //判断eleId
    if(jObj==null) return "未知的eleId";
    //rptSegment
    var rptSegment = $('<div class="rptSegment"/></div>');
    for (var i=0; i<segArray.length; i++) {
      var segId = segArray[i].id;
      //第一层的时候title中没有style标签，第二层有 ,可以跟晖哥商量下title标签问题？
      //title是放在div下面的span中还是直接放在div中
      if(segArray[i].title){
      //segTitle
      var segTitle = $('<div id="'+segId+'title"></div>');
      segTitle.find("span").html(segArray[i].title);
      rptSegment.append(segTitle);
      }else if(segArray[i].content){
      //segContent
      var segContent= $('<div id="'+segId+'frag'+i+'" class="segContent"/></div>');
      var content = segArray[i].content;
      if(content) {
    	  // TODO 
      }
      segContent.html(content);
      rptSegment.append(segContent);
      }
      var contendEle = templetContentParse(segArray[i].content);
      rptSegment.append(contendEle);
      var subSegs = segArray[i].subSeg;
      //处理树
      var treeNode = {};
      if (segArray[i].name) treeNode.text = segArray[i].name;
      else if (segArray[i].title) {
    	  treeNode.text=$(segArray[i].title).html();
      }
      if (treeNode.text&&treeNode.text!="") {
	      treeNode.id = "_tree_"+segArray[i].id;
	      treeNode.segId = segArray[i].id;
	      treeNode.children = new Array();
	      if (parent==null) {
	        //parent=treeNode;为什么加上这个就会出问题？
	        segTree[i]=treeNode;
	      }else{
	        parent.children[i] = treeNode;
	      }
      }
      buildSegmentGroup(rptSegment, subSegs, treeLevel+1, treeNode);
    }
    jObj.append(rptSegment);
    return rptSegment;
  }
  function templetContentParse(content){
	  // TODO 对content进行解析预留
    return null;
  }
})(jQuery);
