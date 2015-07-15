//v1.0::
//1-对report的解析
//2-对d标签的解析
//3-对data的轮训读取
//4-容错的处理
//5-文本、表格、饼图、柱图、折线图的实现
//v2.0::
//1-ds的实现
//2-调整结构，使得结构更加清晰
//3-修改了D标签与DList数据的对应，更好的容错，更好的性能
//
//计划：完成style的处理，以及多分类的其他处理

/**
 * 精灵报告解析及展示方法
 * 需要用到：
 * JS——jquery+spirit.pageFrame
 * CSS——在本文件所在目录的css目录下的report.css文件中
 * sysInclude.jsp——从这里取根_PATH
 * 包括：
 * 1-主函数
 * 2-D_tag对象的定义
 * 3-报告解析对象：包括递归
 * 4-获取jsonD的方法
 * 5-处理jsonD的对象：包括填充数据
 */

/**
 * 显示类型到文本的转换(目前只支持中文)
 * 若没有对应的显示类型放回空串
 */
function transTagType(showType) {
  if (showType=="value") return "值";
  if (showType=="text") return "文本";
  if (showType=="table") return "表格";
  if (showType=="pie") return "饼图";
  if (showType=="bar") return "柱图";
  if (showType=="line") return "折线图";
  if (showType=="radar") return "雷达图";
  if (showType=="map_pts") return "地图";
  return "";
}

/**
 * 解析report数据中生成的，为完成整体逻辑所记录的
 * 数据结构
 */
var parseSysData={
  monitorData:new Object(), //为监控DList读取设置的对象
  treeData:null, //生成树，此树包括parent属性
  ddtagdomMap:null, //数据|d标签|dom对象的映射对应关系
  ddtagsdomMap:null, //数据|ds标签|dom对象的映射对应关系
  poolThreadId:-1 //轮询进程号
};

/**
 * 主函数，生成报告。
 * 报告的生成，需要一个干净的html容器。
 * 注意：
 * 1-执行此方法的html页面，此方法会把这个页面中的body内容全部清空。
 * 2-次方法会自动加载需要的资源——(这个功能后续再处理，目前需要的资源需要先行在html中加载)
 * 3-目前报告中的图形采用jqplot+eChart控件，表格采用easyUi的表格，提示的窗口采用本框架封装的功能
 * @param param 解析报告的参数，主要是获得报告的必要信息，包括
 * param={
 *   getUrl: 获得报告的Url比如getReport.do，可为空，默认为：report/getReport.do?（一般不用设置，除非有新的获得报告的方法），注意设置时，开头不要以“/”开头，以此为开头，将不会自动加入_PATH的前缀
 *   reportUri：获得报告json的直接Uri
 *   reportId：报告Id
 * }
 */
function generateReport(param) {
  //1-得到获得报告的Url
  var _getUrl=reportParse.parseParam(param);
  if (!_getUrl) return ;

  //2-初始化界面
  $("html").css("overflow-x","hidden"); 
  $("body").html("");//清空页面
  initPageFrame();

  //3-读取report.json
  reportParse.get_parseReport(_getUrl);

  /*
   * 采用pageFrame框架，初始化报告界面
   */
  function initPageFrame(){
    //1、画pageFrame
    //1-1:头部元素
    var topSegment=$('<div id="topSegment"><div id="rTitle"></div></div>');
    $("body").append(topSegment);
    //1-2:主体元素
    var mainSegment=$('<div id="mainSegment" style="padding:10px"></div>');
    //1-2-1:右侧的报告结构树
    var sideFrame=$('<div id="sideFrame"><div id="catalogTree"></div></div>');
    mainSegment.append(sideFrame);
    //1-2-2:报告主体
    var reportFrame=$('<div id="reportFrame"></div>');
    mainSegment.append(reportFrame);
    $("body").append(mainSegment);
    //1-3:尾部元素
    var footSegment=$('<div id="footSegment"></div>');
    $("body").append(footSegment);
    //INIT_PARAM
    var INIT_PARAM={
      pageObjs: {
        topId: "topSegment",
        mainId: "mainSegment",
        footId: "footSegment"
      },
      page_width: -1,
      page_height: -1,
      top_shadow_color:"#E6E6E6",
      top_height: 60,
      top_peg: false,
      foot_height: 0, //脚部高度，目前先不使用页脚
      foot_peg: true,
      myInit: initPos,
      myResize: initPos
    };
    function initPos() {
      $("#reportFrame").spiritUtils("setWidthByViewWidth", $("body").width()-$("#sideFrame").spiritUtils("getViewWidth")-15);
      $("#sideFrame").css("left", $("#reportFrame").width());
    }
    var initStr=$.spiritPageFrame(INIT_PARAM);
    if (initStr) {
      $.messager.alert("页面初始化失败", initStr, "error");
      return ;
    };
  };
};

/**
 * 报告解析对象,此对象主要是方法的集合。
 */
