<template>
  <div class="dashboard-editor-container">

    <el-row :gutter="32">
      <el-col :span="4">
        <div class="card-panel">
          <div class="card-panel-description">
            <div class="card-panel-text">
              日志总数
            </div>
            <count-to :start-val="logCountNumPrev" :end-val="logCountNum" :duration="2600" class="card-panel-num" />
          </div>
        </div>
        <div class="card-panel" style="margin-top: 16px">
          <div class="card-panel-description">
            <div class="card-panel-text">
              报警总数
            </div>
            <count-to :start-val="warnCountNumPrev" :end-val="warnCountNum" :duration="2600" class="card-panel-num" />
          </div>
        </div>
      </el-col>
      <el-col :span="10">
        <div class="chart-level">
          <div id="levelType" style="width: 100%; height: 315px"/>
        </div>
      </el-col>
      <el-col :span="10">
        <div class="chart-level">
          <div id="logType" style="width: 100%; height: 315px"/>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="8">
      <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 12}" :xl="{span: 12}" style="padding-right:8px;margin-bottom:30px;">
        <div class="chart-level">
          <div id="logCount" style="width: 100%; height: 315px"/>
        </div>
      </el-col>
      <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 12}" :lg="{span: 12}" :xl="{span: 12}" style="margin-bottom:30px;">
        <div class="chart-level">
          <div id="warnCount" style="width: 100%; height: 315px"/>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>

import CountTo from 'vue-count-to'
import echarts from 'echarts'
import moment from 'moment'

