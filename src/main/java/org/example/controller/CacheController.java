package org.example.controller;

import org.example.entity.User;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.Map;

@RestController //方法的返回值 会自动转换成 JSON（Spring Boot 会帮你做序列化）客户端的请求体 如果是 POST 或 PUT 请求，通常需要以 JSON 格式 传入数据
@RequestMapping("/cache")
public class CacheController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Autowired // @Autowired 的自动注入发生在 Spring 应用启动时
    private UserService userService;

    @PostMapping("./set")
    public String setCache(@RequestBody Map<String,String> request){
        String key = request.get("key");
        String value = request.get("value");

        // **先检查数据库是否存在该用户**
        User existingUser = userService.findByUsername(value);
        if (existingUser == null) {
            return "❌ 失败：数据库没有该用户，不能存入 Redis";
        }

        redisTemplate.opsForValue().set(key,value, 10, TimeUnit.MINUTES);  // opsForValue操作 Redis String 类型，适用于 缓存对象、简单 KV 存储

        return "缓存设置成功：" + key + " = " + value;
    }

    @GetMapping("./get")
    public String getCache(@RequestParam String key){
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? "缓存获取成功：" + key + " = " + value : "缓存未找到：" + key;
    }

    // 删除缓存
    @DeleteMapping("/delete")
    public String deleteCache(@RequestParam String key) {
        redisTemplate.delete(key);
        return "缓存删除成功：" + key;
    }

}
