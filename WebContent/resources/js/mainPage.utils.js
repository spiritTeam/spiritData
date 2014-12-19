/**
 * 主界面相关的Javascript方法，注意这个js只能被引用在主界面，这里的方法不处理主界面的业务逻辑。
 * 只处理框架中针对主界面，或其他界面调用主界面的逻辑。
 * 注意，他要引用在jquery引用的后面
 */
var IS_MAINPAGE=true;

/**
 * 打开windows的数组
 */
var winArray = [];
var _Zindex = 1000;
var winMask = null;

/**
 * 得到窗口的ID
 * @returns 窗口ID
 */
function getWinUUID() {
  //得到UUID
  var _uuid="";
  var isID=false;
  while (!isID) {
    _uuid=getUUID();
    isID=true;
    $(winArray).each(function(){
      if (this.winID==_uuid) {
        isID=false;
        return;
      }
    });
  }
  return _uuid;
}

/**
 * 创建并打开easyUi的窗口
 * @param winOption是一个js对象，目前支持如下参数
 * winOption.title 窗口标题
 * winOption.url 窗口内嵌的iframe的url
 * winOption.height 窗口高度
 * winOption.width 窗口宽度
 * winOption.icon_css 窗口图标，是css中的名称
 * winOption.icon_url 窗口图标，图标的url，若设置了此参数，icon_css失效
 * winOption.modal 是否是模态窗口，默认为模态窗口
 * winOption.expandAttr 窗口的扩展属性，可定义iframe的id，是javaScript对象，如expandAttr={"frameID":"iframeID"}
 * winOption.top 窗口top
 * winOption.left 窗口left
 * @returns 返回生成窗口的UUID
 */
function newWin(winOption) {
  if (!winOption) {
    $.messager.alert('新建窗口错误','请指定窗口参数!','error');
    return ;
  } else {
    if (!winOption.url) {
      $.messager.alert('新建窗口错误','窗口参数url必须指定!','error');
      return ;
    }
  }
  //得到UUID
  var _uuid = getWinUUID();
  if (!_uuid) return ;
  //在顶层窗口创建对象
  var newWinDiv = window.document.createElement("div");
  var newWin = window.document.createElement("iframe");
  $(newWin).attr("width", "100%").attr("height", "100%")
    .attr("scrolling", "no")
    .attr("frameborder", "no")
    .attr("src", winOption.url.indexOf("?")==-1?winOption.url+"?_winID="+_uuid:winOption.url+"&_winID="+_uuid);
  if (winOption.expandAttr) {
    if (winOption.expandAttr.frameID) $(newWin).attr("id", winOption.expandAttr.frameID);
  }
  $(newWin).appendTo($(newWinDiv));

  //esayUi win处理
  var top = ($(window).height() - parseInt(winOption.height?winOption.height:0))*0.5;
  if (winOption.top&&winOption.top!=""&&((parseInt(winOption.top)+"")!="NaN")) top=parseInt(winOption.top);
  var left = ($(window).width() - parseInt(winOption.width?winOption.width:0))*0.5;
  if (winOption.left&&winOption.left!=""&&((parseInt(winOption.left)+"")!="NaN")) left=parseInt(winOption.left);
  $(newWinDiv).window({
    title: winOption.title?winOption.title:"",
    width: parseInt(winOption.width?winOption.width:400),
    height: parseInt(winOption.height?winOption.height:300),
    top: top,
    left: left,
    modal: ((winOption.modal==undefined)?true:winOption.modal),
    collapsible: false,
    shadow: true,
    closed: true,
    resizable:false,
    draggable: true,
    inline: false,
    minimizable: false,
    maximizable: false,
    onBeforeClose: function(){
      var winID=$(newWinDiv).attr("winID");
      var _i=-1;
      $(winArray).each(function(i){
        if (winID==this.winID) {
          _i=i;
          return ;
        }
      });
      if (_i!=-1) winArray.splice(_i,1);
    },
    onClose: function() {
      $(newWinDiv).window("destroy");
    }
  });
  //处理窗口图标icon_css
  if (winOption.icon_css) {
    $(newWinDiv).window({iconCls: winOption.icon_css});
  }
  //处理窗口图标icon_url
  if (winOption.icon_url) {
    $(newWinDiv).window({iconCls: "abc"});//设置图标区域 background
    //设置效果
    var winObj = $(newWinDiv).parent();
    var tempObj = winObj.parent().find(".panel-icon");
    $(tempObj).css("background", "url('"+winOption.icon_url+"') no-repeat center center");
  }
  $(newWinDiv).attr("winID", _uuid);
  $(newWinDiv).window("open");
  if (winOption.expandAttr) $(newWinDiv).expandAttr = winOption.expandAttr;
  //全局变量处理
  winArray.push({"winID": _uuid, "winOBJ": $(newWinDiv)});
  return _uuid;
}


