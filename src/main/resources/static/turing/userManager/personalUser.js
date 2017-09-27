(function($,$w){
	if(typeof $w.$personalUser=='undefined'){
		$personalUser={
				//获取个人信息
				searchEnterprise:function(){
					  $.ajax({
		                    url: $basePath+'user/searchEnterprise',
		                    type: 'post', //数据发送方式
		                    dataType: 'json', //接受数据格式
		                    beforeSend: function () {//加载数据时显示
		                    },
		                    error: function () { //失败 外界异常引起
		                        //swal("错误", "服务器发生错误，请联系管理员", "error");
		                    },
		                    success: function (data) { //成功 (在此查询范围内有数据)
		                    	$('#personalForm').find('input[name=name]').val(data.enterprise.name);
		                    	$('#personalForm').find('input[name=email]').val(data.enterprise.email);
		                    	$('#personalForm').find('input[name=mobile]').val(data.enterprise.mobile);
		                    	$('#personalForm').find('input[name=phone]').val(data.enterprise.phone);
		                    	$('#personalForm').find('input[name=website]').val(data.enterprise.website);
		                    }
		                });
				},
			       validate:function(){
		            	//为表单添加验证规则
		        		validator = $("#personalForm").validate({
		        			rules:{
		        				name: {
		                            required: true,
		                            maxlength: 20
		                        },
		                        newPassword:{
		                        	required: false,
		                        	minlength:6,
			                        maxlength:32
		                        },
		                        newPasswords:{
		                        	required: false,
		                        	equalTo:"#newPassword"
		                        },
		        				password:{
		        					required:true
		        				},
		        				email: "required"
		        			},
		        			messages:{
		        				name:{required:"请输入用户名",maxlength:"用户名长度不能超过20个字符"},
		        				newPassword:{maxlength:"密码长度不能超过32个字符",minlength:"密码长度不能少于6个字符"},
		        				newPasswords:{ equalTo: "请再次确认密码，否则不能通过"},
		        				password:{required:"请输入密码"},
		        				email:"请输入邮箱"
		        			},
		        			errorPlacement:function(error,element){
		        				error.appendTo(element.parent());
		        				element.parent().removeClass("has-success").addClass("has-error");
		        			},
		        			success:function(element){
		        				element.parent().removeClass("has-error").addClass("has-success");
		        			}
		        		});
		        		return validator;
		            },
			     submitEditUser: function () {
			       		$("#oldPassword").val($.md5($("#oldPassword").val()));
			       		$("#newPassword").val($.md5($("#newPassword").val()));
			       		$("#newPasswords").val($.md5($("#newPasswords").val()));
		                var newForm = $("#personalForm");
		                $.ajax({
		                    url: $basePath+'user/updateOrAddEnterprise',
		                    type: 'post', //数据发送方式
		                    data: newForm.serialize(),
		                    dataType: 'json', //接受数据格式
		                    beforeSend: function () {//加载数据时显示
		                    },
		                    error: function () { //失败 外界异常引起
		                        swal("错误", "服务器发生错误，请联系管理员", "error");
		                    },
		                    success: function (data) { //成功 (在此查询范围内有数据)
		                    	if(data=="success"){
			                    	swal({
					                    title: "修改成功！",
					                    text: "",
					                    type: "success",
					                    timer: 2000,
					                    showConfirmButton: false
					                });
		                    	}else{
		                    		swal({
					                    title: "密码错误！",
					                    text: "",
					                    type: "error",
					                    timer: 2000,
					                    showConfirmButton: false
					                });
		                    	}
                                $("#oldPassword").val('');
                                $("#newPassword").val('');
                                $("#newPasswords").val('');
		                    }
		                });
		            }
			
		}		
	}
})(jQuery,window);

$(document).ready(function(){ 
	var s=document.body.clientHeight-100;
	$('.container-widget').css("height",s);
	$personalUser.searchEnterprise();
	  var newForm = $("#personalForm");
	   newForm.submit(function(){
          var validate=$personalUser.validate();
      	   if(validate.form()){
      		 $personalUser.submitEditUser();
        	} 
     //返回false阻止正常的表单提交
        	return false;
      	}); 	
})
 
  
