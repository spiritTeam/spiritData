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
  var segTree=new Array();
  //level
  var level=0;
  //用于存放jsonDInfo
  var jsonDInfoArray = [];
  //用于存放dataId,方便最后一步进行搜索dom
  var dataIdAry = new Array();

  /**
   * 主函数入口
   */
  $.templetJD = function(reportUrl,reportId) {
    //1,画pageFrame
    initPageFrame();
    //2，从后台请求数据
    var pData = {'reportId':reportId};
    $.ajax({type:"post",url:reportUrl,data:pData,dataType:"json",
      success:function(json){
        var templetJsonObj=str2JsonObj(json);
        if (templetJsonObj.jsonType==1) {
          var templetJD = templetJsonObj.data;
          //3，根据templetD构造出树和框
          //主体
          var _REPORT = templetJD._REPORT;
          //标题
          var _HEAD = templetJD._HEAD;
          //jsonDurl 用于请求jsond
          var _DATA = templetJD._DATA;
          dataIdAry = getJsonD(_DATA);
          $('#rTitle').html(_HEAD.reportName);
          buildSegmentGroup($('#reportFrame'), _REPORT, level, null);
          //显示 
          resolveAndDraw();
          //显示树的部分
          $('#catalogTree').tree({animate:true});
          $('#catalogTree').tree("loadData", segTree);
          //为树结点绑定锚点
          $('#catalogTree').tree({
            onClick: function(node){
              $("body,html").animate({scrollTop:$("#"+node.eleId).offset().top});
            }
          });
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
  function resolveAndDraw(){
    //根据dataIdAry中的id抓取dom
    var domAry = new Array();
    for (var i=0;i<dataIdAry.length;i++) {
      var attr = "_did="+dataIdAry[i];
      var _domAry = $('['+attr+']').toArray();
      domAry[i] = _domAry;
    }
    //用于判断执行和关闭setInterval
    var _dataIdAry = new Array();
    //起setInterval
    var intervalId = setInterval(function() {
      if (_dataIdAry.length==dataIdAry.length) {
        //关闭setInterval
        clearInterval(intervalId);
      }
      //循环jsonDInfoArray，看是否已经取到jsond
      for (var i=0;i<jsonDInfoArray.length;i++) {
        var jsondInfo = jsonDInfoArray[i];
        if (_dataIdAry.toString().indexOf(jsondInfo.id)==-1) {
          //起setInterval,检查d元素是否到位
          if (jsondInfo.jsond!=null&&jsondInfo.jsond!="") {
            for (var k=0;k<domAry.length;k++) {
              var _domAry = domAry[k];
              var _DATA = jsondInfo.jsond._DATA;
              if (jsondInfo.id == $(_domAry[0]).attr('_did')) {
                //相等，说明这个_domAry里面全是这个id的dom，然后进行解析，否则进入下个循环
                for (var j=0;j<_domAry.length;j++) {
                  parseEle($(_domAry[j]),_DATA);
                }
                _dataIdAry.push(jsondInfo.id);
              }
            }
          }
        }
      }
    },500);
  }

  /**
   * 向后台请求jsond,先根据_DATA中的数据，组成jsondInfo,放
   * 入数组中，然后循环数组，向后台请求数据，
   * _DATA:jsondUrl的请求数组
   */
  function getJsonD(_DATA){
    if (_DATA==null||_DATA==""||_DATA[0]==null||_DATA[0]=="") return null;
    var i=0;
    //所有的jsonId
    for (;i<_DATA.length;i++) {
      //jsondInfo
      var jsondInfo = new Object();
      jsondInfo.id = _DATA[i]._id;
      dataIdAry[i] = jsondInfo.id;
      jsondInfo.url =  _DATA[i]._url;
      //jsonD_code有时有，有时没有，负值的时候判断下
      if (_DATA[i]._jsonD_code!=""&&_DATA[i]._jsonD_code!=null) jsondInfo.jsonD_code = _DATA[i]._jsonD_code;
      else jsondInfo.jsonD_code = "";
      jsondInfo.jsond = null;
      jsonDInfoArray[i] = jsondInfo;
    }
    //用于存储已得到jsond的jsondId
    var jsonDIdAry = new Array();
    //启动请求数据线程
    var intervalId = setInterval(function(){
      //如果两个数组长度一样,关闭线程
      if (jsonDIdAry.length==jsonDInfoArray.length) clearInterval(intervalId);
      //每次循环都将id拼起来，方便查找，
      for (var k =0;k<jsonDInfoArray.length;k++) {
        var str = "";
        if (jsonDIdAry.length>0) {
          for(var y = 0;y<jsonDIdAry.length;y++) str+= jsonDIdAry[y]+"";
        }
        var id = jsonDInfoArray[k].id;
        //=-1表示未请求过，
        if (str.lastIndexOf(id)==-1) {
          $.ajax({type:"post",url:jsonDInfoArray[k].url,async:false,dataType:"json",
            success:function(json){
              var jsondJsonObj=str2JsonObj(json);
              if(jsondJsonObj.jsonType==1){
                jsonDInfoArray[k].jsond = jsondJsonObj.data;//allFields(jsondJsonObj.data)
                jsonDIdAry.push(jsonDInfoArray[k].id);
              }else{
                return;
              }
            },error:function(XMLHttpRequest, textStatus, errorThrown ){
              return;
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
    if (segArray==null||segArray=="") return "segArry 为空!";
    //判断eleId
    if (jObj==null) return "未知的eleId";
    //segGroup
    var segGroup = $('<div id="segGroup_'+treeLevel+'" class="segGroup_'+treeLevel+'"/></div>');
    for (var i=0;i<segArray.length;i++) {
      var segId = segArray[i].id;
      //第一层的时候title中没有style标签，第二层有 ,可以跟晖哥商量下title标签问题？
      //title是放在div下面的span中还是直接放在div中
      var _dataAry = null;
      var eleId;
      if (segArray[i].title) {
        //segTitle
        eleId = segId+'_title';
        var segTitle = $('<div id="'+eleId+'" class="segTitle_'+treeLevel+'"></div>');
        segTitle.html(segArray[i].title);
        segGroup.append(segTitle);
      } else { //if(segArray[i].content)
        //segContent
        eleId = segId+'_frag'+i;
        var segContent= $('<div id="'+eleId+'" class="segContent_'+treeLevel+'"/></div>');
        var content = segArray[i].content;
        if (content) {
          var eleS = content.match(/<d\s./g);
          var reg = /<d\s.*?(><\/d>|\/>)/g;
          var pendingAry = new Array();
          var subAry = new Array();
          var subStart=0; 
          for (var s=0;s<eleS.length;s++) {
            var pendingStr = reg.exec(content);
            var start = pendingStr.index;
            var end = reg.lastIndex;
            pendingAry[s] = pendingStr[0];
            //每次只取前面的
            var subStr;
            if (s!=eleS.length-1) {
              //不是最后一个d元素的时候
              if (subStart==start) {
                //subStart==end说明content是从d元素开始的,也可能是两个d连着的
                subStr = "";
                subAry[s] = subStr;
              } else {
                subStr = content.substring(subStart,start);
                subAry[s] = subStr;
              }
            } else {
              //是最后一个d元素的时候
              if (subStart==start) {
                //subStart==end说明content是从d元素开始的,也可能是两个d连着的
                subStr = "";
                subAry[s] = subStr;
                subAry[s+1] = ""; 
              } else {
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
      else if (segArray[i].title) treeNode.text=$(segArray[i].title).html();
      if (treeNode.text&&treeNode.text!="") {
        treeNode.id = "_tree_"+segArray[i].id;
        treeNode.segId = segArray[i].id;
        treeNode.children = new Array();
        treeNode.dataAry = _dataAry;
        treeNode.eleId = eleId;
        if (parent==null) {
          segTree[i]=treeNode;
        } else {
          parent.children[i] = treeNode;
        }
        if (treeLevel>0) {
          var pDataAry;
          if (parent.dataAry) {
            pDataAry = parent.dataAry.toString();
            for (var v=0;v<_dataAry.length;v++) {
              if (pDataAry.indexOf(_dataAry[v])==-1) pDataAry = pDataAry+","+_dataAry[v];
            }
            if (pDataAry.indexOf(",")==pDataAry.length-1) pDataAry = pDataAry.substring(0, pDataAry.indexOf(","));
            parent.dataAry = pDataAry.split(",");
          } else {
            pDataAry = "";
            for (var v=0;v<_dataAry.length;v++) {
              if (pDataAry!="") {
                if (pDataAry.indexOf(_dataAry[v])==-1) pDataAry = pDataAry+_dataAry[v]+",";
              } else pDataAry = pDataAry+_dataAry[v]+",";
              if (pDataAry==",") pDataAry="";
            }
            if (pDataAry.indexOf(",")==pDataAry.length-1) pDataAry = pDataAry.substring(0, pDataAry.indexOf(","));
            parent.dataAry = pDataAry.split(",");
          }
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
    for (var i=0;i<pendingAry.length;i++) {
      var ele = $(pendingAry[i]);
      //为找到的ele设id
      var id = ele.attr('showType')+i;
      ele.attr('id',id);
      var _data = ele.attr('did')+",";
      if (_dataStr=="") _dataStr = _dataStr +_data;
      else if (_dataStr.indexOf(_data)==-1) _dataStr = _dataStr +_data;
      var pendingStr = pendingAry[i];
      pendingStr = pendingStr.replace(/did/,"id='"+id+"' _did");
      if (ele.attr('showType')=="value"){
        if (pendingStr.match(/></)!=null){
          pendingStr = pendingStr.replace(/<d\s{1}/,"<span ");
          pendingStr = pendingStr.replace(/\/d>/,"/span>");
        } else {
          pendingStr = pendingStr.replace(/<d\s{1}/,"<span ");
          pendingStr = pendingStr.replace(/\/>/,"></span>");
        }
      } else {
        if (pendingStr.match(/></)!=null){
          pendingStr = pendingStr.replace(/<d\s{1}/,"<div ");
          pendingStr = pendingStr.replace(/\/d>/,"/div>");
        } else {
          pendingStr = pendingStr.replace(/<d\s{1}/,"<div ");
          pendingStr = pendingStr.replace(/\/>/,"></div>");
        }
      }
      newContent = (newContent.concat(subAry[i],pendingStr));
    }
    newContent = newContent.concat(subAry[subAry.length-1]);
    if (_dataStr.lastIndexOf(",")==_dataStr.length-1) _dataStr = _dataStr.substring(0, _dataStr.lastIndexOf(","));
    var _dataAry = _dataStr.split(",");
    var retObj = new Object();
    if (_dataAry==null) _dataAry.push("");
    retObj._dataAry = _dataAry;
    retObj.newContent = newContent;
    return retObj;
  }
  //以下为公共方法部分
  /**
   * 排序方法
   * orderCol:排序列(按照这列排序)
   * data:排序数组
   * shortType:排序方式，升序或降序
   * sortSize:获取个数
   */
  function sort(sortType, data, orderCol, firstNum) {
    if (firstNum>data.length) firstNum = data.length;
    var ret = new Array(firstNum);
    var usedIndexs="";
    if (data==null||data.length==0) return null;
    var _thisIndex=-1;
    for (var i=0; i<firstNum; i++) {
      var flagData = null;
      for (var k=0;k<data.length;k++) {
        if (usedIndexs.indexOf(k)==-1) {
          flagData=eval("data["+k+"]."+orderCol);
          _thisIndex = k;
          try {
            flagData = parseFloat(flagData);
          } catch (e) {continue;}
          break;
        }
      }
      for (var j=0; j<data.length; j++) {
        if (usedIndexs.indexOf(j)!=-1)continue;
        var _thisData = eval("data["+j+"]."+orderCol);
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
   * 去除括号“()”
   * exp:带"()"的字符串
   */
  function removeBrackets(exp){
    var start = exp.indexOf("(")+1;
    var end = exp.lastIndexOf(")");
    return exp.substring(start,end);
  }
  /**
   * 去除str中的空格
   * exp:带空格的字符串
   */
  function removeSpace(exp){
    return exp.replace(/\s/g,'');
  }
  /**
   * 用来获取title中对象的属性名和属性值
   * 方便easyui table的显示
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
  //以上为公共方法部分

  /**
   * 解析元素，根据showType进行拼接显示效果
   * jQobj:需要解析的元素
   * _DATA：对应的数据
   */
  function parseEle(jQobj, _DATA){
    //指向jsond中的数据
    var value = jQobj.attr('value');
    //得到showType//根据value得到数据
    eval("var _data=_DATA."+value);
    var showType = jQobj.attr('showType');
    if (showType=="value") drawValue(jQobj, _data);
    else if (showType=="table") drawTable(jQobj, _data);
    else if (showType=="pie") drawPie(jQobj, _data);
    else if (showType=="line") drawLine(jQobj, _data);
    else if (showType=="bars") drawBar(jQobj, _data);
    else if (showType.lastIndexOf("first(")!=-1) drawFirst(showType,jQobj, _data);
  }

  //以下方法为对showType的解析
  /**
   * st=first
   * @param showType：showType
   * @param jQobj:jq对象
   * @param _data
   */
  function drawFirst(showType,jQobj,_data){
  //1、提取出first中的表达式
    var exp = removeBrackets(showType);
    //2、数据
    var fData = _data.tableData.tableBody;
    //3、解析exp:!(n|col)和(n|col)？ 
    //去除空格处理
    exp = removeSpace(exp);
    var decorateView = jQobj.attr('decorateView');
    //得到拍序列以及取数范围
    if (decorateView) {
      var showColAry = decorateView.match(/#.*?#/g);
      if (exp.charAt(0)=="!") {
        //处理升序!(n|col)
        if (exp.charAt(1)!="(") {
          alert("缺失符号“(”");
          return;
        } else {
          exp = removeBrackets(exp);
          if (exp.lastIndexOf("|")==-1) {
            alert("缺失符号“|”");
            return;
          } else {
            var firstNum = exp.split("|")[0];
            var oderCol = exp.split("|")[1];
            //接下来排序？sort
            var ary = sort(1,fData,oderCol,firstNum);
            var showStr = "";
            for (var k=0;k<ary.length;k++) {
              var tt = decorateView;
              for (var i=0;i<showColAry.length;i++) {
                var showColExp = showColAry[i];
                var showCol=showColExp.substring(showColExp.indexOf("#")+1,showColExp.lastIndexOf("#"));
                eval("var sVal=ary["+k+"]."+showCol);
                tt = tt.replace(showColExp,sVal);
              }
              showStr = showStr+tt+"，";
            }
            jQobj.html(showStr);
          }
        }
      } else {
        //处理降序n|col？
        if (exp.lastIndexOf("|")==-1) {
          alert("缺失符号“|”");
          return;
        } else {
          var firstNum = exp.split("|")[0];
          if (firstNum>fData.length) firstNum = fData.length;
          var oderCol = exp.split("|")[1];
          //接下来排序？
          var ary = sort(2,fData,oderCol,firstNum);
          var showStr = "";
          for (var k=0;k<ary.length;k++) {
            var tt = decorateView;
            for (var i=0;i<showColAry.length;i++) {
              var showColExp = showColAry[i];
              var showCol=showColExp.substring(showColExp.indexOf("#")+1,showColExp.lastIndexOf("#"));
              eval("var sVal=ary["+k+"]."+showCol);
              tt = tt.replace(showColExp,sVal);
            }
            showStr = showStr+tt+"，";
          }
          jQobj.html(showStr);
        }
      }
    }
  }

  /**
   * st=table
   */
  function drawTable(jQobj,_data){
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
   * st=value
   */
  function drawValue(jQobj,dataAry){
    jQobj.html(dataAry);
  }

  /**
   * st = pie
   * jQobj:jquery对象
   * dataAry：数据
   * decorateView:显示修饰
   */
  function drawPie(jQobj,_data){
    //特有属性
    var pieLabel = jQobj.attr('label');
    var pieData = jQobj.attr('data');
    var ary = [];
    var pie_dataBody = _data.tableData.tableBody;
    //在派中解析这个不知道有没有意义
    var decorateView = jQobj.attr('decorateView');
    if (decorateView) {
      if (decorateView.indexOf("lableShow")==-1) {
        alert("decorateView格式有出错");
      } else {
        var b = decorateView.indexOf("[")+1;
        var e = decorateView.indexOf("]");
        var exp = decorateView.substring(b,e);
        exp = removeSpace(exp);
        //var decorateAry = exp.split(",");
      }
    }
    for (var i=0;i<pie_dataBody.length;i++) { 
      eval("var _pie_label=pie_dataBody[i]."+pieLabel);
      eval("var _pie_data=pie_dataBody[i]."+pieData);
      ary[i] = {label:_pie_label,data:_pie_data};
    }
    jQobj.attr('style','height:150px;width:150px;');
    $.plot(jQobj, ary, {
      series:{
        pie:{
          show:true,
          radius:1,
          label:{
            show:true,
            radius:2/3,
            formatter:function(label, series){
              //调节下面divcss可以调节pie上的字体显示效果
              return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'+label+'<br/>'+Math.round(series.percent)+'%</div>';
            },
            threshold:0.1
          }
        }
      },
      legend:{
        show:false
      }
    });
  }

  /**
   * st=line
   * param
   * jQobj:jquery对象
   * dataAry：数据
   * decorateView:显示修饰
   */
  function drawLine(jQobj,_data,decorateView){
    var label = jQobj.attr('label');
    var data = jQobj.attr('data');
    var line_dataBody = _data.tableData.tableBody;
    var ary = [];
    var height = 20*line_dataBody.length;
    var width = 40*line_dataBody.length;
    jQobj.attr('style','width:'+width+'px;height:'+height+'px;');
    var decorateView = jQobj.attr('decorateView');
    if (decorateView) {
      if (decorateView.indexOf("lableShow")==-1) {
        alert("decorateView格式有出错");
      } else {
        var b = decorateView.indexOf("[")+1;
        var e = decorateView.indexOf("]");
        var exp = decorateView.substring(b,e);
        exp = removeSpace(exp);
        //该数组第一个元素暂时没用。。保留项
        decorateAry = exp.split(",");
      }
    }
    eval("var showStyle = line_dataBody[0]."+decorateAry[1]);
    if (!showStyle) {
      var sum =0;
      for (var i=0;i<line_dataBody.length;i++) {
        eval("var _y = line_dataBody[i]."+data);
        sum = sum+parseFloat(_y);
      }
      for (var i=0;i<line_dataBody.length;i++) {
        eval("var _x = line_dataBody[i]."+label);
        eval("var _y = line_dataBody[i]."+data);
        _y = Math.round((parseFloat(_y)/sum*10000)/100.00)+"%";
        ary[i] = [_x,_y];
      }
    } else {
      for (var i=0;i<line_dataBody.length;i++) {
        eval("var _x = line_dataBody[i]."+label);
        eval("var _y = line_dataBody[i]."+decorateAry[1]);
        ary[i] = [_x,_y];
      }
    }
    jQobj.css({"width":"440px", "height":"220px"});
    $.plot(jQobj, [{label:"最小值", data:ary}], {
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
  }

  /**
   * st==Bars
   * jQobj:jquery对象
   * dataAry：数据
   * decorateView:显示修饰
   */
  function drawBar(jQobj,_data){
    var label = jQobj.attr('label');
    var data = jQobj.attr('data');
    var line_dataBody = _data.tableData.tableBody;
    var ary = [];
    var height = 20*line_dataBody.length;
    var width = 40*line_dataBody.length;
    jQobj.attr('style','width:'+width+'px;height:'+height+'px;');
    for (var i=0;i<line_dataBody.length;i++) {
      eval("var _x = line_dataBody[i]."+label);
      eval("var _y = line_dataBody[i]."+data);
      ary[i] = [_x,_y];
    }
    $.plot(jQobj, [ary], {
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
  //以上方法为对showType的解析

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
