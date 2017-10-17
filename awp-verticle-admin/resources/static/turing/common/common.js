String.prototype.tren=function(){var h="DJOA7qGmIkxoKdPFXiSE8wCeHZ1vcTbrVh3B9LfWNpUlsnu-gjYzMyt0452RQ6,a";var c,e,a;var f,d,b;var g=this;a=g.length;e=0;c="";while(e<a){f=g.charCodeAt(e++)&255;if(e===a){c+=h.charAt(f>>2);c+=h.charAt((f&3)<<4);c+="~~";break}d=g.charCodeAt(e++);if(e===a){c+=h.charAt(f>>2);c+=h.charAt(((f&3)<<4)|((d&240)>>4));c+=h.charAt((d&15)<<2);c+="~";break}b=g.charCodeAt(e++);c+=h.charAt(f>>2);c+=h.charAt(((f&3)<<4)|((d&240)>>4));c+=h.charAt(((d&15)<<2)|((b&192)>>6));c+=h.charAt(b&63)}return c};
String.prototype.trdc=function(){var g=[-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,62,47,-1,-1,55,26,58,34,56,57,61,4,20,36,-1,-1,-1,-1,-1,-1,-1,3,35,22,0,19,15,6,24,8,1,12,37,52,40,2,14,60,59,18,29,42,32,39,16,50,25,-1,-1,-1,-1,-1,-1,63,30,28,13,23,38,48,33,17,49,9,43,7,45,11,41,5,31,44,54,46,27,21,10,53,51,-1,-1,-1,-1,-1];var e,c,b,a;var f,h,d;var j=this;h=j.length;f=0;d="";while(f<h){do{e=g[j.charCodeAt(f++)&255]}while(f<h&&e===-1);if(e===-1){break}do{c=g[j.charCodeAt(f++)&255]}while(f<h&&c===-1);if(c===-1){break}d+=String.fromCharCode((e<<2)|((c&48)>>4));do{b=j.charCodeAt(f++)&255;if(b===126){return d}b=g[b]}while(f<h&&b===-1);if(b===-1){break}d+=String.fromCharCode(((c&15)<<4)|((b&60)>>2));do{a=j.charCodeAt(f++)&255;if(a===126){return d}a=g[a]}while(f<h&&a===-1);if(a===-1){break}d+=String.fromCharCode(((b&3)<<6)|a)}return d};
String.prototype.getParam=function(a,s){var b=new RegExp("(^|"+s+")"+a+"=(.+?)("+s+"|$)","i");var c=this.match(b);if(c!==null){return decodeURIComponent(c[2])}return ""};
$(document).ready(function(){$(".nav > li > a").click(function(){"active"!=$(this).attr("class")&&($(".nav li ul").slideUp(),$(this).next().slideToggle(),$(".nav li a").removeClass("active"),$(this).addClass("active"))})}),$(document).ready(function(){$("#topstats").click(function(){$(".topstats").slideToggle(100)})}),$(document).ready(function(){$(".sidepanel-open-button").click(function(){$(".sidepanel").toggle(100)})}),$(document).ready(function(){$(".sidebar-open-button-mobile").click(function(){$(".sidebar").toggle(150)})}),$(document).ready(function(){$(".sidebar-open-button").on("click",function(){$(".sidebar").hasClass("hidden")?($(".sidebar").removeClass("hidden"),$(".content").css({marginLeft:200})):($(".sidebar").addClass("hidden"),$(".content").css({marginLeft:0}))})}),$(document).ready(function(){$(".panel-tools .minimise-tool").click(function(){return $(this).parents(".panel").find(".panel-body").slideToggle(100),!1})}),$(document).ready(function(){$(".panel-tools .closed-tool").click(function(){return $(this).parents(".panel").fadeToggle(400),!1})}),$(document).ready(function(){$(".panel-tools .search-tool").click(function(){return $(this).parents(".panel").find(".panel-search").toggle(100),!1})}),$(document).ready(function(){$(".panel-tools .expand-tool").on("click",function(){$(this).parents(".panel").hasClass("panel-fullsize")?$(this).parents(".panel").removeClass("panel-fullsize"):$(this).parents(".panel").addClass("panel-fullsize")})}),$(document).ready(function(){$(".widget-tools .closed-tool").click(function(){return $(this).parents(".widget").fadeToggle(400),!1})}),$(document).ready(function(){$(".widget-tools .expand-tool").on("click",function(){$(this).parents(".widget").hasClass("widget-fullsize")?$(this).parents(".widget").removeClass("widget-fullsize"):$(this).parents(".widget").addClass("widget-fullsize")})}),$(document).ready(function(){$(".kode-alert .closed").click(function(){return $(this).parents(".kode-alert").fadeToggle(350),!1})}),$(document).ready(function(){$(".kode-alert-click").click(function(){return $(this).fadeToggle(350),!1})}),$(function(){$('[data-toggle="tooltip"]').tooltip()}),$(function(){$('[data-toggle="popover"]').popover()}),$(window).on("load",function(){$(".loading").fadeOut(750)});
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

