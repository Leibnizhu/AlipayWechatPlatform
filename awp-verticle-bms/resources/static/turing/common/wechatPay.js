/**
 先引入jQuery和jweixin的依赖，然后
 window.WechatPay.init({
    el:"支付按钮的选择器, #payBtn",
    preUrl:"预处理API地址, $basePath + 'pay/wx/pre/2/'",
    orderUrl:"下单API地址, $basePath + 'pay/wx/order'",
    orderData:{//下单数据JSON
        eid:2,
        orderId:new Date().getTime(),
        openId:'of3Pl0-vJ7gN3kINjwup3TyCOBuk',
        price:1,
        name:'产品名',
        callback:'http://www.baidu.com'
    },
    //以下字段非必须
    debug: "true/false",
    paySuccess: function(){alert("支付成功的回调方法")},
    payError: function(){alert("支付失败的回调方法")},
    alertMethod: alert
});
*/
(function ($w) {
    function p() {
        this.config = {};
        this.isWechatPaySupport = false;
        if (!jQuery || !$)
            console.error("必须引入jQuery依赖");
        if (!WeixinJSBridge)
            console.error("必须引入jweixin.js!");
    }

    $w.WechatPay = new p();

    p.prototype.init = function (config) {
        if (!config)
            console.error("config参数不能为空");
        if (!config.el)
            console.error("config的el参数不能为空");
        if (!config.preUrl)
            console.error("config的preUrl参数不能为空");
        if (!config.orderUrl)
            console.error("config的orderUrl参数不能为空");
        if (!config.orderData)
            console.error("config的orderData参数不能为空");
        config.debug = config.debug || false;
        config.paySuccess = config.paySuccess || function(){alert("支付成功")};
        config.payError = config.payError || function(msg){alert(msg)};
        config.alertMethod = config.alertMethod || alert;
        this.config = config;
        var el = config.el;
        $(el).on("click", function () {
            WechatPay.order();
        });
        $(el).prop('disabled', true);
        WechatPay.pre();
    };

    p.prototype.pre = function () {
        var debug = this.config.debug;
        var el = this.config.el;
        var preUrl = this.config.preUrl;
        $.ajax({
            url: preUrl + encodeURIComponent(location.href),
            type: 'GET',
            dataType: 'json',
            success: function (data) {
                if (debug) alert(JSON.stringify(data));
                wx.config({
                    debug: debug,                                        // 开启调试模式,调用的所有api的返回值会在客户端uialert出来
                    appId: data.appId,              // 必填，公众号的唯一标识
                    timestamp: data.timestamp,      // 必填，生成签名的时间戳
                    nonceStr: data.noncestr,        // 必填，生成签名的随机串
                    signature: data.signture,       // 必填，签名，见附录1
                    jsApiList: ['getBrandWCPayRequest']                 // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
                });
                wx.ready(function () {
                    wx.checkJsApi({
                        jsApiList: ['getBrandWCPayRequest'], // 需要检测的JS接口列表，所有JS接口列表见附录2,
                        success: function (res) {
                            if (debug) alert(JSON.stringify(res));
                            WechatPay.isWechatPaySupport = res.checkResult.chooseWXPay;
                            if (WechatPay.isWechatPaySupport) {
                                $(el).prop('disabled', false);
                            }
                        }
                    });
                });
                wx.error(function () {
                    WechatPay.config.alertMethod.call(window, "微信支付初始化失败");
                });
            }
        });
    };
    /**
     * 通知后台调用微信支付统一下单接口
     * 成功后调用onBridgeReady()方法
     */
    p.prototype.order = function () {
        var orderUrl = this.config.orderUrl;
        var orderData = this.config.orderData;
        var debug = this.config.debug;
        if (this.config.isWechatPaySupport) {
            $('#payBtn').prop('disabled', true);
            $.ajax({
                url: orderUrl,
                type: 'POST',
                data: orderData,
                dataType: 'json',
                success: function (data) {
                    if(debug) alert(JSON.stringify(data));
                    if (typeof WeixinJSBridge === "undefined") {
                        if (document.addEventListener) {
                            document.addEventListener('WeixinJSBridgeReady', function () {
                                WechatPay.pay(data)
                            }, false);
                        } else if (document.attachEvent) {
                            document.attachEvent('WeixinJSBridgeReady', function () {
                                WechatPay.pay(data)
                            });
                            document.attachEvent('onWeixinJSBridgeReady', function () {
                                WechatPay.pay(data)
                            });
                        }
                    } else {
                        WechatPay.pay(data);
                    }
                },
                error: function (data) {
                    WechatPay.config.alertMethod.call(window, "请联系工作人员");
                }
            })
        } else {
            WechatPay.config.alertMethod.call(window, "当前版本不支持微信支付");
        }
    };

    /**
     * H5通过JSSDK，带上prepay_id，发起支付申请
     * @param data 后台传来的统一订单接口返回数据
     */
    p.prototype.pay = function (data) {
        var debug = this.config.debug;
        var el = this.config.el;
        WeixinJSBridge.invoke(
            'getBrandWCPayRequest', {
                "appId": data.appId,     //公众号名称，由商户传入
                "timeStamp": String(data.timeStamp),         //时间戳，自1970年以来的秒数
                "nonceStr": data.nonceStr, //随机串
                "package": "prepay_id=" + data.packages,
                "signType": "MD5",         //微信签名方式：
                "paySign": data.paysign //微信签名
            },
            function (res) {
                if (debug) alert(JSON.stringify(res));
                $(el).prop('disabled', false);
                if (res.err_msg === "get_brand_wcpay_request:ok") {
                    WechatPay.config.paySuccess.call(WechatPay);
                } else if (res.err_msg === "get_brand_wcpay_request:cancel") {
                    WechatPay.config.payError.call(WechatPay, "未完成支付");
                } else { // 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。
                    WechatPay.config.payError.call(WechatPay, res.err_desc);
                }
            }
        );
    };
})(window);
