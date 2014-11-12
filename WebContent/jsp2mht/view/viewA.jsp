<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
  String a = request.getAttribute("A")+"";
  String b = request.getAttribute("B")+"";
  String c = request.getAttribute("C")+"";
  String path = request.getContextPath();
  //jquery.jqplot.min.js、jquery.jqplot.min.css、excanvas.min.js、excanvas.js 
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>简报模板(学年评定)</title>
<script type="text/javascript" src="<%=path%>/resources/plugins/jquery/jquery1.83/jquery-1.8.3.min.js"></script>
<script type="text/javascript" src="<%=path%>/resources/plugins/jqplot/jquery.jqplot.min.js"></script>
<script type="text/javascript" src="<%=path%>/resources/plugins/jqplot/excanvas.js"></script>
<script type="text/javascript" src="<%=path%>/resources/plugins/jqplot/excanvas.min.js"></script>
<link type="text/css" href="<%=path%>/resources/plugins/jqplot/jquery.jqplot.min.css">
<link type="text/css" href="<%=path%>/resources/plugins/jqplot/examples.css">
<script language="javascript" type="text/javascript" src="<%=path%>/resources/plugins/jqplot/plugins/jqplot.pieRenderer.js"></script>
<script language="javascript" type="text/javascript" src="<%=path%>/resources/plugins/jqplot/plugins/jqplot.categoryAxisRenderer.min.js"></script>
<script type="text/css">
  .word{
    word-wrap:break-word;
  }
</script>
  <!--  <meta http-equiv="content-type" content="text/html;charset=UTF-8">-->
</head>
<body>
<center>
  <div id="mainDiv" style="width: 600px;height: auto;">
    <h1>XX高中2014学年评定总结</h1>
    <!-- 本次记录 -->
    <div class="word" style="text-align:left;">
      <!--title -->
      <span style="font-size: 16px;">&nbsp;&nbsp;&nbsp;&nbsp;本次考试详情</span><br />
      <span style="font-size: 12px;">&nbsp;&nbsp;&nbsp;&nbsp;本次期末考试共有</span><span style="font-size: 12px;color: red;"><%=a %></span>
      <span style="font-size: 12px;">人参加,数学平均分为</span><span style="font-size: 12px;color: red;"><%=b %></span>
      <span style="font-size: 12px;">,及格率为</span><span style="font-size: 12px;color: red;"><%=c %></span>
      <span style="font-size: 12px;">,语文平均分为</span><span style="font-size: 12px;color: red;"><%=b %></span>
      <span style="font-size: 12px;">,及格率为</span><span style="font-size: 12px;color: red;"><%=c %></span>
      <span style="font-size: 12px;">,英语平均分为</span><span style="font-size: 12px;color: red;"><%=b %></span>
      <span style="font-size: 12px;">,及格率为</span><span style="font-size: 12px;color: red;"><%=c %></span>
    </div>
  </div>
  <div id="chart1" style="width: 500px; height: 300px;float: left;"></div>
  <DIV  id="chart3" style="width: 500px; height: 300px;float: left;"></DIV>
  <script type="text/javascript" language="javascript">
  $(document).ready(function(){
	   var data = [
	     ['Heavy Industry', 12],['Retail', 9], ['Light Industry', 14], 
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
	   //renderBar();
	 });
  function renderBar(){
	  var s1 = [200, 600, 700, 1000];
	    var s2 = [460, -210, 690, 820];
	    var s3 = [-260, -440, 320, 200];
	    // Can specify a custom tick Array.
	    // Ticks should match up one for each y value (category) in the series.
	    var ticks = ['May', 'June', 'July', 'August'];
	    var plot1 = $.jqplot('chart1', [s1, s2, s3], {
	        // The "seriesDefaults" option is an options object that will
	        // be applied to all series in the chart.
	        seriesDefaults:{
	            renderer:$.jqplot.BarRenderer,
	            rendererOptions: {fillToZero: true}
	        },
	        // Custom labels for the series are specified with the "label"
	        // option on the series option.  Here a series option object
	        // is specified for each series.
	        series:[
	            {label:'Hotel'},
	            {label:'Event Regristration'},
	            {label:'Airfare'}
	        ],
	        // Show the legend and put it outside the grid, but inside the
	        // plot container, shrinking the grid to accomodate the legend.
	        // A value of "outside" would not shrink the grid and allow
	        // the legend to overflow the container.
	        legend: {
	            show: true,
	            placement: 'outsideGrid'
	        },
	        axes: {
	            // Use a category axis on the x axis and use our custom ticks.
	            xaxis: {
	                renderer: $.jqplot.CategoryAxisRenderer,
	                ticks: ticks
	            },
	            // Pad the y axis just a little so bars can get close to, but
	            // not touch, the grid boundaries.  1.2 is the default padding.
	            yaxis: {
	                pad: 1.05,
	                tickOptions: {formatString: '$%d'}
	            }
	        }
	    });
	}
  </script>
</center>
</body>
</html>