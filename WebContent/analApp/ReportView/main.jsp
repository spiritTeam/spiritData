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
<link href="<%=path%>/resources/plugins/zui/lib/datetimepicker/datetimepicker.min.css" rel="stylesheet">
<script src="<%=path%>/resources/plugins/zui/js/zui.min.js"></script>
<script src="<%=path%>/resources/plugins/zui/lib/datetimepicker/datetimepicker.min.js"></script>
<!-- 加载analApp的JS -->
<script src="<%=path%>/resources/js/visit.utils.js"></script>
<script src="<%=path%>/analApp/js/analApp.view.js"></script>
<script src="<%=path%>/reportFrame/serial/js/spirit.report.serial.utils.js"></script>

<title>报告主界面</title>
</head>
<style>
.div{padding:2px;border:1px solid #ddd;width:90%;margin:0 auto;}

/*** start 列表数据显示样式 ***/
.dg_border_left_1{border-left:1px grey solid;}
.dg_border_right_1{border-right:1px grey solid;}
.dg_th_font_bold{color:#444;font-weight:bold;}
.dg_td_bgcolor_lightblue{background-color:#e5ffee;}
/*** end 列表数据显示样式 ***/

.border_no{border:0px solid red; }
.padding_top5{padding-top:5px;}
.padding_2px{padding:2px;}

.bt_13_no{border:0px solid transparent;font-size:13px;padding:0px;}
.td_height_49{height:49px;}
.font_15{font-size:15px;}
.div_float_left{float:left;}
.div_inline{ display:inline} 

.roundBase {
border-radius: 5px; /* 所有角都使用半径为5px的圆角，此属性为CSS3标准属性 */
-moz-border-radius: 5px; /* Mozilla浏览器的私有属性 */
-webkit-border-radius: 5px; /* Webkit浏览器的私有属性 */
border-radius: 5px 4px 3px 2px; /* 四个半径值分别是左上角、右上角、右下角和左下角 */
} 
.circleFillRed {
width: 10px;
height: 10px;
background-color: #ff0000;
-webkit-border-radius: 10px;
border-radius:10px;
} 
.circleEmpty {
width: 15px;
height: 15px;
background-color: #efefef; /* Can be set to transparent */
border: 1px #a72525 solid;
-webkit-border-radius: 15px 15px 15px 15px;
border-radius:10px;
} 
.circleEmpty2 {
width: 10px;
height: 10px;
background-color: #efefef; /* Can be set to transparent */
border: 1px #a72525 solid;
-webkit-border-radius: 10px;
border-radius:10px;
} 
.li_inline{  
    display:-moz-inline-box;
    *display:inline;
    display:inline-block; 
    float:left;
}
.wrap{
  word-wrap: break-word;
  word-break:break-all;
  white-space:normal;
}

</style>
<body class="padding_top5" style="background-color:#FFFFFF">
  <div class="div border_no">
    <table style="width:100%;">
      <tr>
        <td style="width:100px;">          
        </td>  
        <td style="width:200px;">
          <div style="float:left;width:100%;">
            <div style="float:left;padding-top:2px;"><font style="font-size:15px;">报告名：</font></div>
            <div style="float:left;"><input id="inp_filename" type="text" class="form-control" style="width:120px;"></input></div>
          </div>
        </td>        
        <td style="width:80px;text-align:right;">   
                              时间段：
        </td>        
        <td style="width:100px;">   
          <div class="col-md-4"><input id="startDate" type="text" class='form-control form-date' placeholder='开始日期' readonly></div>       
        </td>        
        <td style="width:10px;">   
          --      
        </td>           
        <td style="width:100px;">   
          <div class="col-md-4"><input id="endDate" type="text" class='form-control form-date' placeholder='结束日期' readonly></div>       
        </td>          
        <td style="width:100px;text-align:right;">   
          <button class="btn btn-default" onclick="startSearch();">查  询</button>
        </td>    
        <td style="text-align:right;">
          <a href="#" class="">
            <img src="<%=path%>/analApp/images/file_list.png" style="height:45px;width:45px;" onclick="showSearchResult(SHOW_TYPE_LIST);" title="列表预览" alt="列表预览"/>
          </a>
          <a href="#" class="">
            <img src="<%=path%>/analApp/images/file_thumb.png" style="height:45px;width:45px;" onclick="showSearchResult(SHOW_TYPE_THUMB);" title="缩略图预览" alt="缩略图预览"/>
          </a>
        </td>
      </tr>
    </table>    
  </div>
             
  <div class="div border_no">
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
  initDatePicker();  
  _urlPath = "<%=path%>";
  startSearch();
  //定期自动查询报告，前提是必须先访问这个页面才行
  setInterval(startSearch,30*1000);
});

//初始化查询输入框
var searchTxt = "请输入查询内容...";
function initSearchFileInput(){
  var _objSearch = $("#inp_filename");
  _objSearch.keydown(function(e){
    if(e.keyCode == 13){
      startSearch();
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
    startSearch();
  });
}

//初始化日期选择控件 
function initDatePicker() {
  $('.form-date').datetimepicker ({
    language:  'zh-CN',
    weekStart: 1,
    todayBtn:  1,
    autoclose: 1,
    todayHighlight: 1,
    startView: 2,
    minView: 2,
    forceParse: 0,
    format: 'yyyy-mm-dd'
  });
}

//定义查询方式和保存查询结果
var SHOW_TYPE_LIST = "LIST"; //列表显示常量
var SHOW_TYPE_THUMB = "THUMB"; //缩略图显示常量
var showType = SHOW_TYPE_LIST; //默认是列表显示查询结果
var thumbPath = "<%=path%>/analApp/images/"; //报告缩略图所存储的路径
var defaultThumbImg = "excel.png"; //默认显示的缩略图名称
var searchResultJsonData = null; //保存查询后的结果
var objDatatable = null; //列表显示对象

//取出输入条件，提交查询
function startSearch(){
  var searchStr = $("#inp_filename").val();
  var startDateStr = $("#startDate").val();
  var endDateStr = $("#endDate").val();
  
  //异步查询文件列表
  var searchParam={"searchStr":searchStr,"startDateStr":startDateStr,"endDateStr":endDateStr};
  //alert("查询参数："+allFields(searchParam));
  var url="<%=path%>/reportview/searchReportList.do";
  $.ajax({type:"post", async:true, url:url, data:searchParam, dataType:"text",
    success:function(jsonStr) {
      try {
        searchResultJsonData = str2JsonObj(jsonStr); 
        showSearchResult(showType);
      } catch(e) {
        showAlert("解析异常", "查询结果解析成JSON失败：</br>"+(e.message)+"！<br/>", "error", function(){});
      }
    },
    error:function(errorData){
      showAlert("查询异常", "查询失败：</br>"+(errorData?errorData.responseText:"")+"！<br/>", "error", function(){});
    }
  }); 
}

//显示查询结果
function showSearchResult(_showType){
  //showAlert("showSearchResult()","showType="+_showType,"info");
  showType = _showType;
  $('#dgList').css("display","none");
  $('#dgThumb').css("display","none");
    
  if(showType == SHOW_TYPE_LIST){
    showSearchResultList();
  }else if(showType == SHOW_TYPE_THUMB){
    showSearchResultThumb();
  }else{
    showType = SHOW_TYPE_LIST;
    showSearchResultList();
  }
}

//列表显示查询结果
function showSearchResultList(){
  var _objList = $('#dgList');
  _objList.empty();
  _objList.css("display","block");
  //构建dbopts
  var dbopts={
    customizable: true, 
    checkable: false,
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
        {width:300,text:'报告名',type:'string',flex: false,colClass:'text-left font_15',cssClass:'text-center dg_th_font_bold'},
        {width:80,text:'报告类型',type:'string',flex: true,colClass:'text-left font_15',cssClass:'text-center dg_th_font_bold'},
        {width:60,text:'创建日期',type:'date',flex: true,colClass:'text-left font_15',cssClass:'text-center dg_th_font_bold'},
        {width:30,text:'操作 ',type:'string',flex: true,colClass:'text-center font_15',cssClass:'text-center dg_th_font_bold'}
      ]
    }
  };
  //组装显示结果行
  var dtrows =[];
  if (searchResultJsonData!=null && searchResultJsonData.rows!=null && searchResultJsonData.rows.length>0) {
    var jsonRows = searchResultJsonData.rows;
    var len = jsonRows.length;
    for(var i=0;i<len;i++){
      var id = jsonRows[i]["id"];
      var fileName = jsonRows[i]["reportName"];
      var fileFull = fileName;
      //var unRead = jsonRows[i]["unRead"]; //是否未读过
      var unRead = getMainPage().isUnReadReportById(id); //是否未读过
      var ahrf_file = '';
      //是否未读，如果未读则前面加个小红点用于标识
      var optRound = '';
      var unReadId = "unRead_List_"+id;
      if(unRead){
        optRound = '<span id="'+unReadId+'" class="div_float_left circleFillRed" style="margin-left:5px;"/>';
        ahrf_file = '<a href="###" onclick="showReport(\''+id+'\',\''+unReadId+'\');"><strong>'+fileFull+'</strong></a>';
      } else {
    	optRound = '<span class="div_float_left circleFillRed" style="margin-left:5px;visibility:hidden;"/>';
        ahrf_file = '<a href="###" onclick="showReport(\''+id+'\');"><strong>'+fileFull+'</strong></a>';
      }
      ahrf_file = optRound + ahrf_file;
      var fileType = jsonRows[i]["reportType"];
      var createDate = jsonRows[i]["createTimeStr"];
      //var cssClassStr= i%2==0?"":"dg_td_bgcolor_lightblue";
      //var arow={checked:false,data:[fileName+"."+suffix,size,createDate],cssClass:cssClassStr};
      //构建操作按钮
      //var optRelation = '<button type="button" class="btn bt_13_no" onclick="showRelation(\''+id+'\');">关系</button>';
      //var optReport = '<button type="button" class="btn bt_13_no" data-type="ajax" data-url="<%=path%>/demo/Rd/resultRdEchart.jsp" data-toggle="modal">浏览</button>';
      //var optReportView = '<button type="button" class="btn bt_13_no" onclick="showReport(\''+id+'\');">浏览</button>';
      var optHtml = getOptHtml(jsonRows[i],"floatLeft",unReadId);
      var arow={checked:false,data:[ahrf_file,fileType,createDate,optHtml]};
      dtrows.push(arow);
    }
  }
  dbopts.data.rows = dtrows;
  //构建datatable
  var objDatatable = $('<div id="div_datatable"></div>');
  objDatatable.datatable(dbopts);  
  objDatatable.find("table").addClass("table-bordered table-striped");
  //$("tr",objDatatable).css("height","49px");

  //添加到dglist中
  _objList.append(objDatatable);
}

