
(function(doc){
    /**
     * 定义对象，用于定义数据和方法处理等；
     */
    var um = {
        /**
         * 私有的数据域；
         */
        data: {},
        /**
         * 初始化的方法（包括数据和事件的初始化），作为闭包的返回值；
         */
        init: {
            /* 初始化页面数据，主要是对调用方法对菜单、数据列表和模态框的行业下拉框进行初始化*/
            initData: function () {
                $('#btn_add').on('click', function () {
                    // 显示模态框
                    $("#upPhoneModal").modal("show");
                });
                return this;
            },
            /**
             * 注册各种点击或状态改变的事件；
             */
            initEvent: function () {
                // 页脚自适应

            }
        }
    }
    doc.photo = um.init;

    $(doc).ready(function () {
        doc.photo.initData().initEvent(); // 数据和事件初始化
        //0.初始化fileinput
        var fileInput = new FileInput();
        fileInput.Init("#file","#tmurl");
    });
})(document);
