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
<link rel="stylesheet" type="text/css" href="<%=path%>/resources/css/mySpiritUi/pageFrame.css"/>
<script type="text/javascript" src="<%=path%>/resources/plugins/spiritui/jq.spirit.utils.js"></script>
<script type="text/javascript" src="<%=path%>/resources/plugins/spiritui/jq.spirit.pageFrame.js"></script>
<script type="text/javascript" src="<%=path%>/resources/js/mainPage.utils.js"></script>

<script type="text/javascript" src="<%=path%>/resources/plugins/flot/excanvas.min.js"></script>
<script type="text/javascript" src="<%=path%>/resources/plugins/flot/jquery.flot.js"></script>
<script type="text/javascript" src="<%=path%>/resources/plugins/flot/jquery.flot.pie.js"></script>
<script type="text/javascript" src="<%=path%>/resources/plugins/flot/jquery.flot.categories.js"></script>
<script type="text/javascript" src="<%=path%>/resources/plugins/Chart.min.js"></script>
<script type="text/javascript" src="<%=path %>/resources/js/brief.jqplotToImg.js"></script>
<!-- 测试 -->
<title>分析报告</title>
</head>
<style>
body {
  background-color:#fff;
}
#sideFrame {
  width:270px; padding-left:20px; position:fixed;
}
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
<!-- 头部:悬浮 -->
<div id="topSegment">
  <div id="rTitle">“XXX”分析报告</div>
</div>

<!-- 脚部:悬浮 -->
<div id="mainSegment" style="padding:10px 10px 0 10px;">

<div id="sideFrame">
  <div id="catalogTree" style="border:1px solid #E6E6E6; width:258px; "></div>