//缩略图显示查询结果
function showSearchResultThumb(){
  var _objThumb = $('#dgThumb');
  _objThumb.css("display","block");
  var thumbHtmlStr = '';
  if(searchResultJsonData!=null && searchResultJsonData.rows!=null && searchResultJsonData.rows.length>0){
    thumbHtmlStr += '<section class="cards">';
    var jsonRows = searchResultJsonData.rows;
    var len = jsonRows.length;
    for(var i=0;i<len;i++){
      var id = jsonRows[i]["id"];
      var fileName = jsonRows[i]["reportName"];
      var fileFull = fileName;
      var desc = jsonRows[i]["descn"];
      var reportType = jsonRows[i]["reportType"];
      var createDate = jsonRows[i]["createTimeStr"];
      //var unRead = jsonRows[i]["unRead"];
      var unRead = getMainPage().isUnReadReportById(id); //是否未读过
      var thumbUrl = jsonRows[i]["thumbUrl"];
      thumbUrl = thumbPath + getStr(thumbUrl,defaultThumbImg);
      
      thumbHtmlStr += '  <div class="col-md-4 col-sm-6 col-lg-2">';
      thumbHtmlStr += '    <div class="card">';
      thumbHtmlStr += '      <div class="media-wrapper">';
      //显示的缩略图片
      thumbHtmlStr += '        <img src='+thumbUrl+' alt="缩略图">';
      thumbHtmlStr += '      </div>';
      //鼠标移到图片上时，下拉浮动框显示的内容
      thumbHtmlStr += '        <span class="caption" style="padding:2px;">'+desc+'</span>';
      //缩略图下方显示的内容
      thumbHtmlStr += '      <div class="media-wrapper">';
      //显示报告名
      var ahrf_file = '';
      //是否未读，如果未读则前面加个小红点用于标识
      var optRound = '';
      var unReadId = "unRead_Thumb_"+id;
      if(unRead){
        optRound = '<span id="'+unReadId+'" class="circleFillRed" style="text-align:center;float:left;"></span>';
        ahrf_file = '<a href="###" class="card-heading" style="padding:0px;" onclick="showReport(\''+id+'\',\''+unReadId+'\');" title="'+fileFull+'">'+'<strong>'+fileFull+'</strong></a>';
      }else{
        optRound = '<span class="div_float_left circleFillRed" style="margin-left:5px;visibility:hidden;"/>';
        ahrf_file = '<a href="###" class="card-heading" style="padding:0px;" onclick="showReport(\''+id+'\');" title="'+fileFull+'">'+'<strong>'+fileFull+'</strong></a>';
      }
      //var tbHead = '<ul style="list-style:none;width:100%;height:100%;padding-left:0px;margin-top:10px;margin-bottom:0px;"><li class="li_inline" style="padding-bottom:10px;">'+optRound+'</li><li class="li_inline wrap">'+ahrf_file+'</li></ul>';      
      var tbHead = '<div><div class="div_float_left,div_inline">'+optRound+'</div><div class="div_float_left,div_inline">'+ahrf_file+'</div></div>';
      thumbHtmlStr += '        '+tbHead;
      thumbHtmlStr += '      </div>'; 
      thumbHtmlStr += '      <div class="media-wrapper card-content text-muted " style="padding:2px;">';
      thumbHtmlStr += '        报告类型：'+reportType+'&nbsp;&nbsp;创建日期：'+createDate+'';
      thumbHtmlStr += '      </div>'; 
      thumbHtmlStr += '      <div class="media-wrapper card-content text-muted">';
      var optHtml = getOptHtml(jsonRows[i],"floatRight",unReadId);
      thumbHtmlStr += '        '+optHtml+'';
      thumbHtmlStr += '      </div>'; 
      thumbHtmlStr += '    </div>';
      thumbHtmlStr += '  </div>';   
    }
    thumbHtmlStr += '</section>';
  }
    
  _objThumb.html(thumbHtmlStr);
}

