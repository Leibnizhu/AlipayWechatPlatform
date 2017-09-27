(function($,$w){
		if(typeof $w.$CommonEchart=='undefined'){
            function sortByValur (object1,object2){
                var value1 = object1.value;
                var value2 = object2.value;

                if(value1<value2){
                    return -1;
                } else if(value1>value2){
                    return 1;
                }else{
                    return 0;
                }
            }
            var maxPoint = 25;
            var minPoint = 2;
			$CommonEchart={
                //地图
                displayMapChart:function (seriesDataPV,seriesDataSHARE,seriesDataOAUTH,seriesDataPARTICIPATION) {
                    var sortedPv = seriesDataPV.sort(sortByValur);
                    var sortedShare = seriesDataSHARE.sort(sortByValur);
                    var sortedOauth = seriesDataOAUTH.sort(sortByValur);
                    var sortedParticipation = seriesDataPARTICIPATION.sort(sortByValur);
                    var dataLimit = {
                      pvMax: sortedPv[sortedPv.length - 1].value,
                      pvMin: sortedPv[0].value,
                      shareMax: sortedShare[sortedShare.length - 1].value,
                      shareMin: sortedShare[0].value,
                      oauthMax: sortedOauth[sortedOauth.length - 1].value,
                      oauthMin: sortedOauth[0].value,
                      participationMax:sortedParticipation[sortedParticipation.length - 1].value,
                      participationMin: sortedParticipation[0].value
                    };
                    var geoCoordMap = {
                        "海门":[121.15,31.89],
                        "鄂尔多斯":[109.781327,39.608266],
                        "招远":[120.38,37.35],
                        "舟山":[122.207216,29.985295],
                        "齐齐哈尔":[123.97,47.33],
                        "盐城":[120.13,33.38],
                        "赤峰":[118.87,42.28],
                        "青岛":[120.33,36.07],
                        "乳山":[121.52,36.89],
                        "金昌":[102.188043,38.520089],
                        "泉州":[118.58,24.93],
                        "莱西":[120.53,36.86],
                        "日照":[119.46,35.42],
                        "胶南":[119.97,35.88],
                        "南通":[121.05,32.08],
                        "拉萨":[91.11,29.97],
                        "云浮":[112.02,22.93],
                        "梅州":[116.1,24.55],
                        "文登":[122.05,37.2],
                        "上海":[121.48,31.22],
                        "攀枝花":[101.718637,26.582347],
                        "威海":[122.1,37.5],
                        "承德":[117.93,40.97],
                        "厦门":[118.1,24.46],
                        "汕尾":[115.375279,22.786211],
                        "潮州":[116.63,23.68],
                        "丹东":[124.37,40.13],
                        "太仓":[121.1,31.45],
                        "曲靖":[103.79,25.51],
                        "烟台":[121.39,37.52],
                        "福州":[119.3,26.08],
                        "瓦房店":[121.979603,39.627114],
                        "即墨":[120.45,36.38],
                        "抚顺":[123.97,41.97],
                        "玉溪":[102.52,24.35],
                        "张家口":[114.87,40.82],
                        "阳泉":[113.57,37.85],
                        "莱州":[119.942327,37.177017],
                        "湖州":[120.1,30.86],
                        "汕头":[116.69,23.39],
                        "昆山":[120.95,31.39],
                        "宁波":[121.56,29.86],
                        "湛江":[110.359377,21.270708],
                        "揭阳":[116.35,23.55],
                        "荣成":[122.41,37.16],
                        "连云港":[119.16,34.59],
                        "葫芦岛":[120.836932,40.711052],
                        "常熟":[120.74,31.64],
                        "东莞":[113.75,23.04],
                        "河源":[114.68,23.73],
                        "淮安":[119.15,33.5],
                        "泰州":[119.9,32.49],
                        "南宁":[108.33,22.84],
                        "营口":[122.18,40.65],
                        "惠州":[114.4,23.09],
                        "江阴":[120.26,31.91],
                        "蓬莱":[120.75,37.8],
                        "韶关":[113.62,24.84],
                        "嘉峪关":[98.289152,39.77313],
                        "广州":[113.23,23.16],
                        "延安":[109.47,36.6],
                        "太原":[112.53,37.87],
                        "清远":[113.01,23.7],
                        "中山":[113.38,22.52],
                        "昆明":[102.73,25.04],
                        "寿光":[118.73,36.86],
                        "盘锦":[122.070714,41.119997],
                        "长治":[113.08,36.18],
                        "深圳":[114.07,22.62],
                        "珠海":[113.52,22.3],
                        "宿迁":[118.3,33.96],
                        "咸阳":[108.72,34.36],
                        "铜川":[109.11,35.09],
                        "平度":[119.97,36.77],
                        "佛山":[113.11,23.05],
                        "海口":[110.35,20.02],
                        "江门":[113.06,22.61],
                        "章丘":[117.53,36.72],
                        "肇庆":[112.44,23.05],
                        "大连":[121.62,38.92],
                        "临汾":[111.5,36.08],
                        "吴江":[120.63,31.16],
                        "石嘴山":[106.39,39.04],
                        "沈阳":[123.38,41.8],
                        "苏州":[120.62,31.32],
                        "茂名":[110.88,21.68],
                        "嘉兴":[120.76,30.77],
                        "长春":[125.35,43.88],
                        "胶州":[120.03336,36.264622],
                        "银川":[106.27,38.47],
                        "张家港":[120.555821,31.875428],
                        "三门峡":[111.19,34.76],
                        "锦州":[121.15,41.13],
                        "南昌":[115.89,28.68],
                        "柳州":[109.4,24.33],
                        "三亚":[109.511909,18.252847],
                        "自贡":[104.778442,29.33903],
                        "吉林":[126.57,43.87],
                        "阳江":[111.95,21.85],
                        "泸州":[105.39,28.91],
                        "西宁":[101.74,36.56],
                        "宜宾":[104.56,29.77],
                        "呼和浩特":[111.65,40.82],
                        "成都":[104.06,30.67],
                        "大同":[113.3,40.12],
                        "镇江":[119.44,32.2],
                        "桂林":[110.28,25.29],
                        "张家界":[110.479191,29.117096],
                        "宜兴":[119.82,31.36],
                        "北海":[109.12,21.49],
                        "西安":[108.95,34.27],
                        "金坛":[119.56,31.74],
                        "东营":[118.49,37.46],
                        "牡丹江":[129.58,44.6],
                        "遵义":[106.9,27.7],
                        "绍兴":[120.58,30.01],
                        "扬州":[119.42,32.39],
                        "常州":[119.95,31.79],
                        "潍坊":[119.1,36.62],
                        "重庆":[106.54,29.59],
                        "台州":[121.420757,28.656386],
                        "南京":[118.78,32.04],
                        "滨州":[118.03,37.36],
                        "贵阳":[106.71,26.57],
                        "无锡":[120.29,31.59],
                        "本溪":[123.73,41.3],
                        "克拉玛依":[84.77,45.59],
                        "渭南":[109.5,34.52],
                        "马鞍山":[118.48,31.56],
                        "宝鸡":[107.15,34.38],
                        "焦作":[113.21,35.24],
                        "句容":[119.16,31.95],
                        "北京":[116.46,39.92],
                        "徐州":[117.2,34.26],
                        "衡水":[115.72,37.72],
                        "包头":[110,40.58],
                        "绵阳":[104.73,31.48],
                        "乌鲁木齐":[87.68,43.77],
                        "枣庄":[117.57,34.86],
                        "杭州":[120.19,30.26],
                        "淄博":[118.05,36.78],
                        "鞍山":[122.85,41.12],
                        "溧阳":[119.48,31.43],
                        "库尔勒":[86.06,41.68],
                        "安阳":[114.35,36.1],
                        "开封":[114.35,34.79],
                        "济南":[117,36.65],
                        "德阳":[104.37,31.13],
                        "温州":[120.65,28.01],
                        "九江":[115.97,29.71],
                        "邯郸":[114.47,36.6],
                        "临安":[119.72,30.23],
                        "兰州":[103.73,36.03],
                        "沧州":[116.83,38.33],
                        "临沂":[118.35,35.05],
                        "南充":[106.110698,30.837793],
                        "天津":[117.2,39.13],
                        "富阳":[119.95,30.07],
                        "泰安":[117.13,36.18],
                        "诸暨":[120.23,29.71],
                        "郑州":[113.65,34.76],
                        "哈尔滨":[126.63,45.75],
                        "聊城":[115.97,36.45],
                        "芜湖":[118.38,31.33],
                        "唐山":[118.02,39.63],
                        "平顶山":[113.29,33.75],
                        "邢台":[114.48,37.05],
                        "德州":[116.29,37.45],
                        "济宁":[116.59,35.38],
                        "荆州":[112.239741,30.335165],
                        "宜昌":[111.3,30.7],
                        "义乌":[120.06,29.32],
                        "丽水":[119.92,28.45],
                        "洛阳":[112.44,34.7],
                        "秦皇岛":[119.57,39.95],
                        "株洲":[113.16,27.83],
                        "石家庄":[114.48,38.03],
                        "莱芜":[117.67,36.19],
                        "常德":[111.69,29.05],
                        "保定":[115.48,38.85],
                        "湘潭":[112.91,27.87],
                        "金华":[119.64,29.12],
                        "岳阳":[113.09,29.37],
                        "长沙":[113,28.21],
                        "衢州":[118.88,28.97],
                        "廊坊":[116.7,39.53],
                        "菏泽":[115.480656,35.23375],
                        "合肥":[117.27,31.86],
                        "武汉":[114.31,30.52],
                        "大庆":[125.03,46.58]
                    };

                    var data = [
                        {name: "海门", value: 9},
                        {name: "鄂尔多斯", value: 12},
                        {name: "招远", value: 12},
                        {name: "舟山", value: 12},
                        {name: "齐齐哈尔", value: 14},
                        {name: "盐城", value: 15}

                    ];

                    var data1 = [
                        {name: "舟山", value: 12},
                        {name: "齐齐哈尔", value: 14},
                        {name: "盐城", value: 15},
                        {name: "赤峰", value: 16},
                        {name: "青岛", value: 18},
                        {name: "乳山", value: 18},

                    ];
                    var convertData = function (data) {
                        var res = [];
                        for (var i = 0; i < data.length; i++) {
                            var geoCoord = geoCoordMap[data[i].name];
                            if (geoCoord) {
                                //console.info(data[i].name+"ggggg"+data[i].name);
                                res.push({
                                    name: data[i].name,
                                    value: geoCoord.concat(data[i].value)
                                });
                            }
                        }
                        return res;
                    };
                    var convertedData = [
                        convertData(seriesDataPV),
                        convertData(seriesDataSHARE),
                        convertData(seriesDataOAUTH),
                        convertData(seriesDataPARTICIPATION)
                    ];
                    var option = {
                        title: {
                            text: '',
                            subtext: '',
                            left: 'center'
                        },
                        legend: {
                            selectedMode:'single',
                            selected: {
                                '曝光次数': true,
                            },
                            data: [{
                                name: '曝光次数',
                                icon: 'circle',
                                textStyle: {
                                    color: '#EE7AE9'
                                }
                            },
                                {
                                    name: '用户参与数',
                                    icon: 'circle',
                                    textStyle: {
                                        color: 'red'
                                    }
                                },
                                {
                                    name: '分享次数',
                                    icon: 'circle',
                                    textStyle: {
                                        color: '#8B2252'
                                    }
                                },
                                {
                                    name: '新增授权',
                                    icon: 'circle',
                                    textStyle: {
                                        color: '8A2BE2'
                                    }
                                }
                            ],
                        },
                        noDataLoadingOption: {
                            text: '暂无数据',
                            effect: 'bubble',
                            effectOption: {
                                effect: {
                                    n: 0
                                }
                            }
                        },
                        tooltip: {
                            trigger: 'item'
                        },
                        layoutCenter: ['50%', '50%'],
                        layoutSize: '120%',

                        toolbox: {
                            show: true,
                            left: 'right',
                            iconStyle: {
                                normal: {
                                    borderColor: '#fff'
                                },
                                emphasis: {
                                    borderColor: '#b1e4ff'
                                }
                            },
                           /* feature: {
                                dataZoom: {},
                                brush: {
                                    type: ['rect', 'polygon', 'clear']
                                },
                                saveAsImage: {
                                    show: true
                                }
                            }*/
                        },

                        geo: {
                            map: 'china',
                            left: '10',
                            right: '35%',
                            zoom:1,
                            label: {
                                emphasis: {
                                    show: false
                                }
                            },
                            roam: true,
                            itemStyle: {
                                normal: {
/*
                                    areaColor: '#323c48',
*/
                                    borderColor: '#111'
                                },
                                emphasis: {
                                    areaColor: 'green'
                                }
                            }
                        },
                        series: [
                            {
                                name: '曝光次数',
                                type: 'effectScatter',
                                coordinateSystem: 'geo',
                                data: convertedData[0],
                                symbolSize: function(val) {
                                   /* var res=0;
                                   if(val[2] / 500>25){
                                       res=25
                                   }else{
                                       res=val[2] / 500;
                                   }
                                    return Math.max(res, 5);*/
                                    return (val[2] - dataLimit.pvMin)*(maxPoint - minPoint)/(dataLimit.pvMax - dataLimit.pvMin) +minPoint;
                                },
                                showEffectOn: 'render',
                                rippleEffect: {
                                    brushType: 'stroke'
                                },
                                hoverAnimation: true,
                                label: {
                                    normal: {
                                        formatter: '{b}',
                                        position: 'right',
                                        show: true
                                    }
                                },
                                itemStyle: {
                                    normal: {
                                        color: '#EE7AE9',
                                        shadowBlur: 50,
                                        shadowColor: '#c8ee43'
                                    }
                                },
                                zlevel: 1
                            },
                            {
                                name: '用户参与数',
                                type: 'effectScatter',
                                coordinateSystem: 'geo',
                                data: convertedData[3],
                                symbolSize: function(val) {
                                    /*var res=0;
                                     if(val[2] / 500>25){
                                     res=25
                                     }else{
                                     res=val[2] / 500;
                                     }
                                     return Math.max(res, 5);*/
                                    return (val[2] - dataLimit.participationMin)*(maxPoint - minPoint)/(dataLimit.participationMax - dataLimit.participationMin) +minPoint;
                                },
                                showEffectOn: 'render',
                                rippleEffect: {
                                    brushType: 'stroke'
                                },
                                hoverAnimation: true,
                                label: {
                                    normal: {
                                        formatter: '{b}',
                                        position: 'right',
                                        show: true
                                    }
                                },
                                itemStyle: {
                                    normal: {
                                        color: 'red',
                                        shadowBlur: 50,
                                        shadowColor: '#EE0000'
                                    }
                                },
                                zlevel: 1
                            },
                            {
                                name: '分享次数',
                                type: 'effectScatter',
                                coordinateSystem: 'geo',
                                data: convertedData[1],
                                symbolSize: function(val) {
                                    /*var res=0;
                                    if(val[2] / 500>25){
                                        res=25
                                    }else{
                                        res=val[2] / 500;
                                    }
                                    return Math.max(res, 5);*/
                                    return (val[2] - dataLimit.shareMin)*(maxPoint - minPoint)/(dataLimit.shareMax - dataLimit.shareMin) +minPoint;
                                },
                                showEffectOn: 'render',
                                rippleEffect: {
                                    brushType: 'stroke'
                                },
                                hoverAnimation: true,
                                label: {
                                    normal: {
                                        formatter: '{b}',
                                        position: 'right',
                                        show: true
                                    }
                                },
                                itemStyle: {
                                    normal: {
                                        color: '#8B2252',
                                        shadowBlur: 50,
                                        shadowColor: '#EE0000'
                                    }
                                },
                                zlevel: 1
                            },
                            {
                                name: '新增授权',
                                type: 'effectScatter',
                                coordinateSystem: 'geo',
                                data: convertedData[2],
                                symbolSize: function(val) {
                                    /*var res=0;
                                    if(val[2] / 500>25){
                                        res=25
                                    }else{
                                        res=val[2] / 500;
                                    }
                                    return Math.max(res, 5);*/
                                    return (val[2] - dataLimit.oauthMin)*(maxPoint - minPoint)/(dataLimit.oauthMax - dataLimit.oauthMin) +minPoint;
                                },
                                showEffectOn: 'render',
                                rippleEffect: {
                                    brushType: 'stroke'
                                },
                                hoverAnimation: true,
                                label: {
                                    normal: {
                                        formatter: '{b}',
                                        position: 'right',
                                        show: true
                                    }
                                },
                                itemStyle: {
                                    normal: {
                                        color: '#8A2BE2',
                                        shadowBlur: 50,
                                        shadowColor: '#EE0000'
                                    }
                                },
                                zlevel: 1
                            }


                        ]
                    };
                    return option
                },

                //柱状图和曲线
                     displayBarLineChart:function (legendDate,xAxisData,seriesDatas,imageName) {
                         var option = {
                             legend: {
                                 data:legendDate
                             },
                             tooltip: {
                                 trigger: 'axis',
                                 axisPointer: {
                                     type: 'cross',
                                     crossStyle: {
                                         color: '#999'
                                     }
                                 }
                             },
                             toolbox: {
                                 feature: {
                                     magicType: {show: true, type: ['line', 'bar']},
                                     restore: {show: true},
                                     saveAsImage: {show: true,
                                                    name: imageName
                                                    }
                                 }
                             },
                             xAxis: [
                                 {
                                     type: 'category',
                                     data:xAxisData,
                                     axisPointer: {
                                         type: 'shadow'
                                     }
                                 }
                             ],
                             yAxis: [
                                 {
                                     type: 'value',
                                     name: '数量',
                                     min: 0,
                                     axisLabel: {
                                         formatter: '{value} 个'
                                     }
                                 }
                             ],
                             series: seriesDatas
                         };
                         return option;
                    },

					//柱状图http://echarts.baidu.com/demo.html#bar-tick-align
					displayBarChart:function(xAxisData,seriesData,seriesName){
						option = {
								color: ['#3398DB'],
							    tooltip : {
							        trigger: 'axis',
							        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
							            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
							        }
							    },
							    grid: {
							        left: '3%',
							        right: '4%',
							        bottom: '3%',
							        containLabel: true
							    },
							    xAxis : [
							        {
							            type : 'category',
							            data : xAxisData
							        }
							    ],
							    yAxis : [
							        {
							            type : 'value'
							        }
							    ],
							    series : [
							        {
							            name:seriesName,
							            type:'bar',
							            data:seriesData
							        }]};
						return option
					},
					//http://echarts.baidu.com/echarts2/doc/example/pie1.html 显示饼图中的一种形状，blockName为图形名称,dataName为数据名称，actualData为显示数据
					displayPieChart:function(titleText, legendData, seriesName,seriesData){
						var option = {
							    title : {
							        text: titleText,
							        subtext: '',
							        x:'center'
							    },
							    tooltip : {
							        trigger: 'item',
							        formatter: "{a} <br/>{b} : {c} ({d}%)"
							    },
							    legend: {
							        orient : 'vertical',
							        x : 'left',
							        data:legendData
							    },
							    toolbox: {
							        show : true,
							        feature : {
							            mark : {show: true},
							            dataView : {show: true, readOnly: false},
							            magicType : {
							                show: true,
							                type: ['pie', 'funnel'],
							                option: {
							                    funnel: {
							                        x: '25%',
							                        width: '50%',
							                        funnelAlign: 'left',
							                        max: 1548
							                    }
							                }
							            },
							            restore : {show: true},
							            saveAsImage : {show: true}
							        }
							    },
							    calculable : true,
							    series : [
							        {
							            name:seriesName,
							            type:'pie',
							            radius : '55%',
							            center: ['50%', '60%'],
							            data:seriesData
							        }
							    ]
							};

						return option
					},
					//显示漏斗图中的一种形状，funnelName为图形名称,dataName为数据名称，expectData为预期数据，actualData为实际数据
					displayFunnel:function (funnelName, dataName, expectData, actualData){
					    funneloption = {
					        title: {
					            text: funnelName,
					            subtext: '',
					                  left: 'center',
					        },

					        toolbox: {
					            feature: {
					                saveAsImage: {}
					            }
					        },
					        legend: {
					        	orient: 'vertical',
					            left: 'left',
					            top: 'center',
					            data: dataName
					        },
					        series: [
					            {
					                name: '预期',
					                type: 'funnel',
					                left: '10%',
					                width: '80%',
					                label: {
					                    normal: {
					                        formatter: '{b}预期'
					                    },
					                    emphasis: {
					                        position:'inside',
					                        formatter: '{b}预期: {c}%'
					                    }
					                },
					                labelLine: {
					                    normal: {
					                        show: false
					                    }
					                },
					                itemStyle: {
					                    normal: {
					                        opacity: 0.7
					                    }
					                },
					                data: expectData
					            },
					            {
					                name: '实际',
					                type: 'funnel',
					                left: '10%',
					                width: '80%',
					                maxSize: '80%',
					                label: {
					                    normal: {
					                        position: 'inside',
					                        formatter: '{c}%',
					                        textStyle: {
					                            color: '#fff'
					                        }
					                    },
					                    emphasis: {
					                        position:'inside',
					                        formatter: '{b}实际: {c}%'
					                    }
					                },
					                itemStyle: {
					                    normal: {
					                        opacity: 0.5,
					                        borderColor: '#fff',
					                        borderWidth: 2
					                    }
					                },
					                data: actualData
					            }
					        ]
					    };
					    return funneloption;
					},
					//单曲线图中的一种
					displayLineChart:function (xData, yData){
						var lineChartoption = {
					        title: {
					            text: ''
					        },
					        tooltip: {
					            trigger: 'axis'
					        },
					        legend: {
					            data:['']
					        },
					        grid: {
					            left: '3%',
					            right: '4%',
					            bottom: '0%',
					            top:"7%",
					            containLabel: true
					        },
					        toolbox: {
					            feature: {
					                saveAsImage: {}
					            }
					        },
					        xAxis: {
					            type: 'category',
					            boundaryGap: false,
					            data: xData
					        },
					        yAxis: {
					            type: 'value'
					        },
					        series: [
					                 {
					                	 name:'指标',
					                     type:'line',
					                     stack: '总量',
					                     data:yData,
					                     itemStyle:{
					                             normal:{color:'#399bff'}
					                         }
					                 },

					        	]
					    	};

						return lineChartoption;

					     },
					   //双饼图   http://echarts.baidu.com/demo.html#pie-nest
					     displayRadiusChart:function(dataLegend,seriesDataflow,seriesDataChannel){
					    	var  radiusChartOption = {
					    		        title: {
					    		               text: '流量来源占比'
					    		           },
					    		            tooltip: {
					    		                trigger: 'item',
					    		                formatter: "{a} <br/>{b}: {c} ({d}%)"
					    		            },
					    		            legend: {
					    		                orient: 'vertical',
					    		                x: 'left',
					    		                y:'center',
					    		                data:dataLegend
					    		            },
					    		            series: [
					    		                {
					    		                    name:'流量占比',
					    		                    center: ['55%', '65%'],
					    		                    type:'pie',
					    		                    selectedMode: 'single',
					    		                    radius: [0, '30%'],

					    		                    label: {
					    		                        normal: {
					    		                            position: 'inner'
					    		                        }
					    		                    },
					    		                    labelLine: {
					    		                        normal: {
					    		                            show: false
					    		                        }
					    		                    },
					    		                    data:seriesDataflow
					    		                },
					    		                {
					    		                    name:'渠道占比',
					    		                    center: ['55%', '64%'],
					    		                    type:'pie',
					    		                    radius: ['40%', '55%'],

					    		                    data:seriesDataChannel
					    		                }
					    		            ]
					    		        };

					    	return radiusChartOption;
					     },
					     //多曲线柱状图  http://echarts.baidu.com/demo.html#line-stack
					     displayMutilLineChart:function(dataLegend,xData,seriesDatas){
					    	 var mutilLineChartOption = {
					    			    title: {
					    			        text: '流量趋势'
					    			    },
					    			    tooltip: {
					    			        trigger: 'axis'
					    			    },
					    			    legend: {
					    			      left: 'center',
					    			      top: 'bottom',
					    			        data:dataLegend
					    			    },
					    			    grid: {
					    			        left: '3%',
					    			        right: '4%',
					    			        bottom: '18%',
					    			        containLabel: true
					    			    },
					    			    toolbox: {
					    			        feature: {
					    			            saveAsImage: {}
					    			        }
					    			    },
					    			    xAxis: {
					    			        type: 'category',
					    			        boundaryGap: false,
					    			        data: xData
					    			    },
					    			    yAxis: {
					    			        type: 'value'
					    			    },
					    			    series: seriesDatas
					    			};
					    	 return mutilLineChartOption;
					     }
			}}
	})(jQuery,window);