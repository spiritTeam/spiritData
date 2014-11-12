<%@ page pageEncoding="utf-8" contentType="text/html; charset=utf-8"%>
<%
  String path = request.getContextPath();
%>
<html>
<head>
<meta http-equiv="content-type" content="test/html;charset=utf-8">
  <!-- jqplpt -->
  <script language="javascript" type="text/javascript" src="<%=path %>/resources/plugins/jqplot/jquery.min.js"></script>
  <script language="javascript" type="text/javascript" src="<%=path %>/resources/plugins/jqplot/jquery.jqplot.min.js"></script>
  <script type="text/javascript" src="<%=path %>/resources/plugins/jqplot/shCore.js"></script>
  <script type="text/javascript" src="<%=path %>/resources/plugins/jqplot/shBrushJScript.js"></script>
  <script type="text/javascript" src="<%=path %>/resources/plugins/jqplot/shBrushXml.js"></script>
  <link type="text/css" rel="stylesheet" href="<%=path %>/resources/plugins/jqplot/shCoreDefault.css"/>
  <link type="text/css" rel="stylesheet" href="<%=path %>/resources/plugins/jqplot/shThemejqPlot.css"/>
  <!-- jqplot2imgjs -->
  <script type="text/javascript" src="<%=path %>/resources/js/brief/brief.jqplotToImg.js"></script>
  <style type="text/css">
  .jqplot{
    height:300px; width:500px; border:1 solid blue;
  }
  </style>
</head>
<body>
  <span id="s1">张三</span><br/>
  <span id="s2">李四</span><br/>
  <span style="color: yellow;font-size: 80px;">王五</span><br/>
  <div id="chart3" class="jqplot" ></div>
  <div id="chart4" class="jqplot" ></div>
  <img src="image002.png"><br/>
  <div></div>
  <div>
    <form id="imgInfoForm" hidden="true" action="<%=path %>/getImage.do" method="post">
      <input type="hidden" id="u1" value="" name="chart3">
      <input type="hidden" id="u1" value="" name="chart4">
      <input type="hidden" id="thisUrl" name="thisUrl" value="">
    </form>
  </div>
</body>
<script type="text/javascript">
$(document).ready(function(){
  // Some simple loops to build up data arrays.
  var cosPoints = [];
  for (var i=0; i<2*Math.PI; i+=0.4){ 
    cosPoints.push([i, Math.cos(i)]); 
  }
    
  var sinPoints = []; 
  for (var i=0; i<2*Math.PI; i+=0.4){ 
     sinPoints.push([i, 2*Math.sin(i-.8)]); 
  }
    
  var powPoints1 = []; 
  for (var i=0; i<2*Math.PI; i+=0.4) { 
      powPoints1.push([i, 2.5 + Math.pow(i/4, 2)]); 
  }
    
  var powPoints2 = []; 
  for (var i=0; i<2*Math.PI; i+=0.4) { 
      powPoints2.push([i, -2.5 - Math.pow(i/4, 2)]); 
  } 
 
  var plot3 = $.jqplot('chart3', [cosPoints, sinPoints, powPoints1, powPoints2], 
    { 
      title:'Line Style Options', 
      // Series options are specified as an array of objects, one object
      // for each series.
      series:[ 
          {
            // Change our line width and use a diamond shaped marker.
            lineWidth:2, 
            markerOptions: { style:'dimaond' }
          }, 
          {
            // Don't show a line, just show markers.
            // Make the markers 7 pixels with an 'x' style
            showLine:false, 
            markerOptions: { size: 7, style:"x" }
          },
          { 
            // Use (open) circlular markers.
            markerOptions: { style:"circle" }
          }, 
          {
            // Use a thicker, 5 pixel line and 10 pixel
            // filled square markers.
            lineWidth:5, 
            markerOptions: { style:"filledSquare", size:10 }
          }
      ]
    }
  );
  var plot4 = $.jqplot('chart4', [cosPoints, sinPoints, powPoints1, powPoints2], 
		    { 
		      title:'Line Style Options', 
		      // Series options are specified as an array of objects, one object
		      // for each series.
		      series:[ 
		          {
		            // Change our line width and use a diamond shaped marker.
		            lineWidth:2, 
		            markerOptions: { style:'dimaond' }
		          }, 
		          {
		            // Don't show a line, just show markers.
		            // Make the markers 7 pixels with an 'x' style
		            showLine:false, 
		            markerOptions: { size: 7, style:"x" }
		          },
		          { 
		            // Use (open) circlular markers.
		            markerOptions: { style:"circle" }
		          }, 
		          {
		            // Use a thicker, 5 pixel line and 10 pixel
		            // filled square markers.
		            lineWidth:5, 
		            markerOptions: { style:"filledSquare", size:10 }
		          }
		      ]
		    }
		  );
});
//本页url
var thisUrl = window.location.href;
//获取japlot元素
var jqplotDoms;  
$(function(){
	$('#s1').css('color','red');
  $('#s2').css({'color':'green','font-size':'80px'});
  initForm();
});
/**
 * 初始化ImgStrForm
 */
function initForm(){
	//初始化链接
	$('#thisUrl').val(thisUrl);
	//初始化图片Str
	initImg();
}
/**
 * 初始化imgStr
 */
function initImg(){
	jqplotDoms = $(".jqplot");
	alert(jqplotDoms.length);
	if(jqplotDoms.length>0){
		for (var i=0;i<jqplotDoms.length;i++) {
	    var jqpoltDom=$(jqplotDoms[i]);
	    var id = jqpoltDom.attr('id');
	    var image = jqplotToImg($('#'+id));
	    var arr=image.split(',');
	    $("input[name='"+id+"']").val(arr[arr.length-1]);
	  } 
	}
}
</script>
</html>