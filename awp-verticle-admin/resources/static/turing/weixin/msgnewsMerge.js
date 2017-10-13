$('.sidepanel-open-button.dropdown-toggle.profilebox').click(function(){
    if($('#top').hasClass('open')){
        $('#top').removeClass('open');
    }else{
        $('#top').addClass('open');
    }
});

$("#leftmenu a").removeClass("active");
$("#publicAccount").css("display","block");
$("#materialManager").addClass("active");

$("#picture").fileinput({
    allowedFileExtensions : ['jpg', 'png'],
    browseClass : "btn btn-success",
    language : 'zh',
    showUpload : false
});

$('#inputCode').click(function(){
    $('#inputCodeTip').hide();
})

$('#author').click(function(){
    $('#authorTip').hide();
})

$('#title').click(function(){
    $('#titleTip').hide();
})

$('#save').click(function(){
    $('#inputCodeTip').hide();
    $('#authorTip').hide();
    $('#titleTip').hide();
    $('#descriptionTip').hide();

    var path = $('#path').val();
    var type = $('#type').val();
    var msgId = $('#msgId').val();
    var baseId = $('#baseId').val();
    var url = '';

    var inputCode = $('#inputCode').val();
    if(inputCode == ''){
        $('#inputCodeTip').show();
        $('body,html').animate({
            scrollTop : 0
        }, 300);
        return false;
    }

    var author = $('#author').val();
    if(author == ''){
        $('#authorTip').show();
        $('body,html').animate({
            scrollTop : 0
        }, 300);
        return false;
    }

    var title = $('#title').val();
    if(title == ''){
        $('#titleTip').show();
        $('body,html').animate({
            scrollTop : 0
        }, 300);
        return false;
    }

    var description = $('#description').val();
    if(description == '' || description == '<br />'){
        $('#descriptionTip').show();
        return false;
    }

    var brief = $('#brief').val();

    if(type ==1){
        url = path + "msgnews/save?inputCode=" + encodeURIComponent(inputCode) + "&author=" + encodeURIComponent(author) + "&title=" + encodeURIComponent(title) + "&brief=" + encodeURIComponent(brief);
    }else{
        // url = path + "msgnews/edit?msgId=" + msgId + "&baseId=" + baseId + "&inputCode=" + encodeURIComponent(inputCode) + "&author=" + encodeURIComponent(author) + "&title=" + encodeURIComponent(title) + "&brief=" + encodeURIComponent(brief);
        url = path + "msgnews/edit";
    }

    $.ajax({
        url: url,
        type : 'POST',
        cache : false,
        data : new FormData($('#upload')[0]),
        processData : false,
        contentType : false,
        success: function (data) {
            if(data == "uploadFail"){
                swal({
                    title: "上传封面图片失败！",
                    text: "",
                    type: "error",
                    timer: 1000,
                    showConfirmButton: false
                });
            }else if(data == "fail"){
                swal({
                    title: "保存失败！",
                    text: "",
                    type: "error",
                    timer: 1000,
                    showConfirmButton: false
                });
            }else if(data == "failData"){
                swal({
                    title: "数据错误！",
                    text: "",
                    type: "error",
                    timer: 1000,
                    showConfirmButton: false
                });
            }else if(data == "refuse"){
                swal({
                    title: "您没有权限或者该素材不存在！",
                    text: "",
                    type: "error",
                    timer: 1000,
                    showConfirmButton: false
                });
            }else{
                swal({
                    title: "保存成功！",
                    text: "",
                    type: "success",
                    timer: 1000,
                    showConfirmButton: false
                });
            }
            window.setTimeout(function(){ window.location.href = path + "msgnews/list"; },3000);
        },
        error: function (data) {
            swal({
                title: "error！",
                text: "",
                type: "error",
                timer: 1000,
                showConfirmButton: false
            });
            window.setTimeout(function(){ window.location.href = path + "msgnews/list"; },3000);
        }
    });
})