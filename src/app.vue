<template>
  <div id="app">
    <h1>Welcome to Vue.js</h1>

    <!-- 登录表单：如果用户没有登录，显示这个表单 -->
    <div v-if="!isLoggedIn">
      <h2>登录</h2>
      <form @submit.prevent="login">
        <div>
          <label for="username">用户名:</label>
          <input type="text" id="username" v-model="username" required />
        </div>
        <div>
          <label for="password">密码:</label>
          <input type="password" id="password" v-model="password" required />
        </div>
        <button type="submit">登录</button>
      </form>
    </div>

    <!-- 登录后：如果用户已登录，显示导航栏和内容 -->
    <div v-else>
      <nav>
        <ul>
          <li><router-link to="/">Home</router-link></li>
          <li><router-link to="/cart">Cart</router-link></li>
          <li><router-link to="/order">Order</router-link></li>
        </ul>
        <!-- 显示登出按钮 -->
        <button @click="logout">登出</button>
      </nav>
      <!-- 这里是动态显示页面内容，具体的页面内容由路由控制 -->
      <router-view></router-view>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'App',
  data() {
    return {
      username: '',
      password: '',
      isLoggedIn: false, // 登录状态
    };
  },
  mounted() {
    // 检查是否已经登录
    if (localStorage.getItem('jwt_token')) { // 初始为空
      this.isLoggedIn = true; // 如果有 token 就说明用户已经登录
    }
  },
  methods: {
    // 登录逻辑
    async login() {
      try {
        const response = await axios.post('http://localhost:8080/auth/login', {
          username: this.username,
          password: this.password,
        });
        const token = response.data.token;
        localStorage.setItem('jwt_token', token); // 存储 JWT

        this.isLoggedIn = true; // 登录成功后更新状态
        this.$router.push('/'); // 跳转到首页
      } catch (error) {
        console.error('登录失败', error);
        alert('用户名或密码错误');
      }
    },

    // 登出逻辑
    logout() {
      // 清除 localStorage 中的 JWT token
      localStorage.removeItem('jwt_token');
      this.isLoggedIn = false; // 设置为未登录状态
      this.$router.push('/'); // 跳转到首页或登录页
    },
  },
};
</script>

<style scoped>
/* 样式部分根据需要添加 */
</style>
