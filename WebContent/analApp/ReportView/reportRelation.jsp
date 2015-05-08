<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%
  //报告关系展示页面
  String path = request.getContextPath();
  String reportIdStr = request.getParameter("reportId");
  //输入中文后需要做转码，否则会出现乱码
  reportIdStr = new String(reportIdStr.getBytes("ISO-8859-1"),"UTF-8");
  //System.out.println("reportId="+reportId);
%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="cache-control" content="no-cache"/>
<meta http-equiv="pragma" content="no-cache"/>
<meta http-equiv="expires" content="0"/>
<jsp:include page="/common/sysInclude.jsp" flush="true"/>

<link rel="stylesheet" type="text/css" href="<%=path%>/resources/plugins/spiritui/themes/default/pageFrame.css"/>

<script type="text/javascript" src="<%=path%>/resources/plugins/flot/excanvas.min.js"></script>
<script type="text/javascript" src="<%=path%>/resources/plugins/flot/jquery.flot.js"></script>
<script type="text/javascript" src="<%=path%>/resources/plugins/flot/jquery.flot.pie.js"></script>
<script type="text/javascript" src="<%=path%>/resources/plugins/flot/jquery.flot.categories.js"></script>
<script type="text/javascript" src="<%=path%>/resources/plugins/Chart.min.js"></script>

<script type="text/javascript" src="<%=path%>/resources/plugins/spiritui/jq.spirit.pageFrame.js"></script>
<!-- ECharts单文件引入 -->
<script src="<%=path%>/resources/plugins/echarts-2.2.1/echarts.js"></script>

<title>报告关系图</title>
</head>

<style>
body {
  background-color:#fff;
}
#sideFrame {
  width:270px; padding-left:20px; position:fixed;
}
#rTitle {
  height:40px; padding:10px; padding-left: 30px; font-size:28px; font-weight:bold;
}
.rptSegment {
  border:1px solid #E6E6E6;
  margin-bottom:15px;
}
.segTitle {
  height:30px; border-bottom:1px solid #E6E6E6;padding: 3px 0 0 5px;
}
.segTitle span {
  width:40px; font-size:24px; font-weight:bold;
}
.subTitle {
  font-size:18px; font-weight:bold; padding:3px;
}
.segContent{padding-left:5px;padding-right:5px;}
</style>

<body style="background-color:#FFFFFF">
<!-- 头部:悬浮 -->
<div id="topSegment" style="display:none;">
  <div id="rTitle">报告关系导图</div>
</div>

<!-- 脚部:悬浮 -->
<div id="mainSegment" style="padding:10px 10px 0 10px;">

<div id="sideFrame">
  <div id="catalogTree" style="border:1px solid #E6E6E6; width:0px; "></div>
</div>
 
<div id="reportFrame">
  <div id="seg1Title" class="subTitle" style="width:480px;"><span>1、报告关系导图：</span></div>
  <table style="width:480px;">
    <tr><td><div id="echart_relation" style="width:440px;height:350px;border:1px solid #ccc;padding:10px;"></div></td></tr>
  </table>
        
  <div id="seg1Title" class="subTitle"  style="width:480px;"><span>2、报告关系表：</span></div>
  <div id="div_tabs" class="easyui-tabs" style="width:460px;height:300px;margin:0px 0px 10px 0px;"> 
    <div id="div_tab_reprep" title="报告-报告关系表" style="padding:5px;display:block;">
      <table id="tb_reprep" class="easyui-datagrid" style="width:440px;"
        data-options="singleSelect:true,collapsible:true,url:'segdemoA1_2_b.json',method:'get'">
        <thead>
          <tr>
            <th data-options="field:'src',width:120,halign:'center',align:'left'">报告名</th>
            <th data-options="field:'dest',width:120,halign:'center',align:'left'">关联报告名</th>
            <th data-options="field:'rel',width:180,halign:'center',align:'left'">关联关系</th>
          </tr>
        </thead>
      </table>
    </div> 
    <div id="div_tab_repfile" title="报告-文件关系表" closable="false" style="overflow:auto;padding:5px;display:block;">
      <table id="tb_repfile" class="easyui-datagrid" style="width:440px;"
        data-options="singleSelect:true,collapsible:true,url:'segdemoA1_2_b.json',method:'get'">
        <thead>
          <tr>
            <th data-options="field:'src',width:120,halign:'center',align:'left'">报告名</th>
            <th data-options="field:'dest',width:120,halign:'center',align:'left'">关联文件名</th>
            <th data-options="field:'rel',width:180,halign:'center',align:'left'">关联关系</th>
          </tr>
        </thead>
      </table>
    </div>
  </div>  
