package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        System.out.println("CORS config is being applied!");
        registry.addMapping("/**") // 允许所有路径
                .allowedOrigins("http://localhost:8081") // 允许来自 http://localhost:8081 的请求
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的 HTTP 方法
                .allowedHeaders("*") // 允许所有的请求头
                .allowCredentials(true); // 允许发送 cookies
    }


}


//既然前端和后端使用不同的端口（8080 和 8081），这就涉及到一个 CORS（跨源资源共享） 的问题。浏览器默认会阻止不同源（不同端口算作不同源）之间的请求