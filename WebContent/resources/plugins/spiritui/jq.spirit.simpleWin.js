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
    var _options = $.extend(true, $.spiritPageFrame.defaults, options);
    var winDiv = $("<div style='background-color:red; border:1px solid green;'></div>");//窗口主对象
    //样式处理
    winDiv.css({
      "height": opt.height,
      "width": opt.width,
      "z-index": opt.zIndex,
      "display": "none"
    });
    $.data(winDiv, 'spiritSimpleWin', _options);//绑定参数

    var swinId = "sui-sWinID-"+getUuid(6);
    winDiv.attr("id", swinId);

    //方法处理
    winDiv.getId=function() {//获得Id
      return (winDiv.attr("id"));
    };
    winDiv.open=function() {//打开窗口
      var opt = $.data(this, "spiritSimpleWin");
      alert(opt.height);
      //遮罩层处理
      if ($("body>div#_wMask").length==0) {
        var maskDiv = $("<div id='_wMask'></div>");
        $("body").append(maskDiv);
        
      } else {
        $("body>div#_wMask").css({
          "z-index": opt.zIndex-1,
          "display":"block"
        });
      }
      //显示窗口
      this.css({
        "display":"block",
        "position":"absolute",
        "top": ($(window).height()-parseInt(opt.height))/2,
        "left":($(window).width()-parseInt(opt.width))/2
      });
    };
    winDiv.resize=function(){//当窗口调整大小时
    	
    };
    winDiv.close=function(){
      
    };

    $("body").append(winDiv);
    $(window).resize(winDiv.resize());
    return winDiv;
  }

  //简单窗口主函数
  $.spiritSimpleWin = function(options) {
    return initSimpleWin(options);
  };

  //默认属性
  $.spiritSimpleWin.defaults = {
    title: "标题",
    titleStyle: "", //标题样式
    headStyle: "", //窗口头样式：主要是高度和背景色

    url: "", //内部的url用iframe实现
    content: "", //内部的html

    width: 640, //窗口的宽度
    height: 480, //窗口的高度

    icon_css: "", //窗口图标的css
    icon_url: "" //窗口图标的url
  };
})(jQuery);

/*
  //默认属性
  $.spiritSimpleWin.defaults = {
    title: "标题",
    titleStyle: "", //标题样式
    headStyle: "", //窗口头样式：主要是高度和背景色

    url: "", //内部的url用iframe实现
    content: "", //内部的html

    width: 640, //窗口的宽度
    height: 480, //窗口的高度

    icon_css: "", //窗口图标的css
    icon_url: "", //窗口图标的url
    zIndex: 1000 //
  };
*/