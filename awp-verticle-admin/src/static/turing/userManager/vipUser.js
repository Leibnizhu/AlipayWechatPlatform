function chart(obj1, obj2){
    // 基于准备好的dom，初始化echarts图表
    var myChart = echarts.init(document.getElementById('quanpinlei'));

    // 过渡---------------------
    myChart.showLoading({
        text: '正在努力的读取数据中...'
    });

    option = {
        title: {
            text: '传播路径',
            x: 'center'
        },
        tooltip: {
            show: false
        },
        animationDurationUpdate: 1500,
        animationEasingUpdate: 'quinticInOut',
        series: [{
            type: 'graph',
            symbolSize: 50,
            roam: true, //禁止用鼠标滚轮缩小放大效果
            hoverAnimation: true,
            focusNodeAdjacency: true,// 突显节点及其邻接节点
            edgeSymbol: ['circle', 'arrow'],
            edgeSymbolSize: [4, 10],
            layout: 'force',
            draggable : true,
            force:{
                initLayout : 'circular',
                layoutAnimation : true,
                edgeLength : 150,
                gravity : 0.005,
                repulsion : 100
            },
            // 连接线上的文字
            edgeLabel: {
                normal: {
                    show: false,
                    textStyle: {
                        fontSize: 9
                    }
                }
            },
            lineStyle: {
                normal: {
                    opacity: 1,
                    width: 1
                }
            },
            // 圆圈内的文字
            label: {
                normal: {
                    show: true
                }
            },
            data: obj1,
            links: obj2
        }]
    };
    myChart.hideLoading();
    myChart.setOption(option);
}

function chart1(obj1, obj2, obj3, obj4, obj5){
    var quanpinlei1 = echarts.init(document.getElementById('quanpinlei1'));
    var indicatorMax = Math.max(obj1, obj2, obj3, obj4, obj5);
    option = {
        title: {
            text: '影响力指数'
        },
        tooltip: {},

        radar: {
            // shape: 'circle',
            indicator: [
                { name: '传播', max: indicatorMax},
                { name: '曝光', max: indicatorMax},
                { name: '转化', max: indicatorMax},
                { name: '人气', max: indicatorMax},
                { name: '活跃', max: indicatorMax}
            ]
        },
        series: [{
            type: 'radar',
            // areaStyle: {normal: {}},
            data : [

                {
                    value : [obj1, obj2, obj3, obj4, obj5]
                }
            ]
        }]
    };
    quanpinlei1.setOption(option);
}

