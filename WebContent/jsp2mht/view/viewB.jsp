<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
  String path = request.getContextPath();
%>
<!DOCTYPE HTML>
<HTML lang="en"><HEAD><meta content="IE=11.0000" 
http-equiv="X-UA-Compatible">
<meta name="Author" content="Chris Leonello">     
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">     
<meta name="keywords" content="chart, plot, graph, javascript, jquery, jqplot, charting, plotting, graphing"> 
<link href="<%=path %>/resources/plugins/jqplot/style.css"  rel="stylesheet" type="text/css">     
<script language="javascript" src="<%=path %>/resources/plugins/jqplot/jquery.min.js" type="text/javascript"></script>
<script language="javascript" src="<%=path %>/resources/plugins/jqplot/jquery.jqplot.min.js" type="text/javascript"></script>
<link href="<%=path %>/resources/plugins/jqplot/shCoreDefault.css" rel="stylesheet" type="text/css">   
<link href="<%=path %>/resources/plugins/jqplot/shThemejqPlot.css" rel="stylesheet" type="text/css">       
<link href="<%=path %>/resources/plugins/jqplot/jquery.jqplot.min.css" rel="stylesheet" type="text/css">   
<link href="<%=path %>/resources/plugins/jqplot/examples.css" rel="stylesheet" type="text/css">     
<TITLE>Rotated Axis Tick Labels | jqPlot</TITLE>
<!-- jqPlot renderers and plugins -->   
<script class="include" language="javascript" src="<%=path %>/resources/plugins/jqplot/jqplot.dateAxisRenderer.min.js" type="text/javascript"></script>
   
<script class="include" language="javascript" src="<%=path %>/resources/plugins/jqplot/jqplot.canvasTextRenderer.min.js" type="text/javascript"></script>
   
<script class="include" language="javascript" src="<%=path %>/resources/plugins/jqplot/jqplot.canvasAxisTickRenderer.min.js" type="text/javascript"></script>
   
<script class="include" language="javascript" src="<%=path %>/resources/plugins/jqplot/jqplot.categoryAxisRenderer.min.js" type="text/javascript"></script>
   
