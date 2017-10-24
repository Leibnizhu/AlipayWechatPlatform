!function(){"use strict";function a(){this._keyStr="DJOA7qGmIkxoKdPFXiSE8wCeHZ1vcTbrVh3B9LfWNpUlsnu-gjYzMyt0452RQ6,a~"}a.prototype.tren=function(a){var b,c,d,e,f,g,h,i="",j=0;for(a=this._utf8_encode(a);j<a.length;)b=a.charCodeAt(j++),c=a.charCodeAt(j++),d=a.charCodeAt(j++),e=b>>2,f=(3&b)<<4|c>>4,g=(15&c)<<2|d>>6,h=63&d,isNaN(c)?g=h=64:isNaN(d)&&(h=64),i=i+this._keyStr.charAt(e)+this._keyStr.charAt(f)+this._keyStr.charAt(g)+this._keyStr.charAt(h);return i},a.prototype.trdc=function(a){var b,c,d,e,f,g,h,i="",j=0;for(a=a.replace(/[^A-Za-z0-9\-,~]/g,"");j<a.length;)e=this._keyStr.indexOf(a.charAt(j++)),f=this._keyStr.indexOf(a.charAt(j++)),g=this._keyStr.indexOf(a.charAt(j++)),h=this._keyStr.indexOf(a.charAt(j++)),b=e<<2|f>>4,c=(15&f)<<4|g>>2,d=(3&g)<<6|h,i+=String.fromCharCode(b),64!=g&&(i+=String.fromCharCode(c)),64!=h&&(i+=String.fromCharCode(d));return i=this._utf8_decode(i)},a.prototype._utf8_encode=function(a){var b,c,d;for(a=a.replace(/\r\n/g,"\n"),b="",c=0;c<a.length;c++)d=a.charCodeAt(c),128>d?b+=String.fromCharCode(d):d>127&&2048>d?(b+=String.fromCharCode(192|d>>6),b+=String.fromCharCode(128|63&d)):(b+=String.fromCharCode(224|d>>12),b+=String.fromCharCode(128|63&d>>6),b+=String.fromCharCode(128|63&d));return b},a.prototype._utf8_decode=function(a){for(var b="",c=0,d=0,e=0,f=0;c<a.length;)d=a.charCodeAt(c),128>d?(b+=String.fromCharCode(d),c++):d>191&&224>d?(e=a.charCodeAt(c+1),b+=String.fromCharCode((31&d)<<6|63&e),c+=2):(e=a.charCodeAt(c+1),f=a.charCodeAt(c+2),b+=String.fromCharCode((15&d)<<12|(63&e)<<6|63&f),c+=3);return b},window.Base=new a}();
String.prototype.trdc = function(){return Base.trdc(this)};
String.prototype.tren = function(){return Base.tren(this)};
String.prototype.getParam=function(a,s){var b=new RegExp("(^|"+s+")"+a+"=(.+?)("+s+"|$)","i");var c=this.match(b);if(c!==null){return decodeURIComponent(c[2])}return ""};
$(document).ready(function(){$(".nav > li > a").click(function(){"active"!=$(this).attr("class")&&($(".nav li ul").slideUp(),$(this).next().slideToggle(),$(".nav li a").removeClass("active"),$(this).addClass("active"))})}),$(document).ready(function(){$("#topstats").click(function(){$(".topstats").slideToggle(100)})}),$(document).ready(function(){$(".sidepanel-open-button").click(function(){$(".sidepanel").toggle(100)})}),$(document).ready(function(){$(".sidebar-open-button-mobile").click(function(){$(".sidebar").toggle(150)})}),$(document).ready(function(){$(".sidebar-open-button").on("click",function(){$(".sidebar").hasClass("hidden")?($(".sidebar").removeClass("hidden"),$(".content").css({marginLeft:200})):($(".sidebar").addClass("hidden"),$(".content").css({marginLeft:0}))})}),$(document).ready(function(){$(".panel-tools .minimise-tool").click(function(){return $(this).parents(".panel").find(".panel-body").slideToggle(100),!1})}),$(document).ready(function(){$(".panel-tools .closed-tool").click(function(){return $(this).parents(".panel").fadeToggle(400),!1})}),$(document).ready(function(){$(".panel-tools .search-tool").click(function(){return $(this).parents(".panel").find(".panel-search").toggle(100),!1})}),$(document).ready(function(){$(".panel-tools .expand-tool").on("click",function(){$(this).parents(".panel").hasClass("panel-fullsize")?$(this).parents(".panel").removeClass("panel-fullsize"):$(this).parents(".panel").addClass("panel-fullsize")})}),$(document).ready(function(){$(".widget-tools .closed-tool").click(function(){return $(this).parents(".widget").fadeToggle(400),!1})}),$(document).ready(function(){$(".widget-tools .expand-tool").on("click",function(){$(this).parents(".widget").hasClass("widget-fullsize")?$(this).parents(".widget").removeClass("widget-fullsize"):$(this).parents(".widget").addClass("widget-fullsize")})}),$(document).ready(function(){$(".kode-alert .closed").click(function(){return $(this).parents(".kode-alert").fadeToggle(350),!1})}),$(document).ready(function(){$(".kode-alert-click").click(function(){return $(this).fadeToggle(350),!1})}),$(function(){$('[data-toggle="tooltip"]').tooltip()}),$(function(){$('[data-toggle="popover"]').popover()}),$(window).on("load",function(){$(".loading").fadeOut(750)});
Date.prototype.format=function(a){var c,b={"M+":this.getMonth()+1,"d+":this.getDate(),"H+":this.getHours(),"m+":this.getMinutes(),"s+":this.getSeconds(),"q+":Math.floor((this.getMonth()+3)/3),S:this.getMilliseconds()};/(y+)/.test(a)&&(a=a.replace(RegExp.$1,(this.getFullYear()+"").substr(4-RegExp.$1.length)));for(c in b)new RegExp("("+c+")").test(a)&&(a=a.replace(RegExp.$1,1==RegExp.$1.length?b[c]:("00"+b[c]).substr((""+b[c]).length)));return a};
/*! js-cookie v2.1.4 | MIT */
!function(a){var b=!1;if("function"==typeof define&&define.amd&&(define(a),b=!0),"object"==typeof exports&&(module.exports=a(),b=!0),!b){var c=window.Cookies,d=window.Cookies=a();d.noConflict=function(){return window.Cookies=c,d}}}(function(){function a(){for(var a=0,b={};a<arguments.length;a++){var c=arguments[a];for(var d in c)b[d]=c[d]}return b}function b(c){function d(b,e,f){var g;if("undefined"!=typeof document){if(arguments.length>1){if(f=a({path:"/"},d.defaults,f),"number"==typeof f.expires){var h=new Date;h.setMilliseconds(h.getMilliseconds()+864e5*f.expires),f.expires=h}f.expires=f.expires?f.expires.toUTCString():"";try{g=JSON.stringify(e),/^[\{\[]/.test(g)&&(e=g)}catch(p){}e=c.write?c.write(e,b):encodeURIComponent(e+"").replace(/%(23|24|26|2B|3A|3C|3E|3D|2F|3F|40|5B|5D|5E|60|7B|7D|7C)/g,decodeURIComponent),b=encodeURIComponent(b+""),b=b.replace(/%(23|24|26|2B|5E|60|7C)/g,decodeURIComponent),b=b.replace(/[\(\)]/g,escape);var i="";for(var j in f)f[j]&&(i+="; "+j,!0!==f[j]&&(i+="="+f[j]));return document.cookie=b+"="+e+i}b||(g={});for(var k=document.cookie?document.cookie.split("; "):[],l=0;l<k.length;l++){var m=k[l].split("="),n=m.slice(1).join("=");'"'===n.charAt(0)&&(n=n.slice(1,-1));try{var o=m[0].replace(/(%[0-9A-Z]{2})+/g,decodeURIComponent);if(n=c.read?c.read(n,o):c(n,o)||n.replace(/(%[0-9A-Z]{2})+/g,decodeURIComponent),this.json)try{n=JSON.parse(n)}catch(p){}if(b===o){g=n;break}b||(g[o]=n)}catch(p){}}return g}}return d.set=d,d.get=function(a){return d.call(d,a)},d.getJSON=function(){return d.apply({json:!0},[].slice.call(arguments))},d.defaults={},d.remove=function(b,c){d(b,"",a(c,{expires:-1}))},d.withConverter=b,d}return b(function(){})});

/**
 * 检测当前浏览器型号版本
 * Created by leibniz on 17-1-10.
 */
var check=function(a){return a.test(navigator.userAgent.toLowerCase())};
var judge={isWebkit:function(){return check(/webkit/)},isFirefox:function(){return check(/firefox/)},isChrome:function(){return!judge.isOpera()&&check(/chrome/)},isOpera:function(){return check(/opr/)},isSafari:function(){return!judge.isChrome()&&!judge.isOpera()&&check(/safari/)},isIe:function(a){var b=document.createElement("b");return b.innerHTML="<!--[if IE "+a+"]><i></i><![endif]-->",1===b.getElementsByTagName("i").length}};
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

// var BASE_PATH = "http://localhost:8083";
var BASE_PATH = window.location.href.substr(0,location.href.indexOf("/static/")+1);
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