<template>
  <div>
    <h1>购物车</h1>
    <div v-if="cart.length === 0">购物车为空</div>
    <div v-else>
      <div v-for="item in cart" :key="item.product.id" class="cart-item">
        <img :src="item.product.image || 'https://via.placeholder.com/200x200?text=No+Image'"
             alt="item.product.name" class="product-image"/>
        <h3>{{ item.product.name }}</h3>
        <p>数量: {{ item.quantity }}</p>
        <p>总价: ￥{{ item.product.price * item.quantity }}</p>
        <button @click="removeFromCart(item)">删除</button>
      </div>
      <button @click="checkout">结算</button>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      cart: [],
      userId: 1,  // 假设当前用户的 ID 为 1
    };
  },
  mounted() {
    this.fetchCart();
  },
  methods: {
    // 获取购物车内容
    fetchCart() {
      axios.get('http://localhost:8080/cart', { params: { userId: this.userId } })
          .then(response => {
            this.cart = response.data;
          })
          .catch(error => {
            console.error("获取购物车失败", error);
          });
    },

    // 从购物车中删除商品
    removeFromCart(item) {
      axios.delete('http://localhost:8080/cart/remove', {
        params: {
          userId: this.userId,
          productId: item.product.id
        }
      })
          .then(response => {
            console.log("商品已从购物车中删除");
            // 删除成功后更新购物车数据
            this.fetchCart();
          })
          .catch(error => {
            console.error("删除购物车商品失败", error);
          });
    },

    // 结算
    checkout() {
      this.$router.push('/order'); // 跳转到订单页面
    }
  }
}
</script>

<style>
.cart-item {
  border: 1px solid #ddd;
  margin: 20px;
  padding: 20px;
  display: flex;
  align-items: center;
}
.product-image {
  width: 50px;
  height: 50px;
  margin-right: 20px;
}
</style>