</div>
<div id="reportFrame">
<div class="rptSegment">
  <div id="seg1Title" class="segTitle"><span>上传数据</span></div>
  <div id="seg1frag1" class="segContent">
    <div class="subTitle">1、结构分析</div>
    <ul>
      <li>
        <div><span style="font-weight:bold;">“人员”页签(Sheet1)</span>为新增结构，元数据结构分析结果如下：</div>
        <div style="padding:3px 0 3px 5px;">
        <table class="easyui-datagrid" style="width:500px;height:230px"
           data-options="singleSelect:true,collapsible:true,url:'segdemoA1_1_a.json',method:'get'">
          <thead>
            <tr>
              <th data-options="field:'mdName',width:100,halign:'center',align:'left'">元数据名</th>
              <th data-options="field:'mdType',width:80,halign:'center',align:'center'">元数据类型</th>
              <th data-options="field:'mdDetail',width:40,halign:'center',align:'center',formatter: detail1">详细</th>
              <th data-options="field:'mdMemo',width:270,halign:'center',align:'left'">语义解释</th>
            </tr>
          </thead>
        </table>
        </div>
      </li>
      <li>
        <div><span style="font-weight:bold;">“案件”页签(Sheet2)</span>为原有结构，元数据用原有结构。</div>
      </li>
    </ul>
  </div>
  <div id="seg1frag2" class="segConteent">
    <div class="subTitle">2、单项指标分析</div>
    <ul>
      <li>
        <div><span style="font-weight:bold;">“人员”[性别]</span>指标分析：</div>
        <div style="padding:3px 0 3px 5px;">“人员”中大多为{男}占78%，{女}占20%，具体分析数据如下：</div>
        <div style="padding:3px 0 3px 5px;"><table><tr>
        <td>
          <div>
        <table class="easyui-datagrid" style="width:300px;height:160px"
           data-options="singleSelect:true,collapsible:true,url:'segdemoA1_2_a.json',method:'get'">
          <thead>
            <tr>
              <th data-options="field:'fl',width:100,halign:'center',align:'center'">分类</th>
              <th data-options="field:'num',width:80,halign:'center',align:'right'">数量</th>
              <th data-options="field:'ver',width:40,halign:'center',align:'right'">比例</th>
            </tr>
          </thead>
        </table>
          </div>
        </td>
        <td><div style="width:240px;height:148px;border:1px solid #95B8E7;padding:5px;margin-left:5px;"><div class="jqplot" id="chartA1_2_a" style="width:220px;height:145px;"></div></div></td>
        </tr></table></div>
      </li>
      <li>
        <div><span style="font-weight:bold;">“人员”[籍贯]</span>指标分析：</div>
        <div style="padding:3px 0 3px 5px;">“人员”中[籍贯]前三位为{北京}占21%、{河北}占18%、{河南}占14%，具体分析如下：</div>
        <div style="padding:3px 0 3px 5px;"><table><tr>
        <td colspan=2>
          <div>
        <table class="easyui-datagrid" style="width:600px;height:130px"
           data-options="singleSelect:true,collapsible:true,url:'segdemoA1_2_b.json',method:'get'">
          <thead>
            <tr>
              <th data-options="field:'fl',width:100,halign:'center',align:'center'">分类</th>
              <th data-options="field:'num',width:50,halign:'center',align:'right'">数量</th>
              <th data-options="field:'ver',width:45,halign:'center',align:'right'">比例</th>
              <th data-options="field:'fl1',width:100,halign:'center',align:'center'">分类</th>
              <th data-options="field:'num1',width:50,halign:'center',align:'right'">数量</th>
              <th data-options="field:'ver1',width:45,halign:'center',align:'right'">比例</th>
              <th data-options="field:'fl2',width:100,halign:'center',align:'center'">分类</th>
              <th data-options="field:'num2',width:50,halign:'center',align:'right'">数量</th>
              <th data-options="field:'ver2',width:45,halign:'center',align:'right'">比例</th>
            </tr>
          </thead>
        </table>
          </div>
        </td>
        </tr><tr>
        <td><div style="width:180px;height:180px;border:1px solid #95B8E7;padding:5px;margin-top:5px;"><div class="jqplot" id="chartA1_2_b" style="width:170px;height:170px;"></div></div></td>
        <td><div style="width:392px;height:180px;border:1px solid #95B8E7;padding:5px;margin-top:5px;margin-left:5px;"><div  class="jqplot" id="chartA1_2_c" style="width:400px;height:170px;"></div></div></td>
        </tr></table></div>
      </li>
      <li>
        <div><span style="font-weight:bold;">“案件”[接报派出所]</span>指标分析：</div>
        <div style="padding:3px 0 3px 5px;">“案件”中[接报派出所]前三位为{A}占21%、{B}占18%、{C}占14%，具体分析如下：</div>
        <div style="padding:3px 0 3px 5px;"><table><tr>
        <td colspan=2>
          <div>
        <table class="easyui-datagrid" style="width:600px;height:110px"
           data-options="singleSelect:true,collapsible:true,url:'segdemoA1_2_c.json',method:'get'">
          <thead>
            <tr>
              <th data-options="field:'fl',width:100,halign:'center',align:'center'">分类</th>
              <th data-options="field:'num',width:50,halign:'center',align:'right'">数量</th>
              <th data-options="field:'ver',width:45,halign:'center',align:'right'">比例</th>
              <th data-options="field:'fl1',width:100,halign:'center',align:'center'">分类</th>
              <th data-options="field:'num1',width:50,halign:'center',align:'right'">数量</th>
              <th data-options="field:'ver1',width:45,halign:'center',align:'right'">比例</th>
              <th data-options="field:'fl2',width:100,halign:'center',align:'center'">分类</th>
              <th data-options="field:'num2',width:50,halign:'center',align:'right'">数量</th>
              <th data-options="field:'ver2',width:45,halign:'center',align:'right'">比例</th>
            </tr>
          </thead>
        </table>
          </div>
        </td>
        </tr><tr>
        <td><div style="width:180px;height:180px;border:1px solid #95B8E7;padding:5px;margin-top:5px;"><div class="jqplot" id="chartA1_2_d" style="width:170px;height:170px;"></div></div></td>
        <td><div style="width:392px;height:180px;border:1px solid #95B8E7;padding:5px;margin-top:5px;margin-left:5px;"><div class="jqplot" id="chartA1_2_e" style="width:400px;height:170px;"></div></div></td>
        </tr></table></div>
      </li>
      <li>
        <div><span style="font-weight:bold;">“案件”[案件类别]</span>指标分析：</div>
        <div style="padding:3px 0 3px 5px;">“案件”中[案件类别]前三位为{入室盗窃}占18%、{诈骗}占15%、{抢夺}占11%，具体分析如下：</div>
        <div style="padding:3px 0 3px 5px;"><table><tr>
        <td colspan=2>
          <div>
        <table class="easyui-datagrid" style="width:600px;height:110px"
           data-options="singleSelect:true,collapsible:true,url:'segdemoA1_2_d.json',method:'get'">
          <thead>
            <tr>
              <th data-options="field:'fl',width:100,halign:'center',align:'center'">分类</th>
              <th data-options="field:'num',width:50,halign:'center',align:'right'">数量</th>
              <th data-options="field:'ver',width:45,halign:'center',align:'right'">比例</th>
              <th data-options="field:'fl1',width:100,halign:'center',align:'center'">分类</th>
              <th data-options="field:'num1',width:50,halign:'center',align:'right'">数量</th>
              <th data-options="field:'ver1',width:45,halign:'center',align:'right'">比例</th>
              <th data-options="field:'fl2',width:100,halign:'center',align:'center'">分类</th>
              <th data-options="field:'num2',width:50,halign:'center',align:'right'">数量</th>
              <th data-options="field:'ver2',width:45,halign:'center',align:'right'">比例</th>
            </tr>
          </thead>
        </table>
          </div>
        </td>
        </tr><tr>
        <td><div style="width:180px;height:180px;border:1px solid #95B8E7;padding:5px;margin-top:5px;"><div class="jqplot" id="chartA1_2_f" style="width:170px;height:170px;"></div></div></td>
        <td><div style="width:392px;height:180px;border:1px solid #95B8E7;padding:5px;margin-top:5px;margin-left:5px;"><div class="jqplot" id="chartA1_2_g" style="width:400px;height:170px;"></div></div></td>
        </tr></table></div>
      </li>
    </ul>
  </div>
  <div id="seg1frag3" class="segConteent">
    <div class="subTitle">3、类别指标分析</div>
    <ul>
      <li>
        <div><span style="font-weight:bold;">“人员”[性别][籍贯]</span>分布情况如下：</div>
        <div style="padding:3px 0 3px 5px;"><table><tr>
        <td>
          <div>
        <table class="easyui-datagrid" style="width:600px;height:105px"
           data-options="singleSelect:true,collapsible:true,url:'segdemoA1_3_a.json',method:'get'">
          <thead>
            <tr>
              <th data-options="field:'fl',width:80,halign:'center',align:'center'"></th>
              <th data-options="field:'bj',width:40,halign:'center',align:'right'">北京</th>
              <th data-options="field:'hb',width:40,halign:'center',align:'right'">河北</th>
              <th data-options="field:'hn',width:40,halign:'center',align:'right'">河南</th>
              <th data-options="field:'tj',width:40,halign:'center',align:'right'">天津</th>
              <th data-options="field:'sh',width:40,halign:'center',align:'right'">上海</th>
              <th data-options="field:'ln',width:40,halign:'center',align:'right'">辽宁</th>
              <th data-options="field:'zj',width:40,halign:'center',align:'right'">浙江</th>
              <th data-options="field:'sc',width:40,halign:'center',align:'right'">四川</th>
              <th data-options="field:'js',width:40,halign:'center',align:'right'">江苏</th>
              <th data-options="field:'hlj',width:40,halign:'center',align:'right'">黑龙江</th>
              <th data-options="field:'qt',width:40,halign:'center',align:'right'">其他</th>
            </tr>
          </thead>
        </table>
          </div>
        </td>
        </tr></table></div>
      </li>
      <li>
        <div><span style="font-weight:bold;">“案件”[接报派出所][案件类别]</span>分布情况如下：</div>
        <div style="padding:3px 0 3px 5px;"><table><tr>
        <td>
          <div>
        <table class="easyui-datagrid" style="width:600px;height:230px"
           data-options="singleSelect:true,collapsible:true,url:'segdemoA1_3_b.json',method:'get'">
          <thead>
            <tr>
              <th data-options="field:'fl',width:80,halign:'center',align:'center'"></th>
              <th data-options="field:'A',width:60,halign:'center',align:'right'">入室盗窃</th>
              <th data-options="field:'B',width:50,halign:'center',align:'right'">诈骗</th>
              <th data-options="field:'C',width:50,halign:'center',align:'right'">抢夺</th>
              <th data-options="field:'D',width:50,halign:'center',align:'right'">抢劫</th>
              <th data-options="field:'E',width:50,halign:'center',align:'right'">扒窃</th>
              <th data-options="field:'F',width:50,halign:'center',align:'right'">拎包</th>
              <th data-options="field:'G',width:60,halign:'center',align:'right'">故意伤害</th>
              <th data-options="field:'H',width:60,halign:'center',align:'right'">盗窃机动车</th>
              <th data-options="field:'qt',width:50,halign:'center',align:'right'">其他</th>
            </tr>
          </thead>
        </table>
          </div>
        </td>
        </tr></table></div>
      </li>
    </ul>
  </div>
  <div id="seg1frag4" class="segConteent">
    <div class="subTitle">4、数值指标分析</div>
    <ul>
      <li>
        <div><span style="font-weight:bold;">“案件”[报案金额]</span>情况如下：总数为13200，平均数为600，最大值为10000，最小值为125。共有100条数据，有[报案金额]为22条，占22%。</div>
      </li>
      <li>
        <div><span style="font-weight:bold;">“案件”[报案金额]</span>[接报派出所]分布情况如下：</div>
        <div style="padding:3px 0 3px 5px;">“案件”[报案金额]按[接报派出所]分前三位为{A}占18%、{B}占15%、{C}占11%，具体分析如下：</div>
        <div style="padding:3px 0 3px 5px;"><table><tr>
        <td colspan=2>
          <div>
        <table class="easyui-datagrid" style="width:600px;height:210px"
           data-options="singleSelect:true,collapsible:true,url:'segdemoA1_4_a.json',method:'get'">
          <thead>
            <tr>
              <th data-options="field:'fl',width:120,halign:'center',align:'center'">接报派出所</th>
              <th data-options="field:'g',width:60,halign:'center',align:'right'">个数</th>
              <th data-options="field:'gz',width:60,halign:'center',align:'right'">个数占比</th>
              <th data-options="field:'z',width:60,halign:'center',align:'right'">总值</th>
              <th data-options="field:'zz',width:60,halign:'center',align:'right'">总值占比</th>
              <th data-options="field:'v',width:60,halign:'center',align:'right'">平均值</th>
              <th data-options="field:'a',width:60,halign:'center',align:'right'">最大值</th>
              <th data-options="field:'i',width:60,halign:'center',align:'right'">最小值</th>
            </tr>
          </thead>
        </table>
          </div>
        </td>
        </tr><tr>
        <td><div style="width:280px;height:180px;border:1px solid #95B8E7;padding:5px;margin-top:5px;"><div style="position:absolute;">个数</div><div class="jqplot" id="chartA1_4_a" style="width:270px;height:170px;"></div></div></td>
        <td><div style="width:290px;height:180px;border:1px solid #95B8E7;padding:5px;margin-top:5px;margin-left:5px;"><div style="position:absolute;">值</div><div class="jqplot" id="chartA1_4_b" style="width:280px;height:170px;"></div></div></td>
        </tr><tr>
        <td colspan=2>
        <div style="width:588px;height:180px;border:1px solid #95B8E7;padding:5px;margin-top:5px;"><div class="jqplot" id="chartA1_4_c" style="width:590px;height:180px;"></div></div>
        </td>
        </tr></table></div>
      </li>
      <li>
        <div><span style="font-weight:bold;">“案件”[报案金额]</span>[案件类别]分布情况如下：</div>
      </li>
    </ul>
  </div>
  <div id="seg1frag5" class="segConteent">
    <div class="subTitle">5、时间指标分析</div>
    <ul>
      <li>
        <div><span style="font-weight:bold;">“案件”[发案时间]</span>情况如下：数据跨度从(2013-09-28)到(2013-10-30)，有[发案时间]为97条，占98%。</div>
      </li>
      <li>
        <div><span style="font-weight:bold;">“案件”按[发案时间]</span>分析，日分布情况如下:</div>
        <div style="width:588px;height:180px;border:1px solid #95B8E7;padding:5px;margin:5px 0 0 5px;"><div class="jqplot" id="chartA1_4_d" style="width:590px;height:180px;"></div></div>
      </li>
      <li>
        <div><span style="font-weight:bold;">“案件”按[发案时间]</span>分析，周分布情况如下:</div>
        <div style="width:588px;height:180px;border:1px solid #95B8E7;padding:5px;margin:5px 0 0 5px;"><div class="jqplot" id="chartA1_4_e" style="width:590px;height:180px;"></div></div>
      </li>
      <li>
        <div><span style="font-weight:bold;">“案件”按[发案时间]、[报案金额]</span>分析，周分布情况如下:</div>
        <div style="width:588px;height:180px;border:1px solid #95B8E7;padding:5px;margin:5px 0 0 5px;"><div class="jqplot" id="chartA1_4_f" style="width:590px;height:180px;"></div></div>
      </li>
      <li>
        <div><span style="font-weight:bold;">“案件”按[发案时间]、[案件类别]</span>分析，周分布情况如下:</div>
        <div style="width:588px;height:180px;border:1px solid #95B8E7;padding:5px;margin:5px 0 0 5px;"><div class="jqplot" id="chartA1_4_g" style="width:590px;height:180px;"></div></div>
      </li>
    </ul>
  </div>
