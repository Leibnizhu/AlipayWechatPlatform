/**
 * 用户列表、新建用户和编辑用户整合在同一个页面中，即客户管理中心页面；
 * 该js的主要功能是对客户管理中心页面的控制，包括数据列表的数据填充、编辑数据填充、表单提交等处理；
 */
(function(doc){
    /**
     * 定义对象，用于定义数据和方法处理等；
     */
    var um = {
        /**
         * 私有的数据域；
         */
        data: {
            isSelect: 0 ,//增加一个动态变量，选择主账号的时候标记此变量 0表示默认选择显示全部主账号 1表示选择了主账号
            basePath: window.location.href, // 当前页面url,用于下面刷新页面时调用
            allUser: [], // 用户数组，用于存放后台传来的用户数据
            userId: "", // 指定处理的用户ID，比如编辑时的用户ID
            turingEnterpriseId : $.cookie('TURING_ENTERPRISE_ID'), // 从cookie中获取上一次选中的（其他页面或本页面）子账号Id
            columnsStyle: [ // 用户列表每一列的属性设置风格
                {
                    "data": "name", // 与传来的数据字段名对应，用户名称
                    "orderable": false // 设置不能排序
                },
                {"data": "email"}, // 与传来的数据字段名对应，邮箱
                {"data": "deposit"}, // 与传来的数据字段名对应，保证金
                {"data": "createDateStr"}, // 与传来的数据字段名对应，创建日期
                {"data": "parentName"}, // 与传来的数据字段名对应，创建者
                { // 状态列
                    "orderable": false, // 设置不能排序
                    "data": null,
                    "createdCell": function (td, cellData, rowData, row, col) { // 对数据进行处理，这里将单元格内容转换为状态数值对应的文字
                        // 判断该行用户的状态数值，1表示启用，0表示暂停
                        if (cellData.status == 1) {
                            $(td).html("已启用"); // 显示为“已启用”文字
                        } else {
                            $(td).html("已暂停"); // 显示为“已暂停”文字
                        }
                    }
                },
                { // 操作列
                    "orderable": false, // 设置不能排序
                    "data": null,
                    "createdCell": function (td, cellData, rowData, row, col) { // 对数据进行处理，这里将单元格内容转换为按钮
                        var resultHtml = ''; // 存放单元格里面要显示的html
                        // 判断当前行用户的状态，如果是启用，则显示暂停按钮，否则如果是暂停，则显示启用按钮
                        if(cellData.status == 1){
                            // 暂停按钮html
                            resultHtml = "<div style='float: left'><a type='button' class='btn btn-rounded btn-default btn-icon mouse-tips' data-tips='暂停'><i class='fa fa-pause' data-toggle='tooltip' data-placement='bottom'></i></a>&nbsp;";
                        }else{
                            // 启用按钮html
                            resultHtml = "<div style='float: left'><a type='button' class='btn btn-rounded btn-default btn-icon mouse-tips' data-tips='启用'><i class='fa fa-play' data-toggle='tooltip' data-placement='bottom'></i></a>&nbsp;";
                        }
                        // 超级管理员登录时，未进行piwik站点注册的商家用户显示发送注册按钮
                        if($('#role').val() == 0 && cellData.role == 2 && cellData.siteid == 0) {
                            // 添加编辑按钮和发送按钮html
                            resultHtml += "<a type='button' class='btn btn-rounded btn-default btn-icon mouse-tips' data-tips='编辑'><i class='fa fa-pencil' data-toggle='tooltip' data-placement='bottom'></i></a>&nbsp;"
                                + "<a type='button' class='btn btn-rounded btn-default btn-icon mouse-tips' data-tips='未注册商家发送piwik站点注册请求'><i class='fa fa-send' data-toggle='tooltip' data-placement='bottom'></i></a></div>";
                            $(td).html(resultHtml).attr("userid", cellData.id).attr("username", cellData.name).attr("status", cellData.status);// 单元格(即td标签)添加用ID和用户名称,方便下面获取
                            $(td).find("a").eq(2).on("click", um.userFunc.sendRequest); // 发送请求按钮添加发送进行piwik站点注册的点击事件
                        } else {
                            // 添加编辑按钮html
                            resultHtml += "<a type='button' class='btn btn-rounded btn-default btn-icon mouse-tips' data-tips='编辑'><i class='fa fa-pencil' data-toggle='tooltip' data-placement='bottom'></i></a></div>";
                            $(td).html(resultHtml).attr("userid", cellData.id).attr("username", cellData.name).attr("status", cellData.status);// 单元格(即td标签)添加用ID和用户名称,方便下面获取
                        }
                        $(td).find("a").eq(0).on("click", um.userFunc.changeStatus); // 状态按钮添加改变状态点击事件
                        $(td).find("a").eq(1).on("click", um.userFunc.displayUser); // 编辑按钮添加显示编辑模态框点击事件
                        $(td).parent().addClass("text-center"); // 添加样式
                        mouseoverANDout($("#EnterpriseUseList")); // 添加按钮提示
                    }
                }
            ],
            languageStyle: { // 表格周围样式风格
                // 左上角的分页大小显示
                lengthMenu: '<div style="float:left;padding:6px 6px 0 0;">展示</div>' + '<div style="float:left;"><select class="form-control input-xsmall">' + '<option value="10">10</option>' + '<option value="20">20</option>' + '<option value="50">50</option>' + '<option value="100">100</option>' + '</select></div>' + '<div style="float:left;padding:6px 0 0 6px;">条记录</div>',
                search: '搜索：', // 右上角的搜索文本，可以写html标签
                paginate: { // 分页的样式内容
                    previous: "上一页",
                    next: "下一页",
                    first: "第一页",
                    last: "最后"
                },
                zeroRecords: "没有内容", // table tbody内容为空时，tbody的内容
                // 下面三者构成了总体的左下角的内容
                info: "总共 _PAGES_ 页 _MAX_ 条，显示第 _START_ 到 _END_ 条", // 左下角的信息显示，大写的词为关键字，会被替换掉
                infoEmpty: "0条记录", // 筛选为空时左下角的显示
                infoFiltered: "" // 筛选之后的左下角筛选提示
            }
        },
        /**
         * 初始化的方法（包括数据和事件的初始化），作为闭包的返回值；
         */
        init: {
            /**
             * 初始化页面数据，主要是对调用方法对菜单、数据列表和模态框的行业下拉框进行初始化；
             */
            initData: function () {
                if($('#role').val() == 1){ //主账号操作数据
                    $("#titles").html("子账号列表");//并将标题改为子账号账号列表
                    um.userFunc.getTable($('#EnterpriseUseList'), um.data.columnsStyle, "user/initUseData"); // 初始化数据列表
                }else{ //超级管理员操作数据
                    um.userFunc.initAllAccount();//初始化所有主账号
                }

                um.userFunc.hightLightMenu(); // 初始化菜单
                um.userFunc.addOption(); // 初始化行业下拉框
                return this;
            },
            /**
             * 注册各种点击或状态改变的事件；
             */
            initEvent: function () {
                // 页脚自适应
                $('#EnterpriseUseList').on('draw.dt',function() {
                    adjustFooter();
                });

                // 超级管理员时，需要对角色选择下拉框的状态进行监测，显示对应的数据
                if($('#role').val() == 0) { // 0代表当前角色是超级管理员
                    // 下拉框状态改变监测，改变时才执行function中的内容
                    $("#roleSel").change(function(){
                        //var chooseRole = $(this).children('option:selected').val(); // 获取被选中的值
                        //um.userFunc.getTable($('#EnterpriseUseList'), um.data.columnsStyle, "user/getroledata?role=" + chooseRole); // 调用getTable方法向后台请求数据，并展示在列表上
                        //以上代码为20170818前的代码，因需求原因，代码变更 by tanjiansheng 20170818
                        var chooseRole = $(this).children('option:selected').val(); // 获取被选中的值,显示被选择主账号中所有的子账号
                        if(chooseRole == 1){ //表示选择了显示全部主账号
                            um.data.isSelect = 0 ;   //标记为选择了显示所有主账号
                            $("#titles").html("主账号列表");//并将标题改为所有主账号列表
                            $.cookie('TURING_ENTERPRISE_ID', null, {path:'/'});//移除COOKIE
                        }else{//选择了具体的某个子账号
                            um.data.isSelect = 1 ; // 标记为选择了其中的一个主账号，显示其子账号
                            $("#titles").html("子账号列表");//并将标题改为子账号账号列表
                            $.cookie('TURING_ENTERPRISE_ID', $('#roleSel').val(), {path:'/'}); // 下拉框改变选项时，将选项的value，即子账号的Id存到cookie中
                        }
                        um.userFunc.getTable($('#EnterpriseUseList'), um.data.columnsStyle, "user/initUseData"); // 初始化数据列表
                    });
                }

                // 新建和编辑模态框的角色下拉框状态监测，改变时才执行function中的内容
                $("#roleOpt").change(function(){
                    $('#depDiv').removeClass("has-error"); // 去除保证金输入框变红样式，防止选择商家进行验证失败后选择代理商出现的样式问题
                    $('#depDiv').removeClass("has-success"); // 去除保证金输入框变绿样式，防止选择商家进行验证成功后选择代理商出现的样式问题
                    $('#deposit-error').css("display", "none"); // 去除保证金验证失败的提示信息，防止选择商家进行验证失败后选择代理商出现的样式问题

                    // 获取下拉框被选中的值，并做不同的处理
                    if($(this).children('option:selected').val() == 1) { // 1表示代理商
                        $("#deposit").attr("disabled", "disabled"); // 设置保证金输入框不可编辑
                        $('#depFlag').html('&nbsp;&nbsp;'); // 将保证金的必填标识红色星号替换成两个空格
                        $("#deposit").rules("remove"); // 去除保证金输入框的验证规则和提示信息
                    } else { // 2表示商家
                        $("#deposit").attr("disabled", false); // 设置保证金输入框可编辑
                        $('#depFlag').html('*'); // 将保证金的两个空格替换成必填标识红色星号
                        $("#deposit").rules("remove"); // 去除保证金输入框的验证规则和提示信息
                        $("#deposit").rules("add",{ // 为保证金输入框添加新的验证规则和提示信息
                            required: true, // 必填
                            positiveInteger: true, // 正整数验证
                            messages:{ // 提示信息
                                required: "请输入保证金", // 无输入时的提示信息
                                positiveInteger: "请输入正整数" // 输入非正整数时的提示信息
                            }
                        });
                    }
                });

                // 注册新增用户按钮点击事件
                $('#btn_add_user').on('click',function(){
                    um.userFunc.displayUser("add"); // 调用方法显示新建模态框
                });

                /**
                 * 以下内容为新建和编辑用户提交代码;
                 */
                var validate = um.userFunc.validate(); // 获取表单验证对象
                var newForm = $("#enterpriseForm"); // 获取表单对象
                // 表单提交事件
                newForm.submit(function(){
                    // 判断表单验证是否成功
                    if(validate.valid()){ // 成功
                        um.userFunc.submitEditUser(); // 提交数据到后台进行处理
                    }
                    // 表单验证失败，返回false阻止正常的表单提交
                    return false;
                });
            }
        },
        /**
         * 其他用户自定义方法定义在userFunc中；
         */
        userFunc: {
            /**
             * 这是初始化时被调用的，用于菜单高亮选择，让系统管理中的客户管理中心菜单被选中，并且高亮。
             */
            hightLightMenu: function () {
                $("#userAnalysis").removeClass("active"); // 取消所有菜单的高亮状态
                $("#systemManager").css("display", "block"); // 展开系统管理菜单
                $("#enterpriseListMenu").addClass("active"); // 客户管理中心高亮显示
            },
            /**
             * 该方法用于从后台获取数据并处理表格内容，根据用户需求会被多次调用，比如超级管理员选择角色后重新加载数据会调用该方法；
             * useList：根据ID获取的表格对象；
             * aoColumnsData： 表格的基本数据，如条数，开始等；
             * url：获取数据的请求；
             * 新增功能：初始化时加载所有的超级管理员下的主账号，若是选择了某个具体的主账号，显示其所有子账号
             * 这里需要使用前面定义的一个标示符：um.data.isSelect 默认为0，则就是加载所有主账号， 1为加载子账号数据
             */
            getTable: function (useList, aoColumnsData, url) {
                if(um.data.isSelect == 1){ //选中某个具体的主账号，而需要加载其所有的子账号，并且进行分页
                    url += "?userIds=" +  $("#roleSel").children('option:selected').val();
                    console.log(url);
                }
                // 表格初始化，填充数据
                useList.DataTable({
                    destroy: true, // 可销毁
                    processing: true,
                    pagingType: "full_numbers", // 分页样式的类型
                    serverSide: true, // 这个用来指明是通过服务端来取数据
                    paging: true, // 可分页
                    ordering: false, // 整体不排序
                    searching: false, // 无搜索
                    order: [[1, 'asc']], // 根据某一列进行排序，因为设置了整体不排序，所以暂时无效
                    // ajax从后台获取数据
                    ajax: {
                        url: $basePath + url, // 获取数据url
                        type: "post", // POST方式
                        // 获取数据后处理方法
                        dataSrc: function (data) {
                            um.data.allUser = data.data; // 将数据赋值给allUser，其他地方要用到
                            return data.data; // 返回数据
                        }
                    },
                    columns: aoColumnsData, // 基本信息
                    lengthChange: true, // 数据展示长度可变
                    //pageLength: "10",
                    language: um.data.languageStyle // 表格周围样式
                });
            },
            /**
             * 初始化下拉菜单，首次进入时，加载全部主账号，第一个元素为全部主账号，其次加入分页中显示的各个主账号名称
             * 如果是超级管理员进入，则表明此方法一定要执行加载该超级管理员当前分页下的所有主账号，选择加载其他分页时，重新绘制
             * 如果是主账号访问此页面，则该方式只是访问到了，并不执行里面的程序
             * @param #role 已经在页面中加载声明，标记此账号的类型
             * create by tanjiansheng 2017-08-18
             */
            initAllAccount : function () {
                if( $('#role').val() == 0 && um.data.isSelect == 0){    //如果是超级管理员操作并且是显示所有主账号，则进入此步骤
                    $('#roleSel').html("<option value='1'>所有主帐号</option>"); //每次加载后，重新绘制下拉框
                    //获取所有该超级管理员下面的所有主账号
                    $.ajax({
                        url: $basePath + 'user/getAllEnterpriseByRole', // 请求url
                        type: 'post', //数据发送方式
                        dataType: 'json', // 接受数据格式
                        error: function () { // 失败 外界异常引起
                            um.userFunc.returnAlert("错误", "服务器发生错误，请联系管理员");
                        },
                        success: function (data) { // 请求成功
                            data.data.forEach(function (user) {
                                $('#roleSel').append("<option value='"+ user.id +"'>"+ user.name +"</option>"); // 添加无用户选项
                            })
                            //根据cookies初始化值
                            /**
                             * 子账户下拉框初始化选择；
                             * 情况较为复杂，先分为cookie是否有值（即之前有没有选过选项）；
                             * 然后，每种情况分为是否有子账号来进行处理；
                             */
                            // 通过cookie判断之前是否有选择过选项（有选择过则会把选项的value记录在cookie中）
                            if (um.data.turingEnterpriseId != null) { // 之前有选择过选项
                                // 判断当前用户是否有可展示的子账号
                                if ($("#roleSel option:selected").val() != null) { // 有可显示的子账号
                                    var tmpId = $("#roleSel").val(); // 获取默认选择的选项的value
                                    $("#roleSel").val(um.data.turingEnterpriseId); // 使下拉框选择保存在cookie中的子账号，没该选项时为null
                                    // 判断是否有上次选择的选项
                                    if ($("#roleSel").val() == null) { // 没有上次选择的选项，表示更换了账号，子账号不同（超级管理员除外）
                                        $("#roleSel").val(tmpId); // 设置下拉框为默认选项
                                    }else{
                                        um.data.isSelect = 1 ;
                                        um.data.userId = um.data.turingEnterpriseId
                                        $("#titles").html("子账号列表");//并将标题改为子账号账号列表
                                    }
                                }
                            }
                            um.userFunc.getTable($('#EnterpriseUseList'), um.data.columnsStyle, "user/initUseData"); // 初始化数据列表
                        }
                    })

                }
            },
            /**
             * 修改用户状态处理方法；
             * 用户点击状态改变按钮后，系统会请求后台修改指定用户的状态值，成功返回后，修改页面的文字和样式；
             */
            changeStatus: function () {
                var obj  = $(this).parent().parent(); // 获取td结点对象，用于获取要修改状态的用户的ID和当前状态值
                var id = obj.attr("userid"); // 获取要修改状态的用户的ID
                var sta = obj.attr("status"); // 获取要修改状态的用户的当前状态值

                // 判断要修改状态的用户当前是属于什么状态，1表示已启用，0表示已暂停
                if(sta == 1) {
                    sta = 0; // 用户改变后的状态值，用于传回后台时，后台不必再转换
                } else {
                    sta = 1; // 用户改变后的状态值，用于传回后台时，后台不必再转换
                }

                // ajax请求
                $.ajax({
                    url: './changestatus', // 请求url
                    type: 'post', //数据发送方式
                    data: { // 发送的数据
                        'id': id, // 要修改状态的用户的ID
                        'status': sta // 用户改变后的状态值
                    },
                    dataType: 'json', // 接受数据格式
                    error: function () { // 失败 外界异常引起
                        um.userFunc.returnAlert("错误", "服务器发生错误，请联系管理员");
                    },
                    success: function (data) { // 请求成功
                        // 判断后台返回处理结果
                        if(data == 'refuse') { // 没权限修改，一般由人工非法请求造成
                            um.userFunc.returnAlert("错误", "您没有权限修改该数据");
                        } else if(data == 'fail') { // 修改失败，一般由代码问题、数据库或网络问题造成
                            um.userFunc.returnAlert("错误", "修改状态失败");
                        } else { // 修改成功处理
                            // 判断用户修改后的状态值，若已暂停，则将按钮替换成启用按钮，否则替换成暂停按钮
                            if(sta == 0) { // 已暂停
                                obj.find('a').eq(0).attr("data-tips", "启用"); // 修改按钮提示
                                obj.find('a').eq(0).find('i').removeClass("fa-pause"); // 去掉暂停按钮
                                obj.find('a').eq(0).find('i').addClass("fa-play"); // 添加启用按钮
                                obj.prev().html("已暂停"); // 状态字段修改成已暂停
                            } else { // 已启用
                                obj.find('a').eq(0).attr("data-tips", "暂停"); // 修改按钮提示
                                obj.find('a').eq(0).find('i').removeClass("fa-play"); // 去掉启用按钮
                                obj.find('a').eq(0).find('i').addClass("fa-pause"); // 添加暂停按钮
                                obj.prev().html("已启用"); // 状态字段修改成已启用
                            }
                            obj.attr("status", sta); // 修改隐藏的状态值
                        }
                    }
                })
            },
            /**
             * 该方法用于超级管理员为商家用户发送piwik站点注册请求；
             */
            sendRequest: function() {
                var obj  = $(this).parent().parent(); // 获取td结点对象，用于获取要修改状态的用户的ID和当前状态值
                var id = obj.attr("userid"); // 获取要进行piwik站点注册的用户的ID

                // ajax请求
                $.ajax({
                    url: './piwikreg', // 请求url
                    type: 'post', //数据发送方式
                    data: { // 发送的数据
                        'id': id // 要进行piwik站点注册的用户的ID
                    },
                    dataType: 'json', // 接受数据格式
                    error: function () { // 失败 外界异常引起
                        um.userFunc.returnAlert("错误", "服务器发生错误，请联系管理员");
                    },
                    success: function (data) { // 请求成功
                        // 判断后台返回处理结果
                        if(data == 'refuse') { // 没权限修改，一般由人工非法请求造成
                            um.userFunc.returnAlert("错误", "没有权限发送请求");
                        } else if(data == 'fail') { // 修改失败，一般由代码问题、数据库或网络等问题造成
                            um.userFunc.returnAlert("错误", "piwik站点注册失败");
                        } else if(data == 'reged') { // 已注册，不会再次请求，除非人工非法操作
                            um.userFunc.returnAlert("错误", "该用户已注册，不能重复注册");
                        } else { // 修改成功处理
                            swal({
                                title: "成功", // 标题
                                text: "piwik站点注册成功", // 内容
                                timer: 1000, // 弹窗时间
                                showConfirmButton: true // 显示确定按钮
                            }, function (isConfirm) {
                                // 点击确定后隐藏发送按钮
                                if(isConfirm) {
                                    obj.find('a').eq(2).css("display", "none"); // 注册完成后将发送按钮隐藏
                                }
                            })
                        }
                    }
                })
            },
            /**
             * 该方法用于将后台的传来的行业数据进行拆分，然后添加到模态框的行业选项中；
             */
            addOption: function() {
                var html = '';
                var listStr = $('#industryList').val(); // 获取行业字典数据
                var tmp = listStr.substr(1, listStr.length - 2); // 去掉首尾的括号
                // 循环将数据中的“DictBase”去掉
                while(tmp.indexOf("DictBase") >= 0) {
                    tmp = tmp.replace("DictBase", "");
                }
                // 循环将数据中的“id=”去掉
                while(tmp.indexOf("id=") >= 0) {
                    tmp = tmp.replace("id=", "");
                }
                // 循环将数据中的“name=”去掉
                while(tmp.indexOf("name=") >= 0) {
                    tmp = tmp.replace("name=", "");
                }
                tmp = tmp.substring(1, tmp.length - 1); // 去掉首尾的括号
                var industryArr = tmp.split("}, {"); // 按“}, {”将数据切割成数值，得到行业（包括id和名称等信息）的数组，即一个元素为一个行业的所有信息组成的字符串
                // 遍历切割后的数组
                for(var i = 0; i < industryArr.length; i++) {
                    // 将某个行业的数据按“,”进行切割，得到元素分别为id、行业名称等的数组
                    var arr = industryArr[i].split(', ');
                    html = html + "<option value='" + arr[0] + "'>" + arr[1].substring(1, arr[1].length -1) + "</option>"; // 拼接行业选项
                }
                $('#industry').html(html);// 将选项添加到模特框中
            },
            /**
             * 该方法用于初始化模态框，包括模态框样式、验证规则等的清除，以及数据的初始化，然后显示出模态框；
             */
            displayUser: function(type){
                // 将所有输入框初始化为空，id为-1，表示新建页面，编辑页面时会在下面修改该值为用户对应的ID
                $("#id").val("-1"); // 用户ID
                $("#email").val(""); // 登录邮箱
                $("#mobile").val(""); // 手机
                $("#name").val(""); // 客户名称
                $('#oldPassword').val(""); // 旧密码，该输入框在编辑的时候会显示，否则隐藏
                $("#password").val(""); // 登录密码
                $("#confirmPassword").val(""); // 确认密码
                $("#deposit").val(""); // 保证金，该输入框只有在超级管理员登录并新建或编辑商家时才可显示并可编辑，否则不显示或不可编辑
                $("#enterpriseForm").validate().resetForm(); // 重置验证信息

                // 去掉验证后输入框的红色样式，防止验证后重新打开模态框时输入框的红色样式还存在的情况
                $('.has-error').each(function(){
                    $(this).removeClass("has-error");
                });
                // 去掉验证后输入框的绿色样式，防止验证后重新打开模态框时输入框的绿色样式还存在的情况
                $('.has-success').each(function() {
                    $(this).removeClass("has-success");
                });

                // 根据登录用户的角色，判断是否显示保证金输入框，超级管理员显示，代理商不显示，商家无法进入该页面，不需要考虑
                if($('#role').val() == 0) { // 0表示超级管理员
                    $('#depositDiv').css('display', false); // 显示保证金输入框
                    $("#deposit").rules("remove"); // 去掉保证金输入框的验证和提示信息
                    // 为保证金输入框添加信息的验证和提示信息
                    $("#deposit").rules("add",{
                        required: true, // 必填
                        positiveInteger: true, // 正整数
                        messages:{ // 提示信息
                            required: "请输入保证金", // 无输入时提示信息
                            positiveInteger: "请输入正整数" // 输入非正整数时提示信息
                        }
                    });
                } else { // 1表示代理商
                    $("#deposit").attr("disabled", "disabled"); // 设置保证金输入框不可编辑
                    $('#depositDiv').css('display', 'none'); // 隐藏保证金输入框
                    $("#deposit").rules("remove"); // 去掉保证金输入框的验证和提示信息
                }

                // 因为编辑时不允许修改用户的角色，在编辑是会隐藏某个选项，所以多次交换打开和关闭新建和编辑时会造成选项不正常，需要在初始化中将模态框中的所有角色选项进行显示，在下面再分情况进行显示控制；
                $('#roleOpt').children().each(function() {
                    $(this).css("display", ""); // 将角色选项进行显示
                    $(this).prop('selected', false); // 去掉角色选项被选中的状态
                });

                // 新增用户模态框处理
                if(type == 'add') {
                    $('#oldDiv').css('display', 'none'); // 隐藏旧密码输入框
                    $('#roleOpt').children().eq(0).prop('selected', 'selected'); // 默认选择第一个角色选项
                    $('#industry').children().eq(0).prop('selected', 'selected'); // 默认选择第一个行业选项
                    // 将为每个选项添加必填红色星号
                    $('.flag').each(function () {
                        $(this).html('*');
                    });

                    $("#deposit").attr("disabled", "disabled"); // 设置保证金不可编辑
                    $("#deposit").rules("remove"); // 将保证金验证信息去掉
                    $('#depFlag').html('&nbsp;&nbsp;'); // 将保证金必填红色星号去掉

                    $("#email").rules("remove"); // 去掉登录邮箱旧验证信息
                    $("#email").rules("add",{ // 添加登录邮箱新验证信息
                        required: true, // 必填
                        isEmail: true, // 邮箱格式
                        remote:{ // 自定义邮箱是否已存在验证
                            url:"./validateEmail", // 请求url
                            type: "post", // 数据发送方式
                            dataType: "json", // 接受数据格式
                            data: { // 要传递的数据
                                old: '', // 旧邮箱，新建时为空
                                email: function() { // 当前输入的邮箱
                                    return $("#email").val();
                                }
                            }
                        },
                        messages:{ // 提示信息
                            required:"请输入邮箱", // 无输入邮箱时提示信息
                            isEmail:"请输入正确格式的邮箱", // 输入错误格式邮箱时提示信息
                            remote:"邮箱已经存在" // 邮箱已经被注册时提示信息
                        }
                    });

                    $("#password").rules("remove"); // 去掉登录密码旧验证信息
                    $("#password").rules("add",{ // 添加登录密码新验证信息
                        required: true, // 必填
                        messages:{ // 提示信息
                            required:"请输入登录密码" // 无输入登录密码时提示信息
                        }
                    });

                    $("#confirmPassword").rules("remove"); // 去掉确认密码旧验证信息
                    $("#confirmPassword").rules("add",{ // 添加确认密码新验证信息
                        required: true, // 必填
                        equalTo: "#password", // 验证与登录密码输入框的内容是否一致
                        messages:{ // 提示信息
                            required: "请输入密码", // 无输入确认密码时提示信息
                            equalTo: "两次密码输入不一致" // 输入确认密码与登录密码不一致时提示信息
                        }
                    });
                    $("#oldPassword").rules("remove"); // 去掉旧密码数据框的旧验证信息
                } else{
                    // 修改用户
                    // 判断角色，如果非超级管理员，则隐藏保证金输入框，并且显示旧的密码输入框
                    if($('#role').val() != 0) { // 商家
                        $('#oldDiv').css('display', ''); // 显示旧密码输入框
                        // 隐藏保证金输入框
                        $('#depositDiv').css('display', 'inherit');
                        $('#depLab').css('display', 'none');
                        $('#depDiv').css('display', 'none');
                    }
                    // 将密码等非必填的输入框的红色星号去掉，替换成空格
                    $('.flag').each(function () {
                        $(this).html('&nbsp;&nbsp;');

                    });

                    var oldEmail = ''; // 旧邮箱保存，邮箱验证重复时会用到
                    var id = $(this).parent().parent().attr("userid"); // 获取编辑用户的ID
                    // 遍历每个用户，根据用户ID找到要编辑的用户，并进行初始化
                    for(var i = 0; i < um.data.allUser.length; i++){
                        var curUser = um.data.allUser[i];
                        // 如果相等，则表示是要编辑的用户，对编辑模态框进行数据填充
                        if(curUser.id == id){
                            oldEmail = curUser.email;
                            $("#id").val(curUser.id); // 用户ID
                            $("#email").val(curUser.email); // 用户邮箱
                            $("#name").val(curUser.name);　//　用户名称
                            $("#mobile").val(curUser.mobile); // 手机
                            // 对角色进行选择，没选中的进行隐藏，避免修改角色
                            $('#roleOpt').children().each(function() {
                                // 判断用户本来的角色，并进行原来角色选项选择，其他选项进行隐藏
                                if($(this).val() == curUser.role) {
                                    $(this).prop('selected', 'selected'); // 对用户原来的选项设置为选中
                                } else {
                                    $(this).css('display', 'none'); // 对非用户原来的选项设置为隐藏，避免修改角色
                                }
                            });
                            // 遍历编辑模态框中的所有行业选项
                            $('#industry').children().each(function() {
                                // 判断用户原来的行业，并对原来行业的选项进行选择
                                if($(this).val() == curUser.industry) {
                                    $(this).prop('selected', 'selected'); // 对用户原来的行业设置为选中
                                }
                            });

                            // 对用户原来角色进行判断，如果是商家（并且当前登录用户为超级管理员），则设置可编辑保证金，并添加验证，否则不可编辑，去除验证
                            if(curUser.role == 2) { // 商家
                                $("#deposit").val(curUser.deposit); // 保证金数据填充
                                $("#deposit").attr("disabled", false); // 保证金输入框设置为可编辑
                                $('#depFlag').html('*'); // 保证金添加必填标识红色星号
                                $("#deposit").rules("remove"); // 去掉保证金输入框旧的验证规则
                                // 判断当前登录用户的角色，如果是超级管理员，则对保证金输入框添加验证规则
                                if($('#role').val() == 0) { // 0表示超级管理员
                                    $("#deposit").rules("add",{
                                        required: true, // 必填
                                        positiveInteger: true, // 正整数验证
                                        messages:{ // 提示信息
                                            required: "请输入保证金", // 无输入时提示信息
                                            positiveInteger: "请输入正整数" // 输入非正整数时提示信息
                                        }
                                    });
                                }
                            } else { // 非商家
                                $("#deposit").attr("disabled", "disabled"); // 将保证金输入框设置为不可编辑
                                $("#deposit").rules("remove"); // 去掉保证金输入框验证规则
                                $('#depFlag').html('&nbsp;&nbsp;'); // 将保证金必填标识替换成空格
                            }
                        }
                    }

                    // 添加邮箱的验证
                    $("#email").rules("remove"); // 去掉旧的邮箱验证规则
                    $("#email").rules("add",{ // 添加新的邮箱验证规则
                        required: true, // 必填
                        isEmail: true, // 邮箱格式验证
                        remote:{ // 邮箱是否已存在验证
                            url:"./validateEmail", //后台邮箱是否已存在验证url
                            type: "post", //数据发送方式
                            dataType: "json", //接受数据格式
                            data: { //要传递的数据
                                old: oldEmail, // 旧的邮箱
                                email: function() { // 用户填的新邮箱
                                    return $("#email").val();
                                }
                            }
                        },
                        messages:{ // 提示信息
                            required:"请输入邮箱", // 无输入邮箱时提示信息
                            isEmail:"请输入正确格式的邮箱", // 输入邮箱格式不正确时提示信息
                            remote:"邮箱已经存在" // 输入已存在邮箱时提示信息
                        }
                    });
                    $("#password").rules("remove"); // 去掉登录密码的验证规则
                    // 添加确认密码的验证
                    $("#confirmPassword").rules("remove"); // 去掉旧的确认密码验证规则
                    $("#confirmPassword").rules("add",{ // 添加新的确认密码验证规则
                        equalTo: "#password", // 是否两次密码一致验证
                        messages:{
                            equalTo: "两次密码输入不一致" // 两次密码不一致时提示信息
                        }
                    });
                    if($('#role').val() != 0) { // 商家
                        // 添加旧密码的验证
                        $("#oldPassword").rules("remove"); // 去掉旧的旧密码验证规则
                        $("#oldPassword").rules("add", { // 添加新的旧密码验证规则
                            isRequired: true, // 是否必填验证
                            remote: { // 旧密码是否正确验证
                                url: "./isright", //后台验证旧密码是否正确的url
                                type: "post", //数据发送方式
                                dataType: "json", //接受数据格式
                                data: { //要传递的数据
                                    valId: id, // 要验证旧密码的用户的ID
                                    oldPassword: function () { // 用户输入的旧密码
                                        return $("#oldPassword").val();
                                    }
                                }
                            },
                            messages: { // 提示信息
                                isRequired: "请输入旧密码", // 要求必填但是用户没填写时提示信息
                                remote: "旧密码错误" // 旧密码错误时提示信息
                            }
                        });
                    }
                }
                // 显示模态框
                $("#editUserModal").modal("show");
            },
            /**
             * 该方法用于对模态框中其他普通输入框验证信息方法，比如手机、客户名称等；
             */
            validate: function(){
                // 为表单普通输入框添加验证规则，如手机、客户名称
                validator = $("#enterpriseForm").validate({
                    ignore: "hidden",
                    rules: { // 验证规则
                        mobile: { // 手机
                            required: true, // 必填
                            isMobile: true // 手机格式验证
                        },
                        name: { // 客户名称
                            required: true, // 必填
                            maxlength: 20 // 最大长度
                        }
                    },
                    messages: { // 提示信息
                        mobile: { // 手机
                            required: '请输入手机号码', // 无输入手机时提示信息
                            isMobile: '请输入正确格式的手机号码' // 输入错误格式的手机时提示信息
                        },
                        name: { // 客户名称
                            required: "请输入客户名称", // 无输入客户名称时提示信息
                            maxlength: "客户名称长度不能超过20个字符" // 输入客户名称长度超过20时提示信息
                        }
                    },
                    /**
                     * 表单验证失败后提示信息的样式处理；
                     */
                    errorPlacement:function(error,element){
                        error.appendTo(element.parent()); // 提示信息位置，一般在输入框下面
                        element.parent().removeClass("has-success").addClass("has-error"); // 使验证错误的输入框变红色
                    },
                    /**
                     * 表单验证成功后的样式处理；
                     */
                    success:function(element){
                        element.parent().removeClass("has-error").addClass("has-success"); // 使验证成功的输入框变绿色
                    }
                });
                /**
                 * 手机号码格式验证的方法；
                 */
                jQuery.validator.addMethod('isMobile', function (value, element) {
                    var length = value.length; // 输入手机号的长度
                    var mobile = /^(1(3|8|5)\d{9})$/; // 手机号码格式正则表达式
                    return this.optional(element) || (length == 11 && mobile.test(value)); // 判断输入手机号是否格式正确
                }, '请输入正确的手机号码');
                /**
                 * 保证金验证，要求为正整数；
                 */
                jQuery.validator.addMethod("positiveInteger", function(value, element) {
                    var aint = parseInt(value); // 将输入转换为数字
                    return aint > 0 && (aint + "") == value; // 判断是否时为正整数
                }, "请输入正整数");
                /**
                 * 旧密码是否必填的验证方法，因为新建页面不需要验证旧密码，而编辑页面需要验证旧密码，所以需要该方法；
                 */
                jQuery.validator.addMethod("isRequired", function(value, element) {
                    var newPassword = $('#password').val(); // 新密码的值
                    // 判断新密码的值是否为空，空的话则旧密码不是必填的，否则旧密码是必填的
                    if(newPassword == '') { // 新密码为空
                        return true; // 旧密码不需要必填
                    } else { // 新密码为非空
                        return value != ''; // 如果旧密码为空则提示必填信息，否则不是必填
                    }
                }, "请输入旧密码");
                /**
                 * 邮箱验证的方法，因为验证插件的邮箱验证有bug，所以需要重写一个；
                 */
                jQuery.validator.addMethod("isEmail", function (value, element) {
                    var mobile =/^\w+@\w+\.\w+/; // 邮箱验证正则表达式
                    return this.optional(element) || mobile.test(value); // 判断并返回结果
                }, "请输入正确格式的邮箱");

                return validator; // 返回表单验证对象
            },
            /**
             * 该方法是AJAX提交表单方法，用于保存用户信息，并处理返回结果；
             */
            submitEditUser: function () {
                // 判断保证金输入框是否可编辑
                if($("#deposit").prop("disabled")) { // 不可编辑
                    // 判断当前用户角色，如果是超级管理员则将值置为0，否则不处理，因为在初始化模态框时就已经处理了
                    if($('#role').val() == 0) {
                        $("#deposit").val(0);
                    }
                }

                var newForm = $("#enterpriseForm"); // 获取表单对象

                // ajax提交表单数据
                $.ajax({
                    url: './saveEnterprise', // 保存或更新用户数据的url
                    type: 'post', // 数据发送方式
                    data: newForm.serialize(), // 对数据进行序列化
                    dataType: 'json', // 接受数据格式
                    beforeSend: function () { // 加载数据时显示
                    },
                    error: function () { // 失败，外界异常引起
                        um.userFunc.returnAlert("错误", "服务器发生错误，请联系管理员");
                    },
                    success: function (data) { // ajax处理成功
                        // 根据保存结果弹窗
                        if(data == 'fail') { // 保存失败
                            um.userFunc.returnAlert("错误", "保存失败");
                        } else if(data == 'refuse') { // 拒绝修改数据
                            um.userFunc.returnAlert("错误", "没有权限修改数据");
                        } else { // 保存成功
                            um.userFunc.returnAlert("成功", "保存成功");
                        }
                        $("#editUserModal").modal("hide"); // 隐藏模态框
                    }
                });
            },
            /**
             * 封装弹窗方法，调用方便，减少代码量；
             */
            returnAlert: function(tit, tex){
                swal({
                    title: tit, // 标题
                    text: tex, // 内容
                    timer: 1000, // 弹窗时间
                    showConfirmButton: true // 显示确定按钮
                }, function (isConfirm) {
                    // 点击确定后刷新页面
                    if(isConfirm) {
                        window.location.href = um.data.basePath; // 刷新页面
                    }
                })
            }
        }
    };
    doc.couponManager = um.init;

    $(doc).ready(function () {
        doc.couponManager.initData().initEvent(); // 数据和事件初始化
    });
})(document);