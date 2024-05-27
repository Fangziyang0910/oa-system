<template>
  <el-container class="full-height-container">
    <el-aside width="200px" style="background-color: rgb(238, 241, 246)">
      <el-menu :default-openeds="['1']">
        <el-submenu index="1">
          <template slot="title">
            <i class="el-icon-message"></i>申请记录
          </template>
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
          <template slot="title">
            <i class="el-icon-message"></i>审核记录
          </template>
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
          <h2>历史申请记录</h2>
          <el-table :data="paginatedData" @row-click="handleRowClick">
            <el-table-column prop="processInstanceId" label="流程实例ID"></el-table-column>
            <el-table-column prop="processDefinition.processDefinitionName" label="流程名称"></el-table-column>
            <el-table-column prop="startTime" label="开始时间"></el-table-column>
            <el-table-column prop="endTime" label="结束时间"></el-table-column>
            <el-table-column prop="abortReason" label="终止原因"></el-table-column>
          </el-table>
          <el-pagination
            background
            layout="prev, pager, next"
            :total="totalInstances"
            :page-size="pageSize"
            @current-change="handlePageChange"
            :current-page.sync="currentPage"
            style="margin-top: 20px"
          ></el-pagination>
        </el-card>
      </el-main>
    </el-container>

    <el-dialog
      title="流程详细信息"
      :visible.sync="processDialogVisible"
      width="50%"
    >
      <div v-if="selectedProcessInstance">
        <h3>流程定义信息</h3>
        <p><strong>流程定义ID:</strong> {{ selectedProcessInstance.processDefinition.processDefinitionId }}</p>
        <p><strong>流程名称:</strong> {{ selectedProcessInstance.processDefinition.processDefinitionName }}</p>
        <p><strong>流程描述:</strong> {{ selectedProcessInstance.processDefinition.processDefinitionDescription }}</p>
        <h3>流程进度</h3>
        <el-timeline>
          <el-timeline-item
            v-for="task in selectedProcessInstance.progress"
            :key="task.taskId"
            :timestamp="task.endTime || task.dueTime || ''"
            placement="top"
          >
            <p><strong>{{ task.taskName }}</strong> - {{ task.assigneeName }}</p>
            <p>{{ task.description }}</p>
            <el-button size="mini" @click="handleTaskClick(task.taskId)">查看任务详情</el-button>
          </el-timeline-item>
        </el-timeline>
        <h3>流程图</h3>
        <div class="process-diagram">
          <img :src="processDiagram" alt="流程图" v-if="processDiagram">
        </div>
      </div>
    </el-dialog>

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
      processInstances: [],
      selectedProcessInstance: null,
      selectedTask: null,
      processDialogVisible: false,
      taskDialogVisible: false,
      processDiagram: '',
      logoutDialogVisible: false,
      currentPage: 1,
      pageSize: 4  // 每页显示4行
    };
  },
  computed: {
    paginatedData() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = this.currentPage * this.pageSize;
      return this.processInstances.slice(start, end);
    },
    totalInstances() {
      return this.processInstances.length;
    }
  },
  created() {
    this.loadUserInfoFromLocalStorage();
    this.fetchProcessInstances();
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
    fetchProcessInstances() {
      const token = localStorage.getItem('token');
      if (!token) {
        alert('Token not found. Please log in again.');
        this.$router.push({ name: 'Login' });
        return;
      }

      axios
        .get('http://139.199.168.63:8080/applicant/listProcessInstancesCompleted', {
          headers: {
            Authorization: token,
            Accept: 'application/json'
          }
        })
        .then(response => {
          if (response.data.code === 0) {
            this.processInstances = response.data.data;
          } else {
            console.error('Error in response:', response.data);
            alert('获取历史申请记录失败');
          }
        })
        .catch(error => {
          this.handleError(error, '获取历史申请记录失败');
        });
    },
    handleRowClick(row) {
      const token = localStorage.getItem('token');
      const processInstanceId = row.processInstanceId;
      axios
        .get(`http://139.199.168.63:8080/applicant/getProcessInstance/${processInstanceId}`, {
          headers: {
            Authorization: token,
            Accept: 'application/json'
          }
        })
        .then(response => {
          if (response.data.code === 0) {
            this.selectedProcessInstance = response.data.data;
            this.fetchProcessDiagram(processInstanceId);
            this.processDialogVisible = true;
          } else {
            console.error('Error in response:', response.data);
            alert('获取流程详细信息失败');
          }
        })
        .catch(error => {
          this.handleError(error, '获取流程详细信息失败');
        });
    },
    handleTaskClick(taskId) {
      const token = localStorage.getItem('token');
      axios
        .get(`http://139.199.168.63:8080/applicant/getHistoricalForm/${taskId}`, {
          headers: {
            Authorization: token,
            Accept: 'application/json'
          }
        })
        .then(response => {
          if (response.data.code === 0) {
            this.selectedTask = response.data.data;
            this.taskDialogVisible = true;
          } else {
            console.error('Error in response:', response.data);
            alert('获取任务详细信息失败');
          }
        })
        .catch(error => {
          this.handleError(error, '获取任务详细信息失败');
        });
    },
    fetchProcessDiagram(processInstanceId) {
      const token = localStorage.getItem('token');
      axios
        .get(`http://139.199.168.63:8080/applicant/getProcessInstanceDiagram/${processInstanceId}`, {
          headers: {
            Authorization: token,
            Accept: 'image/png'
          },
          responseType: 'arraybuffer'
        })
        .then(response => {
          const imageUrl = URL.createObjectURL(new Blob([response.data], { type: 'image/png' }));
          this.processDiagram = imageUrl;
        })
        .catch(error => {
          console.error('Error fetching process diagram:', error);
          alert('获取流程图失败');
        });
    },
    handleError(error, defaultMessage) {
      if (error.response) {
        console.error('Response error:', error.response.data);
        alert(`${defaultMessage}: ${error.response.data.msg || 'Unknown error'}`);
      } else if (error.request) {
        console.error('Request error:', error.request);
        alert(`${defaultMessage}: No response from server.`);
      } else {
        console.error('Error setting up request:', error.message);
        alert(`${defaultMessage}: ${error.message}`);
      }
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
    handlePageChange(page) {
      this.currentPage = page;
    }
  }
};
</script>