</div>
<div class="rptSegment">
  <div id="seg2Title" class="segTitle"><span>积累数据</span></div>
  <div id="seg2frag1" class="segConteent">
    <div class="subTitle">1、单项指标分析</div>
    <ul>
    </ul>
  </div>
  <div id="seg2frag2" class="segConteent">
    <div class="subTitle">2、类别指标分析</div>
    <ul>
    </ul>
  </div>
  <div id="seg2frag3" class="segConteent">
    <div class="subTitle">3、数值指标分析</div>
    <ul>
    </ul>
  </div>
  <div id="seg2frag4" class="segConteent">
    <div class="subTitle">4、时间指标分析</div>
    <ul>
      <li>
        <div><span style="font-weight:bold;">“案件”按[发案时间]</span>分析，当月分布情况如下:</div>
        <div style="width:588px;height:180px;border:1px solid #95B8E7;padding:5px;margin:5px 0 0 5px;"><div class="jqplot" id="chartA2_4_a" style="width:590px;height:180px;"></div></div>
      </li>
      <li>
        <div><span style="font-weight:bold;">“案件”按[发案时间]发案数量</span>分析，月同环比情况如下:</div>
        <div style="width:588px;height:180px;border:1px solid #95B8E7;padding:5px;margin:5px 0 0 5px;"><div  id="chartA2_4_c" style="width:590px;height:180px;"></div></div>
      </li>
      <li>
        <div><span style="font-weight:bold;">“案件”按[发案时间][报案金额]</span>分析，月同环比情况如下:</div>
        <div style="width:588px;height:180px;border:1px solid #95B8E7;padding:5px;margin:5px 0 0 5px;"><div class="jqplot" id="chartA2_4_b" style="width:590px;height:180px;"></div></div>
      </li>
    </ul>
  </div>
