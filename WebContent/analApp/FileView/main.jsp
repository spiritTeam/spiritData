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
<script src="<%=path%>/analApp/js/zui.pager.js"></script>
<script src="<%=path%>/reportFrame/serial/js/spirit.report.serial.utils.js"></script>

<title>文件主界面</title>
</head>
<style>
.div{padding:2px;border:1px solid #ddd;width:90%;margin:0 auto;}
.div_center{margin:0 auto;text-align:center;}

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
positoin:absolute;
float:left;
width: 10px;
height: 10px;
background-color: #ff0000;
-webkit-border-radius: 10px;
border-radius:10px;
}
.circleOpacity {
positoin:absolute;
float:left;
width: 10px;
height: 10px;
background-color: inherit;
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
        <td style="width:350px;align:right;">
          <div style="float:left;width:100%;">
            <div style="float:left;padding-top:2px;"><font style="font-size:15px;">文件名：</font></div>
            <div style="float:left;"><input id="inp_filename" type="text" class="form-control" style="width:220px;"></input></div>
          </div>
        </td>        
        <td style="width:80px;text-align:right;">   
                              时间段：
        </td>        
        <td style="width:100px;">   
          <div class="col-md-4">
            <input id="startDate" type="text" class='form-control form-date' placeholder='开始日期' readonly> 
          </div>      
          <div id="div_close_startData" class='sWin_closeBtn'/></div> 
        </td>        
        <td style="width:10px;">   
          --      
        </td>           
        <td style="width:100px;">   
          <div class="col-md-4"><input id="endDate" type="text" class='form-control form-date' placeholder='结束日期' readonly></div>    
          <div id="div_close_endData" class='sWin_closeBtn'/></div>         
        </td>          
        <td style="width:100px;text-align:right;">   
          <button class="btn btn-default" onclick="startSearch();">查  询</button>
        </td>    
        <td style="text-align:right;">
          <a href="#" class="">
            <img src="<%=path%>/analApp/images/file_list.png" style="height:45px;width:45px;" onclick="switchShowDivResult(SHOW_TYPE_LIST);" title="列表预览" alt="列表预览"/>
          </a>
          <a href="#" class="">
            <img src="<%=path%>/analApp/images/file_thumb.png" style="height:45px;width:45px;" onclick="switchShowDivResult(SHOW_TYPE_THUMB);" title="缩略图预览" alt="缩略图预览"/>
          </a>
        </td>
      </tr>
    </table> 
  </div>
             
  <div id="div_list_group" class="div border_no" style="border:0px solid red;display:none;">
    <!-- 查询结果列表显示-->
    <div id="dgList"></div>
    <!-- 分页条 -->
    <div id="div_pager_list"></div>
  </div>
  <div id="div_thumb_group" class="div border_no" style="border:0px solid red;display:none;">
    <!-- 查询结果缩略图显示 -->
    <div id="dgThumb"></div>
    <!-- 分页条 -->
    <div id="div_pager_thumb"></div>
  </div>
</body>
<script>
//*** begin 常量定义 ***
var pager_list_size = 10; //列表显示时，每页显示的条数
var pager_thumb_size = 10; //卡片显示时，每页显示的条数
//*** end 常量定义 ***

//*** begin 变量定义 ***
var pager_list = null; //列表分页对象
var pager_thumb = null; //卡片分页对象
//*** end 变量定义 ***

//主函数
$(function() {
  initSubmitBt();
  initSearchFileInput();
  initDatePicker();
  //初始化日期清除按钮
  initDataCloseBT();
  //初始化分页
  initPager();
  
  //设置路径
  _urlPath = "<%=path%>";  
  startSearch();
});

//初始化分页
function initPager(){
  pager_list = new $.ZuiPager(); 
  pager_list.initPager({"pageSize":pager_list_size,"divPageId":"div_pager_list","objPager":pager_list,"onSelectPage":selectPage});
    
  pager_thumb = new $.ZuiPager(); 
  pager_thumb.initPager({"pageSize":pager_thumb_size,"divPageId":"div_pager_thumb","objPager":pager_thumb,"onSelectPage":selectPage});
}
//选择了某个页面
function selectPage(pageNumber, pageSize){
	//alert("onSelectPage()... pageNumber="+pageNumber+";pageSize="+pageSize);
	startSearch({"pageNumber":pageNumber, "pageSize":pageSize});
}

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
function initDatePicker(){
  $('.form-date').datetimepicker(
  {
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

//初始化日期清除按钮
function initDataCloseBT(){
  //初始化开始日期，首先定位关闭按钮的位置，其次加入点击事件
  var objStartDate = $("#startDate"); 
  var sleft = px2Float(objStartDate.offset().left);
  var swidth = px2Float(objStartDate.css("width"));
  var spos = sleft + swidth;
  //alert(sleft+" + "+swidth+" = "+spos);
  var objCloseStartBT = $("#div_close_startData");
  objCloseStartBT.css("left",spos);
  var stop = px2Float(objCloseStartBT.css("top"))+2; 
  objCloseStartBT.css("top",stop);
  objCloseStartBT.click(function(){
    objStartDate.datetimepicker("reset");
  });
  //初始化结束日期
  var objEndDate = $("#endDate"); 
  var eleft = px2Float(objEndDate.offset().left);
  var ewidth = px2Float(objEndDate.css("width"));
  var epos = eleft + ewidth;
  //alert(eleft+" + "+ewidth+" = "+epos);
  var objCloseEndBT = $("#div_close_endData");
  objCloseEndBT.css("left",epos);
  var etop = px2Float(objCloseEndBT.css("top"))+2; 
  objCloseEndBT.css("top",etop);
  objCloseEndBT.click(function(){
    objEndDate.datetimepicker("reset");
  });
}

//将带有px后缀的数值字符串，去掉px并转换成float类型
function px2Float(pxStr){
if(pxStr){
  if(typeof(pxStr)=="string"){
    var idx = pxStr.indexOf("px");
    if(idx>-1){
      return parseFloat(pxStr.substring(0,idx));
    }else{
      return parseFloat(pxStr);
    } 
  }else if(typeof(pxStr)=="number"){
    return parseFloat(pxStr);
  }else{
    return parseFloat(pxStr);
  } 
}
return pxStr;
}

//定义查询方式和保存查询结果
var SHOW_TYPE_LIST = "LIST"; //列表显示常量
var SHOW_TYPE_THUMB = "THUMB"; //缩略图显示常量
var showType = SHOW_TYPE_LIST; //默认是列表显示查询结果
var thumbPath = "<%=path%>/analApp/images/"; //文件缩略图所存储的路径
//根据文件后缀名查找相应的图标
var fileSuffixImg = {"default":"file.png","xlsx":"excel.png","xls":"excel.png"};
var searchResultJsonData_list = null; //保存查询后的结果，列表查询，由于加上了分页功能，所以需要分别保存查询结果
var searchResultJsonData_thumb = null; //保存查询后的结果,卡片查询，由于加上了分页功能，所以需要分别保存查询结果
var unReadObjJsonArr = []; //当缩略图显示时，未读小红点位置需要调整到文件名左上角

//取出输入条件，提交查询
function startSearch(searchParam){
	//清除未读报告信息
	unReadObjJsonArr = [];
	//设置查询条件
  //var searchStr = getInputSearchFileStr();
  if(!searchParam){
	  searchParam = {"pageNumber":1, "pageSize":showType==SHOW_TYPE_LIST?pager_list_size:pager_thumb_size};
  }
  searchParam.searchStr = $("#inp_filename").val();
  searchParam.startDateStr = $("#startDate").val();
  searchParam.endDateStr = $("#endDate").val();
  
  //异步查询文件列表  
  //alert("startSearch(): searchParam="+JSON.stringify(searchParam));
  var url="<%=path%>/analApp/demoData/filelist.json";
  url = "<%=path%>/fileview/searchFilePageList.do";
  $.ajax({type:"post", async:true, url:url, data:searchParam, dataType:"text",
    success:function(jsonStr){
      try{
        //alert("fileSearch() search result="+jsonStr); 
        switchShowDivResult(showType);
    	  if(showType == SHOW_TYPE_THUMB){
    		  showSearchResultThumb(jsonStr);
    		}else if(showType == SHOW_TYPE_LIST){
    		  showSearchResultList(jsonStr);
    		  //第一次访问时，只访问了list，没有访问thumb，所以切换时会没有数据显示，此时需要把第一页查询结果给thumb
    		  if(searchParam.pageNumber==1 && searchResultJsonData_thumb == null){
    			  //searchResultJsonData_thumb = searchResultJsonData_list;
    			  showSearchResultThumb(jsonStr);
    			  //alert("searchResultJsonData_thumb="+searchResultJsonData_thumb);
    		  }
    		}
        //pager.setTotalCount(searchResultJsonData.total);
      }catch(e){
        showAlert("解析异常", "查询结果解析成JSON失败：</br>"+(e.message)+"！<br/>", "error", function(){});
      }
    },
    error:function(errorData){
      showAlert("查询异常", "查询失败：</br>"+(errorData?errorData.responseText:"")+"！<br/>", "error", function(){});
    }
  }); 
}

//开关显示查询结果
function switchShowDivResult(_showType){
	showType = _showType;
	//alert("switchShowDivResult() showType="+_showType);
	$('#div_list_group').css("display","none");
	$('#div_thumb_group').css("display","none");
	if(showType == SHOW_TYPE_THUMB){
		$('#div_thumb_group').css("display","block");
		pager_thumb.alignCenter();
	}else{
		$('#div_list_group').css("display","block");
	}
}


//列表显示查询结果
function showSearchResultList(jsonStr){
  searchResultJsonData_list = str2JsonObj(jsonStr); 
  //alert("searchResultJsonData_list="+searchResultJsonData_list);
  var _objList = $('#dgList');
  _objList.empty();
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
        {width:300,text:'文件名',type:'string',flex: false,colClass:'text-left font_15',cssClass:'text-center dg_th_font_bold'},
        {width:60,text:'大小',type:'number',flex: true,colClass:'text-right font_15',cssClass:'text-center dg_th_font_bold'},
        {width:60,text:'创建日期',type:'date',flex: true,colClass:'text-left font_15',cssClass:'text-center dg_th_font_bold'},
        {width:30,text:'操作 ',type:'string',flex: true,colClass:'text-center font_15',cssClass:'text-center dg_th_font_bold'}
      ]
    }
  };
  //组装显示结果行
  var dtrows =[];
  if(searchResultJsonData_list!=null && searchResultJsonData_list.rows!=null && searchResultJsonData_list.rows.length>0){
    var jsonRows = searchResultJsonData_list.rows;
    var len = jsonRows.length;
    for(var i=0;i<len;i++){
      var fileIndexId = jsonRows[i]["fileIndexId"];
      var fileName = jsonRows[i]["clientFileName"];
      var suffix = jsonRows[i]["suffix"];
      var fileFull = fileName;
      var reportId = jsonRows[i]["reportId"];
      //var ahrf_file = '<a href="###" onclick="showFile(\''+fileIndexId+'\',\''+fileFull+'\');"><strong>'+fileFull+'</strong></a>';
      var ahrf_file = getFileHrefHtml(fileIndexId,fileFull,reportId,showType);
      var size = jsonRows[i]["fileSize"];
      var createDate = jsonRows[i]["createTimeStr"];
      
      //var cssClassStr= i%2==0?"":"dg_td_bgcolor_lightblue";
      //var arow={checked:false,data:[fileName+"."+suffix,size,createData],cssClass:cssClassStr};
      //构建操作按钮
      var optHtml = getOptHtml(jsonRows[i],"floatCenter",showType);
      var arow={checked:false,data:[ahrf_file,size,createDate,optHtml]};
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
  
  //设置分页条
  pager_list.setTotalCount(searchResultJsonData_list.total);
}

//缩略图显示查询结果
function showSearchResultThumb(jsonStr){
	var _objThumb = $('#dgThumb');
  var thumbHtmlStr = '';
  searchResultJsonData_thumb = str2JsonObj(jsonStr); 
  if(searchResultJsonData_thumb!=null && searchResultJsonData_thumb.rows!=null && searchResultJsonData_thumb.rows.length>0){
    thumbHtmlStr += '<section id="section_thumb" class="cards">';
    var jsonRows = searchResultJsonData_thumb.rows;
    var len = jsonRows.length;
    for(var i=0;i<len;i++){
      var fileIndexId = jsonRows[i]["fileIndexId"];
      var fileName = jsonRows[i]["clientFileName"];
      var suffix = jsonRows[i]["suffix"];
      var thumbImgUrl = thumbPath + getSuffixImgName(suffix);
      var fileFull = fileName;
      var size = jsonRows[i]["fileSize"];
      var createDate = jsonRows[i]["createTimeStr"];
      var desc = jsonRows[i]["descn"];
      var reportId = jsonRows[i]["reportId"];
      
      thumbHtmlStr += '  <div class="col-md-4 col-sm-6 col-lg-2">';
      thumbHtmlStr += '    <div class="card">';
      thumbHtmlStr += '      <div class="media-wrapper">';
      //显示的缩略图片
      thumbHtmlStr += '        <img src='+thumbImgUrl+' alt="">';
      thumbHtmlStr += '      </div>';   
      //鼠标移到图片上时，下拉浮动框显示的内容
      thumbHtmlStr += '        <span class="caption" style="padding:2px;">'+desc+'</span>';
      //缩略图下方显示的内容
      thumbHtmlStr += '      <div class="media-wrapper"><div class="thumbText">';
      //thumbHtmlStr += '        <a href="###" class="card-heading" onclick="showFile(\''+fileIndexId+'\',\''+fileFull+'\');"><strong>'+fileFull+'</strong></a>';
      var fileHrefHtml = getFileHrefHtml(fileIndexId,fileFull,reportId,showType,"card-heading");
      thumbHtmlStr += fileHrefHtml;
      thumbHtmlStr += '      </div></div>';  
      thumbHtmlStr += '      <div class="media-wrapper card-content text-muted" style="padding:2px;text-align:center;">';
      thumbHtmlStr += '        大小:'+size+'&nbsp;&nbsp;&nbsp;创建日期:'+createDate+'';
      thumbHtmlStr += '      </div>'; 
      thumbHtmlStr += '      <div class="media-wrapper card-content text-muted">';
      var optHtml = getOptHtml(jsonRows[i],"floatRight",showType);
      thumbHtmlStr += '        '+optHtml+'';
      thumbHtmlStr += '      </div>';  
      thumbHtmlStr += '    </div>';
      thumbHtmlStr += '  </div>';   
    }
    thumbHtmlStr += '</section>';
  }
    
  _objThumb.html(thumbHtmlStr);
  //调整红点的位置，目前红点都是在最左侧，需要移动到文件名左边fireInThumb
  ///setTimeout("fitRedDots()",1*1000);
  setTimeout(function(){
    var _objThumb = $('#dgThumb');
    $(_objThumb).find(".thumbText").each(function(i) {
      var textFontSize=$($(this).parent().find("a")[0]).css("font-size");
      var textText=$($(this).parent().find("a")[0]).text();
      var textLength=(textText.cnLength()*(parseInt(textFontSize)/2))+textText.length+11;
      //console.log(textText+":"+textFontSize+":"+textText.cnLength()+":"+textLength);
      $(this).css("width",textLength)
      .css("padding-left", (parseInt($(this).parent().width())-textLength)/2);
    });    
  },1*200);

  //设置分页条
  pager_thumb.setTotalCount(searchResultJsonData_thumb.total);
  
  //卡片 section居中
  //alert($("#dgThumb").width()+"  "+$("#section_thumb").width());
}

//获得输入的查询内容
function getInputSearchFileStr(){
  var searchedStr = ($("#inp_filename").val()==searchTxt)?"":$("#inp_filename").val();
  return searchedStr;
}

//组装一行操作按钮
function getOptHtml(aJsonRow,floatStyle,showType){
  if(!aJsonRow){
    return "";
  }
  var fileIndexId = aJsonRow["fileIndexId"];
  var reportId = aJsonRow["reportId"];
  var fileName = aJsonRow["clientFileName"];
  var suffix = aJsonRow["suffix"];
  var fileFull = fileName;
  //构建操作按钮
  var optView = '<button type="button" class="btn bt_13_no" onclick="showFile(\''+fileIndexId+'\',\''+fileFull+'\');"><i class="icon-list"></i>浏览</button>';
  //var optReport = '<button type="button" class="btn bt_13_no" data-type="ajax" data-url="<%=path%>/demo/Rd/resultRdEchart.jsp" data-toggle="modal">报告</button>';
  var optReport = '';
  if(!isUndefinedNullEmpty(reportId)){
	  var unReadId = getUnReadReportId(reportId,showType);
    optReport = '<button type="button" class="btn bt_13_no" onclick="showReport(\''+reportId+'\',\''+unReadId+'\');"><i class="icon-building"></i>报告</button>';
  }else{
    optReport = '<button type="button" style="visibility:hidden;" class="btn bt_13_no" onclick="showReport(\''+reportId+'\');"><i class="icon-building"></i>报告</button>';
  }
  var optContent = ""+optView+"&nbsp;&nbsp;"+optReport+"&nbsp;";
  var optHtml = optContent;
  if(typeof(floatStyle) != "undefined" && floatStyle=="floatRight"){
    optHtml = "<div style='float:right;margin-right:10px;'>"+optContent+"</div>";  
  }
  return optHtml;  
}

//根据文件后缀名查找相应的图标，如果没有找到则返回default默认的图标
function getSuffixImgName(suffixName){
  var retName = fileSuffixImg["default"];
  try {
    if(typeof(suffixName) != "undefined" && suffixName!=null && suffixName.length>0){
      if(suffixName.charAt(0)=="."){
        suffixName = suffixName.substring(1);
      }
    }
    retName = fileSuffixImg[suffixName];
    if(typeof(retName) == "undefined" || retName==null || retName.length==0){
      retName = fileSuffixImg["default"];
    }
  }catch(e){showAlert("获取图片后缀名","failed to fecth img suffix name. suffix="+suffixName+" err:"+e.message,"error");}
  return retName;
}

/**
 * 获取文件连接的html串，考虑了未读报告的状态，当时存在未读报告时，文件名前加红色小圆点标识
 * fileIndexId -- 文件的ID
 * fileFull -- 文件中文全名，带后缀
 * reportId -- 由此文件生成的报告ID，如果没有则为NULL
 * unReadPre -- 未读ID的前缀，如果是列表则为list_，如果是卡片则为thumb_
 */
function getFileHrefHtml(fileIndexId,fileFull,reportId,showType,hrefClass) {
	var unReadId = getUnReadReportId(reportId,showType);
  var optRound = '<div id="'+unReadId+'" class="'+(unReadId?'circleFillRed':'circleOpacity')+'"/>';
	return '<a href="#" class="'+(hrefClass?hrefClass:'')+'" onclick="showFile(\''+fileIndexId+'\',\''+fileFull+'\');">'+optRound+fileFull+'</a>';
  //是否未读，如果未读则前面加个小红点用于标识
//  $(retHtml).html(optRound+fileFull);
//  if(unReadId){
//		optRound = '<div id="'+unReadId+'" class="circleFillRed div_float_left thumbText"/>';
		//optRound = '<div id="'+unReadId+'" class="div_float_left div_inline" style="margin-left:5px;"/>';
//		unReadObjJsonArr.push({unReadId:"a_"+unReadId});
//  }
  //组装html
  //retHtml += optRound;
	//retHtml += '<a href="###" class="'+(hrefClass?hrefClass:null)+'" onclick="showFile(\''+fileIndexId+'\',\''+fileFull+'\');"><strong id="a_'+unReadId+'">'+fileFull+'</strong></a>';
//	var ahrf_file = '<a href="###" class="'+(hrefClass?hrefClass:null)+'" onclick="showFile(\''+fileIndexId+'\',\''+fileFull+'\');"><strong id="a_'+unReadId+'">'+fileFull+'</strong></a>';
//	  var retHtml = '<div><div class="div_float_left,div_inline">'+optRound+'</div><div class="div_float_left,div_inline">'+ahrf_file+'</div></div>';
//  retHtml = '<div>'+optRound+ahrf_file+'</div>';
	//retHtml += '<a href="###" class="'+(hrefClass?hrefClass:null)+'" onclick="showFile(\''+fileIndexId+'\',\''+fileFull+'\');"><strong id="a_'+unReadId+'"><div class="div_center">'+optRound+'<div class="div_inline">'+fileFull+'</div></div></strong></a>';
	//return retHtml;
}

/**
 * 根据reportId获取未读报告的ID
 */
function getUnReadReportId(reportId,showType){
  var retId = null;
  //if(!isUndefinedNullEmpty(reportId)){
	if(reportId){
		var unRead = getMainPage().isUnReadReportById(reportId); //是否报告未读过
		if(unRead){
			retId = "unRead_"+showType+"_"+reportId;
		}		
	}
	return retId;
}

</script>
</html>
