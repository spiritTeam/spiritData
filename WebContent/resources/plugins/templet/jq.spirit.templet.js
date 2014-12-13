/**
 *框架流程:
 *1、接收templet,
 *2、分析templet中的element，并且按照showType分类
 *3、解析<d>中的属性，如value。。
 *4、根据showType选择处理文件的方法。
 */
function templetResolve(templet){
  //访问jsond的url
  var dataAry = templet._DATA;
  //得到templet主体
  var _Templet = templet._Templet
  //sug数组
  var sugAry;
  //确定元素个数，
  //var dataAry =  
}
