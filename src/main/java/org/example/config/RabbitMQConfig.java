package org.example.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration  //配置类 可以在方法上使用 @Bean 来定义 Spring 管理的 Bean（将方法注册为bean）。 Spring 确保 @Configuration 注解的类不会被多次实例化，保证 @Bean 方法返回的是同一个单例对象。
// 当我们使用 @Configuration 定义一个 Spring 配置类时，Spring 会使用 CGLIB 生成该类的代理子类，从而确保 @Bean 方法返回的 是同一个 Bean 实例。
// 这个 RabbitMQConfig 配置类 确实使用了代理子类，但它的目的不是增加方法逻辑，而是确保 @Bean 方法只执行一次，保证单例模式。
public class RabbitMQConfig {

    public static final String ORDER_QUEUE = "orderQueue";
    public static final String DELAY_QUEUE = "delayQueue";
    public static final String ORDER_EXCHANGE = "orderExchange";
    public static final String DELAY_EXCHANGE = "delayExchange";
    public static final String ROUTING_KEY = "order.routingKey";
    public static final String DELAY_ROUTING_KEY = "delay.routingKey";


//    这些 @Bean 会在 Spring Boot 启动时 被 RabbitMQ 自动注册，然后 生产者和消费者会使用这些队列和交换机。
//
//       📌 使用 1：RabbitMQ 配置自动注册
//    当 Spring Boot 启动时，它会：
//
//    自动创建 orderQueue 和 delayQueue 到 RabbitMQ
//    自动创建 orderExchange 到 RabbitMQ
//    自动绑定 orderQueue 到 orderExchange

    // Spring 使用 CGLIB 代理 @Configuration 类，重写（拦截） @Bean 方法，让 Spring 在调用 orderQueue() 时：
    //第一次调用 → Spring 发现 @Bean，执行 new Queue(ORDER_QUEUE, true)，并缓存这个对象。
    //后续调用 → Spring 直接从缓存里返回这个对象，而不是再执行 new Queue(...)。

    // 订单队列（普通队列）
    @Bean //  把 Queue orderQueue 作为 Spring 的 Bean 注册到 Spring 容器   Spring 只会创建一个 orderQueue 实例（单例模式）
    public Queue orderQueue() {
        return new Queue(ORDER_QUEUE, true);
    }

    @Bean
    public Queue delayQueue() {
        return QueueBuilder.durable(DELAY_QUEUE)
                .withArgument("x-dead-letter-exchange", ORDER_EXCHANGE)  // 死信队列交换机
                .withArgument("x-dead-letter-routing-key", ROUTING_KEY)  // 死信队列路由键
                .withArgument("x-message-ttl", 60000)  // 0.1分钟超时（单位：毫秒）
                .build();
    }


    // 订单交换机（Direct 直连）
    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(ORDER_EXCHANGE);
    }

    // 延迟交换机
    @Bean
    public DirectExchange delayExchange() {
        return new DirectExchange(DELAY_EXCHANGE);
    }

    // 绑定队列到交换机
    @Bean
    public Binding bindingOrderQueue() {
        return BindingBuilder.bind(orderQueue()).to(orderExchange()).with(ROUTING_KEY);
    }

    @Bean
    public Binding bindingDelayQueue() {
        return BindingBuilder.bind(delayQueue()).to(delayExchange()).with(DELAY_ROUTING_KEY);
    }


}
