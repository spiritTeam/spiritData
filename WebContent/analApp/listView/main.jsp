<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%
  request.setCharacterEncoding("UTF-8");
  String path = request.getContextPath();
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
<!-- 加载analApp的JS -->
<script src="<%=path%>/resources/js/visit.utils.js"></script>
<script src="<%=path%>/analApp/js/analApp.view.js"></script>
<script src="<%=path%>/reportFrame/serial/js/spirit.report.serial.utils.js"></script>

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
.highlight{background-color:yellow;font-weight:bold;font-style:italic}
</style>
<body class="padding_top5" style="background-color:#FFFFFF">
  <div class="list div_float_left" style="width:60%;margin:0 auto;padding:10px 20px 10px 160px;">
    <section id="sectionListId" class="items items-hover">      
    </section>
  </div>
</body>
<script>
//变量定义
var thumbPath = "<%=path%>/analApp/images/"; //报告缩略图所存储的路径
var defaultThumbImg = "pie.png"; //默认显示的缩略图名称
var searchResultJsonData = null; //保存查询后的结果
var _searchStr = getUrlParam(window.location.href, "searchStr");
_searchStr = decodeURIComponent(_searchStr);

//主函数
$(function() {
	_urlPath = "<%=path%>";  
  startSearch();
});

//开始查询
function startSearch() {
  searchResultJsonData = {};
  //异步查询文件列表  
  var searchParam={"searchStr":_searchStr};
  var url = "<%=path%>/listview/searchGeneralList.do";
  //alert(allFields(searchParam));
  $.ajax({type:"post", async:true, url:url, data:searchParam, dataType:"json",
    success: function(jsonStr) {
      try {
        //searchResultJsonData = str2JsonObj(jsonStr);
        searchResultJsonData=jsonStr; 
        showSearchResult();
      }catch(e){
        showAlert("解析异常", "查询结果解析成JSON失败：</br>"+(e.message)+"！<br/>", "error", function(){});
      }
    },
    error:function(errorData){
      showAlert("查询异常", "查询失败：</br>"+(errorData?errorData.responseText:"")+"！<br/>", "error", function(){});
    }
  }); 
}

