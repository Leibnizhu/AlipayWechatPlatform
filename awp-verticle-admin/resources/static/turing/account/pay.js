/**
 * 后台支付配置页面处理的js
 * @author Leibniz
 * Created on Sep 5, 2017 11:16
 */

var vm = new Vue({
    el: "#all",
    data: {
        uid: localStorage.id,
        curName: localStorage.name,
        selectedEid: localStorage.id,
        accList: [],
        wxPay: {
            appId: "",
            appSecret: "",
            mchId: "",
            payKey: "",
            opened: undefined,
            submitAble: true
        },
        zfbPay: {
            appId: "",
            appPrivKey: "",
            zfbPubKey: "",
            opened: undefined,
            submitAble: true}
    },
    mounted: function () {
        this.initMenu();
        this.initWxPayValidator();
        this.initZfbPayValidator();
        this.updatePayProp(localStorage.id);
        if(localStorage.role === "0"){
            this.processAdmin();
        }
    },
    watch: {
        'wxPay.opened': function () {
            $("#certDiv").css("display", this.wxPay.opened == 1 ? "" : "none");
        },
        'selectedEid': function(){
            this.updatePayProp();
        }
    },
    methods: {
        /**
         * 菜单展开、加高亮
         */
        initMenu: function () {
            $("#publicAccount").addClass("active").show();
            $("#wxpayListLi").find("a").addClass("active");
        },
        /**
         * 显示微信支付配置模态窗
         */
        showEditWxPay: function () {
            $("#editWxPay").modal("show");
        },
        /**
         * 显示支付宝支付配置模态窗
         */
        showEditZfbPay: function () {
            $("#editZfbPay").modal("show");
        },
        /**
         * 更新微信和支付宝支付配置回显
         */
        updatePayProp: function (id) {
            authAjax({
                url: "/bms/pay/" + (id || (typeof(vm) === "undefined" ? 1 : vm.selectedEid)),
                type: "GET",
                dataType: "JSON",
                success: function (data) {
                    vm.wxPay.opened = data.wx.opened || "0";
                    vm.wxPay.appId = data.wx.appId || "请先进行微信公众号配置";
                    vm.wxPay.appSecret = data.wx.appSecret || "请先进行微信公众号配置";
                    vm.wxPay.mchId = data.wx.mchId;
                    vm.wxPay.payKey = data.wx.payKey;

                    vm.zfbPay.opened = data.zfb.opened || "0";
                    vm.zfbPay.appId = data.zfb.appId;
                    vm.zfbPay.appPrivKey = data.zfb.appPrivKey;
                    vm.zfbPay.zfbPubKey = data.zfb.zfbPubKey;
                }
            });
        },
        /**
         * 初始化微信配置表单验证
         */
        initWxPayValidator: function () {
            $("#wxPayForm").validate({
                rules: {
                    mchId: {required: true, rangelength: [10, 32]},
                    payKey: {required: true, rangelength: [32, 32]}
                    // cert: "required"
                },
                messages: {
                    mchId: {required: "请输入商户ID", rangelength: "商户ID长度至少10位至多32位"},
                    payKey: {required: "请输入支付密钥", rangelength: "支付密钥长度必须32位(字母数字)"}
                    // cert: {required: "请上传有效的证书文件"}
                },
                errorPlacement: function (error, element) {
                    $("#" + element.attr("msgId")).html(error);
                },
                submitHandler: function (form) {
                    vm.wxPay.submitAble = false;
                    authAjax({
                        type: "POST",  //提交方式
                        url: "/bms/pay/wx",
                        data: new FormData($("#wxPayForm")[0]),
                        processData: false,
                        contentType: false,
                        cache: false,
                        success: function (data) {//返回数据根据结果进行相应的处理
                            if (data.status === "success") {
                                swal({title: "提交成功！", text:"更新支付配置成功", icon:"success"}, function () {
                                    $("#editWxPay").modal("hide");
                                    vm.wxPay.submitAble = true;
                                });
                            } else if (data.status === "invalid") {
                                swal({title: "错误", text:"输入数据有误！", icon:"warning"}, function () {
                                    $("#editWxPay").modal("hide");
                                    vm.wxPay.submitAble = true;
                                });
                            } else {
                                swal("错误", "未能更新配置，数据库处理出错", "error");
                                vm.wxPay.submitAble = true;
                            }
                        },
                        error: function () {
                            swal("错误", "服务器异常，请联系管理员", "error");
                            vm.wxPay.submitAble = true;
                        }
                    });
                    return false;
                }
            });
        },
        /**
         * 初始化支付宝配置表单验证
         */
        initZfbPayValidator: function () {
            $("#zfbPayForm").validate({
                rules: {
                    appId: "required",
                    appPrivKey: "required",
                    zfbPubKey: "required"
                },
                messages: {
                    appId: "请输入支付宝应用AppID",
                    appPrivKey: "请输入支付宝应用私钥",
                    zfbPubKey: "请输入支付宝公钥"
                },
                errorPlacement: function (error, element) {
                    $("#" + element.attr("msgId")).html(error);
                },
                submitHandler: function (form) {
                    vm.zfbPay.submitAble = false;
                    authAjax({
                        type: "POST",  //提交方式
                        url: "/bms/pay/zfb",
                        data: $(form).serialize(),
                        cache: false,
                        success: function (data) {//返回数据根据结果进行相应的处理
                            if (data.status === "success") {
                                swal({title: "提交成功！", text:"更新支付配置成功", icon:"success"}, function () {
                                    $("#editZfbPay").modal("hide");
                                    vm.zfbPay.submitAble = true;
                                });
                            } else if (data.status === "invalid") {
                                swal({title: "错误", text:"输入数据有误！", icon:"warning"}, function () {
                                    $("#editZfbPay").modal("hide");
                                    vm.zfbPay.submitAble = true;
                                });
                            } else {
                                swal("错误", "未能更新配置，数据库处理出错", "error");
                                vm.zfbPay.submitAble = true;
                            }
                        },
                        error: function () {
                            swal("错误", "服务器异常，请联系管理员", "error");
                            vm.zfbPay.submitAble = true;
                        }
                    });
                    return false;
                }
            });
        },
        processAdmin: function () {
            authAjax({
                type: "GET",  //提交方式
                url: "/bms/offAcc/all",
                dataType: "json",
                success: function (result) {//返回数据根据结果进行相应的处理
                    vm.accList = result;
                    $("#accountList").css("display", "block");
                }
            });
        }
    }
});
