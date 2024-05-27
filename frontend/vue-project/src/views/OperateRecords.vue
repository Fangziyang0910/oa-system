<template>
  <el-container class="full-height-container">
    <el-aside width="200px" style="background-color: rgb(238, 241, 246)">
      <el-menu :default-openeds="['1']">
        <el-submenu index="1">
          <template slot="title"><i class="el-icon-message"></i>申请记录</template>
          <el-menu-item index="1-1">
            <router-link to="/currentApplication">当前申请</router-link>
          </el-menu-item>
          <el-menu-item index="1-2">
            <router-link to="/applicationRecords">历史申请</router-link>
          </el-menu-item>
        </el-submenu>
        <el-menu-item index="2">
          <template slot="title">
            <i class="el-icon-message"></i>
            <router-link to="/accountManage">账户管理</router-link>
          </template>
        </el-menu-item>
        <el-menu-item v-if="userInfo.isApplicant" index="3">
          <template slot="title">
            <i class="el-icon-message"></i>
            <router-link to="/applyScene">申请页面</router-link>
          </template>
        </el-menu-item>
        <el-submenu v-if="userInfo.isApprover" index="4">
          <template slot="title"><i class="el-icon-message"></i>审核记录</template>
          <el-menu-item index="4-1">
            <router-link to="/ReviewPage">当前审核</router-link>
          </el-menu-item>
          <el-menu-item index="4-2">
            <router-link to="/ReviewRecords">历史审核</router-link>
          </el-menu-item>
        </el-submenu>
        <el-submenu v-if="userInfo.isOperator" index="5">
          <template slot="title"><i class="el-icon-message"></i>运维记录</template>
          <el-menu-item index="5-1">
            <router-link to="/operateRecords">历史运维</router-link>
          </el-menu-item>
          <el-menu-item index="5-2">
            <router-link to="/operationMaintenance">未完成任务</router-link>
          </el-menu-item>
          <el-menu-item index="5-3">
            <router-link to="/missionPool">任务池</router-link>
          </el-menu-item>
        </el-submenu>
      </el-menu>
    </el-aside>

    <el-container class="main-container">
      <el-header class="header">
        <span>{{ userInfo.name }}</span>
        <el-button type="text" @click="logout">注销</el-button>
      </el-header>

      <el-main class="main-content">
        <el-card class="user-info-card">
          <el-table :data="paginatedTasks" style="width: 100%" @row-click="handleRowClick">
            <el-table-column prop="taskName" label="任务名称" width="180"></el-table-column>
            <el-table-column prop="description" label="描述" width="180"></el-table-column>
            <el-table-column prop="starterName" label="发起人" width="180"></el-table-column>
            <el-table-column prop="assigneeName" label="受理人" width="180"></el-table-column>
            <el-table-column prop="endTime" label="结束时间" width="180"></el-table-column>
          </el-table>
          <el-pagination
            @current-change="handleCurrentChange"
            :current-page="currentPage"
            :page-size="pageSize"
            layout="total, prev, pager, next"
            :total="totalTasks"
          >
          </el-pagination>
        </el-card>

        <el-dialog title="任务详细信息" :visible.sync="taskDialogVisible" width="50%">
          <div v-if="taskDetails">
            <el-form :model="taskDetails">
              <el-form-item label="任务名称">
                <el-input v-model="taskDetails.taskName" readonly></el-input>
              </el-form-item>
              <el-form-item label="描述">
                <el-input v-model="taskDetails.description" readonly></el-input>
              </el-form-item>
              <el-form-item label="发起人">
                <el-input v-model="taskDetails.starterName" readonly></el-input>
              </el-form-item>
              <el-form-item label="受理人">
                <el-input v-model="taskDetails.assigneeName" readonly></el-input>
              </el-form-item>
              <el-form-item label="结束时间">
                <el-input v-model="taskDetails.endTime" readonly></el-input>
              </el-form-item>
              <el-button type="primary" @click="showTaskForm(taskDetails.taskId)">查看申请工单</el-button>
              <el-button type="primary" @click="showProcessProgress(taskDetails.taskId)">查看流程进度</el-button>
            </el-form>
          </div>
          <span slot="footer" class="dialog-footer">
            <el-button @click="taskDialogVisible = false">关闭</el-button>
          </span>
        </el-dialog>

        <el-dialog title="流程进度" :visible.sync="processDialogVisible" width="50%">
          <el-table :data="processProgress" style="width: 100%">
            <el-table-column prop="taskName" label="任务名称" width="180"></el-table-column>
            <el-table-column prop="assigneeName" label="执行人" width="180"></el-table-column>
            <el-table-column prop="description" label="描述" width="180"></el-table-column>
            <el-table-column prop="endTime" label="结束时间" width="180"></el-table-column>
          </el-table>
          <span slot="footer" class="dialog-footer">
            <el-button @click="processDialogVisible = false">关闭</el-button>
          </span>
        </el-dialog>
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

