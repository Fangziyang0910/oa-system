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
        <el-table :data="paginatedProcessDefinitions" @row-click="handleRowClick" style="width: 100%">
          <el-table-column prop="processDefinitionId" label="流程ID"></el-table-column>
          <el-table-column prop="processDefinitionName" label="流程名称"></el-table-column>
          <el-table-column prop="processDefinitionDescription" label="描述"></el-table-column>
          <el-table-column prop="processDefinitionVersion" label="版本"></el-table-column>
        </el-table>
        <el-pagination
          @current-change="handlePageChange"
          :current-page="currentPage"
          :page-size="pageSize"
          layout="total, prev, pager, next"
          :total="totalProcessDefinitions">
        </el-pagination>
        <el-dialog
          title="流程图"
          :visible.sync="diagramDialogVisible"
          width="80%">
          <img v-if="diagramSrc" :src="diagramSrc" alt="流程图" class="diagram-image" />
          <span slot="footer" class="dialog-footer">
            <el-button @click="diagramDialogVisible = false">关闭</el-button>
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

.diagram-image {
  width: 100%;
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
      processDefinitions: [],
      currentPage: 1,
      pageSize: 4,
      diagramDialogVisible: false,
      diagramSrc: null
    };
  },
  computed: {
    paginatedProcessDefinitions() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return this.processDefinitions.slice(start, end);
    },
    totalProcessDefinitions() {
      return this.processDefinitions.length;
    }
  },
  created() {
    this.fetchProcessDefinitions();
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
    fetchProcessDefinitions() {
      fetch('http://139.199.168.63:8080/admin/listProcessDefinitions', {
        headers: {
          'Authorization': localStorage.getItem('token'),
          'Content-Type': 'application/json'
        }
      })
      .then(response => response.json())
      .then(data => {
        if (data && data.code === 0) {
          this.processDefinitions = data.data;
        } else {
          this.$message.error('获取流程定义失败');
        }
      })
      .catch(error => {
        console.error('Error fetching process definitions:', error);
        this.$message.error('获取流程定义失败');
      });
    },
    handleRowClick(row) {
      this.fetchProcessDiagram(row.processDefinitionKey);
    },
    fetchProcessDiagram(processDefinitionKey) {
      fetch(`http://139.199.168.63:8080/admin/getProcessDiagram/${processDefinitionKey}`, {
        headers: {
          'Authorization': localStorage.getItem('token'),
          'Accept': 'image/png'
        }
      })
      .then(response => response.blob())
      .then(blob => {
        const url = URL.createObjectURL(blob);
        this.diagramSrc = url;
        this.diagramDialogVisible = true;
      })
      .catch(error => {
        console.error('Error fetching process diagram:', error);
        this.$message.error('获取流程图失败');
      });
    },
    handlePageChange(page) {
      this.currentPage = page;
    }
  }
};
</script>
