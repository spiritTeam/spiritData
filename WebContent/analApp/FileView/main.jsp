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
            <img src="<%=path%>/analApp/images/file_list.png" style="height:45px;width:45px;" onclick="showSearchResult('LIST');" title="列表预览" alt="列表预览"/>
          </a>
          <a href="#" class="">
            <img src="<%=path%>/analApp/images/file_thumb.png" style="height:45px;width:45px;" onclick="showSearchResult('THUMB');" title="缩略图预览" alt="缩略图预览"/>
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
      alert("您输入了："+getInputSearchFileStr());
      showSearchResult("LIST");
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
    alert("您输入了："+getInputSearchFileStr());
    showSearchResult("LIST");
  });
}


//显示查询结果
function showSearchResult(showType){
    $('#dgList').css("display","none");
    $('#dgThumb').css("display","none");
    
	if(showType == "LIST"){
        showSearchResultList();
	}else if(showType == "THUMB"){
        showSearchResultThumb();
	}else{
        showSearchResultList();
	}
}

function showSearchResultList(){
  $('#dgList').css("display","block");
  var dbopts={
    customizable: true, 
    checkable: true,
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
        {width:'250',text:'文件名',type:'string',flex: false,colClass:'text-left dg_border_left_1',cssClass:'text-left dg_th_font_bold'},
        {width: 100,text:'大小',type:'number',flex: true,colClass:'text-right dg_border_left_1 ',cssClass:'text-right dg_th_font_bold'},
        {width:150,text:'创建日期',type:'date',flex: true,colClass:'text-left dg_border_left_1 ',cssClass:'text-left dg_th_font_bold',mergeRows: false}
      ]
    }
  };
	
  //异步查询文件列表	
  var searchParam={};
  var resultJsonData={};
  var url="<%=path%>/analApp/demoData/filelist.json";
  $.ajax({type:"post", async:true, url:url, data:searchParam, dataType:"text",
    success:function(jsonStr){
      try{
        var jsonData = str2JsonObj(jsonStr);
        var dtrows =[];
        var jsonRows = jsonData.rows;
        var len = jsonRows.length;
        for(var i=0;i<len;i++){
          var fileName = jsonRows[i]["name"];
          var suffix = jsonRows[i]["suffix"];
          var size = jsonRows[i]["size"];
          var createData = jsonRows[i]["createData"];
          var cssClassStr= i%2==0?"":"dg_td_bgcolor_lightblue";
          var arow={checked:false,data:[fileName+"."+suffix,size,createData],cssClass:cssClassStr};
          dtrows.push(arow);
        }

        dbopts.data.rows = dtrows;
        //显示结果
        $('#dgList').datatable(dbopts).on('sort.zui.datatable', function(event){}); 
      }catch(e){
        alert("failed to parse jsonStr="+jsonStr+" ."+e.message);
        return;
      }
    },
    error:function(errorData){
      alert("get data err");
      if (errorData ){
        var errData = errorData.responseText;
        //errData = eval(errData);
        alert(errData);
        try{ 
          var objData = eval("("+errData+")");  
          dbopts.data.rows = objData.rows;
          //显示结果
          $('#dgList').datatable(dbopts).on('sort.zui.datatable', function(event){});  
        }catch(e){
          alert(e.message); 
        }
      }
    }
  });	
}

//缩略图显示查询结果
function showSearchResultThumb(){
	var _objThumb = $('#dgThumb');
	_objThumb.css("display","block");
	var thumbHtmlStr = '';
    thumbHtmlStr += '<section class="cards">';
	for(var i=1;i<21;i++){
	    thumbHtmlStr += '  <div class="col-md-4 col-sm-6 col-lg-3">';
	    thumbHtmlStr += '    <a href="###" class="card">';
	    thumbHtmlStr += '      <img src="<%=path%>/analApp/images/img2.jpg" alt="">';
	    thumbHtmlStr += '      <span class="caption">关于图片的说明'+i+'</span>';
	    thumbHtmlStr += '      <strong class="card-heading">图片标题飞'+i+'</strong>';
	    thumbHtmlStr += '    </a>';
	    thumbHtmlStr += '  </div>';		
	}
    thumbHtmlStr += '</section>';      
    
    _objThumb.html(thumbHtmlStr);
	
}

//获得输入的查询内容
function getInputSearchFileStr(){
  var searchedStr = ($("#idSearchFile").val()==searchTxt)?"":$("#idSearchFile").val();
  return searchedStr;
}
</script>
</html>