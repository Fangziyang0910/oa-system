<template>
    <el-container class="full-height-container">
      <el-aside width="200px" style="background-color: rgb(238, 241, 246)">
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
            <router-link to="/processManagement">
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
          <div class="summary-cards">
            <el-card class="summary-card" v-for="(value, key) in summaryInfo" :key="key">
              <div slot="header">{{ key }}</div>
              <div>{{ value }}</div>
            </el-card>
          </div>
          <el-table :data="processDetails" style="width: 100%">
            <el-table-column prop="流程名称" label="流程名称"></el-table-column>
            <el-table-column prop="已经超时的任务数量" label="已经超时的任务数量"></el-table-column>
            <el-table-column prop="运行中的流程实例数量" label="运行中的流程实例数量"></el-table-column>
            <el-table-column prop="未完成的操作任务数量" label="未完成的操作任务数量"></el-table-column>
            <el-table-column prop="未完成的审批任务数量" label="未完成的审批任务数量"></el-table-column>
          </el-table>
          <div class="refresh-controls">
            <el-button type="primary" @click="refreshInfo">刷新</el-button>
            <span>下一次自动刷新: {{ refreshCountdown }} 秒</span>
          </div>
        </el-main>
      </el-container>
  
      <el-dialog
        title="确认注销"
        :visible.sync="logoutDialogVisible"
        width="30%">
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

.main-container {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.header {
  text-align: right;
  font-size: 12px;
  background-color: #b3c0d1;
  color: #333;
  line-height: 60px;
}

.el-aside {
  color: #333;
}

.main-content {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  flex: 1;
  padding: 20px;
}

.refresh-controls {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.summary-cards {
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 20px;
}

.summary-card {
  flex: 1;
  margin: 10px;
  min-width: 200px;
}

.user-info-card {
  width: 80%;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-radius: 10px;
}
</style>
<script>
export default {
  data() {
    return {
      username: localStorage.getItem('username') || '管理员',
      logoutDialogVisible: false,
      summaryInfo: {},
      processDetails: [],
      refreshCountdown: 300, // 初始倒计时时间为300秒，即5分钟
      refreshInterval: null, // 用于存储setInterval的ID
    };
  },
  created() {
    this.fetchAdminInfo();
    this.startAutoRefresh();
  },
  beforeDestroy() {
    // 清除定时器以避免内存泄漏
    clearInterval(this.refreshInterval);
  },
  methods: {
    logout() {
      this.logoutDialogVisible = true;
    },
    confirmLogout() {
      localStorage.removeItem('username');
      localStorage.removeItem('token');
      localStorage.removeItem('isAdmin');
      localStorage.setItem('isLoggedIn', 'false');
      this.$router.push({ name: 'Login' });
    },
    fetchAdminInfo() {
      fetch('http://139.199.168.63:8080/admin/getInfo', {
        headers: {
          'Authorization': localStorage.getItem('token'),
          'Accept': 'text/plain'
        }
      })
      .then(response => response.json())
      .then(data => {
        if (data && data.code === 0) {
          const parsedData = JSON.parse(data.data);
          this.summaryInfo = parsedData.汇总信息;
          this.processDetails = parsedData.流程详细信息;
        } else {
          this.$message.error('获取实况信息失败');
        }
      })
      .catch(error => {
        console.error('Error fetching admin info:', error);
        this.$message.error('获取实况信息失败');
      });
    },
    refreshInfo() {
      this.fetchAdminInfo();
      this.resetCountdown();
    },
    startAutoRefresh() {
      this.refreshInterval = setInterval(() => {
        if (this.refreshCountdown > 0) {
          this.refreshCountdown--;
        } else {
          this.refreshInfo();
        }
      }, 1000); // 每秒更新一次倒计时
    },
    resetCountdown() {
      this.refreshCountdown = 300; // 重置倒计时为300秒
    }
  }
};
</script>
  