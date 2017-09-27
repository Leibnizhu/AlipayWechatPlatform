(function($,$w){
	if(typeof $w.$Commontable=='undefined'){
		$Commontable={
		    table:"",
			getCommonTable:function(dataObject,aoColumnsData,url){   
	    	       $Commontable.table = dataObject.dataTable({
		                  //lengthMenu: [5, 10, 20, 30],//这里也可以设置分页，但是不能设置具体内容，只能是一维或二维数组的方式，所以推荐下面language里面的写法。
						// destroy:true,
		 				// bProcessing: false, // 是否显示取数据时的那个等待提示
		                // bServerSide: false,//这个用来指明是通过服务端来取数据
		                 sAjaxSource: $basePath + url, //这个是请求的地址
		                 fnServerData: $Commontable.retrieveData, // 获取数据的处理函数
		                 paging: true,
		                 ordering: true,//是否启用排序
		                 searching: false,//搜索
		                 aoColumns: aoColumnsData,
    
		                 language: {
		                      lengthMenu: '<div style="float:left;padding:6px 6px 0 0px;">展示</div>'+'<div style="float:left;"><select class="form-control input-xsmall">' + '<option value="1">1</option>' + '<option value="10">10</option>' + '<option value="20">20</option>' + '<option value="30">30</option>' + '<option value="40">40</option>' + '<option value="50">50</option>' + '</select></div>'+'<div style="float:left;padding:6px 0 0 6px;">条记录</div>',//左上角的分页大小显示。
		                      search: '搜索：',//右上角的搜索文本，可以写html标签
		                      paginate: {//分页的样式内容。
		                          previous: "上一页",
		                          next: "下一页",
		                          first: "第一页",
		                          last: "最后"
		                      },

		                      zeroRecords: "没有内容",//table tbody内容为空时，tbody的内容。
		                      //下面三者构成了总体的左下角的内容。
		                      info: "总共 _PAGES_ 页 _MAX_ 条，显示第 _START_ 到 _END_ 条",//左下角的信息显示，大写的词为关键字。
		                      infoEmpty: "0条记录",//筛选为空时左下角的显示。
		                      infoFiltered: ""//筛选之后的左下角筛选提示，
		                  },
		                  paging: true,
		                  pagingType: "full_numbers",//分页样式的类型

		              });
		            
			},
			retrieveData:function( source,aoData, fnCallback) {
	              $.ajax({
	                  url : source,//这个就是请求地址对应sAjaxSource
	                  data : {"aoData":JSON.stringify(aoData)},//这个是把datatable的一些基本数据传给后台,比如起始位置,每页显示的行数
	                  type : 'post',
	                  dataType : 'json',
	                  async : false,
	                  success : function(result) {
	                      fnCallback(result);//把返回的数据传给这个方法就可以了,datatable会自动绑定数据的
	                  },
	                  error : function(msg) {

	                  }
	              });
	          },
				          

		}}
})(jQuery,window);