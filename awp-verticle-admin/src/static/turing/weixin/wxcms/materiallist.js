/**
 * Created by lfh on 2016/12/29.
 */

$(document).ready(function() {
    //菜单设置
    $("#materialManager").attr("href","../msgnews/list");
    $("#userAnalysis").removeClass("active");
    $("#publicAccount").css("display", "block");
    $("#materialManager").addClass("active");

    gettable($("#msgnewsTable"));
    $("#msgnewsTable").on('draw.dt',function() {
        adjustFooter();
    });


    //新增按钮设置
    $("#btn_add_msgnews").attr("href","./newandedit?id=");
    function gettable(tableView){
        var table = tableView.DataTable({
            processing: true,
            serverSide: true,
            paging: true,
            pagingType: "full_numbers",
            ordering:false,
            searching:false,
            ajax: {
                url:$basePath + "msgnews/getMsgNewsData",
                type:"post",
                dataSrc:function(data){
                    //console.log(data.data[0]);
                    return data.data;
                }
            },
            columns:[{"data":"inputCode"},
                {"data":"title"},
                {"orderable":      false,//不排序
                    "data":           null,//没有数据
                    "createdCell": function (td, cellData, rowData, row, col) {
                        var resultHtml = "<a type='button' class='btn btn-rounded btn-default btn-icon mouse-tips' data-tips='预览' href='../wxweb/msg/newsread?id="+cellData.id+"' ><i class='fa fa-search' data-toggle='tooltip' data-placement='bottom'></i></a>&nbsp;&nbsp;" +
                            "<a type='button' class='btn btn-rounded btn-default btn-icon mouse-tips' data-tips='删除' ><i class='fa fa-trash' data-toggle='tooltip' data-placement='bottom'></i></a>&nbsp;&nbsp;" +
                            "<a type='button' class='btn btn-rounded btn-default btn-icon mouse-tips' data-tips='编辑' ><i class='fa fa-pencil' data-toggle='tooltip' data-placement='bottom'></i></a>&nbsp;&nbsp;"
                        //填充活动操作列数据
                        $(td).html(resultHtml).attr("msgnewsid", cellData.id);
                        $(td).find("a").eq(1).on("click", deleteMsgNews);
                        $(td).find("a").eq(2).on("click", editMsgNews);
                        mouseoverANDout($("#msgnewsTable"));
                        }
                    }
                ],

            lengthChange: true,

            pagingType: "full_numbers",//分页样式的类型
            language: {
                lengthMenu: '<div style="float:left;padding:6px 6px 0 0px;">展示</div>' + '<div style="float:left;"><select class="form-control input-xsmall">' + '<option value="10">10</option>' + '<option value="20">20</option>' + '<option value="50">50</option>' + '<option value="100">100</option>' + '</select></div>' + '<div style="float:left;padding:6px 0 0 6px;">条记录</div>',//左上角的分页大小显示。
                search: '搜索：',//右上角的搜索文本，可以写html标签
                paginate: {//分页的样式内容。
                    previous: "上一页",
                    next: "下一页",
                    first: "第一页",
                    last: "最后"
                },
                processing:"请等待",
                zeroRecords: "没有内容",//table tbody内容为空时，tbody的内容。
                //下面三者构成了总体的左下角的内容。
                info: "总共 _PAGES_ 页 _MAX_ 条，显示第 _START_ 到 _END_ 条",//左下角的信息显示，大写的词为关键字。
                infoEmpty: "0条记录",//筛选为空时左下角的显示。
                infoFiltered: ""//筛选之后的左下角筛选提示，
            },

        });

    };

    function editMsgNews() {
        var id = $(this).parent().attr("msgnewsid");
        window.location="./newandedit?id="+id;
    }


    function deleteMsgNews(){
        var id = $(this).parent().attr("msgnewsid");
        var name = $(this).parent().prev().text();
        //console.log(id);
        swal({
                title: "确认删除素材？",
                text: "即将删除素材 <b>"+name+"</b>，删除后将不可恢复！",
                type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",
                confirmButtonText: "删除",
                cancelButtonText: "取消",
                closeOnConfirm: false,
                closeOnCancel: true,
                html: true
            },
            function(isConfirm){
                if (isConfirm) {
                    $.ajax({
                        url: './deleteMsgNews',
                        type: 'post', //数据发送方式
                        data: 'msgNewId=' + id, //发送的数据
                        dataType: 'json', //接受数据格式
                        beforeSend: function () {//加载数据时显示
                        },
                        error: function () { //失败 外界异常引起
                            swal("错误", "服务器发生错误，请联系管理员", "error");
                        },
                        success: function () { //成功 (在此查询范围内有数据)
                            ajaxSuccessAlertAndRefresh("成功删除素材");

                        }
                    });
                }
                function ajaxSuccessAlertAndRefresh(title, text){
                    swal({
                        title: title,
                        text: "  ",
                        type: "success",
                        confirmButtonText: "确认"
                    }, function () {
                        window.location.reload();
                    });
                }
            });
    }

});