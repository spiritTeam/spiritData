<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
  String path = request.getContextPath();
  String username = (String)session.getAttribute("username");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <title>import data page</title>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <!-- jquery+easyui -->
  <jsp:include page="/common/sysInclude.jsp" flush="true"/>
  <!-- upload -->
  <script  type="text/javascript"  src="<%=path %>/resources/js/ajaxfileupload.js"></script>
  <style type="text/css">
  body {margin:0 auto; width:100%;height: 100%}
  .mouseOut{
    width:70px;
    border:1px solid #ABCDEF;
    text-align: right;
    background-image: url(accept.png);
    background-repeat:no-repeat;
    background-position:left;
  }
  .mouseOver{
    width:70px;
    border:1px;
    border-style: inset;
    border-color: #ABCDEF;
    text-align: right;
    background-image: url(accept.png);
    background-repeat:no-repeat;
    background-position:left;
  }
  </style>
</head>
<body>
<center>
<div style="width: 800px;height:500px;border: 1px solid #ABCDEF;">
  <div style="width: 550px;height:500px;float: left;border-right: 1px solid #ABCDEF;">
    <div id="uploadDiv" style="width: 550px;height: 100px;" >
      <div style="height: 50px;text-align: left;" ><div style="height: 20px;"></div><span style="margin-left: 15px;font-size: 20px;font-weight: bold;">第一步:导入文件</span><div style="height: 20px;"></div></div>
      <div style="width: 400px;height: 20px;">
        <table>
          <tr>
            <td><span style="font-size: 16px;">选择excel文件</span></td>
            <td align="right"><input id="excelFile" contentEditable="false" type="file" name="excelFile" style="width:150px;border:1px solid #ABCDEF;"/></td>
          </tr>
        </table>
        <div style="height: 10px;"></div>
        <div style="width: 450px;text-align: right">
          <div style="margin-right: 20px;">
            <input id="uploadButton" type="button" value="开始上传"  onclick="startImport('excelFile');" onmouseover="mouseOver(this)" onmouseout="mouseOut(this)"/>
          </div>
        </div>
      </div>
    </div>
    <div style="height: 20px;"></div>
    <div style="height: 20px;text-align: left;">
      <div style="height: 5px;"></div>
      <span style="margin-left: 15px;font-size: 20px;font-weight: bold;">第二步:指定参数</span>
    </div>
    <div style="height: 10px;"></div>
    <div id="parameterDiv" style="width:550px;height:110px;" >
      <div style="height: 10px;"></div>
      <table id="selectTable" width="400px;" border="1px;" bordercolor="#ABCDEF" >
        <tr align="center" height="22px;" style="font-size: 21px;font-weight: bold;"><td>Sheet名称</td><td>部门</td><td>对应主键</td></tr>
        <tr id="demoTr" style="display: ;font-size: 20px;">
          <td align="center">
            <span>sheetA</span>
          </td>
          <td align="center">
            <select id="signSelect" style="width:150px;font-size: 12px;border:1px solid #ABCDEF;">
              <option></option><option>groupA</option><option>groupB</option><option>groupC</option>
            </select>
          </td>
          <td align="center">
            <select id="pkSelect" style="width:150px;font-size: 12px;border:1px solid #ABCDEF;" ></select>
          </td>
        </tr>
      </table>
      <div style="height: 10px;"></div>
      <div style="width: 500px;text-align: right;">
        <div style="margin-right:20px"><input id="saveDataButton" type="button" value="保存数据" onclick="sendPkAndCacheId();"  onmouseover="mouseOver(this)" onmouseout="mouseOut(this)"/></div> 
      </div>
    </div>
    <div style="height: 15px;"></div>
    <div style="height: 20px;text-align: left;">
      <div style="height: 5px;"></div>
      <span style="margin-left: 15px;font-size: 20px;font-weight: bold;">执行结果</span>
    </div>
    <div style="height: 20px;"></div>
    <div id="resultDiv" style="width: 400px;">
      <div id="step1ResultTitle" style="text-align: left;display: none;"><span style="font-size:16px;font-weight: bold; ">第一步返回结果：</span></div>
      <div id="step1Result" style="text-align: left;word-wrap :break-word;font-size:16px;"></div>
      <div style="height: 7px;"></div>
      <div id="step2ResultTitle" style="text-align: left;display: none;"><span style="font-size:16px;font-weight: bold; ">第二步返回结果：</span></div>
      <div id="step2Result" style="text-align: left;word-wrap :break-word;font-size:16px;"></div>
    </div>
  </div>
  <div id="mainDiv" style="width:249px;height:500px;float: left" > 
    <div id="rightDiv" style="width:200px;height:400px;">
      <div  style="height: 5px;text-align: left"></div>
      <div id="desc"><br>
        <span id="descTitle" style="font-size: 20px;font-weight: bold;"></span><br><br>
        <div id="descBody" style="width:200px; text-align: left;word-wrap :break-word;font-size: 16px;"></div>
      </div>
    </div>
  </div>
