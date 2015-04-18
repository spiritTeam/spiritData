<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%
  String path = request.getContextPath();
%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<!-- DEMO页面  用户登录后显示的主页面，包括文件查询、上传、分析，用户管理等功能 -->
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="cache-control" content="no-cache"/>
<meta http-equiv="pragma" content="no-cache"/>
<meta http-equiv="expires" content="0"/>
<jsp:include page="/common/sysInclude.jsp" flush="true"/>

<link rel="stylesheet" type="text/css" href="<%=path%>/resources/css/mySpiritUi/pageFrame.css"/>
<script type="text/javascript" src="<%=path%>/resources/plugins/spiritui/jq.spirit.pageFrame.js"></script>

<!-- 加载ZUI - 开源HTML5跨屏框架 -->
<link href="<%=path%>/resources/plugins/zui/css/zui.min.css" rel="stylesheet">
<link href="<%=path%>/resources/plugins/zui/css/example.css" rel="stylesheet">
<script src="<%=path%>/resources/plugins/zui/js/zui.min.js"></script>

<title>用户主界面DEMO</title>
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

.def-nav{
    display:block;
    float:left;
    height:48px;
    font:18px "Microsoft YaHei","Microsoft JhengHei","黑体";
    color:#d8d8d8;
    text-align:center;
    width:90px;
    line-height:48px
}

