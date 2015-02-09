
/**
 * 得到提示输入控件的主容器
 * @param _jQobj 提示控件中的某个控件
 * @returns 主容器
 */
function getMainAlert(_jQobj) {
  var mainAlert=_jQobj;
  var i=10;
  while (((i--)>0)
    &&(!$(mainAlert).attr("class")||$(mainAlert).attr("class").indexOf("alertInput-")==-1)) {
    mainAlert=$(mainAlert).parent(); //10次结束
  }
  if ($(mainAlert).attr("class").indexOf("alertInput-")!=-1) return  $(mainAlert);
  else return null;
}

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
    //得到主容器
    var mainAlert=getMainAlert(_jQobj);
    if (mainAlert) {
      _jQobj[0].select();
      //设置为选中样式
      var width = parseFloat(mainAlert.css("width"))-2;
      var height = parseFloat(mainAlert.css("height"));
      var lineheight = mainAlert.css("line-height")?parseFloat(mainAlert.css("line-height")):null;
      var paddingLeft = mainAlert.css("padding-left")?parseFloat(mainAlert.css("padding-left")):null;
      if (mainAlert.attr("class").indexOf("alertInput-mail")!=-1) {
        width -= parseFloat(mainAlert.find("#mailSuffix").css("width"))+2;
      } else if (mainAlert.attr("class").indexOf("alertInput-vCode")!=-1) {
        width -= parseFloat(mainAlert.find("#vCodeImg").css("width"))+4;
      }
      _jQobj.css({"width":(width-2)+"px", "height":(height-2)+"px", "border": "2px #ABCDEF solid"});
      if (lineheight) _jQobj.css({"line-height":(lineheight-2)+"px"});
      if (paddingLeft) _jQobj.css({"padding-left":(paddingLeft-1)+"px"});
      //处理maskTitle
      mainAlert.find(".maskTitle").hide();
    }
  }
  /**
   * 处理tab移入移出时对input的mask处理
   * @param _jQobj：jq对象
   */
  function blurInput(_jQobj) {
    //得到主容器
    var mainAlert=getMainAlert(_jQobj);
    if (mainAlert) {
      //设置为选中样式
      var width = parseFloat(mainAlert.css("width"))-2;
      var height = parseFloat(mainAlert.css("height"));
      var lineheight = mainAlert.css("line-height")?parseFloat(mainAlert.css("line-height")):null;
      var paddingLeft = mainAlert.css("padding-left")?parseFloat(mainAlert.css("padding-left")):null;
      if (mainAlert.attr("class").indexOf("alertInput-mail")!=-1) {
        width -= parseFloat(mainAlert.find("#mailSuffix").css("width"))+2;
      } else if (mainAlert.attr("class").indexOf("alertInput-vCode")!=-1) {
        width -= parseFloat(mainAlert.find("#vCodeImg").css("width"))+4;
      }
      _jQobj.css({"width":width+"px", "height":height+"px", "border": "1px #ABADB3 solid"});
      if (lineheight) _jQobj.css({"line-height":lineheight+"px"});
      if (paddingLeft) _jQobj.css({"padding-left":paddingLeft+"px"});
      //处理maskTitle
      if (_jQobj.val()) mainAlert.find(".maskTitle").hide();
      else mainAlert.find(".maskTitle").show();
    }
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