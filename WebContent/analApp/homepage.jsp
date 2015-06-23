<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="com.spiritdata.framework.FConstants"%>
<%@page import="com.spiritdata.dataanal.UGA.pojo.User"%>
<%
  String path = request.getContextPath();
%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="cache-control" content="no-cache"/>
<meta http-equiv="pragma" content="no-cache"/>
<meta http-equiv="expires" content="0"/>
<jsp:include page="/common/sysInclude.jsp" flush="true"/>

<link rel="stylesheet" type="text/css" href="<%=path%>/resources/css/mainPage.css"/>
<script type="text/javascript" src="<%=path%>/resources/plugins/spiritui/jq.spirit.pageFrame.js"></script>
<link rel="stylesheet" type="text/css" href="<%=path%>/login/css/login.css">
<style>
#fileIn {
  position:absolute;
  width:650px;
  height:175px;
  top:50px;
}
#dayLogo {
  position:absolute;
  width:450px;
  height:160px;
}
#mlogo {
  float:right;
  max-height:158px;
  height:expression(this.width>184?this.height=this.height*184/this.width:auto);
  width:expression(this.width>184?184:auto);
  max-width:184px; 
}
#internet, #plus {
  position:absolute;
  font-size:70px;
  font-weight:900;
  color:#CC3333;
  font-style:oblique;
  font-family:'黑体';
}
#descript {
  position:absolute;
  font-size:24px;
  font-weight:bold;
  font-family:'黑体';
  color:#055A2E;
}
#inForm {
  position:absolute;
  padding:0px;
  margin:0px;
  width:640px;
  height:36px;
  top:165px;
}
#upf {display:none;}
#upIcon {
  position:absolute;
  width:25px;
  height:25px;
  border:1px solid blue;
  top:4px;
  left:9px;
  background-image:url(../resources/images/uploadIcon.gif)
}
#su {
  width:100px;
  height:35px;
  display:inline-block;
  border:1px solid #067239;
  border-left:0px;
  border-radius:0 3px 3px 0;
  background-image:-webkit-linear-gradient(bottom, #64CD4F 31%, #43D454);
  background-color:#399D27;
  font:15px 宋体 Tahoma, Helvetica, Arial, 'Microsoft YaHei', sans-serif;
  color:#fff;
  margin-left:-7px;
}
#upfs {
  width:495px;
  height:33px;
  display:inline-block;
  border:1px solid #067239;
  border-radius:3px 0 0 3px;
  padding-left:35px;
  font:12px 宋体 Tahoma, Helvetica, Arial, 'Microsoft YaHei', sans-serif;
  color:#bfbfbf;
}

/*休息等待区*/
#waittingArea {
  position:absolute;
  border:1px solid #BCCBDC;
  width:505px;
  height:300px;
  top:230px;
  display:none;
}
#ppbar {
  position:absolute;
  width:505px;
  height:35px;
}
#pp {
  position:absolute;
  width:435px;
  top:6px;
  left:20px;
}
#logshow {
  position:absolute;
  border-top:1px solid #BCCBDC;
  width:505px;
  height:264px;
  top:35px;
  overflow-y:auto;
}
#showResult {
  position:absolute;
  width:25px;
  height:25px;
  border:1px solid blue;
  border-radius:3px;
  top:4px;
  left:473px;
  background-image:url(resources/images/uploadIcon.gif)
}

.div_center{margin:0 auto;text-align:center;}
.div_inline{ display:inline} 
</style>
</head>

<body class="_body">
<!-- 实际功能区中部 -->
<div id="mainSegment" class="div_center">
  <div id="fileIn">
    <div id="dayLogo">
      <img id="mlogo" src="../resources/images/logo/main_logo.png"/>
      <div id="internet">互联网</div>
      <div id="plus">+</div>
      <div id="descript">无模式开放数据分析平台</div>
    </div>
    <div id="inForm">
      <form method="post" action="/sa/fileUpLoad.do" enctype="multipart/form-data" id="afUpload" target="tframe">        
        <input id="upf" name="upf" type=file style="display:yes;" onchange="showFileInfo()"/>        
        <div>
          <div id="upIcon" onclick="upIcon_clk();"></div>
          <input id="upfs" name="upfs" type="text" readonly="readonly" onclick="upfs_clk();"/>        
          <input id="su" type="button" value="分析一下" onclick="uploadF();"/>
        </div>
      </form>
    </div>
  </div>
  <!-- 等待提示区 -->
  <div id="waittingArea">
    <div id="ppbar">
      <div id="pp"></div>
      <div id="showResult" onclick="showResult();"></div>
    </div>
    <div id="logshow">
    </div>
  </div>
