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
<script type="text/javascript" src="<%=path%>/resources/plugins/spiritui/jq.spirit.pageFrame.js"></script>
<script src="<%=path%>/resources/js/visit.utils.js"></script>
<script src="<%=path%>/analApp/js/analApp.view.js"></script>
<script src="<%=path%>/analApp/js/zui.pager.js"></script>
<script src="<%=path%>/reportFrame/serial/js/spirit.report.serial.utils.js"></script>

<title>列表显示查询结果主界面</title>
</head>
<style>
.div{padding:2px;border:1px solid #ddd;width:90%;margin:0 auto;}
.div_center{margin:0 auto;text-align:center;}
.border_no{border:0px solid red; }
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

/*** begin 小红圆点，标识未读报告 ***/
.circleFillRed {
float:left;
width: 10px;
height: 10px;
background-color: #ff0000;
-webkit-border-radius: 10px;
border-radius:10px;
}
.circleOpacity {
float:left;
width: 10px;
height: 10px;
background-color: inherit;
-webkit-border-radius: 10px;
border-radius:10px;
}
/*** end 小红圆点，标识未读报告 ***/
/* 脚部 */
.footSegment_bgwhite_bordertop{
  border: 0px solid #95b8e7;
  border-top: 1px solid #95b8e7;
  background-color: #FFF;
}
</style>
<body class="padding_top5" style="background-color:#FFFFFF">
  <div id="mainSegment" class="list div_float_left" style="width:100%;margin:0 auto;padding:10px 30px;">  
    <section id="sectionListId" class="items items-hover">      
    </section>  
    <!-- 由于footSegment会遮住mainSegment的底部，所以需要设置一个DIV，不让footSegment遮 -->
    <div style="height:60px;">
    </div>  
  </div>  
  <div id="footSegment" class="div_center footSegment_bgwhite_bordertop">    
    <!-- 列表显示分页条 -->
    <div id="div_pager_list"></div>
  </div>
</body>
<script>
//*** begin 常量定义 ***
var pager_list_size = 10; //列表显示时，每页显示的条数
//*** end 常量定义 ***

//*** begin 变量定义 ***
var thumbPath = "<%=path%>/analApp/images/"; //报告缩略图所存储的路径
var defaultThumbImg = "pie.png"; //默认显示的缩略图名称
var searchResultJsonData_list = null; //保存查询后的结果
var _searchStr = getUrlParam(window.location.href, "searchStr");
_searchStr = decodeURIComponent(_searchStr);

var pager_list = null; //列表分页对象
var pager_selected_list = null; //存储最近一次选择的页码，用于切换列表/卡片显示时再次查询

//主窗口参数
var INIT_PARAM = {
  //页面中所用到的元素的id，只用到三个Div，另，这三个div应在body层
  pageObjs: {
    mainId: "mainSegment", //主体Id
    footId: "footSegment" //主体Id
  },
  page_width: 0,
  page_height: 0,

  foot_height: 60, //脚部高度
  foot_peg: false //是否钉住脚部在底端。false：脚部随垂直滚动条移动(浮动)；true：脚部钉在底端
};

//*** end 变量定义 ***

//主函数
$(function() {
	//初始化页面框架
	var initStr = $.spiritPageFrame(INIT_PARAM);
	if (initStr) {
	  showAlert("页面初始化失败", initStr, "error");
	  return ;
	};
	$("#footSegment").removeClass("footSegment").addClass("footSegment_bgwhite_bordertop");
	
	//初始化分页
	initPager();
	
	//查询
	_urlPath = "<%=path%>";  
  startSearch();
});

//初始化分页
function initPager(){
	pager_list = new $.ZuiPager(); 
	pager_list.initPager({"pageSize":pager_list_size,"divPageId":"div_pager_list","objPager":pager_list,"onSelectPage":selectPage});  
}
//选择了某个页面
function selectPage(pageNumber, pageSize){
	//alert("onSelectPage()... pageNumber="+pageNumber+";pageSize="+pageSize);
	startSearch({"pageNumber":pageNumber, "pageSize":pageSize});
}

//开始查询
function startSearch(searchParam) {
  //searchResultJsonData_list = {};
  //异步查询文件列表  
  searchParam = combineSearchParam(searchParam);
  //var url = "<%=path%>/listview/searchGeneralList.do";
  var url = "<%=path%>/listview/searchGeneralPageList.do";
  //alert(allFields(searchParam));
  $.ajax({type:"post", async:true, url:url, data:searchParam, dataType:"json",
    success: function(jsonObj) {
      try {
        //searchResultJsonData = str2JsonObj(jsonStr);
        //searchResultJsonData_list=jsonStr;         
        showSearchResult(jsonObj);
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
function showSearchResult(jsonObj) {
	searchResultJsonData_list = jsonObj; 
	var _objList = $('#sectionListId');
  _objList.empty();
    
  if(searchResultJsonData_list!=null && searchResultJsonData_list.rows!=null && searchResultJsonData_list.rows.length>0){
   var jsonRows = searchResultJsonData_list.rows;
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
      if(fileType=="file"){ //文件类型
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
        divItemHead.append('<h4><span class="label label-primary font_size15">'+flag+'</span>&nbsp; <a href="###" class="href_file" style="padding-left:20px;" onclick="showFile(\''+id+'\',\''+fileFull+'\');">'+fileFull+'</a></h4>');
        //item-content内容
        divItemContent.append('<div class="text font_size13">'+'ID：'+highlightStr(id,_searchStr)+'&nbsp;&nbsp;&nbsp;'
          +'名称：'+highlightStr(fileFull,_searchStr)+'&nbsp;&nbsp;&nbsp;'
          +'报告ID：'+highlightStr(reportId,_searchStr)+'&nbsp;&nbsp;&nbsp;'+'大小：'+highlightStr(size,_searchStr)+'&nbsp;&nbsp;&nbsp;'
          +'上传时间：'+highlightStr(createDate,_searchStr)+'&nbsp;&nbsp;&nbsp;'+'简介：'+highlightStr(desc,_searchStr)+'</div>');
      } else if (fileType=="report") { //报告类型
        var reportType = arow["reportType"];
        flag = "RPT";
        var unReadId = getUnReadReportId(id,"list");        
        //item-head内容
        divItemHead.append('<div class="pull-right"><a href="###" onclick="showReport(\''+id+'\',\''+unReadId+'\');"><i class="icon-list"></i>浏览</a> &nbsp;<a href="#" onclick="showRelation(\''+id+'\');"><i class="icon-building"></i>关系</a></div>');
        //divItemHead.append('<h4><span class="label label-success font_size15">'+flag+'</span>&nbsp; <a href="###" onclick="showReport(\''+id+'\');" class="href_file">'+fileFull+'</a></h4>');
        var optRound = '<div id="'+unReadId+'" class="'+(unReadId?'circleFillRed':'circleOpacity')+'" style="margin-left:10px;" />';
        var hrefReportHtml = '<a href="###" onclick="showReport(\''+id+'\',\''+unReadId+'\');" class="href_file">'+optRound+fileFull+'</a>';
        divItemHead.append('<h4><span class="label label-success font_size15" style="float:left;">'+flag+'</span>&nbsp;'+hrefReportHtml+'</h4>');
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

    //设置分页条
    pager_list.setTotalCount(searchResultJsonData_list.total,pager_selected_list.pageNumber);
  } else {
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
    
    //隐藏分页条
    pager_list.hidePager();
  }
}


//过滤查询条件，组装成符合逻辑的查询条件
function combineSearchParam(searchParam){
  //如果不存在查询条件（说明是第一次查询），如果查询条件改变，则重置查询页面参数
  if(!searchParam || !pager_selected_list || _searchStr!=pager_selected_list.searchStr){        
	  searchParam = {"pageNumber":1, "pageSize":pager_list_size, "searchStr":_searchStr};
  }else{
	  searchParam.searchStr = _searchStr;
  }
  pager_selected_list = searchParam;   
  return pager_selected_list;
}

</script>
</html>