<!-- 
        <div id="seg1Title" class="subTitle"  style="width:450px;"><span>2、报告关系表：</span></div>
        <table id="tb_reprep2" class="easyui-datagrid" style="width:440px;"
           data-options="singleSelect:true,collapsible:true,url:'segdemoA1_2_b.json',method:'get'">
          <thead>
            <tr>
              <th data-options="field:'src',width:120,halign:'center',align:'left'">报告名</th>
              <th data-options="field:'dest',width:120,halign:'center',align:'left'">关联报告名</th>
              <th data-options="field:'rel',width:180,halign:'center',align:'left'">关联关系</th>
            </tr>
          </thead>
            <tr><td>报告1</td><td>报告2</td><td>父报告</td></tr>
            <tr><td>报告1</td><td>报告3</td><td>父报告</td></tr>
            <tr><td>报告1</td><td>报告4</td><td>子报告</td></tr>
            <tr><td>报告2</td><td>报告3</td><td> -- </td></tr>
            <tr><td>报告2</td><td>报告4</td><td> -- </td></tr>
            <tr><td>报告3</td><td>报告4</td><td>子报告</td></tr>
        </table>
        
        <div id="seg1Title" class="subTitle"  style="width:450px;"><span>3、报告-文件关系表：</span></div>
        <table id="tb_repfile2" class="easyui-datagrid" style="width:440px;"
           data-options="singleSelect:true,collapsible:true,url:'segdemoA1_2_b.json',method:'get'">
          <thead>
            <tr>
              <th data-options="field:'src',width:120,halign:'center',align:'left'">报告名</th>
              <th data-options="field:'dest',width:120,halign:'center',align:'left'">关联文件名</th>
              <th data-options="field:'rel',width:180,halign:'center',align:'left'">关联关系</th>
            </tr>
          </thead>
            <tr><td>报告1</td><td>文件2</td><td>全部引用该文件</td></tr>
            <tr><td>报告1</td><td>文件3</td><td>全部引用该文件</td></tr>
            <tr><td>报告1</td><td>文件4</td><td>部分引用该文件</td></tr>
            <tr><td>报告2</td><td>文件5</td><td>全部引用该文件</td></tr>
            <tr><td>报告2</td><td>文件3</td><td>部分引用该文件</td></tr>
            <tr><td>报告3</td><td>文件5</td><td>全部引用该文件</td></tr>
        </table>
</div>
-->        
</body>

<script language="javascript">
//主窗口参数
var INIT_PARAM = {
  pageObjs: {
    topId: "topSegment1",
    mainId: "mainSegment"
  },
  page_width: -1,
  page_height: -1,
  top_shadow_color:"#E6E6E6",
  top_height: 0,
  top_peg: false,
  myInit: initPos,
  myResize: initPos
};

function initPos() {
  $("#reportFrame").spiritUtils("setWidthByViewWidth", $("body").width()-$("#sideFrame").spiritUtils("getViewWidth"));
  $("#sideFrame").css("left", $("#reportFrame").width());
}

//主函数
$(function() {
  var initStr = $.spiritPageFrame(INIT_PARAM);
  if (initStr) {
    $.messager.alert("页面初始化失败", initStr, "error");
    return ;
  };
  
  //初始化tabs
  initTabs();
  
  //开始查找报告关联信息
  searchRelation();
});

/**
 * 初始化echarts
 */
require.config({
  paths: {
    echarts: '<%=path%>/resources/plugins/echarts-2.2.1',
    zrender: '<%=path%>/resources/plugins/echarts-2.2.1/zrender'
  }
});

//初始化ECHARTS，按需加载JS
require(
  [
    'echarts',
    'echarts/chart/force', //力导向图，按需加载
    'echarts/chart/chord' //和弦图，按需加载
  ]
);

//初始化tabs
function initTabs(){
  $('#div_tabs').tabs({  
    border:true,  
    onSelect:function(title){  
      //alert(title+' is selected');  
    }  
  });  
}

