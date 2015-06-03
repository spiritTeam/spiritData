/**
 * 精灵报告解析及展示方法
 * 需要用到：
 * JS——jquery+spirit.pageFrame
 * CSS——在本文件所在目录的css目录下的report.css文件中
 * sysInclude.jsp——从这里取根_PATH
 */

/**
 * 主函数，生成报告。
 * 报告的生成，需要一个干净的html容器。
 * 注意：
 * 1-执行此方法的html页面，此方法会把这个页面中的body内容全部清空。
 * 2-次方法会自动加载需要的资源——(这个功能后续再处理，目前需要的资源需要先行在html中加载)
 * 3-目前报告中的图形采用jqplot+eChart控件，表格采用easyUi的表格，提示的窗口采用本框架封装的功能
 * @param param 解析报告的参数，主要是获得报告的必要信息，包括
 * param = {
 *   getUrl: 获得报告的Url比如getReport.do，可为空，默认为：report/getReport.do?（一般不用设置，除非有新的获得报告的方法），注意设置时，开头不要以“/”开头，以此为开头，将不会自动加入_PATH的前缀
 *   reportUri：获得报告json的直接Uri
 *   reportId：报告Id
 * }
 */
function generateReport(param) {
  //1-参数校验
  var checkOk = true;
  var _msg = "", _temp = null, _url = null;
  //1.1-校验整个参数
  if (!param) {
    _msg = "参数为空，无法显示报告！";
    if (mPage) mPage.$.messager.alert("提示", _msg, "error");
    else alert(_msg);
    checkOk = false;
  }
  //1.2-校验报告Id或Uri，若有Uri，则Id被忽略
  if (checkOk) {
    _temp = param.reportUri;
    if (_temp&&$.trim(_temp)!=""&&$.trim(_temp)!="undefined") {
      _url = "?uri="+encodeURIComponent(param.reportUri);
    } else {
      _temp = param.reportId;
      if (_temp&&$.trim(_temp)!=""&&$.trim(_temp)!="undefined") {
        _url = "?reportId="+param.reportId;
      } else {//两个参数都没有设置，出错了
        _msg="报告Uri或Id至少指定一项，目前两项都未指定，无法显示报告！";
        if (mPage) mPage.$.messager.alert("提示", _msg, "error");
        else alert(_msg);
        checkOk = false;
      }
    }
  }
  if (!checkOk) return;//若校验不通过，则后面的逻辑都不做了
  //1.3-组装获取report数据的Url
  _temp = param.getUrl;
  if (_temp&&$.trim(_temp)!=""&&$.trim(_temp)!="undefined") {
  	_temp=decodeURIComponent(_temp);
    if (_temp.indexOf("/")!=0&&_temp.indexOf("\\")!=0) {//无根，加上根
      _temp=_PATH+"/"+_temp;
    }
    _url = _temp+_url;
  } else {
    _url = _PATH+"/report/getReport.do"+_url;
  }
}