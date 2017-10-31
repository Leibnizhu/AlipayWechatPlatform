var curEid = -1;
var vm = new Vue({
    el: "#all",
    data: {
        eid: parseInt(localStorage.id),
        selectedEid: parseInt(localStorage.id), //下拉框选择的id
        name: undefined,
        role: parseInt(localStorage.role),
        curName: localStorage.name,
        appid: undefined,
        appsecret: undefined,
        verify: undefined,
        projUrl: undefined,
        accList: []
    },
    mounted: function () {
        this.initMenu();
        this.initAll();
        this.initFormValidate();
    },
    computed: {},
    watch: {
        "selectedEid": function (newVal, oldVal) {
            if(oldVal === -1){
                return;
            }
            vm.loadData(newVal);
        }
    },
    methods: {
        initMenu: function () {
            $("#publicAccount").addClass("active").show();
            $("#wechatNumberId").find("a").addClass("active");
        },
        initAll: function () {
            this.loadData(localStorage.id, function(data){
                vm.curName = data.name;
                if (data.role === 0) {
                    curEid = data.id;
                    vm.processAdmin();
                }
            });
        },
        loadData: function (eid, cb) {
            authAjax({
                url: "/bms/offAcc/" + eid,
                type: 'GET',
                success: function (data) {
                    vm.resumeDataFromAjax(data);
                    if(cb)  cb.call(this, data);
                }
            });
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
                    $("#accountList").css("display", "block");
                }
            });
        },
        resumeDataFromAjax: function (data) {
            vm.eid = data.id;
            vm.name = data.name;
            vm.appid = data.appid;
            vm.appsecret = data.appsecret;
            vm.verify = data.verify;
            vm.projUrl = data.projUrl;
        }
    }
});