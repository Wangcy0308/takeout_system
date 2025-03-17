import { createRouter, createWebHistory } from "vue-router";
import HomePage from "@/views/HomePage.vue";
import CartPage from "@/views/CartPage.vue";
import OrderPage from "@/views/OrderPage.vue";
import LoginPage from "@/views/Login.vue";

const routes = [
    { path: "/", component: HomePage },
    { path: "/cart", component: CartPage, meta: { requiresAuth: true } },
    { path: "/order", component: OrderPage, meta: { requiresAuth: true } },
    { path: "/login", component: LoginPage },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

// 路由守卫：保护需要身份验证的页面
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem("jwt_token");
    if (to.matched.some(record => record.meta.requiresAuth) && !token) {
        next("/login");  // 如果用户没有登录，跳转到登录页面
    } else {
        next();  // 继续访问目标页面
    }
});

export default router;
