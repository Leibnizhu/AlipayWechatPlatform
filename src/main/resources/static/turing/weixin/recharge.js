var flag;
var recharge;
$(document).ready(function () {
//	alert(flag);
	recharge = $('#recharge').val();;
//	alert(recharge);
	var sign = $('#sign').val();
//	alert(location.href.split('#')[0]);
	wx.config({
	    debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
	    appId: 'wxdc4ec562c4b26c3f', // 必填，公众号的唯一标识
	    timestamp: $('#time').val(), // 必填，生成签名的时间戳
	    nonceStr: 'gzturing', // 必填，生成签名的随机串
	    signature: sign,// 必填，签名，见附录1
	    jsApiList: ['WeixinJSBridge','chooseWXPay','getBrandWCPayRequest'] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
	});
	wx.ready(function(){

	    alert("success");
	    $('#phoneTraffic').on("click","a", function(){
//	    	alert(flag);
	    	if (recharge == "yes"){
	    		if (flag ){
		    		var phone = $('#phone').val();
			       	var product = $(this).children().eq(0).text();
			       	var openid = $('#openid').val();
//			       	alert(product);
			       	$.ajax({
			       		url:$basePath + 'recharge/phoneRecharge?phone=' + phone + '&product=' + product + '&openid=' + openid,
			       		type:'post',
			       		contentType:'application/uft-8',
			       		dataType:'json',
			       		success:function(data){
			       			//alert(data.appId);
			       			//alert(data.timeStamp);
			       			//alert(data.nonceStr);
			       			//alert(data.packages);
			       			//alert(data.paysign);
			       			if (typeof WeixinJSBridge == "undefined"){
								if( document.addEventListener ){
									document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
								}else if (document.attachEvent){
									document.attachEvent('WeixinJSBridgeReady', onBridgeReady);
									document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
								}
							}else{
								onBridgeReady(data.appId,data.timeStamp,data.nonceStr,data.packages,data.paysign);
							}
			       		},
			       		error:function(data){
			       			
			       		}
			       	})
		    	}
	    	}
	    	
	    })
	    
		// config信息验证后会执行ready方法，所有接口调用都必须在config接口获得结果之后，config是一个客户端的异步操作，所以如果需要在页面加载时就调用相关接口，则须把相关接口放在ready函数中调用来确保正确执行。对于用户触发时才调用的接口，则可以直接调用，不需要放在ready函数中。
	});
	wx.error(function(res){
//	    alert("error");
		// config信息验证失败会执行error函数，如签名过期导致验证失败，具体错误信息可以打开config的debug模式查看，也可以在返回的res参数中查看，对于SPA可以在这里更新签名。
	});
	
})

function onBridgeReady(appid,timestamp,str,packages,paysign){
   WeixinJSBridge.invoke(
       'getBrandWCPayRequest', {
           "appId": String(appid),     //公众号名称，由商户传入     
           "timeStamp":String(timestamp),         //时间戳，自1970年以来的秒数     
           "nonceStr":String(str), //随机串     
           "package" : "prepay_id=" + packages,     
           "signType": "MD5",         //微信签名方式：     
           "paySign": String(paysign) //微信签名 
       },
       function(res){  
//    	   alert(JSON.stringify(res));
//    	   alert(res.err_msg);
           if(res.err_msg == "get_brand_wcpay_request：ok" ) {}     // 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。 
       }
   ); 
}
$(document).ready(function () {
	vailPhone();
})
function vailPhone(){
    var phone = jQuery("#phone").val();
    flag = false;
    var message = "";
    var myreg = /^(((13[0-9]{1})|(14[0-9]{1})|(17[0]{1})|(15[0-3]{1})|(15[5-9]{1})|(18[0-9]{1}))+\d{8})$/;       
    if(phone == ''){
    	message = "手机号码不能为空！";
    }else if(phone.length !=11){
    	message = "请输入有效的手机号码！";
    }else{
    	flag = true;
    }
    if (recharge == "yes"){
    	if(!flag){
        	jQuery("#phoneP").html(message);
        	$(".check-list").removeClass("check-list-r");
        	//jQuery("#phone").focus();
        }else{
        	jQuery("#phoneP").empty();
        	$.ajax({
    			url:$basePath + "recharge/checkPhone?phone=" + jQuery("#phone").val(),
    			type:'post',
    			contentType:'application/utf-8',
    			dataType:'json',
    			success:function(data){
    				$('#phoneTraffic').empty();
    				for(var i=0;i<data.price.length;i++){
    					var html = "<li><a href=\"javascript:;\"><h4 id=\"30M\">" +data.price[i].type + "</h4><span>" + "售价"+data.price[i].salePrice+"元" + "</span></a></li>";
    					$('#phoneTraffic').append(html);
    				}
    			},
    			error:function(data){
    				console.log(data);
    			}
    		})
    		$(".check-list").addClass("check-list-r");
        }
        return flag;
    }
    
}
