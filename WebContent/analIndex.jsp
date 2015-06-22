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
  int userState = 0;
  if (user != null) {
    loginName = user.getLoginName();
    userState = user.getUserState();
  }
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

<script type="text/javascript" src="<%=path%>/resources/js/mainPage.utils.js"></script><!-- 主界面标识 -->

<script type="text/javascript" src="<%=path%>/resources/plugins/spiritui/jq.spirit.pageFrame.js"></script>
<link rel="stylesheet" type="text/css" href="<%=path%>/login/css/login.css">
<link rel="stylesheet" type="text/css" href="<%=path%>/resources/plugins/spiritui/themes/default/pageFrame.css"/>

<title>互联网+无模式开放数据分析平台—灵派诺达</title>
</head>
<style>
body {
  background-color:#fff;
}
</style>
<body>
<!-- 头部:悬浮 -->
<div id="topSegment">
</div>

<!-- 脚部:悬浮 
<div id="footSegment" style="padding:0px;display:none;"></div>
-->

<!-- 中间主框架 -->
<div id="mainSegment">
  <iframe id="ms_Homepage"></iframe>
  <iframe id="ms_Report"></iframe>
  <iframe id="ms_File"></iframe>
  <iframe id="ms_Search"></iframe>
</div>

<iframe id="tframe" name="tframe" bordercolor=red frameborder="yes" border=1 width="600" height="200" style="width:600px;heigth:200px; boder:1px solid red;display:none;"></iframe>
</body>

<script>
//初始化查询输入框
var searchTxt = "请输入查询内容...";

//主窗口参数
var INIT_PARAM = {
  pageObjs: {
    topId: "topSegment",
    mainId: "mainSegment"
  },
  page_width: 1000,
  page_height: -1,
  top_shadow_color:"#E6E6E6",
  top_height: 45,
  top_peg: false,
  win_min_width: 640, //页面最小的高度。当窗口高度小于这个值，不对界面位置及尺寸进行调整。主体部分宽度也照此设置
  win_min_height: 480, //页面最小的高度。当窗口高度小于这个值，不对界面位置及尺寸进行调整。主体部分高度也照此设置
  myInit: initPosition,
  myResize: myResize
};

/**变量定义区**/
//菜单定义
var MENU_INFO = {
  "defaultView":{"iframeId":"mainSegmentIframe","fileName":"analApp/constructing.jsp"},  
  "reportView":{"iframeId":"mainSegmentIframe_report","fileName":"analApp/ReportView/main.jsp"},
  "fileView":{"iframeId":"mainSegmentIframe_file","fileName":"analApp/FileView/main.jsp"},
  "generalSearchView":{"iframeId":"mainSegmentIframe_search","fileName":"analApp/listView/main.jsp"},
  "dataUpLoadView":{"iframeId":"mainSegmentIframe","fileName":"dataUpload.jsp"}
};
//记录新增报表信息
var newReportJson={};

//主函数
$(function() {
  //alert("enter mainAnalApp.jsp...");
  var initStr = $.spiritPageFrame(INIT_PARAM);
  if (initStr) {
    $.messager.alert("页面初始化失败", initStr, "error");
    return ;
  };
});

//初始化界面
function initPosition() {
  $("#mainSegmentIframe").width($("#mainSegment").width());
  $("#mainSegmentIframe").height($("#mainSegment").height());
  $("#mainSegmentIframe_report").width($("#mainSegment").width());
  $("#mainSegmentIframe_report").height($("#mainSegment").height());
  $("#mainSegmentIframe_file").width($("#mainSegment").width());
  $("#mainSegmentIframe_file").height($("#mainSegment").height());
  $("#mainSegmentIframe_search").width($("#mainSegment").width());
  $("#mainSegmentIframe_search").height($("#mainSegment").height());
};
//当界面尺寸改变
function myResize() {
  $("#mainSegmentIframe").width($("#mainSegment").width());
  $("#mainSegmentIframe").height($("#mainSegment").height());
  $("#mainSegmentIframe_report").width($("#mainSegment").width());
  $("#mainSegmentIframe_report").height($("#mainSegment").height());
  $("#mainSegmentIframe_file").width($("#mainSegment").width());
  $("#mainSegmentIframe_file").height($("#mainSegment").height());
  $("#mainSegmentIframe_search").width($("#mainSegment").width());
  $("#mainSegmentIframe_search").height($("#mainSegment").height());
};

function initSearchFileInput(){
  var _objSearch = $("#idSearchFile");
  _objSearch.keydown(function(e){
    if(e.keyCode == 13){
      //alert("搜索输入框按回车按钮了");
      startSearch();
    }
  });
}

var lastSearchStr = "";
//取出输入条件，提交查询
function startSearch(){
  var searchStr = ($("#idSearchFile").val()==searchTxt)?"":$("#idSearchFile").val();
  //alert("您输入了："+ searchStr);
  var fileParam = "searchStr="+searchStr+"&refreshme=yes";
  //还需把查询条件传入###
  if(lastSearchStr!=searchStr){    
    lastSearchStr = searchStr;
  }
  showIframe('generalSearchView',fileParam);
}

