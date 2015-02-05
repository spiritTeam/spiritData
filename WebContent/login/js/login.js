/**
 * 得到mainPage中打开窗口的winId
 */
function getWinId(mainPage){
  var winId = "";
  if(mainPage.registerWinId!=null&&mainPage.registerWinId!="") winId = mainPage.registerWinId;
  if(mainPage.modifyWinId!=null&&mainPage.modifyWinId!="") winId = mainPage.modifyWinId;
  if(mainPage.loginWinId!=null&&mainPage.loginWinId!="") winId = mainPage.loginWinId;
  return winId;
}
/**
 * 清除mainPage中打开窗口的winId
 */
function cleanWinId(mainPage){
  mainPage.loginWinId = "";
  mainPage.modifyWinId = "";
  mainPage.registerWinId = "";
}
/**
 * 
 */
function inputEffect() {
  //对tab到input处理
  $(".alertInputComp").bind('focus',function(){
    dealInput_Tab($(this),"_focus");
    dealInput_Border($(this),"_focus");
  });$(".alertInputComp").bind('blur',function(){
    dealInput_Tab($(this),"_blur");
    dealInput_Border($(this),"_blur");
  });
  //对鼠标移入移出进行处理
  $(".alertInputComp").bind('mouseover',function(){
    dealInput_Mouse($(this),'_mouseover');
    dealInput_Border($(this),"_mouseover");
  });$(".alertInputComp").bind('mouseout',function(){
    dealInput_Mouse($(this),'_mouseout');
    dealInput_Border($(this),"_mouseout");
  });
  /**
   * 处理tab移入移出时对input的mask处理
   * @param _jQobj：jq对象
   * @param dealType：处理方式？_blur显示,_focus隐藏
   */
  function dealInput_Tab(_jQobj,dealType){
    if(_jQobj){
      //得到mainAlert
      var mainAlert=_jQobj.parent();
      if (!$(mainAlert).attr("class")||$(mainAlert).attr("class").indexOf("alertInput-Text")!=-1) mainAlert=$(mainAlert).parent();
      //分dealType处理
      if(dealType=='_blur'){
        if(_jQobj.val()){
          mainAlert.find(".maskTitle").hide();
        }else mainAlert.find(".maskTitle").show();
      }else{
        mainAlert.find(".maskTitle").hide();
      }
    }
  }
  /**
   * 处理input移入移出时对input的mask处理
   * @param _jQobj：jq对象
   * @param dealType：处理方式？_mouseover隐藏,_mouseout显示，
   */
  function dealInput_Mouse(_jQobj,dealType){
    if(_jQobj){
      var mainAlert=_jQobj.parent();
        if (!$(mainAlert).attr("class")||$(mainAlert).attr("class").indexOf("alertInput-Text")!=-1) mainAlert=$(mainAlert).parent();
      if(dealType=='_mouseover'){
        $(mainAlert).find(".maskTitle").hide();
      }else{
        if (_jQobj.val()) $(mainAlert).find(".maskTitle").hide();
        else $(mainAlert).find(".maskTitle").show();
      }
    }
  }
  /**
   * 处理边框效果
   * 当dealType=_mouseover,_mouseout时，处理mouse移入移出时边框效果
   * 当dealType=_focus,_blur处理tab移入移出边框效果
   * @param _jQobj：jq对象
   * @param dealType：处理方式？_mouseover,_mouseout，_focus,_blur
   */
  function dealInput_Border(_jQobj,dealType){
    var width = parseFloat(_jQobj.css("width"));
    var height = parseFloat(_jQobj.css("height"));
    var lineheight = parseFloat(_jQobj.css("line-height"));
    var paddingLeft = parseFloat(_jQobj.css("padding-left"));
    if(dealType=='_mouseover'||dealType=='_focus'){
      _jQobj.css({"width":(width-1)+"px", "height":(height-2)+"px", "border": "2px #ABCDEF solid"});
      if (lineheight) _jQobj.css({"line-height":(lineheight-2)+"px"});
      if (paddingLeft) _jQobj.css({"padding-left":(paddingLeft-1)+"px"});
    }else{
      _jQobj.css({"width":(width+1)+"px", "height":(height+2)+"px", "border": "1px #ABADB3 solid"});
      if (lineheight) _jQobj.css({"line-height":(lineheight+2)+"px"});
      if (paddingLeft) _jQobj.css({"padding-left":(paddingLeft+1)+"px"});
    }
  }
}

