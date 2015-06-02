/**
 * 精灵报告解析及展示方法
 * 需要用到：
 * JS——jquery+spirit.pageFrame
 * CSS——在本文件所在目录的css目录下的report.css文件中
 */

/**
 * 主函数，生成报告。
 * 报告的生成，需要一个干净的html容器。
 * 注意：
 * 1-执行此方法的html页面，此方法会把这个页面中的body内容全部清空。
 * 2-次方法会自动加载需要的资源——(这个功能后续再处理，目前需要的资源需要先行在html中加载)
 * 3-目前报告中的图形采用jqplot+eChart控件，表格采用easyUi的表格，提示的窗口采用本框架封装的功能
 * @param reportUrl，报告的url，是获取report.json的Uri，可以传入具体的可访问的文件，也可以是report的id
 */
function generateReport(reportUrl) {
	
}