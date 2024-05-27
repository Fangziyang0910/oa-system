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
              <el-table-column prop="taskName" label="任务名称"></el-table-column>
              <el-table-column prop="description" label="描述"></el-table-column>
              <el-table-column prop="dueTime" label="截止时间"></el-table-column>
              <el-table-column prop="starterName" label="发起人"></el-table-column>
              <el-table-column label="操作">
                <template slot-scope="scope">
                  <el-button @click.stop="claimTask(scope.row.taskId)">申领</el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-pagination
              @current-change="handleCurrentChange"
              :current-page="currentPage"
              :page-size="pageSize"
              layout="total, prev, pager, next"
              :total="totalTasks">
            </el-pagination>
          </el-card>
  
          <el-dialog
            title="任务详细信息"
            :visible.sync="taskDialogVisible"
            width="50%">
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
                <el-form-item label="截止时间">
                  <el-input v-model="taskDetails.dueTime" readonly></el-input>
                </el-form-item>
                <el-form-item label="结束时间">
                  <el-input v-model="taskDetails.endTime" readonly></el-input>
                </el-form-item>
                <el-button type="primary" @click="showTaskForm">查看申请工单</el-button>
              </el-form>
              <el-table :data="taskProgress" style="width: 100%">
                <el-table-column prop="taskName" label="任务名称"></el-table-column>
                <el-table-column prop="assigneeName" label="办理人"></el-table-column>
                <el-table-column prop="endTime" label="结束时间"></el-table-column>
              </el-table>
            </div>
            <span slot="footer" class="dialog-footer">
              <el-button @click="taskDialogVisible = false">关闭</el-button>
            </span>
          </el-dialog>
  
          <el-dialog
            title="申请工单"
            :visible.sync="formDialogVisible"
            width="50%">
            <div v-if="taskForm">
              <el-form :model="taskForm">
                <el-form-item v-for="field in taskForm.formFields" :key="field.id" :label="field.name">
                  <el-input v-model="field.value" readonly></el-input>
                </el-form-item>
              </el-form>
            </div>
            <span slot="footer" class="dialog-footer">
              <el-button @click="formDialogVisible = false">关闭</el-button>
            </span>
          </el-dialog>
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
    align-items: flex-start; /* 将align-items改为flex-start */
    flex: 1;
    padding: 20px; /* 添加一些内边距 */
  }
  
  .user-info-card {
    width: 110%;
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
        tasks: [],
        currentPage: 1,
        pageSize: 10,
        totalTasks: 0,
        taskDialogVisible: false,
        formDialogVisible: false,
        taskDetails: null,
        taskProgress: [],
        taskForm: null,
        logoutDialogVisible: false
      };
    },
    computed: {
      paginatedTasks() {
        const start = (this.currentPage - 1) * this.pageSize;
        const end = start + this.pageSize;
        return this.tasks.slice(start, end);
      }
    },
    created() {
      this.loadUserInfoFromLocalStorage();
      this.fetchTasks();
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
      fetchTasks() {
        axios.get('http://139.199.168.63:8080/operator/listOperatorCandidateTasks', {
          headers: {
            Authorization: localStorage.getItem('token')
          }
        })
        .then(response => {
          if (response.data.code === 0) {
            this.tasks = response.data.data;
            this.totalTasks = this.tasks.length;
          } else {
            this.$message.error('获取任务池数据失败');
          }
        })
        .catch(error => {
          console.error('Error fetching tasks:', error);
          this.$message.error('获取任务池数据失败');
        });
      },
      handleCurrentChange(page) {
        this.currentPage = page;
      },
      handleRowClick(row) {
        this.taskDetails = row;
        this.fetchTaskProgress(row.taskId);
      },
      fetchTaskProgress(taskId) {
        axios.get(`http://139.199.168.63:8080/operator/getProcessProgress/${taskId}`, {
          headers: {
            Authorization: localStorage.getItem('token')
          }
        })
        .then(response => {
          if (response.data.code === 0) {
            this.taskProgress = response.data.data;
            this.taskDialogVisible = true;
          } else {
            this.$message.error('获取任务流程进度失败');
          }
        })
        .catch(error => {
          console.error('Error fetching task progress:', error);
          this.$message.error('获取任务流程进度失败');
        });
      },
      showTaskForm() {
        axios.get(`http://139.199.168.63:8080/operator/getStartForm/${this.taskDetails.taskId}`, {
          headers: {
            Authorization: localStorage.getItem('token')
          }
        })
        .then(response => {
          if (response.data.code === 0) {
            this.taskForm = response.data.data;
            this.formDialogVisible = true;
          } else {
            this.$message.error('获取申请工单失败');
          }
        })
        .catch(error => {
          console.error('Error fetching task form:', error);
          this.$message.error('获取申请工单失败');
        });
      },
      claimTask(taskId) {
        axios.get(`http://139.199.168.63:8080/operator/claimCandidateTask/${taskId}`, {
          headers: {
            Authorization: localStorage.getItem('token')
          }
        })
        .then(response => {
          if (response.data.code === 0) {
            this.$message.success('申领任务成功');
            this.fetchTasks();
          } else {
            this.$message.error('申领任务失败');
          }
        })
        .catch(error => {
          console.error('Error claiming task:', error);
          this.$message.error('申领任务失败');
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
  