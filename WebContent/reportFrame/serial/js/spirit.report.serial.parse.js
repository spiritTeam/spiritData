/**
 * 精灵报告解析及展示方法
 * 需要用到：
 * JS——jquery+spirit.pageFrame
 * CSS——在本文件所在目录的css目录下的report.css文件中
 * sysInclude.jsp——从这里取根_PATH
 */

/**
 * 主函数，生成报告。
 * 报告的生成，需要一个干净的html容器。
 * 注意：
 * 1-执行此方法的html页面，此方法会把这个页面中的body内容全部清空。
 * 2-次方法会自动加载需要的资源——(这个功能后续再处理，目前需要的资源需要先行在html中加载)
 * 3-目前报告中的图形采用jqplot+eChart控件，表格采用easyUi的表格，提示的窗口采用本框架封装的功能
 * @param param 解析报告的参数，主要是获得报告的必要信息，包括
 * param = {
 *   getUrl: 获得报告的Url比如getReport.do，可为空，默认为：report/getReport.do?（一般不用设置，除非有新的获得报告的方法），注意设置时，开头不要以“/”开头，以此为开头，将不会自动加入_PATH的前缀
 *   reportUri：获得报告json的直接Uri
 *   reportId：报告Id
 * }
 */
function generateReport(param) {
  //1-得到获得报告的Url
  var _getUrl = reportParse.parseParam(param);
  if (!_getUrl) return ;

  //2-初始化界面
  $("body").html("");//清空页面
  initPageFrame();

  //3-读取report.json
  reportParse.get_parseReport(_getUrl);
}

/**
 * 为辅助解析的数据结构
 */
var parseSysData = {
  monitorData:new Obejct(), //为监控DList读取设置的对象
  treeData:null //生成树，此树包括parent属性
};

/**
 * 报告解析对象,此对象主要是方法的集合。
 */
