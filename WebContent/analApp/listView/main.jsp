<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%
  request.setCharacterEncoding("UTF-8");
  String path = request.getContextPath();
  String searchStr = request.getParameter("searchStr");
  //输入中文后需要做转码，否则会出现乱码
  searchStr = new String(searchStr.getBytes("ISO-8859-1"),"UTF-8");
  //System.out.println("searchStr="+searchStr);
%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<!-- 通用查询列表显示结果页面 -->
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="cache-control" content="no-cache"/>
<meta http-equiv="pragma" content="no-cache"/>
<meta http-equiv="expires" content="0"/>

<jsp:include page="/common/sysInclude.jsp" flush="true"/>

<!-- 加载ZUI - 开源HTML5跨屏框架 -->
<link href="<%=path%>/resources/plugins/zui/css/zui.min.css" rel="stylesheet">
<link href="<%=path%>/resources/plugins/zui/css/example.css" rel="stylesheet">
<script src="<%=path%>/resources/plugins/zui/js/zui.min.js"></script>

<title>列表显示查询结果主界面</title>
</head>
<style>
.div_float_left{float:left;}
.padding_top5{padding-top:5px;}
.font_size13{font-size:13px;}
.font_size15{font-size:15px;}
.font_size18{font-size:18px;}
a:link{color:#0000CC;}
.href_file{font-size:medium;font-weight:normal;font-family:arial;color:#0000CC;}
.items .item{padding:10px 0px;border-bottom:0px solid #E5E5E5;transition:all 0.5s cubic-bezier(0.175, 0.885, 0.32, 1) 0s;}
.media{max-width:100%;text-align:center;vertical-align:middle;background-color:#FFF;border:0px solid #DDD;color:#AAA;}
</style>
<body class="padding_top5" style="background-color:#FFFFFF">
  <div class="list div_float_left" style="width:60%;margin:0 auto;padding:10px 20px 10px 100px;">
    <section id="sectionListId" class="items items-hover">
      <div class="item">
        <div class="item-heading">
          <div class="pull-right"><a href="###"><i class="icon-list"></i>浏览</a> &nbsp;<a href="#"><i class="icon-building"></i>报告</a></div>
          <h4><span class="label label-success font_size15">XLS</span>&nbsp; <a href="###" class="font_size18">文件1</a></h4>
        </div>
        <div class="item-content">
          <div class="media pull-left">
            <div class="media-place-holder" style="width:200px;height:100px;line-height:100px">200x100</div>
          </div>
          <div class="text font_size15">文件1的简要描述信息在此显示!<br>上传人：XXX，上传时间：XXX</div>
        </div>
        <div class="item-footer">
        </div>
      </div>
      <div class="item">
        <div class="item-heading">
          <div class="pull-right"><a href="###"><i class="icon-list"></i>浏览</a> &nbsp;<a href="#"><i class="icon-building"></i>关系</a></div>
          <h4><span class="label label-success font_size15">RPT</span>&nbsp; <a href="###" class="font_size18">报告1</a></h4>
        </div>
        <div class="item-content">
          <div class="media pull-left">
            <div class="media-place-holder" style="width:200px;height:100px;line-height:100px">200x100</div>
          </div>
          <div class="text font_size15">报告1的简要描述信息在此显示!<br>上传人：XXX，上传时间：XXX</div>
        </div>
        <div class="item-footer">
        </div>
      </div>
    </section>
  </div>
</body>
<script>
//变量定义
var thumbPath = "<%=path%>/analApp/images/"; //报告缩略图所存储的路径
var defaultThumbImg = "pie.png"; //默认显示的缩略图名称
var searchResultJsonData = null; //保存查询后的结果

//主函数
$(function() {
	startSearch();
});

//开始查询
function startSearch(){
	var _searchStr = "<%=searchStr%>";
	searchResultJsonData = {};
  //异步查询文件列表  
  var searchParam={"searchStr":_searchStr};
  var url="<%=path%>/analApp/demoData/commonlist.json";
  $.ajax({type:"post", async:true, url:url, data:searchParam, dataType:"text",
    success:function(jsonStr){
      try{
        searchResultJsonData = str2JsonObj(jsonStr); 
        showSearchResult();
      }catch(e){
        $.messager.alert("解析异常", "查询结果解析成JSON失败：</br>"+(e.message)+"！<br/>", "error", function(){});
      }
    },
    error:function(errorData){
      $.messager.alert("查询异常", "查询失败：</br>"+(errorData?errorData.responseText:"")+"！<br/>", "error", function(){});
    }
  }); 
}

//显示查询结果
function showSearchResult(){
  if(searchResultJsonData!=null && searchResultJsonData.rows!=null && searchResultJsonData.rows.length>0){
	  var _objList = $('#sectionListId');
	  _objList.empty();
	  var jsonRows = searchResultJsonData.rows;
	  var len = jsonRows.length;
    for(var i=0;i<len;i++){
    	//创建一条记录的div
    	var divItem = $('<div class="item"></div>');
    	divItem.appendTo(_objList);
      //创建item-heading
      var divItemHead = $('<div class="item-heading"></div>');    
      divItemHead.appendTo(divItem);  
      //创建 item-content
      var divItemContent = $('<div class="item-content"></div>');
      divItemContent.appendTo(divItem);
      //创建item-footer       
      var divItemFooter = $('<div class="item-footer"></div>');
      divItemFooter.appendTo(divItem);
    	
	    //读取一条记录的内容
    	var fileId = jsonRows[i]["id"];
	    var fileName = jsonRows[i]["name"];
      var fileFull = fileName;
	    var size = jsonRows[i]["size"];
	    var createDate = jsonRows[i]["createDate"];
	    var desc = jsonRows[i]["desc"];    
	    var fileType = jsonRows[i]["type"];
	    var flag = "FILE";
	    if(fileType=="file"){
	      var suffix = jsonRows[i]["suffix"];
	      if(typeof(suffix)!="undefined" && suffix!=null && suffix.length>0){
	    	  fileFull += "."+suffix;
	    	  flag = suffix.toUpperCase();
	      }
	      //item-head内容
	      divItemHead.append('<div class="pull-right"><a href="###"><i class="icon-list"></i>浏览</a> &nbsp;<a href="#"><i class="icon-building"></i>报告</a></div>');
	      divItemHead.append('<h4><span class="label label-primary font_size15">'+flag+'</span>&nbsp; <a href="###" class="href_file" onclick="showFile(\''+fileId+'\');">'+fileFull+'</a></h4>');
	      //item-content内容
	      divItemContent.append('<div class="text font_size13">'+'大小：'+size+'，上传时间：'+createDate+'，简介：'+desc+'</div>');
	    }else if(fileType=="report"){
	    	flag = "RPT";
	      //item-head内容
	      divItemHead.append('<div class="pull-right"><a href="###"><i class="icon-list"></i>浏览</a> &nbsp;<a href="#"><i class="icon-building"></i>关系</a></div>');
	      divItemHead.append('<h4><span class="label label-success font_size15">'+flag+'</span>&nbsp; <a href="###" class="href_file">'+fileFull+'</a></h4>');
	      //item-content内容
	      var mediaPullLeftDiv = $('<div class="media pull-left"></div>');
	      mediaPullLeftDiv.appendTo(divItemContent);
	      var mediaPlaceHolderDiv = $('<div class="media-place-holder" style="width:121px;height:75px;line-height:75px"></div>');
	      mediaPlaceHolderDiv.appendTo(mediaPullLeftDiv);
	      var thumbUrl = jsonRows[i]["thumbUrl"];
	      thumbUrl = thumbPath + getStr(thumbUrl,defaultThumbImg);
	      mediaPlaceHolderDiv.append('<img src='+thumbUrl+' style="width:121px;height:75px;" alt="缩略图">');
	      divItemContent.append('<div class="text font_size13">'+'大小：'+size+'，上传时间：'+createDate+'，简介：'+desc+'</div>');
	    }
    }
  }
}

/**
 * 返回字符串，如果aStr不存在或为空，则使用默认的字符串
 * @param aStr 指定的字符串
 * @param defaultStr 默认的返回字符串
 */
function getStr(aStr,defaultStr){
  var retStr = defaultStr;
  if(typeof(aStr)!="undefined" &&!aStr && aStr.length>0){
    retStr = aStr;
  }
  return retStr;
}

//查询结果中，当点击了某个文件，触发此操作
function showFile(fileId){
alert("您点击了："+fileId);
}

//弹框显示报告详情
function showReport(reportId) {
var winOption={
  url:"<%=path%>/demo/Rd/resultRdEchart.jsp",
  title:"报告详情",
  height:600,
  width:1000,
  iframeScroll:"yes"
};
openSWinInMain(winOption);
}
</script>
</html>