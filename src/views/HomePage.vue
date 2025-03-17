<template>
  <div class="homepage">
    <h1>商品列表</h1>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading">加载中...</div>

    <!-- 商品展示区 -->
    <div v-else>
      <div v-for="product in products" :key="product.id" class="product-card">
        <img :src="product.image || 'https://via.placeholder.com/200x200?text=No+Image'" alt="product.name" class="product-image"/>
        <div class="product-info">
          <h3>{{ product.name }}</h3>
          <p>价格: ￥{{ product.price }}</p>
          <p>{{ product.description }}</p>
          <button @click="addToCart(product)">加入购物车</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      products: [],  // 商品数据
      loading: true,  // 加载状态
    };
  },
  mounted() {
    this.fetchProducts();  // 页面加载时获取商品数据
  },
  methods: {
    // 获取商品数据
    async fetchProducts() {
      this.loading = true;  // 开始加载
      try {
        const token = localStorage.getItem("jwt_token"); // 获取 JWT Token
        const response = await axios.get('http://localhost:8080/products', {
          headers: {
            Authorization: `Bearer ${token}`,  // 设置 Authorization 请求头
          },
        });
        this.products = response.data;  // 获取商品数据
      } catch (error) {
        console.error("获取商品失败", error);  // 错误处理
      } finally {
        this.loading = false;  // 请求完成，更新 loading 状态
      }
    },

    // 添加商品到购物车
    addToCart(product) {
      console.log("加入购物车：", product);
      // 添加商品到购物车的功能逻辑（例如调用后端 API）
    },
  },
};
</script>

<style scoped>
/* 页面总体布局 */
.homepage {
  padding: 20px;
  text-align: center;
}

/* 加载中状态 */
.loading {
  font-size: 24px;
  color: #888;
}

/* 商品卡片布局 */
.product-card {
  display: inline-block;
  width: 220px;
  margin: 15px;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  background-color: #fff;
  text-align: center;
}

/* 商品图片样式 */
.product-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 8px;
  margin-bottom: 10px;
}

/* 商品信息样式 */
.product-info h3 {
  font-size: 18px;
  margin-bottom: 10px;
}

.product-info p {
  font-size: 14px;
  color: #666;
}

.product-info button {
  margin-top: 10px;
  padding: 10px 20px;
  background-color: #ff9900;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.product-info button:hover {
  background-color: #e68900;
}
</style>
