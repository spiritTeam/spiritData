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
  //用于存放dataId,方便最后一不进行搜索dom
  var dataIdAry;
  
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
          dataIdAry = getJsonD(_DATA);
          $('#rTitle').html(_HEAD.reportName);
          buildSegmentGroup($('#reportFrame'), _TEMPLET, level, null);
          //显示 
          show();
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
   * 在content完成后，对新的content中元素进行解析
   * 1、根据dataId抓取dom
   * 2、起setInterval
   */
  function show(){
    //根据dataIdAry中的id抓取dom
    var domAry = new Array();
    for(var i=0;i<dataIdAry.length;i++) {
      var attr = "_data="+dataIdAry[i];
      var _domAry = $('['+attr+']').toArray();
      domAry[i] = _domAry;
    }
    //用于判断执行和关闭setInterval
    var _dataIdAry = new Array();
    //起setInterval
    var intervalId = setInterval(function(){
      if(_dataIdAry.length==dataIdAry.length) {alert("in close");
      // TODO 这里没有弹出关闭框？但确实关闭了
        clearInterval(intervalId);
      }
      for(var i=0;i<jsonDInfoArray.length;i++){
        var jsondInfo = jsonDInfoArray[i];
        if(_dataIdAry.toString().indexOf(jsondInfo.id)==-1){
          //起setInterval,检查d元素是否到位
          if(jsondInfo.jsond!=null&&jsondInfo.jsond!=""){
            for(var k=0;k<domAry.length;k++){
              var _domAry = domAry[i];
              var _DATA = jsondInfo.jsond._DATA;
              if(jsondInfo.id == $(_domAry[0]).attr('_data')){
                //相等，说明这个_domAry里面全是这个id的dom，然后进行解析，否则进入下个循环
                for(var j=0;j<_domAry.length;j++){
                  _dataIdAry.push(jsondInfo.id);
                  parseEle($(_domAry[j]),_DATA);
                }
              }
            }
          }
        }
      }
    },200);
  }
  
  /**
   * 向后台请求jsond,先根据_DATA中的数据，组成jsondInfo,放
   * 入数组中，然后循环数组，向后台请求数据，
   * _DATA:jsondUrl的请求数组
   */
  function getJsonD(_DATA){
    if(_DATA==null||_DATA==""||_DATA[0]==null||_DATA[0]=="") return null;
    var i=0;
    var dataIdAry = new Array();
    for(;i<_DATA.length;i++){
      //jsondInfo
      var jsondInfo = new Object();
      jsondInfo.id = _DATA[i]._id;
      dataIdAry[i] = jsondInfo.id;
      jsondInfo.url =  _DATA[i]._url;
      //jsonD_code有时有，有时没有，负值的时候判断下
      if(_DATA[i]._jsonD_code!=""&&_DATA[i]._jsonD_code!=null) jsondInfo.jsonD_code = _DATA[i]._jsonD_code;
      else jsondInfo.jsonD_code = "";
      jsondInfo.jsond = null;
      jsonDInfoArray[i] = jsondInfo;
    }
    //用于存储已得到jsond的jsondId
    var jsonDIdAry = new Array();
    //启动请求数据线程
    var intervalId = setInterval(function(){
      //如果两个数组长度一样,关闭线程
      if(jsonDIdAry.length==jsonDInfoArray.length) clearInterval(intervalId);
      //每次循环都将id拼起来，方便查找，
      for(var k =0;k<jsonDInfoArray.length;k++){
        var str = "";
        if(jsonDIdAry.length>0){
          for(var y = 0;y<jsonDIdAry.length;y++) str+= jsonDIdAry[y]+"";
        }
        var id = jsonDInfoArray[k].id;
        //=-1表示未请求过，
        if(str.lastIndexOf(id)==-1){
          $.ajax({type:"post",url:jsonDInfoArray[k].url,async:false,dataType:"json",
            success:function(json){
              var jsondJsonObj=str2JsonObj(json);
              if(jsondJsonObj.jsonType==1){
                jsonDInfoArray[k].jsond = jsondJsonObj.data;//allFields(jsondJsonObj.data)
                jsonDIdAry.push(jsonDInfoArray[k].id);
              }else{
                $.messager.alert("提示",jsonData.message,'info');
              }
            },error:function(XMLHttpRequest, textStatus, errorThrown ){
              $.messager.alert("提示","未知错误！",'info');
            }
          });
        }
      }
    },500);
    return dataIdAry;
  }
  
  /**
   * 通过遍历实现树和主界面的构建
   * jQbj:jQuery对象，指代的是根节点
   * segArray：数据数组，
   * treeLevel：层数,
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
    for (var i=0;i<segArray.length;i++) {
      var segId = segArray[i].id;
      //第一层的时候title中没有style标签，第二层有 ,可以跟晖哥商量下title标签问题？
      //title是放在div下面的span中还是直接放在div中
      var _dataAry;
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
          var eleS = content.match(/<d\s./g);
          var reg = /<d\s.*?(><\/d>|\/>)/g;
          var pendingAry = new Array();
          var subAry = new Array();
          var subStart=0; 
          for(var s=0;s<eleS.length;s++){
            var pendingStr = reg.exec(content);
            var start = pendingStr.index;
            var end = reg.lastIndex;
            pendingAry[s] = pendingStr[0];
            //每次只取前面的
            var subStr;
            if(s!=eleS.length-1){
              //不是最后一个d元素的时候
              if(subStart==start) {
                //subStart==end说明content是从d元素开始的,也可能是两个d连着的
                subStr = "";
                subAry[s] = subStr;
              }else{
                subStr = content.substring(subStart,start);
                subAry[s] = subStr;
              }
            }else{
              //是最后一个d元素的时候
              if(subStart==start) {
                //subStart==end说明content是从d元素开始的,也可能是两个d连着的
                subStr = "";
                subAry[s] = subStr;
                subAry[s+1] = ""; 
              }else{
                subStr = content.substring(subStart,start);
                subAry[s] = subStr;
                var ending = content.substring(end,content.length);
                subAry[s+1] = ending;
              }
            }
            subStart = end;
          }
          var retObj = templetContentParse(pendingAry,subAry);
          _dataAry = retObj._dataAry;
          newContent = retObj.newContent;
        }
        segContent.html(newContent);
        segGroup.append(segContent);
      }
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
        treeNode.dataAry=_dataAry;
        if (parent==null) {
          segTree[i]=treeNode;
        } else {
          parent.children[i] = treeNode;
        }
        if(treeLevel>1){
          var pDataAry = parent.dataAry.toString();
          for(var v=0;v<_dataAry.length;v++){
            if(pDataAry.indexOf(_dataAry[v])==-1) pDataAry = pDataAry+_dataAry[v]+",";
          }
          parent.dataAry = pDataAry.split(",");
        }
      }
      buildSegmentGroup(segGroup, subSegs, treeLevel+1, treeNode);
    }
    jObj.append(segGroup);
    return segGroup;
  }
  
  /**
   * 把</d>或这<d></d>,根据showType是否=value，
   * 来替换成<span>或者<div>;
   * pendingAry：待处理的d元素数组，
   * subAry：不用处理的数组
   * return：返回处理完成后的content
   */
  function templetContentParse(pendingAry,subAry){
    var newContent="";
    var _dataStr = "";
    for(var i=0;i<pendingAry.length;i++){
      var ele = $(pendingAry[i]);
      var _data = ele.attr('data')+",";
      if(_dataStr=="") _dataStr = _dataStr +_data;
      else if(_dataStr.indexOf(_data)==-1) _dataStr = _dataStr +_data;
      var pendingStr = pendingAry[i];
      pendingStr = pendingStr.replace(/data/,"_data");
      if(ele.attr('showType')=="value"){
        if(pendingStr.match(/></)!=null){
          pendingStr = pendingStr.replace(/<d\s{1}/,"<span ");
          pendingStr = pendingStr.replace(/\/d>/,"/span>");
        }else{
          pendingStr = pendingStr.replace(/<d\s{1}/,"<span ");
          pendingStr = pendingStr.replace(/\/>/,"></span>");
        }
      }else{
        if(pendingStr.match(/></)!=null){
          pendingStr = pendingStr.replace(/<d\s{1}/,"<div ");
          pendingStr = pendingStr.replace(/\/d>/,"/div>");
        }else{
          pendingStr = pendingStr.replace(/<d\s{1}/,"<div ");
          pendingStr = pendingStr.replace(/\/>/,"></div>");
        }
      }
      newContent = (newContent.concat(subAry[i],pendingStr));
    }
    newContent = newContent.concat(subAry[subAry.length-1]);
    var _dataAry = _dataStr.split(",");
    var retObj = new Object();
    retObj._dataAry = _dataAry;
    retObj.newContent = newContent;
    return retObj;
  }
  
  /**
   * 用来获取title中对象的属性名和属性值
   * 方便easyui table的显示
   * obj:仅限一个对象，
   * return 
   */
  function getAllPrpos (obj) { // TODO 该方法未整理
    // 用来保存所有的属性名称和值 
  var retProps = new Object();
    var props = "" ; 
    // 开始遍历 
    for ( var p in obj ){
      // 方法 
      if ( typeof (obj[p]) == " function " ){
        obj[p]();
      } else { 
        // p 为属性名称，obj[p]为对应属性的值 
        //props += p+ " = " + obj[p] + "\t" ;
        retProps.prposName = p;
        retProps.prposValue = obj[p];
      } 
    } 
    // 最后显示所有的属性
    //alert(props);
    return retProps; 
  } 
  /**
   * 解析元素，根据showType进行拼接显示效果
   * jQobj:需要解析的元素
   * _DATA：对应的数据
   */
  function parseEle(jQobj, _DATA){
    //得到showType
    var showType = jQobj.attr('showType');
    //指向jsond中的数据
    var value = jQobj.attr('value');
    //根据value得到数据
    eval("var _data=_DATA."+value);
    //st = value
    if(showType=="value") jQobj.html(_data);
    //st = tbale
    else if(showType=="table"){
      var table_body = _data.tableData.tableBody;
      var table_titles = _data.tableData.titles;
      var colAry = new Array();
      for(var i=0;i<table_titles.length;i++){
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
        // TODO 这数据是有问题的，取不到title
        columns:[colAry],
        data:table_body
      });
    //st = pie
    }else if(showType=="pie"){
      //特有属性
      var pieLabel = jQobj.attr('label');
      var pieData = jQobj.attr('data');
      var decorateView = jQobj.attr('decorateView');
      var ary = [];
      var pie_dataBody = _data.tableData.tableBody;
      for(var i=0;i<pie_dataBody.length;i++){ 
        eval("var _pie_label=pie_dataBody[i]."+pieLabel);
        eval("var _pie_data=pie_dataBody[i]."+pieData);
        ary[i] = {label:_pie_label,data:_pie_data};
      }
      jQobj.attr('style','height:150px;width:150px;')
      $.plot(jQobj, ary, {
        series:{
          pie:{
            show:true,
            radius:1,
            label:{
              show:true,
              radius:2/3,
              formatter:function(label, series){
                return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'+label+'<br/>'+Math.round(series.percent)+'%</div>';
              },
              threshold:0.1
            }
          }
        },
        legend:{
          show:false
        }
      });//st = line
    }else if(showType=="line"){
      var xAxis = jQobj.attr('xAxis');
      var yAxis = jQobj.attr('yAxis');
      var line_dataBody = _data.tableData.tableBody;
      var id = jQobj.attr('id');
      var ary = [];
      var height = 20*line_dataBody.length;
      var width = 40*line_dataBody.length;alert(height+width);
      jQobj.attr('style','width:'+width+'px;height:'+height+'px;');
      for(var i=0;i<line_dataBody.length;i++){
        eval("var _x = line_dataBody[i]."+xAxis);
        eval("var _y = line_dataBody[i]."+yAxis);
        ary[i] = [_x,_y];
      }
      $.plot(jQobj, [{label:"最小值", data:ary}],{
        series: {
          lines: { show: true },
          points: { show: true }
        },
        xaxis: {
          mode: "categories",
          autoscaleMargin: 0.05,
          tickLength: 0
        },
        yaxis:{
          show:true,
          position:'left',
          tickLength:40,
          tickDecimals:0
        },
        legend:{show:false}
      });
    }else if(showType=="bars"){return;
      function bars(id,json){
        var ary = [];
        for(var i=0;i<json.length;i++){
          ary[i] = [json[i].sex,json[i].num];
        }
        $.plot("#"+id, [ary], {
          series: {
            bars: {
              show: true,
              barWidth: 0.3,
              align: "center",
              fill:0.3
            }
          },
          xaxis: {
            mode: "categories",
            autoscaleMargin: 0.05,
            tickLength: 0
          },
          yaxis:{
            show:true,
            position:'left',
            tickLength:40,
            tickDecimals:0
          },
          legend:{ show:true, position: "sw" }
        });
      }
    }else {
      //alert("暂不支持showType为"+showType+"类型的解析");
    }
    
    //decorateView
    jQobj.attr('decorateView');
    //dom元素中的元素属性
    jQobj.attr('id');
    //var e = $("<div></div>");
    //alert("e="+e[0].outerHTML);
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