/**
 * 异步请求后台，查找是否有新的报告生成
 */
function searchNewReport(){
  //异步查询是否有新增报表   
  var searchParam={"searchType":"fectchNewReport","searchStr":""};
  var url="<%=path%>/reportview/searchNewReport.do";
  $.ajax({type:"post", async:true, url:url, data:searchParam, dataType:"json",
    success:function(jsonData){
      try{
        newReportJson = jsonData;          
        if(newReportJson&&newReportJson.rows&&newReportJson.rows.length>0){
           refreshNewReportDIV();
        }
      }catch(e){
        //alert("解析新报告异常    "+e.message);
        $.messager.alert("解析新报告异常", "查询结果解析成JSON失败：</br>"+(e.message)+"！<br/>", "error", function(){});
      }
    },
    error:function(errorData){
      //alert("err");
      //$.messager.alert("查询新报告异常", "查询失败：</br>"+(errorData?errorData.responseText:"")+"！<br/>", "error", function(){});
    }
  });   
}


//*** begin 处理未读报告 ***//

/**
 * 刷新是否有新报告生成的DIV
 */
function refreshNewReportDIV(){
  var objAReport=$("#a_report");
  var objShowCount = $("#id_report_new");
  var len = newReportJson.rows.length;
  if(len>0){
    objShowCount.html(len>99?"...":len);
    objShowCount.css("visibility","visible");
    //当显示新增条数时，禁用报告按钮
    //objAReport.removeAttr("href");//去掉a标签中的href属性
    objAReport.removeAttr("onclick");//去掉a标签中的onclick事件
    setTimeout(function(){objAReport.attr("onclick","showIframe('reportView');");},500);
  }else{
    objShowCount.css("visibility","hidden");
    //objAReport.addAttr("href","###");//加上a标签中的href属性
    objAReport.attr("onclick","showIframe('reportView');");//加上a标签中的onclick事件
  }
}

/**
 * 模态显示新报告列表
 * 当点击新报告数量图标时，触发此方法
 */
function showModelNewReportList(){
  if(newReportJson.rows.length<=0){
    return;
  }
  var winOption={
    url:"<%=path%>/analApp/ReportView/unReadTable.jsp",
    //content:contentHtml,
    title:"未读报告列表",
    height:600,
    width:750,
    iframeScroll:"yes"
  };
  openSWinInMain(winOption);
}

/**
 * 返回指定的报告ID是否为未读报告
 * 当条件查询报告的时候，触发此方法
 * 如果areportId在newReportJson中，则返回true
 */
function isUnReadReportById(areportId){   
  if(newReportJson&&newReportJson.rows&&newReportJson.rows.length>0 && areportId){
    for(var i=0;i<newReportJson.rows.length;i++){
      var aRow = newReportJson.rows[i];
      if(aRow.reportId && aRow.reportId==areportId){
        return true;
      }
    }
  }
  return false;
}

//*** end 处理未读报告 ***//

//点击logo图片
function clickLogo(){
  alert("click logo");
}

//隐藏所有iframe
function showIframe(viewName,fileParam){
  try{
    //alert("showIfram viewName="+viewName+"  fileParam="+fileParam);
    //先隐藏所有iframe
    $("#mainSegmentIframe").css("display","none");
    $("#mainSegmentIframe_report").css("display","none");
    $("#mainSegmentIframe_file").css("display","none");
    $("#mainSegmentIframe_search").css("display","none");
  
    //显示指定的iframe
    var iframeId = "";
    var fileName = "";
    if(viewName){
      if(MENU_INFO[viewName]){ //找到菜单项定义
        iframeId = MENU_INFO[viewName].iframeId;
        fileName = MENU_INFO[viewName].fileName;
      }else{ //未找到菜单项定义,使用默认的
        iframeId = MENU_INFO.defaultView.iframeId;
        fileName = MENU_INFO.defaultView.fileName;
      }
    }else{//未找到菜单项定义,使用默认的
      iframeId = MENU_INFO.defaultView.iframeId;
      fileName = MENU_INFO.defaultView.fileName;
    }
    var fileFull = fileName;
    //加入参数
    if(fileParam){
      fileFull += "?"+fileParam;
    }
    
    if($("#"+iframeId+"").attr("src") && $("#"+iframeId+"").attr("src").indexOf(fileName)>-1){
      if($("#"+iframeId+"").attr("src").indexOf("refreshme=yes")>-1){ //如果强制刷新，则重新请求
        $("#"+iframeId+"").attr("src",_PATH+"/"+fileFull); 
      }else{//已经加载过了，则不再加载
        //alert("已经加载过了，不再加载！");
      }
    }else{
      $("#"+iframeId+"").attr("src",_PATH+"/"+fileFull);  
    }
    $("#"+iframeId+"").css("display","block");
  }catch(e){
    $.messager.alert("显示iframe异常", "显示失败：</br> viewName="+viewName+"  errMsg:"+(e?e.message:"")+"！<br/>", "error", function(){});
  }
}

