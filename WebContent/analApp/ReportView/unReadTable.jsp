<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%
  String path = request.getContextPath();
%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<!-- 未读报告列表页面，当点击报告菜单边的红点时弹出此页面 -->
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="cache-control" content="no-cache"/>
<meta http-equiv="pragma" content="no-cache"/>
<meta http-equiv="expires" content="0"/>

<jsp:include page="/common/sysInclude.jsp" flush="true"/>

<title>未读报告列表页面</title>
</head>
<style>
.div{padding:2px;border:1px solid #ddd;width:95%;height:95%;margin:0 auto;overflow-y:auto;}
.border_no{border:0px solid red; }
.padding_top5{padding-top:5px;}
</style>
<body class="padding_top5" style="background-color:#FFFFFF">
  <div class="div border_no">
    <table id="tb_unReadReport" class="easyui-datagrid" style="overflow-y:scroll;"
           data-options="singleSelect:true,collapsible:true">
      <thead>
        <tr>
          <th data-options="field:'reportName',width:240,halign:'center',align:'left'">报告名</th>
          <th data-options="field:'size',width:60,halign:'center',align:'center'">大小</th>
          <th data-options="field:'createDate',width:100,halign:'center',align:'center'">生成日期</th>
        </tr>
      </thead>
    </table>    
  </div>           
</body>

<script>
//定义常量变量
var INTERVAL_TIME = 10*1000; //定时去后台取数据的间隔时间
//主函数
$(function() {
	  //定时查询是否有新报告
	  searchNewReport();
	  setInterval(searchNewReport,INTERVAL_TIME);
});

/**
 * 异步请求后台，查找是否有新的报告生成
 */
function searchNewReport(){
  //异步查询是否有新增报表   
  var searchParam={"searchType":"fectchNewReport","searchStr":""};
    var url="<%=path%>/reportview/searchNewReport.do";
    alert("search new report");
    $.ajax({type:"post", async:true, url:url, data:searchParam, dataType:"text",
      success:function(jsonData){
        try{
        	alert("succ");
        	alert("searchNewReport():"+jsonData.rows.length);
          if(jsonData.rows.length>0){
            refreshNewReportTable(jsonData);
          }
        }catch(e){
          $.messager.alert("解析新报告异常", "查询结果解析成JSON失败：</br>"+(e.message)+"！<br/>", "error", function(){});
        }
      },
      error:function(errorData){
    	  alert("failed");
        $.messager.alert("查询新报告异常", "查询失败：</br>"+(errorData?errorData.responseText:"")+"！<br/>", "error", function(){});
      }
    });   
}

/**
 * 当有新数据来时，更新报告列表
 */
function refreshNewReportTable(jsonData){
	var obj = {"total":100,"rows":[{"reportName":"report1","size":"1M","createDate":"2015-01-02"}]};
	$('#tb_unReadReport').datagrid('loadData',jsonData);
}


function detail1(value,row,index) {
  if (value=='1') return '<div style="border:1px solid red; width:12px; height:12px;margin-left:5px;"></div>';
}
</script>
</html>