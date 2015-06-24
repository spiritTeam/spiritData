<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="com.spiritdata.framework.FConstants"%>
<%@page import="com.spiritdata.dataanal.UGA.pojo.User"%>
<%
  String path = request.getContextPath(); //base Url
  String sid = request.getSession().getId(); //sessionId
  //得到用户信息
  User user = ((User)session.getAttribute(FConstants.SESSION_USER));
  String loginName = "";
  if (user!=null) loginName = user.getLoginName();
%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<!-- 默认的应用主页面  -->
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="cache-control" content="no-cache"/>
<meta http-equiv="pragma" content="no-cache"/>
<meta http-equiv="expires" content="0"/>
<jsp:include page="/common/sysInclude.jsp" flush="true"/>

<link rel="stylesheet" type="text/css" href="<%=path%>/resources/plugins/spiritui/themes/default/pageFrame.css"/>
<link rel="stylesheet" type="text/css" href="<%=path%>/index/css/all_spiritUi.css"/>
<link rel="stylesheet" type="text/css" href="<%=path%>/index/css/main.css"/>

<script type="text/javascript" src="<%=path%>/resources/js/mainPage.utils.js"></script><!-- 主界面标识 -->
<script type="text/javascript" src="<%=path%>/resources/plugins/spiritui/jq.spirit.pageFrame.js"></script>
<script type="text/javascript" src="<%=path%>/resources/plugins/spiritui/jq.spirit.tabs.js"></script>

<!-- 加载ZUI - 开源HTML5跨屏框架 -->
<link href="<%=path%>/resources/plugins/zui/css/zui.min.css" rel="stylesheet">
<link href="<%=path%>/resources/plugins/zui/css/example.css" rel="stylesheet">
<script src="<%=path%>/resources/plugins/zui/js/zui.min.js"></script>

<title>互联网+无模式开放数据分析平台—灵派诺达</title>
</head>
<body>
<!-- 遮罩层 -->
<div id="mask" style="display:yes; position:absolute;vertical-align:middle;text-align:center; align:center;">
  <div id="maskTitle">正在分析，请稍候...</div>
</div>

<!-- 头部:悬浮 -->
<div id="topSegment">
  <div id="logo" class="main_logo"></div>
  <div id="ifNavs" class="main_ifnavs">
    <div id="nav_homepage" class="nav" onclick="switchIF(0)">首页</div>
    <div id="nav_report" class="nav" style="left:100px" onclick="switchIF(1)">报告</div>
    <div id="nav_file" class="nav" style="left:200px" onclick="switchIF(3)">文件</div>
  </div>
  <div id="funBar" class="main_funbar">
    <div class="input-group" style="display:block; margin-bottom:5px; width:240px" onclick="switchIF(4)">
      <input id="searchAll" class="form-control" style="width:160px; height:24px" type="text" placeholder="请输入查询内容...">
      <span class="input-group-btn">
        <button id="submitSearchAll" onclick="startSearch();" class="btn btn-default" type="button" style="font:18px Microsoft YaHei,Microsoft JhengHei,黑体;">搜索</button>
      </span>
    </div>
    <div id="upf_btn" class="upf_btn" title="上传数据文件"></div>
  </div>
  <div id="userManage" class="main_userm">
    <div id="userShow"><span style="color:black;">用户：</span><span id="loginName"></span></div>
    <div class="loginButton" id="login" onclick="login()">&nbsp;登&nbsp;录&nbsp;</div>
    <div class="loginButton" id="logout" onclick="logout()">&nbsp;注&nbsp;销&nbsp;</div>
    <div class="loginButton" id="updateUser" onclick="updateUser()">&nbsp;修&nbsp;改&nbsp;</div>
    <div class="loginButton" id="register" onclick="register()">&nbsp;注&nbsp;册&nbsp;</div>
  </div>
</div>

<!-- 中间主框架 -->
<div id="mainSegment">
  <iframe frameborder="no" style="display:none;" id="ifmHomepage" _src="analApp/homepage.jsp"></iframe>
  <iframe frameborder="no" style="display:none;" id="ifmReport" _src="analApp/ReportView/main.jsp"></iframe>
  <iframe frameborder="no" style="display:none;" id="ifmFile" _src="analApp/FileView/main.jsp"></iframe>
  <iframe frameborder="no" style="display:none;" id="ifmSearch" _src="analApp/listView/main.jsp"></iframe>
