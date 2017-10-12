(function($,$w){
		if(typeof $w.$AjaxEchart=='undefined'){
			$AjaxEchart={
					//使用共同的后台方法，typeSql:myBitais查询语句id，parametersString:查询参数，以','号分开,echartObject： echart div对象 ，objectStr:设置参数，functionObject 回调函数封装到页面
					ajaxDisplayChart:function(typeSql,parametersString,echartObject,functionObject,objectStr){
						$.ajax({  
				              url: $basePath + "echart/getDataEchart",   
				              data:{typeSql:typeSql,parametersString:parametersString},
				              type: "post",  
				              dataType: "json",  	            
				              success: function (response) {
				            		  functionObject(response.list,echartObject,objectStr);
				            	  }
				              });
					},
					//双饼图   http://echarts.baidu.com/demo.html#pie-nest
					reurnDisplayRadiusChart:function(list,echartObject,objectStr){    
				            	  var seriesDataflow=[];
				            	  var seriesDataChannel=[];
				            	  var dataLegend=[];
				            	  var expenseChannel=0;
				            	  var freeChannel=0;
				            	  for(var i=0;i<list.length;i++){
				            		  
				            		  if(list[i].CHANNEL==9){
				            			  freeChannel=freeChannel+list[i].ECOUNT;
				            		  }else{
				            			  expenseChannel=expenseChannel+list[i].ECOUNT;
				            		  }
				            		  dataLegend.push(list[i].ENAME);
				            		  seriesDataChannel.push({value:list[i].ECOUNT, name:list[i].ENAME})
				            	  }
				            	  seriesDataflow.push(
				            			  {value:freeChannel, name:'免费流量', selected:true,itemStyle:{
	    		                              normal:{color:'#d2527f'}
	    		                          }}	  
				            	  );
				            	  seriesDataflow.push(
				            			  {value:expenseChannel, name:'付费流量',itemStyle:{
	    		                              normal:{color:'#399bff'}
	    		                          }}  
				            	  );
				            	var mainPanelPieOption= $CommonEchart.displayRadiusChart(dataLegend,seriesDataflow,seriesDataChannel);
				            	echartObject.setOption(mainPanelPieOption); 
				             
				    },
				    //单饼图 http://echarts.baidu.com/echarts2/doc/example/pie1.html
				    reurnDisplayPieChart:function(list,echartObject,objectStr){    
				      var legendData=[];
		              var seriesData=[];
		              var titleText=objectStr.titleText;
				      var seriesName=objectStr.seriesName;
		              console.info("reurnDisplayPieChart=="+JSON.stringify(list));
				  	  for(var i=0;i<list.length;i++){
				  		legendData.push(list[i].ENAME);
				  		seriesData.push({value:list[i].ECOUNT, name:list[i].ENAME})
	            	  }
		            	var mainPanelPieOption= $CommonEchart.displayPieChart(titleText, legendData,seriesName, seriesData);
		            	echartObject.setOption(mainPanelPieOption); 
		             
		           },
		           //柱状图
		           reurnDisplayBarChart:function(list,echartObject,objectStr){   
		        	   console.info("reurnDisplayBarChart=="+JSON.stringify(list));
		        	      var xAxisData = [];
		                  var seriesData = [];
		            	  for (var i = 0; i < list.length; i++){
		            		  xAxisData.push(list[i].RECORDTIME);
		            		  seriesData.push(list[i].ECOUNT); 
		            	  }  	 
		            	var barChartoption = $CommonEchart.displayBarChart(xAxisData, seriesData,objectStr.seriesName);
		            	echartObject.setOption(barChartoption); 
				             
				    },
		           //单曲线图
		           reurnDisplayOneLineChart:function(list,echartObject,objectStr){   
		        	   console.info("reurnDisplayOneLineChart=="+JSON.stringify(list));
		        	      var xdata = [];
		                  var ydata = [];
		            	  for (var i = 0; i < list.length; i++){
		            		  xdata.push(list[i].RECORDTIME);
		            		  ydata.push(list[i].ECOUNT); 
		            	  }  	 
		            	var lineChartoption = $CommonEchart.displayLineChart(xdata, ydata);
		            	echartObject.setOption(lineChartoption); 
				             
				    },
				  //多曲线图   http://echarts.baidu.com/demo.html#pie-nest
					reurnDisplayLineChart:function(list,echartObject,objectStr){   
						  var dataLegend=[];
		            	  var xData=[];
		            	  var seriesDatas=[];
		            	  var dateTimeArary=$AjaxEchart.getDatesBetween(objectStr.beginTime,objectStr.endTime);
		            	     xData=dateTimeArary;
		            	    var destArray= $AjaxEchart.setReformArray(list);
		            	     for(var i=0;i<destArray.length;i++){
		            	    	 dataLegend.push(destArray[i].ENAME);
		            	    	 var destData=destArray[i].data
		            	    	 var datax=[];
		            	    	 for(var n=0;n<dateTimeArary.length;n++){
		            	    		var isResult=0;
			            	    	 for(var k=0;k<destData.length;k++){
			            	    		 if(dateTimeArary[n]==destData[k].RECORDTIME){
			            	    			 datax.push(destData[k].ECOUNT) 
			            	    			 isResult=1
		                                     break;
			            	    		 }
		            	    	      }
			            	    	 if(isResult==0){
			            	    		 datax.push(0) 
			            	    	 }
		            	    	}
		            	    	 seriesDatas.push( {
			    			            name:destArray[i].ENAME,
			    			            type:'line',
			    			            stack: '总量',
			    			            data:datax
			    			        });
		            	    	 
		            	     }
		            	var mainPanelLineOption= $CommonEchart.displayMutilLineChart(dataLegend,xData,seriesDatas);
		            	echartObject.setOption(mainPanelLineOption); 
				             
				    },
				    getDatesBetween:function(beginTime,endTime){
				    	var dateTimeArary=[];
				    	  var beginDateTime = $AjaxEchart.getDate(beginTime);
				    	  var endDateTime = $AjaxEchart.getDate(endTime);
				    	  dateTimeArary.push(beginTime);
				    	  while((endDateTime.getTime()-beginDateTime.getTime())>=0){
				    	    beginDateTime.setDate(beginDateTime.getDate()+1);
				    	    var year = beginDateTime.getFullYear();
				    	    var month = beginDateTime.getMonth().toString().length==1?"0"+beginDateTime.getMonth().toString():beginDateTime.getMonth();
				    	    var day = beginDateTime.getDate().toString().length==1?"0"+beginDateTime.getDate():beginDateTime.getDate();
				    	    var dataStr=year+'-'+month+'-'+day
				    	    dateTimeArary.push(dataStr);
				    	  }
				    	  dateTimeArary.push(endTime);
				    return dateTimeArary;
				    } ,  getDate:function(datestr){
				    	  var temp = datestr.split("-");
				    	  var date = new Date(temp[0],temp[1],temp[2]);
				    return date;
				    } ,setReformArray:function(arrayStr){
				    	var map = {};
				    	var dest = [];
				    	for(var i = 0; i < arrayStr.length; i++){
				    	    var ai = arrayStr[i];
				    	    if(!map[ai.CHANNEL]){
				    	        dest.push({
				    	        	CHANNEL: ai.CHANNEL,
				    	        	ENAME: ai.ENAME,
				    	            data: [ai]
				    	        });
				    	        map[ai.CHANNEL] = ai;
				    	    }else{
				    	        for(var j = 0; j < dest.length; j++){
				    	            var dj = dest[j];
				    	            if(dj.CHANNEL == ai.CHANNEL){
				    	                dj.data.push(ai);
				    	                break;
				    	            }
				    	        }
				    	    }
				    	}
				    	return dest;
				    }
			
		}
		}
	})(jQuery,window);

   $(document).ready(function(){ 

			 

     })
 