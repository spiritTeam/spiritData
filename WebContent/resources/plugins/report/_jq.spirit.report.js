//以下为全局变量部分=========

/**
 * 页面得到的<%path%> 用于ajax访问或传到其他不能得到<%path%>的页面什么的
 */
var deployName;

/**
 * 储存jsonDInfo的数组
 */
var jsonDInfoArray = [];

/**
 * 用于 储存didId，用于在替换的时候需要替换的元素分类
 */
var jsonDIdAry=[];

/**
 * 树的层数，用于把Segment组成树
 */
var level = 0;

/**
 * 用于储存segment组成的树
 */
var segTree=[];

//以上为全局变量部分=========

var monitor = new Object();

/**
 * 生成报告方法
 */
function generateReport(reportUrl, reportId) {
  //1、初始化页面
  initPageFrame();

  //2、获取report
  var pData = {'reportId':reportId};
  //得到path<%=path%>
  deployName = reportUrl.substring(0,reportUrl.indexOf('/report/'));
  $.ajax({type:"post",url:reportUrl,data:pData,async:true,dataType:"text",
    success:function(json){
      try{
        //str2JsonObj方法来自common.utils.js 用于吧json字符串变成json对象
        var retJson = str2JsonObj(json);
        if (retJson.jsonType==1) {
          //report_json
          var reportData = retJson.data;
          //3，根据report构造出树和框
          //_REPORT report主体部分
          var _REPORT = reportData._REPORT;
          //_HEAD report 头部分
          var _HEAD = reportData._HEAD;
          //_DLIST report jsonD数据信息
          var _DLIST = reportData._DLIST;
          //解析DLIST
          resolveDLIST(_DLIST);
          $('#rTitle').html(_HEAD._reportName+'<input id="download" onclick="$.download(\''+reportId+'\',\''+deployName+'\');" type="button" value="保存报告"/>');
          buildSegmentGroup($('#reportFrame'), _REPORT, level, null);
          
          //显示树的部分
          $('#catalogTree').tree({animate:true});
          $('#catalogTree').tree("loadData", segTree);
          //为树结点绑定锚点
          $('#catalogTree').tree({
            onClick: function(node){
              try {
                var topSegHeight = $("#topSegment").height()+3;  
                $("body,html").animate({scrollTop:$("#"+node.positionId).offset().top - topSegHeight});  
              } catch(e) {
                $.messager.alert("提示",e.message,'info');
              }
            }
          });

          //解析~展示 
          resolveAndDraw();
        }else{
          $.messager.alert("提示",jsonData.message,'info');
        }          
      }catch(e){
        $.messager.alert("提示",e.message,'info');
      }
    },error:function(errorData){
      alert("err:"+errorData);
      $.messager.alert("提示","未知错误！",'info');
    }
  });
}

/**
 * ？？？？
 */
function resolveAndDraw(){
  //根据jsonDIdAry中的id抓取dom
  var domAry = new Array();
  for (var i=0;i<jsonDIdAry.length;i++) {
    var attr = "_did="+jsonDIdAry[i];
    var _domAry = $('['+attr+']').toArray();
    domAry[i] = _domAry;
  }
  //JsonDSize>0说明是有数据需要请求的
  if (monitor.monitorJsonDSize&&monitor.monitorJsonDSize>0) {
    //用于储存已经显示过的id
    monitor._alreadyShownId = [];
    monitor.monitorDrawId = setInterval(retrievalJsonDJson(domAry),500);
  } else {
    return;
  }
}
/**
 * 检索是否有已经得到的jsonDjson
 * 从而进一步解析。
 * @param domAry根据jsonDid分组后的dom数组
 */
function retrievalJsonDJson (domAry) {
  if (jsonDInfoArray.length==0) {//一条数据都没取到
    return;
  } else {
    // 当已经全部显示完时关闭定时任务
    if (monitor._alreadyShownId.length==monitor.monitorJsonDSize) clearInterval(monitor.monitorDrawId);
    for (var i=0;i<jsonDInfoArray.length;i++) {
      var jsonDInfo = jsonDInfoArray[i];
      if (monitor._alreadyShownId.toString().indexOf(jsonDInfo.id)==-1) {
        //起setInterval,检查d元素是否到位
        if (jsonDInfo.json!=null&&jsonDInfo.json!="") {
          for (var k=0;k<domAry.length;k++) {
            var _domAry = domAry[k];
            if (jsonDInfo.id == $(_domAry[0]).attr('_did')) {
              //相等，说明这个_domAry里面全是这个id的dom，然后进行解析，否则进入下个循环
              for (var j=0;j<_domAry.length;j++) {
                var _DATA = jsonDInfo.json._DATA;
                parseEle($(_domAry[j]),_DATA);
              }
            }
          }
          monitor._alreadyShownId.push(jsonDInfo.id);
        }
      }
    }
  }
}