</div>

<iframe id="tframe" name="tframe" style="width:600px;heigth:200px;display:none;"></iframe>
</body>

<script>
//把jsp中java取出的变量在javascript层进行转存
var _sid="<%=sid%>";//sessionId
var _loginName="<%=loginName%>";//用户登录名称

var __STATUS="0";//状态，刚打开主页的状态
//登陆窗口大小
var wHeight = "430";
var wWidth = "330";

//主窗口参数
var INIT_PARAM = {
  pageObjs: {
    topId: "topSegment",
    mainId: "mainSegment"
  },
  page_width: -1,
  page_height: -1,
  top_shadow_color:"#E6E6E6",
  top_height: 50,
  top_peg: false,
  win_min_width: 870, //页面最小的高度。当窗口高度小于这个值，不对界面位置及尺寸进行调整。主体部分宽度也照此设置
  win_min_height: 480, //页面最小的高度。当窗口高度小于这个值，不对界面位置及尺寸进行调整。主体部分高度也照此设置
  myInit: initPosition,
  myResize: myResize
};

//主函数
$(function() {
  //设置主界面
  var initStr = $.spiritPageFrame(INIT_PARAM);
  if (initStr) {
    $.messager.alert("页面初始化失败", initStr, "error");
    return ;
  };
  //============效果处理
  //导航样式
  $(".nav").mouseover(function() {
    if ($(this).hasClass("nav_sel")) return;
    $(this).addClass("nav_mouseOver");
  }).mouseout(function() {
    if ($(this).hasClass("nav_sel")) return;
    $(this).removeClass("nav_mouseOver");
  }).click(function() {
    if ($(this).hasClass("nav_sel")) return;
    var txt = $(this).html();
    $(".nav").each(function(){
      if ($(this).html()!=txt) $(this).removeClass("nav_sel").removeClass("nav_mouseOver");
    });
    $(this).removeClass("nav_mouseOver");
    $(this).addClass("nav_sel");
  });
  //上传按钮
  $("#upf_btn").mouseover(function() {
    if ($(this).hasClass("upf_mouseOver")) return;
    $(this).addClass("upf_mouseOver");
  }).mouseout(function() {
    $(this).removeClass("upf_mouseOver");
  }).click(function() {
  });
  //用户管理相关
  $(".loginButton").mouseover(function() {
    if ($(this).hasClass("lb_mouseOver")) return;
    $(this).addClass("lb_mouseOver");
  }).mouseout(function() {
    $(this).removeClass("lb_mouseOver");
  }).click(function() {
  });

  //================显示状态
  setPage();
});

//初始化界面
function initPosition() {
  $("iframe").each(function(){
    $(this).width($("#mainSegment").width()-4).height($("#mainSegment").height()-4);
  });
  $("#mask").css({"width":$(window).width()-2, "height":$(window).height()-2});
  $("#maskTitle").css({"top":$(window).height()/2+40, "left":($(window).width()-2-$("#maskTitle").width())/2});
}
//当界面尺寸改变
function myResize() {
  $("iframe").each(function(){
    $(this).width($("#mainSegment").width()-4).height($("#mainSegment").height()-4);
  });
  $("#mask").css({"width":$(window).width()-2, "height":$(window).height()-2});
  $("#maskTitle").css({"top":$(window).height()/2+40, "left":($(window).width()-2-$("#maskTitle").width())/2});
}

