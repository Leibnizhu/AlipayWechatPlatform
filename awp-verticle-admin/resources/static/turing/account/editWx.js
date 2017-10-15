var vm = new Vue({
    el: "#all",
    data: {
        name: undefined,
        appid: undefined,
        appsecret: undefined,
        verify: undefined
    },
    mounted: function () {
        this.initMenu();
        this.initData();
        this.initFormValidate();
    },
    computed: {},
    methods: {
        initData: function () {
            authAjax({
                url: "/bms/offAcc/",
                type:'GET',
                success:function(data) {
                    vm.name = data.name;
                    vm.appid = data.appid;
                    vm.appsecret = data.appsecret;
                    vm.verify = data.verify === null ? null : ("MP_verify_"+ data.verify);
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
                        required: true,
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
                        required: "请输入微信公众号安全域名认证文件名",
                        wechatVerify: "请输入正确的安全域名认证文件名"
                    }
                },
                submitHandler: function(form) {
                    authAjax({
                        type : "PUT",  //提交方式
                        url : BASE_PATH + "/bms/offAcc/",
                        data :{
                            username : $('#username').val(),
                            password : $.md5($('#password').val())
                        },
                        dataType :"json",
                        success : function(result) {//返回数据根据结果进行相应的处理
                            if(result.result === "success") {
                                Cookies.set(TOKEN_COOKIE_KEY, result.token, {expires: 1, path: '/'});
                                window.location.href = "/static/";
                            } else {
                                var tips = "<div class=\"alert alert-warning\"><a href=\"#\" class=\"close\" data-dismiss=\"alert\">&times;</a><strong>提示！</strong>用户名或密码错误</div>";
                                $('#tips').html(tips);
                            }
                        }
                    });
                    return false;
                }
            });
        }
    }
});