/**
 * 解析元素，根据showType进行拼接显示效果
 * jQobj:需要解析的元素
 * _DATA：对应的数据
 */
function parseEle(jQobj, _DATA){
  var showType = jQobj.attr('showType');
  if (showType=="text") drawText(jQobj, _DATA);
  else if (showType=="value") drawValue(jQobj, _data);
  else if (showType=="table") drawTable(jQobj, _data);
  else if (showType=="pie") drawPie(jQobj, _data);
  else if (showType=="line") drawLine(jQobj, _data);
  else if (showType=="bars") drawBar(jQobj, _data);
  else if (showType=="map_pts") drawMapPts(jQobj,_data); //地图画点
  else if (showType.lastIndexOf("first(")!=-1) drawFirst(showType,jQobj, _data);
}

//以下为解析showType代码
/**
 * showType=table
 */
function drawTable (jQobj,_data) {
  var table_body = _data.tableData.tableBody;
  var table_titles = _data.tableData.titles;
  var colAry = new Array();
  for (var i=0;i<table_titles.length;i++) {
    //getAllPrpos得到对应的属性
    var titlePrpos = getAllPrpos(table_titles[i]);
    var col = new Object();
    col.field = titlePrpos.prposName;
    col.title = titlePrpos.prposValue;
    col.width = 100;
    colAry.push(col);
  }
  var width = (100*(table_titles.length))+50;
  jQobj.attr('style','width:'+width+'px;');
  jQobj.datagrid({
    singleSelect:true,
    collapsible:true,
    columns:[colAry],
    data:table_body
  });
}

/**
 * showType=value
 */
function drawValue (jQobj,dataAry) {
  jQobj.html(dataAry);
}

/**
 * showType=text
 * text解析主要解析value和decorateView
 * value的复杂程度：value='quotas[1]::!first(3|num)'
 * decorateView的复杂程度：decorateView='{#category#}占#percent(num)#%::{suffix:’、’}'
 * @param jQobj：对象
 * @param _DATA:数据 
 */
function drawText (jQobj,_DATA) {
  //value
  var value = jQobj.attr('value');
  value = removeSpace(value);
  //要显示的最终数据
  var showData = "";
  if (value.indexOf("::")==-1) {
    valIndex = value;
    eval("var _data=_DATA."+valIndex);
    showData = _data;
  } else {
    //取值规则
    var valDemand = value.substring(value.indexOf("::"),value.length);
    //取值位置
    var valIndex  = value.substring(0,value.indexOf("::"));
    //取值范围
    var limit = valDemand.substring(valDemand.indexOf("first("),valDemand.indexOf("|"));
    // oder ->2：升序;1降序
    var sortType = 1;
    if (valDemand.indexOf("!")==-1) sortType = 2;
    // 排序列
    var sortCol = valDemand.substring(valDemand.indexOf("|"),valDemand.indexOf(")"));
    eval("var _data=_DATA."+valIndex);
    // oder ->up：升序;down降序
    if (valDemand.indexOf("!")==-1) oder = "up";
    showData = getDataByDemand(_data,sortType,limit,sortCol);
  }
  //decorateView
  var decorateView = jQobj.attr('decorateView');
  decorateView = removeSpace(decorateView);
  
  jQobj.html(dataAry);
}
//以上为解析showType代码

//以下为公共部分代码==============
/**
 * @param _data数据
 * @param sortType升降序
 * @param limit范围
 * @param sortCol排序列
 */