//组装一行操作按钮
function getOptHtml(aJsonRow,floatStyle,unReadId){
	if(!aJsonRow){
		return "";
	}
	var reportId = aJsonRow["id"];
	var fileId = aJsonRow["fileId"];
  var fileName = aJsonRow["reportName"];
  var fileFull = fileName;
  //构建操作按钮
  var optRelation = '<button type="button" class="btn bt_13_no" onclick="showRelation(\''+reportId+'\');"><i class="icon-list"></i>关系</button>';
  //判断是否未读
  var unRead = getMainPage().isUnReadReportById(reportId); //是否未读过
  var optReportView = '';
  if(unRead){
	  optReportView = '<button type="button" class="btn bt_13_no" onclick="showReport(\''+reportId+'\',\''+unReadId+'\');"><i class="icon-building"></i>浏览</button>'; 
  }else{
	  optReportView = '<button type="button" class="btn bt_13_no" onclick="showReport(\''+reportId+'\');"><i class="icon-building"></i>浏览</button>';
  }
  var optContent = optReportView+"&nbsp;&nbsp;"+optRelation+"";
  var optHtml = optContent;
  if(typeof(floatStyle) != "undefined" && floatStyle=="floatRight"){
	  if(floatStyle=="floatRight"){
		  optHtml = "<div style='float:right;margin-right:10px;'>"+optContent+"</div>";
	  }else if(floatStyle=="floatLeft"){
	      optHtml = "<div style='float:left;margin-left:10px;'>"+optContent+"</div>";
	  }
  }
  return optHtml;  
}

//获得输入的查询内容
function getInputSearchFileStr(){
  var searchedStr = ($("#inp_filename").val()==searchTxt)?"":$("#inp_filename").val();
  return searchedStr;
}

</script>
</html>