var reportParse ={
  /**
   * 解析参数，并得到获得报告数据的Url，此方法中若有不合法的参数，会调用平台message(目前采用easyUi的message)方法给出提示。
   * 若不能解析为获得报告的Url则返回null
   * @param param解析报告的参数，主要是获得报告的必要信息，包括
   * param = {
   *   getUrl: 获得报告的Url比如getReport.do，可为空，默认为：report/getReport.do?（一般不用设置，除非有新的获得报告的方法），注意设置时，开头不要以“/”开头，以此为开头，将不会自动加入_PATH的前缀
   *   reportUri：获得报告json的直接Uri
   *   reportId：报告Id
   * }
   */
  parseParam: function(param) {
    var mPage=getMainPage();
    //1-参数校验
    var checkOk = true;
    var _msg = "", _temp = null, _url = null;
    //1.1-校验整个参数
    if (!param) {
      _msg = "参数为空，无法显示报告！";
      if (mPage) mPage.$.messager.alert("提示", _msg, "error");
      else alert(_msg);
      checkOk = false;
    }
    //1.2-校验报告Id或Uri，若有Uri，则Id被忽略
    if (checkOk) {
      _temp = param.reportUri;
      if (_temp&&$.trim(_temp)!=""&&$.trim(_temp)!="undefined") {
        _url = "?uri="+encodeURIComponent(param.reportUri);
      } else {
        _temp = param.reportId;
        if (_temp&&$.trim(_temp)!=""&&$.trim(_temp)!="undefined") {
          _url = "?reportId="+param.reportId;
        } else {//两个参数都没有设置，出错了
          _msg="报告Uri或Id至少指定一项，目前两项都未指定，无法显示报告！";
          if (mPage) mPage.$.messager.alert("提示", _msg, "error");
          else alert(_msg);
          checkOk = false;
        }
      }
    }
    if (!checkOk) return null;//若校验不通过，则后面的逻辑都不做了
    //1.3-组装获取report数据的Url
    _temp = param.getUrl;
    if (_temp&&$.trim(_temp)!=""&&$.trim(_temp)!="undefined") {
      _temp=decodeURIComponent(_temp);
      if (_temp.indexOf("/")!=0&&_temp.indexOf("\\")!=0) {//无根，加上根
        _temp=_PATH+"/"+_temp;
      }
      _url = _temp+_url;
    } else {
      _url = _PATH+"/report/getReport.do"+_url;
    }
    return _url;
  },
  /**
   * 获得报告数据，注意这里采取ajax的同步方式，返回获得的数据
   * @param url 获得数据的Url
   */
  get_parseReport:function(getUrl) {
    var mPage=getMainPage();
    var _msg = "";
    $.ajax({type:"get",url:getUrl ,async:true, dataType:"json",
      success: function(json) {
        if (json.jsonType!=1) {
          _msg = json.message;
          if (mPage) mPage.$.messager.alert("提示", _msg, "error");
          else alert(_msg);
        } else {//解析，并画内容
          var _data = eval("(" +json.data+ ")");
          reportParse.parseAndDraw(_data);
        }
      }
    });
  },

  //以下都为内部调用函数
  /**
   * 解析report数据，进行第一次扫描：
   * 1-画出报告结构
   * 2-计算整理树对象
   * 3-记录d标签结构
   * @param rptData 报告对象，注意，必须是javascript对象
   */
  parseAndDraw:function(rptData) {
    //解析DLIST，并轮询获取之
    parseSysData.monitorData = reportParse.parseDList(rptData._DLIST);
    //获得报告名称，并显示
    $('#rTitle').html(rptData._HEAD._reportName);
    //解析REPORT（报告主体），并画报告的主体
    var ret = reportParse.parseReport(rptData._REPORT);
    if (ret==0) {//出错了，给出提示，并结束后续的处理、
      var mPage=getMainPage();
      if (mPage) mPage.$.messager.alert("提示", ret.msg, "error");
      return ;
    };
    //画树
    parseSysData.treeData = ret.treeRoot;
    
    //启动获取数据的轮询过程
  },

  /**
   * 解析DList，并返回需要的结构，此结构用于
   * @param _DLIST DList对象
   */
  parseDList: function(_DLIST) {
    if (_DLIST==null||_DLIST==""||_DLIST.length<=0) return ;
    var monitDListObj = new Object();
    monitDListObj.allSize = _DLIST.length; //监控对象：Dlist总数
    monitDListObj.okSize = 0; //成功获得数据的节点数
    monitDListObj.faildSize = 0; //获得数据失败节点个数
    monitDListObj.dList = new Array(_DLIST.length); //数据列表——数组
    for (var i=0;i<_DLIST.length;i++) {
      var oneData = new Object();
      oneData.url = _DLIST[i]._url;
      oneData.jdCode = _DLIST[i]._jsonDCode;
      oneData.getFlag = 0; //获取状态，0还需获取；1获取成功；2获取失败
      oneData.drawFlag = 0; //画D标签的状态：0未画；1正在画；2画完了
      oneData.dataStr = ""; //获取的数据，以字符串方式存储
      monitDListObj.dList[_DLIST._id]=oneData;
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

    var level = 0;
    var treeRoot = new Object();
    var divDtagMap = new Object();

    //处理系统跟结点
    treeRoot.id=-1;
    treeRoot.text="系统根结点";
    if (_REPORT&&_REPORT.length>0) {
      for (int i=0; i<_REPORT.length; i++) {
        var ret = recursionSegs(_REPORT[i], treeRoot, divDtagMap, level, $("#reportFrame"), i);
        if (ret.type==0) { //出错了，不进行处理了
          return ret;
        }
      }
    }
    ret.treeRoot = treeRoot;
    //返回内容
    return ret;
  }
};

/**
 * 采用pageFrame框架，初始化报告界面
 */
function initPageFrame(){
  //1、画pageFrame
  //1-1:头部元素
  var topSegment =$('<div id="topSegment"><div id="rTitle"></div></div>');
  $("body").append(topSegment);
  //1-2:主体元素
  var mainSegment = $('<div id="mainSegment" style="background-color:yellow;border:2px solid red;"></div>');
  //1-2-1:右侧的报告结构树
  var sideFrame = $('<div id="sideFrame"><div id="catalogTree" style="height:100px; border:1px solid blue;"></div></div>');
  mainSegment.append(sideFrame);
  //1-2-2:报告主体
  var reportFrame = $('<div id="reportFrame"></div>');
  mainSegment.append(reportFrame);
  $("body").append(mainSegment);
  //1-3:尾部元素
  var footSegment = $('<div id="footSegment"></div>');
  $("body").append(footSegment);
  //INIT_PARAM
  var INIT_PARAM = {
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
    $("#reportFrame").spiritUtils("setWidthByViewWidth", $("body").width()-$("#sideFrame").spiritUtils("getViewWidth"));
    $("#sideFrame").css("left", $("#reportFrame").width());
  }
  var initStr = $.spiritPageFrame(INIT_PARAM);
  if (initStr) {
    $.messager.alert("页面初始化失败", initStr, "error");
    return ;
  };
};

/**
 * 递归扫描report内容，并构造需要的结构
 * @param segs 报告的一个segment
 * @param ptn 树对象
 * @param ddm div与Dtag的对应关系
 * @param l 树的Level
 * @param jqObj jquery的dom对象
 * @param index 数组中的下标
 * @returns 若解析错误，则放回false
 */
function recursionSegs(segs, ptn, ddm, l, jqObj, index) {
  var ret = new Object();

  var rankId = l+":"+index; //级别Id
  var pTnId = (td)?td.id:null;//上级结点id
  var priorityName = (segs.name&&$.trim(segs.name))?segs.name:((segs.title&&$.trim(segs.title))?segs.title:null);
  var priorityTitle = (segs.title&&$.trim(segs.title))?segs.title:((segs.name&&$.trim(segs.name))?segs.name:null);

  //1-处理树
  var treeNode = new Object();
  //1.1-树结点id
  treeNode.id = pTnId?pTnId+"_"+rankId:"_tn_"+rankId;
  if (segs.id&&$.trim(segs.id)) treeNode.sgid="_tnsg_"+segs.id;//若有段落id，则记录一下
  else {
    ret.type = 0; //处理失败
    ret.meg = "在处理段落时，发现段落没有设置id，无法处理！";
    if (ptn.id==-1) ret.msg += "此段落为根段落的"+index+"个子段落";
    else ret.msg += "此段落为id=["+ptn.id+"]段落的"+index+"个子段落";
    return ret;
  }
  //1.2-树结点名称
  if (priorityName) treeNode.text = priorityName;
  if (!treeNode.text||!$.trim(treeNode.text)) {
    ret.type = 0; //处理失败
    ret.meg = "在处理id=["+treeNode.id+"]的段落时，发现段落的名称或标题没有设置，无法处理";
    return ret;
  }
  //1.3把树节点挂接到上级节点
  if (!ptn.children) ptn.children = new Array();
  treeNode.parent=ptn;
  ptn.children[index]=treeNode;

  //2-画显示对象
  //2.1-每个segment的框架
  var docEle_html = '<div id="segment_'+rankId+'" class="segment segment_'+l+'"><ul>'
    + '<li id="segTitle_'+rankId+'" class="segTitle segTitle_'+l+'">'+priorityTitle+'</li>';
  if (segs.content&&$.trim(segs.content)) docEle_html
    +='<li id="segContent_'+rankId+'" class="segContent segContent_'+l+'"></li>';
  if (segs.subSegs&&segs.subSegs.length>0) docEle_html
    +='<li id="segSubs_'+rankId+'"><div id="ssBody_'+rankId+'" class="segSubs"></div></li>';
  //2.2-生成具体的段内容
  if (segs.content&&$.trim(segs.content)) {
  	var _content = segs.content;
  	//找到D标签
  }
  var docEle = $(docEle_html);//本级doc对象，以jquery方式处理
  jqObj.append(docEle);

  //3-处理div与Dtag的对应关系

  //递归
  if (segs.subSegs&&segs.subSegs.length>0) {
    for (var i=0; i<segs.subSegs.length; i++) {
      var ret = recursionSegs(segs.subSegs[i], ptn, ddm, l+1, $("#ssBody_"+rankId), i);
      if (ret.type==0) { //出错了，不能再处理了
        return ret;
      }
    }
  }
}