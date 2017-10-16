var curEid = -1;
var vm = new Vue({
    el: "#all",
    data: {
        eid: -1,
        id: undefined,
        name: undefined,
        appid: undefined,
        appsecret: undefined,
        verify: undefined,
        accList: []
    },
    mounted: function () {
        this.initMenu();
        this.initData();
        this.initFormValidate();
    },
    computed: {},
    watch: {
        "eid": function (newVal, oldVal) {
            if(oldVal === -1){
                return;
            }
            authAjax({
                url: "/bms/offAcc/" + newVal,
                type: 'GET',
                success: function (data) {
                    vm.resumeDataFromAjax(data);
                }
            });
        }
    },
    methods: {
        initData: function () {
            authAjax({
                url: "/bms/offAcc/",
                type: 'GET',
                success: function (data) {
                    vm.resumeDataFromAjax(data);
                    if (data.role === 0) {
                        curEid = data.id;
                        vm.processAdmin();
                    }
                }
            });
        },
        initMenu: function () {
            $("#publicAccount").addClass("active");
            $("#wechatNumberId a").addClass("active");
            $("#publicAccount").show();
        },
        initFormValidate: function () {
            $.validator.addMethod('wechatVerify', function (value, element) {
                return this.optional(element) || /^MP_verify_\w{16}$/.test(value);
            }, '请输入正确的安全域名认证文件名');// 判断输入手机号是否格式正确

            $("#offAccInfo").validate({
                rules: {
                    name: {
                        required: true
                    },
                    appid: {
                        required: true
                    },
                    appsecret: {
                        required: true
                    },
                    verify: {
                        wechatVerify: true
                    }
                },
                messages: {
                    name: {
                        required: "请输入公众号名称/用户名"
                    },
                    appid: {
                        required: "请输入微信公众号AppID"
                    },
                    appsecret: {
                        required: "请输入微信公众号AppSecret"
                    },
                    verify: {
                        wechatVerify: "请输入正确的安全域名认证文件名"
                    }
                },
                submitHandler: function (form) {
                    authAjax({
                        type: "PUT",  //提交方式
                        url: "/bms/offAcc/",
                        data: $(form).serialize(),
                        dataType: "text",
                        success: function (result) {//返回数据根据结果进行相应的处理
                            if (result === "success") {
                                swal("成功", "更新公众号配置成功", "success");
                            } else {
                                swal("错误", "未能更新配置，数据库处理出错", "error");
                            }
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
                    vm.eid = curEid;
                    $("#accountList").css("display", "block");
                }
            });
        },
        resumeDataFromAjax: function (data) {
            vm.id = data.id;
            vm.name = data.name;
            vm.appid = data.appid;
            vm.appsecret = data.appsecret;
            vm.verify = data.verify;
        }
    }
});