function getDataByDemand (_data,sortType,limit,sortCol) {
  //_data中的数据部分
  eval("var tableData=_data.tableData");
  //原数据中的排序：srot
  eval("var sort=_data.sort");
  //原数据中的表头：titles
  eval("var titlrs=_data.titles");
  //原数据中的表身：tableBody
  eval("var tableBody=_data.tableBody");
  var retBody = [];
  if (sort!=""&&sort!=null) {//当jsonD中已排序的时候
    if (sortCol==sort.sortCol) {//排序列相同
      if (sortType==sort.sortType) {//排序方式相同
        if (limit>tableBody.length) {//范围和长度的比较
          ret = tableBody;
        } else {
          for ( var i=0;i<limit;i++) {
            retBody[i] = tableBody[i];
          }
        }
      } else {//排序方式不同相同
    	  
      }
    } else {//排序列不同
    	
    }
  } else {
	  //当jsonD中未排序的时候
  }
}
/**
 * 去除str中的空格
 * exp:带空格的字符串
 */
function removeSpace(str){
  return str.replace(/\s/g,'');
}

/**
 * 用来获取title中对象的属性名和属性值
 * obj:仅限一个对象，
 * return 返回一个由prposName(属性名)和prposValue(属性值)的新对象
 */
function getAllPrpos (obj) {
  // 用来保存所有的属性名称和值 
  var retProps = new Object();
  // 开始遍历
  for ( var p in obj ) {
    // 方法
    if ( typeof (obj[p]) == " function " ) {
      obj[p]();
    } else {
      // p 为属性名称，obj[p]为对应属性的值 
      retProps.prposName = p;
      retProps.prposValue = obj[p];
    }
  }
  return retProps;
}
//以上为公共部分代码=================

/**
 * 替换content
 * 1、替换元素d->div
 * 2、得到这段content中的did的值的数组
 * 
 * @param matchAry 待处理的content数组
 * @param  mismatchAry不用处理的content数组
 * 对于关系
 */
function reportContentParse(matchAry,mismatchAry){
  //要返回的新的content
  var newContent="";
  //这段content中did的值拼接成的字符串
  var didStr = "";
  for (var i=0;i<matchAry.length;i++) {
    //content转为dom，方便操作
    var ele = $(matchAry[i]);
    //设置元素的Id，为showType+i
    var id = ele.attr('showType')+i;
    
    //把did拼接成字符串
    var did = ele.attr('did')+",";
    if (didStr=="") didStr = didStr +did;
    else if (didStr.indexOf(did)==-1) didStr = didStr +did;
    
    //单个的标签 
    var matchStr = matchAry[i];
    matchStr = matchStr.replace(/did/,"id='"+id+"' _did");
    //showType = text的时候用<span>来替换，其他的showType类型都用<div></div>来替换
    if (ele.attr('text')=="value"){
      //在细分为<d/>和<d></d>这两种情况
      if (matchStr.match(/></)!=null){
        matchStr = matchStr.replace(/<d\s{1}/,"<span ");
        matchStr = matchStr.replace(/\/d>/,"/span>");
      } else {
        matchStr = matchStr.replace(/<d\s{1}/,"<span ");
        matchStr = matchStr.replace(/\/>/,"></span>");
      }
    } else {
      //在细分为<d/>和<d></d>这两种情况
      if (matchStr.match(/></)!=null){
        matchStr = matchStr.replace(/<d\s{1}/,"<div ");
        matchStr = matchStr.replace(/\/d>/,"/div>");
      } else {
        matchStr = matchStr.replace(/<d\s{1}/,"<div ");
        matchStr = matchStr.replace(/\/>/,"></div>");
      }
    }
    //用concat方法把不用处理的字符串和处理后的content拼接起来，组成新的content
    newContent = (newContent.concat(mismatchAry[i],matchStr));
  }
  //把结尾拼一下也可以在循环内拼
  newContent = newContent.concat(mismatchAry[mismatchAry.length-1]);

  //把did转成数组
  if (didStr.lastIndexOf(",")==didStr.length-1) didStr = didStr.substring(0, didStr.lastIndexOf(","));
  var didAry = didStr.split(",");
  if (didAry==null) didAry.push("");
  var retObj = new Object();
  retObj.didAry = didAry;
  retObj.newContent = newContent;
  return retObj;
}
    
/**
 * 构建report主体和树
 */
