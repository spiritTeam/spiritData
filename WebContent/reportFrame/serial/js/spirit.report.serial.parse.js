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
 * 报告解析对象
 */
var reportParse ={
  mPage:null, //主页面
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
  	if (!this.mPage) this.mPage=getMainPage();
    //1-参数校验
    var checkOk = true;
    var _msg = "", _temp = null, _url = null;
    //1.1-校验整个参数
    if (!param) {
      _msg = "参数为空，无法显示报告！";
      if (this.mPage) mPage.$.messager.alert("提示", _msg, "error");
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
          if (this.mPage) mPage.$.messager.alert("提示", _msg, "error");
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
  	if (!this.mPage) this.mPage=getMainPage();
    var _msg = "";
    $.ajax({type:"get",url:getUrl ,async:true, dataType:"json",
      success:function(json){
        alert(json.jsonType);
        if (json.jsonType!=1) {
          _msg = json.message;
          alert(this.mPage);
          if (this.mPage) mPage.$.messager.alert("提示", _msg, "error");
          else alert(_msg);
        } else {//解析，并画内容
        	this.parseAndDraw(str2JsonObj(json.data));
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
  	alert("====\n"+allFields(rptData));
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
}