function chart2(obj1, obj2, obj3, obj4, obj5){
    var quanpinlei2 = echarts.init(document.getElementById('quanpinlei2'));
    var quanpinleidata1 = {
        title : {
            text: '级别分析',
            subtext: '',
            x:'center'
        },
        tooltip : {
            trigger: 'item',
            formatter: "{a} <br/>{b} : {c} ({d}%)"
        },
        legend: {
            orient: 'vertical',
            left: 'left',
            data: ['一级','二级','三级','四级','多级']
        },
        series : [
            {
                name: '访问来源',
                type: 'pie',
                radius : '55%',
                center: ['50%', '60%'],
                data:[
                    {value:obj1, name:'一级'},
                    {value:obj2, name:'二级'},
                    {value:obj3, name:'三级'},
                    {value:obj4, name:'四级'},
                    {value:obj5, name:'多级'}
                ],
                itemStyle: {
                    emphasis: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            }
        ]
    };
    quanpinlei2.setOption(quanpinleidata1);
}

function chart3(obj1, obj2){
    var quanpinlei3 = echarts.init(document.getElementById('quanpinlei3'));
    var quanpinleidata3 = {
        title: {
            text: '城市分布',
            subtext: ''
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        legend: {
            data: ['']
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: {
            type: 'value',
            boundaryGap: [0, 0.01]
        },
        yAxis: {
            type: 'category',
            data: obj1
        },
        series: [
            {
                name: '',
                type: 'bar',
                data: obj2
            }
        ]
    };
    quanpinlei3.setOption(quanpinleidata3);
}

$(document).ready(function() {
     $("#leftmenu a").removeClass("active");
    $("#vipUserMenu").addClass("active");

    chart(note, link);
    chart1(spread, exposure, conversion, popularity,active);
    chart2(first, second, third, fourth, multi);
    chart3(city, count);

    $("#sel_campaign").on("change",function(){
        query();
    });

    $("#date-range-and-time-picker").on("change",function(){
        query();
    });

    function query(){
        var path = $('#path').val();
        var campaignId = $('#sel_campaign').find('option:selected').val();
        var comDate = $('#date-range-and-time-picker').val();

        if(campaignId == '-- 请选择活动 --'){
            swal({
                title: "请选择活动！",
                text: "",
                type: "error",
                timer: 2000,
                showConfirmButton: false
            });
            var iniCity = [];
            var iniCount = [];
            iniCity.push("总共");
            iniCount.push(0);
            chart(null, null);
            chart1(null, null, null, null,null);
            chart2(null, null, null, null, null);
            chart3(iniCity, iniCount);
            return false;
        }

        if(comDate == ''){
            swal({
                title: "请选择查询日期！",
                text: "",
                type: "error",
                timer: 2000,
                showConfirmButton: false
            });
            var iniCity1 = [];
            var iniCount1 = [];
            iniCity1.push("总共");
            iniCount1.push(0);
            chart(null, null);
            chart1(null, null, null, null,null);
            chart2(null, null, null, null, null);
            chart3(iniCity1, iniCount1);
            return false;
        }

        $.ajax({
            url: path + "user/query?campaignId=" + campaignId + "&comDate=" + comDate,
            type : 'POST',
            cache : false,
            data : 'json',
            processData : false,
            contentType : false,
            success: function (data) {
                if(data.res == 'refuse'){
                    swal({
                        title: "您没有权限查看该活动的相关数据！",
                        text: "",
                        type: "error",
                        timer: 2000,
                        showConfirmButton: false
                    });
                    window.setTimeout(function(){ window.location.href = path + "user/tovipuser"; },3500);
                }else{
                    var iniCity2 = [];
                    var iniCount2 = [];
                    iniCity2.push("总共");
                    iniCount2.push(0);
                    chart(null, null);
                    chart1(null, null, null, null,null);
                    chart2(null, null, null, null, null);
                    chart3(iniCity2, iniCount2);

                    if(data.vipSpreadList != undefined && data.vipSpreadList != ''){
                        var newNote = data.notes;
                        var newLink = data.links;
                        chart(newNote, newLink);
                    }

                    if(data.vipInfluenceIndex != undefined && data.vipInfluenceIndex != ''){
                        var newSpread = data.vipInfluenceIndex.spread;
                        var newExposure = data.vipInfluenceIndex.exposure;
                        var newConversion = data.vipInfluenceIndex.conversion;
                        var newPopularity = data.vipInfluenceIndex.popularity;
                        var newActive = data.vipInfluenceIndex.active;

                        chart1(newSpread, newExposure, newConversion, newPopularity,newActive);
                    }

                    if(data.vipLevelanalysis != undefined && data.vipLevelanalysis != ''){
                        var newFirst = data.vipLevelanalysis.firstLevel;
                        var newSecond = data.vipLevelanalysis.secondLevel;
                        var newThird = data.vipLevelanalysis.thirdLevel;
                        var newFourth = data.vipLevelanalysis.fourthLevel;
                        var newMulti = data.vipLevelanalysis.multiLevel;

                        chart2(newFirst, newSecond, newThird, newFourth, newMulti);
                    }

                    if(data.vipUrbanDistributionList != undefined && data.vipUrbanDistributionList != ''){
                        var newCity = [];
                        var newCount = [];

                        newCity.push("总共");
                        newCount.push(data.total);
                        for(var i = 0; i < data.vipUrbanDistributionList.length; i++){
                            newCity.push(data.vipUrbanDistributionList[i].city);
                            newCount.push(data.vipUrbanDistributionList[i].count);
                        }

                        chart3(newCity, newCount);
                    }
                }
            },
            error: function () {
                swal({
                    title: "error！",
                    text: "",
                    type: "error",
                    timer: 2000,
                    showConfirmButton: false
                });
                window.setTimeout(function(){ window.location.href = path + "user/tovipuser"; },3500);
            }
        });
    }
});

$(document).ready(function() {
    // Date Range and Time Picker -->
    $('#date-range-and-time-picker').daterangepicker({
        opens : 'right', //日期选择框的弹出位置
        timePicker: false,
        singleDatePicker:true,
        format: 'YYYY-MM-DD',
        startDate: moment().format('YYYY-MM-DD'),
        locale : {
            applyLabel : '确定',
            cancelLabel : '取消',
            fromLabel : '起始时间',
            // toLabel : '结束时间',
            customRangeLabel : '自定义',
            daysOfWeek : [ '日', '一', '二', '三', '四', '五', '六' ],
            monthNames : [ '一月', '二月', '三月', '四月', '五月', '六月',
                '七月', '八月', '九月', '十月', '十一月', '十二月' ],
            firstDay : 1
        }
    }, function(start, end, label) {
        console.log(start.toISOString(), end.toISOString(), label);
    });

    //startOf('day')
    $('#date-range-and-time-picker').val(moment().format('YYYY-MM-DD'));
});