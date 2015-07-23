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

<script type="text/javascript" src="<%=path%>/resources/plugins/spiritui/jq.spirit.pageFrame.js"></script>
<script type="text/javascript" src="<%=path%>/resources/plugins/spiritui/jq.spirit.tabs.js"></script>
<script type="text/javascript" src="<%=path%>/resources/plugins/spiritui/jq.spirit.simpleWin.js"></script>
<link rel="stylesheet" type="text/css" href="<%=path%>/resources/css/mainPage.css"/>
<link rel="stylesheet" type="text/css" href="css/homepage.css"/>
</head>

<body class="_body">
<!-- 实际功能区中部 -->
<div id="mainSegment" class="div_center">
  <div id="fileIn">
    <div id="dayLogo">
      <img id="mlogo" src="../resources/images/logo/main_logo.png"/>
      <div id="internet" onclick="test()">互联网</div>
      <div id="plus">+</div>
      <div id="descript">无模式·开放型·数据探索平台</div>
    </div>
    <div id="inForm">
      <form method="post" action="<%=path%>/fileUpLoad.do" enctype="multipart/form-data" id="afUpload">
        <input id="upf" name="upf" type=file style="display:yes;" onchange="showFileInfo()"/>
        <div>
          <div id="upIcon" onclick="upIcon_clk();"></div>
          <input id="upfs" name="upfs" type="text" readonly="readonly" onclick="upfs_clk();"/>
          <input id="su" type="button" value="探索一下" onclick="uploadF();"/>
        </div>
      </form>
    </div>
  </div>
  <!-- 等待提示区 -->
  <div id="desc">
    <div id="tabBar">
    </div>
    <div id="tabPanels">
      <div id="versionDesc" class="tabPanel">
<span>目前版本</span>：Ver(0.00…00..0.1.0)版(内部公开测试——0.0.1版)<br/>
<br/>
<span>提供功能：</span>对电子表格数据Excel的无模式分析。并且仅能分析：<br/>
1-对一个Excel中的多个页签进行处理<br/>
2-每个页签中的数据表的表头要尽量简单<br/>
<br/>
内测版本，功能不完善。预理解报告功能，可浏览<a href="#" onclick="showDemo()" style="text-decoration:underline;">报告样例</a>。
      </div>
      <div id="declare" class="tabPanel">
<span>在公开内测阶段，我们将：</span><br/>
1-保证您所上传数据资产的安全性。<br/>
2-对内测中存在的问题进行及时修改。<br/>
3-版本更新时对数据进行的调整，可能导致您的数据资产丢失，请保管好您的数据资产。<br/>
4-提前2天对“版本更新”在此进行公示。<br/>
5-有问题可以通过QQ号1794595752或QQ邮箱1794595752@qq.com与我们联系。<br/>
&nbsp;&nbsp;也可联系团队成员：王晖(13910672205)、原锋(13522876218)<br/>
<br/>
<span>建议：</span><br/>
1-您在登录后使用本网站提供的数据服务，我们会对同一账号的数据资产进行统一管理和分析。<br/>
2-目前只支持对电子表格Excel数据的无模式分析。<br/>
<br/>
      </div>
      <div id="introduce" class="tabPanel">
在全球互联的背景下，围绕数据的“生产、采集整理、交换/交易、数学分析、智能应用”必将形成全新的产业链条，我们将在其中做出自己的贡献。<br/>
<br/>
以数据为核心，努力提供“好用、有用”的互联网数据分析服务是我们现阶段的目标。<br/>
<br/>
感谢团队中的每个人为此做出的创造性的贡献！<br/>
<br/>
<br/>
    <span>北京灵派诺达科技有限公司<span>
      </div>
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
  <div id="icp" class="icp">
  ©2015 灵派诺达 京ICP备15028482号 
  </div>
</div>

<script>
var mainPage;
//提示信息
var _promptMessage="点击选择分析的文件，目前仅支持Excel电子表格文件";
var checkProcessId=-1;
var _suClicked=false;

