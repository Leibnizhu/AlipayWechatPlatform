var curEid = -1;
var vm = new Vue({
    el: "#all",
    data: {
        eid: parseInt(localStorage.id),
        selectedEid: parseInt(localStorage.id), //下拉框选择的id
        role: parseInt(localStorage.role),
        curName: localStorage.name,
        email: localStorage.email,
        password: "",
        newPassword: "",
        rePassword: "",
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
            if (oldVal === -1) {
                return;
            }
            vm.loadData(newVal);
        }
    },
    methods: {
        initMenu: function () {
            $("#systemManager").addClass("active").show();
            $("#pswdMenuLi").find("a").addClass("active");
        },
        initAll: function () {
            if (this.role === 0) {
                curEid = this.eid;
                this.processAdmin();
            }
        },
        initFormValidate: function () {
            $("#accPswd").validate({
                rules: {
                    email: {
                        required: true
                    },
                    password: {
                        required: true
                    },
                    rePassword: {
                        equalTo: "#newPassword"
                    }
                },
                messages: {
                    email: {
                        required: "请输入登录邮箱"
                    },
                    password: {
                        required: "请输入密码"
                    },
                    rePassword: {
                        equalTo: "请保证密码一致"
                    }
                },
                submitHandler: function () {
                    authAjax({
                        type: "PUT",  //提交方式
                        url: "/bms/offAcc/pswd",
                        data: {
                            id: vm.selectedEid,
                            email: vm.email,
                            oldPassword: vm.password.trim() === "" ? null : $.md5(vm.password),
                            newPassword: vm.newPassword.trim() === "" ? null : $.md5(vm.newPassword),
                            rePassword: vm.rePassword.trim() === "" ? null : $.md5(vm.rePassword)
                        },
                        dataType: "text",
                        success: function (result) {//返回数据根据结果进行相应的处理
                            if (result === "success") {
                                localStorage.email = vm.email;
                                vm.password = vm.newPassword = vm.rePassword = "";
                                swal("成功", "更新邮箱/密码成功", "success");
                            } else if (result === "errPswd") {
                                vm.password = vm.newPassword = vm.rePassword = "";
                                swal("错误", "旧密码错误", "error");
                            } else {
                                swal("错误", "未能更新配置，数据库处理出错", "error");
                            }
                        },
                        error: function (e) {
                            if (e.respnoseText === "EMPTY_REPEAT_PSWD") {
                                vm.password = vm.newPassword = vm.rePassword = "";
                                swal("错误", "必须重复输入新密码", "error");
                            } else if (e.respnoseText === "PSWD_NOT_EQUAL") {
                                vm.password = vm.newPassword = vm.rePassword = "";
                                swal("错误", "两次输入新密码不一致", "error");
                            } else {
                                swal("错误", "服务器内部错误，请联系管理员", "error");
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
        loadData: function (eid) {
            for (var i = 0; i < this.accList.length; i++) {
                if (this.accList[i].id == eid) {
                    this.email = this.accList[i].email;
                    this.password = "";
                    this.newPassword = "";
                    this.rePassword = "";
                    break;
                }
            }
        }
    }
});