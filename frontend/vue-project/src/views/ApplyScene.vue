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
        <div class="process-diagram" v-if="processDiagram">
          <img :src="processDiagram" alt="流程图">
        </div>
        <el-form ref="form" :model="form" label-width="120px" class="user-info-card">
          <el-form-item label="流程定义">
            <el-select v-model="selectedProcessDefinition" placeholder="请选择流程">
              <el-option
                v-for="item in processDefinitions"
                :key="item.processDefinitionKey"
                :label="item.processDefinitionName"
                :value="item.processDefinitionKey"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item v-for="(field, index) in formFields" :key="index" :label="field.name">
            <template v-if="field.name === '员工部门'">
              <el-select v-model="form.applicantDepartment" placeholder="请选择部门">
                <el-option v-for="item in departments" :key="item" :label="item" :value="item"></el-option>
              </el-select>
            </template>
            <template v-else>
              <component
                :is="getFieldComponent(field.type, field.id)"
                v-model="form[field.id]"
                v-bind="getFieldAttributes(field)"
              ></component>
            </template>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="startProcessInstance">开始流程</el-button>
          </el-form-item>
        </el-form>
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
  width: 80%;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-radius: 10px;
}

.process-diagram {
  width: 50%;
  text-align: center;
  margin-bottom: 20px;
  display: flex;
  justify-content: center;
}

.process-diagram img {
  width: 20%;
  height: auto;
}
</style>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      userInfo: {},
      form: {},
      departments: [],
      processDefinitions: [],
      selectedProcessDefinition: '',
      processDiagram: '',
      formFields: [],
      logoutDialogVisible: false
    };
  },
  created() {
    this.loadUserInfoFromLocalStorage();
    this.fetchDepartments();
    this.fetchProcessDefinitions();
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
    fetchDepartments() {
      const token = localStorage.getItem('token');
      axios
        .get('http://139.199.168.63:8080/permission/getDepartments', {
          headers: {
            Authorization: token,
            Accept: 'application/json'
          }
        })
        .then(response => {
          if (response.data.code === 0) {
            this.departments = JSON.parse(response.data.data);
          } else {
            alert('获取部门列表失败');
          }
        })
        .catch(error => {
          console.error('Error fetching departments:', error);
          alert('获取部门列表失败');
        });
    },
    fetchProcessDefinitions() {
      const token = localStorage.getItem('token');
      axios
        .get('http://139.199.168.63:8080/applicant/listProcessDefinitions', {
          headers: {
            Authorization: token,
            Accept: 'application/json'
          }
        })
        .then(response => {
          if (response.data.code === 0) {
            this.processDefinitions = response.data.data;
          } else {
            alert('获取流程定义失败');
          }
        })
        .catch(error => {
          console.error('Error fetching process definitions:', error);
          alert('获取流程定义失败');
        });
    },
    fetchProcessDiagram(processDefinitionKey) {
      const token = localStorage.getItem('token');
      axios
        .get(`http://139.199.168.63:8080/applicant/getOriginalProcessDiagram/${processDefinitionKey}`, {
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
        });
    },
    fetchStartForm(processDefinitionKey) {
      const token = localStorage.getItem('token');
      axios
        .get(`http://139.199.168.63:8080/applicant/getStartForm/${processDefinitionKey}`, {
          headers: {
            Authorization: token,
            Accept: 'application/json'
          }
        })
        .then(response => {
          if (response.data.code === 0) {
            const formData = JSON.parse(response.data.data);
            this.formFields = formData.fields;
            this.form = {};
            formData.fields.forEach(field => {
              this.$set(this.form, field.id, '');
            });
          } else {
            alert('获取表单失败');
          }
        })
        .catch(error => {
          console.error('Error fetching start form:', error);
          alert('获取表单失败');
        });
    },
    startProcessInstance() {
  const token = localStorage.getItem('token');
  if (!token) {
    alert('Token not found. Please log in again.');
    this.$router.push({ name: 'Login' });
    return;
  }

  const selectedProcessDefinitionKey = this.selectedProcessDefinition;
  if (!selectedProcessDefinitionKey) {
    alert('Please select a process definition.');
    return;
  }

  axios.get(
    `http://139.199.168.63:8080/applicant/createProcessInstance/${selectedProcessDefinitionKey}`,
    {
      headers: {
        Authorization: token,
        Accept: 'application/json'
      }
    }
  )
  .then(response => {
    if (response.data.code === 0) {
      const processInstanceId = response.data.data.processInstanceId;
      this.submitStartForm(processInstanceId);
    } else {
      console.error('Error in response:', response.data);
      alert(`启动流程失败: ${response.data.msg}`);
    }
  })
  .catch(error => {
    if (error.response) {
      // The request was made and the server responded with a status code
      console.error('Response error:', error.response.data);
      alert(`启动流程失败: ${error.response.data.msg || 'Unknown error'}`);
    } else if (error.request) {
      // The request was made but no response was received
      console.error('Request error:', error.request);
      alert('启动流程失败: No response from server.');
    } else {
      // Something happened in setting up the request that triggered an Error
      console.error('Error setting up request:', error.message);
      alert(`启动流程失败: ${error.message}`);
    }
  });
}
,
    submitStartForm(processInstanceId) {
      const token = localStorage.getItem('token');
      const formData = {
        form: this.form,
        processInstanceId
      };
      axios
        .post('http://139.199.168.63:8080/applicant/submitStartForm', formData, {
          headers: {
            Authorization: token,
            'Content-Type': 'application/json'
          }
        })
        .then(response => {
          if (response.data.code === 0) {
            alert('流程启动成功');
            this.$router.push({ name: 'CurrentApplication' });
          } else {
            alert(`提交表单失败: ${response.data.msg}`);
          }
        })
        .catch(error => {
          console.error('Error submitting start form:', error);
          alert('提交表单失败');
        });
    },
    getFieldComponent(type, id) {
      if (id === 'applicantDepartment') {
        return 'el-select';
      }
      switch (type) {
        case 'string':
          return 'el-input';
        case 'integer':
          return 'el-input-number';
        case 'date':
          return 'el-date-picker';
        default:
          return 'el-input';
      }
    },
    getFieldAttributes(field) {
      const attributes = {};
      if (field.type === 'date') {
        attributes.type = 'date';
        attributes.placeholder = '请选择日期';
      } else if (field.type === 'integer') {
        attributes.min = 0;
      } else if (field.type === 'string' && field.id === 'applicantDepartment') {
        attributes.placeholder = '请选择部门';
        attributes.options = this.departments.map(dept => ({ label: dept, value: dept }));
      } else {
        attributes.placeholder = field.placeholder;
      }
      return attributes;
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
  },
  watch: {
    selectedProcessDefinition(newVal) {
      if (newVal) {
        this.fetchProcessDiagram(newVal);
        this.fetchStartForm(newVal);
      }
    }
  }
};
</script>