/**
 * 创建并打开简单模态窗口
 * @param winOption是一个js对象，目前支持如下参数
 * winOption.title 窗口标题
 * winOption.url 窗口内嵌的iframe的url
 * winOption.height 窗口高度
 * winOption.width 窗口宽度
 * winOption.minButton 最小化窗口按钮是否显示
 * winOption.expandAttr 窗口的扩展属性，可定义iframe的id，是javaScript对象，如expandAttr={"frameID":"iframeID"}
 * @returns 返回生成窗口的UUID
 */
function newSWin(winOption) {
  if (!winOption) {
    $.messager.alert('新建窗口错误','请指定窗口参数!','error');
    return ;
  } else {
    if (!winOption.url) {
      $.messager.alert('新建窗口错误','窗口参数url必须指定!','error');
      return ;
    }
  }
  //得到UUID
  var _uuid = getWinUUID();
  if (!_uuid) return;
  //遮罩
  if (winMask==null) {
  	winMask=window.document.createElement("div");
    $(winMask).appendTo($("body"));
  }
  $(winMask).css({
  	width:$(window).width(),
    height:$(window).height(),
    top:0,
    left:0,
    border:"solid 0px",
    position:"absolute",
    "background-color": "#EBEBEB",
    zIndex:_Zindex++,
    display:"none",
    "filter":"alpha(opacity=70)",
    "-moz-opacity":"0.7",
    "opacity":"0.7"
  }).attr("id", "winMask");
  $(winMask).show();

  //在顶层窗口创建对象
  var newWinDiv = window.document.createElement("div");
  var newWin = window.document.createElement("iframe");
  $(newWin).attr("width", winOption.width?winOption.width:400).attr("height", winOption.height?winOption.height:300)
    .attr("scrolling", "auto")
    .attr("frameborder", "no")
    .attr("src", winOption.url.indexOf("?")==-1?winOption.url+"?_winID="+_uuid:winOption.url+"&_winID="+_uuid);
  if (winOption.expandAttr) {
    if (winOption.expandAttr.frameID) $(newWin).attr("id", winOption.expandAttr.frameID);
  }
  $(newWin).appendTo($(newWinDiv));
  var top = ($(window).height() - parseInt(winOption.height?winOption.height:0))*0.5;
  if (winOption.top&&winOption.top!=""&&((parseInt(winOption.top)+"")!="NaN")) top=parseInt(winOption.top);
  var left = ($(window).width() - parseInt(winOption.width?winOption.width:0))*0.5;
  if (winOption.left&&winOption.left!=""&&((parseInt(winOption.left)+"")!="NaN")) left=parseInt(winOption.left);
  $(newWinDiv).css({
    width: parseInt(winOption.width?winOption.width:400),
    height: parseInt(winOption.height?winOption.height:300),
    top: top,
    left: left,
    border: "solid 1px #36B148",
    position:"absolute",
    zIndex:_Zindex++,
    display:"none"
  }).attr("id", _uuid);
  //按钮调整
  //最小化按钮
  var btnBar = window.document.createElement("div");
  $(btnBar).appendTo($(newWinDiv));
  $(btnBar).css({
  	border: "solid 1px red",
    position:"absolute",
  	width: 80,
  	height: 40,
  	left:900,
  	top:0
  });
//  $(btnBar).hide();
  if (winOption.expandAttr) $(newWinDiv).expandAttr = winOption.expandAttr;
  $(newWinDiv).attr("winID", _uuid);
  $(newWinDiv).appendTo($("body"));
  $(newWinDiv).show();
  //全局变量处理
  winArray.push({"winID": _uuid, "winOBJ": $(newWinDiv)});
  return _uuid;
}

/**
 * 关闭并销毁窗口
 * @param winId 窗口的ID
 */
function closeWin(winId) {
  $(winArray).each(function(){
    if (this.winID==winId) {
      this.winOBJ.window('close');
      return ;
    }
  });
}

/**
 * 得到窗口jquery对象
 * @param winId 窗口的ID
 */
function getWin(winId) {
  var ret=null;
  $(winArray).each(function(){
    if (this.winID==winId) {
      ret = this.winOBJ;
      return ;
    }
  });
  return ret;
}