</div>
</center>
</body>
<script type="text/javascript">
/**空格*/
var nbsp='&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;';
/**主键重复，主键空*/
var repeatMessage = "",nullMessage = "";
/**对应的比对结果，相应的后台缓存id*/
var matchResult = '',cacheId = '';
/**匹配信息，主键名*/
var matchInfo = '',pkName = '';
var selectAndCacheIdArry='';
$(function(){
  init();
});
function init(){
  initButton();
  initDesc();
  initDemoSelect();
}
function initDemoSelect(){
  $('#pkSelect').attr('disabled',true);
  $('#signSelect').attr('disabled',true);
}
function initDesc(){
  $('#descTitle').html("程序执行流程");
  $('#descBody').html(nbsp+nbsp+"第一步：导入文件，点击\"开始上传\"按钮，系统将根据excel文件结构与现有的数据表的结构进行匹配。</br>"+
    "<div style='height:5px;'></div>"+nbsp+nbsp+"第二步：指定必要参数，如匹配到相同结构的数据表，则只需要指定部门即可，如未匹配到相应的数据表，则需要指定主键和部门两个必要参数。</br>"+
    "<div style='height:5px;'></div>"+nbsp+nbsp+"第三步：保存数据，点击\"保存数据\"按钮，程将对数据处理，并返回结果。此过程可能会稍长，请耐心等待<img src='emoticon_smile.png'/>。"
  );
}
function initButton(){
  $('#uploadButton').addClass('mouseOut');
  $('#saveDataButton').addClass('mouseOut');
}
function mouseOver(obj){
  $(obj).removeClass('mouseOut');
  $(obj).addClass('mouseOver');
}
function mouseOut(obj){
  $(obj).removeClass('mouseOver');
  $(obj).addClass('mouseOut');
}
function sendPkAndCacheId(){
  if((nullMessage!=""&&nullMessage!=null)||(repeatMessage!=""&&repeatMessage!=null)){
    $.messager.alert('上传数据提示',nbsp+nbsp+'您上传的excel主键位可能为空值或重复，请修改后重新上传，详情请查看执行结果','info');
    return;
  }
  for(var i=0;i<selectAndCacheIdArry.length;i++){
	  var pkSelectId = 'pkSelect'+i;
    var signSelectId = 'signSelect'+i;
    var pkSelectVal = $('#'+pkSelectId).val();
    var signSelVal = $('#'+signSelectId).val();
    if(pkSelectVal==null||pkSelectVal==''||signSelVal==null||signSelVal==''){
    	$.messager.alert('主键标识选择提示','您还有主键或标识没有选择','info');
    	return;
    }
  }
  var pkCacheIdSignStr = getSelectData();
  $.ajax({
    type:'post',
    url:'<%=path %>/saveData.do?pkCacheIdSignStr='+pkCacheIdSignStr,
    //data:pkCacheIdSignStr,
    success:function(data){
    	//mht
    	clearTable();
      initStep2(data);
    }
  });
}
function getSelectData(){
	var pkCacheIdSignStr = '';
	for(var i=0;i<selectAndCacheIdArry.length;i++){
		var pkSelectVal = $('#'+selectAndCacheIdArry[i].pkSelectId).val();
		var signSelVal = $('#'+selectAndCacheIdArry[i].signSelectId).val();
		var sheetName = selectAndCacheIdArry[i].sheetName;
		var cacheId = selectAndCacheIdArry[i].cacheId;
		var str;
		if(i<selectAndCacheIdArry.length-1){
			str= pkSelectVal+','+signSelVal+','+cacheId+','+sheetName+";";
		}else{
			str= pkSelectVal+','+signSelVal+','+cacheId+','+sheetName;
		}
		pkCacheIdSignStr = pkCacheIdSignStr+str;
	}
	return pkCacheIdSignStr;
}
function initStep2(resultData){
	var allRows,insertRows,saveResult;
	var sheetName,updataRows;
	var step2Mesage = nbsp+nbsp;
	$('#step2ResultTitle').css('display',"");
	for(var i=0;i<resultData.length;i++){
		var oneResult = resultData[i];
		saveResult = oneResult.saveResult;
		sheetName = oneResult.sheetName;
		allRows = oneResult.allRows;
		insertRows = oneResult.insertRows;
		updataRows = allRows-insertRows;
		if(saveResult==true){
			step2Mesage = step2Mesage+'在sheet名为"'+sheetName+'"的sheet中，执行保存数据成功，本次一共上传'+allRows+'条数据，其中新增'+insertRows+'条，更新'+updataRows+'条。<br>'+nbsp+nbsp;
		}else{
			$.messager.alert('保存数据提示',nbsp+nbsp+'您上传的excel主键位可能为空值或重复，请修改后重新上传，详情请查看执行结果','info');
	    if(nullMessage!=""&&nullMessage!=null){
	      step2Mesage = step2Mesage+nullMessage;
	    }
	    if(repeatMessage!=""&&repeatMessage!=null){
	      step2Mesage = step2Mesage+'<br>'+nbsp+nbsp+repeatMessage+'<br>';
	    }
		}
	}
	$('#step2Result').html(step2Mesage);
  afterSaveSuccess();
}
function afterSaveSuccess(){
	$('#saveDataButton').attr('disabled',true);
	$('#demoTr').css('display','');
	$('#signSelect').attr('disabled',true);
  $('#pkSelect').attr("disabled",true);
  $('#excelFile').val('');
  repeatMessage = "",nullMessage = "";
  matchResult = '',cacheId = '';
  matchInfo = '',pkName = '';
}
function clearTable(){
  var tb = $("#selectTable");
  tb = document.getElementById("selectTable");
  var rowNum=tb.rows.length;
  alert(rowNum);
  for (var i=2;i<rowNum;i++){
    tb.deleteRow(i);
    rowNum=rowNum-1;
    i=i-1;
  }
}

