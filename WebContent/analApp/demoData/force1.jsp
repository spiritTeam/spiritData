<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%
  String path = request.getContextPath();
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

<title>DEMO报告关系图</title>
</head>

<style>
body {
  background-color:#fff;
}
#sideFrame {
  width:270px; padding-left:20px; position:fixed;
}
#rTitle {
  height:40px; padding:10px; padding-left: 30px; font-size:36px; font-weight:bold;
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
<div id="topSegment">
  <div id="rTitle">报告关系导图</div>
</div>

<!-- 脚部:悬浮 -->
<div id="mainSegment" style="padding:10px 10px 0 10px;">


<div id="sideFrame">
  <div id="catalogTree" style="border:1px solid #E6E6E6; width:0px; "></div>
</div>

 
<div id="reportFrame">
<div class="rptSegment">
    <ul>
      <li>
        <div id="seg1Title" class="segTitle"><span>报告关系导图：</span></div>
        <div style="padding:3px 0 3px 5px;"><table>
          <tr><td><div id="echart_relation" style="width:900px;height:500px;border:1px solid #ccc;padding:10px;"></div></td></tr>
        </table></div>
      </li>
      <li>
        <div id="seg1Title" class="segTitle"><span>报告关系表：</span></div>
        <div style="padding:3px 0 3px 5px;">
          <table class="easyui-datagrid" style="width:920px;"
           data-options="singleSelect:true,collapsible:true,url:'segdemoA1_2_b.json',method:'get'">
          <thead>
            <tr>
              <th data-options="field:'fl',width:350,halign:'center',align:'left'">报告名</th>
              <th data-options="field:'num',width:350,halign:'center',align:'left'">关联报告名</th>
              <th data-options="field:'ver',width:200,halign:'center',align:'left'">关联关系</th>
            </tr>
          </thead>
            <tr>
              <td>报告1</td>
              <td>报告2</td>
              <td>关系1</td>
            </tr>
        </table>
        </div>
      </div>
      </li>
    </ul>
</div>
</body>

<script language="javascript">
//主窗口参数
var INIT_PARAM = {
  pageObjs: {
    topId: "topSegment",
    mainId: "mainSegment"
  },
  page_width: -1,
  page_height: -1,
  top_shadow_color:"#E6E6E6",
  top_height: 60,
  top_peg: false,
  myInit: initPos,
  myResize: initPos
};

function initPos() {
  $("#reportFrame").spiritUtils("setWidthByViewWidth", $("body").width()-$("#sideFrame").spiritUtils("getViewWidth"));
  $("#sideFrame").css("left", $("#reportFrame").width());
}

/**
 * echarts
 */
// 路径配置
require.config({
    paths: {
        echarts: '<%=path%>/resources/plugins/echarts-2.2.1',
        zrender: '<%=path%>/resources/plugins/echarts-2.2.1/zrender'
    }
});

//主函数
$(function() {
var initStr = $.spiritPageFrame(INIT_PARAM);
if (initStr) {
  $.messager.alert("页面初始化失败", initStr, "error");
  return ;
};
});

//使用
require(
 [
     'echarts',
     'echarts/chart/force' //力导向图，按需加载
 ],
 function (ec) {
   //画报告关系 图
   drawRelation(ec);
 } 
);

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


//画报告关系图
function drawRelation(ec){
	var myChart = ec.init(document.getElementById('echart_relation')); 
	var opt_relation = {
		    title : {
		        text: '主报告：报告1',
		        subtext: '',
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
		    series : [
		        {
		            type:'force',
		            name : "报告关系",
		            ribbonType: false,
		            categories : [
		                {
		                    name: '人物'
		                },
		                {
		                    name: '报告'
		                },
		                {
		                    name:'文件'
		                }
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
		            nodes:[
		                {category:0, name: '报告1', value : 10, label: '报告1\n（主要）'},
		                {category:1, name: '报告2',value : 2},
		                {category:1, name: '报告3',value : 3},
		                {category:1, name: '报告4',value : 7},
		                {category:2, name: '文件1',value : 5},
		                {category:2, name: '文件2',value : 8},
		                {category:2, name: '文件3',value : 9},
		                {category:2, name: '文件4',value : 4},
		                {category:2, name: '文件5',value : 4}
		            ],
		            links : [
		                {source : '报告2', target : '报告1', weight : 1, name: '女儿'},
		                {source : '报告3', target : '报告1', weight : 2, name: '父亲'},
		                {source : '报告4', target : '报告1', weight : 2},
		                {source : '文件1', target : '报告1', weight : 3, name: '合伙人'},
		                {source : '文件2', target : '报告1', weight : 1},
		                {source : '文件3', target : '报告1', weight : 6, name: '竞争对手'},
		                {source : '文件4', target : '报告1', weight : 1, name: '爱将'},
		                {source : '文件5', target : '报告1', weight : 1},
		                {source : '文件2', target : '报告3', weight : 1},
		                {source : '文件2', target : '报告4', weight : 1},
		                {source : '文件2', target : '文件1', weight : 1},
		                {source : '文件3', target : '文件2', weight : 6},
		                {source : '文件5', target : '文件2', weight : 1}
		            ]
		        }
		    ]   
		};
	
	myChart.setOption(opt_relation);
	
	myChart.on(ecConfig.EVENT.CLICK, focus);
	myChart.on(ecConfig.EVENT.FORCE_LAYOUT_END, function () {
	    console.log(myChart.chart.force.getPosition());
	});

}

</script>
</html>
