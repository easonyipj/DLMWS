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
      width: {
        type: String,
        default: '200px'
      },
      height: {
        type: String,
        default: '200px'
      },
      time: {
        type: Array,
        default: []
      }
    },
    data() {
      return {
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
        chart: null,
        websocket: null,
        userCpu: [],
        sysCpu: [],
        cpuTime: [],
        memUsed: [],
        swapUsed:[],
        memTime:[]
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
    mounted() {
      this.initWebSocket();
      console.log(this.userCpu);
      console.log(this.sysCpu);
      console.log(this.cpuTime);
      this.initChart()
    },
    beforeDestroy() {
      if (!this.chart) {
        return
      }
      this.chart.dispose()
      this.chart = null
      this.websocket.close()
    },
    methods: {
      initWebSocket() {
        const ws_uri = "ws://127.0.0.1:8088" + "/ws/host/" + this.ip;//ws地址
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
        if(data.cpuList !== undefined) {
          let cpuDate = data.cpuList;
          this.userCpu = [];
          this.sysCpu = [];
          this.cpuTime = [];
          for( let j = 0; j < cpuDate.length; j++) {
            this.userCpu.push(cpuDate[j].userCpu*100);
            this.sysCpu.push(cpuDate[j].sysCpu*100);
            this.cpuTime.push(new Date(cpuDate[j].time).toLocaleString());
          }
        }
        if(data.memList !== undefined) {
          let memDate = data.memList;
          this.memUsed = [];
          this.swapUsed = [];
          this.memTime = [];
          for( let j = 0; j < memDate.length; j++) {
            this.memUsed.push(memDate[j].memUsed);
            this.swapUsed.push(memDate[j].swapUsed);
            this.memTime.push(new Date(memDate[j].time).toLocaleString());
          }
        }
        this.setChartData(this.cpuTime, this.sysCpu, this.userCpu, this.memTime, this.memUsed, this.swapUsed)
      },
      // 发送时间区间
      websocket_send(data){
        this.websocket.send(data);
      },
      websocket_close(e){
        console.log("connection closed (" + e.code + ")");
      },
      setChartData(time, sys, user, memTime, mem, swap) {
        this.chart.setOption({
          xAxis: [{
            name: 'cpu',
            data: time
          },{
            name: 'mem',
            data: memTime
          }],
          series: [{
            name: 'sysCpu',
            data: sys
          }, {
            name: 'userCpu',
            data: user
          },{
            name: 'memUsed',
            data: mem
          },{
            name: 'swapUsed',
            data: swap
          }]
        })
      },
      initChart() {
        this.chart = echarts.init(document.getElementById(this.id))
        this.chart.setOption({
          backgroundColor: '#394056',
          title: [{
            top: 20,
            text: 'Host Cpu',
            textStyle: {
              fontWeight: 'normal',
              fontSize: 16,
              color: '#F1F1F3'
            },
            left: '1%'
          },{
            top: 350,
            text: 'Host Mem',
            textStyle: {
              fontWeight: 'normal',
              fontSize: 16,
              color: '#F1F1F3'
            },
            left: '1%'
          }],
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              lineStyle: {
                color: '#57617B'
              }
            }
          },
          legend: [{
            top: 20,
            icon: 'rect',
            itemWidth: 14,
            itemHeight: 5,
            itemGap: 13,
            data: ['sysCpu', 'userCpu'],
            right: '4%',
            textStyle: {
              fontSize: 12,
              color: '#F1F1F3'
            }
          },{
            top: 350,
            icon: 'rect',
            itemWidth: 14,
            itemHeight: 5,
            itemGap: 13,
            data: ['memUsed', 'swapUsed'],
            right: '4%',
            textStyle: {
              fontSize: 12,
              color: '#F1F1F3'
            }
          }
          ],
          grid: [{
            top: 80,
            height: '30%',
            left: '5%',
            right: '5%',
            bottom: '2%',
            containLabel: true
          },{
            top: '50%',
            height: '30%',
            left: '5%',
            right: '5%',
            bottom: '2%',
            containLabel: true
          }],
          xAxis: [{
            name: 'cpu',
            type: 'category',
            gridIndex: 0,
            boundaryGap: false,
            axisLine: {
              lineStyle: {
                color: '#57617B'
              }
            },
            data: this.cpuTime
          },{
            name: 'mem',
            type: 'category',
            gridIndex: 1,
            boundaryGap: false,
            axisLine: {
              lineStyle: {
                color: '#57617B'
              }
            },
            data: this.memTime
          }],
          yAxis: [{
            type: 'value',
            gridIndex: 0,
            name: '(%)',
            axisTick: {
              show: false
            },
            axisLine: {
              lineStyle: {
                color: '#57617B'
              }
            },
            axisLabel: {
              margin: 10,
              textStyle: {
                fontSize: 14
              }
            },
            splitLine: {
              lineStyle: {
                color: '#57617B'
              }
            }
          }, {
            type: 'value',
            gridIndex: 1,
            name: '(G)',
            axisTick: {
              show: false
            },
            axisLine: {
              lineStyle: {
                color: '#57617B'
              }
            },
            axisLabel: {
              margin: 10,
              textStyle: {
                fontSize: 14
              }
            },
            splitLine: {
              lineStyle: {
                color: '#57617B'
              }
            }
          }],
          series: [{
            name: 'sysCpu',
            type: 'line',
            xAxisIndex: 0,
            yAxisIndex: 0,
            smooth: true,
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
            data: this.sysCpu
          }, {
            name: 'userCpu',
            type: 'line',
            xAxisIndex: 0,
            yAxisIndex: 0,
            smooth: true,
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
            data: this.userCpu//[120, 110, 125, 145, 122, 165, 122, 220, 182, 191, 134, 150]
          }, {
            name:'memUsed',
            type: 'line',
            xAxisIndex: 1,
            yAxisIndex: 1,
            smooth: true,
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
            data: this.memUsed
          }, {
            name: 'swapUsed',
            type: 'line',
            xAxisIndex: 1,
            yAxisIndex: 1,
            smooth: true,
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
            data: this.swapUsed
          }]
        })
      }
    }
  }
</script>
<style>
  .hostBar{
    margin-bottom: 10px;
  }
</style>
