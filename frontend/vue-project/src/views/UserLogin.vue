<template>
    <div class="login-container">
      <h2>登录</h2>
      <form @submit.prevent="login" class="login-form">
        <div class="form-group">
          <label for="username">用户名:</label>
          <input
            id="username"
            v-model="username"
            type="text"
            placeholder="输入你的用户名"
            required
            class="form-control"
          >
        </div>
        <div class="form-group">
          <label for="password">密码:</label>
          <input
            type="password"
            id="password"
            v-model="password"
            placeholder="输入你的密码"
            required
            class="form-control"
          >
        </div>
        <button type="submit" class="btn login-btn">登录</button>
        <button type="button" class="btn admin-login-btn" @click="adminLogin">管理员登录</button>
        <router-link to="/register" class="btn register-btn">账户注册</router-link>
      </form>
    </div>
  </template>
  
  <script>
export default {
  data() {
    return {
      username: "",
      password: "",
    };
  },
  methods: {
    login() {
      const requestOptions = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name: this.username,
          password: this.password
        })
      };

      fetch('http://139.199.168.63:8080/user/login', requestOptions)
        .then(response => {
          if (!response.ok) {
            throw new Error('Failed to login with status: ' + response.status);
          }
          return response.json();
        })
        .then(data => {
          if (data && data.data && data.data.token) {
            localStorage.setItem('isLoggedIn', 'true');
            localStorage.setItem('token', data.data.token);
            localStorage.setItem('username', data.data.name);

            // 调用获取用户信息的方法
            this.fetchUserInfo(data.data.token);
          } else {
            alert('Login failed. Please check your credentials.');
          }
        })
        .catch(error => {
          console.error('Login error:', error);
          alert('There was a problem with the login. Please try again.');
        });
    },
    adminLogin() {
      const requestOptions = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name: this.username,
          password: this.password
        })
      };

      fetch('http://139.199.168.63:8080/admin/login', requestOptions)
        .then(response => {
          if (!response.ok) {
            throw new Error('Failed to login with status: ' + response.status);
          }
          return response.json();
        })
        .then(data => {
          if (data && data.data && data.data.token) {
            localStorage.setItem('isLoggedIn', 'true');
            localStorage.setItem('token', data.data.token);
            localStorage.setItem('username', data.data.name);
            localStorage.setItem('isAdmin', 'true');

            // 直接跳转到 ProcessManagement 页面
            this.$router.push({ name: 'ProcessManagement' });
          } else {
            alert('Login failed. Please check your credentials.');
          }
        })
        .catch(error => {
          console.error('Login error:', error);
          alert('There was a problem with the login. Please try again.');
        });
    },
    fetchUserInfo(token) {
      fetch('http://139.199.168.63:8080/user/getUserInfo', {
        method: 'POST',
        headers: {
          'Authorization': token,
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        }
      })
      .then(response => {
        if (!response.ok) {
          throw new Error('Failed to fetch user info with status: ' + response.status);
        }
        return response.json();
      })
      .then(data => {
        if (data && data.data) {
          // 保存用户的所有信息
          localStorage.setItem('userInfo', JSON.stringify(data.data));

          // 重定向到AccountManage页面
          this.$router.push({ name: 'AccountManage' });
        } else {
          alert('Failed to fetch user info.');
        }
      })
      .catch(error => {
        console.error('Fetch user info error:', error);
        alert('There was a problem with fetching user info. Please try again.');
      });
    }
  }
}
</script>

  
  
    
  <style scoped>
  .login-container {
    max-width: 360px;
    margin: 50px auto;
    padding: 20px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    border-radius: 8px;
    background-color: #ffffff;
  }
  
  .login-form {
    display: flex;
    flex-direction: column;
  }
  
  .form-group {
    margin-bottom: 15px;
  }
  
  .form-control {
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 4px;
    font-size: 16px;
  }
  
  .btn {
    padding: 10px 20px;
    border: none;
    border-radius: 4px;
    font-size: 16px;
    cursor: pointer;
    transition: background-color 0.3s, transform 0.2s;
  }
  
  .login-btn {
    background-color: #007bff;
    color: white;
  }
  
  .login-btn:hover {
    background-color: #0056b3;
  }
  .admin-login-btn {
  background-color: #ff5733;
  color: white;
  margin-top: 10px;
}

.admin-login-btn:hover {
  background-color: #c70039;
}
  .register-btn {
    margin-top: 15px;
    text-align: center;
    background-color: #4caf50; /* Green background */
    color: white;
  }
  
  .register-btn:hover {
    background-color: #46a049;
  }
  
  input[type="text"],
  input[type="password"] {
    display: block;
    width: 100%;
    box-sizing: border-box; /* Ensures padding does not affect width */
  }
  </style>
  