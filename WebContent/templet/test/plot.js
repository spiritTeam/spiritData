function entrance(id,showMethod,json){
  if(showMethod=='line'){
    line(id,json);
  }else if(showMethod=='pie'){
    pie(id,json);
  }else if(showMethod=='bars'){
    bars(id,json);
  }else if(showMethod=='all'){
    line(id,json);
    pie(id,json);
    bars(id,json);
  }
}
function line(id,json){
  var ary = [];
  for(var i=0;i<json.length;i++){
    ary[i] = [json[i].sex,json[i].num];
  }
  $.plot("#"+id, [{label:"最小值", data:ary}],{
    series: {
      lines: { show: true },
      points: { show: true }
    },
    xaxis: {
      mode: "categories",
      autoscaleMargin: 0.05,
      tickLength: 0
    },
    yaxis:{
      show:true,
      position:'left',
      tickLength:40,
      tickDecimals:0
    },
    legend:{show:false}
  });
}
function pie(id,json){
  var ary = [];
  for(var i=0;i<json.length;i++){
    ary[i] = {label:json[i].sex,data:json[i].num};
  }
  $.plot($("#"+id), ary, {
    series:{
      pie:{
        show:true,
        radius:1,
        label:{
          show:true,
          radius:2/3,
          formatter:function(label, series){
            return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'+label+'<br/>'+Math.round(series.percent)+'%</div>';
          },
          threshold:0.1
        }
      }
    },
    legend:{
      show:false
    }
  });
}
function bars(id,json){
  var ary = [];
  for(var i=0;i<json.length;i++){
    ary[i] = [json[i].sex,json[i].num];
  }
  $.plot("#"+id, [ary], {
    series: {
      bars: {
        show: true,
        barWidth: 0.3,
        align: "center",
        fill:0.3
      }
    },
    xaxis: {
      mode: "categories",
      autoscaleMargin: 0.05,
      tickLength: 0
    },
    yaxis:{
      show:true,
      position:'left',
      tickLength:40,
      tickDecimals:0
    },
    legend:{ show:true, position: "sw" }
  });
}