//根据reportId异步请求获取报告关联信息
function searchRelation(){
  //获取父窗口传来的报告ID
  var reportId = <%=reportIdStr%>;
  //异步查询文件列表  
  var searchParam={"reportId":reportId};
  //alert("查询参数："+allFields(searchParam));
  var url="<%=path%>/reportview/searchReportRelation.do";
  $.ajax({type:"post", async:true, url:url, data:searchParam, dataType:"text",
    success:function(jsonStr){
      try{
        var jsonData = str2JsonObj(jsonStr); 
        //画关系图
        drawRelationForce(jsonData.forceData);
        //画关系表
        drawRelationTabs(jsonData);
      }catch(e){
        $.messager.alert("解析异常", "查询结果解析成JSON失败：</br>"+(e.message)+"！<br/>", "error", function(){});
      }
    },
    error:function(errorData){
      $.messager.alert("查询异常", "查询失败：</br>"+(errorData?errorData.responseText:"")+"！<br/>", "error", function(){});
    }
  }); 
}

//画报告关系图
function drawRelationForce(forceData){
	 //异步请求报告关系信息
  try{
	   var ecConfig = require('echarts/config');
	   var myChart = require('echarts').init(document.getElementById('echart_relation')); 
	   var opt_relation = {
		    title : {
		      text: '主报告：'+forceData.title,
		      subtext: ''+forceData.subTitle,
		      x:'right',
		      y:'bottom'
		    },
		    tooltip : {
		      trigger: 'item',
		      formatter: '{a} : {b}'
		    },
		    toolbox: {
		      show : true,
		      feature : {
		        restore : {show: true},
		        magicType: {show: true, type: ['force', 'chord']},
		        saveAsImage : {show: true}
		      }
		    },
		    legend: {
		      x: 'left',
		      data:['报告','文件']
		    },
		    series : [{
		      type:'force',
		      name : "报告关系",
		      ribbonType: false,
		      categories : [
		        {name: '关系'},
		        {name: '报告'},
		        {name:'文件'}
		      ],
		      itemStyle: {
		        normal: {
		          label: {
		            show: true,
		            textStyle: {
		              color: '#333'
		            }
		          },
		          nodeStyle : {
		            brushType : 'both',
		            borderColor : 'rgba(255,215,0,0.4)',
		            borderWidth : 1
		          },
		          linkStyle: {
		            type: 'curve'
		          }
		        },
		        emphasis: {
		          label: {
		            show: false
		            // textStyle: null      // 默认使用全局文本样式，详见TEXTSTYLE
		          },
		          nodeStyle : {
		            //r: 30
		          },
		          linkStyle : {}
		        }
		      },
		      useWorker: false,
		      minRadius : 15,
		      maxRadius : 25,
		      gravity: 1.1,
		      scaling: 1.1,
		      roam: 'move',
		      nodes:forceData.nodes,
		      links : forceData.links
		    }]   
		  };
	
	   myChart.setOption(opt_relation);
	
	   myChart.on(ecConfig.EVENT.CLICK, focus);
	   myChart.on(ecConfig.EVENT.FORCE_LAYOUT_END, function () {
	     console.log(myChart.chart.force.getPosition());
	   });
  }catch(e){
	   $.messager.alert("画关系图异常", "画关系图失败：</br>"+(e?e.message:"")+"！<br/>", "error", function(){});
  }
}

//画关系表
function drawRelationTabs(jsonData){
	//加载报告-报告关系表
	$('#tb_reprep').datagrid('loadData',jsonData.tbRepRep);
	//加载报告-报文件关系表
	  $('#tb_repfile').datagrid('loadData',jsonData.tbRepFile);
}

//点击事件
function focus(param) {
    var data = param.data;
    var links = option.series[0].links;
    var nodes = option.series[0].nodes;
    if (
        data.source !== undefined
        && data.target !== undefined
    ) { //点击的是边
        var sourceNode = nodes.filter(function (n) {return n.name == data.source})[0];
        var targetNode = nodes.filter(function (n) {return n.name == data.target})[0];
        console.log("选中了边 " + sourceNode.name + ' -> ' + targetNode.name + ' (' + data.weight + ')');
    } else { // 点击的是点
        console.log("选中了" + data.name + '(' + data.value + ')');
    }
}
</script>
</html>
