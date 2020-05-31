<template>
  <div class="app-container">
    <div v-if="chartVisible">
      <chart-monitor v-on:back="changeVisible(false)" height="800px" width="100%" :ip="ip" />
    </div>
    <div v-if="!chartVisible">
      <el-row :gutter="0">
        <el-col :span="24">
          <el-form :inline="true"  size="mini" class="demo-form-inline">
            <el-form-item>
              <el-button size="mini" icon="el-icon-s-data"  type="primary" @click="addHost">添加主机</el-button>
            </el-form-item>
<!--            <el-form-item>-->
<!--              <el-button size="mini"  icon="el-icon-search" type="success" @click="statistics">查看统计</el-button>-->
<!--            </el-form-item>-->
          </el-form>
        </el-col>
      </el-row>
      <el-row>
        <el-col>
          <el-table
            size="mini"
            v-loading="listLoading"
            :data="hostList"
            element-loading-text="Loading"
            fit
            :row-class-name="tableRowClassName"
            highlight-current-row
          >
            <el-table-column
              prop="id"
              label="主机id"
              sortable />
            <el-table-column
              prop="ip"
              label="主机ip"
              sortable />
            <el-table-column
              prop="name"
              label="主机名"
              sortable />
            <el-table-column
              prop="projects"
              label="部署服务"
            />
            <el-table-column
              prop="owner"
              label="owner"
            />
            <el-table-column
              label="状态"
            >
              <template slot-scope="scope">
                {{ scope.row.status | statusFilter }}
              </template>
            </el-table-column>
            <el-table-column align="center" width="80" label="操作" >
              <template slot-scope="scope">
                <el-button size="mini" icon="el-icon-search" circle @click="monitor(scope.row.ip)"></el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
      </el-row>
    </div>
    <div>
      <el-dialog title="添加主机" :visible.sync="dialogFormVisible">
        <el-form :model="host">
          <el-form-item label="ip" >
            <el-input v-model="host.ip" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="name" >
            <el-input v-model="host.name" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="密钥" >
            <el-input v-model="host.secret" autocomplete="off"></el-input>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取 消</el-button>
          <el-button type="primary" @click="submit">确 定</el-button>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>

import { list } from '../../api/host'
import moment from 'moment'
import chartMonitor from './chartMonitor'
import axios from 'axios'

export default {
  components: { chartMonitor },
  filters: {
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
      dialogFormVisible: false,
      ip: '',
      chartVisible: false,
      listLoading: true,
      hostList: [],
      host:{
        ip:'',
        name: '',
        secret:''
      },
    }
  },
  watch: {
  },
  created() {
    this.getHostList();
  },
  methods: {
    tableRowClassName({row, rowIndex}) {
      if (row.status === 1) {
        return 'success-row';
      } else if (rowIndex === 0) {
        return 'warning-row';
      }
      return '';
    },
    addHost() {
      this.dialogFormVisible = true;
    },
    statistics() {

    },
    monitor(ip) {
      this.ip = ip;
      this.chartVisible = true;
    },
    getHostList() {
      list({owner : 'yipingjian'}).then(res => {
        this.hostList = res.data;
        this.listLoading = false
      })
    },
    changeVisible(bool) {
      this.chartVisible = bool;
    },
    submit() {
      axios
        .post('http://localhost:8088/host/add', this.host)
        .then( res => {
          if(res.data.code === 200) {
            this.$message.success("添加成功");
            this.dialogFormVisible = false;
            this.getHostList();
          }else {
            this.dialogFormVisible = false;
            this.$message.error("添加失败");
          }
        })
        .catch(function (error) {
          console.log(error);
        });
    }
  }
}
</script>
<style scoped>
  .el-table .warning-row {
    background: #ff8922;
  }

  .el-table .success-row {
    background: #f0f9eb;
  }
</style>
