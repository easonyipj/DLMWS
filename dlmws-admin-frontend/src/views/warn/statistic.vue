<template>
<div class="app-container">
  <el-row :gutter="0">
    <el-col :span="24">
      <el-form :inline="true"  size="mini" class="demo-form-inline">
        <el-form-item label="项目">
          <el-select size="mini" v-model="project" placeholder="请选择项目">
            <el-option label="全部" value="all"></el-option>
            <el-option
              v-for="item in projectList"
              :key="item"
              :label="item"
              :value="item"/>
          </el-select>
        </el-form-item>
        <el-form-item label="请选择时间">
          <el-date-picker
            size="mini"
            v-model="date"
            type="datetimerange"
            :picker-options="pickerOptions"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            align="right">
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button size="mini" icon="el-icon-s-data"  type="primary" @click="getStatistics(true)">查询统计</el-button>
        </el-form-item>
      </el-form>
    </el-col>
  </el-row>
  <el-row :gutter="0">
    <el-col :span="24">
      <div id="warnCountChart" style="width: 100%;height:400px;" ></div>
    </el-col>
  </el-row>
  <el-row :gutter="0">
    <el-col :span="12">
      <div id="logTypeChart" style="width: 100%;height:300px;" ></div>
    </el-col>
    <el-col :span="12">
      <div id="keyWordCountsChart" style="width: 100%;height:300px;" ></div>
    </el-col>
  </el-row>
  <el-row :gutter="0">
    <el-col :span="12">
      <div id="dingChart" style="width: 100%;height:300px;" ></div>
    </el-col>
    <el-col :span="12">
      <div id="emailChart" style="width: 100%;height:300px;" ></div>
    </el-col>
  </el-row>
</div>
</template>

