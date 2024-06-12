<template>
    <el-container class="full-height-container">
      <el-aside width="200px" class="aside">
        <el-menu>
          <el-menu-item index="1">
            <router-link to="/processManagement">
              <i class="el-icon-message"></i> 流程管理
            </router-link>
          </el-menu-item>
          <el-menu-item index="2">
            <router-link to="/operationFacts">
              <i class="el-icon-message"></i> 运维实况
            </router-link>
          </el-menu-item>
          <el-menu-item index="3">
            <router-link to="/operationReport">
              <i class="el-icon-message"></i> 运维报告
            </router-link>
          </el-menu-item>
          <el-menu-item index="4">
            <router-link to="/adminCenter">
              <i class="el-icon-message"></i> 个人中心
            </router-link>
          </el-menu-item>
        </el-menu>
      </el-aside>
  
      <el-container class="main-container">
        <el-header class="header">
          <span>{{ username }}</span>
          <el-button type="text" @click="logout">注销</el-button>
        </el-header>
  
        <el-main class="main-content">
          <el-card class="user-info-card">
            <h2>个人信息</h2>
            <p><strong>用户名:</strong> {{ username }}</p>
            <p><strong>邮箱:</strong> {{ email }}</p>
            <p><strong>电话:</strong> {{ phone }}</p>
          </el-card>
        </el-main>
      </el-container>
  
      <el-dialog title="确认注销" :visible.sync="logoutDialogVisible" width="30%">
        <span>您确定要注销吗？</span>
        <span slot="footer" class="dialog-footer">
          <el-button @click="logoutDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmLogout">确认</el-button>
        </span>
      </el-dialog>
    </el-container>
  </template>
  
  <style scoped>
  .full-height-container {
    height: 100vh;
    display: flex;
  }
  
  .aside {
    background-color: rgb(238, 241, 246);
  }
  
  .main-container {
    display: flex;
    flex-direction: column;
    flex: 1;
  }
  
  .header {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    padding: 0 20px;
    font-size: 14px;
    background-color: #b3c0d1;
    color: #333;
    line-height: 60px;
  }
  
  .main-content {
    display: flex;
    justify-content: center;
    align-items: center;
    flex: 1;
    padding: 20px;
    background-color: #f5f5f5;
  }
  
  .user-info-card {
    width: 100%;
    max-width: 600px;
    padding: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    border-radius: 10px;
    background-color: #fff;
  }
  
  .user-info-card h2 {
    margin-bottom: 20px;
    font-size: 18px;
    color: #303133;
  }
  
  .user-info-card p {
    margin: 10px 0;
    font-size: 16px;
    color: #606266;
  }
  
  .dialog-footer {
    text-align: right;
  }
  
  .el-button {
    margin: 0 8px;
  }
  </style>
  
  <script>
  export default {
    data() {
      return {
        username: localStorage.getItem('username') || '管理员',
        email: localStorage.getItem('email') || '',
        phone: localStorage.getItem('phone') || '',
        logoutDialogVisible: false
      };
    },
    methods: {
      logout() {
        this.logoutDialogVisible = true;
      },
      confirmLogout() {
        localStorage.clear();
        this.$router.push({ name: 'Login' });
      }
    }
  };
  </script>
  