</div>
<div class="rptSegment">
  <div id="seg4Title" class="segTitle"><span>高级</span></div>
  <div id="seg4frag1" class="segContent">
    <div class="subTitle">数据质量及结构</div>
    <ul>
      <li>
        <div>“人员”元数据结构如下：</div>
        <div style="padding:3px 0 3px 5px;">
        <table class="easyui-datagrid" style="width:650px;height:180px"
           data-options="singleSelect:true,collapsible:true,url:'segdemoA4_1_a.json',method:'get'">
          <thead>
            <tr>
              <th data-options="field:'mdName',width:100,halign:'center',align:'left'">元数据名</th>
              <th data-options="field:'mdType',width:80,halign:'center',align:'center'">元数据类型</th>
              <th data-options="field:'mdDetail',width:40,halign:'center',align:'center',formatter: detail1">详细</th>
              <th data-options="field:'mdMemo',width:140,halign:'center',align:'left'">语义解释</th>
              <th data-options="field:'mdRange',width:180,halign:'center',align:'left'">范围</th>
              <th data-options="field:'mdNullV',width:80,halign:'center',align:'right'">稀疏率</th>
            </tr>
          </thead>
        </table>
        </div>
      </li>
      <li>
        <div>“人员”元数据质量如下：</div>
        <div style="padding:3px 0 3px 5px;"><table><tr>
        <td><div style="width:432px;height:180px;border:1px solid #95B8E7;padding:5px;">
          <p>数据稀疏率：11%，达到良好水平。</p>
          <p>数据语义质量：90%，达到良好水平。其中{出生日期}判断为生日，其中有12%数据不准确。</p>
          <p>数据更新率：20%，不经常更新，最近一次更新32天前。</p>
          <p>数据使用率：70%，经常参与分析，最近一次分析0天前。</p>
          <p>数据关联性：0%，未发现与其关联的其他数据。</p>
          <p></p>
        </div></td>
        <td><div style="width:210px;height:180px;border:1px solid #95B8E7;padding:5px;margin-left:5px;">
          <canvas id="chartA4_1_a" height="170" width="170"></canvas>
        </div></td>
        </tr></table></div>
      </li>
      <li>
        <div>“案件”元数据结构如下：</div>
        <div style="padding:3px 0 3px 5px;">
        <table class="easyui-datagrid" style="width:650px;height:280px"
           data-options="singleSelect:true,collapsible:true,url:'segdemoA4_2_a.json',method:'get'">
          <thead>
            <tr>
              <th data-options="field:'mdName',width:100,halign:'center',align:'left'">元数据名</th>
              <th data-options="field:'mdType',width:80,halign:'center',align:'center'">元数据类型</th>
              <th data-options="field:'mdDetail',width:40,halign:'center',align:'center',formatter: detail1">详细</th>
              <th data-options="field:'mdMemo',width:140,halign:'center',align:'left'">语义解释</th>
              <th data-options="field:'mdRange',width:180,halign:'center',align:'left'">范围</th>
              <th data-options="field:'mdNullV',width:80,halign:'center',align:'right'">稀疏率</th>
            </tr>
          </thead>
        </table>
        <form id="imgInfoForm" hidden="true" action="<%=path %>/getImage.do" method="post" onsubmit="initForm();">
		      <input type="hidden" id="u1" value="" name="chartA1_2_a">
		      <input type="hidden" id="u1" value="" name="chartA1_2_b">
		      <input type="hidden" id="u1" value="" name="chartA1_2_c">
		      <input type="hidden" id="u1" value="" name="chartA1_2_d">
		      <input type="hidden" id="u1" value="" name="chartA1_2_e">
		      <input type="hidden" id="u1" value="" name="chartA1_2_f">
		      <input type="hidden" id="u1" value="" name="chartA1_2_g">
		      <input type="hidden" id="u1" value="" name="chartA1_4_a">
		      <input type="hidden" id="u1" value="" name="chartA1_4_b">
		      <input type="hidden" id="u1" value="" name="chartA1_4_c">
          <input type="hidden" id="u1" value="" name="chartA1_4_d">
          <input type="hidden" id="u1" value="" name="chartA1_4_e">
          <input type="hidden" id="u1" value="" name="chartA1_4_f">
          <input type="hidden" id="u1" value="" name="chartA1_4_g">
          <input type="hidden" id="u1" value="" name="chartA2_4_a">
          <input type="hidden" id="u1" value="" name="chartA2_4_b">
		      <input type="hidden" id="thisUrl" name="thisUrl" value="">
		    </form>
        </div>
      </li>
    </ul>
  </div>
