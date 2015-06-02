/**
 * 报告展示的通用方法，主要是一个壳子。
 * 包括如下功能：
 * 1-解析相应的参数
 * 2-打开简单窗口的页面
 * 3-调用解析方法
 * 需要jquery作为基础包
 */

/**
 * 显示报告。按照顺序方式显示
 * @param param={
 *   reportUri: //报告json的可访问文件位置，如/DataCenter/report/after(9234_234).json
 *   reportId: //报告id
 *   //以上两个参数至少要设置一个
 *   reportGetUri: //获得report的json的Uri，可以为空，也可以设置，目前不用设置，默认的为report/getReport.do
 *   //以下为窗口设置
 *   title: //窗口标题拦，默认为“报告详情”
 *   height: //窗口高度，默认为600
 *   width: //窗口宽度，默认为900
 *   iframeScroll: //窗口时否有滚动条，默认有滚动条
 * }
 */
function showReportSerial(param) {
	var mPage =getMainPage();
	var _msg = "";
	if (!param) {
		_msg = "参数为空，无法显示报告！";
		if (mPage) mPage.$.messager.alert("提示", _msg, "error");
		else alert(_msg);
		return;
	}
	var winOption = new Object();
	
}

/**
 * 按id显示报告页面
 * @param reportId 报告Id
 * @param winOption 报告页面的参数,，如下：
 * winOption={
 *   title: //窗口标题拦
 *   height: //窗口高度
 *   width: //窗口宽度
 *   iframeScroll: //窗口时否有滚动条
 * }
 */
function showReportById(reportId, winOption) {
	var newParam = $.extend(true, {}, winOption);
	newParam.reprotId = reportId;
	showReport(newParam);
}

/**
 * 按Uri显示报告页面
 * @param reportUri 报告Uri
 * @param winOption 报告页面的参数
 * winOption={
 *   title: //窗口标题拦
 *   height: //窗口高度
 *   width: //窗口宽度
 *   iframeScroll: //窗口时否有滚动条
 * }
 */
function showReportByUri(reportUri, winOption) {
	var newParam = $.extend(true, {}, winOption);
	newParam.reprotUri = reportUri;
	showReport(newParam);
}