</div>
<iframe id="tframe" name="tframe" style="width:600px;heigth:200px;display:none;"></iframe>
<script>
var mainPage;
//提示信息
var _promptMessage="点击选择分析的文件";
var analysizeing=false;
var _suClicked=false;

//主窗口参数
var INIT_PARAM = {
  //页面中所用到的元素的id，只用到三个Div，另，这三个div应在body层
  pageObjs: {
    mainId: "mainSegment" //主体Id
  },
  page_width: 0,
  page_height: 0,

  top_height: 0, //顶部高度
  top_peg: false,
  top_shadow_color:"#1AE517",//颜色

  foot_height: 0, //脚部高度
  foot_peg: false, //是否钉住脚部在底端。false：脚部随垂直滚动条移动(浮动)；true：脚部钉在底端

  win_min_width: -1, //页面最小的高度。当窗口高度小于这个值，不对界面位置及尺寸进行调整。主体部分宽度也照此设置
  win_min_height: -1, //页面最小的高度。当窗口高度小于这个值，不对界面位置及尺寸进行调整。主体部分高度也照此设置
  myInit: initPosition,
  myResize: myResize
};
//初始化界面
function initPosition() {//注意，不要在此设置topSegment/mainSegment/footSegment等框架元素的宽高等，否则，页面不会自动进行调整
  //控制中心区域图片
  var left=(parseFloat($("#mainSegment").width())-parseFloat($("#fileIn").width()))/2;
  $("#fileIn").css({"left": left});
  left = (parseFloat($("#fileIn").width())-parseFloat($("#dayLogo").width()))/2;
  $("#dayLogo").css({"left": left});
  left = (parseFloat($("#fileIn").width())-parseFloat($("#inForm").width()))/2;
  $("#inForm").css({"left": left});
  $("#internet").css({"left":"20px", "top":"50px"});
  $("#plus").css({"left":"228px", "top":"65px"});
  $("#descript").css({"left":"-1px", "top":"126px"});
  $("#plugs").css({"left":"-1px", "top":"126px"});
  left = (parseFloat($("#mainSegment").width())-parseFloat($("#waittingArea").width()))/2;
  $("#waittingArea").css({"left": left});
}
//当界面尺寸改变
function myResize() {
  if (INIT_PARAM.page_width==0) {
    //控制中心区域图片
    var left=(parseFloat($("#mainSegment").width())-parseFloat($("#fileIn").width()))/2;
    $("#fileIn").css({"left": left});
    left = (parseFloat($("#mainSegment").width())-parseFloat($("#waittingArea").width()))/2;
    $("#waittingArea").css({"left": left});
  }
}

//主函数
$(function() {
  mainPage = getMainPage();

  var initStr = $.spiritPageFrame(INIT_PARAM);
  if (initStr) {
  	if (mainPage) mainPage.$.messager.alert("页面初始化失败", initStr, "error");
  	else $.messager.alert("页面初始化失败", initStr, "error");
    return ;
  };
  $("#upfs").val(_promptMessage);
  $("#su").mouseover(function(){
    if ($("#upfs").val()!=_promptMessage) {
      $(this).attr("title", "");
      $(this).css({"color":"yellow", "background-color":"#81FC6A"});
    } else {
      $(this).attr("title", "请先上传文件");
    }
  }).mouseout(function(){
    $(this).css({"color":"white", "background-color":"#36B148"});
  });
});

//点击大的输入框
function upfs_clk() {
  _suClicked=false;
  if ($("#upfs").val()==_promptMessage) $("#upf").click();
};
//点击上传按钮
function upIcon_clk() {
  $("#upf").click();
};
//显示选择的文件名称
function showFileInfo() {
  $("#upfs").val($("#upf").val());
  if (_suClicked) uploadF();
};
//文件上传
function uploadF() {
  if ($("#upfs").val()==_promptMessage) {
    _suClicked=true;
    $("#upf").click();
    return;
  }
  try {
    var form = $('#afUpload');
    $(form).attr('action', _PATH+'/fileUpLoad.do');
    $(form).attr('method', 'POST');
    $(form).attr('target', 'tframe');
    if (form.encoding) form.encoding = 'multipart/form-data';
    else form.enctype = 'multipart/form-data';
    $(form).submit();
  } catch(e) {
  	if (mainPage) mainPage.$.messager.alert("文件上传失败", e, "error");
  	else $.messager.alert("文件上传失败", e, "error");
  }
}

function showResult() {
  openSWin({"title":"分析结果", "url":"demo/Rd/resultRd.jsp", "width":1000, "height":600, modal:true});
}
</script>
</body>
</html>