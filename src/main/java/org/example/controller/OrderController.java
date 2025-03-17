package org.example.controller;

import org.example.entity.Order;
import org.example.entity.OrderDetail;
import org.example.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")  ////为控制器提供基础路径，所有接口的路径都以 /orders 开头
public class OrderController {
    @Autowired //通过 @Autowired，Spring 自动将 OrderService 注入到 OrderController 中，因此你可以在 OrderController 中直接使用 orderService，而无需自己创建实例。
    private OrderService orderService;

    @PostMapping// @RequestParam：从 URL 的查询参数 中获取数据（?key=value 的形式）。 @RequestBody：从 HTTP 请求体 中获取数据（通常是 JSON 格式的数据）。
    public Order createOrder(@RequestParam Long userId, @RequestBody List<OrderDetail> orderDetails){
        return orderService.createOrder(userId, orderDetails);
    }

    @PostMapping("/createFromCart")
    public Order createOrderFromCart(@RequestParam Long userId) {
        return orderService.createOrderFromCart(userId);
    }

    @GetMapping
    public List<Order> getOrderByUser(@RequestParam Long userId){
        return orderService.getOrdersByUser(userId);
    }

    //支付订单接口
    @PostMapping("/{id}/pay")
    public String payOrder(@PathVariable Long id){
        return orderService.PayOrder(id);
    }


    @GetMapping("/testMq/{orderId}")
    public String testRabbitMQ(@PathVariable Long orderId) {
        orderService.testRabbitMQ(orderId);
        return "消息已发送：" + orderId;
    }
}
