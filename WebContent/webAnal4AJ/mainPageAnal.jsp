<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%
  String path = request.getContextPath();
%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<!-- 关系型数据模板 -->
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="cache-control" content="no-cache"/>
<meta http-equiv="pragma" content="no-cache"/>
<meta http-equiv="expires" content="0"/>
<jsp:include page="/common/sysInclude.jsp" flush="true"/>

<link rel="stylesheet" type="text/css" href="<%=path%>/resources/plugins/spiritui/themes/default/pageFrame.css"/>

<script type="text/javascript" src="<%=path%>/resources/plugins/flot/excanvas.min.js"></script>
<script type="text/javascript" src="<%=path%>/resources/plugins/flot/jquery.flot.min.js"></script>
<script type="text/javascript" src="<%=path%>/resources/plugins/flot/jquery.flot.pie.min.js"></script>
<script type="text/javascript" src="<%=path%>/resources/plugins/flot/jquery.flot.time.min.js"></script>
<script type="text/javascript" src="<%=path%>/resources/plugins/flot/jquery.flot.categories.min.js"></script>
<script type="text/javascript" src="<%=path%>/resources/plugins/Chart.min.js"></script>

<script type="text/javascript" src="<%=path%>/resources/plugins/spiritui/jq.spirit.pageFrame.js"></script>
<!-- ECharts单文件引入 -->
<script src="<%=path%>/resources/plugins/echarts-2.2.1/echarts.js"></script>

