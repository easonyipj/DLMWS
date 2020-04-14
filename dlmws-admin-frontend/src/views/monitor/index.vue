<template>
  <div class="app-container">
    <div v-if="showSearch===false">
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
            <el-form-item>
              <el-button size="mini"  icon="el-icon-search" type="success" @click="changeSearchVisible(true)">检索日志</el-button>
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>
      <el-row :gutter="0">
        <div id="logCountPerMin" style="width: 100%;height:400px;" ></div>
      </el-row>
      <el-row :gutter="0">
        <div v-if="project === 'all'">
          <div id="logCountPerLevel" style="width: 100%;height:600px;" ></div>
        </div>
        <div v-if="project !== 'all'" >
          <div id="logCountTop" style="width: 100%;height:600px;" ></div>
        </div>
      </el-row>
    </div>
    <search v-on:back="changeSearchVisible(false)"  v-if="showSearch"></search>
  </div>
</template>

<script>

import moment from 'moment'
import Search from './search'
import { logCount, levelCount, topCount } from '../../api/tomcat'
import axios from 'axios'
import echarts from 'echarts'

export default {
  components: { Search },
  comments: {
    Search
  },
  filters: {
    statusFilter(status) {
      const statusMap = {
        'INFO': 'success',
        ERROR: 'danger',
        WARN: 'warning',
        'DEBUG': 'info'
      }
      return statusMap[status]
    },
    dateFormat(date) {
      return moment(date).format("YYYY-MM-DD HH:mm:ss");
    }
  },
  data() {
    return {
      level:[],
      classData: [],
      threadData:[],
      errorData:[],
      logCountChart: null,
      logCountPerLevel: null,
      logCountTop: null,
      xTime:[],
      logCountSeries:{},
      project:'all',
      projectList: [],
      showSearch: false,
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
      list: null,
      listLoading: true,
    }
  },
  created() {
    // TODO: 获取登录用户的项目列表
    this.projectList = ['dlmws-log', 'dlmws-agent'];
  },
  mounted() {
    this.initChart();
  },
  methods: {
    changeSearchVisible(bool) {
      this.showSearch = bool;
    },
    initChart() {
      this.logCountChart = echarts.init(document.getElementById('logCountPerMin'))
      this.logCountChart.setOption({
        tooltip: {
          trigger: 'axis',
          position: function (pt) {
            return [pt[0], '10%'];
          }
        },
        title: {
          left: 'center',
          text: '每分钟日志产生数统计',
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
      this.logCountPerLevel = echarts.init(document.getElementById('logCountPerLevel'));
      this.logCountPerLevel.setOption({
        tooltip: {},
        title: [{
          top:'35',
          left: 'center',
          text: '分项目日志等级统计',
        },{
          text: 'ERROR',
          top:'15%',
          left: '22%',
        },{
          text: 'INFO',
          top:'15%',
          left: '72.5%',
        },{
          text: 'WARN',
          top:'55%',
          left: '22%',
        },{
          text: 'DEBUG',
          top:'55%',
          left: '72.5%',
        },],
        series: [{
          id: 'A',
          type: 'pie',
          radius: 60,
          center: ['25%', '35%'],
          data: []
        }, {
          id: 'B',
          type: 'pie',
          radius: 60,
          center: ['75%', '35%'],
          data: []
        }, {
          id: 'C',
          type: 'pie',
          radius: 60,
          center: ['25%', '75%'],
          data: []
        }, {
          id: 'D',
          type: 'pie',
          radius: 60,
          center: ['75%', '75%'],
          data: []
        }]
      });
    },
    getStatistics() {
      // 将时间转换成ISO标准时间
      let st = this.date[0];
      let ed = this.date[1];
      let start = moment(new Date(st), 'X').utc(8).format('YYYY-MM-DDTHH:mm:ss') + '.000+08:00';
      let end = moment(new Date(ed), 'X').utc(8).format('YYYY-MM-DDTHH:mm:ss') + '.000+08:00';
      let params = {
        projects: this.project === 'all' ? this.projectList.join(",") : this.project,
        from: start,
        to: end
      }
      logCount(params).then(res => {
        this.getTimeDate(res.data)
      })

      if(this.project === 'all') {
        levelCount(params).then(res => {
         this.getLevelCount(res.data)
        })
      }else {
        levelCount(params).then(res => {
          this.level = res.data
        })
        topCount({
          ...params,
          type: 'threadPosition.keyword'
        }).then(res => {
          this.threadData = res.data
        })
        topCount({
          ...params,
          type : "classPosition.keyword"
        }).then(res => {
          this.classData = res.data
        })
        topCount({
          ...params,
          type : 'errorType.keyword'
        }).then(res => {
          this.errorData = res.data
        })

        this.getTopCount(this.level, this.threadData, this.classData, this.errorData)
      }

      // axios
      //   .post('http://localhost:8088/tomcat/logCount/project/min', params)
      //   .then( res => {
      //     this.getTimeDate(res.data.data)
      //   })
      //   .catch(function (error) {
      //     console.log(error);
      //   });
    },
    getTimeDate(data) {
      // 起始时间
      let st = this.date[0].getTime() - this.date[0].getSeconds() * 1000;
      // 项目个数
      let size = data.length;
      // 初始化下标、数据值、series
      let index = [];
      let series = [];
      for(let i = 0; i < size; i++) {
        index.push(0);
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
      this.xTime = [];
      for(let t = st; t < this.date[1].getTime(); t += 60 * 1000) {
        let time = moment(t).format("YYYY-MM-DD HH:mm:ss");
        this.xTime.push(time);
        for(let i = 0; i <size; i++) {
          let key = '';
          if(index[i] < data[i].logCounts.length) {
            key = moment(data[i].logCounts[index[i]].key + 8 * 3600 * 1000).format("YYYY-MM-DD HH:mm:ss");
          }
          if(key === time) {
            series[i].data.push(data[i].logCounts[index[i]].docCount);
            index[i] += 1;
          }else {
            series[i].data.push(0);
          }
        }
      }
      this.logCountChart.setOption({
        xAxis: {
          data: this.xTime
        },
        series: series
      })
    },
    getLevelCount(data) {
      let size = data.length;
      let info = [];
      let error = [];
      let debug = [];
      let warn = [];
      for(let i = 0; i < size; i++) {
        for(let j = 0; j < data[i].logCounts.length; j++) {
          if(data[i].logCounts[j].key === "ERROR") {
            error.push({
              name: data[i].project,
              value: data[i].logCounts[j].docCount
            })
          }
          if(data[i].logCounts[j].key === "INFO") {
            info.push({
              name: data[i].project,
              value: data[i].logCounts[j].docCount
            })
          }
          if(data[i].logCounts[j].key === "DEBUG") {
            debug.push({
              name: data[i].project,
              value: data[i].logCounts[j].docCount
            })
          }
          if(data[i].logCounts[j].key === "WARN") {
            warn.push({
              name: data[i].project,
              value: data[i].logCounts[j].docCount
            })
          }
        }
      }
      this.logCountPerLevel.setOption({
        title: [{
          top:'35',
          left: 'center',
          text: '分项目日志等级统计',
        },{
          text: 'ERROR',
          top:'15%',
          left: '22%',
        },{
          text: 'INFO',
          top:'15%',
          left: '72.5%',
        },{
          text: 'WARN',
          top:'55%',
          left: '22%',
        },{
          text: 'DEBUG',
          top:'55%',
          left: '72.5%',
        },],
        series: [{
          id: 'A',
          type: 'pie',
          radius: 60,
          center: ['25%', '35%'],
          data: error
        }, {
          id: 'B',
          type: 'pie',
          radius: 60,
          center: ['75%', '35%'],
          data: info
        }, {
          id: 'C',
          type: 'pie',
          radius: 60,
          center: ['25%', '75%'],
          data: warn
        }, {
          id: 'D',
          type: 'pie',
          radius: 60,
          center: ['75%', '75%'],
          data: debug
        }]})
    },
    getTopCount() {
      this.logCountTop = echarts.init(document.getElementById('logCountTop'));
      this.logCountTop.setOption({
        tooltip: {},
        title: [{
          top:'35',
          left: 'center',
          text: '项目统计情况',
        },{
          text: '日志等级',
          top:'15%',
          left: '22%',
        },{
          text: 'top5线程',
          top:'15%',
          left: '72.5%',
        },{
          text: 'top5类',
          top:'55%',
          left: '22%',
        },{
          text: 'top5异常',
          top:'55%',
          left: '72.5%',
        }],
        series: [{
          id: 'A',
          type: 'pie',
          radius: 60,
          center: ['25%', '35%'],
          data: []
        }, {
          id: 'B',
          type: 'pie',
          radius: 60,
          center: ['75%', '35%'],
          data: []
        }, {
          id: 'C',
          type: 'pie',
          radius: 60,
          center: ['25%', '75%'],
          data: []
        }, {
          id: 'D',
          type: 'pie',
          radius: 60,
          center: ['75%', '75%'],
          data: []
        }]
      });

      let threadCount = [];
      console.log(this.threadData[0].logCounts)
      for(let j = 0; j < this.threadData[0].logCounts.length; j++) {
        threadCount.push({
          name: this.threadData[0].logCounts[j].key,
          value: this.threadData[0].logCounts[j].docCount
        })
      }
      this.logCountTop.setOption({
        title: [{
          top:'35',
          left: 'center',
          text: '项目统计情况',
        },{
          text: '日志等级',
          top:'15%',
          left: '22%',
        },{
          text: 'top5线程',
          top:'15%',
          left: '72.5%',
        },{
          text: 'top5类',
          top:'55%',
          left: '22%',
        },{
          text: 'top5异常',
          top:'55%',
          left: '72.5%',
        }],
        series: [{
          id: 'A',
          type: 'pie',
          radius: 60,
          center: ['25%', '35%'],
          data: levelCount
        }, {
          id: 'B',
          type: 'pie',
          radius: 60,
          center: ['75%', '35%'],
          data: threadCount
        }, {
          id: 'C',
          type: 'pie',
          radius: 60,
          center: ['25%', '75%'],
          data: classCount
        }, {
          id: 'D',
          type: 'pie',
          radius: 60,
          center: ['75%', '75%'],
          data: errorCount
        }]})
      let classCount = []
      for(let j = 0; j < this.classData[0].logCounts.length; j++) {
        classCount.push({
          name: this.classData[0].logCounts[j].key,
          value: this.classData[0].logCounts[j].docCount
        })
      }
      this.logCountTop.setOption({
        title: [{
          top:'35',
          left: 'center',
          text: '项目统计情况',
        },{
          text: '日志等级',
          top:'15%',
          left: '22%',
        },{
          text: 'top5线程',
          top:'15%',
          left: '72.5%',
        },{
          text: 'top5类',
          top:'55%',
          left: '22%',
        },{
          text: 'top5异常',
          top:'55%',
          left: '72.5%',
        }],
        series: [{
          id: 'A',
          type: 'pie',
          radius: 60,
          center: ['25%', '35%'],
          data: levelCount
        }, {
          id: 'B',
          type: 'pie',
          radius: 60,
          center: ['75%', '35%'],
          data: threadCount
        }, {
          id: 'C',
          type: 'pie',
          radius: 60,
          center: ['25%', '75%'],
          data: classCount
        }, {
          id: 'D',
          type: 'pie',
          radius: 60,
          center: ['75%', '75%'],
          data: errorCount
        }]})
      let errorCount = []
      for(let j = 0; j < this.errorData[0].logCounts.length; j++) {
        errorCount.push({
          name:  this.errorData[0].logCounts[j].key,
          value:  this.errorData[0].logCounts[j].docCount
        })
      }
      this.logCountTop.setOption({
        title: [{
          top:'35',
          left: 'center',
          text: '项目统计情况',
        },{
          text: '日志等级',
          top:'15%',
          left: '22%',
        },{
          text: 'top5线程',
          top:'15%',
          left: '72.5%',
        },{
          text: 'top5类',
          top:'55%',
          left: '22%',
        },{
          text: 'top5异常',
          top:'55%',
          left: '72.5%',
        }],
        series: [{
          id: 'A',
          type: 'pie',
          radius: 60,
          center: ['25%', '35%'],
          data: levelCount
        }, {
          id: 'B',
          type: 'pie',
          radius: 60,
          center: ['75%', '35%'],
          data: threadCount
        }, {
          id: 'C',
          type: 'pie',
          radius: 60,
          center: ['25%', '75%'],
          data: classCount
        }, {
          id: 'D',
          type: 'pie',
          radius: 60,
          center: ['75%', '75%'],
          data: errorCount
        }]})
      let levelCount = [];
      for(let j = 0; j < this.level[0].logCounts.length; j++) {
        if(this.level[0].logCounts[j].key === "ERROR") {
          levelCount.push({
            name: this.level[0].logCounts[j].key,
            value: this.level[0].logCounts[j].docCount
          })
        }
        if(this.level[0].logCounts[j].key === "INFO") {
          levelCount.push({
            name: this.level[0].logCounts[j].key,
            value: this.level[0].logCounts[j].docCount
          })
        }
        if(this.level[0].logCounts[j].key === "DEBUG") {
          this.levelCount.push({
            name: this.level[0].logCounts[j].key,
            value: this.level[0].logCounts[j].docCount
          })
        }
        if(this.level[0].logCounts[j].key === "WARN") {
          levelCount.push({
            name: this.level[0].logCounts[j].key,
            value: this.level[0].logCounts[j].docCount
          })
        }
      }
      this.logCountTop.setOption({
        title: [{
          top:'35',
          left: 'center',
          text: '项目统计情况',
        },{
          text: '日志等级',
          top:'15%',
          left: '22%',
        },{
          text: 'top5线程',
          top:'15%',
          left: '72.5%',
        },{
          text: 'top5类',
          top:'55%',
          left: '22%',
        },{
          text: 'top5异常',
          top:'55%',
          left: '72.5%',
        }],
        series: [{
          id: 'A',
          type: 'pie',
          radius: 60,
          center: ['25%', '35%'],
          data: levelCount
        }, {
          id: 'B',
          type: 'pie',
          radius: 60,
          center: ['75%', '35%'],
          data: threadCount
        }, {
          id: 'C',
          type: 'pie',
          radius: 60,
          center: ['25%', '75%'],
          data: classCount
        }, {
          id: 'D',
          type: 'pie',
          radius: 60,
          center: ['75%', '75%'],
          data: errorCount
        }]})
      console.log(this.logCountTop);
    }
  }
}
</script>
