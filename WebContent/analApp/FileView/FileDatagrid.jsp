<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%
  String path = request.getContextPath();
  String fileId = request.getParameter("fileId");
  fileId = new String(fileId.getBytes("ISO-8859-1"),"UTF-8");
  //System.out.println("fileId="+fileId);
  String fileName = request.getParameter("fileName");
  fileName = new String(fileName.getBytes("ISO-8859-1"),"UTF-8");
  //System.out.println("fileName="+fileName);
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
.div{padding:2px;border:0px solid #ddd;width:98%;height:98%;margin:0 auto;overflow-y:auto;overflow-x:hidden;}
.border_no{border:0px solid red; }
.padding_top5{padding-top:5px;}
.head1{font-weight:bold;font-size:16px;}
.div_center{margin:0 auto;text-align:center;}
//easyui
.datagrid-header-row td{background-color:blue;color:#fff}
</style>
<body class="padding_top5 div_center" style="background-color:#FFFFFF;width:1000px;">
  <div id="div_fileName" class="div border_no head1">    
  </div>
  <div id="div_main" class="div div_center" style="width:960px;">  
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
	startSearch();
});

//查询指定文件ID的数据 
function startSearch(){
  //异步查询文件列表  
  var searchParam={"fileId":_fileId};
  var url="<%=path%>/fileview/getFileData.do";
  $.ajax({type:"post", async:true, url:url, data:searchParam, dataType:"text",
    success:function(jsonStr){
      try{
    	  //alert(jsonStr);
        searchResultJsonData = str2JsonObj(jsonStr); 
        if(searchResultJsonData.totalSheet==1){
        	showDatagrid(); //只显示表格
        }else if(searchResultJsonData.totalSheet>1){
        	showTabs(); //先画TABS，然后每个TAB里面再显示表格
        }
      }catch(e){
        $.messager.alert("解析异常", "查询结果解析成JSON失败：</br>"+(e.message)+"！<br/>", "error", function(){});
      }
    },
    error:function(errorData){
      $.messager.alert("查询异常", "查询失败：</br>"+(errorData?errorData.responseText:"")+"！<br/>", "error", function(){});
    }
  }); 
}

//显示表格数据 
function showDatagrid(){
	//$("#div_main").datagrid(searchResultJsonData.SheetDataList[0]);
	//return;
	var objDatagrid = $('<div id="div_datatable" style="width:950px;"></div>');
	$("#div_main").append(objDatagrid);
	objDatagrid.addClass("div_center");
	objDatagrid.datagrid(searchResultJsonData.SheetDataList[0]["fileInfoMap"]);
	objDatagrid.datagrid("loadData", searchResultJsonData.SheetDataList[0]["fileDataMap"]);
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
	  objDatagrid.addClass("div_center");
	  objDatagrid.datagrid(searchResultJsonData.SheetDataList[i]["fileInfoMap"]);
	  objDatagrid.datagrid("loadData", searchResultJsonData.SheetDataList[i]["fileDataMap"]);
  }
  
}




</script>
</html>