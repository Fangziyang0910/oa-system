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

      <el-main>
        <div class="report-filter">
          <el-button @click="fetchReports" type="primary">刷新</el-button>
          <span class="countdown">自动刷新倒计时: {{ countdown }}秒</span>
        </div>

        <el-collapse v-model="activeYear" accordion>
          <el-collapse-item
            v-for="(reports, year) in groupedReports"
            :key="year"
            :title="year + '年'"
            :name="year"
          >
            <div class="weekly-reports">
              <el-button
                v-for="report in reports"
                :key="report.reportId"
                class="weekly-report-button"
                @click="handleReportClick(report)"
              >
                {{ formatReportDateRange(report) }}
              </el-button>
            </div>
          </el-collapse-item>
        </el-collapse>

        <el-dialog title="报告详情" :visible.sync="dialogVisible" width="50%">
          <div v-if="reportDetails">
            <p><strong>标题:</strong> {{ reportDetails.title }}</p>
            <p><strong>创建时间:</strong> {{ reportDetails.createTime }}</p>
            <el-tabs v-model="activeTab">
              <el-tab-pane label="报告内容" name="content">
                <pre>{{ formatReportContent(reportDetails.content) }}</pre>
              </el-tab-pane>
              <el-tab-pane label="日报" name="daily">
                <el-select v-model="selectedDate" placeholder="请选择日期">
                  <el-option
                    v-for="date in weekDates"
                    :key="date"
                    :label="date"
                    :value="date"
                  />
                </el-select>
                <el-button @click="fetchDailyReport" type="primary">查看日报</el-button>
                <div v-if="dailyReportDetails">
                  <p><strong>标题:</strong> {{ dailyReportDetails.title }}</p>
                  <p><strong>创建时间:</strong> {{ dailyReportDetails.createTime }}</p>
                  <pre>{{ formatReportContent(dailyReportDetails.content) }}</pre>
                </div>
              </el-tab-pane>
            </el-tabs>
          </div>
          <span slot="footer" class="dialog-footer">
            <el-button @click="dialogVisible = false">关闭</el-button>
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

.report-filter {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.report-filter .el-button {
  margin-right: 10px;
}

.countdown {
  margin-left: 20px;
  font-size: 14px;
  color: #666;
}

.weekly-reports {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.weekly-report-button {
  margin: 5px;
  padding: 10px 20px;
  border-radius: 12px;
  background-color: #303133;
  color: white;
  transition: background-color 0.3s;
}

.weekly-report-button:hover {
  background-color: #606266;
}
</style>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      username: localStorage.getItem('username') || '管理员',
      logoutDialogVisible: false,
      activeYear: null,
      groupedReports: {},
      reports: [],
      dialogVisible: false,
      reportDetails: null,
      selectedDate: null,
      weekDates: [],
      dailyDialogVisible: false,
      dailyReportDetails: null,
      countdown: 300,
      autoRefreshInterval: null,
      activeTab: 'content'
    };
  },
  created() {
    this.fetchReports();
    this.startAutoRefresh();
  },
  methods: {
    fetchReports() {
      const token = localStorage.getItem('token');
      if (!token) {
        this.$router.push({ name: 'Login' });
        return;
      }

      axios.get('http://139.199.168.63:8080/admin/listWeeklyReports', {
        headers: {
          'Authorization': token,
          'Accept': 'application/json'
        }
      })
      .then(response => {
        if (response.data.code === 0) {
          this.reports = response.data.data;
          this.groupReportsByYear();
        } else if (response.data.code === 401) {
          this.handleSessionExpired();
        } else {
          this.$message.error('获取报告数据失败');
        }
      })
      .catch(error => {
        console.error('Error fetching reports:', error);
        this.$message.error('获取报告数据失败');
      });
    },
    groupReportsByYear() {
      this.groupedReports = this.reports.reduce((groups, report) => {
        const year = new Date(report.startTime).getFullYear();
        if (!groups[year]) {
          groups[year] = [];
        }
        groups[year].push(report);
        return groups;
      }, {});
    },
    formatReportDateRange(report) {
      const start = new Date(report.startTime).toLocaleDateString('zh-CN', {
        month: '2-digit',
        day: '2-digit'
      });
      const end = new Date(report.endTime).toLocaleDateString('zh-CN', {
        month: '2-digit',
        day: '2-digit'
      });
      return `${start}-${end}`;
    },
    handleReportClick(report) {
      const token = localStorage.getItem('token');
      if (!token) {
        this.$router.push({ name: 'Login' });
        return;
      }

      axios.get(`http://139.199.168.63:8080/admin/getWeeklyReport/${report.reportId}`, {
        headers: {
          'Authorization': token,
          'Accept': 'application/json'
        }
      })
      .then(response => {
        if (response.data.code === 0) {
          this.reportDetails = response.data.data;
          this.weekDates = this.getWeekDates(report.startTime, report.endTime);
          this.dialogVisible = true;
        } else if (response.data.code === 401) {
          this.handleSessionExpired();
        } else {
          this.$message.error('获取报告详情失败');
        }
      })
      .catch(error => {
        console.error('Error fetching report details:', error);
        this.$message.error('获取报告详情失败');
      });
    },
    getWeekDates(startTime, endTime) {
      const dates = [];
      const start = new Date(startTime);
      const end = new Date(endTime);
      let current = new Date(start);
      while (current <= end) {
        dates.push(current.toISOString().split('T')[0]); // Use ISO string for the API call
        current.setDate(current.getDate() + 1);
      }
      return dates;
    },
    fetchDailyReport() {
      if (!this.selectedDate) {
        this.$message.warning('请选择日期');
        return;
      }

      const token = localStorage.getItem('token');
      if (!token) {
        this.$router.push({ name: 'Login' });
        return;
      }

      axios.get(`http://139.199.168.63:8080/admin/getDailyReport/${this.selectedDate}`, {
        headers: {
          'Authorization': token,
          'Accept': 'application/json'
        }
      })
      .then(response => {
        if (response.data.code === 0) {
          this.dailyReportDetails = response.data.data;
        } else if (response.data.code === 401) {
          this.handleSessionExpired();
        } else {
          this.$message.error('获取日报详情失败');
        }
      })
      .catch(error => {
        console.error('Error fetching daily report details:', error);
        this.$message.error('获取日报详情失败');
      });
    },
    formatReportContent(content) {
      try {
        const parsedContent = JSON.parse(content);
        let formattedContent = '';
        if (parsedContent.任务信息) {
          formattedContent += '任务信息:\n';
          for (const [key, value] of Object.entries(parsedContent.任务信息)) {
            formattedContent += `${key}: ${value}\n`;
          }
        }
        if (parsedContent.流程详细信息) {
          formattedContent += '\n流程详细信息:\n';
          parsedContent.流程详细信息.forEach((detail, index) => {
            formattedContent += `流程${index + 1}:\n`;
            for (const [key, value] of Object.entries(detail)) {
              formattedContent += `  ${key}: ${value}\n`;
            }
          });
        }
        return formattedContent;
      } catch (error) {
        return content;
      }
    },
    startAutoRefresh() {
      this.autoRefreshInterval = setInterval(() => {
        if (this.countdown > 0) {
          this.countdown--;
        } else {
          this.fetchReports();
          this.countdown = 300;
        }
      }, 1000);
    },
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
    handleSessionExpired() {
      this.$message.error('会话已过期，请重新登录');
      this.confirmLogout();
    }
  },
  beforeDestroy() {
    clearInterval(this.autoRefreshInterval);
  }
};
</script>
