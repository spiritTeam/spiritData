/**
 * 访问信息收集方法
 * 注意，这个js需要sysInclude.jsp要引用
 */

/**
 * 上传访问日志信息
 * @param objInfo 对象的属性，包括：objType, objId，或objUrl，或fromUrl
 * 参数解释为：
 * objInfo= {
 *   objType:类型，若类型为空，则系统自动按纯页面进行处理
 *   objId:类型对象的Id
 *   objUrl:类型对象的Url，以上两个参数至少要有一个设置，否则也会按照纯页面处理
 *   fromUrl:可以设置，也可以不设置
 * }
 */
function visitLog(objInfo) {
  //获得纯页面信息
  function getPurePage() {
    objInfo = new Object();
    objInfo.objType=99;
    objInfo.objUrl=encodeUrlComponent(window.location.href);
    return objInfo;
  }

  var param = new Object();
  if (!objInfo||!objInfo.objType) {//若没有参数，则只把当前的页面情况上传，objType=99//这类是普通页面
    param = getPurePage();
  } else {
    var objId = objInfo.objId+"";
    var objUrl = objInfo.objUrl+"";
    if (($.trim(objId)+$.trim(objUrl))=="") param = getPurePage();
    else {
      param.objId=objId;
      param.objUrl=objUrl;
    }
    if ($.trim(objInfo.fromUrl+"")=="") param.fromUrl=encodeUrlComponent(window.location.href);
  }
  //地图信息，点位信息
  var _temp = _getPointInfo();
  if (_temp&&(_temp instanceof string)) param.poinInfo = temp;
  //IP+Mac
  _temp = _getClientInfo();
  if (_temp) {
    param.clientIp=_temp.clientIp;
    param.clientMac=_temp.clientMac;
  }
  //设备信息，可能还要有操作系统的信息，但现在没有
  _temp = _getEquipInfo();
  if (_temp) {
    param.equipName=_temp.equipName;
    param.equipVer=_temp.equipVer;
  }
  //浏览器信息
  _temp = _getExploreInfo();
  if (_temp) {
    param.exploreName=_temp.exploreName;
    param.exploreVer=_temp.exploreVer;
  }
  if (nowInfo)
  param.objType=objType;

  alert(allFields(param));
  alert(_PATH);
  $.ajax({
  	type: "POST",
  	url: _PATH+"/vLog/gather.do",
  	async: true,
  	data: param
  });
}

/**
 * 上传访问日志信息
 * @param reportInfo
 * @param reportInfo 报告对象，包括：objId或objUrl
 * 参数解释为：
 * reportInfo= {
 *   reportId:类型对象的Id
 *   reportUrl:类型对象的Url，以上两个参数至少要有一个设置，否则也会按照纯页面处理
 * }
 */
function visitLog_REPORT(reportInfo) {
	var param = new Object();
	param.objType=1;
	param.objId=reportId;
	param.objUrl=reportInfo.reportUrl;
	visitLog(param);
}

//以下是生成各类信息的数据
/**
 * 获得当前的点位信息
 * 这个信息应该是一个json串，如GIS标准：经纬度+高程，注意是字符串
 */
/*目前还得不到这样的信息*/
function _getPointInfo() {
  return null;
}

/**
 * 获得客户端的Ip和Mac地址
 * 返回值是一个对象
 */
/*目前还得不到这样的信息*/
function _getClientInfo() {
  return null;
}

/**
 * 获得客户端的设备信息，设备型号及版本号，如手机的厂商/手机的版本
 * 返回值是一个对象
 */
/*目前还得不到这样的信息*/
function _getEquipInfo() {
  return null;
}

/**
 * 获得客户端的浏览器信息
 * 返回值是一个对象
 */
/*目前还得不到这样的信息*/
function _getExploreInfo() {
  return null;
}