//显示查询结果
function showSearchResult() {
  if(searchResultJsonData!=null && searchResultJsonData.rows!=null && searchResultJsonData.rows.length>0){
    var _objList = $('#sectionListId');
    _objList.empty();
    var jsonRows = searchResultJsonData.rows;
    var len = jsonRows.length;
    for(var i=0;i<len;i++){
      //读取一条记录的内容
      //var arow = str2JsonObj(jsonRows[i]["aRowJsonStr"]);
      var arow = jsonRows[i]["aRowJsonStr"];
      if (arow==null) continue;
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
      
      var id = arow["id"];
      var fileName = arow["name"];
      var fileFull = fileName;
      var createDate = arow["createDate"];
      var desc = arow["desc"];    
      var fileType = arow["type"];
      var flag = "FILE";
      if(fileType=="file"){
        var reportId = arow["reportId"];
        var size = arow["size"];
        var suffix = arow["suffix"];
        if(typeof(suffix)!="undefined" && suffix!=null && suffix.length>0){
          if(suffix.charAt(0)=="."){
            suffix = suffix.substring(1);
          }
          //fileFull += "."+suffix;
          flag = suffix.toUpperCase();
        }
        //item-head内容
        if(reportId && reportId!=null && reportId.length>0){
        	divItemHead.append('<div class="pull-right"><a href="###" onclick="showFile(\''+id+'\',\''+fileFull+'\');"><i class="icon-list"></i>浏览</a> &nbsp;<a href="#" onclick="showReport(\''+reportId+'\');"><i class="icon-building"></i>报告</a></div>');
        }else{
        	divItemHead.append('<div class="pull-right"><a href="###" onclick="showFile(\''+id+'\',\''+fileFull+'\');"><i class="icon-list"></i>浏览</a> &nbsp;<a href="#" style="visibility:hidden;" onclick="showReport(\''+reportId+'\');"><i class="icon-building"></i>报告</a></div>');
        }
        divItemHead.append('<h4><span class="label label-primary font_size15">'+flag+'</span>&nbsp; <a href="###" class="href_file" onclick="showFile(\''+id+'\',\''+fileFull+'\');">'+fileFull+'</a></h4>');
        //item-content内容
        divItemContent.append('<div class="text font_size13">'+'ID：'+highlightStr(id,_searchStr)+'&nbsp;&nbsp;&nbsp;'
          +'名称：'+highlightStr(fileFull,_searchStr)+'&nbsp;&nbsp;&nbsp;'
          +'报告ID：'+highlightStr(reportId,_searchStr)+'&nbsp;&nbsp;&nbsp;'+'大小：'+highlightStr(size,_searchStr)+'&nbsp;&nbsp;&nbsp;'
          +'上传时间：'+highlightStr(createDate,_searchStr)+'&nbsp;&nbsp;&nbsp;'+'简介：'+highlightStr(desc,_searchStr)+'</div>');
      } else if (fileType=="report") {
        var reportType = arow["reportType"];
        flag = "RPT";
        //item-head内容
        divItemHead.append('<div class="pull-right"><a href="###" onclick="showReport(\''+id+'\');"><i class="icon-list"></i>浏览</a> &nbsp;<a href="#" onclick="showRelation(\''+id+'\');"><i class="icon-building"></i>关系</a></div>');
        divItemHead.append('<h4><span class="label label-success font_size15">'+flag+'</span>&nbsp; <a href="###" onclick="showReport(\''+id+'\');" class="href_file">'+fileFull+'</a></h4>');
        //item-content内容
        var mediaPullLeftDiv = $('<div class="media pull-left"></div>');
        mediaPullLeftDiv.appendTo(divItemContent);
        var mediaPlaceHolderDiv = $('<div class="media-place-holder" style="width:121px;height:75px;line-height:75px"></div>');
        mediaPlaceHolderDiv.appendTo(mediaPullLeftDiv);
        var thumbUrl = arow["thumbUrl"];
        thumbUrl = thumbPath + getStr(thumbUrl,defaultThumbImg);
        
        mediaPlaceHolderDiv.append('<img src='+thumbUrl+' style="width:121px;height:75px;cursor:pointer;" onclick="showReport(\''+id+'\');" alt="缩略图">');
        divItemContent.append('<div class="text font_size13">'+'ID：'+highlightStr(id,_searchStr)+'&nbsp;&nbsp;&nbsp;'
          +'名称：'+highlightStr(fileFull,_searchStr)+'&nbsp;&nbsp;&nbsp;'+'报告类型：'+highlightStr(reportType,_searchStr)+'&nbsp;&nbsp;&nbsp;'
          +'上传时间：'+highlightStr(createDate,_searchStr)+'&nbsp;&nbsp;&nbsp;'+'简介：'+highlightStr(desc,_searchStr)+'</div>');      
      }
    }
  } else {
    var _objList = $('#sectionListId');
    _objList.empty();
    //创建一条记录的div
    var divItem = $('<div class="item"></div>');
    divItem.appendTo(_objList);
    //创建item-heading
    var divItemHead = $('<div class="item-heading"></div>');    
    divItemHead.appendTo(divItem);  
    divItemHead.append('<h4><span class="label label-primary font_size15">没有数据</span></h4>');
    //创建 item-content
    var divItemContent = $('<div class="item-content"></div>');
    divItemContent.appendTo(divItem);
    divItemContent.append('<div class="text font_size13">没有检索到相关数据！</div>');
    //创建item-footer       
    var divItemFooter = $('<div class="item-footer"></div>');
    divItemFooter.appendTo(divItem);
  }
}
</script>
</html>