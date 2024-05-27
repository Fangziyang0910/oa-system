<template>
  <el-container class="full-height-container">
    <el-aside width="200px" style="background-color: rgb(238, 241, 246)">
      <el-menu :default-openeds="['1']">
        <el-submenu index="1">
          <template #title><i class="el-icon-message"></i>申请记录</template>
          <el-menu-item index="1-1">
            <router-link to="/currentApplication">当前申请</router-link>
          </el-menu-item>
          <el-menu-item index="1-2">
            <router-link to="/applicationRecords">历史申请</router-link>
          </el-menu-item>
        </el-submenu>
        <el-menu-item index="2">
          <template #title>
            <i class="el-icon-message"></i>
            <router-link to="/accountManage">账户管理</router-link>
          </template>
        </el-menu-item>
        <el-menu-item v-if="userInfo.isApplicant" index="3">
          <template #title>
            <i class="el-icon-message"></i>
            <router-link to="/applyScene">申请页面</router-link>
          </template>
        </el-menu-item>
        <el-submenu v-if="userInfo.isApprover" index="4">
          <template #title><i class="el-icon-message"></i>审核记录</template>
          <el-menu-item index="4-1">
            <router-link to="/ReviewPage">当前审核</router-link>
          </el-menu-item>
          <el-menu-item index="4-2">
            <router-link to="/ReviewRecords">历史审核</router-link>
          </el-menu-item>
        </el-submenu>
        <el-submenu v-if="userInfo.isOperator" index="5">
          <template #title><i class="el-icon-message"></i>运维记录</template>
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
            <el-table-column prop="taskName" label="任务名称" width="100">
              <template #default="scope">
                <span v-if="scope.row.ownerName === userInfo.name">【Owner】</span>
                {{ scope.row.taskName }}
              </template>
            </el-table-column>
            <el-table-column prop="description" label="描述" width="100"></el-table-column>
            <el-table-column prop="dueTime" label="截止时间" width="180"></el-table-column>
            <el-table-column prop="starterName" label="发起人" width="180"></el-table-column>
            <el-table-column label="操作" width="180">
              <template #default="scope">
              <el-button @click.stop="handleApproval(scope.row.taskId,scope.row.ownerName)">审批</el-button>
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
              <el-button v-if="isOwner" type="danger" @click="abandonTask">放弃任务</el-button>
              <el-button v-if="isOwner" type="primary" @click="fetchCandidateUsers">分配任务</el-button>
              <el-button v-if="isOwner" type="danger" @click="unassignTask">结束分配任务</el-button>
            </el-form>
            <el-table :data="taskProgress" style="width: 100%; margin-top: 20px;">
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
          title="分配任务"
          :visible.sync="assignDialogVisible"
          width="30%">
          <el-form>
            <el-form-item label="选择候选人">
              <el-select v-model="selectedCandidate" placeholder="请选择候选人">
                <el-option
                  v-for="candidate in candidates"
                  :key="candidate"
                  :label="candidate"
                  :value="candidate">
                </el-option>
              </el-select>
            </el-form-item>
          </el-form>
          <span slot="footer" class="dialog-footer">
            <el-button @click="assignDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="assignTask">确认分配</el-button>
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
        <el-dialog title="审核工单" :visible.sync="approvalDialogVisible" width="50%">
        <div v-if="approvalForm">
          <el-form :model="approvalForm">
            <el-form-item v-for="field in approvalForm.formFields" :key="field.id" :label="field.name">
              <el-input v-model="field.value" :readonly="field.readOnly"></el-input>
            </el-form-item>
          </el-form>            
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button  @click="saveWorkOrder(approvalForm.taskId)">保存工单</el-button>
          <el-button v-if="approvalForm && approvalForm.ownerName === userInfo.name" type="primary" @click="submitWorkOrder(approvalForm.taskId)">提交工单</el-button>
          <el-button v-else type="danger" @click="endAcceptance(approvalForm.taskId)">结束受理</el-button>
          <el-button @click="approvalDialogVisible = false">关闭</el-button>
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
      tasks: [],
      taskDetails: null,
      taskProgress: [],
      taskForm: null,
      candidates: [],
      selectedCandidate: '',
      taskDialogVisible: false,
      formDialogVisible: false,
      assignDialogVisible: false,
      currentPage: 1,
      pageSize: 8,
      totalTasks: 0,
      logoutDialogVisible: false,
      approvalForm: null,
      approvalDialogVisible: false
    };
  },
  computed: {
    paginatedTasks() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return this.tasks.slice(start, end);
    },
    isOwner() {
      return this.taskDetails && this.taskDetails.ownerName === this.userInfo.name;
    },
    isAssignee() {
      return this.taskDetails && this.taskDetails.assigneeName === this.userInfo.name;
    }
  },
  created() {
    this.loadUserInfoFromLocalStorage();
    this.fetchTasks();
  },
  methods: {
    abandonTask() {
    // 获取任务ID
    const taskId = this.taskDetails.taskId;

    // 检查任务ID是否存在
    if (!taskId) {
      this.$message.error('任务ID不存在，无法放弃任务');
      return;
    }

    // 发送GET请求到后端接口
    axios.get(`http://139.199.168.63:8080/operator/unclaimCandidateTask/${taskId}`, {
      headers: {
        Authorization: localStorage.getItem('token'),
        'Accept': 'application/json'
      }
    })
    .then(response => {
      if (response.data.code === 0) {
        // 处理成功
        this.$message.success('任务已成功放弃');
        // 重新加载任务数据或其他操作
        this.approvalDialogVisible = false;
        this.fetchTasks();
      } else {
        // 处理失败
        this.$message.error('放弃任务失败: ' + response.data.msg);
      }
    })
    .catch(error => {
      console.error('Error abandoning task:', error);
      this.$message.error('放弃任务失败: ' + error.message);
    });
  },
    submitWorkOrder(taskId) {
  // Check if approvalForm is available
  if (!this.approvalForm) {
    this.$message.error('审核工单信息未加载');
    return;
  }

  // Prepare form data to be sent to the backend
  const formData = {};
  this.approvalForm.formFields.forEach(field => {
    formData[field.id] = field.value;
  });

  // Prepare request data to be sent to the backend
  const requestData = {
    form: formData,
    taskId: taskId // Assuming taskId is obtained from taskDetails
  };

  // Make an HTTP POST request to the backend endpoint
  axios.post('http://139.199.168.63:8080/operator/completeOperatorTask', requestData, {
    headers: {
      Authorization: localStorage.getItem('token'),
      'Content-Type': 'application/json'
    }
  })
  .then(response => {
    if (response.data.code === 0) {
      // Handle success
      this.$message.success('工单提交成功');
      this.approvalDialogVisible = false;
      this.fetchTasks();
      // Optionally, you can reload the form or perform other actions
    } else {
      // Handle failure
      this.$message.error('工单提交失败: ' + response.data.msg);
    }
  })
  .catch(error => {
    console.error('Error submitting work order:', error);
    this.$message.error('工单提交失败: ' + error.message);
  });
},
    saveWorkOrder(taskId) {
  // Check if approvalForm is available
  if (!this.approvalForm) {
    this.$message.error('审核工单信息未加载');
    return;
  }

  // Prepare form data to be sent to the backend
  const formData = {};
  this.approvalForm.formFields.forEach(field => {
    formData[field.id] = field.value;
  });


  // Prepare request data to be sent to the backend
  const requestData = {
    form: formData,
    taskId:taskId // Assuming taskId is obtained from taskDetails
  };

  // Make an HTTP POST request to the backend endpoint
  axios.post('http://139.199.168.63:8080/operator/saveOperatorTask', requestData, {
    headers: {
      Authorization: localStorage.getItem('token'),
      'Content-Type': 'application/json'
    }
  })
  .then(response => {
    if (response.data.code === 0) {
      // Handle success
      this.$message.success('工单保存成功');


    } else {
      // Handle failure
      this.$message.error('工单保存失败: ' + response.data.msg);
    }
  })
  .catch(error => {
  console.error('Error saving work order:', error);
  this.$message.error('工单保存失败: ' + error.message);
});

}
,
    async handleApproval(taskId,ownerName) {
      try {
        // Fetch the task form template
        const templateResponse = await axios.get(`http://139.199.168.63:8080/operator/getTaskForm/${taskId}`, {
          headers: {
            Authorization: localStorage.getItem('token')
          }
        });

        if (templateResponse.data.code !== 0) {
          this.$message.error('获取工单模板失败');
          return;
        }
        const formTemplate = JSON.parse(templateResponse.data.data);
        // Fetch the cached task form data
        const dataResponse = await axios.get(`http://139.199.168.63:8080/operator/getTaskFormData/${taskId}`, {
          headers: {
            Authorization: localStorage.getItem('token')
          }
        });
        if (dataResponse.data.code !== 0) {
          this.$message.error('获取缓存工单数据失败');
          return;
        }
        const formData = dataResponse.data.data;
        
        // Merge the template and the cached data
        formTemplate.fields.forEach(field => {
          const cachedField = formData.formFields.find(f => f.id === field.id);
          if (cachedField) {
            field.value = cachedField.value;
          }
        });
        
        this.approvalForm = {
          formFields: formTemplate.fields,
          formName: formTemplate.name,
          ownerName:ownerName,
          taskId:taskId
        };
        this.approvalDialogVisible = true;
      } catch (error) {
        console.error('Error handling approval:', error);
        this.$message.error('操作失败');
      }
    },
    async endAcceptance(taskId) {
      try {
        const response = await axios.get(`http://139.199.168.63:8080/operator/endAssignedTask/${taskId}`, {
          headers: {
            Authorization: localStorage.getItem('token')


          }
        });
        if (response.data.code === 0) {
          this.$message.success('结束受理成功');
          this.approvalDialogVisible = false;
        } else {
          this.$message.error(`结束受理失败: ${response.data.msg}`);
          console.error('End Acceptance Error:', response.data);
        }
      } catch (error) {
        console.error('Error ending acceptance:', error);
        this.$message.error('结束受理失败');
      }
    },
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
      axios.get('http://139.199.168.63:8080/operator/listOperatorAssignTasks', {
        headers: {
          Authorization: localStorage.getItem('token')
        }
      })
      .then(response => {
        if (response.data.code === 0) {
          this.tasks = response.data.data;
          this.totalTasks = this.tasks.length;
        } else {
          this.$message.error('获取任务数据失败');
        }
      })
      .catch(error => {
        console.error('Error fetching tasks:', error);
        this.$message.error('获取任务数据失败');
      });
    },
    handleCurrentChange(page) {
      this.currentPage = page;
    },
    handleRowClick(row) {
      this.taskDetails = row;
      this.fetchTaskDetails(row.taskId);
      this.fetchTaskProgress(row.taskId);
      if (this.isOwner) {
        this.fetchTaskForm(row.taskId);
      }
    },
    fetchTaskDetails(taskId) {
      axios.get(`http://139.199.168.63:8080/operator/getTaskNotCompleted/${taskId}`, {
        headers: {
          Authorization: localStorage.getItem('token')
        }
      })
      .then(response => {
        if (response.data.code === 0) {
          this.taskDetails = response.data.data;
        } else {
          this.$message.error('获取未完成任务详情失败');
        }
      })
      .catch(error => {
        console.error('Error fetching task details:', error);
        this.$message.error('获取未完成任务详情失败');
      });
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
    unassignTask() {
      axios.get(`http://139.199.168.63:8080/operator/unassignTask/${this.taskDetails.taskId}`, {
        headers: {
          Authorization: localStorage.getItem('token')
        }
      })
      .then(response => {
        if (response.data.code === 0) {
          this.$message.success('任务分配结束成功');
          this.taskDialogVisible = false;
          this.fetchTasks();  // 重新加载任务列表
        } else {
          this.$message.error('任务分配结束失败');
        }
      })
      .catch(error => {
        console.error('Error ending task:', error);
        this.$message.error('任务分配结束失败');
      });
    },
    fetchCandidateUsers() {
      axios.get(`http://139.199.168.63:8080/operator/listOperatorCandidateUsers/${this.taskDetails.taskId}`, {
        headers: {
          Authorization: localStorage.getItem('token')
        }
      })
      .then(response => {
        if (response.data.code === 0) {
          this.candidates = response.data.data;
          this.assignDialogVisible = true;
        } else {
          this.$message.error('获取候选人失败');
        }
      })
      .catch(error => {
        console.error('Error fetching candidates:', error);
        this.$message.error('获取候选人失败');
      });
    },
    assignTask() {
      axios.post(`http://139.199.168.63:8080/operator/assignTask`, {}, {
        headers: {
          Authorization: localStorage.getItem('token')
        },
        params: {
          taskId: this.taskDetails.taskId,
          userName: this.selectedCandidate
        }
      })
      .then(response => {
        if (response.data.code === 0) {
          this.$message.success('任务分配成功');
          this.assignDialogVisible = false;
          this.fetchTasks();  // 重新加载任务列表
        } else {
          this.$message.error('任务分配失败');
        }
      })
      .catch(error => {
        console.error('Error assigning task:', error);
        this.$message.error('任务分配失败');
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
