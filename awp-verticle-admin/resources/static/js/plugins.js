$(document).ready(function(){$(".nav > li > a").click(function(){"active"!=$(this).attr("class")&&($(".nav li ul").slideUp(),$(this).next().slideToggle(),$(".nav li a").removeClass("active"),$(this).addClass("active"))})}),$(document).ready(function(){$("#topstats").click(function(){$(".topstats").slideToggle(100)})}),$(document).ready(function(){$(".sidepanel-open-button").click(function(){$(".sidepanel").toggle(100)})}),$(document).ready(function(){$(".sidebar-open-button-mobile").click(function(){$(".sidebar").toggle(150)})}),$(document).ready(function(){$(".sidebar-open-button").on("click",function(){$(".sidebar").hasClass("hidden")?($(".sidebar").removeClass("hidden"),$(".content").css({marginLeft:200})):($(".sidebar").addClass("hidden"),$(".content").css({marginLeft:0}))})}),$(document).ready(function(){$(".panel-tools .minimise-tool").click(function(){return $(this).parents(".panel").find(".panel-body").slideToggle(100),!1})}),$(document).ready(function(){$(".panel-tools .closed-tool").click(function(){return $(this).parents(".panel").fadeToggle(400),!1})}),$(document).ready(function(){$(".panel-tools .search-tool").click(function(){return $(this).parents(".panel").find(".panel-search").toggle(100),!1})}),$(document).ready(function(){$(".panel-tools .expand-tool").on("click",function(){$(this).parents(".panel").hasClass("panel-fullsize")?$(this).parents(".panel").removeClass("panel-fullsize"):$(this).parents(".panel").addClass("panel-fullsize")})}),$(document).ready(function(){$(".widget-tools .closed-tool").click(function(){return $(this).parents(".widget").fadeToggle(400),!1})}),$(document).ready(function(){$(".widget-tools .expand-tool").on("click",function(){$(this).parents(".widget").hasClass("widget-fullsize")?$(this).parents(".widget").removeClass("widget-fullsize"):$(this).parents(".widget").addClass("widget-fullsize")})}),$(document).ready(function(){$(".kode-alert .closed").click(function(){return $(this).parents(".kode-alert").fadeToggle(350),!1})}),$(document).ready(function(){$(".kode-alert-click").click(function(){return $(this).fadeToggle(350),!1})}),$(function(){$('[data-toggle="tooltip"]').tooltip()}),$(function(){$('[data-toggle="popover"]').popover()}),$(window).load(function(){$(".loading").fadeOut(750)});