/**
 * 设置input控件效果
 */
function inputEffect() {
  //对tab到input处理
  $(".alertInputComp").bind('focus',function() {
    focusInput($(this));
  }).bind('blur',function() {
    blurInput($(this));
  });
  //对鼠标移入移出进行处理
  $(".alertInputComp").bind('mouseover',function(){
    $(this).focus();
  }).bind('mouseout',function(){
    $(this).blur();
  });
  /**
   * input的获得焦点
   * @param _jQobj：jq对象
   */
  function focusInput(_jQobj) {
    _jQobj[0].select();
    //设置为选中样式
    var width = parseFloat(_jQobj.css("width"));
    var height = parseFloat(_jQobj.css("height"));
    var lineheight = parseFloat(_jQobj.css("line-height"));
    var paddingLeft = parseFloat(_jQobj.css("padding-left"));
    _jQobj.css({"width":(width-1)+"px", "height":(height-2)+"px", "border": "2px #ABCDEF solid"});
    if (lineheight) _jQobj.css({"line-height":(lineheight-2)+"px"});
    if (paddingLeft) _jQobj.css({"padding-left":(paddingLeft-1)+"px"});
    //处理maskTitle
    //得到mainAlert
    var mainAlert=_jQobj.parent();
    if (!$(mainAlert).attr("class")||$(mainAlert).attr("class").indexOf("alertInput-Text")!=-1) mainAlert=$(mainAlert).parent();
    mainAlert.find(".maskTitle").hide();
  }
  /**
   * 处理tab移入移出时对input的mask处理
   * @param _jQobj：jq对象
   */
  function blurInput(_jQobj) {
    //设置为选中样式
    var width = parseFloat(_jQobj.css("width"));
    var height = parseFloat(_jQobj.css("height"));
    var lineheight = parseFloat(_jQobj.css("line-height"));
    var paddingLeft = parseFloat(_jQobj.css("padding-left"));
    _jQobj.css({"width":(width+1)+"px", "height":(height+2)+"px", "border": "1px #ABADB3 solid"});
    if (lineheight) _jQobj.css({"line-height":(lineheight+2)+"px"});
    if (paddingLeft) _jQobj.css({"padding-left":(paddingLeft+1)+"px"});
    //处理maskTitle
    //得到mainAlert
    var mainAlert=_jQobj.parent();
    if (!$(mainAlert).attr("class")||$(mainAlert).attr("class").indexOf("alertInput-Text")!=-1) mainAlert=$(mainAlert).parent();
    if(_jQobj.val()) mainAlert.find(".maskTitle").hide();
    else mainAlert.find(".maskTitle").show();
  }
}

/**
 * 设置按钮效果，鼠标划过
 */
function commitOverOutEffect() {
  $("#commitButton").bind('mouseover',function(){
    $(this).css({"background-color":"#81FC6A"});
    $(this).find("span").css("color", "yellow");
  }).bind('mouseout',function(){
    $(this).css({"background-image":"url(images/bg.png)"});
    $(this).find("span").css("color", "#fff");
  });
}

/**
 * mask效果，鼠标划过
 */
function maskTitleOverOutEffect() {
  $(".maskTitle").bind('mouseover',function(){
    $(this).hide();
  });
}

/**
 * 设置提示图标位置，根据不同的控件
 */
function setCorrectPosition() {
  //1-提示图标
  var imgList = $(".alertImg");
  var i=0, len=imgList.length;
  for (;i<len; i++) {
    var top = $(imgList[i]).parent().offset().top;
    var left = $(imgList[i]).parent().offset().left;
    var pWidth = parseFloat($(imgList[i]).parent().css("width"));
    $(imgList[i]).css("top", (top-(parseFloat($(imgList[i]).css("height"))/2)));
    $(imgList[i]).css("left", (left+pWidth-(parseFloat($(imgList[i]).css("width"))/2)));
  }
  //2-提示文字
  var maskTitleList = $(".maskTitle");
  var i=0, len=maskTitleList.length;
  for (;i<len; i++) {
    var top = $(maskTitleList[i]).parent().offset().top;
    var left = $(maskTitleList[i]).parent().offset().left;
    $(maskTitleList[i]).css("top", top);
    $(maskTitleList[i]).css("left", left);
  }
}

/**
 * 初始化maskTitle
 */
function initMaskTitle() {
  var flagCompList = $(".alertInputComp");
  var i=0, len=flagCompList.length;
  for (;i<len; i++) {
    var _this = flagCompList[i];
    if ($(_this).val()&&$(_this).val().trim()!="") {
      var mainAlert=$(_this).parent();
      if (!$(mainAlert).attr("class")||$(mainAlert).attr("class").indexOf("alertInput-Text")!=-1) mainAlert=$(mainAlert).parent();
      mainAlert.find(".maskTitle").hide();
    }
  }
}
/**
 * 初始化邮箱后缀
 * @param jsonPath json路径
 */
function initMailSuffix(jsonPath) {
  $('#mailSel').combobox({    
    url:jsonPath,   
    valueField:'id',   
    textField:'text',
    height:37,
    width:97,
    onChange:function (index,o) {
      var eleId = 'mail';
      validateMail(eleId,index);
    },
    editable:false
  });
  $(".combo").css('border-color','#ABADB3');
  $(".combo").css('border-left','none');
  $(".panel-header, .panel-body").css('border-color','#ABADB3');
}
/**
 * 初始化遮罩样式
 */
function initMask(){
  $("#mask").css({
    "padding-top": ($(window).height()-95)/3,
    "top": parseInt($("#mainDiv").css("top"))-10,
    "left": parseInt($("#mainDiv").css("left"))-10,
    "width": $(window).width(),
    "height": $(window).height()
  });
}