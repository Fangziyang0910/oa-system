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
            <h2>历史审核记录</h2>
            <el-table :data="paginatedData" @row-click="handleRowClick">
              <el-table-column prop="taskId" label="任务ID" width="170"></el-table-column>
              <el-table-column prop="taskName" label="任务名称"></el-table-column>
              <el-table-column prop="starterName" label="发起人"></el-table-column>
              <el-table-column prop="processDefinitionName" label="流程名称" width="110"></el-table-column>
              <el-table-column prop="description" label="描述"></el-table-column>
              <el-table-column prop="endTime" label="结束时间" width="230"></el-table-column>
            </el-table>
            <el-pagination
              background
              layout="prev, pager, next"
              :total="totalTasks"
              :page-size="pageSize"
              @current-change="handlePageChange"
              :current-page.sync="currentPage"
              style="margin-top: 20px"
            ></el-pagination>
          </el-card>
        </el-main>
      </el-container>
  
      <el-dialog
        title="任务详细信息"
        :visible.sync="taskDialogVisible"
        width="50%"
      >
        <div v-if="selectedTask">
          <h3>{{ selectedTask.formName }}</h3>
          <el-form :model="selectedTask">
            <el-form-item
              v-for="field in selectedTask.formFields"
              :key="field.id"
              :label="field.name"
            >
              <el-input v-model="field.value" :readonly="field.readOnly"></el-input>
            </el-form-item>
          </el-form>
          <h3>流程进度</h3>
          <el-timeline>
            <el-timeline-item
              v-for="task in taskProgress"
              :key="task.taskId"
              :timestamp="task.endTime || task.dueTime || ''"
              placement="top"
            >
              <p><strong>{{ task.taskName }}</strong> - {{ task.assigneeName }}</p>
              <p>{{ task.description }}</p>
            </el-timeline-item>
          </el-timeline>
        </div>
      </el-dialog>
  
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
    width: 80%;
    padding: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    border-radius: 10px;
  }
  
  .process-diagram img {
    width: 100%;
    height: auto;
    display: block;
  }
  </style>
  
  <script>
  import axios from 'axios';
  
  export default {
    data() {
      return {
        userInfo: {},
        approvalTasks: [],
        selectedTask: null,
        taskDialogVisible: false,
        taskProgress: [],
        logoutDialogVisible: false,
        currentPage: 1,
        pageSize: 4, // 每页显示4行
      };
    },
    computed: {
      paginatedData() {
        const start = (this.currentPage - 1) * this.pageSize;
        const end = this.currentPage * this.pageSize;
        return this.approvalTasks.slice(start, end);
      },
      totalTasks() {
        return this.approvalTasks.length;
      },
    },
    created() {
      this.loadUserInfoFromLocalStorage();
      this.fetchApprovalTasks();
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
      fetchApprovalTasks() {
        const token = localStorage.getItem('token');
        axios
          .get('http://139.199.168.63:8080/approver/listApprovalTasksCompleted', {
            headers: {
              Authorization: token,
              Accept: 'application/json',
            },
          })
          .then(response => {
            if (response.data.code === 0) {
              this.approvalTasks = response.data.data;
            } else {
              alert('获取历史审核记录失败');
            }
          })
          .catch(error => {
            console.error('Error fetching approval tasks:', error);
            alert('获取历史审核记录失败');
          });
      },
      handleRowClick(row) {
        const token = localStorage.getItem('token');
        axios
          .get(`http://139.199.168.63:8080/approver/getTaskCompleted/${row.taskId}`, {
            headers: {
              Authorization: token,
              Accept: 'application/json',
            },
          })
          .then(response => {
            if (response.data.code === 0) {
              this.selectedTask = response.data.data;
              this.fetchTaskForm(row.taskId);
              this.fetchTaskProgress(row.taskId);
              this.taskDialogVisible = true;
            } else {
              console.error('Error in response:', response.data);
              alert('获取任务详细信息失败');
            }
          })
          .catch(error => {
            console.error('Error fetching task details:', error);
            alert('获取任务详细信息失败');
          });
      },
      fetchTaskForm(taskId) {
        const token = localStorage.getItem('token');
        axios
          .get(`http://139.199.168.63:8080/approver/getHistoricalForm/${taskId}`, {
            headers: {
              Authorization: token,
              Accept: 'application/json',
            },
          })
          .then(response => {
            if (response.data.code === 0) {
              this.selectedTask = { ...this.selectedTask, ...response.data.data };
            } else {
              console.error('Error in response:', response.data);
              alert('获取任务表单失败');
            }
          })
          .catch(error => {
            console.error('Error fetching task form:', error);
            alert('获取任务表单失败');
          });
      },
      fetchTaskProgress(taskId) {
        const token = localStorage.getItem('token');
        axios
          .get(`http://139.199.168.63:8080/approver/getProcessProgress/${taskId}`, {
            headers: {
              Authorization: token,
              Accept: 'application/json',
            },
          })
          .then(response => {
            if (response.data.code === 0) {
              this.taskProgress = response.data.data;
            } else {
              console.error('Error in response:', response.data);
              alert('获取流程进度失败');
            }
          })
          .catch(error => {
            console.error('Error fetching task progress:', error);
            alert('获取流程进度失败');
          });
      },
      handlePageChange(page) {
        this.currentPage = page;
      },
      logout() {
        this.logoutDialogVisible = true;
      },
      confirmLogout() {
        localStorage.removeItem('userInfo');
        localStorage.removeItem('token');
        localStorage.setItem('isLoggedIn', 'false');
        this.$router.push({ name: 'Login' });
      },
    },
  };
  </script>
  