var reportParse={
  /**
   * 解析参数，并得到获得报告数据的Url，此方法中若有不合法的参数，会调用平台message(目前采用easyUi的message)方法给出提示。
   * 若不能解析为获得报告的Url则返回null
   * @param param解析报告的参数，主要是获得报告的必要信息，包括
   * param={
   *   getUrl: 获得报告的Url比如getReport.do，可为空，默认为：report/getReport.do?（一般不用设置，除非有新的获得报告的方法），注意设置时，开头不要以“/”开头，以此为开头，将不会自动加入_PATH的前缀
   *   reportUri：获得报告json的直接Uri
   *   reportId：报告Id
   * }
   */
  parseParam: function(param) {
    var mPage=getMainPage();
    //1-参数校验
    var checkOk=true;
    var _msg="", _temp=null, _url=null;
    //1.1-校验整个参数
    if (!param) {
      _msg="参数为空，无法显示报告！";
      if (mPage) mPage.$.messager.alert("提示", _msg, "error");
      else alert(_msg);
      checkOk=false;
    }
    //1.2-校验报告Id或Uri，若有Uri，则Id被忽略
    if (checkOk) {
      _temp=param.reportUri;
      if (_temp&&$.trim(_temp)!=""&&$.trim(_temp)!="undefined") {
        _url="?uri="+encodeURIComponent(param.reportUri);
      } else {
        _temp=param.reportId;
        if (_temp&&$.trim(_temp)!=""&&$.trim(_temp)!="undefined") {
          _url="?reportId="+param.reportId;
        } else {//两个参数都没有设置，出错了
          _msg="报告Uri或Id至少指定一项，目前两项都未指定，无法显示报告！";
          if (mPage) mPage.$.messager.alert("提示", _msg, "error");
          else alert(_msg);
          checkOk=false;
        }
      }
    }
    if (!checkOk) return null;//若校验不通过，则后面的逻辑都不做了
    //1.3-组装获取report数据的Url
    _temp=param.getUrl;
    if (_temp&&$.trim(_temp)!=""&&$.trim(_temp)!="undefined") {
      _temp=decodeURIComponent(_temp);
      if (_temp.indexOf("/")!=0&&_temp.indexOf("\\")!=0
        &&(_temp.indexOf("file:")!=0)
      ) {//无根，加上根
        _temp=_PATH+"/"+_temp;
      }
      _url=_temp+_url;
      if (_temp.indexOf("file:///")==0) {
        _url="file:///"+param.reportUri
      }
    } else {
      _url=_PATH+"/report/getReport.do"+_url;
    }
    return _url;
  },
  /**
   * 获得报告数据，注意这里采取ajax的同步方式，返回获得的数据
   * @param url 获得数据的Url
   */
  get_parseReport: function(getUrl) {
    var mPage=getMainPage();
    var _msg="";
    $.ajax({type:"get",url:getUrl ,async:true, dataType:"json",
      success: function(json) {
        if (json.jsonType!=1) {
          _msg=json.message;
          if (mPage) mPage.$.messager.alert("提示", _msg, "error");
          else alert(_msg);
        } else {//解析，并画内容
          var _data=eval("("+json.data+")");
          reportParse.parseAndDraw(_data);
        }
      },
      error: function(XMLHttpRequest, textStatus, errorThrown) {
        _msg="通过["+getUrl+"]请求数据失败。<br/>status="+XMLHttpRequest.status;
        if (mPage) mPage.$.messager.alert("提示", _msg, "error");
        else alert(_msg);
      }
    });
  },

  //以下都为parseReport对象的内部调用函数
  /**
   * 解析report数据，进行一次扫描：
   * 1-画出报告
   * 2-计算整理树对象
   * 3-记录d标签结构
   * @param rptData 报告对象，注意，必须是javascript对象
   */
  parseAndDraw: function(rptData) {
    //解析DLIST
    parseSysData.monitorData=reportParse.parseDList(rptData._DLIST);
    //获得报告名称，并显示
    $('#rTitle').html(rptData._HEAD._reportName);
    //解析REPORT（报告主体），并画报告的主体
    var ret=reportParse.parseReport(rptData._REPORT);
    if (ret==0) {//出错了，给出提示，并结束后续的处理
      var mPage=getMainPage();
      if (mPage) mPage.$.messager.alert("提示", ret.msg, "error");
      return ;
    };
    //画树
    parseSysData.treeData=ret.treeRoot;
    $('#catalogTree').tree({animate:true});
    $('#catalogTree').tree("loadData", parseSysData.treeData.children);
    //为树结点绑定锚点
    $('#catalogTree').tree({
      onClick: function(node) {
        var topSegHeight=$("#topSegment").height()+3;
        if (node.segId=="segment0_0") $("body,html").animate({scrollTop:0});
        else $("body,html").animate({scrollTop:$("#"+node.segId).offset().top - topSegHeight});
      }
    });
    parseSysData.ddtagdomMap=ret.dm;
    parseSysData.ddtagsdomMap=ret.dsm;
    //启动获取数据的轮询过程
    parseSysData.poolThreadId=setInterval(reportParse.getDataFromDListAndDraw, 2000);//2秒钟获取一次数据
  },

  /**
   * 解析DList，并返回需要的结构，此结构用于
   * @param _DLIST DList对象
   */
  parseDList: function(_DLIST) {
    if (_DLIST==null||_DLIST==""||_DLIST.length<=0) return ;
    var monitDListObj=new Object();
    monitDListObj.allSize=_DLIST.length; //监控对象：Dlist总数
    monitDListObj.okSize=0; //成功获得数据的节点数
    monitDListObj.faildSize=0; //获得数据失败节点个数
    monitDListObj.dList=new Array(_DLIST.length); //数据列表——数组
    for (var i=0;i<_DLIST.length;i++) {
      var oneData=new Object();
      oneData.url=_DLIST[i]._url;
      oneData.jdCode=_DLIST[i]._jsonDCode;
      oneData.getFlag=0; //获取状态并获取次数，0还需获取；-1获取成功；-2获取失败
      oneData.drawFlag=0; //画D标签的状态：0未画；1正在画；2画完了
      oneData.progress=0; //数据处理进度，从0-1的一个数值
      monitDListObj.dList[_DLIST[i]._id]=oneData;
    }
    return monitDListObj;
  },

  /**
   * 解析DList，并返回需要的结构，此结构用于
   * //1-画报告的Html；
   * //2-得到报告的树结构，包括树下D标签及D数据的对应关系，并返回
   * //3-得到D标签——转换后的DIV|SPAN元素/D数据的对应关系
   * @param _REPORT Report数据
   */
  parseReport: function(_REPORT) {
    if (!_REPORT) return ;

    var level=0;
    var treeRoot=new Object();
    var dtagMap=new Object();
    var dtagsMap=new Object();

    //处理系统跟结点
    treeRoot.id=-1;
    treeRoot.text="系统根结点";
    if (_REPORT&&_REPORT.length>0) {
      for (var i=0; i<_REPORT.length; i++) {
        var ret=reportParse.recursionSegs(_REPORT[i], treeRoot, dtagMap, dtagsMap, level, $("#reportFrame"), i);
        if (ret) { //出错了，不进行处理了
          return ret;
        }
      }
    }
    var ret= new Object();
    ret.treeRoot=treeRoot;
    ret.dm=dtagMap;
    ret.dsm=dtagsMap;
    //返回内容
    return ret;
  },

  //内部私有函数
  /*
   * 递归扫描report内容，并构造需要的结构
   * @param segs 报告的一个segment
   * @param ptn 树对象
   * @param ddm div与Dtag的对应关系
   * @param ddsm div与Dtags的对应关系
   * @param l 树的Level
   * @param jqObj jquery的dom对象
   * @param index 数组中的下标
   * @returns 若解析错误，则放回false
   */
  recursionSegs: function(segs, ptn, ddm, ddsm, l, jqObj, index) {
    var ret=new Object();

    var pTnId=(ptn)?ptn.id:null;//上级结点id
    var priorityName=(segs.name&&$.trim(segs.name))?segs.name:((segs.title&&$.trim(segs.title))?segs.title:null);
    var priorityTitle=(segs.title&&$.trim(segs.title))?segs.title:((segs.name&&$.trim(segs.name))?segs.name:null);

    //1-处理树
    var treeNode=new Object();
    //1.1-树结点id
    var rankId=l+"_"+index; //级别Id
    treeNode.id=(pTnId&&pTnId!=-1)?pTnId+"__"+rankId:"_tn"+rankId;
    rankId=treeNode.id.substr(3);
    treeNode.segId='segment'+rankId;
    if (segs.id&&$.trim(segs.id)) treeNode.sgid="_tnsg_"+segs.id;//若有段落id，则记录一下
    else {
      ret="在处理段落时，发现段落没有设置id，无法处理！";
      if (ptn.id==-1) ret.msg+="此段落为根段落的"+index+"个子段落";
      else ret+="此段落为id=["+ptn.id+"]段落的"+index+"个子段落";
      return ret;
    }
    //1.2-树结点名称
    if (priorityName) treeNode.text=priorityName;
    if (!treeNode.text||!$.trim(treeNode.text)) {
      ret="在处理id=["+treeNode.id+"]的段落时，发现段落的名称或标题没有设置，无法处理";
      return ret;
    }
    //1.3把树节点挂接到上级节点
    if (!ptn.children) ptn.children=new Array();
    treeNode.parent=ptn;
    ptn.children[index]=treeNode;

    //2-画显示对象
    //2.1-每个segment的框架
    var docEle_html='<div id="segment'+rankId+'" class="segment segment_'+l+'"><ul>'
      + '<li id="segTitle'+rankId+'" class="segTitle segTitle_'+l+'">'+priorityTitle+'</li>';
    if (segs.content&&$.trim(segs.content)) docEle_html
      +='<li id="segContent'+rankId+'" class="segContent segContent_'+l+'"></li>';
    if (segs.subSegs&&segs.subSegs.length>0) docEle_html
      +='<li id="segSubs'+rankId+'"><div id="ssBody'+rankId+'" class="segSubs"></div></li>';
    var jqDocEle=$(docEle_html);//本级doc对象，以jquery方式处理
    //2.2-生成具体的段内容
    if (segs.content&&$.trim(segs.content)) {
      var _content=segs.content;
      var i=0;
      var _replaceDom=null;
      //先找Ds标签
      var ml=_content.match(/<ds.*?(<\/ds>)/gi);//match list
      if (ml&&ml.length>0) {
        var oneDs="";
        var dtagsDivId="rpDoms"+rankId+"_"+i;
        for (; i<ml.length; i++) {
          oneDs = ml[i];
          _replaceDom=null;
          _replaceDom="<div id='"+dtagsDivId+"'";
          var dTags=new D_Tags.buildByDtagsHtml(oneDs, dtagsDivId, treeNode);
          if (dTags.transLog) {
            _replaceDom+=" class='dtagParseErr dtagParseErrDiv'>"+(dTags.transLog?dTags.transLog:"ds解析异常，无法处理")+"</div>"
          } else {
            _replaceDom+=" class='dtagDiv'>图形组</div>";
            ddsm[dtagsDivId]=dTags;
          }
          _content=_content.replace(oneDs, _replaceDom);
        }
      }
      //找到D标签
      ml=_content.match(/<d .*?(<\/d>|\/>)/gi);//match list
      if (ml&&ml.length>0) {
        for (i=0; i<ml.length; i++) {
          var dTag=new D_tag.buildByDTagHtml(ml[i]);
          //根据dTag生成替换对象
          _replaceDom=null;
          if (!dTag.showType) {
            _replaceDom="<p id='rpDom"+rankId+"_"+i+"' class='dtagParseErr'>"+(dTag.transLog?dTag.transLog:"d标签没有showType，无法处理")+"</p>";
          } else {
            if (dTag.transLog) {
              if (dTag.showType=="value"||dTag.showType=="text") _replaceDom="<span id='rpDom"+rankId+"_"+i+"' class='dtagParseErr'>"+(dTag.transLog?dTag.transLog:"未知问题")+"</span>";
              else _replaceDom="<div id='rpDom"+rankId+"_"+i+"' class='dtagParseErr dtagParseErrDiv'>"+(dTag.transLog?dTag.transLog:"未知问题")+"</div>";
            } else {
              if (dTag.showType=="value"||dTag.showType=="text") {
                _replaceDom="<span id='rpDom"+rankId+"_"+i+"' class='dtagTextWaiting'>"+transTagType(dTag.showType)+"</span>";
              } else {
                _replaceDom="<div id='rpDom"+rankId+"_"+i+"' class='dtagDiv'>"+transTagType(dTag.showType)+"</div>";
              }
              reportParse.addDindex2TreeNode(treeNode, dTag.did);//加入树结点的dArray属性(包含所有下级结点中的D标签的did的列表)
              reportParse.addMap2Ddm("rpDom"+rankId+"_"+i, dTag, ddm);//加入ddm——对应关系对象
            }
          }
          _content=_content.replace(ml[i], _replaceDom);
        }
      }
      jqDocEle.find("#segContent"+rankId).html(_content);
    }
    jqObj.append(jqDocEle);

    //递归
    if (segs.subSegs&&segs.subSegs.length>0) {
      for (var i=0; i<segs.subSegs.length; i++) {
        var _ret=reportParse.recursionSegs(segs.subSegs[i], treeNode, ddm, ddsm, l+1, $("#ssBody"+rankId), i);
        if (_ret) { //出错了，不能再处理了
          return _ret;
        }
      }
    }
  },
  /*
   * 把dIndex加入树结点，包括上级结点
   * @param tn 树结点
   * @param dindex jsonD在本report中的序号
   */
  addDindex2TreeNode: function(tn, dindex) {
    if (!tn.dArray) tn.dArray=new Array();
    var found=false;
    for (var i=0; i<tn.dArray.length; i++) {
      if (tn.dArray[i]==dindex) {
        found=true;
        break;
      }
    }
    if (!found) tn.dArray.push(dindex);
    if (tn.parent) reportParse.addDindex2TreeNode(tn.parent, dindex);
  },
  /* 
   * 把映射关系加入全局映射对象
   * @param domId 替换d标签的dom对象的Id
   * @param dtag d标签对象
   * @param ddm 全局映射对象
   */
  addMap2Ddm: function(domId, dtag, ddm) {
    if (!ddm) ddm=new Object();
    var mapL=ddm[dtag.did];
    if (!mapL) ddm[dtag.did]=new Object();
    ddm[dtag.did][domId]=dtag;
  },
  /*
   * 从Dlist中获取数据并根据获取的数据画d标签和ds标签
   */
  getDataFromDListAndDraw: function() {
    var mPage=getMainPage();
    var _msg="";
    var i=0;

    var md=parseSysData.monitorData;
    if (!md||!md.dList) {
      clearInterval(parseSysData.poolThreadId);
      _msg="监控参数异常，无法获得数据";
      if (mPage) mPage.$.messager.alert("提示", _msg, "error");
      else alert(_msg);
      return ;
    }
    if (md.dList.length==0||(md.okSize+md.faildSize)>md.allSize) {
      clearInterval(parseSysData.poolThreadId);
      return;
    }

    //找到有意义的数据源：从ddtagdomMap和ddtagsdomMap中找出
    var usedDataItemIndex = new Array();
    var p;
    var ddm = parseSysData.ddtagdomMap;
    var ddsm = parseSysData.ddtagsdomMap;
    if (ddm) {
      for(p in ddm) {
        if (typeof(p)!="function") {
          usedDataItemIndex.push(p);
        }
      }
    }
    if (ddsm) {
      for(p in ddsm) {
        if (typeof(p)!="function"&&ddsm[p]&&ddsm[p].dArray&&ddsm[p].dArray.length>0) {
          for (i=0; i<ddsm[p].dArray.length; i++) {
            var oneTag=ddsm[p].dArray[i];
            if (oneTag&&oneTag.dTag&&oneTag.dTag.did) usedDataItemIndex.push(oneTag.dTag.did);
          }
        }
      }
    }
    //以下是真正的处理过程
    for (i=0; i<usedDataItemIndex.length; i++) {
      var thisD=md.dList[usedDataItemIndex[i]];
      if (thisD) {
        if (thisD.getFlag==-1||thisD.getFlag==-2) continue;
        var getJsonDUrl=_PATH+"/"+thisD.url;
        $.ajax({type:"get", url:getJsonDUrl, async:true, dataType:"json", _index: i,
          success: function(json) {
            if (json.jsonType==0) {//获取失败
              md.faildSize++;
              thisD.getFlag=-2;//失败
              thisD.drawFlag=1;//正在画
              D_tag.dealReadDataErr(this._index, json.message);
              D_Tags.dealReadDataErr(this._index, json.message);
              thisD.drawFlag=2;//画完了
            } if (json.jsonType==1) {//获取成功：解析，并填充d标签内容
              md.okSize++;
              var _data=eval("("+json.data+")");
              thisD.getFlag=-1;//成功
              thisD.drawFlag=1;//正在画
              D_tag.dealReadDataOk(this._index, _data);
              D_Tags.dealReadDataOk(this._index, _data);
              thisD.drawFlag=2;//画完了
            } if (json.jsonType==2) {//在运行中，处理
              thisD.getFlag++;//获取次数+1 //目前获取次数不做处理
              thisD.drawFlag=0;//还未画
              //处理进度
              thisD.progress=json.progress;
            } else {//很异常，目前先不做处理
            }
          },
          error: function(XMLHttpRequest, textStatus, errorThrown) {
            md.faildSize++;
            thisD.getFlag=-2;//失败
            thisD.drawFlag=1;//正在画
            D_tag.dealReadDataErr(this._index, "status="+XMLHttpRequest.status);
            D_Tags.dealReadDataErr(this._index, "status="+XMLHttpRequest.status);
            thisD.drawFlag=2;//画完了
          }
        });
      } else {
        clearInterval(parseSysData.poolThreadId);
        _msg="监控参数异常，无法获得数据";
        if (mPage) mPage.$.messager.alert("提示", _msg, "error");
        else alert(_msg);
        break;
      }
    }
  }
};

