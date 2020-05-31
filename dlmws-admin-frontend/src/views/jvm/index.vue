<template>
  <div class="app-container">
    <div v-if="chartVisible">
      <jvm-monitor v-on:back="changeVisible(false)" :ip="ip" :pid="pid" height="800px" width="100%"></jvm-monitor>
    </div>
    <div v-if="!chartVisible">
      <el-row :gutter="0">
        <el-col :span="24">
          <el-form :inline="true"  size="mini" class="demo-form-inline">
            <el-form-item>
<!--              <el-button size="mini"  icon="el-icon-s-data" type="primary" @click="statistics">查看统计</el-button>-->
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>
      <el-row>
        <el-col>
          <el-table
            style="width: 100%"
            size="mini"
            v-loading="listLoading"
            :data="jvmList"
            element-loading-text="Loading"
            fit
            :row-class-name="tableRowClassNameJVM"
          >
            <el-table-column
              prop="ip"
              label="主机ip"
              sortable />
            <el-table-column
              prop="pid"
              label="pid"
              sortable />
            <el-table-column
              prop="name"
              label="进程名"
              sortable />
            <el-table-column
              sortable
              label="加入时间"
            >
              <template slot-scope="scope">
                {{ scope.row.addDate | dateFormat }}
              </template>
            </el-table-column>
            <el-table-column
              prop="owner"
              label="owner"
            />
            <el-table-column
              sortable
              label="状态"
            >
              <template slot-scope="scope">
                <el-tag size="mini" :type="scope.row.status | classFilter">{{ scope.row.status| statusFilter }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column align="center" width="80" label="操作" >
              <template slot-scope="scope">
                <el-button size="mini" icon="el-icon-search" circle @click="monitor(scope.row.ip, scope.row.pid)"></el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
  import moment from 'moment'
  import { list } from '../../api/jvm'
  import jvmMonitor from './jvmMonitor'
  export default {
    components:{ jvmMonitor },

    filters: {
      classFilter(status) {
        const statusMap = {
          0: 'info',
          1: 'success',
          2: 'danger',
          null: 'danger'
        }
        return statusMap[status]
      },
      statusFilter(status) {
        const statusMap = {
          1: '正常',
          0: '失联',
        }
        return statusMap[status]
      },
      dateFormat(date) {
        return moment(date).format("YYYY-MM-DD HH:mm:ss");
      }
    },
    data() {
      return {
        ip: '',
        pid: '',
        chartVisible: false,
        listLoading: true,
        jvmList: [],
      }
    },
    watch: {
    },
    created() {
      this.getJvmList();
    },
    methods: {
      changeVisible(bool) {
        this.chartVisible = bool;
      },
      tableRowClassNameJVM({row, rowIndex}) {
        console.log(row)
        if (row.status === 1) {
          return 'success-row';
        } else if (row.status === 0) {
          return 'warning-row';
        }

        if(rowIndex === 1) {
          return 'success-row';
        }
        return '';
      },
      addHost() {

      },
      statistics() {

      },
      monitor(ip, pid) {
        this.ip = ip;
        this.pid = pid;
        this.chartVisible = true;
      },
      getJvmList() {
        list({owner : 'yipingjian'}).then(res => {
          this.jvmList = res.data;
          this.listLoading = false
        })
      }
    }
  }
</script>

<style scoped>
  .el-table .warning-row {
    background: rgba(168, 153, 149, 0.27);
  }

  .el-table .success-row {
    background: #f0f9eb;
  }
</style>
