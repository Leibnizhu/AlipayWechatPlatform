var supportedSweetAlert = !(judge.isIe(6) || judge.isIe(7) || judge.isIe(8));
var $basePath = "/";
Date.prototype.format = function (fmt) {
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "H+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
};

/**
 * 页面上需要引用本文件，并在body中加入：
 * <input type="hidden" id="SERVER_TIME" th:value="${#dates.createNow().getTime()}"/>
 *
 * @param callback 监测到返回事件时的回调方法
 */
function registerPageReturnEvent(callback) {
    //Andoroid用
    //每次webview重新打开H5首页，就把server time记录本地存储
    var SERVER_TIME = document.getElementById("SERVER_TIME");
    var REMOTE_VER = SERVER_TIME && SERVER_TIME.value;
    if (REMOTE_VER) {
        var LOCAL_VER = sessionStorage && sessionStorage.PAGEVERSION;
        if (LOCAL_VER && parseInt(LOCAL_VER) >= parseInt(REMOTE_VER)) {
            //说明html是从本地缓存中读取的
            callback();
        } else {
            //说明html是从server端重新生成的，更新LOCAL_VER
            sessionStorage.PAGEVERSION = REMOTE_VER;
        }
    }

    //iOS用
    window.addEventListener('pageshow', function(e){
        if (e.persisted) {
            callback();
        }
    });
}