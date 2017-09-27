(function($w){
		if(typeof $w.$Menu=='undefined'){
			var menu={
					 /**
			         * 私有数据域
			         */
			        data: {},
			        /**
			         * 初始化的方法，作为闭包的返回值
			         */
			        init: {
			            /**
			             * 初始化页面数据
			             */
			            initData: function () {
			            	menu.Func.setPermission();
			                return this;
			            },
			            /**
			             * 注册各种点击事件
			             */
			            initEvent: function () {
			            	
				         	 
			            }
			        },
			        Func:{
			        	//设置权限
                       setPermission:function(){
                    	   //普通用户不能看到系统管理
                    	   if(typeof $role != 'undefined' && $role == 2 ){
                    			$("#systemManagerLi").remove();
                    		}
                    	   
                    	  if(typeof $role != 'undefined' && $role !=0){
                           $.ajax({
                               url: $basePath+'user/getMenus',
                               type: 'post', //数据发送方式
                               dataType: 'json', //接受数据格式
                               beforeSend: function () {//加载数据时显示
                               },
                               error: function () { //失败 外界异常引起
                                   //swal("错误", "服务器发生错误，请联系管理员", "error");
                               },
                               success: function (response) { //成功 (在此查询范围内有数据)
                                if(response.success){
                                	var menuList= response.menuList;
                                	for(var i=0;i<response.menuList.length;i++){
                                		if(!menuList[i].userId){
                                			//alert(menuList[i].menuEnglish);
                                			$("#"+menuList[i].menuEnglish).css('display','none'); 
                                		}
                                	}
                                	
                                }
                               }
                           });
                    	   }
			        	}
			    }
			}
			//向外开放方法
			$w.$Menu={
					init:menu.init
			}
		}
					
})(window);

$(document).ready(function(){
	//console.log($name);
	if(typeof $name != 'undefined'){
        $('#userName').text($name);
    }
	/*$('#leftmenu').children().on('click', function () {
		var id = $(this).children().eq(1).attr('id');
		console.log(id);
        $('#' + id).css("color", "#00ff00");
        $('#activityAalysis').css("color", "#00ff00");
		$('#activityAalysis').css("display", "block");
    })
	$("#userAnalysis").removeClass("active");
	$(this).css("display", "block");
	$("#enterpriseListMenu").addClass("active");*/

	$Menu.init.initData().initEvent();
})
 