function buildSegmentGroup(jObj, segArray, treeLevel, parent) {
  //判断segArray
  if (segArray==null||segArray=="") return "segArry 为空!";
  //判断eleId
  if (jObj==null) return "未知的eleId";
  //segGroup
  var segGroup = $('<div id="segGroup_'+treeLevel+'" class="segGroup_'+treeLevel+'"/></div>');
  //subSeg分为3个部分，1、title部分；2、content部分；3、subSeg部分
  for (var i=0;i<segArray.length;i++) {
    var segId = segArray[i].id;
    var _didAry = null;

    //title部分
    var titleText = "";
    if (segArray[i].title) titleText = segArray[i].title;
    else titleText = segArray[i].name;
    var titleId = segId+'_title';
    //segTitle
    var segTitle = $('<div id="'+titleId+'" class="segTitle_'+treeLevel+'"></div>');
    segTitle.html(titleText);
    segGroup.append(segTitle);
    
    //content部分
    if (segArray[i].content) {

      //1、创建元素
      //处理之前的content
      var content = segArray[i].content;
      //处理后的content
      var newContent = "";
      //content Id
      var contentId = segId+'_frag'+i;
      //content 元素
      var segContent= $('<div id="'+contentId+'" class="segContent_'+treeLevel+'"/></div>');

      //2、根据匹配要求把content分割为两个数组,matchAry:符合规则的部分,mismatchAry不符合的部分
      //match方法返回一个匹配到的数组，dSize为d标签个数
      /**
       * 以下这段逻辑的算法：
       * 把整个content拆分两个数组，一个是匹配到<d>的数组matchAry，
       * 令一个未匹配到<d>的数组misatchAry，分割顺序是一对一的关系，且以
       * mismatch为开头和结尾，如没有则以""来补位。
       */
      if (content.match(/<d\s./g)&&content.match(/<d\s./g).length>0) {
        var dSize = content.match(/<d\s./g).length;
        //匹配规则
        var reg = /<d\s.*?(><\/d>|\/>)/g;
        //用于存放匹配到的数组
        var matchAry = new Array();
        //用于存放未匹配的数组
        var mismatchAry = new Array();
        //mismatch的起始位置
        var mismatchStart = 0;
        for (var s=0;s<dSize;s++) {
          //得到匹配的数组0位置为所需
          var matchStrAry = reg.exec(content);
          //匹配起始位置
          var start = matchStrAry.index;
          //匹配结束位置
          var end = reg.lastIndex;
          //存放到匹配数组中
          matchAry[s] = matchStrAry[0];
          //每次只取前面的
          var mismatchStr;
          //用mismatch的起始位置和match的起始位置比较，来确定mismatch是否需要补位
          if (s!=dSize-1) {
            //mismatchStart==start:content是以d开头的或者是两个d是连着的
            //mismatchStart!=start:是正常的情况，不是以d开头或不是两个d
            if (mismatchStart==start) {
              mismatchStr = "";
              mismatchAry[s] = mismatchStr;
            } else {
              mismatchStr = content.substring(mismatchStart,start);
              mismatchAry[s] = mismatchStr;
            }
          } else {
            //是最后一个d元素的时候，mismatchStart==start说明content是以d元素结束的，也可能是两个d连着的因为是最后一个需要补两个位
            if (mismatchStart==start) {
              mismatchStr = "";
              mismatchAry[s] = mismatchStr;
              mismatchAry[s+1] = ""; 
            } else {
              //是最后一个d元素的时候，mismatchStart！=start说明不需要补位
              mismatchStr = content.substring(mismatchStart,start);
              mismatchAry[s] = mismatchStr;
              var ending = content.substring(end,content.length);
              mismatchAry[s+1] = ending;
            }
          }
          mismatchStart = end;
        }
        var retObj = reportContentParse(matchAry,mismatchAry);
        _didAry = retObj.didAry;
        newContent = retObj.newContent;
        segContent.html(newContent);
        segGroup.append(segContent);
      }
    }

    //tree部分
    var treeNode = new Object();
    if (segArray[i].name) treeNode.text = segArray[i].name;
    else if (segArray[i].title) treeNode.text=$(segArray[i].title).html();
    if (treeNode.text&&treeNode.text!="") {
      //treeId
      treeNode.id = "_tree_"+segArray[i].id;
      //对应的segId
      treeNode.segId = segArray[i].id;
      //对应的子节点
      treeNode.children = new Array();
      //本身和子节点用到的did数组
      treeNode.didAry = _didAry;
      //要定位的结点的id
      treeNode.positionId = titleId;
      if (parent==null) {
        segTree[i]=treeNode;
      } else {
        parent.children[i] = treeNode;
      }
      if (treeLevel>0) {
        var pDataAry;
        if (parent.dataAry) {
          pDataAry = parent.dataAry.toString();
          for (var v=0;v<_didAry.length;v++) {
            if (pDataAry.indexOf(_didAry[v])==-1) pDataAry = pDataAry+","+_didAry[v];
          }
          if (pDataAry.indexOf(",")==pDataAry.length-1) pDataAry = pDataAry.substring(0, pDataAry.indexOf(","));
          parent.dataAry = pDataAry.split(",");
        } else {
          pDataAry = "";
          if(_didAry){
            for (var v=0;v<_didAry.length;v++) {
              if (pDataAry!="") {
                if (pDataAry.indexOf(_didAry[v])==-1) pDataAry = pDataAry+_didAry[v]+",";
              } else pDataAry = pDataAry+_didAry[v]+",";
              if (pDataAry==",") pDataAry="";
            }
            if (pDataAry.indexOf(",")==pDataAry.length-1) pDataAry = pDataAry.substring(0, pDataAry.indexOf(","));
            parent.dataAry = pDataAry.split(",");
          }
        }
      }
    }
    
    //subSeg部分 这部分写在树的下面是因为要使用treeNode作为参数
    if (segArray[i].subSeg) buildSegmentGroup(segGroup, segArray[i].subSeg, treeLevel+1, treeNode);
  }
  jObj.append(segGroup);
  return segGroup;
}

