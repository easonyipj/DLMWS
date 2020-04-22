<template>
  <div class="app-container">
    <div >
      <el-row :gutter="0">
        <el-col :span="24">
          <el-form :inline="true"  size="mini" class="demo-form-inline">
            <el-form-item>
              <el-button size="mini" icon="el-icon-circle-plus"  type="primary" @click="addRule">添加规则</el-button>
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>
      <el-row>
        <el-col>
          <el-table
            size="mini"
            v-loading="listLoading"
            :data="ruleList"
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
              label="状态"
            >
              <template slot-scope="scope">
                <el-tag size="mini" :type="scope.row.status| classFilter">{{ scope.row.status| statusFilter }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column
              prop="owner"
              label="owner"
            />
            <el-table-column align="center" width="160" label="操作" >
              <template slot-scope="scope">
                <div style="display: inline">
                  <el-button type="text" size="mini" @click="edit(scope.row)">编辑</el-button>
                  <el-button type="text" size="mini" @click="deleteRule(scope.row)">删除</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
      </el-row>
    </div>
    <div>
      <el-dialog title="添加规则" :visible.sync="dialogFormVisible">
        <el-form :inline="true" size="mini">
          <el-form-item label="项目" >
            <el-input v-model="rule.project" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="关键字" >
            <el-input v-model="rule.keyword" autocomplete="off"></el-input>
          </el-form-item>
        </el-form>
        <el-form :inline="true" size="mini">
          <el-form-item label="类型" >
            <el-select v-model="rule.type" placeholder="请选择">
              <el-option
                v-for="item in typeList"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item v-if="rule.type==='interval'" label="阈值">
            <el-input v-model="rule.threshold" ></el-input>
          </el-form-item>
          <el-form-item v-if="rule.type==='interval'" label="周期">
            <el-input v-model="rule.intervalTime" ></el-input>
          </el-form-item>
        </el-form>
        <el-form size="mini">
          <el-form-item label="通知钉钉Id">
            <el-input v-model="rule.dingTalkId" placeholder="请输入内容"></el-input>
          </el-form-item>
          <el-form-item label="通知email">
            <el-input v-model="rule.email" placeholder="请输入内容"></el-input>
          </el-form-item>
        </el-form>
        <el-form :inline="true" size="mini">
          <el-form-item label="状态">
            <el-radio v-model="rule.status" :label=true >激活</el-radio>
            <el-radio v-model="rule.status" :label=false >关闭</el-radio>
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
  import { list } from '../../api/rule'
  import moment from 'moment'
  import axios from 'axios'

  export default {
    filters: {
      statusFilter(status) {
        const statusMap = {
          true: '激活',
          false: '关闭',
        }
        return statusMap[status]
      },
      classFilter(status) {
        const statusMap = {
          true: 'success',
          false: 'info'
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
    name: 'index',
    data() {
      return {
        listLoading: true,
        dialogFormVisible: false,
        editable: false,
        typeList:[
          {"label": "瞬时阈值报警", "value":"immediate"},
          {"label": "时间序列阈值报警", "value":"interval"}
          ],
        ruleList: [],
        rule:{
          project: '',
          keyword: '',
          type: '',
          threshold: 0,
          intervalTime: 0,
          dingTalkId: '',
          email:'',
          status: true,
        },
      }
    },
    created() {
      this.getRuleList();
    },
    methods: {
      addRule() {
        this.dialogFormVisible = true;
      },
      getRuleList() {
        list().then(res => {
          this.ruleList = res.data;
          this.listLoading = false
        })
      },
      submit() {
        let api = '/add';
        if(this.editable === true) {
          api = '/update'
        }
        if(this.rule.type === 'immediate') {
          this.rule.threshold = 0;
          this.rule.intervalTime = 0;
        }
        axios
        .post('http://localhost:8088/rule' + api, this.rule)
        .then( res => {
          if(res.status === 500 || res.code === 500) {
            this.$message.error("添加失败");
          }else {
            this.$message.success("添加成功");
          }
          console.log(res)
          this.dialogFormVisible = false;
          this.rule = {};
          this.editable = false;
          this.getRuleList();
        })
        .catch(function (error) {
            console.log(error);
        });
      },
      edit(rule) {
        this.rule = rule;
        this.editable = true;
        this.dialogFormVisible = true;
      },
      deleteRule(rule){
        this.$alert('确认删除这条规则？', '确认删除', {
          confirmButtonText: '确定',
          callback: action => {
            axios
              .post('http://localhost:8088/rule/delete', rule)
              .then( res => {
                if(res.status === 500 || res.data.code === 500) {
                  this.$message.error("删除失败");
                }else {
                  this.$message.success("删除成功");
                }
                this.getRuleList();
              })
              .catch(function (error) {
                console.log(error);
              });
          }
        });
      }
    }
  }
</script>

<style scoped>

</style>
