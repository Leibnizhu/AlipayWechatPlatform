(function ($w) {
    if (typeof $w.$Fans == 'undefined') {
        var temp = {
            /**
             * 私有数据域
             */
            data: {

            },
            /**
             * 初始化的方法，作为闭包的返回值
             */
            init: {
                /**
                 * 调用全部初始化方法
                 */
                initAll: function () {
                    temp.init.initData();
                    temp.init.initEvent();
                },
                /**
                 * 初始化页面数据,内容尽量简洁突出逻辑，具体的方法实现放在Func中
                 */
                initData: function () {
                    //菜单高亮和展开
                    $("#leftmenu a").removeClass("active");
                    $("#publicAccount").css("display","block");
                    $("#fansList").addClass("active");
                },
                /**
                 * 注册各种点击事件,内容尽量简洁突出逻辑，具体的方法实现放在Func中
                 */
                initEvent: function() {
                    $('#sysBt').on("click", function() {
                        temp.func.doSync();
                    });
                }
            },
            func: {
                doSync :function()  {
            swal({
                    title: "确认同步？",
                    text: "确定同步?\n每次最多同步10000名粉丝的信息，耗时较长，请耐心等待，不要重复点击",
                    type: "warning",
                    showCancelButton: true,
                    confirmButtonColor: "#2AB764",
                    confirmButtonText: "同步",
                    cancelButtonText: "取消"
                },
                function(isConfirm){
                    if (isConfirm)
                        $.ajax({
                            url: '[[@{/wxapi/syncAccountFansList}]]',
                            type: 'post', //数据发送方式
                            data: '', //发送的数据
                            dataType: 'json', //接受数据格式
                            beforeSend: function () {//加载数据时显示
                            },
                            error: function () { //失败 外界异常引起
                                swal("错误", "服务器发生错误，请联系管理员", "error");
                            },
                            success: function (data) { //成功
                                var msg = JSON.parse(data);
                                if(msg["code"]=="success"){
                                    swal({title:"同步菜单成功", text:msg["msg"], type:"success"}, function(){window.location.href="[[@{/fans/list}]]";});
                                } else{
                                    swal("同步菜单失败", msg["msg"], "error");
                                }
                            }
                        });
                });
            }
            }
        };
        //向外开放方法
        $w.$Fans = {
            init: temp.init.initAll
        }
    }
})(window);

$(document).ready(function(){
    $Fans.init();
    adjustFooter();
});