//=====显示状态的处理
//根据目前页面的状态，设置页面的显示
function setPage() {
  if (__STATUS==0&&!_loginName) setNologNoupf();
  if (__STATUS==0&&_loginName) setHaslogNoupf();
  if (__STATUS==1&&!_loginName) setNologHasupf();
  if (__STATUS==1&&_loginName) setHaslogHasUpf();
}
//设置为：未登录/未上传文件
function setNologNoupf() {
  //点击主页
  $("#nav_homepage").click();
  //设置其他的现实状态
  $("#nav_report").hide();
  $("#nav_file").hide();
  $("#funBar").hide();
  //显示登录区域
  $("#login").show();
  $("#logout").hide();
  $("#updateUser").hide();
  $("#register").show();
  $("#userShow").hide();
}
//设置为：已登录/未上传文件
function setHaslogNoupf() {
  //显示页签
  $("#nav_report").show();
  $("#nav_file").show();
  $("#funBar").show();
  //点击报告页
  $("#nav_report").click();
  //显示登录区域
  $("#login").hide();
  $("#logout").show();
  $("#updateUser").show();
  $("#register").hide();
  $("#userShow").show();

  $("#loginName").html(_loginName);
  $("#loginName").attr("title", _loginName);
}
//设置为：未登录/已上传文件
function setNologHasupf() {
  //显示页签
  $("#nav_report").show();
  $("#nav_file").show();
  $("#funBar").show();
  //点击报告页
  $("#nav_report").click();
  //显示登录区域
  $("#login").show();
  $("#logout").hide();
  $("#updateUser").hide();
  $("#register").show();
  $("#userShow").hide();
}
//设置为：已登录/已上传文件
function setHaslogHasUpf() {
  //显示页签
  $("#nav_report").show();
  $("#nav_file").show();
  $("#funBar").show();
  //点击报告页
  $("#nav_report").click();
  //显示登录区域
  $("#login").hide();
  $("#logout").show();
  $("#updateUser").show();
  $("#register").hide();
  $("#userShow").show();
}

//=======切换iframe页面
function switchIF(_type, _param) {
  $("#ifmHomepage").css("display","none");
  $("#ifmReport").css("display","none");
  $("#ifmFile").css("display","none");
  $("#ifmSearch").css("display","none");

  $("#upf_btn").show();
  var ifmId="";
  switch(_type) {
  case 0: //切换到主页
    ifmId="ifmHomepage";
    $("#upf_btn").hide();
    break;
  case 1: //切换到报告页
    ifmId="ifmReport";
    break;
  case 2: //切换到文件页
    ifmId="ifmFile";
    break;
  case 3:
    ifmId="ifmSearch";
    break; //切换到搜索结果页
  }

  if (ifmId) {
    var curObj=$("#"+ifmId);
    if (!curObj.attr("src")||curObj.attr("src").indexOf(curObj.attr("_src"))==-1) {//地址错误，需要重新设置src
      if (curObj.attr("_src")) {
      	var _url = curObj.attr("_src");
      	if (_param) _url+=(_url.indexOf("?")==-1?"?":"&")+_param;
        curObj.attr("src",_PATH+"/"+_url);
      }
    }
    if (curObj.attr("src")) curObj.css("display","block");
  }
}

//========登录相关
//登录
function login() {
  _openSwin("登录", "/login/login.jsp");
}
/**
 * 注销
 */
function logout() {
  $("#loginName").val("");
  var url=_PATH+"/logout.do";
  $.ajax({type:"post", async:true, url:url, data:null, dataType:"json",
    success: function(json) {
      if (json.type==1) {
        $.messager.alert("注销信息","注销成功!",'info',function(){
          _loginName="";
          setPage();
        });
      } else {
        if(json.data==null){
          $.messager.alert("提示","您还未登录!",'info');
        }else{
          $.messager.alert("错误", "注销失败："+json.data+"!</br>返回未登录首页。", "error", function(){
            _loginName="";
            setPage();
          });
        }
      }
    },
    error: function(errorData) {
      $.messager.alert("错误", "注销失败：</br>"+(errorData?errorData.responseText:"")+"!</br>返回未登录首页。", "error", function(){
        window.location.href=_MAIN_PAGE+"?noAuth";
        setNoLogin();
      });
    }
  });
};
//注册
function register(){
  _openSwin("注册", "/login/register.jsp");
}
//修改个人信息
function updateUser(){
  _openSwin("修改", "/login/update.jsp");
}
function _openSwin(title, url) {
  var winOption={
    "url":_PATH+url,
    "title":title,
    "height":wHeight,
    "width":wWidth,
    "modal":true
  };
  openSWinInMain(winOption);
}

//======为其他页面调用
function setLogined(loginName) {
  _loginName=loginName;
  setPage();
}
//显示遮罩层，flag=1为显示,flag=0为隐藏，message是遮罩层的名称
function showMask(flag, message) {
  if (flag) {
  	$("#maskTitle").html(message+(message.indexOf("...")==-1?"...":""));
    $("#mask").show();
    $("#maskTitle").css({"left":($(window).width()-2-$("#maskTitle").width())/2});
  } else $("#mask").hide();
}
</script>
</html>