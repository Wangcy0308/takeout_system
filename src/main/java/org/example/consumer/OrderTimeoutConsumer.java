package org.example.consumer;

import org.example.service.OrderService;
import org.example.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class OrderTimeoutConsumer {

    @Autowired
    private OrderService orderService;


//    订单创建时，OrderService 把 orderId 发送到 DELAY_QUEUE
//30 分钟后，RabbitMQ 把 orderId 转发到 ORDER_QUEUE
//    @RabbitListener 自动监听 ORDER_QUEUE，接收 orderId，然后调用 cancelOrder()
@RabbitListener(queues = RabbitMQConfig.ORDER_QUEUE)
public void processOrderTimeout(String message) {
    System.out.println("📩 收到超时订单消息：" + message);
    try {
        Long orderId = Long.parseLong(message);
        System.out.println("🛠 开始取消订单：" + orderId);
        orderService.cancelOrder(orderId);
        System.out.println("✅ 订单 " + orderId + " 取消成功");
    } catch (Exception e) {
        System.err.println("❌ 解析订单ID失败：" + message);
        e.printStackTrace();
    }
}
}
