$().ready(function() {
    // 在键盘按下并释放及提交后验证提交表单
    $("#signUpForm").validate({
        rules: {
            username: {
                required: true,
                minlength: 2
            },
            password: {
                required: true,
                minlength: 5
            }
        },
        messages: {
            username: {
                required: "请输入用户名",
                minlength: "用户名必需由两个字母组成"
            },
            password: {
                required: "请输入密码",
                minlength: "密码长度不能小于 5 个字母"
            }
        },
        submitHandler: function() {
            $.ajax({
                type : "post",  //提交方式
                url : BASE_PATH + "/bms/login/",
                data :{
                    username : $('#username').val(),
                    password : $.md5($('#password').val())
                },
                dataType :"json",
                success : function(result) {//返回数据根据结果进行相应的处理
                    if(result.result === "success") {
                        Cookies.set(TOKEN_COOKIE_KEY, result.token, {expires: 1, path: '/'});
                        localStorage.setItem("role", result.role);
                        localStorage.setItem("name", result.name);
                        localStorage.setItem("id", result.id);
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
});
function keyLogin(){
    if (event.keyCode==13){ //回车键的键值为13
        $("#signUpForm").submit(); //调用登录按钮的登录事件
    }
}