<script class="include" language="javascript" src="<%=path %>/resources/plugins/jqplot/jqplot.barRenderer.min.js" type="text/javascript"></script>
<script class="include" language="javascript" src="<%=path %>/resources/plugins/jqplot/plugins/jqplot.pieRenderer.js"></script>
<script class="include" language="javascript" src="<%=path %>/resources/plugins/jqplot/plugins/jqplot.donutRenderer.js"></script>
<script class="include" language="javascript" src="<%=path %>/resources/plugins/jqplot/plugins/jqplot.pointLabels.js"></script>
<script type="text/javascript">
function jqplotToImg(obj) {
  var newCanvas = document.createElement("canvas");
  newCanvas.width = obj.find("canvas.jqplot-base-canvas").width()+20;
  newCanvas.height = obj.find("canvas.jqplot-base-canvas").height()+10;
  var baseOffset = obj.find("canvas.jqplot-base-canvas").offset();
  // make white background for pasting
  var context = newCanvas.getContext("2d");
  context.fillStyle = "rgba(255,255,255,1)";
  context.fillRect(0, 0, newCanvas.width, newCanvas.height);
  obj.children().each(function () {
    // for the div's with the X and Y axis
    if ($(this)[0].tagName.toLowerCase() == 'div') {
      // X axis is built with canvas
      $(this).children("canvas").each(function() {
        var offset = $(this).offset();
        newCanvas.getContext("2d").drawImage(this,
          offset.left - baseOffset.left+20,
          offset.top - baseOffset.top
        );
      });
      // Y axis got div inside, so we get the text and draw it on the canvas
      $(this).children("div").each(function() {
        var offset = $(this).offset();
        var context = newCanvas.getContext("2d");
        context.font = $(this).css('font-style') + " " + $(this).css('font-size') + " " + $(this).css('font-family');
        context.fillStyle = $(this).css('color');
        context.fillText($(this).text(),
          offset.left - baseOffset.left+20,
          offset.top - baseOffset.top + $(this).height()
        );
      });
    } else if($(this)[0].tagName.toLowerCase() == 'canvas') {
      // all other canvas from the chart
      var offset = $(this).offset();
      newCanvas.getContext("2d").drawImage(this,
        offset.left - baseOffset.left+20,
        offset.top - baseOffset.top
      );
    }
  });
  // add the point labels
  obj.children(".jqplot-point-label").each(function() {
    var offset = $(this).offset();
    var context = newCanvas.getContext("2d");
    context.font = $(this).css('font-style') + " " + $(this).css('font-size') + " " + $(this).css('font-family');
    context.fillStyle = $(this).css('color');
    context.fillText($(this).text(),
      offset.left - baseOffset.left+20,
      offset.top - baseOffset.top + $(this).height()*3/4
    );
  });
  //add the data labels
  obj.children(".jqplot-data-label").each(function() {
    var offset = $(this).offset();
    var context = newCanvas.getContext("2d");
    context.font = $(this).css('font-style') + " " + $(this).css('font-size') + " " + $(this).css('font-family');
    context.fillStyle = $(this).css('color');
    context.fillText($(this).text(),
      offset.left - baseOffset.left+20,
      offset.top - baseOffset.top + $(this).height()*3/4
    );
  });
  // add the title
  obj.children("div.jqplot-title").each(function() {
    var offset = $(this).offset();
    var context = newCanvas.getContext("2d");
    context.font = $(this).css('font-style') + " " + $(this).css('font-size') + " " + $(this).css('font-family');
    context.textAlign = $(this).css('text-align');
    context.fillStyle = $(this).css('color');
    context.fillText($(this).text(),
      newCanvas.width / 2,
      offset.top - baseOffset.top + $(this).height()
    );
  });
  // add the legend
  obj.children("table.jqplot-table-legend").each(function() {
    var offset = $(this).offset();
    var context = newCanvas.getContext("2d");
    context.strokeStyle = $(this).css('border-top-color');
    context.strokeRect(
      offset.left - baseOffset.left+20,
      offset.top - baseOffset.top,
      $(this).width(),$(this).height()
    );
    context.fillStyle = $(this).css('background-color');
    context.fillRect(
      offset.left - baseOffset.left+20,
      offset.top - baseOffset.top,
      $(this).width(),$(this).height()
    );
  });
  // add the rectangles
  obj.find("div.jqplot-table-legend-swatch").each(function() {
    var offset = $(this).offset();
    var context = newCanvas.getContext("2d");
    context.fillStyle = $(this).css('background-color');
    context.fillRect(
      offset.left - baseOffset.left+20,
      offset.top - baseOffset.top,
      $(this).parent().width(),$(this).parent().height()
    );
  });
  obj.find("td.jqplot-table-legend").each(function() {
    var offset = $(this).offset();
    var context = newCanvas.getContext("2d");
    context.font = $(this).css('font-style') + " " + $(this).css('font-size') + " " + $(this).css('font-family');
    context.fillStyle = $(this).css('color');
    context.textAlign = $(this).css('text-align');
    context.textBaseline = $(this).css('vertical-align');
    context.fillText($(this).text(),
      offset.left - baseOffset.left+20,
      offset.top - baseOffset.top + $(this).height()/2 + parseInt($(this).css('padding-top').replace('px',''))
    );
  });
  // convert the image to base64 format
  return newCanvas.toDataURL("image/png");
}
</script>
<script class="code" type="text/javascript">
//柱图
var thisUrl=window.location.href;
var showDataMapJson;
var tickList;
var showDataList;
var labelList;
$(function(){
  var _url="<%=path%>/getAnalysusResult.do";
  $.ajax({
    async: false,
    type:"post",
    url: _url,
    dataType: "json",
    success: function (data){
    	showDataMapJson = data;
 			tickList=showDataMapJson.tickList;
 			showDataList = showDataMapJson.showDataList;
 			labelList = showDataMapJson.labelList;
    }                       
  });
});
function renderBar(){
	var k1=[showDataList[0].length];
	var k2=[showDataList[0].length];
	var k3=[showDataList[0].length];
	for(var o in showDataList){
		if(o==0){
			var rd = showDataList[o];
			for(var j in rd){
				k1[j]=parseInt(rd[j]);
			}
		}else if(o==1){
			var rd = showDataList[o];
      for(var j in rd){
        k2[j]=parseInt(rd[j]);
      }
		}else{
			var rd = showDataList[o];
      for(var j in rd){
        k3[j]=parseInt(rd[j]);
      }
		}
	}
  var ticks = tickList;
  var plot1 = $.jqplot('chart1', [k1, k2, k3], {
  seriesDefaults:{
    renderer:$.jqplot.BarRenderer,
    rendererOptions: {fillToZero: true}
  },
  series:[
    {label:labelList[0]},
    {label:labelList[1]},
    {label:labelList[2]}
  ],
  legend: {
    show: true,
    placement: 'outsideGrid'
  },
  //设置轴
  axes: {
    xaxis: {
      renderer: $.jqplot.CategoryAxisRenderer,
      ticks: ticks
    },
    yaxis: {
    	//是一个相乘因子
      pad: 1.05,
      //这个地方显示标签栏格式
      tickOptions: {formatString: '%d%'}
    }
  }
});
}
$(document).ready(function(){
	//饼图
  var data = [
    ['数学', 12],['语文', 9], ['Light Industry', 14], 
    ['Out of home', 16],['Commuting', 7], ['Orientation', 9]
  ];
  var plot1 = jQuery.jqplot ('chart3', [data], 
    { 
	    //seriesDefaults:如果有多个分类，这可通过该配置属性设置各个分类的共性属性
      seriesDefaults: {
        // Make this a pie chart.利用渲染器（这里是利用饼图PieRenderer）渲染现有图表  
        renderer: jQuery.jqplot.PieRenderer, 
        /** // 传给上个属性所设置渲染器的option对象，线状图的渲染器没有option对象，
                不同图表的Option配置对象请参见下面《jqPlot各个不同插件的Option对象设置》  
        */
        rendererOptions: {
          // Put data labels on the pie slices.
          // By default, labels show the percentage of the slice.
          //真实的展现切片标签
          showDataLabels: true
        }
      }, 
      legend: {
    	  //设置是否出现分类名称框（即所有分类的名称出现在图的某个位
    	  show:true,
    	  // 分类名称框出现位置, nw, n, ne, e, se, s, sw, w. (8个方位) 
    	  location: 'e' }
    }
  );
  renderBar();
});
function saveImg(id){
	$("#thisUrl").val(thisUrl);
  var image = jqplotToImg($('#'+id)); 
  var arr=image.split(',');
  $("#imgstr").val(arr[arr.length-1]);
  //$("#from").submit();
  var dataOption={
    imgId:id,
    imgBody:$('#imgstr').val(),
    url:thisUrl
  };
  toAjax(dataOption);
}
function toAjax(dataOption){
	$.ajax({
    type: "post",
    url: "<%=path %>/getImage.do",
    data: dataOption,
    dataType: "json",
    success: function(data){
    }
	});
}
function saveImageInfo (id) { 
  var image = jqplotToImg($('#'+id)); 
  var w=window.open('about:blank','image from canvas'); 
  w.document.write("<img src='"+image+"' alt='from canvas'/>"); 
} 
</script>
<meta name="GENERATOR" content="MSHTML 11.00.9600.16663"></HEAD> 
<body>
<div id="chart1" style="width: 500px; height: 300px;float: left;">
</div>
<div style="float: left;">
<input TYPE="button" VALUE="保存到客户端" ONCLICK="saveImageInfo('chart1');"><br><br>
<input TYPE="button" VALUE="保存到服务器" ONCLICK="saveImg('chart1');">
</div>
<div  id="chart3" style="width: 500px; height: 300px;float: left;"></div>
<div style="float: left;">
<input TYPE="button" VALUE="保存到客户端" ONCLICK="saveImageInfo('chart3');"><br><br>
<input TYPE="button" VALUE="保存到服务器" ONCLICK="saveImg('chart3');">
</div>
<form action="<%=path %>/getImage.do" method="post" target="frmimg" hidden="hidden" id="from" >
  <input hidden="hidden" id="imgstr" name="chart"/>
  <input hidden="hidden" id="thisUrl" name="thisUrl" value=""/>
</form>
<div ></div>
</body>
</HTML>
