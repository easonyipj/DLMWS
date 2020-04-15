<template>
  <div>
    <div class="hostBar" >
      <el-date-picker
        size="mini"
        v-model="dateTime"
        value-format="timestamp"
        type="datetimerange"
        :picker-options="pickerOptions"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        align="right">
      </el-date-picker>
      <el-button size="mini" type="info" icon="el-icon-back" v-on:click="$emit('back')">返回</el-button>
    </div>
    <div :id="id" :class="className" :style="{height:height,width:width}" />
  </div>

</template>

<script>
  import echarts from 'echarts'
  let threadChart = null;
  export default {
    props: {
      className: {
        type: String,
        default: 'chart'
      },
      id: {
        type: String,
        default: 'chart'
      },
      ip: {
        type: String,
        default: ''
      },
      pid: {
        type: Number,
        default: ''
      },
      width: {
        type: String,
        default: '200px'
      },
      height: {
        type: String,
        default: '200px'
      }
    },
    watch: {
      'dateTime' : function(newVal){
        let st = 0;
        let ed = 0;
        if(newVal !== null) {
          st = newVal[0];
          ed = newVal[1];
        }
        let data = {
          'st': st,
          'ed': ed
        };
        this.websocket_send(JSON.stringify(data));
      },
    },
    data() {
      return {
        chart: null,
        threadTime: [],
        total: [],
        runnable: [],
        timeWaiting: [],
        waiting: [],
        threadData: [],
        memTime: [],
        memUsed: [],
        memCapacity: [],
        classTime:[],
        classLoaded: [],
        pickerOptions: {
          shortcuts: [{
            text: '最近一周',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
              picker.$emit('pick', [start, end]);
            }
          }, {
            text: '最近一个月',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
              picker.$emit('pick', [start, end]);
            }
          }, {
            text: '最近三个月',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
              picker.$emit('pick', [start, end]);
            }
          }]
        },
        dateTime: [],
      }
    },
    mounted() {
      this.initWebSocket();
      this.initChart();
      this.chartListen();
    },
    beforeDestroy() {
      if (!threadChart) {
        return
      }
      threadChart.dispose()
      threadChart = null
      this.websocket.close()
    },
    methods: {
      initWebSocket() {
        const ws_uri = "ws://127.0.0.1:8088" + "/ws/jvm/" + this.ip + "/" + this.pid;//ws地址
        this.websocket = new WebSocket(ws_uri);
        this.websocket.onopen = this.websocket_open;
        this.websocket.onerror = this.websocket_error;
        this.websocket.onmessage = this.websocket_message;
        this.websocket.onclose = this.websocket_close;
      },
      websocket_open() {
        console.log("WebSocket连接成功");
      },
      websocket_error(e) {
        console.log("WebSocket连接发生错误");
      },
      websocket_message(e){
        const data = JSON.parse(e.data)
        if(data.threadList !== undefined) {
          let threadList = data.threadList;
          this.threadTime = ['type'];
          this.memTime =[];
          this.classTime = [];
          this.runnable = ['runnable'];
          this.waiting = ['waiting'];
          this.timeWaiting = ['timeWaiting'];
          this.total = [];
          for( let j = 0; j < threadList.length; j++) {
            this.memTime.push(new Date(threadList[j].time).toLocaleString());
            this.classTime.push(new Date(threadList[j].time).toLocaleString());
            this.threadTime.push(new Date(threadList[j].time).toLocaleString());
            this.runnable.push(threadList[j].runnable);
            this.waiting.push(threadList[j].waiting);
            this.timeWaiting.push(threadList[j].timeWaiting);
            this.total.push(threadList[j].total);
          }
          this.setThreadChartData(this.threadTime, this.runnable, this.waiting, this.timeWaiting);
        }
        if(data.memoryList !== undefined) {
          let memData = data.memoryList;
          this.memUsed = [];
          this.memCapacity = [];
          for( let j = 0; j < memData.length; j++) {
            this.memUsed.push(memData[j].memUsed);
            this.memCapacity.push(memData[j].memCapacity);
          }
          this.setMemoryChartData()
        }
        if(data.classList !== undefined) {
          let classData = data.classList;
          this.classLoaded = [];
          for( let j = 0; j < classData.length; j++) {
            this.classLoaded.push(classData[j].classLoaded);
          }
          this.setClassChartData();
        }
      },
      // 发送时间区间
      websocket_send(data){
        this.websocket.send(data);
      },
      websocket_close(e){
        console.log("connection closed (" + e.code + ")");
      },
      setMemoryChartData() {
        threadChart.setOption({
          xAxis: [
            {id:'mem', data: this.memTime},
          ],
          series:[
            {name:'memUsed', data: this.memUsed},
            {name:'memCapacity', data: this.memCapacity},
          ]
        });
      },
      setClassChartData() {
        threadChart.setOption({
          xAxis: [
            {id:'class', data: this.classTime},
          ],
          series:[
            {name:'classLoaded', data: this.classLoaded},
          ]
        });
      },
      setThreadChartData(threadTime, runnable, waiting, timeWaiting) {
        this.threadData = [];
        this.threadData.push(threadTime);
        this.threadData.push(runnable);
        this.threadData.push(waiting);
        this.threadData.push(timeWaiting);
        threadChart.setOption({
          dataset: {
            source: this.threadData
          }
        });
      },
      initChart() {
        threadChart = echarts.init(document.getElementById(this.id))
        threadChart.setOption({
          legend: {top:'20'},
          tooltip: {
            trigger: 'axis',
            showContent: true
          },
          dataset: {
            source: [
              ['type', '0'],
              ['runnable', 0],
              ['waiting', 0],
              ['timeWaiting', 0],
            ]
          },
          xAxis: [
            {type: 'category', gridIndex: 0, nameGap: '1'},
            {type: 'category', id:'mem', gridIndex: 1, data: this.memTime},
            {type: 'category', id:'class', gridIndex: 2, data: this.threadTime}
          ],
          yAxis: [
            {gridIndex: 0},
            {gridIndex: 1},
            {gridIndex: 2},
          ],
          grid:  [
            {top: '55%', width: '40%', left: '65', height:'40%'},
            {top: '10%', width: '40%', left: '55%', height:'40%'},
            {top: '55%', width: '40%', left: '55%', height:'40%'},
          ],
          series: [
            {type: 'line', xAxisIndex: 0, yAxisIndex: 0, smooth: true,
              symbol: 'circle',
              symbolSize: 5,
              showSymbol: false,
              lineStyle: {
                normal: {
                  width: 1
                }
              },
              areaStyle: {
                normal: {
                  color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                    offset: 0,
                    color: 'rgba(137, 189, 27, 0.3)'
                  }, {
                    offset: 0.8,
                    color: 'rgba(137, 189, 27, 0)'
                  }], false),
                  shadowColor: 'rgba(0, 0, 0, 0.1)',
                  shadowBlur: 10
                }
              },
              itemStyle: {
                normal: {
                  color: 'rgb(137,189,27)',
                  borderColor: 'rgba(137,189,2,0.27)',
                  borderWidth: 12

                }
              },
              seriesLayoutBy: 'row'},
            {type: 'line', xAxisIndex: 0, yAxisIndex: 0, smooth: true,
              symbol: 'circle',
              symbolSize: 5,
              showSymbol: false,
              lineStyle: {
                normal: {
                  width: 1
                }
              },
              areaStyle: {
                normal: {
                  color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                    offset: 0,
                    color: 'rgba(0, 136, 212, 0.3)'
                  }, {
                    offset: 0.8,
                    color: 'rgba(0, 136, 212, 0)'
                  }], false),
                  shadowColor: 'rgba(0, 0, 0, 0.1)',
                  shadowBlur: 10
                }
              },
              itemStyle: {
                normal: {
                  color: 'rgb(0,136,212)',
                  borderColor: 'rgba(0,136,212,0.2)',
                  borderWidth: 12

                }
              },
              seriesLayoutBy: 'row'},
            {type: 'line', xAxisIndex: 0, yAxisIndex: 0, smooth: true,
              symbol: 'circle',
              symbolSize: 5,
              showSymbol: false,
              lineStyle: {
                normal: {
                  width: 1
                }
              },
              areaStyle: {
                normal: {
                  color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                    offset: 0,
                    color: 'rgba(255,137,34,0.8)'
                  }, {
                    offset: 0.8,
                    color: 'rgba(137, 189, 27, 0)'
                  }], false),
                  shadowColor: 'rgba(0, 0, 0, 0.1)',
                  shadowBlur: 10
                }
              },
              itemStyle: {
                normal: {
                  color: 'rgb(255,88,35)',
                  borderColor: 'rgba(255,137,34,0.8)',
                  borderWidth: 12

                }
              },
              seriesLayoutBy: 'row'},
            {type: 'line', name:'memUsed', xAxisIndex: 1, yAxisIndex: 1, smooth: true, data: this.memUsed},
            {type: 'line', name:'memCapacity', xAxisIndex: 1, yAxisIndex: 1, smooth: true, data: this.memCapacity},
            {type: 'line', name:'classLoaded', xAxisIndex: 2, yAxisIndex: 2, smooth: true, data: this.classLoaded},
            {
              type: 'pie',
              id: 'pie',
              radius: '30%',
              center: ['25%', '30%'],
              label: {
                formatter: '{b}: {@0} ({d}%)'
              },
              encode: {
                itemName: 'type',
                value: '0',
                tooltip: '0'
              }
            }
          ]
        });

      },
      chartListen() {
        setTimeout(function () {
          threadChart.on('updateAxisPointer', function (event) {
            var xAxisInfo = event.axesInfo[0];
            if (xAxisInfo) {
              var dimension = xAxisInfo.value + 1;
              threadChart.setOption({
                series: {
                  id: 'pie',
                  label: {
                    formatter: '{b}: {@[' + dimension + ']} ({d}%)'
                  },
                  encode: {
                    value: dimension,
                    tooltip: dimension
                  }
                }
              });
            }
          });
        })
      }
    }
  }
</script>
