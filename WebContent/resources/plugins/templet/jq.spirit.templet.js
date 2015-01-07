/**
 *框架介绍:
 *1、两个参数，一个是url,另一个是templetId
 *2、入口函数是$.templetJD(templetUrl,templetId);
 *3、执行流程:
 *  _1:先初始化PageFrame。
 *  _2:向后太请求数据。
 *  _3完成结构的组建(树和templet主体)。
 *  _4解析jsonD。
 *  _5显示
 */
(function($){
  
  //树
  var segTree=[];
  //level
  var level=0;
  //用于存放jsonDInfo
  var jsonDInfoArray = [];
  var keyArray = [];
  
  /**
   * 主函数入口
   */
  $.templetJD = function(templetUrl,templetId,deployPath){
    //1,画pageFrame
    initPageFrame();
    //2，从后台请求数据
    var pData = {'templetId':templetId};
    $.ajax({type:"post",url:templetUrl,data:pData,dataType:"json",
      success:function(json){
        var templetJsonObj=str2JsonObj(json);
        if(templetJsonObj.jsonType==1){
          var templetJD = templetJsonObj.data;
          //3，根据templetD构造出树和框
          //主体
          var _TEMPLET = templetJD._TEMPLET;
          //标题
          var _HEAD = templetJD._HEAD;
          //jsonDurl 用于请求jsond
          var _DATA = templetJD._DATA;
          getJsonD(_DATA);
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
   * 向后台请求jsond
   * _DATA:jsondUrl的请求数组
   */
  function getJsonD(_DATA){
    if(_DATA==null||_DATA==""||_DATA[0]==null||_DATA[0]=="") return null;
    var i=0;
    for(;i<_DATA.length;i++){
      var jsondInfo = new Object();
      jsondInfo.id = _DATA[i]._id;
      jsondInfo.url =  _DATA[i]._url;
      if(_DATA[i]._jsonD_code!=""&&_DATA[i]._jsonD_code!=null) jsondInfo.jsonD_code = _DATA[i]._jsonD_code;
      else jsondInfo.jsonD_code = "";
      jsondInfo.jsond = null;
      jsondInfo.interval=function(index, jsonDurl){
    	  //document.write(jsonDurl);
    	 alert(index+":"+jsonDurl);
    	  /*
          var pData = {'jsondId':"jsondId"};
          $.ajax({type:"post",url:jsonDurl,data:pData,dataType:"json",
            success:function(json){
              var jsondJsonObj=str2JsonObj(json);
              if(jsondJsonObj.jsonType==1){
                  //alert("0:"+jsonDInfoArray.length+"\n"+url+"\n"+allFields(jsonDInfoArray[0]));
                  //alert("1:"+jsonDInfoArray.length+"\n"+url+"\n"+allFields(jsonDInfoArray[1]));
              	alert("inner:"+index);
              }
            },error:function(XMLHttpRequest, textStatus, errorThrown ){
            }
          });*/
      };
      jsonDInfoArray[i] = jsondInfo;
    }
    for(i=0;i<jsonDInfoArray.length;i++) {
    	//alert("0:\n"+allFields(jsonDInfoArray[i]));
    	//jsonDInfoArray[i].interval(i, jsonDInfoArray[i].url);
    	//var intervalKey= 
    	jsonDInfoArray[i].intervalKey=setInterval(jsonDInfoArray[i].interval(i, jsonDInfoArray[i].url),500);
    }
  }
  
  /**
   * 通过遍历实现树和主界面的构建
   * jQbj:jQuery对象，指代的是根节点
   * segArray：数据数组，
   * treeLevel：层数,
   * segTree:一个数组，用于储存节点
   * parent:可以是空也可以是null表示第一次遍历，无parent,
   * 结构：jObj[segGroup:{segTitle,segContent,subSegs},
   *     segGroup:{segTitle,segContent,subSegs},
   *     segGroup:{segTitle,segContent,subSegs}]
   */
  function buildSegmentGroup(jObj, segArray, treeLevel, parent) {
    //判断segArray
    if(segArray==null||segArray=="") return "segArry 为空!";
    //判断eleId
    if(jObj==null) return "未知的eleId";
    //segGroup
    var segGroup = $('<div id="segGroup_'+treeLevel+'" class="segGroup_'+treeLevel+'"/></div>');
    for (var i=0; i<segArray.length; i++) {
      var segId = segArray[i].id;
      //第一层的时候title中没有style标签，第二层有 ,可以跟晖哥商量下title标签问题？
      //title是放在div下面的span中还是直接放在div中
      if(segArray[i].title){
      //segTitle
      var segTitle = $('<div id="'+segId+'title" class="segTitle_'+treeLevel+'"></div>');
      segTitle.html(segArray[i].title);
      segGroup.append(segTitle);
      }else if(segArray[i].content){
        //segContent
        var segContent= $('<div id="'+segId+'frag'+i+'" class="segContent_'+treeLevel+'"/></div>');
        var content = segArray[i].content;
        if(content) {
          // TODO 
        }
        segContent.html(content);
        segGroup.append(segContent);
      }
      var contendEle = templetContentParse(segArray[i].content);
      segGroup.append(contendEle);
      var subSegs = segArray[i].subSeg;
      //处理树
      var treeNode = {};
      if (segArray[i].name) treeNode.text = segArray[i].name;
      else
      if (segArray[i].title) treeNode.text=$(segArray[i].title).html();

      if (treeNode.text&&treeNode.text!="") {
        treeNode.id = "_tree_"+segArray[i].id;
        treeNode.segId = segArray[i].id;
        treeNode.children = new Array();
        if (parent==null) {
          //parent=treeNode;为什么加上这个就会出问题？
          segTree[i]=treeNode;
        } else {
          parent.children[i] = treeNode;
        }
      }
      buildSegmentGroup(segGroup, subSegs, treeLevel+1, treeNode);
    }
    jObj.append(segGroup);
    return segGroup;
  }
  
  function templetContentParse(content){
    // TODO 对content进行解析预留
    return null;
  }
  
  /**
   * 初始化pageFrame
   */
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
    //1-2-1-1catalogTree
    var catalogTree = $('<div id="catalogTree" style="border:1px solid #E6E6E6; width:258px; "></div>');
    sideFrame.append(catalogTree);
    //1-2-2reportFrame
    var reportFrame = $('<div id="reportFrame"></div>');
    mainSegment.append(sideFrame);
    mainSegment.append(reportFrame);
    //INIT_PARAM
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
})(jQuery);
