<template>
  <div class="login-container">
    <div class="login-card">
      <h2 class="login-title">欢迎回来</h2>
      <form @submit.prevent="login" class="login-form">
        <div class="input-group">
          <label for="username" class="input-label">用户名</label>
          <input
              type="text"
              id="username"
              v-model="username"
              class="input-field"
              placeholder="请输入用户名"
              required
          />
        </div>
        <div class="input-group">
          <label for="password" class="input-label">密码</label>
          <input
              type="password"
              id="password"
              v-model="password"
              class="input-field"
              placeholder="请输入密码"
              required
          />
        </div>
        <button type="submit" class="login-button">登录</button>
      </form>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      username: "",
      password: "",
    };
  },
  methods: {
    async login() {
      try {
        const response = await axios.post("http://localhost:8080/auth/login", {
          username: this.username,
          password: this.password,
        });

        const token = response.data.token;
        localStorage.setItem("jwt_token", token);

        this.$router.push("/"); // 跳转到首页
      } catch (error) {
        console.error("登录失败", error);
        alert("用户名或密码错误");
      }
    },
  },
};
</script>

<style scoped>
/* 背景样式 */
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(to right, #4facfe, #00f2fe);
}

/* 卡片样式 */
.login-card {
  background-color: white;
  border-radius: 10px;
  padding: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

/* 标题样式 */
.login-title {
  font-size: 24px;
  font-weight: bold;
  text-align: center;
  color: #333;
  margin-bottom: 20px;
}

/* 表单元素 */
.input-group {
  margin-bottom: 15px;
}

.input-label {
  font-size: 14px;
  color: #555;
  margin-bottom: 5px;
}

.input-field {
  width: 100%;
  padding: 10px;
  font-size: 16px;
  border: 1px solid #ccc;
  border-radius: 5px;
  box-sizing: border-box;
  transition: border-color 0.3s ease;
}

.input-field:focus {
  border-color: #4facfe;
  outline: none;
}

/* 按钮样式 */
.login-button {
  width: 100%;
  padding: 12px;
  font-size: 16px;
  background-color: #4facfe;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.login-button:hover {
  background-color: #3b8fd4;
}
</style>
