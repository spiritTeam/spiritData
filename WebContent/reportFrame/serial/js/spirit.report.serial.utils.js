/**
 * 报告展示的通用方法，主要是一个壳子。
 * 包括如下功能：
 * 1-解析相应的参数
 * 2-打开简单窗口的页面
 * 3-调用解析方法
 * 需要基础包:
 * jquery/本框架simpleWin/sysInclude.jsp(若有这个jsp，则前两项不用加载了，这个jsp中都包括)
 */

/**
 * 定义SerialShow对象，作为命名空间来使用，以免过多的showReport的干扰
 */
var ReportSerialShow = {
  /**
   * 显示报告。按照顺序方式显示
   * @param param={
   *   reportUri: //报告json的可访问文件位置，如/DataCenter/report/after(9234_234).json
   *   reportId: //报告id
   *   //以上两个参数至少要设置一个，若有Uri，则Id不起作用
   *   reportGetUrl: //获得report的json的Url，可以为空，也可以设置，目前不用设置，默认的为report/getReport.do
   *   //以下为窗口设置
   *   title: //窗口标题拦，默认为“报告详情”
   *   height: //窗口高度，默认为580
   *   width: //窗口宽度，默认为900
   *   iframeScroll: //窗口时否有滚动条，默认有滚动条
   * }
   */
  show:function(param) {
    var mPage =getMainPage();
    var _msg = "";
    if (!param) {
      _msg = "参数为空，无法显示报告！";
      if (mPage) mPage.$.messager.alert("提示", _msg, "error");
      else alert(_msg);
      return;
    }

    var winOption = new Object();
    //连接
    winOption.url = _PATH+"/reportFrame/serial/showSerial.jsp";
    var uri=param.reportUri;
    if (uri&&$.trim(uri)!=""&&$.trim(uri)!="undefined") winOption.url+="?reportUri="+encodeURIComponent(uri);
    else {
      var rid= param.reportId;
      if (rid&&$.trim(rid)!=""&&$.trim(rid)!="undefined") winOption.url+="?reportId="+rid;
      else {//出错了
        _msg="报告Uri或Id至少指定一项，目前两项都未指定，无法显示报告！";
        if (mPage) mPage.$.messager.alert("提示", _msg, "error");
        else alert(_msg);
        return;
      }
    }
    //处理窗口显示参数
    winOption.title=(param.title?param.title:"报告详情");
    winOption.height=(param.height?param.height:580);
    winOption.width=(param.width?param.width:900);
    winOption.iframeScroll=(param.iframeScroll?param.iframeScroll:"yes");
    //获得report.json的Uri
    if (param.reportGetUrl) winOption.url+="&getUrl="+encodeURIComponent(param.reportGetUrl);
    //打开窗口
    openSWinInMain(winOption);
  },

  /**
   * 按id显示报告页面
   * @param reportId 报告Id
   * @param winOption 报告页面的参数，可为空。如下：
   * winOption={
   *   title: //窗口标题拦
   *   height: //窗口高度
   *   width: //窗口宽度
   *   iframeScroll: //窗口时否有滚动条
   * }
   */
  showById:function(reportId, winOption) {
    var newParam = new Object();
    if (winOption) newParam = $.extend(true, {}, winOption);
    newParam.reportId = reportId;
    this.show(newParam);
  },

  /**
   * 按Uri显示报告页面
   * @param reportUri 报告Uri
   * @param winOption 报告页面的参数，可为空。如下：
   * winOption={
   *   title: //窗口标题拦
   *   height: //窗口高度
   *   width: //窗口宽度
   *   iframeScroll: //窗口时否有滚动条
   * }
   */
  showByUri:function (reportUri, winOption) {
    var newParam = new Object();
    if (winOption) newParam = $.extend(true, {}, winOption);
    newParam.reportUri = reportUri;
    this.show(newParam);
  }
};