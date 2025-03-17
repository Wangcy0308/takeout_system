import { createApp } from 'vue';  // 从 vue 引入 createApp 方法
import App from './app.vue';  // 引入根组件 App.vue
import router from './router';  // 引入路由配置

// 创建 Vue 应用
createApp(App)
    .use(router)
    .mount('#app');