/**
 * 1、解析_DLIST为jsonDInfo
 * 2、得到jsonD数据
 */
function resolveDLIST(_DLIST){
  // 判断_DLIST是否符合标准
  if (_DLIST==null||_DLIST==""||_DLIST.length<=0) return null;
  //所有的jsonId
  monitor.monitorJsonDSize = _DLIST.length;
  monitor._getJsonDIdAry = [];
  for (var i=0;i<_DLIST.length;i++) {
    //jsonDInfo 囊括了jsonD的信息
    var jsonDInfo = new Object();
    jsonDInfo.id = _DLIST[i]._id;
    jsonDIdAry[i] = jsonDInfo.id;
    jsonDInfo.url =  _DLIST[i]._url;
    //jsonD_code有时有，有时没有，负值的时候判断下
    if (_DLIST[i]._jsonD_code!=""&&_DLIST[i]._jsonD_code!=null) jsonDInfo.jsonD_code = _DLIST[i]._jsonD_code;
    else jsonDInfo.jsonD_code = "";
    jsonDInfoArray.push(jsonDInfo);
    //jsonDjson 用于存贮jsonD的数据 会在后面赋值
    monitor.monitorJsonDId = setInterval(getJsonDJson(jsonDInfo),500);
  }
}

/**
 * 获取jsonDInfo.json
 * @param jsonDInfo
 */
function getJsonDJson(jsonDInfo) {
  if (monitor._getJsonDIdAry.length==monitor.monitorJsonDSize) clearInterval(monitor.monitorJsonDId);
  var dId = jsonDInfo.id;
  for (var k=0;k<jsonDInfoArray.length;k++) {
    //当已存数组中不包含该id，并且找到对应的jsoDinfo时，进行获取数据
    if(monitor._getJsonDIdAry.toString().indexOf(dId)==-1&&dId==jsonDInfoArray[k].id){
      //处理rul
      var _url;
      jsonDInfo = jsonDInfoArray[k];
      if ((jsonDInfo.url).indexOf("/")!=-1) _url = deployName+"/"+jsonDInfo.url;
      else _url = deployName+jsonDInfo.url;
      //uri
      var pData = {'uri':jsonDInfo.url};
      $.ajax({type:"post",url:_url,data:pData,async:true,dataType:"text",
        success:function(json){
          var jsonDObj = str2JsonObj(json);
          if(jsonDObj.jsonType==1){
            jsonDInfo.json = jsonDObj.data;
            monitor._getJsonDIdAry.push(dId);
          }else{
            alert("获取数据错误");
          }
        },
        error:function(errorData){
          alert("err:"+errorData);
        }
      });
    }
  }
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