/**
 * 为zui的pager分页实现功能 
 */
(function ($) {
  $.ZuiPager = function () {
	//页面初始化参数，一般new的时候指定，之后不再更改了 {pageSize-一页显示的条数,divPageId-显示分页的DIV,}
	var _argInit = {"pageSize":20,"pageShowCount":20}; 
    //页面操作参数，存放动态信息，如当前点击的页、总页数、页显示的范围等
    var _argDynamic = {"pageNumber":1,"totalPage":-1,"beginPage":1,"endPage":_argInit.pageShowCount};
        
    //this.initPager方法，加上了this，就是公有方法了，外部可以访问。
    this.initPager = function (arg) {   
      if(arg){
    	_argInit = arg;
    	_argInit.pageSize = arg.pageSize?arg.pageSize:20; //每页显示的条数
    	_argInit.pageShowCount = arg.pageShowCount?arg.pageShowCount:20; //显示多少页，用于点击相应的页面，如：1..20页，10..30页
    	_argDynamic.endPage = _argInit.pageShowCount;
      }
      if(!_argInit.divPageId || !$("#"+_argInit.divPageId).attr("id")){
        showAlert("初始化分页", "没有指定包含分页的DIV，无法初始化分页！", "error");
        return;
      }
      if(!_argInit.objPager){
        showAlert("初始化分页", "没有指定分页对象，无法初始化分页！", "error");
        return;
      }
      if(_argInit.pageSize<1 || _argInit.pageSize>50){
    	showAlert("初始化分页", "pageSize范围[1,50]", "error");
        return;
      }
      if(_argInit.pageShowCount<1 || _argInit.pageShowCount>25){
      	showAlert("初始化分页", "pageShowCount范围[1,25]", "error");
          return;
        }
      //创建页面
      this.createPager();
    };
    //创建分页页面html
    this.createPager = function (){
      //alert("createPager()...");
      var objDivPager = $("#"+_argInit.divPageId);
      //<ul id="ul_pager" class="pager pager-loose">
      var objUl = $("<ul></ul>");
      objUl.attr("id","ul_pager"+_argInit.divPageId);
      objUl.addClass("pager");
      objUl.addClass("pager-loose");
      objDivPager.append(objUl);
      //var liPrev = '<li id="li_prev" class="previous disabled"><a href="#" onclick="_argInit.obj.showPrevPage();">« 上一页</a></li>';     
      var liPrev = '<li id="li_prev'+_argInit.divPageId+'" class="previous disabled"><a href="#">« 上一页</a></li>';     
      objUl.append(liPrev);
      //<li class="active"><a href="#">1</a></li>
      for(var i=1;i<=_argInit.pageShowCount;i++){
    	var liClass = i==1?"active":null;
        //var liStr = '<li id="li_page_'+i+'" class="'+liClass+'"><a href="#" onclick="showPage('+i+');">'+i+'</a></li>';
    	var liStr = '<li id="li_page_'+_argInit.divPageId+i+'" class="'+liClass+'"><a href="#">'+i+'</a></li>';
        objUl.append(liStr);
      }
      //var liNext = '<li id="li_next" class="next"><a href="#" onclick="showNextPage();">下一页 »</a></li>';
      var liNext = '<li id="li_next'+_argInit.divPageId+'" class="next"><a href="#">下一页 »</a></li>';
      objUl.append(liNext);
    };
    
    this.getArgDynamic = function (){
      return _argDynamic;
    };
    //设置总共多少条数据
    this.setTotalCount = function(totalCount,pageNumber){
      if(!totalCount){
        return;
      }
      if(pageNumber && pageNumber==1 && _argDynamic.pageNumber!= pageNumber){
    	_argDynamic.pageNumber = pageNumber;
        _argDynamic.beginPage = 1;
	    _argDynamic.endPage = _argDynamic.beginPage + _argInit.pageShowCount - 1;  
      }
      _argDynamic.totalCount = totalCount;
      var totalPage = Math.ceil(totalCount/_argInit.pageSize);
      //当总页数变化了,总页数还小于需要显示的页数时，需要调整分页工具条重新居中
      if(_argDynamic.totalPage<0 || (totalPage!=_argDynamic.totalPage && totalPage<_argInit.pageShowCount)){
    	//alert("should center ...");
    	_argDynamic.shouldCenter = true;
      }else{
    	_argDynamic.shouldCenter = false;  
      }
      _argDynamic.totalPage = totalPage;
      //alert("setTotalCount() totalCount="+totalCount+" totalPage="+_argDynamic.totalPage+" pageSize="+_argInit.pageSize);
      //this.alertArgs();
      this.adjustPageShowStyle();
    };
    
    //公有方法
    this.alertArgs = function(title){
      alert(title+" "+"_argInit:"+JSON.stringify(_argInit)+"  _argDynamic="+JSON.stringify(_argDynamic));	
    };
    
    //内部方法，不对外
    //显示前一页
    this.showPrevPage = function(){
      //alert("showPrevPage()... pageNum="+_argDynamic.pageNumber);
      if(_argDynamic.pageNumber>1){
    	if(typeof(this.showPage)=="function"){
     	  this.showPage(_argDynamic.pageNumber-1);
    	}else{
    	  _argInit.objPager.showPage(_argDynamic.pageNumber-1);
    	}
      }
      //adjustPageShowStyle();
    };
    //刷新当前页
    this.refreshPage = function(){
      //alert(typeof(this.showPage)=="function");
      if(typeof(this.showPage)=="function"){
        this.showPage(_argDynamic.pageNumber);
      }else{
    	_argInit.objPager.showPage(_argDynamic.pageNumber);
      }
    };
    //显示指定的分页内容，当点击某个分页按钮时触发此操作
    this.showPage = function(pageIdx){
      var pageNumber = pageIdx;
      //alert("showPage() pageIdx="+pageIdx+ "  pageText="+$("#li_page_"+pageIdx).text()+" pageNumber="+pageNumber);
      //onSelectPage(1, 2);
      if(_argInit.onSelectPage && typeof(_argInit.onSelectPage)=="function"){
    	//设置参数，查询数据
    	_argDynamic.pageNumber = pageNumber;
    	_argInit.onSelectPage(_argDynamic.pageNumber,_argInit.pageSize);
    	//页按钮显示调整，把当前页居中，左右变化按钮页数字
    	//adjustPageShowStyle();
      }    
    };
    //显示后一页
    this.showNextPage = function(){
      //alert("showNextPage()... pageNum="+_argDynamic.pageNumber+"  "+typeof(this.showPage));
      if(_argDynamic.pageNumber<_argDynamic.totalPage){    
        if(typeof(this.showPage)=="function"){
          this.showPage(_argDynamic.pageNumber+1);
        }else{
          _argInit.objPager.showPage(_argDynamic.pageNumber+1);
        }
      }
  	  //adjustPageShowStyle();
    };
    /**
     * 调整页按钮显示样式
     * 1、当前页超过pageShowCount/2时，居中高亮显示，其它页不高亮显示
     * 2、当前页为第一页时，“前一页”按钮不可用；当前页为最后一页时，“后一页”不可用
     */
    this.adjustPageShowStyle = function(){
      //this.alertArgs("adjust:");
      //_argInit.objPager.alertArgs("adjust2:");
      //alert("adjustPageShowStyle() pageNumber="+_argDynamic.pageNumber);
      this.showPager();
      //判断前一页
      var obj_li_pre = $("#li_prev"+_argInit.divPageId);
      obj_li_pre.unbind("click");
      if(_argDynamic.pageNumber==1){
    	  obj_li_pre.addClass("disabled");  
    	//$("#li_prev").unbind("click",_argInit.obj.showPrevPage);
      }else{
    	  obj_li_pre.removeClass("disabled");
    	  obj_li_pre.bind("click",_argInit.objPager.showPrevPage);
      	//$("#li_prev").bind("click",function(e){_argInit.obj.showPrevPage();});    	
      }
      //判断后一页
      var obj_li_next = $("#li_next"+_argInit.divPageId);
      obj_li_next.unbind("click");
      if(_argDynamic.pageNumber==_argDynamic.totalPage){
    	obj_li_next.addClass("disabled");  
      }else{
    	obj_li_next.removeClass("disabled");
    	obj_li_next.bind("click",_argInit.objPager.showNextPage);
      }
      //清除
      //当前页居中
      var page_middle = Math.ceil(_argInit.pageShowCount/2);
      if(_argDynamic.totalPage >= _argDynamic.endPage){
	    if(_argDynamic.pageNumber>(_argDynamic.beginPage+page_middle)){//如果当前页过半，则调整显示页面值
	      _argDynamic.beginPage = _argDynamic.pageNumber - page_middle;
	      _argDynamic.endPage = _argDynamic.beginPage + _argInit.pageShowCount - 1;    	
	      if(_argDynamic.endPage > _argDynamic.totalPage){
	        _argDynamic.endPage = _argDynamic.totalPage;
	        _argDynamic.beginPage = _argDynamic.endPage - _argInit.pageShowCount + 1;
	      }
	    }else if(_argDynamic.pageNumber<(_argDynamic.beginPage+page_middle)){//如果当前页不过半，也要调整显示页面值
	      _argDynamic.beginPage = _argDynamic.pageNumber - page_middle;
	      if(_argDynamic.beginPage<1){
	      	_argDynamic.beginPage = 1;
	      }
	      _argDynamic.endPage = _argDynamic.beginPage + _argInit.pageShowCount -1; 
	    }  
      }
      //alert("adjustPageShowStyle() _argInit="+JSON.stringify(_argInit)+" _argDynamic="+JSON.stringify(_argDynamic));
      
      var pageShowCount = _argInit.pageShowCount;
      //如果当前总页数<需要显示的页数时，需要把多余的页按钮隐藏
      if(_argDynamic.totalPage < pageShowCount){
    	pageShowCount = _argDynamic.totalPage;
        for(var i=_argDynamic.totalPage+1;i<=_argInit.pageShowCount;i++){
          var objLi = $("#li_page_"+_argInit.divPageId+i);
          objLi.css("display","none");
        }
      }
      for(var i=1;i<=pageShowCount;i++){
    	var objLi = $("#li_page_"+_argInit.divPageId+i);
        objLi.css("display","block");
    	objLi.removeClass("active");
    	objLi.find("a").blur();
    	var pageNum = _argDynamic.beginPage+i-1;
    	if(pageNum<10){pageNum = "0"+pageNum;}
    	objLi.find("a").text(pageNum);
    	objLi.find("a").unbind("click");
    	objLi.find("a").bind("click",function(e){_argInit.objPager.showPage(parseInt($(this).text()));});
    	if(pageNum == _argDynamic.pageNumber){
    	  objLi.addClass("active");
    	}
      }
      //分页工具条居中
      this.alignCenter();
    };
    
    //分页工具条居中
    this.alignCenter = function(){
      //ul居中
      try{
        var pageShowCount = _argInit.pageShowCount; 
        if(_argDynamic.totalPage < pageShowCount){
          pageShowCount = _argDynamic.totalPage;
        }
  	    //alert("dgList:"+$("dgList").width()+" div_pager:"+$("#div_pager").width()+" ul_pager:"+$("#ul_pager").width());
  	    //alert("_argDynamic.shouldCenter="+_argDynamic.shouldCenter);
  	    if(_argDynamic.shouldCenter){
          var obj_li_pre = $("#li_prev"+_argInit.divPageId);
  	      var obj_li_next = $("#li_next"+_argInit.divPageId);
          var shownWidth=0;//实际页面显示的宽度，用于计算居中
          var borderLeft = parseInt(obj_li_next.find("a").css("border-left-width"))||0;
          var marginLeft = parseInt(obj_li_next.find("a").css("margin-left"))||0;
          var paddingLeft = parseInt(obj_li_next.find("a").css("padding-left"))||0;
          var outWidth = (borderLeft+paddingLeft)*2;
          var li_prev_width = obj_li_pre.find("a").width()+outWidth;
          shownWidth += li_prev_width;
          var li_next_width = obj_li_next.find("a").width()+outWidth+marginLeft;
          shownWidth += li_next_width;

          for(var i=1;i<=pageShowCount;i++){
            var objLi = $("#li_page_"+_argInit.divPageId+i);
      	    shownWidth += objLi.find("a").width()+outWidth+marginLeft;
          }
          if(pageShowCount>0){
            //shownWidth += ($("#li_page_1").find("a").width()+outWidth+marginLeft) * pageShowCount;
          } 
          var obj_divPager = $("#"+_argInit.divPageId);
          var obj_ul = $("#ul_pager"+_argInit.divPageId);
          var divPager_width = obj_divPager.width();
          var offLeft = parseInt((divPager_width - shownWidth)/2);
          //alert("page middle() ul_width="+ul_width+"  shownWidth="+shownWidth+"  offLeft="+offLeft);
          obj_ul.css("padding-left",offLeft);
          //alert("divPager width="+divPager_width+"  ul_width="+obj_ul.width()+"  offLeft="+offLeft);
          if(divPager_width ==0){ //还没有初始化好
            //_argDynamic.totalPage = -1;
            _argDynamic.shouldCenter = true;
          }else{
        	//_argDynamic.shouldCenter = false;
          }
  	    }
      }catch(e){
  	    showAlert("调整样式", "调整分页居中失败：</br>"+(e.message)+"！<br/>", "error", function(){});
      }	  
    };

    //当没有查到记录的时候，需要把分页工具条隐藏
    this.hidePager = function(){
      var obj_div_pager = $("#"+_argInit.divPageId);
      obj_div_pager.css({"visibility":"hidden"});
    };
    //当查到记录的时候，需要把分页工具条显示
    this.showPager = function(){
      var obj_div_pager = $("#"+_argInit.divPageId);
      obj_div_pager.css({"visibility":"visible"});
    };
    
  };  
})(jQuery);

//使用方法  var a = new $.DragField({ title: "Hi" }); a.testFun();