/** 用户注册、修改相关内容    */
//初始化按钮方法
function initButton(initType) {
  var lgName;
  if(initType) lgName = $('#loginName').val();
  else $('#loginName').val('<%=loginName%>');
  lgName = $('#loginName').val();
  if (lgName!=null&&lgName!="") {
    setLogined(lgName);
  } else {
    setNoLogin();
  }
}
//头中按钮样式
function buttonStyle(){
  $(".loginButton").css({'background-color':'#32C52F','float':'left'}).children().css({'color':'#FDFFFD'});
  $(".loginButton").bind("mouseover",function(){
    $(this).css({'background-color':'#D9F4DF'}).children().css({'color':'#32C52F'});
  });
  $(".loginButton").bind("mouseleave",function(){
    $(this).css({'background-color':'#32C52F'}).children().css({'color':'#FDFFFD'});
  });
}
//===根据登录状态，修改页面显示
function setNoLogin() {
  $('#_logout').parent().css('display','none');
  $('#login').parent().css('display','');
  $('#register').parent().css('display','');
  $('#updateUser').parent().css('display','none');
}
function setLogined(loginName) {
  try{
    //alert("setLogined() loginName="+loginName);
    $('#_logout').parent().css("display", "");
    $('#login').parent().css('display','none');
    $('#register').parent().css('display','none');
    $('#updateUser').parent().css('display','');
    if(loginName!=""&&loginName!=null){
      $('loginName').val(loginName);
      $("#div_userName").html(loginName);
    }
  }catch(e){alert(e.message);}
}

//以下为页面跳转部分============
//跳转到注册页面
function register(){
  var _url ="<%=path%>/login/register.jsp";
  var winOption={
    url:_url,
    title:"注册",
    height:wHeight,
    width:wWidth,
    modal:true,
    zIndex:-1
  };
  openSWinInMain(winOption);
}
// 忘记密码
function forgetPassword(){
  var _url="<%=path%>/login/forgetPassword.jsp";
  var title = "忘记密码";
  var winOption={
    url:_url,
    title:title,
    height:wHeight,
    width:wWidth,
    modal:true
  };
  openSWinInMain(winOption);
}
//修改个人信息
function updateUser(){
  var _url="<%=path%>/login/update.jsp?";
  var title = "修改";
  var winOption={
    url:_url,
    title:title,
    height:wHeight,
    width:wWidth,
    modal:true
  };
  openSWinInMain(winOption);
}
//以上为页面跳转部分============

//登录
var wHeight = "430";
var wWidth = "330";
function login(){
  var loginType = 1;
  var _url="<%=path%>/login/login.jsp?loginType="+loginType;
  var winOption={
    url:_url,
    title:"登录",
    height:wHeight,
    width:wWidth,
    modal:true
  };
  $('#login').parent().attr('disabled','disabled');
  openSWinInMain(winOption);
  setTimeout(function(){
    $('#login').bind("onclick",function(){login();});
  }, 1000);
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
          window.location.href=_MAIN_PAGE;
          setNoLogin();
        });
      } else {
        if(json.data==null){
          $.messager.alert("提示","您还未登录!",'info');
          setNoLogin();
        }else{
          $.messager.alert("错误", "注销失败："+json.data+"！</br>返回登录页面。", "error", function(){
            window.location.href=_MAIN_PAGE;
            setNoLogin();
          });
        }
      }
    },
    error: function(errorData) {
      $.messager.alert("错误", "注销失败：</br>"+(errorData?errorData.responseText:"")+"！<br/>返回登录页面。", "error", function(){
        window.location.href=_MAIN_PAGE+"?noAuth";
        setNoLogin();
      });
    }
  });
};

/**
 * 数据上传
 */
function uploadFile(){
  try{
    //提交上传文件
    $("#upfs").val($("#upf").val());
    //alert($("#upf").val());
    $('#afUpload').form('submit',{
      async:true,
      success:function(respStr){
        //prompt('',"succ upload file. resp str="+respStr);
        var respJson = null;
        try{respJson=str2JsonObj(respStr);}catch(e){alert("str 2 json err. jsonStr="+respStr);}
        if(respJson.message && respJson.message[0] && respJson.message[0].success=="TRUE"){
          //alert("succ upload file.");
          //查找是否生成新报告
          searchNewReport();
          //跳转到报告页面
          setTimeout(function(a,b){return function(){showIframe(a,b);}}('reportView','refreshme=yes'),1*1000);
          //showIframe('reportView','refreshme=yes');          
        }else{
          alert("上传文件失败 .");
        }
      },
      error:function(errData){
        alert("failed to upload file. errData="+errData);
      }
    });
    //$('#afUpload').submit();
    //跳转到报告页面
    //showIframe('reportView');
  }catch(e){
    alert("failed to upload file.  e="+e.message);
  }
}
</script>
</html>