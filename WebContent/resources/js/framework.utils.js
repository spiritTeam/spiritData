//框架通用方法集，处理与框架结构相关逻辑
/**
 * 得到框架首页
 */
function getMainPage() {
  var mainPage = null, win = window, pWin = null;
  while (!mainPage) {
    if (win.IS_MAINPAGE) mainPage = win;
    else {
      pWin = win.top;
      if (pWin==win||pWin==null) pWin = win.opener;
      if (pWin==win||pWin==null) break;
      win=pWin;
    }
  }
  return mainPage;
}

/**
 * 创建并打开easyUi的窗口
 * @param winOption是一个js对象，目前支持如下参数
 * winOption.title 窗口标题
 * winOption.url 窗口内嵌的iframe的url
 * winOption.height 窗口高度
 * winOption.width 窗口宽度
 * winOption.icon_css 窗口图标，是css中的名称
 * winOption.icon_url 窗口图标，图标的url，若设置了此参数，icon_css失效
 * winOption.modal 是否是模态窗口，默认为模态窗口
 * winOption.expandAttr 窗口的扩展属性，可定义iframe的id，是javaScript对象，如expandAttr={"frameID":"iframeID"}
 * @returns 返回生成窗口的UUID
 */
function openWin(winOption) {
  var mainPage = getMainPage();
  if (mainPage) return mainPage.newWin(winOption);
}

/**
 * 创建并打开简单模态窗口
 * @param winOption是一个js对象，目前支持如下参数
 * winOption.title 窗口标题
 * winOption.url 窗口内嵌的iframe的url
 * winOption.height 窗口高度
 * winOption.width 窗口宽度
 * winOption.minButton 最小化窗口按钮是否显示
 * winOption.expandAttr 窗口的扩展属性，可定义iframe的id，是javaScript对象，如expandAttr={"frameID":"iframeID"}
 * @returns 返回生成窗口的UUID
 */
function openSWin(winOption) {
  var mainPage = getMainPage();
  if (mainPage) return mainPage.newSWin(winOption);
}

/**
 * 关闭并销毁窗口
 * @param winId 窗口的ID
 */
function closeWin(winId) {
  var mainPage = getMainPage();
  if (mainPage) mainPage.closeWin(winId);
}

/**
 * 得到窗口jquery对象
 * @param winId 窗口的ID
 */
function getWin(winId) {
  var mainPage = getMainPage();
  if (mainPage) mainPage.getWin(winId);
}

//对DOM对象的处理
/**
 * 找到顶层页面
 */
function getTopWin() {
  var topWin = window.top;
  while (topWin.openner) topWin=topWin.operner.top;
  return topWin;
}