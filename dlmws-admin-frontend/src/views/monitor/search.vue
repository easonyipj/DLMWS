<template>
  <div>
    <el-form :inline="true"  size="mini" class="demo-form-inline">
      <el-form-item label="日志类型">
        <el-select v-model="logType" placeholder="请选择日志类型">
          <el-option
            v-for="item in logTypeList"
            :key="item"
            :label="item"
            :value="item"/>
        </el-select>
      </el-form-item>
      <el-form-item label="项目">
        <el-select v-model="tomcatVo.project" placeholder="项目">
          <el-option
            v-for="item in projectList"
            :key="item"
            :label="item"
            :value="item"/>
        </el-select>
      </el-form-item>
      <el-form-item label="hostName">
        <el-input v-model="tomcatVo.hostName" placeholder="请输入主机名称"></el-input>
      </el-form-item>
      <el-form-item label="agentId">
        <el-input v-model="tomcatVo.agentId" placeholder="agentId"></el-input>
      </el-form-item>
    </el-form>
    <el-form :inline="true" size="mini" class="demo-form-inline">
      <el-form-item label="日志等级">
        <el-select v-model="tomcatVo.level" placeholder="日志等级">
          <el-option label="INFO" value="INFO"></el-option>
          <el-option label="ERROR" value="ERROR"></el-option>
          <el-option label="WARN" value="WARN"></el-option>
          <el-option label="DEBUG" value="DEBUG"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="   PID">
        <el-input v-model="tomcatVo.pid" placeholder="Java 进程 Id"></el-input>
      </el-form-item>
      <el-form-item label="Java类">
        <el-input v-model="tomcatVo.classPosition" placeholder="classPosition"></el-input>
      </el-form-item>
      <el-form-item label="Java线程">
        <el-input v-model="tomcatVo.threadPosition" placeholder="threadPosition"></el-input>
      </el-form-item>
    </el-form>
    <el-form :inline="true" size="mini" class="demo-form-inline">
      <el-form-item label="日志内容">
        <el-input v-model="tomcatVo.logMessage" placeholder="内容关键字"></el-input>
      </el-form-item>
      <el-form-item label="异常内容">
        <el-input v-model="tomcatVo.errorType" placeholder="错误堆栈和错误类型信息"></el-input>
      </el-form-item>
      <el-form-item label="发生时间">
        <el-date-picker
          v-model="date"
          type="datetimerange"
          :picker-options="pickerOptions"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="timestamp"
          align="right">
        </el-date-picker>
      </el-form-item>
    </el-form>
    <el-form :inline="true" size="mini" class="demo-form-inline">
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="submit">搜索</el-button>
      </el-form-item>
      <el-form-item>
        <el-button type="danger" icon="el-icon-delete" @click="clearForm">清空</el-button>
      </el-form-item>
      <el-form-item>
        <el-button type="info" icon="el-icon-back" v-on:click="$emit('back')">返回</el-button>
      </el-form-item>
    </el-form>
    <el-table
      size="mini"
      v-loading="listLoading"
      :data="tomcatLogList"
      element-loading-text="Loading"
      border
      fit
      :default-sort = "{prop: 'occurredTime', order: 'descending'}"
      highlight-current-row
    >
      <el-table-column
        label="日期"
        sortable
        width="150">
        <template slot-scope="scope">
          {{ scope.row.occurredTime | dateFormat }}
        </template>
      </el-table-column>
      <el-table-column class-name="status-col" label="level" width="80" align="center">
        <template slot-scope="scope">
          <el-tag size="mini" :type="scope.row.level | statusFilter">{{ scope.row.level }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="pid"
        label="pid"
        sortable />
      <el-table-column
        prop="classPosition"
        label="Java类"
        sortable />
      <el-table-column
        prop="threadPosition"
        label="Java线程"
        sortable />
      <el-table-column
        prop="logMessage"
        label="日志内容"
      />
      <el-table-column align="center" width="80" label="异常信息" >
        <template slot-scope="scope">
          <div v-if="scope.row.level === 'ERROR'">
            <el-tooltip placement="top">
              <div slot="content">{{scope.row.errorType}}<br/>{{scope.row.stacktrace}}</div>
              <el-button size="mini" icon="el-icon-search" circle></el-button>
            </el-tooltip>
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
  import moment from 'moment'
  import { search } from '../../api/tomcat'

  export default {
    name: 'search',
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
        projectList: [],
        logTypeList: [],
        logType:'',
        from: 1,
        size: 50,
        tomcatLogList :[],
        tomcatVo: {
          logType: '',
          project: '',
          hostName: '',
          agentId: '',
          level: '',
          logMessage: '',
          pid: '',
          classPosition: '',
          threadPosition: '',
          errorType:'',
          stacktrace:''
        },
        date:[],
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
        list: null,
        listLoading: true
      }
    },
    created() {
      // TODO 获取用户的项目
      this.projectList = ['dlmws-log', 'dlmws-agent'];
      this.logTypeList = ['tomcat', 'mysql'];
      this.tomcatVo.project = this.projectList[0];
      this.logType = this.logTypeList[0];
      this.fetchData()
    },
    methods: {
      fetchData() {
        this.submit();
      },
      clearForm() {
        this.date = '';
        this.tomcatVo = {};
      },
      submit() {
        this.tomcatVo.from = this.from;
        this.tomcatVo.size = this.size;
        this.tomcatVo.stacktrace = this.tomcatVo.errorType;
        if(this.date.length !== 0) {
          this.tomcatVo.occurredTime = this.date[0] + "," + this.date[1];
        }
        console.log(this.tomcatVo)
        this.listLoading = true;
        search(this.tomcatVo).then(rep => {
          this.tomcatLogList = rep.data;
          console.log(this.tomcatLogList);
          this.listLoading = false;
        });
      }
    }
  }
</script>

<style scoped>

</style>
