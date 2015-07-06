<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%
  String path = request.getContextPath();
  String fileId = request.getParameter("fileId");
  String fileName = request.getParameter("fileName");
  String _OSNAME = System.getProperties().getProperty("os.name");
  //if (_OSNAME.toUpperCase().startsWith("WINDOW")) {
  //  fileId = new String(fileId.getBytes("ISO-8859-1"),"UTF-8");
  //  fileName = new String(fileName.getBytes("ISO-8859-1"),"UTF-8");
  //}
  //System.out.println("fileId="+fileId+"  fileName="+fileName);
%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<!-- 显示文件的详细内容，以DATAGRID表形式显示，一个SHEET一张表，如果有多个SHEET则用TABS显示 -->
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="cache-control" content="no-cache"/>
<meta http-equiv="pragma" content="no-cache"/>
<meta http-equiv="expires" content="0"/>

<jsp:include page="/common/sysInclude.jsp" flush="true"/>

<title>文件详细数据显示页面</title>
</head>
<style>
.div2{padding:2px;border:0px solid #ddd;width:98%;height:98%;margin:0 auto;overflow-y:auto;overflow-x:hidden;}
.border_no{border:0px solid red; }
.padding_top5{padding-top:5px;}
.head1{font-weight:bold;font-size:18px;}
.div_center{margin:0 auto;text-align:center;}
//easyui
.datagrid-header-row td{background-color:blue;color:#fff}
</style>
<body class="padding_top5 div_center" style="background-color:#FFFFFF;width:1000px;">
  <div id="div_fileName" class="border_no head1" style="padding:5px;width:960px;">    
  </div>
  <div id="div_main" class="div_center" style="width:960px;">  
    <div id="div_tabs" class="easyui-tabs" style="width:950px;height:500px;display:none;"></div>  
  </div>           
</body>

<script>
//变量定义
var _fileId; //文件ID
var _fileName; //文件名称
var searchResultJsonData = null; //保存查询后的结果
//主函数
$(function() {
	_fileId = "<%=fileId%>";
	_fileName = "<%=fileName%>";
	$("#div_fileName").html("文件名："+_fileName+"");
	initDivMain();
	startSearch();
});

//初始化DIV_MAIN的位置，使其居中
function initDivMain(){
	$("#div_main").css({"left":(($(window).width()-$("#div_main").width())/2),"padding-left":12});
}

//查询指定文件ID的数据 
function startSearch(){
  //异步查询文件列表
  var searchParam={"fileId":_fileId,"pageNumber":1,"pageSize":15};
  var url="<%=path%>/fileview/getFilePageData.do";
  $.ajax({type:"post", async:true, url:url, data:searchParam, dataType:"text",
    success:function(jsonStr){
      try{
    	  //alert(jsonStr);
        searchResultJsonData = str2JsonObj(jsonStr); 
        //if(searchResultJsonData.totalSheet==1){
        //	showDatagrid(); //只显示表格
        //}else if(searchResultJsonData.totalSheet>1){
        //	showTabs(); //先画TABS，然后每个TAB里面再显示表格
        //}
        if(searchResultJsonData && searchResultJsonData.totalSheet>0){
        	showTabs(); //先画TABS，然后每个TAB里面再显示表格
        }
      }catch(e){
        showAlert("解析异常", "查询结果解析成JSON失败：</br>"+(e.message)+"！<br/>", "error", function(){});
      }
    },
    error:function(errorData){
      showAlert("查询异常", "查询失败：</br>"+(errorData?errorData.responseText:"")+"！<br/>", "error", function(){});
    }
  }); 
}

//显示单个表格数据 
function showDatagrid(){
	//$("#div_main").datagrid(searchResultJsonData.SheetDataList[0]);
	//return;
	var objDatagrid = $('<div id="div_datatable" style="width:950px;"></div>');
	$("#div_main").append(objDatagrid);
  initDatagrid(objDatagrid,searchResultJsonData.SheetDataList[0]);
}

//显示TABS 
function showTabs(){
  var objTabs = $('#div_tabs');
  objTabs.css("display","block");
  //构造tab
  for(var i=0;i<searchResultJsonData.totalSheet;i++){
	  //var objTab = $('<div id="div_datatable" style="width:950px;"></div>');
	  var aSheetDataMap = searchResultJsonData.SheetDataList[i];
	  var fileInfoMap = aSheetDataMap.fileInfoMap;
	  var fileDataMap = aSheetDataMap.fileDataMap;
	  var tabtitle = fileInfoMap.title;
	  var tabid = "tab"+i;
	  objTabs.tabs('add',{
	    title: tabtitle,
	    content: '<div id='+tabid+' style="width:98%">aaa</div>',
	    closable: false
	  });		  	  
  }   
  objTabs.tabs("select", 0);
  
  //加载datagrid
  for(var i=0;i<searchResultJsonData.totalSheet;i++){
	  var tabid = "tab"+i;
	  var objDatagrid = $("#"+tabid);
	  initDatagrid(objDatagrid,searchResultJsonData.SheetDataList[i]);
  }  
}

//初始化datagrid
function initDatagrid(objDatagrid,dgDataJson){
  try{
    //设置样式
    objDatagrid.addClass("div_center");
    //表名、长宽、列等设置
    dgDataJson["fileInfoMap"].title=""; //不显示表头
	  objDatagrid.datagrid(dgDataJson["fileInfoMap"]);
    var opts = objDatagrid.datagrid('options');
    //设置分页
    var pager = objDatagrid.datagrid('getPager');  
    pager.pagination({ 
      pageSize: 15,//每页显示的记录条数，默认为10 
      pageList: [15,20,30,50,100],//可以设置每页记录条数的列表 
      beforePageText: '第',//页数文本框前显示的汉字 
      afterPageText: '页    共 {pages} 页', 
      displayMsg: '当前显示 {from} - {to} 条记录   共 {total} 条记录', 
      /*onBeforeRefresh:function(){
          $(this).pagination('loading');
          alert('before refresh');
          $(this).pagination('loaded');
      }*/ 
      onSelectPage: function (pageNumber, pageSize) {
        //showAlert("点击分页",'onSelectPage pageNumber:' + pageNumber + ',pageSize:' + pageSize,"info");
        opts.pageNumber = pageNumber;
        opts.pageSize = pageSize;
        $(pager).pagination('refresh',{
          pageNumber:pageNumber,
          pageSize:pageSize
        });
        //showAlert(dgDataJson.tableName+"   "+dgDataJson.selCols);
        getTablePageData(dgDataJson.tableName,dgDataJson.selCols,pageNumber, pageSize,objDatagrid);
      }
    });

    //getData(1,15); 
    //加载数据
    
    objDatagrid.datagrid("loadData", dgDataJson["fileDataMap"]);		
	}catch(e){
		//showAlert(e.message);
	}
}

//请求分页查询，获取返回结果，在datagrid中显示
function getTablePageData(tableName,selCols,pageNumber, pageSize,objDatagrid){
	//异步查询文件列表
	var searchParam={"tableName":tableName,"selCols":selCols,"pageNumber":pageNumber,"pageSize":pageSize};
	var url="<%=path%>/fileview/getTablePageData.do";
	$.ajax({type:"post", async:true, url:url, data:searchParam, dataType:"text",
	  success:function(jsonStr){
	    try{
	      //alert(jsonStr);
	      searchResultJsonData = str2JsonObj(jsonStr); 
	      objDatagrid.datagrid("loadData", searchResultJsonData); 
	    }catch(e){
	      showAlert("解析异常", "查询结果解析成JSON失败：</br>"+(e.message)+"！<br/>", "error", function(){});
	    }
	  },
	  error:function(errorData){
	    showAlert("查询异常", "查询失败：</br>"+(errorData?errorData.responseText:"")+"！<br/>", "error", function(){});
	  }
	});
}




</script>
</html>