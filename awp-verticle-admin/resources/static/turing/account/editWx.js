var vm = new Vue({

});
//重构的代码
(function($w) {
    // 判断AdminReturn是否被定义过，未定义时，则创建一个对象，AdminReturn是该js的文件名
    if(typeof $w.$AdminWechat == 'undefined') { // AdminReturn未被定义
        /**
         * 定义一个对象，对象中包含了数据域、初始化方法和用户自定义方法；
         */
        var temp = {
            /**
             * 私有数据域，即处理页面过程中需要用到的变量；
             */
            data: {
                userId: $("#userOperate").val(),
                data : "",//回显数据
                commitType : "",//提交类型
                operate : "",//操作结果
                formName:$("#wechatInfo #name"),//获得公众号名称输入框
                formAccount:$("#wechatInfo #account"),//获得公众号ID输入框
                formWxAccount:$("#wechatInfo #wxAccount"),//获得微信号输入框
                formToken:$("#wechatInfo #token"),//获得token输入框
                formUrl:$("#wechatInfo #url"),//url输入框
                formAppid:$("#wechatInfo #appid"),//appid输入框
                formAppsecret:$("#wechatInfo #appsecret"),//应用密钥输入框
                formFileTxt:$("#wechatInfo #fileTxt"),//文件输入框
                formId:$("#wechatInfo #id"),   //获得id 输入框
                formCommit:$("#wechatInfo #commit"),  //获得提交框
                turingEnterpriseId : $.cookie('TURING_ENTERPRISE_ID') // 从cookie中获取上一次选中的（其他页面或本页面）子账号Id
            },
            /**
             * 初始化的方法，包括数据初始化和事件注册，作为闭包的返回值；
             */
            init: {
                /**
                 * 总初始化方法，该方法用于封装调用数据初始化和事件注册的方法；
                 */
                initAll: function() {
                    temp.init.initData(); // 调用数据初始化的方法
                    temp.init.initEvent(); // 调用事件注册的方法
                    temp.init.origin();//其他事件初始化
                },
                /**
                 * 该方法用于初始化页面数据，如果其中用到其他方法的话，其他方法的具体实现放在func中；
                 */
                initData: function() {
                    //主账号操作状态下，设置下拉框默认选中的值
                    var role = $("#roleOperate").val();  //获得操作本次公众号的角色
                    /**
                     * 用于操作主账号操作子账号公众号配置信息
                     * 主账号下无子账号，则显示无子账号不能创建活动，有子账号，则进行回显，并给下拉框子账号添加选中事件，
                     * 选中的更换的子账号后，重新加载该子账号公众号配置信息进行显示
                     * create by tanjiansheng 2017-08-17
                     */
                    if(role == 1){  //操作本次公众号配置的是主账号。
                        var isChild = $("#isExistChild").val();  //当isChild==0，则说明该主账号下无子账号
                        if("0" == isChild) {//判断主账号是否是没有子账号的，没有子账号则不进行数据加载
                            $('#enterpriseName').append("<option value='-1'>无用户,不能创建活动</option>"); // 添加无用户选项
                            $('#submits').attr({"disabled":"true"});//获取提交按钮，并且禁用提交按钮
                        }else { //如果有子账号，则进行子账号数据加载
                            $("#enterpriseName").val(temp.data.userId);//下拉框选中userId的选项
                        }
                    }else if(role == 0){ //操作本次公众号配置的是超级管理员
                        $("#accountTitle").html("选择帐号：");//更改为操作账号
                        //加入当前账号:修改 2017-08-21 超级管理员没有公众号配置，只是操作所有子账号的公众号配置
                        //$('#enterpriseName').prepend("<option value='"+ returnData.userId +"' selected>当前帐号</option>"); // 添加超级管理员选项
                    }
                    if(role ==1 || role ==0){//超级管理员和主账号需要加载cookies
                        /**
                         * 子账户下拉框初始化选择；
                         * 情况较为复杂，先分为cookie是否有值（即之前有没有选过选项）；
                         * 然后，每种情况分为是否有子账号来进行处理；
                         */
                        // 通过cookie判断之前是否有选择过选项（有选择过则会把选项的value记录在cookie中）
                        if (temp.data.turingEnterpriseId != null) { // 之前有选择过选项
                            // 判断当前用户是否有可展示的子账号
                            if ($("#enterpriseName option:selected").val() != null) { // 有可显示的子账号
                                var tmpId = $("#enterpriseName").val(); // 获取默认选择的选项的value
                                $("#enterpriseName").val(temp.data.turingEnterpriseId); // 使下拉框选择保存在cookie中的子账号，没该选项时为null
                                // 判断是否有上次选择的选项
                                if ($("#enterpriseName").val() == null) { // 没有上次选择的选项，表示更换了账号，子账号不同（超级管理员除外）
                                    $("#enterpriseName").val(tmpId); // 设置下拉框为默认选项
                                }else{
                                    temp.func.loadingDataByUserId(temp.data.turingEnterpriseId);
                                }
                            }
                        }
                    }
                },
                /**
                 * 该方法用于注册点击事件，如果其中用到其他方法的话，其他方法的具体实现放在func中；
                 */
                initEvent: function () {
                    //添加监听事件
                    $("#enterpriseName").on("change", function () {//为下拉框添加点击事件
                        $.cookie('TURING_ENTERPRISE_ID', $('#enterpriseName').val(), {path:'/'}); // 下拉框改变选项时，将选项的value，即子账号的Id存到cookie中
                        //传入选中的值，然后加载相应的公众号配置
                        temp.func.loadingDataByUserId($("#enterpriseName").val());
                    })
                },
                /**
                 * 此方法是重构前需要加载的事件或者验证方式
                 */
                origin : function () {
                    temp.func.originEventOne();  //初始化重构事件一
                    temp.func.originEventTwo();  //初始化重构事件二
                }
            },
            /**
             * 自定义方法区，将供调用的方法定义在该区域；
             */
            func: {
                /**
                 * 下拉框事件，触发重新加载显示数据,根据传入的userId，寻找数据，重新填充进去表单
                 * create by tanjiansheng 2017-08-17
                 */
                loadingDataByUserId : function (userIds) {
                    var urls = $basePath + "wechatNumber/loadingDataByUserId?userId="+userIds;
                    $.ajax({
                        url: urls,
                        type:'post',
                        dataType: "json",
                        error:function(){
                            alert("加载失败，连接不成功。");
                        },
                        success:function(data){
                            temp.data.data = data.data;
                            temp.data.commitType = data.commitType;
                            temp.data.operate = data.operate;
                            if("success" == data.operate){
                                temp.func.writingDataInForm();//调用方法重新写入数据
                            }else { //"error" == result.operate
                                alert("userId错误，数据加载失败，请联系管理员");
                            }
                        }
                    });
                },
                /**
                 * 当下拉框事件发生时，接收回传数据，并且根据数据重写表单
                 */
                writingDataInForm : function () {
                    temp.data.formCommit.val(temp.data.commitType);//重新设置提交类型
                    temp.data.formName.val(temp.data.data.name);//重新设置公众号名称
                    temp.data.formAccount.val(temp.data.data.account);//重新设置公众号ID
                    temp.data.formWxAccount.val(temp.data.data.wxAccount);//重新设置微信号
                    temp.data.formToken.val(temp.data.data.token);//重新设置token
                    temp.data.formUrl.val(temp.data.data.url);//重新设置url
                    temp.data.formAppid.val(temp.data.data.appid);//重新设置appid
                    temp.data.formAppsecret.val(temp.data.data.appsecret);//重新设置appsecret
                    temp.data.formId.val(temp.data.data.id);//重新设置id
                },
                /**
                 *  事件抽取一
                 */
                originEventOne : function () {
                    $("#wechatInfo").validate({
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

                        },
                        messages: {
                            name: {
                                required: "请填写公众号名称"
                            },
                            appid: {
                                required: "请填写appId"
                            },
                            appsecret: {
                                required: "请填写appSecret"
                            }

                        }
                    });
                    $("#fileTxt").click(function() {
                        $("#inputFile").hide();
                        $("#inputFileError").hide();
                        $("#fileName").hide();
                        flag = true;
                    });
                    $("#userAnalysis").removeClass("active");
                    $("#publicAccount").addClass("active");
                    $("#publicAccount li:first").find("a").addClass("active");
                    $("#publicAccount").show();
                    if($(".variable-src").val()=="true"){
                        sweetAlert("提示", "第一次使用请先进行公众号配置！", "warning");
                    }
                    if ($('#commit').val() ==  "update"){
                        $('#txt').css('display','none');
                    }
                },
                /**
                 *  事件抽取二
                 */
                originEventTwo : function () {

                }

            }
        };
        // 向外暴露总初始化方法
        $w.$AdminWechat = {
            init: temp.init.initAll // 总初始化方法
        };
    }
})(window);
$(document).ready(function(){
    $AdminWechat.init();
});
$.validator.setDefaults({
    submitHandler: function() {
        // console.log(flag);
        if($('#fileTxt').val() == "" && $('#commit').val() ==  "insert"){
            $("#inputFile").show();
            flag = false;
        }
        if (flag){
            var urls = $basePath + "wechatNumber/insert";
            if($("#roleOperate").val() == "1"){ //主账号操作，则增加userId标识
                urls += "?id=" + $("#userOperate").val();
                console.log(urls);
            }
            $.ajax({
                url: urls,
                type:'post',
                async:false,
                data: new FormData($('#wechatInfo')[0]),
                processData : false,
                contentType : false,
                success:function(result){
                    console.log(result);
                    if(result=="SUCCESS"){
                        swal({
                            title: "",
                            text: "保存成功",
                            // text: "保存成功，请登录公众号，在服务器配置页面填入URL和token",
                            type: "success",
                            confirmButtonText: "确认"
                        },function(){
                            window.location.href = $basePath + "camAnalysis/show";
                        });

                    }else{
                        sweetAlert("","文件名不正确","error");
                    }
                }
            });
        }

    }

});