</div>
</div>

</div>
</body>
<script>
//本页url
var thisUrl = window.location.href;
//获取japlot元素
var jqplotDoms;  
function initForm(){
  //初始化链接
  $('#thisUrl').val(thisUrl);
  //初始化图片Str
  initImg();
}
function initImg(){
  jqplotDoms = $(".jqplot");
  if(jqplotDoms.length>0){
    for (var i=0;i<jqplotDoms.length;i++) {
      var jqpoltDom=$(jqplotDoms[i]);
      var id = jqpoltDom.attr('id');
      var image = jqplotToImg($('#'+id));
      var arr=image.split(',');
      $("input[name='"+id+"']").val(arr[arr.length-1]);
    } 
  }
}
//主窗口参数
var INIT_PARAM = {
  pageObjs: {
    topId: "topSegment",
    mainId: "mainSegment"
  },
  page_width: -1,
  page_height: -1,
  top_shadow_color:"#E6E6E6",
  top_height: 60,
  top_peg: false
};

//主函数
$(function() {
  var initStr = $().spiritPageFrame(INIT_PARAM);
  if (initStr) {
    $.messager.alert("页面初始化失败", initStr, "error");
    return ;
  };
  $("#reportFrame").spiritUtils("setWidthByViewWidth", $("body").width()-$("#sideFrame").spiritUtils("getViewWidth"));
  $("#sideFrame").css("left", $("#reportFrame").width());
  //树
  var treeData=[{
    "id":1, "text":"上传数据",
    "children":[{"id":11, "text":"结构分析"},{"id":12, "text":"单项指标分析"},{"id":13, "text":"类别指标分析"},{"id":14, "text":"数值指标分析"},{"id":15, "text":"时间指标分析"}]
  },{
    "id":2, "text":"积累数据",
    "children":[{"id":21, "text":"单项指标分析"},{"id":22, "text":"类别指标分析"},{"id":23, "text":"数值指标分析"},{"id":24, "text":"时间指标分析"}]
  },{
    "id":3, "text":"高级",
    "children":[{"id":31, "text":"数据质量及结构"},{"id":32, "text":"时间关联分析"},{"id":33, "text":"分布统计"},{"id":34, "text":"关联分析"}]
  }];
  $("#catalogTree").tree({animate:true});
  $("#catalogTree").tree("loadData", treeData);
  //准备数据:人员性别
  var A1_2_a = [{label:'男', data:156},{label:'女', data:40}, {label:'未知', data:4}];
  $.plot($("#chartA1_2_a"), A1_2_a, {
    series: {
      pie: {
        show: true,
        radius: 1,
        label: {
          show: true,
          radius: 2/3,
          formatter: function(label, series){
            return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'+label+'<br/>'+Math.round(series.percent)+'%</div>';
          },
          threshold: 0.1
        }
      }
    },
    legend: {
      show: false
    }
  });
  //准备数据:人员
  var A1_2_b = [{label:'北京', data:42},{label:'河北', data:36},{label:'河南', data:28},{label:'天津', data:27},{label:'上海', data:25},{label:'辽宁', data:12},
                {label:'浙江', data:8},{label:'四川', data:8},{label:'江苏', data:6},{label:'黑龙江', data:6},{label:'其他', data:2}];
  $.plot($("#chartA1_2_b"), A1_2_b, {
    series: {
      pie: {
        show: true,
        radius: 1,
        label: {
          show: true,
          radius: 2/3,
          formatter: function(label, series){
            return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'+label+'<br/>'+Math.round(series.percent)+'%</div>';
          },
          threshold: 0.1
        }
      }
    },
    legend: {
      show: false
    }
  });
  var A1_2_c = [['北京',42],['河北',36],['河南',28],['河北',36],['天津',27],['河北',36],['上海',25],['辽宁',12],['浙江',8],['四川',8],['江苏',6],['黑龙江',6],['其他',2]];
  $.plot("#chartA1_2_c", [A1_2_c], {
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
  //准备数据:案件
  var A1_2_d = [{label:'A', data:42},{label:'B', data:36},{label:'C', data:28},{label:'D', data:27},{label:'E', data:25},{label:'F', data:20},
                {label:'G', data:14},{label:'其他', data:8}];
  $.plot($("#chartA1_2_d"), A1_2_d, {
    series: {
      pie: {
        show: true,
        radius: 1,
        label: {
          show: true,
          radius: 2/3,
          formatter: function(label, series){
            return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'+label+'<br/>'+Math.round(series.percent)+'%</div>';
          },
          threshold: 0.1
        }
      }
    },
    legend: {
      show: false
    }
  });
  var A1_2_e = [['A',42],['B',36],['C',28],['D',27],['E',25],['F',20],['G',14],['其他',8]];
  $.plot("#chartA1_2_e", [A1_2_e], {
    series: {
      bars: {
        show: true,
        barWidth: 0.5,
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
  var A1_2_f = [{label:'入室盗窃', data:18},{label:'诈骗', data:15},{label:'抢夺', data:11},{label:'抢劫', data:10},{label:'扒窃', data:9},{label:'拎包', data:9},
                {label:'故意伤害', data:9},{label:'盗窃机动车', data:9},{label:'其他', data:10}];
  $.plot($("#chartA1_2_f"), A1_2_f, {
    series: {
      pie: {
        show: true,
        radius: 1,
        label: {
          show: true,
          radius: 2/3,
          formatter: function(label, series){
            return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'+label+'<br/>'+Math.round(series.percent)+'%</div>';
          },
          threshold: 0.1
        }
      }
    },
    legend: {
      show: false
    }
  });
  var A1_2_g = [['入室盗窃',18],['诈骗',15],['抢夺',11],['抢劫',10],['扒窃',9],['拎包',9],['故意伤害',9],['盗窃机动车',9],['其他',10]];
  $.plot("#chartA1_2_g", [A1_2_g], {
    series: {
      bars: {
        show: true,
        barWidth: 0.5,
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
  var A1_4_a = [{label:'A', data:18},{label:'B', data:15},{label:'C', data:11},{label:'D', data:10},{label:'E', data:9},{label:'F', data:9},
                {label:'G', data:9},{label:'其他', data:10}];
  $.plot($("#chartA1_4_a"), A1_4_a, {
    series: {
      pie: {
        show: true,
        radius: 1,
        label: {
          show: true,
          radius: 2/3,
          formatter: function(label, series){
            return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'+label+'<br/>'+Math.round(series.percent)+'%</div>';
          },
          threshold: 0.1
        }
      }
    },
    legend: {
      show: false
    }
  });
  $.plot($("#chartA1_4_b"), A1_4_a, {
    series: {
      pie: {
        show: true,
        radius: 1,
        label: {
          show: true,
          radius: 2/3,
          formatter: function(label, series){
            return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'+label+'<br/>'+Math.round(series.percent)+'%</div>';
          },
          threshold: 0.1
        }
      }
    },
    legend: {
      show: false
    }
  });
//  var A1_4_ca = [['入室盗窃',18],['诈骗',15],['抢夺',11],['抢劫',10],['扒窃',9],['拎包',9],['故意伤害',9],['盗窃机动车',9],['其他',10]];
  var A1_4_ca = [['A',345],['B',488],['C',10223],['D',0],['E',233],['F',455],['G',988],['其他',0]];
  var A1_4_cb = [['A',123],['B',122],['C',2000],['D',0],['E',117],['F',100],['G',322],['其他',0]];
  var A1_4_cc = [['A',144],['B',122],['C',10000],['D',0],['E',200],['F',300],['G',500],['其他',0]];
  var A1_4_cd = [['A',18],['B',122],['C',125],['D',0],['E',33],['F',45],['G',12],['其他',0]];
  $.plot("#chartA1_4_c", [
      {label:"总值", data:A1_4_ca},
      {label:"平均值", data:A1_4_cb},
      {label:"最大值", data:A1_4_cc},
      {label:"最小值", data:A1_4_cd}
    ], {
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
    legend:{ show:true}
  });

  $.plot("#chartA1_4_d", [
      {label:"最小值", data:[["9-28",12],["9-29",34],["9-30",30],["9-31",24],["10-1",3],["10-2",5],["10-3",7],["10-4",10],["10-5",11],["10-6",23],["10-7",34],["10-8",56],["10-9",34]]}
    ], {
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

  $.plot("#chartA1_4_e", [
      {label:"最小值", data:[["w1",120],["w2",340],["w3",300],["w4",240]]}
    ], {
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

  $.plot("#chartA1_4_f", [
      {label:"值", data:[["w1",120],["w2",340],["w3",300],["w4",240]], bars:{
        show:true,
        barWidth: 0.3,
        align: "center",
        fill:0.3
      }, lines:{show:false}, points:{show:false}},
      {label:"增长率", data:[null,["w2",240],["w3",200],["w4",340]], lines:{show:true}, points:{show:false}}
    ], {
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
    legend:{show:true}
  });

  $.plot("#chartA1_4_g", [
      {label:"入室盗窃", data:[["w1",3],["w2",6],["w3",2],["w4",12]]},
      {label:"诈骗", data:[["w1",2],["w2",3],["w3",1],["w4",0]]},
      {label:"抢夺", data:[["w1",1],["w2",0],["w3",0],["w4",3]]},
      {label:"抢劫", data:[["w1",1],["w2",0],["w3",0],["w4",0]]},
      {label:"扒窃", data:[["w1",3],["w2",6],["w3",3],["w4",8]]},
      {label:"故意伤害", data:[["w1",0],["w2",2],["w3",0],["w4",0]]}
    ], {
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
    legend:{show:true}
  });

  $.plot("#chartA2_4_a", [
      {label:"当月", data:[["9-28",12],["9-29",34],["9-30",30],["9-31",24],["10-1",3],["10-2",5],["10-3",7],["10-4",10],["10-5",11],["10-6",23],["10-7",34],["10-8",56],["10-9",34]]}
    ], {
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

  $.plot("#chartA2_4_b", [
      {label:"2013年", data:[["1月",12],["2月",30],["3月",24],["4月",3],["5月",5],["6月",7],["7月",10],["8月",11],["9月",23],["10月",34],["11月",56],["12月",33]], bars:{
        show:true,
        barWidth: 0.3,
        align: "left",
        fill:0.3
      }, lines:{show:false}, points:{show:false}},
      {label:"2014年", data:[["1月",34],["2月",33],["3月",12],["4月",56],["5月",53],["6月",17],["7月",11],["8月",34],["9月",44],["10月",32],["11月",11],["12月",34]],bars:{
        show:true,
        barWidth: 0.3,
        align: "right",
        fill:0.3
      }, lines:{show:false}, points:{show:false}},
      {label:"2014年同比", data:[["1月",0.34],["2月",0.33],["3月",-0.12],["4月",0.56],["5月",0.53],["6月",-0.17],["7月",0.11],["8月",0.34],["9月",0.44],["10月",0.32],["11月",0.11],["12月",0.34]],
        lines:{show:true}, points:{show:true}, yaxis: 2
      },
      {label:"2013年同比", data:[["1月",0.34],["2月",0.33],["3月",-0.12],["4月",0.56],["5月",0.53],["6月",-0.17],["7月",0.11],["8月",0.34],["9月",0.44],["10月",0.32],["11月",0.11],["12月",0.34]],
        lines:{show:true}, points:{show:true}, yaxis: 2
      },
      {label:"2014年环比", data:[["1月",0.34],["2月",0.33],["3月",-0.12],["4月",0.56],["5月",0.53],["6月",-0.17],["7月",0.11],["8月",0.34],["9月",0.44],["10月",0.32],["11月",0.11],["12月",0.34]],
        lines:{show:true}, points:{show:true}, yaxis: 2
      },
      {label:"2013年环比", data:[["1月",0.34],["2月",0.33],["3月",-0.12],["4月",0.56],["5月",0.53],["6月",-0.17],["7月",0.11],["8月",0.34],["9月",0.44],["10月",0.32],["11月",0.11],["12月",0.34]],
        lines:{show:true}, points:{show:true}, yaxis: 2
      }
    ], {
    xaxis: {
      mode: "categories",
      autoscaleMargin: 0.05,
      tickLength: 0
    },
    yaxes: [
      { position: "left",min: 0 },
      {
        position: "right",
        tickFormatter: null,
        tickFormatter: function (v, axis) {
          return (parseFloat(v.toFixed(axis.tickDecimals))*100) +"%";
        }
      }
    ], legend:{show:true}
  });

  var radarChartData = {
    labels: ["数据稀疏率", "语义质量", "更新率", "利用率", "关联性"],
    datasets: [{
      label: "001",
      fillColor: "rgba(220,220,220,0.2)",
      strokeColor: "rgba(220,220,220,1)",
      pointColor: "rgba(220,220,220,1)",
      pointStrokeColor: "#fff",
      pointHighlightFill: "#fff",
      pointHighlightStroke: "rgba(220,220,220,1)",
      data: [0.11,0.23,0.30,0.54,0]
    }]
  };
  var myRadar = new Chart(document.getElementById("chartA4_1_a").getContext("2d")).Radar(radarChartData, {
    responsive: true
  });
});

function detail1(value,row,index) {
  if (value=='1') return '<div style="border:1px solid red; width:12px; height:12px;margin-left:5px;"></div>';
}
</script>
</html>