export default {
  name: 'DashboardAdmin',
  components: {
    CountTo
  },
  data() {
    return {
      levelType: null,
      logType: null,
      logCount: null,
      warnCount: null,
      logCountNum: 0,
      logCountNumPrev: 0,
      warnCountNum: 0,
      warnCountNumPrev: 0,
    }
  },
  mounted() {
    this.initChart();
    this.initWebSocket();
  },
  beforeDestroy() {
    this.websocket.close()
  },
  methods: {
    initChart() {
      this.levelType = echarts.init(document.getElementById('levelType'))
      this.levelType.setOption({
        title: {
          text: '日志等级统计',
          left: '21%',
          top: '46%'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          type: 'scroll',
          orient: 'vertical',
          right: 10,
          top: 20,
          bottom: 20,
          data: ['INFO', 'ERROR', 'DEBUG', 'WARN']
        },
        series: [
          {
            name: '日志等级',
            type: 'pie',
            radius: ['50%', '70%'],
            center: ['35%', '50%'],
            avoidLabelOverlap: false,
            label: {
              show: false,
             // position: 'center'
            },
            emphasis: {
              // label: {
              //   show: true,
              //   fontSize: '30',
              //   fontWeight: 'bold'
              // }
            },
            labelLine: {
              show: false
            },
            data: [
              {value: 310, name: 'INFO'},
              {value: 234, name: 'ERROR'},
              {value: 135, name: 'WARN'},
              {value: 1548, name: 'DEBUG'}
            ]
          }
        ]
      });
      this.logType = echarts.init(document.getElementById('logType'))
      this.logType.setOption({
        title: {
          text: '报警类型统计',
          left: '21%',
          top: '46%'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          type: 'scroll',
          orient: 'vertical',
          right: 10,
          top: 20,
          bottom: 20,
          data: ['INFO', 'ERROR', 'DEBUG', 'WARN']
        },
        series: [
          {
            name: '日志等级',
            type: 'pie',
            radius: ['50%', '70%'],
            center: ['35%', '50%'],
            avoidLabelOverlap: false,
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              // label: {
              //   show: true,
              //   fontSize: '30',
              //   fontWeight: 'bold'
              // }
            },
            labelLine: {
              show: false
            },
            data: [
              {value: 310, name: 'INFO'},
              {value: 234, name: 'ERROR'},
              {value: 135, name: 'WARN'},
              {value: 1548, name: 'DEBUG'}
            ]
          }
        ]
      });
      this.logCount = echarts.init(document.getElementById('logCount'))
      this.logCount.setOption({
        xAxis: {
          data: [],
          boundaryGap: false,
          axisTick: {
            show: false
          }
        },
        grid: {
          left: 10,
          right: 10,
          bottom: 20,
          top: 30,
          containLabel: true
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross'
          },
          padding: [5, 10]
        },
        yAxis: {
          axisTick: {
            show: false
          }
        },
        title: {
          text: '日志数量趋势',
          left: 'center',
        },
        series: [
          {
            name: 'actual',
            smooth: true,
            type: 'line',
            itemStyle: {
              normal: {
                color: '#3888fa',
                lineStyle: {
                  color: '#3888fa',
                  width: 2
                },
                areaStyle: {
                  color: '#f3f8ff'
                }
              }
            },
            data: [],
            animationDuration: 2800,
            animationEasing: 'quadraticOut'
          }]
      });
      this.warnCount = echarts.init(document.getElementById('warnCount'))
      this.warnCount.setOption({
        xAxis: {
          data: [],
          boundaryGap: false,
          axisTick: {
            show: false
          }
        },
        grid: {
          left: 10,
          right: 10,
          bottom: 20,
          top: 30,
          containLabel: true
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross'
          },
          padding: [5, 10]
        },
        yAxis: {
          axisTick: {
            show: false
          }
        },
        title: {
          text: '报警数量趋势',
          left: 'center',
        },
        series: [
          {
            name: 'actual',
            smooth: true,
            type: 'line',
            itemStyle: {
              normal: {
                color: '#fa161e',
                lineStyle: {
                  color: '#fa161e',
                  width: 2
                },
                areaStyle: {
                  color: '#ff8922'
                }
              }
            },
            data: [],
            animationDuration: 2800,
            animationEasing: 'quadraticOut'
          }]
      });
    },
    initWebSocket() {
      const ws_uri = "ws://127.0.0.1:8088" + "/ws/log/" + "yipingjian";//ws地址
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
      console.log(data)
      this.logCountNumPrev = this.logCountNum;
      this.warnCountNumPrev = this.warnCountNum;
      this.logCountNum = data.logCount;
      this.warnCountNum = data.warnCount;
      this.setLogCountChart(data.logCountList, this.logCount);
      this.setLogCountChart(data.warnCountList, this.warnCount);
      this.setTypeCountChart(data.logLevelCount, this.levelType)
      this.setTypeCountChart(data.warnTypeCount, this.logType);
    },
    // 发送时间区间
    websocket_send(data){
      this.websocket.send(data);
    },
    websocket_close(e){
      console.log("connection closed (" + e.code + ")");
    },
    setLogCountChart(data, chart) {
      let time = [];
      let value = [];
      for(let i = 0; i < data.length; i++) {
        let key = moment(new Date(data[i].time)).format("YYYY-MM-DD HH:mm:ss")
        time.push(key);
        value.push(data[i].count);
      }
      chart.setOption({
        xAxis: {
          data: time
        },
        series: [
          {
            data: value,
          }]
      });
    },
    setTypeCountChart(data, chart) {
      let meta = [];
      let valueList = [];
      for(let i = 0; i < data.length; i++) {
        meta.push(data[i].type);
        valueList.push({
          name: data[i].type,
          value: data[i].count
        })
      }
      chart.setOption({
        legend: {
          data: meta
        },
        series: [
          {
            data: valueList
          }
        ]
      });
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-editor-container {
  padding: 32px;
  background-color: rgb(240, 242, 245);
  position: relative;


  .chart-wrapper {
    background: #fff;
    padding: 16px 16px 0;
    margin-bottom: 32px;
  }

  .chart-level {
    background: #fff;
    margin-bottom: 32px;
  }

  .card-panel {
    height: 150px;
    cursor: pointer;
    font-size: 12px;
    position: relative;
    overflow: hidden;
    color: #666;
    background: #fff;
    box-shadow: 4px 4px 40px rgba(0, 0, 0, .05);
    border-color: rgba(0, 0, 0, .05);

    &:hover {
      .card-panel-icon-wrapper {
        color: #fff;
      }

      .icon-people {
        background: #40c9c6;
      }

      .icon-message {
        background: #36a3f7;
      }

      .icon-money {
        background: #f4516c;
      }

      .icon-shopping {
        background: #34bfa3
      }
    }


    .card-panel-icon-wrapper {
      float: left;
      margin: 14px 0 0 14px;
      padding: 16px;
      transition: all 0.38s ease-out;
      border-radius: 6px;
    }

    .card-panel-icon {
      float: left;
      font-size: 48px;
    }

    .card-panel-description {
      float: right;
      font-weight: bold;
      margin: 30px;
      margin-top: 40px;

      .card-panel-text {
        line-height: 18px;
        color: rgba(0, 0, 0, 0.45);
        font-size: 16px;
        margin-bottom: 12px;
      }

      .card-panel-num {
        font-size: 20px;
      }
    }
  }
}

@media (max-width:1024px) {
  .chart-wrapper {
    padding: 8px;
  }
}
</style>
