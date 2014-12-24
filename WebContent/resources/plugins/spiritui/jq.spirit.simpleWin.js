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
  var _bv = getBrowserVersion();
  //默认属性
  var defaults = {
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
    winDiv.attr("id", swinId);
    $.data(winDiv, 'spiritSimpleWin', _options);//绑定参数
    //样式处理
    winDiv.css({
      "height": _options.height,
      "width": _options.width,
      "z-index": _options.zIndex,
      "display": "none"
    });

    //画里面的内容
    //1-头
    var titleDiv=$("<div class='sWin_head'><div class='sWin_icon' /><div class='sWin_title' /><div class='sWin_closeBtn' /></div>");
    //窗口头
    titleDiv.spiritUtils("setWidthByViewWidth", winDiv.width());
    if (_options.headCss) titleDiv.css(headCss);
    titleDiv.spiritUtils("setWidthByViewWidth", winDiv.width());
    //窗口图标
    if (_options.iconCss) titleDiv.find("div.sWin_icon").css(iconCss);
    else if (_options.iconUrl) titleDiv.find("div.sWin_icon").css("background", "url('"+_options.iconUrl+"') no-repeat");
    //标题区域
    if (_options.titleCss) titleDiv.find("div.sWin_title").css(titleCss);
    titleDiv.find(".sWin_title").html(_options.title);
    titleDiv.find(".sWin_title").spiritUtils("setWidthByViewWidth", titleDiv.spiritUtils("getViewWidth"));
    //关闭按钮
    titleDiv.find(".sWin_closeBtn").css({
      "left": winDiv.width()-titleDiv.find(".sWin_closeBtn").width()-10
    });
    titleDiv.find(".sWin_closeBtn").mouseover(function(){
    	$(this).css("background-position", "-72px -48px");
    }).mouseout(function(){
    	$(this).css("background-position", "-56px -48px");
    }).click(function(){
      $(this).parent().parent().hide();
      winDiv.close();
      $("body>div._wMask").hide();
    });
    winDiv.append(titleDiv);
    //2-体
    var contentDiv = $("<div class='sWin_content'></div>");//窗口内容
    contentDiv.spiritUtils("setWidthByViewWidth", winDiv.width());
    contentDiv.spiritUtils("setHeightByViewHeight", winDiv.height()-titleDiv.spiritUtils("getViewHeight"));
    contentDiv.css("top", titleDiv.spiritUtils("getViewHeight"));
    if (_options.url) {
      var _iframe = window.document.createElement("iframe");
      $(_iframe).attr("width", "100%").attr("height", "100%").attr("scrolling", "no").attr("frameborder", "no").attr("src", _options.url.indexOf("?")==-1?_options.url+"?_winID="+swinId:_options.url+"&_winID="+swinId);
      if (_options.expandAttr) {
        if (_options.expandAttr.frameID) $(_iframe).attr("id", _options.expandAttr.frameID);
      }
      $(_iframe).appendTo($(contentDiv));
    }

    winDiv.append(contentDiv);

    //方法处理
    winDiv.getId=function() {//获得Id
      return (winDiv.attr("id"));
    };
    winDiv.open=function() {//打开窗口
      var opt = $.data(this, "spiritSimpleWin");
      //遮罩层处理
      if ($("body>div._wMask").length==0) {
        var maskDiv = $("<div class='_wMask'>AABBCCDD</div>");
        $("body").append(maskDiv);
      } else {
        $("body>div._wMask").css({
          "z-index": opt.zIndex-1,
          "display":"block"
        });
      }
      //显示窗口
      this.css({
        "display":"block",
        "position":"absolute",
        "top": ($(window).height()-parseFloat(opt.height))/2,
        "left":($(window).width()-parseFloat(opt.width))/2
      });
    };
    winDiv.resize=function(){//当窗口调整大小时
      
    };
    winDiv.close=function(){
    	//alert(winDiv.getId());
      //删除对象
      //resize去掉
    };

    $("body").append(winDiv);
    return winDiv;
  }

  //简单窗口主函数
  $.spiritSimpleWin = function(options) {
    return initSimpleWin(options);
  };

  //绑定window的resize方法
  //100毫秒后调整页面位置，为其中的控件调整位置准备时间
  function _swResizeTimeout() {
    setTimeout(resizePosition ,100);
  }
  function resizePosition() {
    var _sWins = $("body>div.sWin");
    var i=0; len=_sWins.length;
    window.console.log(len);
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
    zIndex:1000
  };
*/