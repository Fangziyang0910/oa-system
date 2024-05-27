<template>
  <div class="register-container">
    <h2>账户注册</h2>
    <form
      @submit.prevent="register"
      class="register-form"
    >
      <div class="form-group">
        <label for="name">用户名:</label>
        <input
          id="name"
          v-model="user.name"
          required
          type="text"
          class="form-control"
          placeholder="请输入用户名"
        >
      </div>
      <div class="form-group">
        <label for="email">电子邮件:</label>
        <input
          type="email"
          id="email"
          v-model="user.email"
          required
          class="form-control"
          placeholder="请输入电子邮件"
        >
      </div>
      <div class="form-group">
        <label for="password">密码:</label>
        <input
          type="password"
          id="password"
          v-model="user.password"
          required
          class="form-control"
          placeholder="请输入密码"
        >
      </div>
      <div class="form-group">
        <label for="city">城市:</label>
        <input
          type="text"
          id="city"
          v-model="user.city"
          class="form-control"
          placeholder="请输入城市"
        >
      </div>
      <div class="form-group">
        <label for="phone">电话:</label>
        <input
          type="tel"
          id="phone"
          v-model="user.phone"
          class="form-control"
          placeholder="请输入电话号码"
        >
      </div>
      <button
        type="submit"
        class="btn register-btn"
      >注册</button>
    </form>
  </div>
</template>

<script>
export default {
  data() {
    return {
      user: {
        name: "",
        email: "",
        password: "",
        city: "",
        phone: "",
        permissionId: 0  // Assuming a default permission ID if not collected from form
      },
    };
  },
  methods: {
    register() {
      const requestOptions = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(this.user)
      };

      fetch('http://139.199.168.63:8080/user/register', requestOptions)
        .then(response => {
          if (!response.ok) {
            throw new Error('Registration failed with status: ' + response.status);
          }
          return response.json();
        })
        .then(data => {
          if (data.code === 0) {
            console.log('Registration successful', data.msg);
            this.$router.push({ name: "Login" });  // Redirect to login page after successful registration
          } else {
            alert('Registration failed: ' + data.msg);
          }
        })
        .catch(error => {
          console.error('Registration error:', error);
          alert('There was a problem with the registration.');
        });
    }
  }
};
</script>

  
    
<style scoped>
.register-container {
  max-width: 400px;  /* Adjusted for slightly wider form fields */
  margin: 50px auto;
  padding: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15); /* Slightly deeper shadow for more pop */
  border-radius: 10px; /* More pronounced rounded corners */
  background-color: #ffffff;
}

.register-form {
  display: flex;
  flex-direction: column;
}

.form-group {
  margin-bottom: 20px; /* Increased margin for better spacing */
}

.form-control {
  padding: 12px; /* More padding for a better text entry experience */
  border: 1px solid #ccc;
  border-radius: 5px; /* Slightly rounded borders for a softer look */
  font-size: 16px;
  outline: none; /* Removes the default focus outline */
}

.form-control:focus {
  border-color: #007bff; /* Highlight focus with a blue border */
  box-shadow: inset 0 1px 1px rgba(0,0,0,0.075), 0 0 8px rgba(0,123,255,0.6); /* Subtle glow effect */
}

.btn {
  padding: 12px 20px;
  border: none;
  border-radius: 5px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s, transform 0.2s, box-shadow 0.2s;
  background-color: #007bff;
  color: white;
}

.register-btn:hover, .register-btn:focus {
  background-color: #0056b3;
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2); /* More pronounced shadow on hover/focus */
}

input[type="text"],
input[type="password"],
input[type="email"], /* Added styles for email field */
input[type="tel"] { /* Added styles for telephone field */
  display: block;
  width: 100%;
  box-sizing: border-box;
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .register-container {
    padding: 15px;
  }
}
</style>
