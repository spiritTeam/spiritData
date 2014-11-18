<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%
  String path = request.getContextPath();
  String sid = request.getSession().getId();
%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="cache-control" content="no-cache"/>
<meta http-equiv="pragma" content="no-cache"/>
<meta http-equiv="expires" content="0"/>
<jsp:include page="/common/sysInclude.jsp" flush="true"/>

<script type="text/javascript" src="<%=path%>/resources/plugins/spiritui/jq.spirit.utils.js"></script>
<script type="text/javascript" src="<%=path%>/resources/plugins/spiritui/jq.spirit.pageFrame.js"></script>
<script type="text/javascript" src="<%=path%>/resources/plugins/spiritui/jq.spirit.tabs.js"></script>

<script type="text/javascript" src="<%=path%>/resources/js/mainPage.utils.js"></script>

<link rel="stylesheet" type="text/css" href="<%=path%>/resources/css/mainPage.css"/>
<link rel="stylesheet" type="text/css" href="<%=path%>/resources/plugins/spiritui/themes/default/all.css"/>
<style>
#fileIn {
  position:absolute;
  width:650px;
  height:175px;
  top:50px;
}
#dayLogo {
  position:absolute;
  border:1px solid blue;
  width:400px;
  height:130px;
}
#upf{
  width:523px;
  height:22px;
  font:16px\/22px arial;
  margin:5px 0 0 7px;
  padding:0;
  background:#fff;
  border:0;
  outline:0;
  -webkit-appearance:none;
}
#inForm {
  position:absolute;
  padding:0px;
  margin:0px;
  width:640px;
  height:36px;
  top:135px;
}
#upIcon {
  position:absolute;
  width:25px;
  height:25px;
  border:1px solid blue;
  top:4px;
  left:7px;
  background-image:url(resources/images/uploadIcon.gif)
}
#su {
  width:100px;
  height:35px;
  display:inline-block;
  border:1px solid #36B148;
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
  border:1px solid #36B148;
  border-radius:3px 0 0 3px;
  padding-left:35px;
  font:12px 宋体 Tahoma, Helvetica, Arial, 'Microsoft YaHei', sans-serif;
  color:#bfbfbf;
}
#mask {
  z-index: 1000;
  top:52px;
  left:170px;
  width:400px;
  height:130px;
  border:1px solid red;
}

/*休息等待区*/
#waittingArea {
  position:absolute;
  border:1px solid #BCCBDC;
  width:505px;
  height:305px;
  top:230px;
//  background-image:url(resources/images/waitting.gif);
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
  height:269px;
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
</style>
</head>

<body class="_body">
<!-- 遮罩层 -->
<div id="mask" style="display:none; position:absolute;vertical-align:middle;text-align:center; align:center;">
  <div style="background-image:resources/images/waiting_circle.gif; width:85px; height:81px;"></div>
  <span style="font-weight:bold;" id="maskTitle">正在分析，请稍候...</span>
</div>

<!-- 头部:悬浮 -->
<div id="topSegment">
  <div style="float:right;">
	  <!-- <label for="loginName"><%=sid %>||登录名：</label><input type="text" id='loginName' tabindex="1"/><label for="password">密　码：</label><input type="password" id='password' tabindex="2"/><div id="commitButton" style="height:16px; width:16px; background-color:green;"></div> -->
	  <div ><%=sid %>||<span onclick="login();">登录</span><input value="注销" type="button" onclick="logout();"><input value="修改密码" type="button" onclick="modPwd();"></div>
  </div>
</div>

<!-- 脚部:悬浮 -->
<div id="footSegment"></div>

<!-- 实际功能区中部 -->
<div id="mainSegment">
  <div id="fileIn">
    <div id="dayLogo"></div>
    <div id="inForm"><form method="post" action="/sa/fileUpLoad.do" enctype="multipart/form-data" id="afUpload" target="tframe">
      <input id="upf" name="upf" type=file style="display:none;" onchange="showFileInfo()"/>
      <div id="upIcon" onclick="upIcon_clk();"></div>
      <input id="upfs" name="upfs" type="text" readonly="readonly" onclick="upfs_clk();"/>
      <input id="su" type="button" value="分析一下" onclick="uploadF();"/>
    </form></div>
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
<iframe id="tframe" name="tframe" bordercolor=red frameborder="yes" border=1 width="600" height="200" style="width:600px;heigth:200px; boder:1px solid red;display:none;"></iframe>

