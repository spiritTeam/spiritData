/**
 * jQuery spiritui-simpleWin 精灵组件
 * 简单window。
 * 目前窗口时模态的，不能拖动和改变大小
 *
 * Copyright (c) 2014.9 wh
 *
 * Licensed same as jquery - MIT License
 * http://www.opensource.org/licenses/mit-license.php
 */

(function($) {
  //本控件内的全局变量
  var _bv = getBrowserVersion();

  function initSimpleWin(options) {
    //参数合并，传入的参数和默认参数
    var _options = $.extend(true, $.fn.spiritPageFrame.defaults, options);
    if ($.trim(_options.pageObjs.mainId)=="") return "未指定主体部分Id，无法初始化页面！";
    if ($("#"+_options.pageObjs.mainId).length==0) return "无id为\""+_options.pageObjs.mainId+"\"的元素，无法初始化页面！";
    INIT_PARAM = _options;//绑定参数
    //处理中间的部分
    $("body").append("<div id='_main'><div id='_top'></div><div id='_foot'></div></div>");
    $("body>div#_main>div#_top").after($("#"+_options.pageObjs.mainId));
    //0-预处理
    _hScrollbarWidth=getHScrollbarWidth();//纵向滚动条
    _wScrollbarWidth=getWScrollbarWidth();//横向滚动条
    _hasTop = $.trim(_options.pageObjs.topId)!=""&&$("#"+_options.pageObjs.topId).length==1;//是否有头部
    _hasFoot = $.trim(_options.pageObjs.footId)!=""&&$("#"+_options.pageObjs.footId).length==1;//是否有尾部
    //样式处理
    if (_hasTop) $("#"+_options.pageObjs.topId).addClass("hoverArea").addClass("topSegment");
    if (_hasFoot) $("#"+_options.pageObjs.footId).addClass("hoverArea").addClass("footSegment");

    initPosition();//初始化
    $(window).resize(_resizeTimeout);//页面调整
    $(window).scroll(scrollPositioin);//滚动条
    return "";
  }

  //简单窗口主函数
  $.fn.spiritSimpleWin = function(options, param) {
    //若参数一为字符串，则直接当作本插件的方法进行处理，这里的this是本插件对应的jquery选择器的选择结果
    if (typeof options=='string') return $.fn.spiritSimpleWin.methods[options](this, param);

    return initWin(options);
  };
  //插件方法，参考eaqyUi的写法
  $.fn.spiritSimpleWin.methods = {
  };

/* winOption.title 窗口标题
  * winOption.url 窗口内嵌的iframe的url
  * winOption.height 窗口高度
  * winOption.width 窗口宽度
  * winOption.icon_css 窗口图标，是css中的名称
  * winOption.icon_url 窗口图标，图标的url，若设置了此参数，icon_css失效
  * winOption.modal 是否是模态窗口，默认为模态窗口
  * winOption.expandAttr 窗口的扩展属性，可定义iframe的id，是javaScript对象，如expandAttr={"frameID":"iframeID"}
*/
  //默认属性
  $.fn.spiritSimpleWin.defaults = {
    height:100,
    width:100,
    title:"",
    modal:true,
    btnBar:[{
    },{
      
    }]
  };

})(jQuery);
/*
  //默认属性
  $.fn.spiritPageFrame.defaults = {
    //页面中所用到的元素的id，只用到三个Div，另，这三个div应在body层
    pageObjs: {
      topId: "topSegment", //头部Id
      mainId: "mainSegment", //主体Id
      footId: "footSegment" //尾部Id
    },

    page_width: 0, //主页面的宽度。<0：宽度不控制；0：宽度自适应；>0：宽度值，页面定宽
    page_height: 0, //主页面的高度。<0：高度不控制；0：高度自适应；>0：高度值，页面定高

    win_min_width: 640, //页面最小的高度。当窗口高度小于这个值，不对界面位置及尺寸进行调整。主体部分宽度也照此设置
    win_min_height: 480, //页面最小的高度。当窗口高度小于这个值，不对界面位置及尺寸进行调整。主体部分高度也照此设置

    top_height: 120, //顶部高度
    top_shadow_color: null, //头部阴影颜色
    top_peg: false, //是否钉住头部在顶端。false：顶部随垂直滚动条移动(浮动)；true：顶部钉在顶端

    foot_height: 40, //脚部高度
    foot_peg: false, //是否钉住脚部在底端。false：脚部随垂直滚动条移动(浮动)；true：脚部钉在底端

    iframe_height_flag: 1 //具体功能区域（可能是整个中部，也可能是带左侧导航的中部）的iframe高度标志。1：iframe高度与框架匹配；非1：框架高度适应iframe内部高度(反向适应)
  };
*/