/**
 * 检测当前浏览器型号版本
 * Created by leibniz on 17-1-10.
 */
var check = function (r) {
    return r.test(navigator.userAgent.toLowerCase());
};

var judge = {
    /**
     * 是否为webkit内核的浏览器
     */
    isWebkit: function () {
        return check(/webkit/);
    },
    /**
     * 是否为火狐浏览器
     */
    isFirefox: function () {
        return check(/firefox/);
    },
    /**
     * 是否为谷歌浏览器
     */
    isChrome: function () {
        return !judge.isOpera() && check(/chrome/);
    },
    /**
     * 是否为Opera浏览器
     */
    isOpera: function () {
        return check(/opr/);
    },
    /**
     * 检测是否为Safari浏览器
     */
    isSafari: function () {
        // google chrome浏览器中也包含了safari
        return !judge.isChrome() && !judge.isOpera() && check(/safari/)
    },
    isIe: function(ver){
        var b = document.createElement('b');
        b.innerHTML = '<!--[if IE ' + ver + ']><i></i><![endif]-->';
        return b.getElementsByTagName('i').length === 1;
    }
};