<script>
//提示信息
var _promptMessage="点击选择分析的文件";
var analysizeing=false;
var _suClicked=false;

//主窗口参数
var INIT_PARAM = {
  //页面中所用到的元素的id，只用到三个Div，另，这三个div应在body层
  pageObjs: {
    topId: "topSegment", //头部Id
    mainId: "mainSegment", //主体Id
    footId: "footSegment" //尾部Id
  },
  page_width: 0,
  page_height: 0,

  top_height: 30, //顶部高度
  top_peg: true,

  foot_height: 75, //脚部高度
  foot_peg: false, //是否钉住脚部在底端。false：脚部随垂直滚动条移动(浮动)；true：脚部钉在底端

  win_min_width: 800, //页面最小的高度。当窗口高度小于这个值，不对界面位置及尺寸进行调整。主体部分宽度也照此设置
  win_min_height: 580, //页面最小的高度。当窗口高度小于这个值，不对界面位置及尺寸进行调整。主体部分高度也照此设置
  myInit: initPosition,
  myResize: myResize
};

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
    //演示
    {
      analysizeing=true;//开始分析
      $("#waittingArea").fadeIn(200);//等待提示区
      showDemo();
    }
  } catch(e) {
    $.messager.alert("文件上传失败", e, "error");
  }
}
//登陆
function loginF() {
  var url="<%=path%>/login.do";
  var pData={
    "loginName":$("#loginName").val(),
    "password":$("#password").val(),
    "browser":getBrowserVersion()
  };
  $.ajax({type:"post", async:false, url:url, data:pData, dataType:"json",
    success: function(json) {
      if (json.type==1) {
        alert("loginOk");
        return;
      } else if (json.type==2) {
        $.messager.alert("错误", "登录失败："+json.data, "error", function(){
          $("#loginname").focus();
          $("#mask").hide();
          setBodyEnter(true);
        });
      } else {
        $.messager.alert("错误", "登录异常："+json.data, "error", function(){
          $("#loginname").focus();
          $("#mask").hide();
          setBodyEnter(true);
        });
      }
    },
    error: function(errorData) {
      if (errorData) {
        $.messager.alert("错误", "登录异常：未知！", "error", function(){
          $("#loginname").focus();
          $("#mask").hide();
          setBodyEnter(true);
        });
      } else {
        $("#mask").hide();
        setBodyEnter(true);
      }
    }
  });
}
//主函数
$(function() {
  $("#commitButton")
  .click(function(){
    loginF();
  });

  var initStr = $().spiritPageFrame(INIT_PARAM);
  if (initStr) {
    $.messager.alert("页面初始化失败", initStr, "error");
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

//初始化界面
function initPosition() {//注意，不要在此设置topSegment/mainSegment/footSegment等框架元素的宽高等，否则，页面不会自动进行调整
  //控制中心区域图片
  var left=(parseFloat($("#mainSegment").width())-parseFloat($("#fileIn").width()))/2;
  $("#fileIn").css({"left": left});
  left = (parseFloat($("#fileIn").width())-parseFloat($("#dayLogo").width()))/2;
  $("#dayLogo").css({"left": left});
  left = (parseFloat($("#fileIn").width())-parseFloat($("#inForm").width()))/2;
  $("#inForm").css({"left": left});
  //遮罩层
//  left = (parseFloat($("#mainSegment").width())-parseFloat($("#mask").width()))/2;
//  $("#mask").css({"left": left+2});
  //等待提示区
  left = (parseFloat($("#mainSegment").width())-parseFloat($("#waittingArea").width()))/2;
  $("#waittingArea").css({"left": left});
};
//当界面尺寸改变
function myResize() {
  if (INIT_PARAM.page_width==0) {
    //控制中心区域图片
    var left=(parseFloat($("#mainSegment").width())-parseFloat($("#fileIn").width()))/2;
    $("#fileIn").css({"left": left});
    //遮罩层
//    left = (parseFloat($("#mainSegment").width())-parseFloat($("#mask").width()))/2;
//    $("#mask").css({"left": left+2});
    //等待提示区
    left = (parseFloat($("#mainSegment").width())-parseFloat($("#waittingArea").width()))/2;
    $("#waittingArea").css({"left": left});
  }
};

//demo
function showDemo() {
  $("#pp").progressbar();

  var time=new Date();
  var time1=time, time2=time;
  var logInfo="";
  var value = $("#pp").progressbar("getValue");
  var i=0;

  //上传
  var stepStr = "上传文件...";
  logInfo += "<p>"+time.Format("yyyy-MM-dd hh:mm:ss.S")+"\t[001文件上传] 开始上传";
  $("#logshow").html(logInfo);
  uploadFile();

  function uploadFile() {
    value = $("#pp").progressbar("getValue");
    if (value<100){
      value += Math.floor(Math.random() * 10);
      if (value>100) value=100;
      $("#pp").progressbar({"text": stepStr+"("+value+"%)", "value":value});
      setTimeout(arguments.callee, 100);
    } else {
      time2=new Date();
      logInfo += "<p>"+time2.Format("yyyy-MM-dd hh:mm:ss.S")+"\t[001文件上传] 文件上传成功，用时"+(time2-time1)+"毫秒";
      $("#logshow").html(logInfo);
      $("#pp").progressbar({"text": stepStr+"("+value+"%)", "value":0});

      i=0;
      stepStr="分析元数据结构...";
      analysisStructure();
    }
  };

  //结构分析
  var mdSys =['有3个页签(sheet)可供分析'
    ,'页签"人员"(sheet1)元数据分析...'
    ,'页签"人员"(sheet1)元数据分析完成，符合导入标准，已匹配现有元数据'
    ,'页签"案件"(sheet2)元数据分析...'
    ,'页签"案件"(sheet2)元数据分析完成，符合导入标准，不匹配现有元数据'
    ,'页签"统计"(sheet3)元数据分析...'
    ,'页签"统计"(sheet3)元数据分析完成，不符合导入标准，此页签信息无法导入'
  ];
  function analysisStructure() {
    value = $("#pp").progressbar("getValue");
    if (value==0) {
      time2=new Date();
      time1=time2;
      logInfo += "<p>"+time2.Format("yyyy-MM-dd hh:mm:ss.S")+"\t[002元数据结构分析] 数据为电子表格(excel)文档";
      $("#logshow").html(logInfo);
      time2=new Date();
      logInfo += "<p>"+time2.Format("yyyy-MM-dd hh:mm:ss.S")+"\t[002元数据结构分析] 分析元数据结构";
      $("#logshow").html(logInfo);

      value += Math.floor(Math.random()*10);
      if (value>100) value=100;
      $("#pp").progressbar("setValue", value);

      setTimeout(arguments.callee, 100);
    } else if (value<100){
      value += Math.floor(Math.random()*10);
      $("#pp").progressbar({"text": stepStr+"("+value+"%)", "value":value});
      if (i<=6) {
        logInfo += "<p>"+time2.Format("yyyy-MM-dd hh:mm:ss.S")+"\t[002元数据结构分析] "+mdSys[i++];
        $("#logshow").html(logInfo);
      }
      if (value>100) value=100;
      $("#pp").progressbar({"text": stepStr+"("+value+"%)", "value":value});
      setTimeout(arguments.callee, 100);
    } else {
      if (i<6) {
        for (;i<6;i++) {
          logInfo += "<p>"+time2.Format("yyyy-MM-dd hh:mm:ss.S")+"\t[002元数据结构分析] "+mdSys[i++];
          $("#logshow").html(logInfo);
        }
      }
      time2=new Date();
      logInfo += "<p>"+time2.Format("yyyy-MM-dd hh:mm:ss.S")+"\t[002元数据结构分析] 元数据分析成功，用时"+(time2-time1)+"毫秒";
      $("#logshow").html(logInfo);
      $("#pp").progressbar({"text": stepStr+"("+value+"%)", "value":0});

      i=0;
      stepStr="数据导入...";
      importData();
    }
  }

  //结构分析
  var imSys =[
    '导入"人员"(sheet1)数据...'
    ,'导入"人员"(sheet1)数据成功'
    ,'导入"案件"(sheet2)数据...'
    ,'导入"案件"(sheet2)数据成功'
  ];
  function importData() {
    value = $("#pp").progressbar("getValue");
    if (value==0) {
      time2=new Date();
      time1=time2;
      logInfo += "<p>"+time2.Format("yyyy-MM-dd hh:mm:ss.S")+"\t[003数据导入] 开始导入数据";
      $("#logshow").html(logInfo);

      value += Math.floor(Math.random()*10);
      if (value>100) value=100;
      $("#pp").progressbar("setValue", value);

      setTimeout(arguments.callee, 100);
    } else if (value<100){
      value += Math.floor(Math.random()*10);
      $("#pp").progressbar({"text": stepStr+"("+value+"%)", "value":value});
      if (i<=3) {
        logInfo += "<p>"+time2.Format("yyyy-MM-dd hh:mm:ss.S")+"\t[003数据导入] "+imSys[i++];
        $("#logshow").html(logInfo);
      }
      if (value>100) value=100;
      $("#pp").progressbar({"text": stepStr+"("+value+"%)", "value":value});

      setTimeout(arguments.callee, 100);
    } else {
      if (i<3) {
        for (;i<3;i++) {
          logInfo += "<p>"+time2.Format("yyyy-MM-dd hh:mm:ss.S")+"\t[003数据导入] "+imSys[i++];
          $("#logshow").html(logInfo);
        }
      }
      time2=new Date();
      logInfo += "<p>"+time2.Format("yyyy-MM-dd hh:mm:ss.S")+"\t[003数据导入] 数据导入成功，用时"+(time2-time1)+"毫秒";
      $("#logshow").html(logInfo);
      $("#pp").progressbar({"text": stepStr+"("+value+"%)", "value":0});

      i=0;
      stepStr="分析元数据语义...";
      analysisContent();
    }
  }

  //语义分析
  var dcSys =[
    '分析"人员"(sheet1)元数据语义...'
    ,'"人员"(sheet1)元数据语义分析完成，列(SFZ)为身份证，列(JG)为字典项'
    ,'分析"案件"(sheet2)元数据语义...'
    ,'"案件"(sheet2)元数据语义分析完成，列(SFZ)为身份证，列(AJLX)为字典项'
  ];
  function analysisContent() {
    value = $("#pp").progressbar("getValue");
    if (value==0) {
      time2=new Date();
      time1=time2;
      logInfo += "<p>"+time2.Format("yyyy-MM-dd hh:mm:ss.S")+"\t[004元数据语义分析] 开始分析元数据语义";
      $("#logshow").html(logInfo);

      value += Math.floor(Math.random()*10);
      if (value>100) value=100;
      $("#pp").progressbar("setValue", value);

      setTimeout(arguments.callee, 100);
    } else if (value<100){
      value += Math.floor(Math.random()*10);
      $("#pp").progressbar({"text": stepStr+"("+value+"%)", "value":value});
      if (i<=3) {
        logInfo += "<p>"+time2.Format("yyyy-MM-dd hh:mm:ss.S")+"\t[004元数据语义分析] "+dcSys[i++];
        $("#logshow").html(logInfo);
      }
      if (value>100) value=100;
      $("#pp").progressbar({"text": stepStr+"("+value+"%)", "value":value});

      setTimeout(arguments.callee, 100);
    } else {
      if (i<3) {
        for (;i<3;i++) {
          logInfo += "<p>"+time2.Format("yyyy-MM-dd hh:mm:ss.S")+"\t[004元数据语义分析] "+dcSys[i++];
          $("#logshow").html(logInfo);
        }
      }
      time2=new Date();
      logInfo += "<p>"+time2.Format("yyyy-MM-dd hh:mm:ss.S")+"\t[004元数据语义分析] 元数据语义分析成功，用时"+(time2-time1)+"毫秒";
      $("#logshow").html(logInfo);
      $("#pp").progressbar({"text": stepStr+"("+value+"%)", "value":0});

      i=0;
      stepStr="单项数据分析...";
      singleAnalysis();
    }
  }

  function singleAnalysis() {
    value = $("#pp").progressbar("getValue");
    if (value==0) {
      time2=new Date();
      time1=time2;
      logInfo += "<p>"+time2.Format("yyyy-MM-dd hh:mm:ss.S")+"\t[005单项数据分析] 开始单项数据分析";
      $("#logshow").html(logInfo);

      value += Math.floor(Math.random()*10);
      if (value>100) value=100;
      $("#pp").progressbar("setValue", value);

      setTimeout(arguments.callee, 100);
    } else if (value<100){
      value += Math.floor(Math.random()*10);
      $("#pp").progressbar({"text": stepStr+"("+value+"%)", "value":value});
//      if (i<=3) {
//        logInfo += "<p>"+time2.Format("yyyy-MM-dd hh:mm:ss.S")+"\t[004元数据语义分析] "+dcSys[i++];
//        $("#logshow").html(logInfo);
//      }
      if (value>100) value=100;
      $("#pp").progressbar({"text": stepStr+"("+value+"%)", "value":value});

      setTimeout(arguments.callee, 100);
    } else {
//      if (i<3) {
//        for (;i<3;i++) {
//          logInfo += "<p>"+time2.Format("yyyy-MM-dd hh:mm:ss.S")+"\t[004元数据语义分析] "+dcSys[i++];
//          $("#logshow").html(logInfo);
//        }
//      }
      time2=new Date();
      logInfo += "<p>"+time2.Format("yyyy-MM-dd hh:mm:ss.S")+"\t[005单项数据分析] 单项数据分析成功，用时"+(time2-time1)+"毫秒";
      $("#logshow").html(logInfo);
      //$("#pp").progressbar({"text": stepStr+"("+value+"%)", "value":0});

      i=0;
      stepStr="...";
      //singleAnalysis();
     // showResult();
    }
  }
}

function showResult() {
  //openSWin({"title":"分析结果", "url":"demo/Rd/resultRd.jsp", "width":1000, "height":600, modal:true});
}

function onlyLogout(ip, mac, browser) {
  var msg = "您已经在["+ip+"("+mac+")]客户端用["+browser+"]浏览器重新登录了，当前登录失效！";
  if ((!ip&&!mac)||(ip+mac=="")) msg = "您已经在另一客户端用["+browser+"]浏览器重新登录了，当前登录失效！";
  $.messager.alert("提示", msg+"<br/>现返回登录页面。", "info", function(){ logout(); });
}
/**
 * 注销
 */
function logout() {
  var url=_PATH+"/logout.do";
  $.ajax({type:"post", async:true, url:url, data:null, dataType:"json",
    success: function(json) {
      if (json.type==1) {
    	  $.messager.alert("注销信息","注销成功!",'info',function(){
    		  window.location.href="<%=path%>/login/login.jsp?noAuth";
    	  });
      } else {  
        $.messager.alert("错误", "注销失败："+json.data+"！</br>返回登录页面。", "error", function(){
          window.location.href="<%=path%>/login/login.jsp?noAuth";
        });
      }
    },
    error: function(errorData) {
      $.messager.alert("错误", "注销失败：</br>"+(errorData?errorData.responseText:"")+"！<br/>返回登录页面。", "error", function(){
        window.location.href="<%=path%>/login/login.jsp?noAuth";
      });
    }
  });
};
function modPwd(){
	openSWin({"title":"修改密码", "url":"<%=path%>/login/modPwd.jsp?modType=1", "width":1000, "height":600, modal:true});
}
</script>
</body>
</html>
