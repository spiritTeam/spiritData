/**
 * jQuery spiritui-simpleWin 精灵组件
 * 简单window。
 * 目前窗口时模态的，不能拖动和改变大小
 *
 * Copyright (c) 2014.12 wh
 *
 * Licensed same as jquery - MIT License
 * http://www.opensource.org/licenses/mit-license.php
 */

(function($) {
  //本控件内的全局变量
  //var _bv = getBrowserVersion();版本信息，现在没用上
  //默认属性
  var defaults = {
    width: 640, //窗口的宽度
    height: 480, //窗口的高度
    title: "窗口标题",
    onBeforeClose: null, //窗口关闭之前
    zIndex:1000
  };

  /**
   * 生成UUID，默认为36位
   */
  var CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".split("");  
  function getUuid(len,radix) {
    var chars = CHARS, uuid = [];
    radix = radix||chars.length;
    if (len) {
      for (var i=0; i<len; i++) uuid[i] = chars[0 | Math.random()*radix];  
    } else {
      var r;
      uuid[8] = uuid[13] = uuid[18] = uuid[23] = "-";
      uuid[14] = "4";
      for (var i=0; i<36; i++) {
        if (!uuid[i]) {
          r = 0 | Math.random()*16;
          uuid[i] = chars[(i == 19)?(r&0x3)|0x8:r];
        }
      }
    }
    return uuid.join("");
  }

  function initSimpleWin(options) {
    //参数合并，传入的参数和默认参数
    var _options = $.extend(true, {}, defaults, options);
    var swinId = "sui-sWinID-"+getUuid(6);
    var winDiv = $("<div class='sWin'></div>");//窗口主对象
    $("body").append(winDiv);
    winDiv.attr("id", swinId);
    $.data(winDiv, 'spiritSimpleWin', _options);//绑定参数
    //样式处理
    winDiv.css({
      "height": _options.height,
      "width": _options.width,
      "z-index": _options.zIndex,
      "display": "none"
    });

    //1-画里面的内容
    //a)头
    var headDiv=$("<div class='sWin_head'><div class='sWin_icon' /><div class='sWin_title' /><div class='sWin_closeBtn' /></div>");
    winDiv.append(headDiv);
    //窗口头
    if (_options.headCss) headDiv.css(headCss);
    headDiv.spiritUtils("setWidthByViewWidth", winDiv.width());
    //窗口图标
    if (_options.iconCss) headDiv.find("div.sWin_icon").css(iconCss);
    else if (_options.iconUrl) headDiv.find("div.sWin_icon").css("background-image", "url('"+_options.iconUrl+"') no-repeat");
    //标题区域
    if (_options.titleCss) headDiv.find("div.sWin_title").css(titleCss);
    headDiv.find(".sWin_title").html(_options.title);
    headDiv.find(".sWin_title").spiritUtils("setWidthByViewWidth", headDiv.spiritUtils("getViewWidth"));
    //关闭按钮
    headDiv.find(".sWin_closeBtn").css({
      "left": winDiv.width()-headDiv.find(".sWin_closeBtn").width()-10
    });
    headDiv.find(".sWin_closeBtn").mouseover(function(){
      $(this).css("background-position", "-72px -48px");
    }).mouseout(function(){
      $(this).css("background-position", "-56px -48px");
    }).click(function(){
      winDiv.close();
    });
    //b)体
    var contentDiv = $("<div class='sWin_content'></div>");//窗口内容
    winDiv.append(contentDiv);
    contentDiv.spiritUtils("setWidthByViewWidth", winDiv.width());
    contentDiv.spiritUtils("setHeightByViewHeight", winDiv.height()-headDiv.spiritUtils("getViewHeight"));
    contentDiv.css("top", headDiv.spiritUtils("getViewHeight"));
    if (_options.url) {
      var _iframe = window.document.createElement("iframe");
      $(_iframe).attr("width", "100%").attr("height", "100%").attr("scrolling", "no").attr("frameborder", "no").attr("src", _options.url.indexOf("?")==-1?_options.url+"?_winID="+swinId:_options.url+"&_winID="+swinId);
      if (_options.expandAttr) {
        if (_options.expandAttr.frameID) $(_iframe).attr("id", _options.expandAttr.frameID);
      }
      $(_iframe).appendTo($(contentDiv));
    } else {
      $(contentDiv).html(_options.content);
    }
    //2-拖动效果
    headDiv[0].beginX=-1;
    headDiv[0].beginY=-2;
    headDiv[0].beginTop=-1;
    headDiv[0].beginLeft=-2;
    headDiv[0].isDown=false;
    headDiv.mousemove(function(e){
      if (this.isDown) {
        $(this).parent().css({
          top: this.beginTop+(e.pageY-this.beginY),
          left: this.beginLeft+(e.pageX-this.beginX)
        });
      }
    }).mousedown(function(e){
      this.beginX=e.pageX;
      this.beginY=e.pageY;
      this.beginTop=parseFloat($(this).parent().css("top"));
      this.beginLeft=parseFloat($(this).parent().css("left"));
      this.isDown=true;
    }).mouseup(function(){this.isDown=false;}).mouseleave(function(){this.isDown=false;});
    //3-方法处理
    winDiv.getId=function() {//获得Id
      return (winDiv.attr("id"));
    };
    winDiv.open=function() {//打开窗口
      var opt = $.data(this, "spiritSimpleWin");
      //遮罩层处理
      var maskDiv=$("body>div._wMask");
      if (maskDiv.length==0) {
        maskDiv = $("<div class='_wMask'></div>");
        $("body").append(maskDiv);
      }
      maskDiv.css({
        "z-index": opt.zIndex-1,
        "display":"block"
      });
      //显示窗口
      this.css({
        "display":"block",
        "position":"absolute",
        "top": ($(window).height()-parseFloat(opt.height))/2,
        "left":($(window).width()-parseFloat(opt.width))/2
      });
    };
    winDiv.resize=function(){//当窗口调整大小时，resize不进行处理
      //var abc='ddd';
    };
    winDiv.modify=function(options) {//修改参数，目前只修改标题
    	var opt = $.data(this, "spiritSimpleWin");
    	opt = $.extend(true, {}, opt, options);
      $.data(this, 'spiritSimpleWin', opt);//绑定参数
      if (opt.title&&opt.title!="") $(this).find(".sWin_head>.sWin_title").html(opt.title);
    };
    winDiv.close=function(){
      var opt = $.data(this, "spiritSimpleWin");
      if (opt.onBeforeClose) opt.onBeforeClose(winDiv.attr("id"));
    	winDiv.remove();
      $("body>div._wMask").css("display", "none");
      //删除对象
      //resize去掉
    };

    return winDiv;
  }

  //简单窗口主函数
  $.spiritSimpleWin = function(options, param) {
    return initSimpleWin(options);
  };

  //绑定window的resize方法
  //100毫秒后调整页面位置，为其中的控件调整位置准备时间
  function _swResizeTimeout() {
    setTimeout(resizePosition ,100);
  }
  function resizePosition() {
    var _sWins = $("body>div.sWin");
    var i=0;
    var len=_sWins.length;
    for (; i<len; i++) {
      var _sWin = _sWins[i];
      $(_sWin).css({
        "top": ($(window).height()-parseFloat($(_sWin).height()))/2,
        "left":($(window).width()-parseFloat($(_sWin).width()))/2
      });
    }
  }
  $(window).resize(_swResizeTimeout);
})(jQuery);

/*
  //默认属性
  defaults = {
    width: 640, //窗口的宽度
    height: 480, //窗口的高度
    url: "", //内部的url用iframe实现
    content: "", //内部的html

    headCss: "", //窗口头样式：主要是高度和背景色
    title: "标题",
    titleCss: "", //标题样式
    iconCss: "", //窗口图标的css
    iconUrl: "", //窗口图标的url

    onBeforeClose: null, //窗口关闭之前
    zIndex:1000,
    expandAttr 窗口的扩展属性，可定义iframe的id，是javaScript对象，如expandAttr={"frameID":"iframeID"}
  };
*/