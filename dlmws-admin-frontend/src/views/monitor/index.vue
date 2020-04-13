<template>
  <div class="app-container">
    <div v-if="showSearch===false">
      <el-row :gutter="0">
        <el-col :span="24">
          <el-form :inline="true"  size="mini" class="demo-form-inline">
            <el-form-item label="项目">
              <el-select size="mini" v-model="project" placeholder="请选择项目">
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




    </div>
    <search v-on:back="changeSearchVisible(false)"  v-if="showSearch"></search>
  </div>
</template>

<script>

import moment from 'moment'
import Search from './search'
import { logCount } from '../../api/tomcat'
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
      xTime:[],
      project:'',
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
      listLoading: true
    }
  },
  created() {
    this.projectList = ['dlmws-log', 'dlmws-agent'];
  },
  methods: {
    changeSearchVisible(bool) {
      this.showSearch = bool;
    },

    getStatistics() {
      // 将时间转换成ISO标准时间
      let st = this.date[0];
      let ed = this.date[1];
      let start = moment(new Date(st), 'X').utc(8).format('YYYY-MM-DDTHH:mm:ss') + '.000';
      let end = moment(new Date(ed), 'X').utc(8).format('YYYY-MM-DDTHH:mm:ss') + '.000';
      let params = {
        projects: this.projectList,
        from: start,
        to: end
      }
      logCount(JSON.stringify(params)).then(res => {
        this.getTimeDate(res.data)
      })
      //
      // this.getTimeDate();
    },
    getTimeDate(data) {
      // 起始时间
      let st = this.date[0].getTime() - this.date[0].getSeconds() * 1000;
      // 项目个数
      let size = 2;
      // 初始化下标、数据值
      let index = [];
      let value = [];
      for(let i = 0; i < size; i++) {
        index.push(0);
        value.push([]);
      }
      for(let t = st; t < this.date[1].getTime(); t += 60 * 1000) {
        let time = moment(t).format("YYYY-MM-DD HH:mm:ss");
        this.xTime.push(time);
        for(let i = 0; i <size; i++) {
          let key = '';
          if(index[i] < data[i].logCounts.length) {
            key = moment(data[i].logCounts[index[i]].key + 8 * 3600 * 1000).format("YYYY-MM-DD HH:mm:ss");
          }
          if(key === time) {
            value[i].push(data[i].logCounts[index[i]].docCount);
            index[i] += 1;
          }else {
            value[i].push(0);
          }
          // console.log(key);
          // console.log(data[i].logCounts[index[i]].docCount);
        }
      }
      console.log(this.xTime);
      for(let i = 0; i < size; i++) {
        console.log(value[i]);
      }

    }

  }
}
</script>