//主窗口参数
var INIT_PARAM = {
  //页面中所用到的元素的id，只用到三个Div，另，这三个div应在body层
  pageObjs: {
    mainId: "mainSegment"
  },
  page_width: 0,
  page_height: 0,

//  foot_height: 30, //脚部高度
//  foot_peg: false, //是否钉住脚部在底端。false：脚部随垂直滚动条移动(浮动)；true：脚部钉在底端

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
  $("#internet").css({"left":"65px", "top":"50px"});
  $("#plus").css({"left":"270px", "top":"65px"});
  $("#descript").css({"left":"-25px", "top":"126px"});
  $("#plugs").css({"left":"-1px", "top":"126px"});
  left = (parseFloat($("#mainSegment").width())-parseFloat($("#waittingArea").width()))/2;
  $("#waittingArea").css({"left": left});
  left = (parseFloat($("#mainSegment").width())-parseFloat($("#tabBar").width()))/2;
  $("#desc").css({"left": left, "top":$("#inForm").offset().top+50, "position":"absolute"});
  $("#tabPanels").css({"top":$("#tabBar").css("height")});
  var top2=$("#desc").offset().top+230;
  $("#icp").css({"left":left, "top":top2});
}
//当界面尺寸改变
function myResize() {
  if (INIT_PARAM.page_width<=0) {
    //控制中心区域图片
    var left=(parseFloat($("#mainSegment").width())-parseFloat($("#fileIn").width()))/2;
    $("#fileIn").css({"left": left});
    left = (parseFloat($("#mainSegment").width())-parseFloat($("#waittingArea").width()))/2;
    $("#waittingArea").css({"left": left});
    left = (parseFloat($("#mainSegment").width())-parseFloat($("#tabBar").width()))/2;
    $("#desc").css({"left": left});
    $("#icp").css({"left":left});
  }
}

//主函数
$(function() {
  mainPage = getMainPage();
  var initStr = $.spiritPageFrame(INIT_PARAM);
  if (initStr) {
  	showAlert("页面初始化失败", initStr, "error");
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
  //介绍区域tab控制
  $("#tabBar").spiritTabs({id:"1"
    , tabs:[
      {title:"版本及功能", onClick:"changeTab(1)"}
     ,{title:"声明", onClick:"changeTab(2)"}
     ,{title:"介绍", onClick:"changeTab(3)"}
    ]
  });
  $("#tabBar>div").first().click();
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
  //校验文件格式
  var fileName=$("#upfs").val();
  var _pos=fileName.lastIndexOf('.');
  if (_pos==-1) showAlert("数据上传", "抱歉！目前系统不支持对此格式文件的数据处理。", "warning");
  var _ext = fileName.substr(_pos);
  if (_ext.toUpperCase()!=".XLS"&&_ext.toUpperCase()!=".XLSX") {
    showAlert("数据上传", "抱歉！目前系统不支持对此格式文件的数据处理。", "warning");
    return;
  }
  try {
    var form = $('#afUpload');
    if (mainPage) mainPage.showMask(1, "正在上传文件，进行即时分析。<br/>请等待...");

    $(form).attr('action', _PATH+'/fileUpLoad.do');
    $(form).attr('method', 'POST');
    $(form).attr('target', 'tframe');
    if (form.encoding) form.encoding = 'multipart/form-data';
    else form.enctype = 'multipart/form-data';
    $(form).form('submit',{
      async: true,
      success: function(respStr) {
        var respJson = null;
        try {
          respJson=str2JsonObj(respStr);
        } catch(e) {
          showAlert("上传异常", e.message+"<br/>返回数据为="+respStr,"error");
        }
        var success=(respJson.jsonType==1&&respJson.data&&(respJson.data.length==1&&respJson.data[0].success));
        if (success+""=="TRUE") {
          $("#upfs").val(_promptMessage);
          if (mainPage) {
            mainPage.getNoVisitReports();//重新获取未ert读
            mainPage.setAfterFirstUpload();
          }
        } else {
          var msg = "";
          if (respJson.data) {
            msg=allFields(respJson.data[0]);
          } else {
            if (respJson.message instanceof string) msg=respJson.message;
            else msg=allFields(respJson.message[0]);
          }
          if (!msg) msg="未知问题";
          showAlert("数据上传", "数据文件上传失败！<br/>"+msg, "error");
        }
        mainPage.showMask(0);
      },
      error: function(errData) {
        showAlert("上传失败", errData, "error");
        mainPage.showMask(0);
      }
    });
  } catch(e) {
  	showAlert("文件上传失败", e.message, "error");
  }
}

function changeTab(tabIndex) {
  $("#tabBar>div").each(function(i) {
  	$(this).css({"height":"30px"});
    $(this).css({"padding-top":"0px"});
  	if (i==tabIndex-1) {
      $(this).css({"height":"28px"});
      $(this).css({"text-height":"28px"});
  	}
  });
  $(".tabPanel").hide();
  if (tabIndex==1) $("#versionDesc").show();
  else if (tabIndex==2) $("#declare").show();
  else if (tabIndex==3) $("#introduce").show();
}
function showResult() {
  openSWinInMain({"title":"分析结果", "url":"demo/Rd/resultRd.jsp", "width":1000, "height":600, "iframeScroll":"yes"});
}
function showDemo() {
  openSWinInMain({"title":"分析报告Demo", "url":"demo/Rd/resultRdEchart.jsp", "width":1000, "height":600, "iframeScroll":"yes"});
}
</script>
</body>
</html>