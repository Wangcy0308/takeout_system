package org.example.service;

import org.aspectj.weaver.ast.Or;
import org.example.config.RabbitMQConfig;
import org.example.entity.Order;
import org.example.entity.CartItem;
import org.example.entity.OrderDetail;
import org.example.entity.Product;
import org.example.repository.CartRepository;
import org.example.repository.OrderRepository;
import org.example.repository.ProductRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate; // ✨ 新增 RabbitMQ 发送工具
    /**
     * 从购物车生成订单
     * @param userId 用户ID
     * @return 返回创建的订单对象
     */

    public Order createOrderFromCart(Long userId){
        try {
            // 查询购物车数据
            List<CartItem> cartItems = cartRepository.findByUserId(userId);
            if (cartItems.isEmpty()) {
                throw new RuntimeException("Shopping cart is empty");
            }

            List<OrderDetail> orderDetails = new ArrayList<>();
            double totalAmount = 0;

            // 遍历购物车，生成订单明细
            for (CartItem cartItem : cartItems) {
                Product product = productRepository.findById(cartItem.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                // 检查库存
                if (product.getStock() < cartItem.getQuantity()) {
                    throw new RuntimeException("Insufficient stock for product: " + product.getName());
                }

                // 减库存并保存
                product.setStock(product.getStock() - cartItem.getQuantity());
                productRepository.save(product);

                // 创建订单明细
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setProductId(product.getId());
                orderDetail.setProductName(product.getName());
                orderDetail.setQuantity(cartItem.getQuantity());
                orderDetail.setPrice(product.getPrice());
                orderDetails.add(orderDetail);

                totalAmount += product.getPrice() * cartItem.getQuantity();
            }

            // 创建订单
            Order order = new Order();
            order.setUserId(userId);
            order.setTotalAmount(totalAmount);
            order.setStatus(Order.OrderStatus.PENDING);
            order.setCreatedTime(LocalDateTime.now());


            // 绑定订单和订单明细的关系
            for (OrderDetail orderDetail : orderDetails) {
                orderDetail.setOrder(order);
            }

            order.setOrderDetails(orderDetails); // 在保存订单对象（Order）之前，先把订单明细（OrderDetail）的 order 字段设置好。 曾经这里报错过
            // 保存订单
            Order savedOrder = orderRepository.save(order);

            // 清空购物车
            cartRepository.deleteAll(cartItems);

            // ✨ 发送 RabbitMQ 消息，延迟 30 分钟检查订单状态
            rabbitTemplate.convertAndSend(RabbitMQConfig.DELAY_EXCHANGE, RabbitMQConfig.DELAY_ROUTING_KEY, savedOrder.getId());
            System.out.println("🚀 订单已创建，等待 30 分钟：" + savedOrder.getId());

            return savedOrder;
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常堆栈
            throw new RuntimeException("Error creating order: " + e.getMessage());
        }
    }

    public Order createOrder(Long userId, List<OrderDetail> orderDetails){// 处理订单 将订单明细保存到数据库
        /*这个函数是OrderController返回的 create order里面通过post请求输入json结构体会确保用户id
         输入参数：
         userId：用户ID，表示哪个用户创建了这个订单。
         orderDetails：订单明细列表，包含用户想要购买的商品及其数量。
         返回值：
         返回创建成功的 Order 对象，包含订单的所有信息。
         判断物品数量是否足够的逻辑不算冗余，因为这是一个非常重要的业务规则。即使在购物车阶段已经做了库存检查，在订单生成阶段仍然需要再做一次确认。原因如下
         用户 A 和用户 B 同时操作购物车，在库存只剩 1 件时，用户 A 和用户 B 都可以将该商品加入购物车。但当用户提交订单时，只有一个用户能成功减库存，另一个用户会因库存不足失败。
         */
        double totalAmount = 0;
        for(OrderDetail detail: orderDetails){  //findbyid 通过主键查找
            Product product = productRepository.findById(detail.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            if(product.getStock()<detail.getQuantity()){
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            product.setStock(product.getStock()-detail.getQuantity());
            productRepository.save(product);  // 这一步会将product的最新状态同步更新到数据库

            totalAmount += detail.getQuantity() * product.getPrice();
            detail.setProductName(product.getName());
            detail.setPrice(product.getPrice());
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setCreatedTime(LocalDateTime.now());
        order.setOrderDetails(orderDetails);

        for (OrderDetail detail : orderDetails) {
            detail.setOrder(order); // 这正是 正确的行为！所有 OrderDetail 都应该属于同一个订单，这样才能确保数据库中的数据关系是一对多（OneToMany）
        }
        return orderRepository.save(order);
    }

    /**
     * 根据用户ID获取订单列表
     * @param userId 用户ID
     * @return 返回订单列表
     */
    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    /**
     * 支付订单：直接将订单状态变更为 PAID
     * @param orderId 订单ID
     * @return 返回支付结果消息
     */
    public String PayOrder(Long orderId){
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if(optionalOrder.isEmpty()){
            throw new RuntimeException("Order not found!"); // 如果订单不存在
        }

        Order order = optionalOrder.get(); // 如果 Optional 包含的值存在，get() 方法将返回该值。

        //检查订单状态是否已经支付
        if(order.getStatus() == Order.OrderStatus.PAID){
            return "Order is already paid!";
        }

        // 简单模拟：直接将订单状态更新为 PAID
        order.setStatus(Order.OrderStatus.PAID);
        orderRepository.save(order);

        return "Payment successful!";
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if(orderOpt.isPresent()) {
            Order order = orderOpt.get();
            if (order.getStatus() == Order.OrderStatus.PENDING) {
                order.setStatus(Order.OrderStatus.CANCELED);
                orderRepository.saveAndFlush(order); // 立即提交事务
                System.out.println("❌ 订单超时未支付，已取消：" + orderId);
            } else {
                System.out.println("✅ 订单已支付，不需要取消：" + orderId);
            }
        }
    }
    //原子性：确保 cancelOrder 方法中的所有数据库操作要么全部成功，要么全部失败。
    //一致性：确保数据库从一个一致状态转换到另一个一致状态。
    //隔离性：确保多个并发事务不会互相干扰。
    //持久性：一旦事务提交，对数据库的修改就是永久性的。

    public void testRabbitMQ(Long orderId) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.DELAY_EXCHANGE, RabbitMQConfig.DELAY_ROUTING_KEY, String.valueOf(orderId));
        System.out.println("🐰 发送测试消息：" + orderId);
    }


    @Async
    @Transactional
    public void processOrderCancellation(Long orderId) {
        cancelOrder(orderId);
    }
}
