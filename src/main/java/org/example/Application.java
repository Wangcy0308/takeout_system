package org.example;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;  // 启动嵌入式 Web 服务器（如 Tomcat） 内置在应用程序里的小型Web服务器，你的应用程序可以直接运行它
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication  // 组合注解，用于标记主类，并启用 Spring Boot 的核心功能  @:spring的注解机制
@EnableRabbit  // 让 Spring 监听 RabbitMQ 队列

public class Application {
    public static void main(String []args){
        SpringApplication.run(Application.class,args);
    }
}
