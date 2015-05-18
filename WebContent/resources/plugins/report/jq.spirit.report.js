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
/**
 * 百度地图全局变量
 */
var CONST_MAP_BAIDU = "BAIDU";
/**
 * monitor用于监控setInterval
 */
var monitor = new Object();
//以上为全局变量部分=========
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
          //$('#rTitle').html(_HEAD._reportName+'<input id="download" onclick="$.download(\''+reportId+'\',\''+deployName+'\');" type="button" value="保存报告"/>');
          $('#rTitle').html(_HEAD._reportName);
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
 * 对元素按照didId分类
 * 通过线程控制对元素的解析
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
	  // TODO 人工停顿
    alert(monitor._alreadyShownId.length);
    if (monitor._alreadyShownId.length==monitor.monitorJsonDSize) {clearInterval(monitor.monitorDrawId);}
    for (var i=0;i<jsonDInfoArray.length;i++) {
      var jsonDInfo = jsonDInfoArray[i];
      var shownIdAry = monitor._alreadyShownId;
      if (shownIdAry.toString().indexOf(jsonDInfo.id)==-1) {
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
  else if (showType=="value") drawValue(jQobj, _DATA);
  else if (showType=="table") drawTable(jQobj, _DATA);
  else if (showType=="pie") drawPie(jQobj, _DATA);
  else if (showType=="line") drawLine(jQobj, _DATA);
  else if (showType=="bar") drawBar(jQobj, _DATA);
  else if (showType=="map_pts") drawMapPts(jQobj,_DATA); //地图画点
  else if (showType=="radar") drawRadar(jQobj,_DATA);//雷达图
}

//以下为解析showType代码==============
/**
 * showType==radar 地图画点
 * 实现方法，echarts
 * 样例 showType:radar <d did='0' showType='radar' param='{^xAxis^:^category^ , ^yAxis^:^num^ , ^max^:^100^}' value='quotas[0]' decorateView='#num#'/>
 */
function drawRadar(jQobj,_DATA){

  //基本属性部分
  var value = jQobj.attr('value');
  eval("var _data=_DATA."+value);
  //获取param属性
  var param =  jQobj.attr('param');
  var paramJson = str2Json(param);
  //radar半径显示xAxis的值
  var xAxis = paramJson.xAxis;
  //radar半径的数值点显示yAxis的值
  var yAxis = paramJson.yAxis;
  //半径最大值
  var max = paramJson.max;
  //title
  var title = _data.titleName;
  //data
  var data = _data.tableData.tableBody;
  //初始化长宽高
  var width=500;
  var height=500;
  jQobj.attr('style','width:'+width+'px;height:'+height+'px;');
  
  //构建radar数据结构
  //指示器部分数据：由max和xAxis,结构由obj{text,max}的数组组成
  var indicatorData = [];
  //seriesData：主要由yAxis组成,结构由obj{value[],name}的数组组成，这里的一个obj代表一组数据，会根据indicatorData一一对应
  var seriesData = [];
  //单个的seriesData的value数组
  var sDataValAry = [];
  for (var i=0;i<data.length;i++) {
    var rowData = data[i];
    //iData
    var iData = new Object();
    iData.text = rowData[xAxis];
    iData.max = max;
    indicatorData.push(iData);
    sDataValAry.push(rowData[yAxis]);
  }
  //sData
  var sData = new Object();
  sData.value = sDataValAry;
  sData.name = title;
  seriesData.push(sData);
  
  try{
    require(
      [
       'echarts',
       'echarts/chart/radar'
      ],
      function (ec) {
        //获取案件分布地图对象
        var radar = ec.init(jQobj[0]);
        option = {
          title : {
            text: title,
          },
          tooltip : {
            trigger: 'axis'
          },
          legend: {
            orient : 'vertical',
            x : 'right',
            y : 'bottom',
            data:[]
          },
          toolbox: {
            show : true
          },
          polar : [{
            indicator : indicatorData
          }],
          calculable : true,
          series : [{
            name: title,
            type: 'radar',
            data : seriesData
          }]
        };
        radar.setOption(option);
      }
    );
  }catch(e){
    $.messager.alert("draw map points err", e.message, "error");
  }
}
/**
 * showType==map_pts 地图画点
 * 样例:showType:map_pts <d did='0' showType='map_pts' param='{^X^:^coordX^, ^Y^:^coordY^ ,^Z^:^coordZ^,^mapType^:^BAIDU^}' value='quotas[0]'/>
 */
function drawMapPts(jQobj,_DATA){
  var value = jQobj.attr('value');
  eval("var _data=_DATA."+value);
  //获取param属性
  var param =  jQobj.attr('param');
  var paramJson = str2Json(param);
  try{
    //所使用的地图
    var sys = paramJson.mapType;
    if(sys == undefined || sys==CONST_MAP_BAIDU){
      drawBaiDuPts(jQobj,_data,paramJson);
    }else{
      $.messager.alert("unsupported map type:", sys, "error");
    }
  }catch(e){
    $.messager.alert("draw map points err", e.message, "error");
  }
}

/**
 * mapType==baidu
 * @param jQobj jq对象
 * @param _data 数据
 * @param param 参数param='{^X^:^coordX^, ^Y^:^coordY^ ,^Z^:^coordZ^,^mapType^:^BAIDU^}'
 */
function drawBaiDuPts(jQobj,_data,param){
  //设置DOM对象的宽度和高度
  var width=500;
  var height=500;
  jQobj.attr('style','width:'+width+'px;height:'+height+'px;');
  var mapDivId = jQobj.attr('id')+"_map";
  jQobj.append('<div id="'+mapDivId+'"/></div>');
  $('#'+mapDivId).attr('style','width:'+(width-2)+'px;height:'+(height-2)+'px;');
  //获取坐标列 x、y、z、
  var xCol = param.X;
  var yCol = param.Y;
  var zCol = param.Z;
  //titleName
  var titleName = _data.tableData.titleName;
  //data
  var data = _data.tableData.tableBody;
  //decorateView 这个decorateView可能不太需要，因为向显示的数据直接标注出来就好，插件可以直接显示
  /**
   * decorateView = "
   * <tr><td>姓名</td><td>#xm#</td></tr>
   * <tr><td>身份证</td><td>#sfz#</td></tr>
   * <tr><td>性别</td><td>#sb#</td></tr>
   * <tr><td>城市</td><td>#city#</td></tr>
   * ::{^envelopeType^:^table^}"
   */
  var decorateView = jQobj.attr('decorateView');
  decorateView = removeSpace(decorateView);
  //根据decorateView得到displayRule
  //displayRule显示规则
  if (decorateView) {
    if (decorateView.indexOf("::")!=-1) {//有envelopeType的情况
      //model 这个是decorateView的前部分，用于对找出colName和和显示名的关系，
      var model = decorateView.substring(0,decorateView.indexOf("::"));
      // displayClaim 显示要求
      var displayReq = decorateView.substring(decorateView.indexOf("::")+2,decorateView.length);
      if (displayReq) {
        //把要求转换成json串，方便得到模型
        var displayReqObj = str2Json(displayReq);
        //按行还是按table解析
        var envelopeType = displayReqObj.envelopeType;
        //分割后的model
        var subModelAry = resolveMapPtsDecorateView(model,envelopeType);
        
        //初始化geoCoord数据，用于点的显示
        //无论envelopeType是否是table，data中添加的name属性均为subModel的第一个
        var colName = subModelAry[0]._colName;
        var geoCoordAry = new Object();
        for (var k=0;k<data.length;k++) {
          var _d = data[k];
          var x_axis = _d[xCol];
          var y_axis = _d[yCol];
          var z_axis = _d[zCol];
          //和data中的name对应？
          var _name = _d[colName];
          data[k].name = _name;
          data[k].value = "";
          var axixAry = [];
          if (x_axis!=null&&x_axis!=""&&y_axis!=null&&y_axis!="") {
            axixAry.push(parseFloat(x_axis));
            axixAry.push(parseFloat(y_axis));
            if (z_axis!=null&&z_axis!="") axixAry.push(parseFloat(z_axis));
          } else {
            alert("坐标获取失败");
            return;
          }
          geoCoordAry[_name] = axixAry;
        }
        
        //根据需求，加载插件
        require([
          'echarts',
          'echarts/chart/map'
          ],
          function (ec) {
            //获取案件分布地图对象
            var mapPtAnJian = ec.init($('#'+mapDivId)[0]);
            option = {
              title : {
                text: titleName,
                x:'center'
              },
              tooltip : {
                trigger: 'item',
                formatter: function(params){
                  var retStr="";
                  //标头
                  if (params!=""&&params!=null) {
                    if (params.seriesName==undefined || params.seriesName=="") {
                      params.series.tooltip.backgroundColor="rgba(0,0,0,0)";
                      return retStr;
                    } else {
                      var dataObj = params.data;
                      if (envelopeType=="table") {//table方式显示，
                        for (var i=0;i<subModelAry.length;i++) {
                          var subModel = subModelAry[i];
                          var _colName = subModel._colName;
                          retStr +=subModel._displayName+":"+dataObj[_colName]+'<br/>';
                        }
                      } else {//非table方式显示 
                        retStr = model;
                        for (var j=0;j<subModelAry.length;j++) {
                          var subModel = subModelAry[j];
                          var _colName = subModel._colName;
                          retStr = retStr.replace(subModel.colName,dataObj[_colName]);
                        }
                      }
                    }
                  }
                  return retStr;
                }
              },
              legend: {
                orient: 'vertical',
                x:'left',
                data:[titleName]
              },
              toolbox: {
                show: true,
                orient : 'vertical',
                x: 'right',
                y: 'center'
              },
              series : [
                {
                  name: titleName,
                  type: 'map',
                  mapType: 'china',
                  itemStyle:{
                    normal:{label:{show:true}},
                    emphasis:{label:{show:true}}
                  },
                  data:[],
                  markPoint:{
                    symbolSize:5,
                    effect : {
                      //show: true
                    },
                    itemStyle:{
                      normal:{
                        borderColor:'#87cefa',
                        borderWidth:1,
                        label:{
                          show:false
                        }
                      },
                      emphasis:{
                        borderColor:'#1e90ff',
                        borderWidth:5,
                        label:{
                          show:false  
                        }
                      }
                    },
                    data:data
                  },
                  geoCoord:geoCoordAry
                }
              ],
            };
            mapPtAnJian.setOption(option);
          }
        );
      }
    } else {
      alert("envelopeType解析失败");
    }
  } else {
    alert("解析MapPts_decorateView格式出错:decorateView格式出错！");
  }
}

/**
 * 根据已知模型，和封装类型，返回一个colName和display的对象数组
 * @param model 显示模型
 * @param envelopeType 封装类型：table/div
 */
function resolveMapPtsDecorateView(model,envelopeType){
  //把model安装一行一行的拆成数组
  var subModelAry = [];
  if (envelopeType=="table") {
    //拆分成单个的tr
    var trAry = model.match(/<tr>.*?<\/tr>/g);
    if (trAry!=null&&trAry.length>0) {
      //第一个是displayName,第二个是colName
      for (var i=0;i<trAry.length;i++) {
        var tdAry = trAry[i].match(/<td>.*?<\/td>/g);
        if (tdAry.length==2) {
          //为什么长度等于2，因为displayName和colName是一一对应的，如果不等于2的话说明少了其中某一项都无法找到正确的对应关系
          if (tdAry[0]=="<td></td>"||tdAry[1]=="<td></td>") {//当其中一个td中没有内容的时候，格式也是不正常的，同样无法找到对应关系
            alert("解析MapPts_decorateView格式出错：在第"+i+1+"行的<td></td>中的值为空");
          } else {
            var obj = new Object();
            //displayName
            obj.displayName = tdAry[0];
            //不带<td>标签的
            var _displayName = tdAry[0].substring(tdAry[0].indexOf('<td>')+4,tdAry[0].indexOf('</td>'));
            obj._displayName = _displayName;
            //colName
            obj.colName = tdAry[1];
            obj._colName = tdAry[1].substring(tdAry[1].indexOf('#')+1,tdAry[1].lastIndexOf('#'));
            subModelAry.push(obj);
          }
        } else {
          alert("解析MapPts_decorateView格式出错：在第"+i+1+"行的<td></td>格式不正确");
        }
      }
    } else {
      alert("解析MapPts_decorateView格式出错：原因可能是未匹配到完整的<tr></tr>标签");
    }
  } else {
    var ary = model.match(/#.*?#/g);
    for (var ii=0;ii<ary.length;ii++) {
      var _obj = new Object();
      var str = ary[ii];
      _obj._colName = str.substring(str.indexOf('#')+1,str.lastIndexOf('#'));
      _obj.colName =str;
      subModelAry.push(_obj);
    }
  }
  return subModelAry;
}
/**
 * 样例：showType:bar <d did='0' showType='bar' param='{^xAxis^:^category^, ^yAxis^:^num^}' value='quotas[0]' decorateView='#category#, #percent(num)#' />
 * showType==Bar
 */
function drawBar(jQobj,_DATA){
  var value = jQobj.attr('value');
  eval("var _data=_DATA."+value);
  //获取param属性
  var param =  jQobj.attr('param');
  var paramObj = str2Json(param);
  //x和y
  var xAxis = paramObj.xAxis;
  var yAxis = paramObj.yAxis;
  var bar_dataBody = _data.tableData.tableBody;
  var dataAry = [];
  var decorateView = jQobj.attr('decorateView');
  if (decorateView) decorateView = removeSpace(decorateView);
  var _ary = []; 
  var ary = decorateView.match(/#.*?#/g);
  for (var j=0;j<ary.length;j++) {
    var obj = new Object();
    obj.col = ary[j];
    obj._col = ary[j].substring(ary[j].indexOf('#')+1,ary[j].lastIndexOf('#'));
    _ary.push(obj);
  }
  var height = 50*bar_dataBody.length;
  var width = 70*bar_dataBody.length;
  jQobj.attr('style','width:'+width+'px;height:'+height+'px;');
  for (var i=0;i<bar_dataBody.length;i++) {
    eval("var _x = bar_dataBody[i]."+xAxis);
    eval("var _y = bar_dataBody[i]."+yAxis);
    dataAry[i] = [_x,_y];
  }
  $.plot(jQobj, [dataAry], {
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
    legend:{ show:true, position: "sw" },
    grid: {
      hoverable: true,
      clickable: true
    }
  });

  //添加滑动所需的框体并且绑定事件
  var hoverId = jQobj.attr('id')+'_hover';
  jQobj.append("<div id='"+hoverId+"' style='width:40px;height:20px;'></div>");
  jQobj.bind("plothover", function(event, pos, obj){
    if (!obj) {
      return;
    }
    var view = decorateView;
    if (_ary!=null&&_ary!="") {
      var dataIndex = obj.dataIndex;
      var rowData = bar_dataBody[dataIndex];
      for (var j=0;j<_ary.length;j++) {
        var _obj = _ary[j];
        var col = _obj._col;
        var val = rowData[col];
        if (val) {
          view = view.replace(_obj.col,val);
        } else {
          if (col.indexOf("percent(")!=-1) {
            col = col.substring(col.indexOf("(")+1,col.indexOf(")"));
            val = rowData[col];
            var sum = getSum(bar_dataBody,col);
            var percent = getPercent(val,sum,null);
            view = view.replace(_obj.col,percent);
          }
        }
      }
    }
    $("#"+hoverId).html("<span style='font-weight:bold; color:black;'>" +view+ "</span>");
  });
}
/**
 * showType=line
 * 样例：showType:line <d did='0' showType='line' param='{^xAxis^:^category^, ^yAxis^:^num^}' value='quotas[0]' decorateView='#num#'/>
 */
function drawLine(jQobj,_DATA){
  var value = jQobj.attr('value');
  eval("var _data=_DATA."+value);
  //获取param属性
  var param =  jQobj.attr('param');
  var paramObj = str2Json(param);
  //x和y
  var xAxis = paramObj.xAxis;
  var yAxis = paramObj.yAxis;
  var line_dataBody = _data.tableData.tableBody;
  var dataAry = [];
  var height = 20*line_dataBody.length;
  var width = 40*line_dataBody.length;
  jQobj.attr('style','width:'+width+'px;height:'+height+'px;');
  var decorateView = jQobj.attr('decorateView');
  decorateView = removeSpace(decorateView);
  var _ary = []; 
  var ary = decorateView.match(/#.*?#/g);
  for (var j=0;j<ary.length;j++) {
    var obj = new Object();
    obj.col = ary[j];
    obj._col = ary[j].substring(ary[j].indexOf('#')+1,ary[j].lastIndexOf('#'));
    _ary.push(obj);
  }
  for (var i=0;i<line_dataBody.length;i++) {
    eval("var _x = line_dataBody[i]."+xAxis);
    eval("var _y = line_dataBody[i]."+yAxis);
    dataAry[i] = [_x,_y];
  }
  jQobj.css({"width":"440px", "height":"220px"});
  $.plot(jQobj, [{label:"最小值", data:dataAry}], {
    series: {
      lines: { show: true },
      points: { show: true }
    },
    grid: {
      hoverable: true,
      clickable: true
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
  
  //添加滑动所需的div框体并且绑定事件
  var hoverId = jQobj.attr('id')+'_hover';
  jQobj.append("<div id='"+hoverId+"' style='width:40px;height:20px;'></div>");
  jQobj.bind("plothover", function(event, pos, obj){
    if (!obj) {
      return;
    }
    var view = decorateView;
    if (_ary!=null&&_ary!="") {
      var dataIndex = obj.dataIndex;
      var rowData = line_dataBody[dataIndex];
      for (var j=0;j<_ary.length;j++) {
        var _obj = _ary[j];
        var col = _obj._col;
        var val = rowData[col];
        if (val) {
          view = view.replace(_obj.col,val);
        } else {
          if (col.indexOf("percent(")!=-1) {
            col = col.substring(col.indexOf("(")+1,col.indexOf(")"));
            val = rowData[col];
            var sum = getSum(line_dataBody,col);
            var percent = getPercent(val,sum,null);
            view = view.replace(_obj.col,percent);
          }
        }
      }
    }
    $("#"+hoverId).html("<span style='font-weight:bold; color:black;'>"+view+ "</span>");
  });
}
/**
 * <d did='0' showType='pie' param='{^xAxis^:^category^, ^yAxis^:^num^}' value='quotas[0]' decorateView='#category#, #percent(num)#' />
 * showType = pie
 * jQobj:jquery对象
 * _DATA：数据
 */
function drawPie(jQobj,_DATA){
  var value = jQobj.attr('value');
  eval("var _data=_DATA."+value);
  //获取param属性
  var param =  jQobj.attr('param');
  var paramObj = str2Json(param);
  //x和y
  var xAxis = paramObj.xAxis;
  var yAxis = paramObj.yAxis;
  var dataAry = [];
  var pie_dataBody = _data.tableData.tableBody;
  //在pie中解析decorateView='#category#, #percent(num)#'简单格式
  var decorateView = jQobj.attr('decorateView');
  if (decorateView) decorateView = removeSpace(decorateView);
  var _ary = []; 
  var ary = decorateView.match(/#.*?#/g);
  for (var j=0;j<ary.length;j++) {
    var obj = new Object();
    obj.col = ary[j];
    obj._col = ary[j].substring(ary[j].indexOf('#')+1,ary[j].lastIndexOf('#'));
    _ary.push(obj);
  }
  
  //根据param构建显示数据
  for (var i=0;i<pie_dataBody.length;i++) { 
    eval("var _pie_label=pie_dataBody[i]."+xAxis);
    eval("var _pie_data=pie_dataBody[i]."+yAxis);
    dataAry[i] = {label:_pie_label,data:_pie_data};
  }
  //pie div样式
  jQobj.attr('style','height:250px;width:350px;');
  //初始化pie
  $.plot(jQobj, dataAry, {
    series: {
      pie: {
        show: true
      }
    },
    grid: {
      hoverable: true,
      clickable: true
    }
  });

  //添加滑动所需的div框体并且绑定事件
  var hoverId = jQobj.attr('id')+'_hover';
  jQobj.append("<div id='"+hoverId+"' style='width:40px;height:20px;'></div>");
  jQobj.bind("plothover", function(event, pos, obj){
    if (!obj) {
      return;
    }
    //decorateView样例：decorateView='#category#, #percent(num)#'
    var view = decorateView;
    if (_ary!=null&&_ary!="") {
      var seriesIndex = obj.seriesIndex;
      var rowData = pie_dataBody[seriesIndex];
      for (var j=0;j<_ary.length;j++) {
        var _obj = _ary[j];
        var col = _obj._col;
        var val = rowData[col];
        if (val) {
          view = view.replace(_obj.col,val);
        } else {
          if (col.indexOf("percent(")!=-1) {
            col = col.substring(col.indexOf("(")+1,col.indexOf(")"));
            val = rowData[col];
            var sum = getSum(pie_dataBody,col);
            var percent = getPercent(val,sum,null);
            view = view.replace(_obj.col,percent);
          }
        }
      }
    }
    $("#"+hoverId).html("<span style='font-weight:bold; color:" + obj.series.color + "'>" + view + "%</span>");
  });
}

/**
 * showType=table
 */
function drawTable (jQobj,_DATA) {//由于修改了getPrposMapAry方法，所有drawTable也要修改
  var value = jQobj.attr('value');
  eval("var _data=_DATA."+value);
  //数据主体
  var table_body = _data.tableData.tableBody;
  //title
  var table_titles = _data.tableData.titles;
  //存储数据的数组
  var colAry = new Array();
  for (var i=0;i<table_titles.length;i++) {
    //getPrposMapAry得到对应的属性
    var titlePrpos = getPrposMapAry([table_titles[i]]);
    var col = new Object();
    col.field = titlePrpos[0].prposName;
    col.title = titlePrpos[0].prposValue;
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
function drawValue (jQobj,_DATA) {
  var value = jQobj.attr('value');
  eval("var _data=_DATA."+value);
  jQobj.html(_data);
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
  var showData = new Object();
  //value中的"::"的作用是来确定是否需要排序的
  var valIndex;
  if (value.indexOf("::")==-1) {
    valIndex = value;
    eval("var _data=_DATA."+valIndex);
    showData.dataBody = _data.tableData.tableBody;
    showData.titles = _data.tableData.titles;
    //由于不需要排序，他本是就是元数据
    showData.sourceData = _data.tableData.tableBody;
  } else {
    //取值规则
    var valDemand = value.substring(value.indexOf("::"),value.length);
    //取值位置
    valIndex = value.substring(0,value.indexOf("::"));
    //取值范围
    var limit = valDemand.substring(valDemand.indexOf("first(")+6,valDemand.indexOf("|"));
    // oder ->2：升序;1降序
    var sortType = 1;
    if (valDemand.indexOf("!")==-1) sortType = 2;
    // 排序列
    var sortCol = valDemand.substring(valDemand.indexOf("|")+1,valDemand.indexOf(")"));
    eval("var _data=_DATA."+valIndex);
    // oder ->up：升序;down降序
    if (valDemand.indexOf("!")==-1) oder = "up";
    showData = getDataBySortDemand(_data,sortType,limit,sortCol);
  }

  //decorateView
  var decorateView = jQobj.attr('decorateView');
  decorateView = removeSpace(decorateView);
  //把decorateView分为两个部分，dBegin，dEnd，
  //decorateView主体部分
  var dViewBegin = "";
  //decorateView特殊显示部分
  var dViewEnd = "";
  //decorateView中的"::"是用来确定是否需要前缀Suffix？后缀prefix？
  if (decorateView.indexOf("::")!=-1) {
    dViewBegin = decorateView.substring(0,decorateView.indexOf("::"));
    dViewEnd = decorateView.substring(decorateView.indexOf("::")+2,decorateView.length);
  } else {
    dViewBegin = decorateView;
  }
  showData = getDataByShowDemand(dViewBegin,dViewEnd,showData);
  jQobj.html(showData);
}
//以上为解析showType代码============================

//以下为公共部分代码==============

/**
 * 求某列的总和,如果不能计算，会返回""
 * @param data数据数组
 * @param colName列名
 */
function getSum(data,colName){
  var sum = 0;
  try{
    for(var i=0;i<data.length;i++) {
      var val = data[i][colName];
      sum = sum+parseFloat(val);
    }
    return sum;
  }catch(e) {
    return "";
  }
}

/**
 * 求一个数的百分数默认保留2位 如果计算失败会返回""
 * @param num 数值
 * @param sum 总和
 * @param length 保留小数点后几位,默认两位
 */
function getPercent(num,sum,length){
  var percent = "";
  if (!length) length = 2;
  if (num!=""&&num!=null&&sum!=""&&sum!=null&&num<sum) {
    try {
      num = parseFloat(num);
      sum = parseFloat(sum);
      length = parseFloat(length);
    } catch(e){
      return "";
    }
    percent = (num*100)/sum+"";
    //小数点的位置
    var len = percent.indexOf(".");
    if (len!=-1) {
      if (len+length<=percent.length) {//多余2位的时候
        percent = percent.substring(0,len+length+1);
      } else {
        var _len = len+length+1;
        for (var i=0;i<_len-percent.length;i++) {
          percent = percent+"0";
        }
      }
    } else {
      percent = percent+".";
      for (var i=0;i<length;i++) {
        percent = percent+"0";
      }
    }
  }
  return percent;
}
/**
 * 根据显示需求筛选数据
 * @param dViewBegin 显示主体部分
 * @param dViewEnd 特殊显示部分
 * @param showData 数据，包含titles以及经过排序筛选后的data
 */
function getDataByShowDemand(dViewBegin,dViewEnd,showData) {
  //1、找出需要计算列，并算出总和
  //colAry 需要替换参数的数组
  var colAry = dViewBegin.match(/#.*?#/g);
  //数据部分
  var _data = showData.dataBody;
  //元数据部分
  var sourceData = showData.sourceData;
  //求和参数对象数组
  var sumParamAry = [];
  //取一行数据，看能不能得到对应的数据，如不能得到，则需要计算，
  var _dataRow = _data[0];
  for (var i=0;i<colAry.length;i++) {
    var colName = colAry[i].split("#")[1];
    var val = _dataRow[colName];
    //求和参数对象colName为需要求和的列，sum为该列的和
    var sumParam = new Object();
    if (!val&&colName.indexOf("percent(")!=-1) {
      //colName是带"#"的
      sumParam.colName = colName;
      var _colName = colName.substring(colName.indexOf("(")+1,colName.lastIndexOf(")"));
      sumParam.sum = getSum(sourceData,_colName);
      //_colName是不带"#"的
      sumParam._colName = _colName;
      sumParamAry.push(sumParam);
    }
  }

  //2、对模板中的参数进行替换
  var showAry = [];
  for (var j=0;j<_data.length;j++) {
    //一行数据
    var data = _data[j];
    //模板字符串
    var templet = dViewBegin;
    for (var k=0;k<colAry.length;k++) {
      //cloName
      var cName = (colAry[k]).split("#")[1];
      //替换字符串的值
      var cVal = data[cName];
      if (!cVal){//当取不到的时候
        for (var kk=0;kk<sumParamAry.length;kk++) {
          if (cName==sumParamAry[kk].colName&&cName.indexOf("percent(")!=-1) {//当
            var _cName = sumParamAry[kk]._colName;
            cVal = data[_cName];
            var sum = sumParamAry[kk].sum;
            cVal = getPercent(parseFloat(cVal),sum,null);
            templet = templet.replace(colAry[k],cVal);
            continue;
          } 
        }
      }
      templet = templet.replace(colAry[k],cVal);
    }
    showAry.push(templet);
  }
  
  //3、解析dViewEnd
  var showStr = "";
  //默认后缀修饰
  var prefix = "";
  //默认前缀修饰
  var suffix = "；";
  if (dViewEnd!=null&&dViewEnd!="") {
    var jsonObj = str2Json(dViewEnd);
    if (jsonObj.prefix) prefix=jsonObj.prefix;
    if (jsonObj.suffix) suffix=jsonObj.suffix;
  }
  for (var jj=0;jj<showAry.length;jj++) {
    showStr = showStr+prefix+showAry[jj]+suffix;
  }
  return showStr;
}

/**
 * 把decorateView中的"^"和"~"转换成相应的单引号和双引号
 * 然后返回一个json对象
 * @param jsonStr 需要处理的字符串
 * @returns jsonObj
 */
function str2Json(jsonStr){
  jsonStr = jsonStr.replace(/\^/g,"\"");
  jsonStr = jsonStr.replace(/\~/g,"\'");
  var jsonObj = str2JsonObj(jsonStr);
  return jsonObj;
}

/**
 * 根据排序需求筛选数据
 * @param _data数据
 * @param sortType升降序
 * @param limit范围
 * @param sortCol排序列：时间或数值类型。
 */
function getDataBySortDemand(_data,sortType,limit,sortCol) {
  //_data中的数据部分
  eval("var tableData=_data.tableData");
  //原数据中的排序对象：tSort
  eval("var tSort=tableData.sort");
  //原数据中的表头：titles
  eval("var titles=tableData.titles");
  //原数据中的表身：tableBody
  eval("var tableBody=tableData.tableBody");
  //筛选后得到的数据
  var dataBody = [];
  var retObj = new Object();
  if (tSort!=""&&tSort!=null) {//当jsonD中已排序的时候
    if (sortCol==tSort.sortCol) {//排序列相同
      if (sortType==tSort.sortType) {//排序方式相同--正取
        if (limit>tableBody.length) {//范围和长度的比较
          dataBody = tableBody;
        } else {
          for (var i=0;i<limit;i++) {
            dataBody[i] = tableBody[i];
          }
        }
      } else {//排序方式不同相同--倒着取
        if (limit>tableBody.length) {//范围和长度的比较
          for (var j=0;j<tableBody.length;j++) {
            dataBody[j] = tableBody[tableBody.length-j-1];
          } 
        } else {
          for (var k=0;k<limit;k++) {
            dataBody[k] = tableBody[tableBody.length-k-1];
          } 
        }
      }
    } else {//排序列不同
      dataBody = sort(sortType, tableBody, sortCol, limit);
    }
  } else {
    dataBody = sort(sortType, tableBody, sortCol, limit);
  }
  retObj.dataBody = dataBody;
  retObj.dataTitles = titles;
  retObj.sourceData = tableBody;
  return retObj;
}

/**
 * 排序方法这方法是getDataByDemand()的一个扩展
 * @param sortType 升降序
 * @param data 数据
 * @param sortCol 排序列
 * @param limit 范围
 * @returns
 */
function sort(sortType, data, sortCol, limit) {
  if (limit>data.length) limit = data.length;
  var ret = new Array(limit);
  var usedIndexs="";
  if (data==null||data.length==0) return null;
  var _thisIndex=-1;
  for (var i=0; i<limit; i++) {
    var flagData = null;
    for (var k=0;k<data.length;k++) {
      if (usedIndexs.indexOf(k)==-1) {
        flagData=eval("data["+k+"]."+sortCol);
        _thisIndex = k;
        try {
          flagData = parseFloat(flagData);
        } catch (e) {continue;}
        break;
      }
    }
    for (var j=0; j<data.length; j++) {
      if (usedIndexs.indexOf(j)!=-1)continue;
      var _thisData = eval("data["+j+"]."+sortCol);
      try {
        _thisData = parseFloat(_thisData);
      } catch (e) { continue; }
      if (sortType==1){
        if (_thisData>flagData) {
          flagData = _thisData;
          _thisIndex=j;
        }
      } else {
        if (_thisData<flagData) {
          flagData = _thisData;
          _thisIndex=j;
        }
      }
    }
    usedIndexs+=","+_thisIndex;
    ret[i]=eval("data["+_thisIndex+"]");
  }
  return ret;
}

/**
 * 去除str中的空格
 * exp:带空格的字符串
 */
function removeSpace(str){
  if(str) {
    return str.replace(/\s/g,'');
  }else{
    return "";
  }
}

/**
 * 用来获取title中对象的属性名和属性值
 * return 返回一个由prposName(属性名)和prposValue(属性值)的新对象
 */
function getPrposMapAry (obj) {
  var retPropsAry = [];
  for (var i=0;i<obj.length;i++) {
    // 用来保存所有的属性名称和值 
    var retProps = new Object();
    var _obj = obj[i];
    // 开始遍历
    for ( var p in _obj ) {
      // 方法
      if ( typeof (_obj[p]) == " function " ) {
        _obj[p]();
      } else {
        // p 为属性名称，_obj[p]为对应属性的值 
        retProps.prposName = p;
        retProps.prposValue = _obj[p];
      }
    }
    retPropsAry.push(retProps);
  }
  return retPropsAry;
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
      if (matchStr.match(/<\/d>/)!=null){
        matchStr = matchStr.replace(/<d\s{1}/,"<span ");
        matchStr = matchStr.replace(/\/d>/,"/span>");
      } else {
        matchStr = matchStr.replace(/<d\s{1}/,"<span ");
        matchStr = matchStr.substring(0,matchStr.lastIndexOf("/>"))+"></span>";
      }
    } else {
      //在细分为<d/>和<d></d>这两种情况
      if (matchStr.match(/<\/d>/)!=null){
        matchStr = matchStr.replace(/<d\s{1}/,"<div ");
        matchStr = matchStr.replace(/\/d>/,"/div>");
      } else {
        matchStr = matchStr.replace(/<d\s{1}/,"<div ");
        //这里不采用正则的原因是因为decorateView中可能存在/>
        matchStr = matchStr.substring(0,matchStr.lastIndexOf("/>"))+"></div>";
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
            for (var p=0;p<_didAry.length;p++) {
              if (pDataAry!="") {
                if (pDataAry.indexOf(_didAry[p])==-1) pDataAry = pDataAry+_didAry[p]+",";
              } else pDataAry = pDataAry+_didAry[p]+",";
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
    monitor.monitorJsonDId = setInterval(getJsonDJson(jsonDInfo),200);
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
        error: function(XMLHttpRequest, textStatus, errorThrown) {
          alert(XMLHttpRequest.status);
          alert(XMLHttpRequest.readyState);
          alert(textStatus);
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
