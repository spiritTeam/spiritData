<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%
  String path = request.getContextPath();
%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<!-- 用户登录后显示的主页面，包括文件查询、上传、分析，用户管理等功能 -->
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

<title>文件主界面</title>
</head>
<style>
.div{padding:2px;border:1px solid #ddd;width:90%;margin:0 auto;}

/*** start 列表数据显示样式 ***/
.dg_border_left_1{border-left:1px grey solid;}
.dg_border_right_1{border-right:1px grey solid;}
.dg_th_font_bold{color:#444;font-weight:bold;}
.dg_td_bgcolor_lightblue{background-color:#e5ffee;}
/*** end 列表数据显示样式 ***/


</style>
<body style="background-color:#FFFFFF">
  <div class="div">
    <table style="width:100%;">
      <tr>
        <td style="width:20%;">          
        </td>    
        <td style="width:50%;">
          <div class="input-group">
            <input id="idSearchFile" class="form-control"  type="text" style="height:37px;" placeholder="请输入查询内容...">
            <span class="input-group-btn">
              <button id="idSubmitSearchFile" class="btn btn-default" type="button" style="font:18px Microsoft YaHei,Microsoft JhengHei,黑体;">搜索</button>
            </span>
          </div>  
        </td>    
        <td style="text-align:right;">
          <a href="#" class="">
            <img src="<%=path%>/analApp/images/file_list.png" style="height:45px;width:45px;" onclick="showSearchResult(SHOW_TYPE_LIST);" title="列表预览" alt="列表预览"/>
          </a>
          <a href="#" class="">
            <img src="<%=path%>/analApp/images/file_thumb.png" style="height:45px;width:45px;" onclick="showSearchResult(SHOW_TYPE_THUMB);" title="缩略图预览" alt="缩略图预览"/>
          </a>
        </td>
    </table>    
  </div>
             
  <div class="div">
    <!-- 查询结果列表显示-->
    <div id="dgList" style="display:none;"></div>
    <!-- 查询结果缩略图显示 -->
    <div id="dgThumb" style="display:none;"></div>
  </div>
</body>
<script>
//主函数
$(function() {
  initSubmitBt();
  initSearchFileInput();
});

//初始化查询输入框
var searchTxt = "请输入查询内容...";
function initSearchFileInput(){
  var _objSearch = $("#idSearchFile");
  _objSearch.keydown(function(e){
    if(e.keyCode == 13){
    	startSearch();
    }
  });
}

//初始化查询提交按钮
function initSubmitBt(){
  $("#idSubmitSearchFile").mouseover(function(){
    //$(this).css("color","#CC0000");
  }).mouseout(function(){
    //$(this).css("color","#000000");
  }).click(function(){   
	  startSearch();
  });
}


//定义查询方式和保存查询结果
var SHOW_TYPE_LIST = "LIST"; //列表显示常量
var SHOW_TYPE_THUMB = "THUMB"; //缩略图显示常量
var showType = SHOW_TYPE_LIST; //默认是列表显示查询结果
var searchResultJsonData = null; //保存查询后的结果
var objDatatable = null; //列表显示对象

//取出输入条件，提交查询
function startSearch(){
	var searchStr = getInputSearchFileStr();
  alert("您输入了："+ searchStr);

  //异步查询文件列表  
  var searchParam={"searchStr":searchStr};
  var url="<%=path%>/analApp/demoData/filelist.json";
  $.ajax({type:"post", async:true, url:url, data:searchParam, dataType:"text",
    success:function(jsonStr){
      try{
    	  searchResultJsonData = str2JsonObj(jsonStr); 
    	  showSearchResult(showType);
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
function showSearchResult(_showType){
	//alert("showSearchResult() showType="+_showType);
	showType = _showType;
  $('#dgList').css("display","none");
  $('#dgThumb').css("display","none");
    
	if(showType == SHOW_TYPE_LIST){
    showSearchResultList();
	}else if(showType == SHOW_TYPE_THUMB){
    showSearchResultThumb();
	}else{
		showType = SHOW_TYPE_LIST;
    showSearchResultList();
	}
}

//列表显示查询结果
function showSearchResultList(){
	var _objList = $('#dgList');
	_objList.empty();
	_objList.css("display","block");
  //构建dbopts
  var dbopts={
    customizable: true, 
    checkable: false,
    // sortable: true,
    sort: function(event){},
    mergeRows: true,
    scrollPos: 'out',
    fixedLeftWidth: '45%',
    fixedHeaderOffset: 8,
    storage: false,
    scrollPos:'in',
    rowHover:true,
    data:{  
      cols:[
        {width:'250',text:'文件名',type:'string',flex: false,colClass:'text-left',cssClass:'text-center dg_th_font_bold'},
        {width: 100,text:'大小',type:'number',flex: true,colClass:'text-right',cssClass:'text-center dg_th_font_bold'},
        {width:150,text:'创建日期',type:'date',flex: true,colClass:'text-left',cssClass:'text-center dg_th_font_bold',mergeRows: false}
      ]
    }
  };
  //组装显示结果行
  var dtrows =[];
  if(searchResultJsonData!=null && searchResultJsonData.rows!=null && searchResultJsonData.rows.length>0){
	  var jsonRows = searchResultJsonData.rows;
	  var len = jsonRows.length;
	  for(var i=0;i<len;i++){
	    var fileName = jsonRows[i]["name"];
	    var suffix = jsonRows[i]["suffix"];
	    var fileFull = fileName+"."+suffix;
	    var ahrf_file = '<a href="###" onclick="showFile(\''+fileFull+'\');"><strong>'+fileFull+'</strong></a>';
	    var size = jsonRows[i]["size"];
	    var createData = jsonRows[i]["createData"];
	    //var cssClassStr= i%2==0?"":"dg_td_bgcolor_lightblue";
	    //var arow={checked:false,data:[fileName+"."+suffix,size,createData],cssClass:cssClassStr};
	    var arow={checked:false,data:[ahrf_file,size,createData]};
	    dtrows.push(arow);
	  }
  }
  dbopts.data.rows = dtrows;
  //构建datatable
  var objDatatable = $('<div id="div_datatable"></div>');
  objDatatable.datatable(dbopts);  
  objDatatable.find("table").addClass("table-bordered table-striped");
	//添加到dglist中
  _objList.append(objDatatable);
}

//缩略图显示查询结果
function showSearchResultThumb(){
	var _objThumb = $('#dgThumb');
	_objThumb.css("display","block");
	var thumbHtmlStr = '';
	if(searchResultJsonData!=null && searchResultJsonData.rows!=null && searchResultJsonData.rows.length>0){
	  thumbHtmlStr += '<section class="cards">';
	  var jsonRows = searchResultJsonData.rows;
	  var len = jsonRows.length;
	  for(var i=0;i<len;i++){
	    var fileName = jsonRows[i]["name"];
	    var suffix = jsonRows[i]["suffix"];
	    var fileFull = fileName+"."+suffix;
	    var desc = jsonRows[i]["desc"];
	    thumbHtmlStr += '  <div class="col-md-4 col-sm-6 col-lg-2">';
	    thumbHtmlStr += '    <div class="card">';
		  thumbHtmlStr += '      <div class="media-wrapper">';
		  thumbHtmlStr += '        <img src="<%=path%>/analApp/images/excel.png" alt="">';
	    thumbHtmlStr += '      </div>';   
		  thumbHtmlStr += '        <span class="caption">'+desc+'</span>';
	    thumbHtmlStr += '      <div class="media-wrapper">';
		  thumbHtmlStr += '        <a href="###" class="card-heading" onclick="showFile(\''+fileFull+'\');"><strong>'+fileFull+'</strong></a>';
	    thumbHtmlStr += '      </div>';   
		  thumbHtmlStr += '    </div>';
		  thumbHtmlStr += '  </div>';   
		}
	  thumbHtmlStr += '</section>';
	}
    
  _objThumb.html(thumbHtmlStr);
}

//获得输入的查询内容
function getInputSearchFileStr(){
  var searchedStr = ($("#idSearchFile").val()==searchTxt)?"":$("#idSearchFile").val();
  return searchedStr;
}

//查询结果中，当点击了某个文件，触发此操作
function showFile(fileName){
	alert("您点击了："+fileName);
}
</script>
</html>