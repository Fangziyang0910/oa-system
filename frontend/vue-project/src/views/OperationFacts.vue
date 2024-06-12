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
        <div class="summary-cards">
          <el-card class="summary-card" v-for="(value, key) in summaryInfo" :key="key">
            <div slot="header">{{ key }}</div>
            <div>{{ value }}</div>
          </el-card>
        </div>
        <el-table :data="paginatedProcessDetails" style="width: 100%">
          <el-table-column prop="流程名称" label="流程名称"></el-table-column>
          <el-table-column prop="已经超时的任务数量" label="已经超时的任务数量"></el-table-column>
          <el-table-column prop="运行中的流程实例数量" label="运行中的流程实例数量"></el-table-column>
          <el-table-column prop="未完成的操作任务数量" label="未完成的操作任务数量"></el-table-column>
          <el-table-column prop="未完成的审批任务数量" label="未完成的审批任务数量"></el-table-column>
        </el-table>
        <div class="pagination-controls">
          <el-pagination
            background
            layout="prev, pager, next"
            :total="processDetails.length"
            :page-size="pageSize"
            @current-change="handlePageChange">
          </el-pagination>
        </div>
        <div class="refresh-controls">
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

.aside {
  background-color: #eef1f6;
}

.main-container {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  font-size: 14px;
  background-color: #b3c0d1;
  color: #333;
  line-height: 60px;
}

.main-content {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  flex: 1;
  padding: 20px;
}

.pagination-controls {
  display: flex;
  justify-content: center;
  margin-top: 20px;
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
      logoutDialogVisible: false,
      summaryInfo: {},
      processDetails: [],
      currentPage: 1,
      pageSize: 5,
      refreshCountdown: 300,
      refreshInterval: null,
    };
  },
  computed: {
    paginatedProcessDetails() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return this.processDetails.slice(start, end);
    }
  },
  created() {
    this.fetchAdminInfo();
    this.startAutoRefresh();
  },
  beforeDestroy() {
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
    handlePageChange(page) {
      this.currentPage = page;
    },
    startAutoRefresh() {
      this.refreshInterval = setInterval(() => {
        if (this.refreshCountdown > 0) {
          this.refreshCountdown--;
        } else {
          this.refreshInfo();
        }
      }, 1000);
    },
    resetCountdown() {
      this.refreshCountdown = 300;
    }
  }
};
</script>
