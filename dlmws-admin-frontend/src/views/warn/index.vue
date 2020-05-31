<template>
  <div class="app-container">
    <div>
      <el-form :inline="true"  size="mini" class="demo-form-inline">
        <el-form-item label="项目" label-width="68px">
          <el-select v-model="warnVo.project" placeholder="请选择项目">
            <el-option
              v-for="item in projectList"
              :key="item"
              :label="item"
              :value="item"/>
          </el-select>
        </el-form-item>
        <el-form-item label="主机IP" label-width="68px">
          <el-input v-model="warnVo.ip" placeholder="请输入主机IP" style="width: 176px"></el-input>
        </el-form-item>
        <el-form-item label="报警关键字">
          <el-input v-model="warnVo.keyword" placeholder="keywords" style="width: 176px"></el-input>
        </el-form-item>
      </el-form>
      <el-form :inline="true" size="mini" class="demo-form-inline">
        <el-form-item label="报警类型">
          <el-select v-model="warnVo.type" placeholder="请选择项目">
            <el-option
              v-for="item in typeList"
              :key="item.label"
              :label="item.label"
              :value="item.value"/>
          </el-select>
        </el-form-item>
        <el-form-item label="钉钉状态">
          <el-select v-model="warnVo.dingTalkStatus" placeholder="请选择">
            <el-option
              v-for="item in sendStatusList"
              :key="item.label"
              :label="item.label"
              :value="item.value"/>
          </el-select>
        </el-form-item>
        <el-form-item label="邮件状态" label-width="82px">
          <el-select v-model="warnVo.emailStatus" placeholder="请选择">
            <el-option
              v-for="item in sendStatusList"
              :key="item.label"
              :label="item.label"
              :value="item.value"/>
          </el-select>
        </el-form-item>
      </el-form>
      <el-form :inline="true" size="mini" >
        <el-form-item label="发生时间" >
          <el-date-picker
            style="width: 176px"
            v-model="occuDate"
            type="datetimerange"
            :picker-options="pickerOptions"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="timestamp"
            align="right">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="报警时间">
          <el-date-picker
            style="width: 176px"
            v-model="warnDate"
            type="datetimerange"
            :picker-options="pickerOptions"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="timestamp"
            align="right">
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="getWarnRecordList">搜索</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="danger" icon="el-icon-delete" @click="clear">清空</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div>
      <el-table
        size="mini"
        v-loading="listLoading"
        :data="warnList"
        element-loading-text="Loading"
        fit
        highlight-current-row
      >
        <el-table-column type="expand">
          <template slot-scope="props">
            <el-form label-position="left" inline class="demo-table-expand">
              <el-form-item v-if="props.row.type ==='interval'" label="阈值">
                <span>{{ props.row.threshold }}</span>
              </el-form-item>
              <el-form-item v-if="props.row.type ==='interval'" label="周期">
                <span>{{ props.row.intervalTime }}</span>
              </el-form-item>
              <el-form-item  label="钉钉Id">
                <span>{{ props.row.dingTalkId }}</span>
              </el-form-item>
              <el-form-item  label="收件人">
                <span>{{ props.row.email }}</span>
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>
        <el-table-column
          prop="project"
          label="项目"
          sortable />
        <el-table-column
          prop="ip"
          label="ip"
          sortable />
        <el-table-column
          prop="keyword"
          label="关键字"
          sortable />
        <el-table-column
          label="类型">
          <template slot-scope="scope">
            {{ scope.row.type | typeFilter }}
          </template>
        </el-table-column>
        <el-table-column
          label="邮件状态"
        >
          <template slot-scope="scope">
            <el-tag size="mini" :type="scope.row.emailStatus | classFilter">{{ scope.row.emailStatus| statusFilter }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          label="钉钉状态"
        >
          <template slot-scope="scope">
            <el-tag size="mini" :type="scope.row.dingTalkStatus| classFilter">{{ scope.row.dingTalkStatus| statusFilter }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          label="报警时间"
          sortable >
          <template slot-scope="scope">
            {{ scope.row.warningTime | dateFormat }}
          </template>
        </el-table-column>
        <el-table-column align="center" width="140" label="操作" >
          <template slot-scope="scope">
            <div style="display: inline">
              <el-popover
                placement="bottom"
                width="400"
                trigger="click" >
                <span>
                  {{ scope.row.logText }}
                </span>
                <el-button slot="reference"  type="text" size="mini">查看日志</el-button>
              </el-popover>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div>
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[100, 200, 300, 400]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="totalCount">
      </el-pagination>
    </div>

  </div>
</template>

<script>
  import moment from 'moment'
  import { list } from '../../api/warn'

  export default {
    name: 'index',
    filters: {
      statusFilter(status) {
        const statusMap = {
          0: '用户取消',
          1: '发送成功',
          2: '发送失败',
          null: '发送失败',
        }
        return statusMap[status]
      },
      classFilter(status) {
        const statusMap = {
          0: 'info',
          1: 'success',
          2: 'danger',
          null: 'danger'
        }
        return statusMap[status]
      },
      typeFilter(status) {
        const statusMap = {
          'interval': '时间序列阈值',
          'immediate': '瞬时阈值',
        }
        return statusMap[status]
      },
      dateFormat(date) {
        return moment(date).format("YYYY-MM-DD HH:mm:ss");
      }
    },
    data() {
      return {
        listLoading: true,
        dialogFormVisible: false,
        editable: false,
        typeList:[
          {"label": "瞬时阈值报警", "value":"immediate"},
          {"label": "时间序列阈值报警", "value":"interval"}
        ],
        sendStatusList:[
          {"label": "用户取消", "value":0},
          {"label": "发送成功", "value":1},
          {"label": "发送失败", "value":2}
        ],
        projectList: [],
        warnList: [],
        pageSize: 100,
        currentPage: 1,
        totalPage: 0,
        totalCount: 0,
        warnVo: {
          project: '',
          keyword: '',
          ip:'',
          type:'',
          dingTalkStatus: null,
          emailStatus: null,
          occurredTime: '',
          warningTime: '',
        },
        occuDate:[],
        warnDate:[],
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
      }
    },
    created() {
      this.project = ["dlmws-log", "dlmws-agent"]
      this.getWarnRecordList();
    },
    methods: {
      getWarnRecordList(){
        if(this.occuDate.length !== 0) {
          this.warnVo.occurredTime = this.occuDate[0] + "," + this.occuDate[1];
        }
        if(this.warnDate.length !== 0) {
          this.warnVo.warningTime = this.warnDate[0] + "," + this.warnDate[1];
        }
        this.warnVo.pageSize = this.pageSize;
        this.warnVo.currentPage = this.currentPage;
        list(this.warnVo).then(res => {
          this.listLoading = false;
          this.warnList = res.data.list;
          this.totalCount = res.data.totalCount;
          this.totalPage = res.data.totalPage;
          this.currentPage = res.data.currentPage;
        })
      },
      handleSizeChange(value) {
        this.pageSize = value;
        this.currentPage = 1;
        this.getWarnRecordList();
      },
      handleCurrentChange(value) {
        this.currentPage = value;
        this.getWarnRecordList();
      },
      clear() {
        this.warnVo = {
          project: '',
            keyword: '',
            ip:'',
            type:'',
            dingTalkStatus: null,
            emailStatus: null,
            occurredTime: '',
            warningTime: '',
        }
      }
    }
  }
</script>

<style scoped>

</style>