<script>
  import echarts from 'echarts'
  import moment from 'moment'
  import { statistic } from '../../api/warn'

  export default {
    name: 'statistic',
    created() {
      // TODO: 获取登录用户的项目列表
      this.projectList = ['dlmws-log', 'dlmws-agent'];
    },
    mounted() {
      this.initChart();
    },
    data() {
      return{
        project: '',
        warnCountChart: null,
        logTypeChart: null,
        dingChart: null,
        emailChart: null,
        projectCountChart: null,
        keyWordCountsChart: null,
        projectList: [],
        date:[new Date(new Date().getTime() -  3600 * 1000 * 24), new Date()],
        pickerOptions: {
          shortcuts: [{
            text: '最近一天',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24);
              picker.$emit('pick', [start, end]);
            }
          }, {
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
      }
    },
    methods:{
      getStatistics(){
        let st = this.date[0];
        let ed = this.date[1];
        let start = moment(new Date(st)).format("YYYY-MM-DD HH:mm:ss");
        let end = moment(new Date(ed)).format("YYYY-MM-DD HH:mm:ss");
        let param = {
          'projects': this.project,
          'from': start,
          'to': end
        }
        statistic(param).then(res => {
            this.setWarnCountChartData(res.data.warnCounts)
            this.setLogTypeCountChartData(res.data.logTypeCounts, this.logTypeChart)
            this.setLogTypeCountChartData(res.data.keyWordCounts, this.keyWordCountsChart)
            this.setStatusCountChartData(res.data.dingWarnStatusCounts, this.dingChart)
            this.setStatusCountChartData(res.data.emailWarnStatusCounts, this.emailChart)
          }
        )
      },

      initChart() {
        this.warnCountChart = echarts.init(document.getElementById('warnCountChart'))
        this.warnCountChart.setOption({
          tooltip: {
            trigger: 'axis',
            position: function (pt) {
              return [pt[0], '10%'];
            }
          },
          title: {
            left: 'center',
            text: '报警分钟产生数统计',
          },
          toolbox: {
            feature: {
              dataZoom: {
                yAxisIndex: 'none'
              },
              restore: {},
              saveAsImage: {}
            }
          },
          xAxis: {
            type: 'category',
            boundaryGap: false,
            data: this.xTime
          },
          yAxis: {
            type: 'value',
            boundaryGap: [0, '100%']
          },
          series: [
            {
              name: '模拟数据',
              type: 'line',
              smooth: true,
              symbol: 'none',
              sampling: 'average',
              data: []
            }
          ]
        });
        this.logTypeChart = echarts.init(document.getElementById('logTypeChart'))
        this.logTypeChart.setOption({
          title: {
            text: '日志类型统计',
            left: 'center'
          },
          tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b} : {c} ({d}%)'
          },
          legend: {
            orient: 'vertical',
            left: 'left',
            data: []
          },
          series: [
            {
              name: '日志类型统计',
              type: 'pie',
              radius: '55%',
              center: ['50%', '60%'],
              emphasis: {
                itemStyle: {
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
              },
              data: []
            }
          ]
        });
        this.keyWordCountsChart = echarts.init(document.getElementById('keyWordCountsChart'))
        this.keyWordCountsChart.setOption({
          title: {
            text: '关键字统计',
            left: 'center'
          },
          tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b} : {c} ({d}%)'
          },
          legend: {
            orient: 'vertical',
            left: 'left',
            data: []
          },
          series: [
            {
              name: '关键字统计',
              type: 'pie',
              radius: '55%',
              center: ['50%', '60%'],
              emphasis: {
                itemStyle: {
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
              },
              data: []
            }
          ]
        });
        this.dingChart = echarts.init(document.getElementById('dingChart'))
        this.dingChart.setOption({
          title: {
            text: '钉钉发送状态',
            left: 'center'
          },
          tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b} : {c} ({d}%)'
          },
          legend: {
            orient: 'vertical',
            left: 'left',
            data: []
          },
          series: [
            {
              name: '钉钉发送状态',
              type: 'pie',
              radius: '55%',
              center: ['50%', '60%'],
              data: [],
              emphasis: {
                itemStyle: {
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
              }
            }
          ]
        });
        this.emailChart = echarts.init(document.getElementById('emailChart'))
        this.emailChart.setOption({
          title: {
            text: '邮件发送状态',
            left: 'center'
          },
          tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b} : {c} ({d}%)'
          },
          legend: {
            orient: 'vertical',
            left: 'left',
            data: []
          },
          series: [
            {
              name: '邮件发送状态',
              type: 'pie',
              radius: '55%',
              center: ['50%', '60%'],
              data: [],
              emphasis: {
                itemStyle: {
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
              }
            }
          ]
        })
      },
      setWarnCountChartData(data) {
        let st = this.date[0].getTime() - this.date[0].getSeconds() * 1000;
        let xTime = [];
        let series = [];
        for(let i = 0; i < data.length; i++) {
          series.push({
            name: data[i].project,
            type: 'line',
            smooth: true,
            symbol: 'none',
            sampling: 'average',
            // itemStyle: this.itemStyle[i],
            // areaStyle: this.areaStyle[i],
            data: []
          });
        }
        for(let t = st; t < this.date[1].getTime(); t += 60 * 1000) {
          let time = moment(t).format("YYYY-MM-DD HH:mm:ss");
          xTime.push(time);
          for(let i = 0; i <data.length; i++) {
            series[i].data.push(0);
          }
        }

        for(let i = 0; i < data.length; i++) {
          for(let j = 0; j < data[i].warnCountUnits.length; j++) {
            let date = new Date(data[i].warnCountUnits[j].time);
            let key = moment(date.getTime() - date.getSeconds() * 1000).format("YYYY-MM-DD HH:mm:ss");
            let index = xTime.indexOf(key);
            series[i].data[index] += data[i].warnCountUnits[j].count;
          }
        }

        this.warnCountChart.setOption({
          xAxis: {
            data: xTime
          },
          series: series
        })
      },
      setLogTypeCountChartData(data, chart) {
        let meta = [];
        let chartData = [];
        console.log(data)
        for(let i = 0; i < data.length; i++) {
          for(let j = 0; j < data[i].list.length; j++) {
            let unit = data[i].list[j];
            meta.push(unit.logType);
            chartData.push({
              value : unit.count,
              name: unit.logType
            })
          }
        }
        chart.setOption({
          legend: {
            data: meta
          },
          series: [
            {
              data: chartData
            }
          ]
        })
        console.log(meta)
        console.log(chartData)
      },
      setStatusCountChartData(data, chart) {
        let meta = [];
        let chartData = [];

        for(let i = 0; i < data.length; i++) {
          for(let j = 0; j < data[i].list.length; j++) {
            let key = data[i].list[j].key;
            if(meta.indexOf(key) === -1) {
              meta.push(key)
              chartData.push({
                name: key,
                value: data[i].list[j].count
              })
            }else {
              let value = chartData.find(function(x) {return x.name = key;}).value
              value +=  data[i].list[j].count
              chartData.find(function(x) {return x.name = key;}).value = value
            }
          }
        }
        chart.setOption({
          legend: {
            data: meta
          },
          series: [
            {
              data: chartData
            }
          ]
        })
        console.log(meta)
        console.log(chartData)
      }
    }
  }
</script>

<style scoped>

</style>