<style>
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
  justify-content: center;
  align-items: flex-start;
  flex: 1;
  padding: 20px;
}

.user-info-card {
  width: 100%;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-radius: 10px;
}
</style>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      userInfo: {},
      completedTasks: [],
      currentPage: 1,
      pageSize: 10,
      totalTasks: 0,
      taskDetails: null,
      taskDialogVisible: false,
      processProgress: [],
      processDialogVisible: false,
      logoutDialogVisible: false
    };
  },
  computed: {
    paginatedTasks() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return this.completedTasks.slice(start, end);
    }
  },
  created() {
    this.loadUserInfoFromLocalStorage();
    this.fetchCompletedTasks();
  },
  methods: {
    loadUserInfoFromLocalStorage() {
      const userInfo = JSON.parse(localStorage.getItem('userInfo'));
      if (userInfo) {
        this.userInfo = userInfo;
      } else {
        alert('用户未登录，请先登录');
        this.$router.push({ name: 'Login' });
      }
    },
    fetchCompletedTasks() {
      axios.get('http://139.199.168.63:8080/operator/listOperatorTasksCompleted', {
        headers: {
          Authorization: localStorage.getItem('token'),
          'Accept': 'application/json'
        }
      })
      .then(response => {
        if (response.data.code === 0) {
          this.completedTasks = response.data.data;
          this.totalTasks = this.completedTasks.length;
        } else {
          this.$message.error('获取已完成任务失败: ' + response.data.msg);
        }
      })
      .catch(error => {
        console.error('Error fetching completed tasks:', error);
        this.$message.error('获取已完成任务失败');
      });
    },
    handleCurrentChange(page) {
      this.currentPage = page;
    },
    handleRowClick(row) {
      this.fetchTaskDetails(row.taskId);
    },
    fetchTaskDetails(taskId) {
      axios.get(`http://139.199.168.63:8080/operator/getTaskCompleted/${taskId}`, {
        headers: {
          Authorization: localStorage.getItem('token'),
          'Accept': 'application/json'
        }
      })
      .then(response => {
        if (response.data.code === 0) {
          this.taskDetails = response.data.data;
          this.taskDialogVisible = true;
        } else {
          this.$message.error('获取任务详情失败: ' + response.data.msg);
        }
      })
      .catch(error => {
        console.error('Error fetching task details:', error);
        this.$message.error('获取任务详情失败');
      });
    },
    showTaskForm(taskId) {
      axios.get(`http://139.199.168.63:8080/operator/getHistoricalForm/${taskId}`, {
        headers: {
          Authorization: localStorage.getItem('token'),
          'Accept': 'application/json'
        }
      })
      .then(response => {
        if (response.data.code === 0) {
          const form = response.data.data;
          this.$alert(`
            <strong>表单名称:</strong> ${form.formName}<br>
            ${form.formFields.map(field => `<strong>${field.name}:</strong> ${field.value}`).join('<br>')}
          `, '申请工单', {
            dangerouslyUseHTMLString: true
          });
        } else {
          this.$message.error('获取申请工单失败: ' + response.data.msg);
        }
      })
      .catch(error => {
        console.error('Error fetching task form:', error);
        this.$message.error('获取申请工单失败');
      });
    },
    showProcessProgress(taskId) {
      axios.get(`http://139.199.168.63:8080/operator/getProcessProgress/${taskId}`, {
        headers: {
          Authorization: localStorage.getItem('token'),
          'Accept': 'application/json'
        }
      })
      .then(response => {
        if (response.data.code === 0) {
          this.processProgress = response.data.data;
          this.processDialogVisible = true;
        } else {
          this.$message.error('获取流程进度失败: ' + response.data.msg);
        }
      })
      .catch(error => {
        console.error('Error fetching process progress:', error);
        this.$message.error('获取流程进度失败');
      });
    },
    logout() {
      this.logoutDialogVisible = true;
    },
    confirmLogout() {
      localStorage.removeItem('userInfo');
      localStorage.removeItem('token');
      localStorage.setItem('isLoggedIn', 'false');
      this.$router.push({ name: 'Login' });
    }
  }
};
</script>