function startImport(type){
  var f = $("#excelFile").val();
  if(f==""||f==null){
    $.messager.alert('上传提示',nbsp+nbsp+"请选择一个文件。",'info');
    return;
  }else{
    var t = f.substring(f.lastIndexOf('.'),f.length);
    if(t==".xls"||t==".xlsx"){
      toUp(type);
    }else{
      $.messager.alert("上传提示",nbsp+nbsp+"请选择excel文件。","info");
      return;
    }
  }
}
function toUp(type) {
  //上传文件
  $.ajaxFileUpload({
    //跟具updateP得到不同的上传文本的ID
    url:'<%=path %>/fileUpLoad.do.do',//需要链接到服务器地址
    secureuri:false,
    fileElementId:''+type+'',//文件选择框的id属性（必须）
    dataType:'json',
    success:function (data, status){
      var fileUploadInfo = data.data;
      var uploadContent = fileUploadInfo[0];
      var pkCacheMapList =uploadContent.pkCacheMapList;
      initSelectAndStpe1(pkCacheMapList);
    }
  });
}
function initSelectAndStpe1(pkCacheMapList){
	initSelect(pkCacheMapList);
	afterUpload();
  $('#step1ResultTitle').css("display","");
  if((nullMessage!=""&&nullMessage!=null)||(repeatMessage!=""&&repeatMessage!=null)){
    $.messager.alert('上传提示',nbsp+nbsp+'您上传的excel主键位可能为空值或重复，请修改后重新上传，详情请查看执行结果。','info');
  }
  var step1Mesage = nbsp+nbsp;
  if(nullMessage!=""&&nullMessage!=null){
    step1Mesage = step1Mesage+nullMessage;
  }
  if(repeatMessage!=""&&repeatMessage!=null){
    step1Mesage = step1Mesage+'<br>'+nbsp+nbsp+repeatMessage;
  }
  $('#step1Result').html(step1Mesage);
  $('#step1Result').html(nbsp+nbsp+matchInfo);
  if(matchResult==true){
    $('#signSelect').attr('disabled',false);
    $('#pkSelect').val(pkName);
    $('#pkSelect').attr("disabled",true);
    return;
  }else{
    $('#signSelect').attr('disabled',false);
    $('#pkSelect').attr("disabled",false);
    return;
  }
}
function initSelect(pkCacheMapList){
	var td;
  var tr;
  $('#demoTr').css('display','none');
  /**循环sheet，并且赋值常量*/
  selectAndCacheIdArry = new Array([pkCacheMapList.length]);
  selectAndCacheIdArry.shift();
  for(var i=0;i<pkCacheMapList.length;i++){
	  var selectAndCacheId = ({
		  sheetName:'',
		  trId :'',
	    cacheId:'',
	    pkSelectId:'',
	    signSelectId:''
	  });
	  /**initSelect用 的变量*/
    var singlePkCacheMap = pkCacheMapList[i];
    var titleArray = singlePkCacheMap.titleList;
    var sheetName = singlePkCacheMap.sheetName;
    matchResult = singlePkCacheMap.matchResult;
    var pkSelectId = 'pkSelect'+i;
    var signSelectId = 'signSelect'+i;
    var trId = 'tr'+i;
    if(matchResult==true){
    	pkName = singlePkCacheMap.pkName;
    	var pkSelect = '<select id="'+pkSelectId+'" style="width:150px;font-size: 12px;border:1px solid #ABCDEF;" ><option>'+pkName+'</option></select>';
      /**循环title*/
      var signSelect = '<select id="'+signSelectId+'" style="width:150px;font-size: 12px;border:1px solid #ABCDEF;"><option></option><option>groupA</option><option>groupB</option><option>groupC</option></select>';
      td = $('<td align="center">'+sheetName+
          '</td><td align="center">'+signSelect+'</td><td align="center">'+pkSelect+'</td>');
      tr = $('<tr id="'+trId+'" height="20px;"></tr>').append(td);
      $('#selectTable').append(tr);
    }else{
    	var pkSelect = '<select id="'+pkSelectId+'" style="width:150px;font-size: 12px;border:1px solid #ABCDEF;" ><option></option>';
      /**循环title*/
      for(var k=0;k<titleArray.length;k++){
    	  pkSelect =pkSelect+'<option>'+titleArray[k]+'</option>';
      }
      pkSelect = pkSelect +'</select>';
      var signSelect = '<select id="'+signSelectId+'" style="width:150px;font-size: 12px;border:1px solid #ABCDEF;"><option></option><option>groupA</option><option>groupB</option><option>groupC</option></select>';
      td = $('<td align="center">'+sheetName+
          '</td><td align="center">'+signSelect+'</td><td align="center">'+pkSelect+'</td>');
      tr = $('<tr id="'+trId+'" height="20px;"></tr>').append(td);
      $('#selectTable').append(tr);
    }
    /**常量赋值*/
    if(singlePkCacheMap.nullMessage!=""&&singlePkCacheMap.nullMessage!=null){
    	nullMessage = nullMessage+singlePkCacheMap.nullMessage;
    }
    if(singlePkCacheMap.repeatMessage!=""&&singlePkCacheMap.repeatMessage!=null){
    	repeatMessage = repeatMessage+singlePkCacheMap.repeatMessage;
    }
    if(i!=pkCacheMapList.length-1){
    	matchInfo = matchInfo+"在名为"+sheetName+"的sheet中，"+singlePkCacheMap.matchInfo+'<br>'+nbsp+nbsp;
    }else{
    	matchInfo = matchInfo+"在名为"+sheetName+"的sheet中，"+singlePkCacheMap.matchInfo;
    }
   	
    matchResult='';
   	/**给传id对象selectAndCacheId赋值*/
   	selectAndCacheId.sheetName = sheetName;
    selectAndCacheId.cacheId = singlePkCacheMap.cacheId;
    selectAndCacheId.pkSelectId = pkSelectId;
    selectAndCacheId.signSelectId =signSelectId;
    selectAndCacheId.trId = trId;
    selectAndCacheIdArry.push(selectAndCacheId);
  }
  return selectAndCacheIdArry;
}
function afterUpload(){
  $('#saveDataButton').attr('disabled',false);
  $('#signSelect').val("");
  $('#signSelect').attr('disabled',false);
  $('#pkSelect').attr("disabled",false);
}
</script>
</html>