<title>安监局首页分析</title>
</head>
<style>
body {background-color:#fff;}
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
</style>
<body style="background-color:#FFFFFF">
<!-- 头部:悬浮
<div id="topSegment">
  <div id="rTitle">“XXX”分析报告</div>
</div> -->

<div id="mainSegment" style="padding:10px 10px 0 10px;">
<div id="reportFrame">
<div class="rptSegment" id="ajMainWeb" style="display:none;background-color:#F7F7F7;font-size:24px;height:40px;font-weight:bold;line-height:40px;padding-left:10px;font-family:黑体,Helvetica Neue,Helvetica,Tahoma,Arial,sans-serif">
  <div id="seg1Title" class="segTitle" style="border:none;backgroud-color:red;"><span>实时数据：</span></div>
</div>
<div class="rptSegment">
  <div id="seg1Title" class="segTitle"><span>实时数据：</span></div>
  <div id="seg2frag4" class="segConteent" style="padding-top:10px;">
    <div style="display:inline; padding:10px;height:40px;">刷新间隔<select id="_interval" onchange="intervalChange()"><option value=1 selected>1分钟</option><option value=3>3分钟</option><option value=5>5分钟</option></select>分钟；当日总访问量：<span id="curDate"></span>；历史总访问量：<span id='allCount'></span></div>
    <div id="monitor" style="width:600px;height:200px;border:1px solid #E6E6E6; margin:5px;"></div>
  </div>
</div>
<div class="rptSegment">
  <div id="seg2Title" class="segTitle"><span>历史访问：</span></div>
  <div id="seg2frag1" class="segConteent">
    <div class="subTitle">每日访问量<span style='font-weight:normal'>(前7日)<span></div>
    <div id="seg1frag1" class="segContent">
      <div id="hisPerDate" style="width:600px;height:200px;border:1px solid #E6E6E6; margin:5px;"></div>
    </div>
  </div>
  <div id="seg2frag2" class="segConteent">
    <div class="subTitle">每小时平均访问量</div>
    <div id="seg1frag1" class="segContent">
      <div id="hisAvgHours" style="width:600px;height:200px;border:1px solid #E6E6E6; margin:5px;"></div>
    </div>
  </div>
  <div id="seg2frag3" class="segConteent">
    <div class="subTitle">访问来源情况</div>
    <div id="seg1frag1" class="segContent">
      <table><tr>
        <td>
          <div id="visitFrom" style="width:400px;height:200px;border:1px solid #E6E6E6; margin:5px;"></div>
        </td>
        <td><div style="height:200px;">前十位来源如左图：其中：<span id="descript"></span></div></td>
      </tr></table>
    </div>
  </div>
</div>
</div>
</body>
<script>
var mPage=getMainPage();
var plot = null;
var processId = -1;
var realD = [];
var _realD = [];
var beginTime="";
var perDate=[], _perDate=[];

//主窗口参数
var INIT_PARAM = {
  pageObjs: {
//    topId: "topSegment",
//    footId: "footSegment",
    mainId: "mainSegment"
  },
  page_width: -1,
  page_height: -1,
  top_height: 50,
  top_peg: false,
  myResize: null,
  myInit: initPos
};
//主函数
$(function() {
  //设置主界面
  var initStr = $.spiritPageFrame(INIT_PARAM);
  if (initStr) {
    $.messager.alert("页面初始化失败", initStr, "error");
    return ;
  };
  var winId = getUrlParam(window.location.href, "_winID");
  if (winId) {
    var win=getSWinInMain(winId);
    if (win) win.modify({title:"国家安全生产监督总局网站首页访问分析"});
  } else {
    $("#ajMainWeb").html("国家安全生产监督总局网站首页访问分析").show();
  }

  //1-实时部分
  //总数
  $.ajax({type:"get",url:"<%=path%>/getRealCount.do" ,async:true, dataType:"json",
    success: function(json) {
      $("#allCount").html(json.all);
      $("#curDate").html(json.curDate);
    },
    error: function(XMLHttpRequest, textStatus, errorThrown) {
      _msg="status="+XMLHttpRequest.status;
      if (mPage) mPage.$.messager.alert("提示", _msg, "error");
      else alert(_msg);
    }
  });
  $.ajax({type:"get",url:"<%=path%>/initMonitor.do" ,async:true, dataType:"json",
    success: function(json) {
      var c=0;
      var TrealD=[], T_realD=[];
      for (p in json) {
        TrealD[c]=json[p];
        T_realD[c++]=p;
      }
      c=0;
      for (var i=TrealD.length-1; i>=0; i--) {
        realD.push([c, TrealD[i]]);
        _realD[c++]=T_realD[i];
        beginTime=T_realD[i];
      }
      plot=$.plot("#monitor", [realD] , {
        series: { 
          shadowSize: 0,
          lines: { show: true },
          points: { show: true }
        },
        grid: { hoverable: true },
        xaxis: {
          show:false
        }
      });
    },
    error: function(XMLHttpRequest, textStatus, errorThrown) {
      _msg="status="+XMLHttpRequest.status;
      if (mPage) mPage.$.messager.alert("提示", _msg, "error");
      else alert(_msg);
    }
  });
  var previousPoint = null;
  $("#monitor").bind("plothover", function (event, pos, item) {
    if (item) {
      if (previousPoint != item.dataIndex) {
        previousPoint = item.dataIndex;
        $("#tooltip").remove();
        var x = item.datapoint[0];
        var y = item.datapoint[1];
        showTooltip(item.pageX, item.pageY,
          "从"+_realD[x].substr(8, 2)+":"+_realD[x].substr(10, 2)+"起"+parseInt($("#_interval").val())+"分钟内"+
          "访问数为："+y
        );
      }
    } else {
      $("#tooltip").remove();
      previousPoint = null;            
    }
  });
  //获取小时分布
  $.ajax({type:"get",url:"<%=path%>/historyHourVisit.do" ,async:true, dataType:"json",
    success: function(json) {
      var d1 = [];
      d1.push(['0', json['00']]);
      d1.push(['1', json['01']]);
      d1.push(['2', json['02']]);
      d1.push(['3', json['03']]);
      d1.push(['4', json['04']]);
      d1.push(['5', json['05']]);
      d1.push(['6', json['06']]);
      d1.push(['7', json['07']]);
      d1.push(['8', json['08']]);
      d1.push(['9', json['09']]);
      d1.push(['10', json['10']]);
      d1.push(['11', json['11']]);
      d1.push(['12', json['12']]);
      d1.push(['13', json['13']]);
      d1.push(['14', json['14']]);
      d1.push(['15', json['15']]);
      d1.push(['16', json['16']]);
      d1.push(['17', json['17']]);
      d1.push(['18', json['18']]);
      d1.push(['19', json['19']]);
      d1.push(['20', json['20']]);
      d1.push(['21', json['21']]);
      d1.push(['22', json['22']]);
      d1.push(['23', json['23']]);

      $.plot($("#hisAvgHours"), [d1], {
        series: {
          bars: {show: true, barWidth: 0.5, align: "center", fill:0.6}
        },
        grid: {
          hoverable: true,
          borderWidth: {top: 0, right: 0, bottom: 1, left: 1}
        },
        xaxis: {
          mode: "categories",
          autoscaleMargin: 0.03,
          tickLength: 0
        },
        yaxis:{
          show:true,
          position:'left',
          tickLength:40,
          tickDecimals:0
        }
      });
    },
    error: function(XMLHttpRequest, textStatus, errorThrown) {
      _msg="status="+XMLHttpRequest.status;
      if (mPage) mPage.$.messager.alert("提示", _msg, "error");
      else alert(_msg);
    }
  });
  $("#hisAvgHours").bind("plothover", function (event, pos, item) {
    if (item) {
      previousPoint = item.dataIndex;
      $("#tooltip").remove();
      var x = parseInt(item.datapoint[0]);
      var y = parseInt(item.datapoint[1]);
      showTooltip(pos.pageX+20, pos.pageY-10,
        "<li><label>时间：</label><span>"+x+":00至"+(x+1)+":00</span></li>" + 
        "<li><label>平均访问数：</label><span>"+y+"</span></li></ul>"
      );
    } else {
      $("#tooltip").remove();
    }
  });

  //每日访问量趋势
  $.ajax({type:"get",url:"<%=path%>/perDate.do" ,async:true, dataType:"json",
    success: function(json) {
      var c=0;
      for (p in json) {
      	c++;
      }
      for (p in json) {
        perDate[c]=[c, json[p]];
        _perDate[c]=p;
        c--;
      }
      $.plot("#hisPerDate", [perDate] , {
        series: { 
          shadowSize: 0,
          lines: { show: true },
          points: { show: true }
        },
        grid: { hoverable: true },
        xaxis: {
          minTickSize:1,
          min:0,
          max:8,
          tickFormatter:function(val, axis) {
          	if (val==0||val==8) return "";
          	return _perDate[val]?_perDate[val]:"";
          }
        }
      });
    },
    error: function(XMLHttpRequest, textStatus, errorThrown) {
      _msg="status="+XMLHttpRequest.status;
      if (mPage) mPage.$.messager.alert("提示", _msg, "error");
      else alert(_msg);
    }
  });
  $("#hisPerDate").bind("plothover", function (event, pos, item) {
    if (item) {
      previousPoint = item.dataIndex;
      $("#tooltip").remove();
      var x = parseInt(item.datapoint[0]);
      var y = parseInt(item.datapoint[1]);
      showTooltip(pos.pageX+20, pos.pageY-10,
        "<li><label>时间：</label><span>"+_perDate[x]+"</span></li>" + 
        "<li><label>访问数：</label><span>"+y+"</span></li></ul>"
      );
    } else {
      $("#tooltip").remove();
    }
  });

  //访问来源分析
  $.ajax({type:"get",url:"<%=path%>/visitFrom.do" ,async:true, dataType:"json",
    success: function(json) {
      var d1 = [];
      var _descript = "";
      var i=0;
      var allSum=0;
      for (p in json) {
        d1.push({"label":p, "data":json[p]});
        allSum+=parseInt(json[p]);
      }
      for (p in json) {
        if (i<3) {
          if (i==0) _descript+="<br/>\""+p+"\"最多，占比为"+Math.round((json[p]*100)/allSum)+"%";
          else if (i==1) _descript+="<br/>其次为\""+p+"\"，占比为"+Math.round((json[p]*100)/allSum)+"%";
          else if (i==2) _descript+="<br/>第三位是\""+p+"\"，占比为"+Math.round((json[p]*100)/allSum)+"%";
        } else break;
        i++;
      }
      $("#descript").html(_descript);
      $.plot($("#visitFrom"), d1, {
        series: {
          pie: {
            show: true,
            radius: 1,
            label: {
              show: true,
              radius: 2/3,
              formatter: function(label, series){
                return '<div style="font-size:8pt;text-align:center;color:white;">'+label+'<br/>'+Math.round(series.percent)+'%</div>';
              },
              threshold:0.1
            }
          }
        },
        grid: {hoverable: true}
      });
    },
    error: function(XMLHttpRequest, textStatus, errorThrown) {
      _msg="status="+XMLHttpRequest.status;
      if (mPage) mPage.$.messager.alert("提示", _msg, "error");
      else alert(_msg);
    }
  });
  $("#visitFrom").bind("plothover", function (event, pos, item) {
    if (item) {
      previousPoint = item.dataIndex;
      $("#tooltip").remove();
      var _num = item.series.data+"";
      _num=_num.substr(_num.indexOf(",")+1);
      showTooltip(pos.pageX+20, pos.pageY-10,
        "<li><label>分类：</label><span>"+item.series.label+"</span></li>" + 
        "<li><label>访问数：</label><span>"+_num+"</span></li></ul>" +
        "<li><label>占比：</label><span>"+Math.round(item.series.percent)+"%</span></li></ul>"
      );
    } else {
      $("#tooltip").remove();
    }
  });
  intervalChange();
});

function initPos() {
  $("#reportFrame").spiritUtils("setWidthByViewWidth", $("body").width()-20);
}
function showTooltip(x, y, contents) {
  $('<div id="tooltip">' + contents + '</div>').css( {
    position: 'absolute',
    display: 'none',
    top: y+5,
    left: x+5,
    border: '1px solid #fdd',
    padding: '2px',
    'background-color': '#fee',
    opacity: 0.80
  }).appendTo("body").fadeIn(200);
}

function intervalChange() {
  var _intervalMs=parseInt($("#_interval").val());
  //总数
  $.ajax({type:"get",url:"<%=path%>/getRealCount.do" ,async:true, dataType:"json",
    success: function(json) {
      $("#allCount").html(json.all);
      $("#curDate").html(json.curDate);
    },
    error: function(XMLHttpRequest, textStatus, errorThrown) {
      _msg="status="+XMLHttpRequest.status;
      if (mPage) mPage.$.messager.alert("提示", _msg, "error");
      else alert(_msg);
    }
  });
  if (!beginTime) {
    setTimeout(intervalChange, _intervalMs*60*1000);//1分钟
    return;
  }
  $.ajax({type:"get",url:"<%=path%>/initMonitor.do" ,async:true, dataType:"json",
    success: function(json) {
      _realD=[]; realD=[];
      var c=0;
      var TrealD=[], T_realD=[];
      for (p in json) {
        TrealD[c]=json[p];
        T_realD[c++]=p;
      }
      c=0;
      for (var i=TrealD.length-1; i>=0; i--) {
        realD.push([c, TrealD[i]]);
        _realD[c++]=T_realD[i];
        beginTime=T_realD[i];
      }
      plot.setData([realD]);
      plot.draw();
      setTimeout(intervalChange, _intervalMs*60*1000);//1分钟
    },
    error: function(XMLHttpRequest, textStatus, errorThrown) {
      _msg="status="+XMLHttpRequest.status;
      if (mPage) mPage.$.messager.alert("提示", _msg, "error");
      else alert(_msg);
    }
  });
  /*
  $.ajax({type:"get",url:"<%=path%>/getRealVisit.do?minites=1&beginTime="+beginTime ,async:true, dataType:"json",
    success: function(json) {
      var val = null;
      var canDraw =true;
      if (!json||json=="") canDraw=false;
      for (p in json) {
        beginTime=p;
        val=json[p];
      }
      if (!val) canDraw=false;
      if (canDraw) {
      	var TrealD=[], T_realD=[];
      	var len=realD.length;
      	for (var i=1; i<realD.length; i++) {
          TrealD[i-1]=realD[i];
          TrealD[i-1][0]=TrealD[i-1][0]-1;
          T_realD[i-1]=_realD[i];
      	}
        TrealD[len-1]=[len-1, val];
        T_realD[len-1]=beginTime;
      	realD=TrealD;
      	_realD=T_realD;
        plot.setData([realD]);
        plot.draw();
      }
      setTimeout(intervalChange, _intervalMs*1000);//1分钟
    },
    error: function(XMLHttpRequest, textStatus, errorThrown) {
      _msg="status="+XMLHttpRequest.status;
      if (mPage) mPage.$.messager.alert("提示", _msg, "error");
      else alert(_msg);
    }
  });
   */
}
</script>
</html>