/*! js-cookie v2.1.4 | MIT */
!function(a){var b=!1;if("function"==typeof define&&define.amd&&(define(a),b=!0),"object"==typeof exports&&(module.exports=a(),b=!0),!b){var c=window.Cookies,d=window.Cookies=a();d.noConflict=function(){return window.Cookies=c,d}}}(function(){function a(){for(var a=0,b={};a<arguments.length;a++){var c=arguments[a];for(var d in c)b[d]=c[d]}return b}function b(c){function d(b,e,f){var g;if("undefined"!=typeof document){if(arguments.length>1){if(f=a({path:"/"},d.defaults,f),"number"==typeof f.expires){var h=new Date;h.setMilliseconds(h.getMilliseconds()+864e5*f.expires),f.expires=h}f.expires=f.expires?f.expires.toUTCString():"";try{g=JSON.stringify(e),/^[\{\[]/.test(g)&&(e=g)}catch(p){}e=c.write?c.write(e,b):encodeURIComponent(e+"").replace(/%(23|24|26|2B|3A|3C|3E|3D|2F|3F|40|5B|5D|5E|60|7B|7D|7C)/g,decodeURIComponent),b=encodeURIComponent(b+""),b=b.replace(/%(23|24|26|2B|5E|60|7C)/g,decodeURIComponent),b=b.replace(/[\(\)]/g,escape);var i="";for(var j in f)f[j]&&(i+="; "+j,!0!==f[j]&&(i+="="+f[j]));return document.cookie=b+"="+e+i}b||(g={});for(var k=document.cookie?document.cookie.split("; "):[],l=0;l<k.length;l++){var m=k[l].split("="),n=m.slice(1).join("=");'"'===n.charAt(0)&&(n=n.slice(1,-1));try{var o=m[0].replace(/(%[0-9A-Z]{2})+/g,decodeURIComponent);if(n=c.read?c.read(n,o):c(n,o)||n.replace(/(%[0-9A-Z]{2})+/g,decodeURIComponent),this.json)try{n=JSON.parse(n)}catch(p){}if(b===o){g=n;break}b||(g[o]=n)}catch(p){}}return g}}return d.set=d,d.get=function(a){return d.call(d,a)},d.getJSON=function(){return d.apply({json:!0},[].slice.call(arguments))},d.defaults={},d.remove=function(b,c){d(b,"",a(c,{expires:-1}))},d.withConverter=b,d}return b(function(){})});

/**
 * 检测当前浏览器型号版本
 * Created by leibniz on 17-1-10.
 */
var check = function (r) {
    return r.test(navigator.userAgent.toLowerCase());
};

var judge = {
    isWebkit: function () {
        return check(/webkit/);
    },
    isFirefox: function () {
        return check(/firefox/);
    },
    isChrome: function () {
        return !judge.isOpera() && check(/chrome/);
    },
    isOpera: function () {
        return check(/opr/);
    },
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
var supportedSweetAlert = !(judge.isIe(6) || judge.isIe(7) || judge.isIe(8));

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

var BASE_PATH = "http://localhost:8083";
var TOKEN_COOKIE_KEY = "awpJwtToken";
function authAjax(config){
    $.ajax({
        url: BASE_PATH + config.url,
        type: config.type,
        data: config.data,
        processData: config.processData,
        contentType: config.contentType,
        cache: config.cache,
        dataType: config.dataType || "json",
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", "Bearer " + Cookies.get(TOKEN_COOKIE_KEY));
        },
        error:function(e,b){
            if(e.status === 401){
                jwtLogout();
            } else {
                if(config.error)
                    config.error.call(document,e,b);
            }
        },
        success:function(data) {
            config.success.call(document, data);
        }
    });
}

function jwtLogout(){
    Cookies.remove(TOKEN_COOKIE_KEY);
    localStorage.clear();
    window.location.href = BASE_PATH + "/static/page/login.html";
}