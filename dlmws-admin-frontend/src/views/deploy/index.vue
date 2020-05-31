<template>
  <div class="app-container">
    <div class="form">
      <el-form ref="form" :model="form" label-width="120px">
        <el-form-item label="项目名">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="机器IP">
          <el-input v-model="form.ip" placeholder="请输入机器ip"/>
        </el-form-item>
        <el-form-item label="密钥">
          <el-input v-model="form.secret" show-password/>
        </el-form-item>
        <el-form-item label="项目描述">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit">Create</el-button>
          <el-button @click="onCancel">Cancel</el-button>
        </el-form-item>
      </el-form>
    </div>

  </div>
</template>

<script>
  import axios from 'axios'

  export default {
    data() {
      return {
        form: {
          name: '',
          ip: '',
          description: '',
          secret:''
        }
      }
    },
    methods: {
      onSubmit() {
        axios
          .post('http://localhost:8088/project/add', this.form)
          .then( res => {
            if(res.status === 500 || res.code === 500) {
              this.$message.error("添加失败");
            }else {
              this.$message.success("添加成功");
            }
          })
          .catch(function (error) {
            console.log(error);
          });
      },
      onCancel() {
        this.$message({
          message: 'cancel!',
          type: 'warning'
        })
      }
    }
  }
</script>

<style scoped>
  .line{
    text-align: center;
  }
  .app-container .form{
    width: 60%;
  }
</style>

