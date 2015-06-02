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
<!-- 加载analApp的JS -->
<script src="<%=path%>/resources/js/visit.utils.js"></script>
<script src="<%=path%>/analApp/js/analApp.view.js"></script>

<title>未读报告列表页面</title>
</head>
<style>
.div{padding:2px;border:0px solid #ddd;width:98%;height:98%;margin:0 auto;overflow-y:auto;overflow-x:hidden;}
.border_no{border:0px solid red; }
.padding_top5{padding-top:5px;}
//easyui
.datagrid-header-row td{background-color:blue;color:#fff}
</style>
<body class="padding_top5" style="background-color:#FFFFFF">
  <div id="divtb" class="div border_no">
    <table id="tb_unReadReport" class="easyui-datagrid" style="overflow-x:hidden;overflow-y:scroll;width:480px;height:520px;"
           data-options="singleSelect:true,collapsible:true">
      <thead>
        <tr>
          <th data-options="field:'reportName',width:290,halign:'center',align:'left'"><label style='font-weight: bolder;color:blue;'>报告名</label></th>
          <th data-options="field:'size',width:60,halign:'center',align:'right'"><label style='font-weight: bolder;color:blue;'>大小</label></th>
          <th data-options="field:'createDate',width:100,halign:'center',align:'left'"><label style='font-weight: bolder;color:blue;'>生成日期</label></th>
        </tr>
      </thead>
    </table>    
  </div>           
</body>

<script>
//主函数
$(function() {
  $('#tb_unReadReport').datagrid({
    rowStyler: function(index,row){
      if (index % 2 > 0) {
        return 'background-color:#F6F6F6;';//和一般的样式写法一样
      }
    },
    onClickCell: function (rowIndex, field, value) {
      //alert(rowIndex+" "+field+"  "+value);  
      //当点击了某个新报告后，此报告名的字体变色，表示已经不是未读的报告
      if(field=="reportName"){
    	  showReport(value);
    	  var _objRepName = $('#divtb').find("table[class='datagrid-btable']").find("td[field='reportName']")[rowIndex];
    	  $(_objRepName).find("font").css("color","green");
      }
    }
  });

  var jsonData = getMainPage().newReportJson;
  refreshNewReportTable(jsonData);  

  //对每个报告名称加上点击事件  
  $('#divtb').find("table[class='datagrid-btable']")
  .find("td[field='reportName']").each(function(i,domEle){
	  var _this = $(this);
	  var _div = _this.find("div");
	  var repId = jsonData.rows[i]["reportId"];
	  //var ahrf_file = '<a href="###" onclick="showReport(\''+repId+'\',\''+i+'\');"><font style="font-weight: bolder;color:red;">'+_div.text()+'</font></a>';
	  var ahrf_file = '<font style="font-weight: bolder;color:red;">'+_div.text()+'</font>';
	  _div.empty();
	  _div.append(ahrf_file);
	  _this.mouseover(function(){_this.css("cursor","pointer");})
	       .mouseout(function(){_this.css("cursor","default");});
	});
  
});


/**
 * 当有新数据来时，更新报告列表
 */
function refreshNewReportTable(jsonData){
  //jsonData = {"total":100,"rows":[{"reportName":"report1","size":"1M","createDate":"2015-01-02","reportId":"12345"}]};
	$('#tb_unReadReport').datagrid('loadData',jsonData);
}


</script>
</html>