/*** start 列表数据显示样式 ***/
.dg_border_left_1{border-left:1px grey solid;}
.dg_border_right_1{border-right:1px grey solid;}
.dg_th_font_bold{color:#444;font-weight:bold;}
.dg_td_bgcolor_lightblue{background-color:#e5ffee;}
/*** end 列表数据显示样式 ***/


</style>
<body style="background-color:#FFFFFF">
<!-- 头部:悬浮 -->
<div id="topSegment">
  <div style="padding:5px;border:0px solid #ddd;">
    <table>
      <tr>
        <td style="width:10%;">
          <a href="#" class="def-nav" style="width:120px;">
            <img src="./img/logo_op.jpg" style="height:100%;width:120px;" onclick="clickLogo()" alt="公司LOGO"/>
          </a>
        </td>    
        <td>
          <a href="#" class="def-nav" style="width:15%;">报告</a>
          <a href="#" class="def-nav" style="width:10%;">文件</a>
          <a href="#" class="def-nav" style="width:70%;">文件上传并分析</a>
        </td>    
        <td style="width:6%;">
          <a href="#" class="def-nav">用户处理</a>
        </td>
    </table>
  </div>
</div>

<!-- 脚部:悬浮 -->
<div id="footSegment" style="padding:10px 10px 0 10px;display:none;"></div>

<!-- 中间主框架 -->
<div id="mainSegment">
  <div style="padding:8px;border:1px solid #ddd;">
    <table style="width:90%;">
      <tr>
        <td style="width:20%;">          
        </td>    
        <td style="width:50%;">
          <div class="input-group">
            <input id="idSearchFile" class="form-control"  type="text" style="height:37px;" placeholder="请输入查询内容...">
            <span class="input-group-btn">
              <button id="idSubmitSearchFile" class="btn btn-default" type="button" style="font:18px Microsoft YaHei,Microsoft JhengHei,黑体;">搜索</button>
            </span>
          </div>  
        </td>    
        <td style="text-align:right;">
          <a href="#" class="">
            <img src="./img/file_list.png" style="height:45px;width:45px;" onclick="showSearchResult('LIST');" title="列表预览" alt="列表预览"/>
          </a>
          <a href="#" class="">
            <img src="./img/file_thumb.png" style="height:45px;width:45px;" onclick="showSearchResult('THUMB');" title="缩略图预览" alt="缩略图预览"/>
          </a>
        </td>
    </table>    
  </div>
             
  <div style="padding:8px;border:0px solid #ddd;width:80%;margin: 0 auto;">
    <!-- 查询结果列表显示-->
    <div id="dgList" style="display:none;"></div>
    <!-- 查询结果缩略图显示 -->
    <div id="dgThumb" style="display:none;"></div>
  </div>
  
</div>

</body>
<script>
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
  myResize: initPos,
  win_min_width: 640, //页面最小的高度。当窗口高度小于这个值，不对界面位置及尺寸进行调整。主体部分宽度也照此设置
  win_min_height: 480 //页面最小的高度。当窗口高度小于这个值，不对界面位置及尺寸进行调整。主体部分高度也照此设置
};

function initPos() {
  //$("#mainFrame").spiritUtils("setWidthByViewWidth", $("body").width()-$("#sideFrame").spiritUtils("getViewWidth"));
  //$("#sideFrame").css("left", $("#reportFrame").width());
}
//主函数
$(function() {
  var initStr = $.spiritPageFrame(INIT_PARAM);
  if (initStr) {
    $.messager.alert("页面初始化失败", initStr, "error");
    return ;
  };

  $('#topSegment').css({'border':'1px solid #95b8e7','border-bottom':'0','background':'#32C52F','overflow':'hidden','border':'0','border-bottom':'0px'});
  //$('#footSegment').css({'border':'1px solid #32C52F','background':'#32C52F','opacity':'1'});
  
  initSubmitBt();
  initSearchFileInput();
});

//初始化查询输入框
var searchTxt = "请输入查询内容...";
function initSearchFileInput(){
  var _objSearch = $("#idSearchFile");
  _objSearch.keydown(function(e){
    if(e.keyCode == 13){
      alert("您输入了："+getInputSearchFileStr());
      showSearchResult("LIST");
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
    alert("您输入了："+getInputSearchFileStr());
    showSearchResult("LIST");
  });
}


//显示查询结果
function showSearchResult(showType){
    $('#dgList').css("display","none");
    $('#dgThumb').css("display","none");
    
	if(showType == "LIST"){
        showSearchResultList();
	}else if(showType == "THUMB"){
        showSearchResultThumb();
	}else{
        showSearchResultList();
	}
}

//列表显示查询结果
function showSearchResultList(){
	$('#dgList').css("display","block");
	$('#dgList').datatable
    ({
        customizable: true, checkable: true,
        // sortable: true,
        sort: function(event)
        {
        },
        mergeRows: true,
        scrollPos: 'out',
        fixedLeftWidth: '45%',
        fixedHeaderOffset: 41,
        storage: true,
        data:
        {
            cols:
            [
                {sort:'down',width:80,text: '序号',type: 'number',flex: false,colClass:'text-center dg_border_left_1',cssClass:'text-left dg_th_font_bold',css:''},
                {width:'auto',text:'文件名',type:'string',flex: false,colClass:'text-left dg_border_left_1',cssClass:'text-left dg_th_font_bold'},
                {width:100,text:'文件类型',type:'string',flex: false,colClass:'text-left dg_border_left_1 ',cssClass:'text-left dg_th_font_bold'},
                {width: 100,text:'大小',type:'number',flex: true,colClass:'text-right dg_border_left_1 ',cssClass:'text-right dg_th_font_bold'},
                {width:150,text:'创建日期',type:'date',flex: true,colClass:'text-left dg_border_left_1 ',cssClass:'text-left dg_th_font_bold',mergeRows: false},
                {sort:false,width:250,text:'简述',type:'string',flex: true,colClass:'text-left dg_border_left_1 ',cssClass:'text-left dg_th_font_bold'}
            ],
            rows:
            [
                {checked: false,data:['1', '<a href="###">文件1</a>', 'XLSX','3M', '2014-08-01', '天气数据'],cssClass:'dg_td_bgcolor_lightblue '},
                {checked: true,data: ['2', '<a href="###">文件2</a>', 'XLSX','33M', '2014-04-02', '天气数据']},
                {checked: true,data: ['3', '<a href="###">文件3</a>', 'JSON','2M', '2015-04-03', '金融数据'],cssClass:'dg_td_bgcolor_lightblue '},
                {checked: true,data: ['4', '<a href="###">文件4</a>', 'WORD','5M', '2015-04-04', '教育数据']},
                {checked: true,data: ['5', '<a href="###">文件5</a>', 'JPG','14M', '2015-04-05', '图片数据'],cssClass:'dg_td_bgcolor_lightblue '},
                {checked: true,data: ['6', '<a href="###">文件6</a>', 'XLSX','33M', '2014-04-02', '天气数据']},
                {checked: true,data: ['7', '<a href="###">文件7</a>', 'JSON','2M', '2015-04-03', '金融数据'],cssClass:'dg_td_bgcolor_lightblue '},
                {checked: true,data: ['8', '<a href="###">文件8</a>', 'XLSX','33M', '2014-04-02', '天气数据']},
                {checked: true,data: ['9', '<a href="###">文件9</a>', 'JSON','2M', '2015-04-03', '金融数据'],cssClass:'dg_td_bgcolor_lightblue '},
                {checked: true,data: ['10', '<a href="###">文件10</a>', 'XLSX','33M', '2014-04-02', '天气数据']},
                {checked: true,data: ['11', '<a href="###">文件11</a>', 'JSON','2M', '2015-04-03', '金融数据'],cssClass:'dg_td_bgcolor_lightblue '},
                {checked: true,data: ['12', '<a href="###">文件12</a>', 'XLSX','33M', '2014-04-02', '天气数据']},
                {checked: true,data: ['13', '<a href="###">文件13</a>', 'JSON','2M', '2015-04-03', '金融数据'],cssClass:'dg_td_bgcolor_lightblue '},
                {checked: true,data: ['14', '<a href="###">文件14</a>', 'XLSX','33M', '2014-04-02', '天气数据']},
                {checked: true,data: ['15', '<a href="###">文件15</a>', 'JSON','2M', '2015-04-03', '金融数据'],cssClass:'dg_td_bgcolor_lightblue '},
                {checked: true,data: ['16', '<a href="###">文件16</a>', 'XLSX','33M', '2014-04-02', '天气数据']},
                {checked: true,data: ['17', '<a href="###">文件17</a>', 'JSON','2M', '2015-04-03', '金融数据'],cssClass:'dg_td_bgcolor_lightblue '}
            ]
        }
    }).on('sort.zui.datatable', function(event)
    {
    });
}

//缩略图显示查询结果
function showSearchResultThumb(){
	var _objThumb = $('#dgThumb');
	_objThumb.css("display","block");
	var thumbHtmlStr = '';
    thumbHtmlStr += '<section class="cards">';
	for(var i=1;i<21;i++){
	    thumbHtmlStr += '  <div class="col-md-4 col-sm-6 col-lg-3">';
	    thumbHtmlStr += '    <a href="###" class="card">';
	    thumbHtmlStr += '      <img src="./img/img2.jpg" alt="">';
	    thumbHtmlStr += '      <span class="caption">关于图片的说明'+i+'</span>';
	    thumbHtmlStr += '      <strong class="card-heading">图片标题飞'+i+'</strong>';
	    thumbHtmlStr += '    </a>';
	    thumbHtmlStr += '  </div>';		
	}
    thumbHtmlStr += '</section>';      
    
    _objThumb.html(thumbHtmlStr);
	
}

//获得输入的查询内容
function getInputSearchFileStr(){
  var searchedStr = ($("#idSearchFile").val()==searchTxt)?"":$("#idSearchFile").val();
  return searchedStr;
}

function clickLogo(){
  alert("click logo");
}
</script>
</html>