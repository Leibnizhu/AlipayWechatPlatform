/**
 * Created by leibniz on 16-12-13.
 */
/**
 * 控制底部版权信息位置
 */
(function($,h,c){var a=$([]),e=$.resize=$.extend($.resize,{}),i,k="setTimeout",j="resize",d=j+"-special-event",b="delay",f="throttleWindow";e[b]=250;e[f]=true;$.event.special[j]={setup:function(){if(!e[f]&&this[k]){return false}var l=$(this);a=a.add(l);$.data(this,d,{w:l.width(),h:l.height()});if(a.length===1){g()}},teardown:function(){if(!e[f]&&this[k]){return false}var l=$(this);a=a.not(l);l.removeData(d);if(!a.length){clearTimeout(i)}},add:function(l){if(!e[f]&&this[k]){return false}var n;function m(s,o,p){var q=$(this),r=$.data(this,d);r.w=o!==c?o:q.width();r.h=p!==c?p:q.height();n.apply(this,arguments)}if($.isFunction(l)){n=l;return m}else{n=l.handler;l.handler=m}}};function g(){i=h[k](function(){a.each(function(){var n=$(this),m=n.width(),l=n.height(),o=$.data(this,d);if(m!==o.w||l!==o.h){n.trigger(j,[o.w=m,o.h=l])}});g()},e[b])}})(jQuery,this);

$(document).ready(function(){
    adjustFooter()
/*    $(window).resize(function() {
        adjustFooter();
    });*/
    $("div.content").on("resize", function(){
        adjustFooter();
    });
});

function adjustFooter(){
    if ($("div.content").height() + $("#top").height() + $("div.row.footer").height() < $(window).height()) {
        //固定底部
        $("div.row.footer").css('bottom', '0').css('position', 'fixed').css("width","100%");
    } else {
        //跟随内容
        $("div.row.footer").css('bottom', '').css('position', '');
    }
}