//=DTag处理=begin==========================
var D_tag={
    /*
    ==dTag对象说明
    did:null, //对应的data编码，report中_DLIST里的jsonD标签的顺序号
    showType:null, //显示类型
    value:null, //数据，获取_DATA中的那个对象，解析是一个对象，包括:dPoint(字符串)或dFilter(字符串)
    param:null, //showType附属说明，在标签串中以json格式存在，解析后应该是一个对象
    decorateView:null, //显示修饰语法
    tdParseData:null,//表数据解析后的数据结构，辅助数据类型，value类型没有这个属性
    transLog:null, //转换的记录，若转换成功，则此对象为空，否则此对象为错误转换的说明(要分行记录)
    */
    /**
     * 通过html获得Dtag对象
     */
  buildByDTagHtml: function(dtagHtml) {
    var ret = new Object();
    ret.transLog="";
    var jqObj=$(dtagHtml);
    var _pos=-1;

    //1-did
    var _tempStr=jqObj.attr("did");
    if (!_tempStr) ret.transLog+="\nD标签没有设置数据编码did";
    else {
      ret.did=_tempStr;
      if (parseSysData&&parseSysData.monitorData&&parseSysData.monitorData.dList&&parseSysData.monitorData.dList.length>0) {
        if (!parseSysData.monitorData.dList[ret.did]) {
          ret.transLog+="\nD标签对应的did未对应任何数据。";
        }
      } else ret.transLog+="\nD标签对应的did未对应任何数据。";
    }
    //2-showType
    _tempStr=jqObj.attr("showType");
    if (!_tempStr) ret.transLog+="\nD标签没有设置显示类型showType";
    else {
      _tempStr=_tempStr.toLowerCase();
      if (_tempStr=="value"||_tempStr=="text"||_tempStr=="table"||_tempStr=="pie"||_tempStr=="line"||_tempStr=="bar"||_tempStr=="radar"||_tempStr=="map_pts")
        ret.showType=_tempStr;
      else
        ret.transLog+="\nD标签的显示类型[showType="+_tempStr+"]所指定的类型不合法";
    }
    //3-value
    _tempStr=jqObj.attr("value");
    if (!_tempStr) ret.transLog="\nD标签没有设置数据值value";
    else { //解析value
      ret.value=new Object();
      _tempStr=replaceInnerKH(_tempStr);
      _pos=_tempStr.indexOf("::");
      if (_pos==-1) ret.value.dPoint=_tempStr;//数据获取指针
      else {
        ret.value.dPoint=_tempStr.substring(0, _pos);
        _pos=_tempStr.indexOf("::");
        if (_pos!=-1) ret.value.dFilter=new Array();
        while (_pos!=-1) {
          _tempStr=_tempStr.substr(_pos+2);
          _pos=_tempStr.indexOf("::");
          ret.value.dFilter.push(_pos==-1?_tempStr:_tempStr.substring(0, _pos));
        }
      }
    }
    //4-param
    _tempStr=jqObj.attr("param");
    if (!_tempStr) {
      if (ret.showType!='value'&&ret.showType!='text'&&ret.showType!='table'&&ret.showType!='map_pts') {
        ret.transLog="\n类型为["+ret.showType+"]的D标签没有设置类型参数param";
      }
    } else { //解析param
      _tempStr=replaceInnerKH(_tempStr);
      ret.param=eval("("+_tempStr+")");
    }
    //5-decorateView
    _tempStr=jqObj.attr("decorateView");
    if (_tempStr) { //解析param
      ret.decorateView=new Object();
      _tempStr=replaceInnerKH(_tempStr);
      _pos=_tempStr.indexOf("::");
      if (_pos==-1) ret.decorateView.view=_tempStr;//数据获取指针
      else {
        ret.decorateView.view=$.trim(_tempStr.substring(0, _pos));
        ret.decorateView.ext=eval("("+_tempStr.substr(_pos+2)+")");
      }
    }
    if (ret.showType=="text"&&(!ret.decorateView||!ret.decorateView.view)) {
      ret.transLog+="\n[showType=text]类型的D标签，必须设置decorateView(显示修饰)属性";
    }

    //6-查合法性
    if (ret.showType!="value") {
      ret.tdParseData=D_tag.tableDataFun.parseDtag(ret);
      _tempStr=D_tag.tableDataFun.checkDtag(ret);
      if (_tempStr) ret.transLog+="\n"+_tempStr;
    }
    if (ret.transLog) ret.transLog=(ret.transLog+"").substring(1);

    return ret;
    //内部函数：内部串^|~的转码
    function replaceInnerKH(str) {
      str=str.replace(/\^/g,"\"");
      str=str.replace(/\~/g,"\'");
      return str;
    }
  },
  /**
   * 若读取数据错误，处理相关联的D标签区域，把错误信息显示出来
   * @param dindex report中_DLIST里的jsonD标签的顺序号
   * @param errMsg 错误信息
   */
  dealReadDataErr: function(dindex, errMsg) {
    var map=parseSysData.ddtagdomMap[dindex];
    if (!map) return;
    var p;//dom对象的id
    for(var p in map) {
      if (typeof(p)!="function") {
        var jqObj=$("#"+p);
        if (jqObj) {
//          var oldStr=jqObj.html();
//          if (oldStr) {
//            oldStr=oldStr.substr(0, oldStr.indexOf("<")==-1?oldStr.length:oldStr.indexOf("<"));
//          }
          jqObj.html("<span class='errMsgSpan'>"+errMsg+"</span>");
        }
      }
    }
  },
  /**
   * 若读取数据成功，按D标签的定义显示数据
   * @param dindex report中_DLIST里的jsonD标签的顺序号
   * @param d 读出的数据，是json各式
   */
  dealReadDataOk: function(dindex, d) {
    var map=parseSysData.ddtagdomMap[dindex];
    if (!map) return;
    var p;//dom对象的id
    for(var p in map) {
      if (typeof(p)!="function") {
        var jqObj=$("#"+p);
        var dtag=map[p]; //d标签
        //读取数据
        var dataOk=true;
        var _pointData=null
        try {
          _pointData=eval("(d._DATA."+dtag.value.dPoint+")");
        } catch(e) {
          _pointData=e.message;
          if (_pointData.indexOf("undefined")!=-1) _pointData="数据文件中'"+dtag.value.dPoint+"'未定义";
          dataOk=false;
        }
        if (!dataOk) jqObj.html("<span class='errMsgSpan'>"+_pointData+"</span>");
        else D_tag.drawTagArea(jqObj, dtag, _pointData);
      }
    }
  },
  /**
   * 画d标签的区域
   * @param jqEle jquery的html dom 元素
   * @param dtag d标签对象
   * @param d 要处理的数据(已经从jsonD中的_DATA中取到的数据)
   */
  drawTagArea: function(jqEle, dtag, d) {
    if (jqEle.html().indexOf("<")!=-1) {//已经画过了
      dtag.drawFlag=2;
      return;
    }
    if (dtag.showType=="value") D_tag.draw.drawValue(jqEle, dtag, d);
    else {
      var errMsg="";
      var _tData=d.tableData;
      if (!_tData.dataList||_tData.dataList.length==0) errMsg="表数据中没有任何数据";
      else errMsg=D_tag.tableDataFun.checkMatch(dtag, _tData); //检查数据的匹配情况，并把结果追加到dtag中的tdParseData中去
      if (errMsg) jqEle.html("<span class='errMsgSpan'>"+errMsg+"</span>");
      else {
        //TODO 检查d数据是否符合，如titles中的内容是否和dlist中的内容能够匹配
        if (dtag.showType=="text") D_tag.draw.drawText(jqEle, dtag, _tData);
        else if (dtag.showType=="table") D_tag.draw.drawTable(jqEle, dtag, _tData);
        else if (dtag.showType=="pie") D_tag.draw.drawPie(jqEle, dtag, _tData);
        else if (dtag.showType=="line") D_tag.draw.drawLine(jqEle, dtag, _tData);
        else if (dtag.showType=="bar") D_tag.draw.drawBar(jqEle, dtag, _tData);
        else if (dtag.showType=="radar") D_tag.draw.drawRadar(jqEle, dtag, _tData); //雷达图
        else if (dtag.showType=="map_pts") D_tag.draw.drawMapPts(jqEle, dtag, _tData); //地图画点
      }
    }
  },

  //具体画内容的方法
  draw:{
    drawValue: function(jqEle, dtag, d) {//简单取值
      var htmlStr=d;
      if (dtag.decorateView) {
        if (dtag.decorateView.view) htmlStr=dtag.decorateView.view.replace(/#value#/gi, d);
      }
      jqEle.attr("class", "dtagTextShow");
      jqEle.html(htmlStr);
    },
    //画文本段:jqEle(html元素的jquery对象)，dtag(标签信息)，d(表数据)
    drawText: function(jqEle, dtag, d) {
      var htmlStr="";
      var tp=dtag.tdParseData; //语法分析后的记录
      if (tp.decorate.errMsg||tp.sort.errMsg) {
        if (tp.decorate.errMsg) htmlStr="欲显示的["+tp.decorate.errMsg+"]字段在表数据中不存在";
        if (tp.sort.errMsg) htmlStr=(htmlStr?"，":"")+"过滤函数中所指明的["+tp.decorate.errMsg+"]字段在表数据中不存在";
      }
      if (htmlStr) htmlStr="<span class='errMsgSpan'>"+htmlStr+"</span>";
      else {//获取数据
        var fTdata=D_tag.tableDataFun.filterData(dtag, d);
        if (fTdata.errMsg) htmlStr="<span class='errMsgSpan'>"+fTdata.errMsg+"</span>";
        else {
          //获得分割符
          var splitChar=(dtag.decorateView.ext&&dtag.decorateView.ext.suffix)?dtag.decorateView.ext.suffix:"、";
          var oneRowSource,oneRow;
          for (var i=0; i<fTdata.dList.length; i++) {
            oneRow=fTdata.dList[i];
            oneRowSource=dtag.decorateView.view;
            //替换
            var ml=oneRowSource.match(/#.+?#/gi);
            if (ml&&ml.length>0) {
              for (var j=0; j<ml.length; j++) {
                oneRowSource=oneRowSource.replace(ml[j], oneRow[ml[j].substr(1, ml[j].length-2)]);
              }
            }
            htmlStr+=splitChar+oneRowSource;
          }
          htmlStr=htmlStr.substr(splitChar.length);
        }
      }
      jqEle.attr("class", "dtagTextShow");
      jqEle.html(htmlStr);
    },
    //画表格:jqEle(html元素的jquery对象)，dtag(标签信息)，d(表数据)
    drawTable: function(jqEle, dtag, d) {
      var fTdata=D_tag.tableDataFun.filterData(dtag, d);
      if (fTdata.errMsg) {
        htmlStr="<span class='errMsgSpan'>"+fTdata.errMsg+"</span>";
        jqEle.html(htmlStr);
      } else {
        //获得列对象
        var cols=new Array();
        var gridWidth=0;
        cols[0]=new Array();
        for (var p in fTdata.titles) {
          if (typeof(p)!="function") {
            var _oneT=new Object();
            _oneT.field=p;
            _oneT.title=fTdata.titles[p];
            _oneT.width=100;
            var canInsert=true;
            if (dtag.param&&dtag.tdParseData&&dtag.tdParseData.param) {
              var filterCols=dtag.tdParseData.param.okL?dtag.tdParseData.param.okL:dtag.tdParseData.param.defL;
              if (filterCols&&filterCols.length>0) {
                canInsert=false;
                for (var i=0; i<filterCols.length; i++) {
                  if (filterCols[i]==p) {
                    canInsert=true;
                    for (var _p in dtag.param) {
                      if (dtag.param[_p]==p) {
                        _oneT.title=_p;
                        break;
                      }
                    }
                    break;
                  }
                }
              }
            }
            if (canInsert) {
              cols[0].push(_oneT);
              if (gridWidth<600) gridWidth+=101;
            }
          }
        }
        if (gridWidth<800) gridWidth++;
        jqEle.css({"width":(gridWidth+10)+"px", "margin-top":"5px"});
        jqEle.datagrid({
          singleSelect:true,
          columns:cols,
          data:fTdata.dList
        });
      }
    },
    //画饼图:jqEle(html元素的jquery对象)，dtag(标签信息)，d(表数据)
    drawPie: function(jqEle, dtag, d) {
      var fTdata=D_tag.tableDataFun.filterData(dtag, d);
      if (fTdata.errMsg) {
        htmlStr="<span class='errMsgSpan'>"+fTdata.errMsg+"</span>";
        jqEle.html(htmlStr);
      } else {
        jqEle.html("");
        //准备数据
        var pieData=new Array();
        for (var i=0; i<fTdata.dList.length; i++) {
          var row=fTdata.dList[i];
          pieData.push({"label":row[dtag.param["xAxis"]], "data":row[dtag.param["yAxis"]]});
        }
        $.plot(jqEle, pieData, {
          series: {pie: {show: true}},
          grid: {hoverable: true}
        });
        jqEle.bind("plothover", function(event, pos, obj) {
          if (!obj) {
            D_tag.draw.hideTooltip();
            return;
          }
          var opt={"top":pos.pageY+5,"left":pos.pageX+5};
          var content=pieData[obj.seriesIndex]["label"]+":"+pieData[obj.seriesIndex]["data"];
          D_tag.draw.showTooltip(opt, content);
        });
      }
    },
    //画折线图:jqEle(html元素的jquery对象)，dtag(标签信息)，d(表数据)
    drawLine: function(jqEle, dtag, d) {
      var fTdata=D_tag.tableDataFun.filterData(dtag, d);
      if (fTdata.errMsg) {
        htmlStr="<span class='errMsgSpan'>"+fTdata.errMsg+"</span>";
        jqEle.html(htmlStr);
      } else {
        jqEle.html("");
        //准备数据
        var lineData=new Array();
        for (var i=0; i<fTdata.dList.length; i++) {
          var row=fTdata.dList[i];
          lineData.push([row[dtag.param["xAxis"]], row[dtag.param["yAxis"]]]);
        }
        $.plot(jqEle, [{"label":d.titles[dtag.param["xAxis"]], "data":lineData}], {
          series: {
            lines: {show: true},
            points: {show: true}
          },
          grid: {
            hoverable: true,
            borderWidth: {top: 0, right: 0, bottom: 1, left: 1}
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
          }
        });
        jqEle.bind("plothover", function(event, pos, obj) {
          if (!obj) {
            D_tag.draw.hideTooltip();
            return;
          }
          var opt={"top":pos.pageY+5, "left":pos.pageX+5};
          var content=barData[obj.dataIndex][0]+":"+barData[obj.dataIndex][1];
          D_tag.draw.showTooltip(opt, content);
        });
      }
    },
    //画柱图:jqEle(html元素的jquery对象)，dtag(标签信息)，d(表数据)
    drawBar: function(jqEle, dtag, d) {
      var fTdata=D_tag.tableDataFun.filterData(dtag, d);
      if (fTdata.errMsg) {
        htmlStr="<span class='errMsgSpan'>"+fTdata.errMsg+"</span>";
        jqEle.html(htmlStr);
      } else {
        jqEle.html("");
        //准备数据
        var barData=new Array();
        for (var i=0; i<fTdata.dList.length; i++) {
          var row=fTdata.dList[i];
          barData.push([row[dtag.param["xAxis"]], row[dtag.param["yAxis"]]]);
        }
        $.plot(jqEle, [{"label":d.titles[dtag.param["xAxis"]], "data":barData}], {
          series: {
            bars: {show: true, barWidth: 0.3, align: "center", fill:0.6}
          },
          grid: {
            hoverable: true,
            borderWidth: {top: 0, right: 0, bottom: 1, left: 1}
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
          }
        });
        jqEle.bind("plothover", function(event, pos, obj) {
          if (!obj) {
            D_tag.draw.hideTooltip();
            return;
          }
          var opt={"top":pos.pageY+5, "left":pos.pageX+5};
          var content=barData[obj.dataIndex][0]+":"+barData[obj.dataIndex][1];
          D_tag.draw.showTooltip(opt, content);
        });
      }
    },
    drawRadar: function(jqEle, tdata) {//雷达图
      
    },
    drawMapPts: function(jqEle, tdata) {
      
    },
    showTooltip: function(opt, cont) {
      var qH=$("#_tooltip");
      if (qH.length==0) {
        $("body").append('<div id="_tooltip"></div>');
        qH=$("#_tooltip");
      }
      qH.html(cont);
      qH.css(opt).show();
    },
    hideTooltip: function() {
      $("#_tooltip").hide();
    }
  },

  //表数据处理与解析
  tableDataFun:{
    /**
     * 按照分析后语法过滤数据，并返回结果。
     * @param dtag D标签数据
     * @param d 表中的真实数据
     * 若出错，返回的对象中包括errMessage信息
     */
    filterData: function(dtag, d) {
      var ret=new Object();

      var tp=dtag.tdParseData; //语法分析的数据
      var showTitles=new Array();
      var tempTitles1=new Array(), tempTitles2=new Array();
      //1-找出需要的列
      var isAllTitles=true; //对showType=table有用
      if (tp.param) {
        if (tp.param.okL) tempTitles1=tp.param.okL;
        else tempTitles1=tp.param.defL;
        if (tempTitles1&&tempTitles1.length>0) isAllTitles=false;
      }
      if (tp.decorate) {
        if (tp.decorate.okL) tempTitles2=tp.decorate.okL;
        else tempTitles2=tp.decorate.defL;
        if (tempTitles2&&tempTitles2.length>0) isAllTitles=false;
      }
      //2-合并列，不包括sort
      var mergeTitles=new Array();
      var i=0, j=0;
      var found=false;
      for (; i<tempTitles1.length; i++) {
        mergeTitles.push(tempTitles1[i]);
      }
      for (i=0; i<tempTitles2.length; i++) {
        found=false;
        for (; j<mergeTitles.length; j++) {
          if (tempTitles2[i]==mergeTitles[j]) {
            found=true;
            break;
          }
        }
        if (!found) mergeTitles.push(tempTitles2[i]);
      }
      if (tp.sort&&tp.sort.sortCol) {
        found=false;
        for (i=0; i<mergeTitles.length; i++) {
          if (mergeTitles[i]==tp.sort.sortCol) {
            found=true;
            break;
          }
        }
        if (!found) mergeTitles.push(tp.sort.sortCol);
      }
      //3-根据合并后的列构造返回的titles，这里需要注意，那些需要计算百分比的列，在这里也要处理
      if (isAllTitles) {
        ret.titles=d.titles;
      } else {
        ret.titles=new Object();
        for (i=0; i<mergeTitles.length; i++) {
          for (var p in d.titles) {
            if (typeof(p)!="function") {
              if (p==mergeTitles[i]) {
                ret.titles[p]=d.titles[p];
                break;
              }
            }
          }
        }
      }
      if (tp.percentL&&tp.percentL.length>0) {//处理百分比列
        for (i=0; i<tp.percentL.length; i++) {
          for (var p in d.titles) {
            if (typeof(p)!="function"&&p==tp.percentL[i]) {
              ret.titles["percent("+p+")"]=d.titles[p]+"占比";
              break;
            }
          }
        }
      }
      //4-获取数据：排序/计算百分比/进行列组合，注意这里的数据，包括列顺序也要和title相互匹配
      var needSort=false;
      var overturn=false;//是否翻转取数据
      var sort=null;
      if (d.sort||tp.sort) {
        if (!d.sort) sort=tp.sort, needSort=true;
        if (!tp.sort) sort=d.sort;
        if (d.sort&&tp.sort) {
          sort=tp.sort;
          if (d.sort.sortCol!=tp.sort.sortCol) needSort=true;
          else {
            if (d.sort.sortType!=tp.sort.sortType) overturn=true;
          }
        }
      }
      //计算百分比
      var countPecent=false;
      var newPercentedL=new Array();
      if (tp.percentL&&tp.percentL.length>0) {
        countPecent=true;
        //首先计算总数
        var sum=new Array();
        for (j=0; j<tp.percentL.length; j++) sum[tp.percentL[j]]=0;
        for (i=0; i<d.dataList.length; i++) {
          var row=d.dataList[i];
          for (j=0; j<tp.percentL.length; j++) {
            sum[tp.percentL[j]]+=row[tp.percentL[j]];
          }
        }
        //组合新的数据
        var _lastPercent=new Object();
        for (i=0; i<tp.percentL.length; i++) _lastPercent[tp.percentL[i]]=1;

        for (i=0; i<d.dataList.length; i++) {
          var nRow=new Object();
          var row=d.dataList[i];
          for (var p in ret.titles) {
            nRow[p]=row[p];
          }
          for(j=0; j<tp.percentL.length; j++) {
            var _n=_lastPercent[tp.percentL[j]];
            if (i!=d.dataList.length-1) {
              _n=sum[tp.percentL[j]]==0?"":row[tp.percentL[j]]/sum[tp.percentL[j]];
              if (_n) _lastPercent[tp.percentL[j]]=_lastPercent[tp.percentL[j]]-_n;
            }
            if (_n) _n=Math.round(_n*10000)/100.00;
            nRow["percent("+tp.percentL[j]+")"]=_n;
          }
          newPercentedL.push(nRow);
        }
      }
      var usedL=newPercentedL.length>0?newPercentedL:d.dataList;
      //排序
      var newSortL=new Array();
      var insertIndex=-1;
      if (needSort) {
        newSortL[0]=usedL[0];
        for (i=1; i<usedL.length; i++) {
          var row=usedL[i];
          insertIndex=-1;
          if (sort.sortType==1) {//从大到小
            for (j=0; j<newSortL.length; j++) {
              if (row[sort.sortCol]>=newSortL[j][sort.sortCol]) {
                insertIndex=j;
                break;
              }
            }
          } else {//从小到大
            for (j=0; j<newSortL.length; j++) {
              if (row[sort.sortCol]<=newSortL[j][sort.sortCol]) {
                insertIndex=j;
                break;
              }
            }
          }
          if (insertIndex==-1) newSortL[newSortL.length]=row;
          else {
            newSortL.insertAt(j, row);
          }
        }
      }
      usedL=newSortL.length>0?newSortL:usedL;
      //取数据
      var getSize=d.dataList.length;
      if (sort) getSize=sort.tbSize?((sort.tbSize<getSize)?sort.tbSize:getSize):getSize;
      ret.dList=[];
      var increase=0;
      if (overturn) {
        for (i=usedL.length-1; i>=0; i--) {
          var row=usedL[i];
          var nRow=generateNR(ret.titles, row);
          ret.dList.push(nRow);
          if ((increase++)>getSize) break;
        }
      } else {
        for (i=0; i<usedL.length; i++) {
          var row=usedL[i];
          var nRow=generateNR(ret.titles, row);
          ret.dList.push(nRow);
          if ((increase++)>getSize) break;
        }
      }
      return ret;

      //内部函数
      function generateNR(ts, r) {//按ts:titles的顺序，从r:row中取数据
        var nr=new Object();
        for (var p in ts) {
          nr[p]=r[p];
        }
        return nr;
      }
    },
    /**
     * 表数据解析d标签，返回辅助数据，包括2个列表和1个排序字段的标签名：
     * 1个列表(decorate={defL, okL, errMsg})：需要显示的列，若标签中没有对列的定义，则返回null
     * 2个列表(param={defL, okL, errMsg})：需要显示的列，若标签中没有对列的定义，则返回nll
     * 1个排序字段标签名(sort):这个字段可以没有,可以为空
     *   sort中包括获取记录个数(tbSize):这个字段是从first中分析出来的，但放在这里似乎不合适，应该给所有的过滤函数作一个统一的语法分析器（先这样！！）,可以为空
     * @param dtag D标签数据
     * @return 如函数声明所描述的“2个列表和1个排序字段的标签名”
     */
    parseDtag: function(dtag) {
      var ret=new Object();
      var ml, _ml;
      var i=0;
      //参数列列表
      if (dtag.param) {
        for(var p in dtag.param) {
          if (typeof(p)!="function"&&p!="mapType") {//地图中的mapType不是在数列表中的
            if (!ret.param) ret.param=new Object();
            if (!ret.param.defL) ret.param.defL=new Array();
            ret.param.defL[i++]=dtag.param[p];
          }
        }
      }
      //修饰列列表
      if (dtag.decorateView&&dtag.decorateView.view) {//获得标签的显示修饰
        ml=dtag.decorateView.view.match(/#.+?#/gi);
        if (ml&&ml.length>0) {
          if (!ret.decorate) ret.decorate=new Object();
          if (!ret.decorate.defL) ret.decorate.defL=new Array();
          for (i=0; i<ml.length; i++) {
            ret.decorate.defL[i]=ml[i].substr(1, ml[i].length-2);
          }
        }
      }
      //函数分析
      if (dtag.value&&dtag.value.dFilter) {
        for (i=0; i<dtag.value.dFilter.length; i++) {
          var _fun=dtag.value.dFilter[i];
          var _pos=_fun.indexOf("!");
          if (_fun.indexOf("first")!=-1) {
            //TODO 这里不对first的过滤方法是否符合标准进行检查
            ret.sort=new Object();
            ml=_fun.match(/\|.+?\)/);
            _ml=_fun.match(/\(.+?\|/);
            if (ml&&ml.length==1&&_ml&&_ml.length==1) {
              ret.sort.tbSize=_ml[0].substr(1, _ml[0].length-2);//很别扭，先这样
              ret.sort.sortCol=ml[0].substr(1, ml[0].length-2);
              ret.sort.sortType=(_pos==0?1:2);
            }
          }/* else
          if (_fun.indexOf("filterCol")!=-1) {
            var _p1=_fun.indexOf("("), _p2=_fun.lastIndexOf(")");
            _fun=_fun.substring(_p1+1, _p2);
            ret.filterCol=new Array();
            while(_fun.indexOf(",")!=-1) {
              ret.filterCol.push($.trim(_fun.substring(0,_fun.indexOf(","))));
              _fun=_fun.substr(_fun.indexOf(",")+1);
            }
            ret.filterCol.push($.trim(_fun));
          }*/
        }
      }
      return ret;
    },
    /**
     * 检查数据与标准(见word文档)是否符合。
     * @param dtag D标签数据
     * @return 若不合法，则返回错误信息，否则，返回null;
     *   注意：通过本检查，会扩充tp的内容，会包括百分比计算列的列表和排序字段标签名
     */
    checkDtag: function(dtag) {
      var tp=dtag.tdParseData, showType=dtag.showType;
      if (showType=='text'&&(!tp.decorate||tp.decorate.length==0)) return "[showType="+showType+"]类型的D标签，decorateView(显示修饰)属性中需要包含字段描述";
      if ((showType=='pie'||showType=='bar'||showType=='line'||showType=='radar'||showType=='map_pts')&&(!tp.param||tp.param.length==0))
        return "[showType="+showType+"]类型的D标签，decorateView(显示修饰)属性中需要包含字段描述";
      return "";
    },
    /**
     * 检查标签定义与数据的匹配程度，注意匹配程度只与表数据中的titles/sort进行处理，不对真正的数据列表进行读取。
     * @param dtag D标签数据
     * @param d 表中的真实数据
     * @return 若不合法，则返回错误信息，否则，返回null(合法);
     *   注意：通过本检查，会扩充dtag.tdParseData的内容，会包括百分比计算列的列表和排序字段标签名
     */
    checkMatch: function(dtag, d) {
      if (dtag.showType=="value") return null;//合法：对value类型的D标签来说，对表数据进行匹配检查没有意义
      else {
        if (!dtag.tdParseData) {
          dtag.tdParseData=D_tag.tableDataFun.parseDtag(dtag);
          var _tempMsg=D_tag.tableDataFun.checkDtag(dtag);
          if (_tempMsg) return _tempMsg;
        }
      }
      var tp=dtag.tdParseData;
      var _defineSort=d.sort;
      var _defineTitles=d.titles;
      if (isEmpty(_defineTitles)) return "表数据不合法，titles没有定义。"; //用到了common.utils.js中的方法
      //匹配参数列
      if (tp.param&&tp.param.defL&&tp.param.defL.length>0) {
        matchList("param", tp.param.defL, _defineTitles, tp);
      }
      //匹配显示修饰列
      if (tp.decorate&&tp.decorate.defL&&tp.decorate.defL.length>0) {
        matchList("decorate", tp.decorate.defL, _defineTitles, tp);
      }
      //匹配排序列
      if (_defineSort&&_defineSort.sortCol) {
        var find=false, _b, _a=_defineSort.sortCol;
        for (var p in _defineTitles) {
          if (typeof(p)!="function") {
            if (p==_a) {
              find=true;
              break;
            }
          }
        }
        //处理百分比
        if (!find&&_a.indexOf("percent")==0) {
          _b=tp.sort.sortCol.match(/\(.+?\)/);
          if (_b&&_b.length==1) {
            _a=_b[0].substr(1, _b[0].length-2);
            find=false;
            for (var p in _defineTitles) {
              if (typeof(p)!="function") {
                if (p==_a) {
                  find=true;
                  break;
                }
              }
            }
            if (find) {
              if (!tp.percentL) tp.percentL=new Array();
              var _find=false;
              for (; j<tp.percentL; j++) {
                if (tp.percentL[j]==_a) {
                  _find=true;
                  break;
                }
              }
              if (!_find) tp.percentL.push(_a);
            }
          }
        }
        if (!find) {//没找到，出错了
          tp.sort.errMsg=tp.sort.sortCol;
        } else {
          tp.sort.sortOkCol=_a;
        }
      }
      //处理错误信息
      var errMsg="";
      if (tp.param&&tp.param.errMsg) errMsg+=","+"param(类型参数)属性中指定的字段["+tp.param.errMsg+"]不存在";
      if (tp.decorate&&tp.decorate.errMsg) errMsg+=","+"decorateView(显示修饰)属性中指定的字段["+tp.decorate.errMsg+"]不存在";
      if (tp.sort&&tp.sort.errMsg) errMsg+=","+"过滤函数(first)中指定的排序字段["+tp.param.errMsg+"]不存在";
      if (errMsg) {
        errMsg=errMsg.substr(1);
        return errMsg;
      }

      //内部私有函数
      /* 
       * 列表匹配
       * @param mType matchType对比列的类别：目前只有param和decorate
       * @param cl compareList比较列表
       * @param tl titleList标题列表，表头列表
       * @param tp D标签按表数据方式解析后的辅助信息：2个列表和1个排序字段的标签名
       * @return 本方法不返回值，而是把计算结果追加到tp中去（注意tp是dtag的一个属性，会自动添加到dtag对象中去）
       *   包括：不匹配的列，匹配的列
       *   注意：通过本检查，会扩充dtag.tdParseData的内容，会包括百分比计算列(percentL)的列表
       */
      function matchList(mType, cl, tl, tp) {
        if (tp[mType].okL&&tp[mType].okL.length>0) return;
        var find=false;
        var _a, _b;
        var i=0, j=0;
        var err="", okL=new Array();
        if (cl&&cl.length>0) {
          for (; i<cl.length; i++) {
            _a=cl[i];
            find=false;
            for (var p in tl) {
              if (typeof(p)!="function") {
                if (p==_a) {
                  find=true;
                  break;
                }
              }
            }
            //处理百分比
            if (!find&&_a.indexOf("percent")==0) {
              _b=_a.match(/\(.+?\)/);
              if (_b&&_b.length==1) {
                _a=_b[0].substr(1, _b[0].length-2);
                find=false;
                for (var p in tl) {
                  if (typeof(p)!="function") {
                    if (p==_a) {
                      find=true;
                      break;
                    }
                  }
                }
                if (find) {
                  if (!tp.percentL) tp.percentL=new Array();
                  var _find=false;
                  for (; j<tp.percentL; j++) {
                    if (tp.percentL[j]==_a) {
                      _find=true;
                      break;
                    }
                  }
                  if (!_find) tp.percentL.push(_a);
                }
              }
            }
            if (!find) err+=","+cl[i]; //没找到，出错了
            else okL.push(_a);
          }
          if (err) {
            err=err.substr(1);
            tp[mType].errMsg=err;
          }
          if (okL.length>0) tp[mType].okL=okL;
        }
      }
    }
  }
};
//=DTag处理=end==========================

//=DTags处理=begin==========================
var D_Tags={
  /*
  dsid:null, //整篇文档中，Dtags的编号
  loadAll:yes|no|err,//是否
  dArray:null, //每个dTag标签对应的情况，包括如下：
    index:序号
    dTag:dtag对象
    loadData:装载的数据，若为空，则说明没有装载上数据
  */
  /**
   * 通过html获得Dtags对象。
   * 若其中一个Dtag标签有错误，则整个过程停止
   */
  buildByDtagsHtml: function(dtagsHtml, divId, treeNode) {
    var ret = new Object();
    var i=0;
    ret.dsid=divId;
    ret.loadAll="no";
    ml=dtagsHtml.match(/<d .*?(<\/d>|\/>)/gi);//match list
    if (ml&&ml.length>0) {
      for (; i<ml.length; i++) {
        var oneTagInTags = new Object();
        oneTagInTags.index=i;
        var dtag=new D_tag.buildByDTagHtml(ml[i]);
        oneTagInTags.dtag=dtag;
        oneTagInTags.loadData=null;
        oneTagInTags.transLog=dtag.transLog;
        if (dtag.showType&&(dtag.showType!='line'&&dtag.showType!='bar')) {
          oneTagInTags.transLog=(dtag.transLog?dtag.transLog+"\n":"")+"图形组目前仅支持[柱图]和[折线图]的组合，目前图形为["+transTagType(dTag.showType)+"]，无法组合。";
        }
        if (!ret.dArray) ret.dArray=new Array();
        ret.dArray.push(oneTagInTags);
        if (oneTagInTags.transLog) break;//若有一个图形组的子图有问题，则整个图形组视为错误
        reportParse.addDindex2TreeNode(treeNode, dtag.did);//加入树结点的dArray属性(包含所有下级结点中的D标签的did的列表)
      }
    }
    ret.transLog="";
    if (ret.dArray&&ret.dArray.length>0) {
      for (i=0; i<ret.dArray.length; i++) {
        if (ret.dArray[i].transLog) ret.transLog+="\n子图["+transTagType(ret.dArray[i].dtag.showType)+"]问题如下："+ret.dArray[i].transLog;
      }
    }
    ret.transLog=$.trim(ret.transLog);
    if (!ret.transLog) ret.transLog=null;

    return ret;
  },
  /**
   * 若读取数据错误，处理相关联的Ds标签区域，把错误信息显示出来
   * @param dindex report中_DLIST里的jsonD标签的顺序号
   * @param errMsg 错误信息
   */
  dealReadDataErr: function(dindex, errMsg) {
    var ddsm=parseSysData.ddtagsdomMap;
    if (!ddsm) return;

    var msg="";
    for(var p in ddsm) {
      if (typeof(p)!="function") {
        dTags=ddsm[p];
        msg="";
        if (dTags.dArray) {
          for (var i=0; i<dTags.dArray.length; i++) {
            var dtag = dTags.dArray[i].dtag;
            if (dtag.did==dindex) {
              msg+="<br\>类型为["+transTagType(dtag.showType)+"]的子图("+dTags.dArray[i].index+")："+errMsg;
            }
          }
        }
        msg=$.trim(msg);
        var jqObj=$("#"+p);
        if (msg&&jqObj) jqObj.html("<span class='errMsgSpan'>"+msg.substr(4)+"</span>");
      }
    }
  },
  /**
   * 若读取数据成功，按Ds标签的定义显示数据
   * @param dindex report中_DLIST里的jsonD标签的顺序号
   * @param d 读出的数据，是json各式
   */
  dealReadDataOk: function(dindex, d) {
    var ddsm=parseSysData.ddtagsdomMap;
    if (!ddsm) return;

    var errMsg="";
    var p;//dom对象的id
    for(var p in ddsm) {
      if (typeof(p)!="function") {
        dTags=ddsm[p];
        if (dTags.dArray) {
          var i=0;
          for (; i<dTags.dArray.length; i++) {
            var dtag=dTags.dArray[i].dtag;
            if (dtag.did==dindex) {//准备数据
              errMsg="";
              var dataOk=true;
              var _pointData=null
              try {
                _pointData=eval("(d._DATA."+dtag.value.dPoint+")");
              } catch(e) {
                _pointData=e.message;
                if (_pointData.indexOf("undefined")!=-1) _pointData="数据文件中'"+dtag.value.dPoint+"'未定义";
                dataOk=false;
              }
              if (!dataOk) dTags.dArray[i].loadData=_pointData;
              else {
                var _tData=_pointData.tableData;
                if (!_tData.dataList||_tData.dataList.length==0) errMsg="表数据中没有任何数据";
                else errMsg=D_tag.tableDataFun.checkMatch(dtag, _tData); //检查数据的匹配情况，并把结果追加到dtag中的tdParseData中去
                if (errMsg) dTags.dArray[i].loadData=errMsg;
                else {
                	var fTdata=D_tag.tableDataFun.filterData(dtag, _tData);
                  if (fTdata.errMsg) dTags.dArray[i].loadData=fTdata.errMsg;
                  else {
                    var loadData=new Array();
                    for (var j=0; j<fTdata.dList.length; j++) {
                      var row=fTdata.dList[j];
                      loadData.push([row[dtag.param["xAxis"]], row[dtag.param["yAxis"]]]);
                    }
                    if (dtag.showType=="bar") {
                      dTags.dArray[i].loadData={"label":_tData.titles[dtag.param["xAxis"]], "data":loadData, "bars":{show:true, barWidth: 0.3, align: "center", fill:0.6}};
                    } else if (dtag.showType=="line") {
                      dTags.dArray[i].loadData={"label":_tData.titles[dtag.param["xAxis"]], "data":loadData, "lines":{show:true}, "points":{show:true}};
                    }
                  }
                }
              }
            }
          }
          var canDraw=true;
          errMsg="";
          for (i=0; i<dTags.dArray.length; i++) {
            if (!dTags.dArray[i].loadData) {
              canDraw=false;
              break;
            }
            if (typeof(dTags.dArray[i].loadData)=='string') {
            	errMsg+="<br\>类型为["+transTagType(dtag.showType)+"]的子图("+dTags.dArray[i].index+")："+dTags.dArray[i].loadData;
            }
          }
          if (canDraw) {
            var jqObj=$("#"+p);
            if (errMsg&&jqObj) jqObj.html("<span class='errMsgSpan'>"+errMsg.substr(4)+"</span>");
            else D_Tags.drawTags(jqObj, dTags);
          };
        }
      }
    }
  },
  //画折线图:jqEle(html元素的jquery对象)，dtag(标签信息)，d(表数据)
  drawTags: function(jqEle, dTags) {
  	if (dTags&&dTags.dArray&&dTags.dArray.length>0) {
  		var series=new Array();
  		for (var i=0; i<dTags.dArray.length; i++) {
  			series.push(dTags.dArray[i].loadData);
  		}
      $.plot(jqEle, series, {
        grid: {
          hoverable: true,
          borderWidth: {top: 0, right: 0, bottom: 1, left: 1}
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
        }
      });
  	}
  }
};
//=DTags处理=end==========================