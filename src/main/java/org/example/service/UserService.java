package org.example.service;

import org.example.entity.User;
import org.example.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;


@Service   // 表示这是一个服务类，Spring 会将其注册为一个 Bean，方便在其他地方（比如控制器）注入使用。
public class UserService {
    @Autowired  //将 UserRepository 注入到 UserService 中，从而使 UserService 可以调用 UserRepository 提供的函数。
    private UserRepository userRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;  //  操作 Redis 的工具类



    // 获取所有的用户
    public List<User> findAll(){
        return userRepository.findAll();
    }

    public User findByUsername(String username) {
        String redisKey = "user"+username;
        User cachedUser = (User) redisTemplate.opsForValue().get(redisKey);

        if(cachedUser!=null){
            System.out.println("✅ 从 Redis 获取用户：" + username);
            return cachedUser;
        }

        // **2️⃣ 如果 Redis 没有，从数据库查询**
        User user = userRepository.findByUsername(username);
        if(user!=null){
            System.out.println("⏳ 从数据库查询用户：" + username);
            // **3️⃣ 把用户存入 Redis，缓存 10 分钟**
            redisTemplate.opsForValue().set(redisKey, user, 10, TimeUnit.MINUTES);
        }

        return user;
    }

    public User findByPhone(String phone){
        return userRepository.findByPhone(phone);
    }

    public User findByUsernameAndPassword(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password);
    }

    // 保存用户（注册或更新）
    public User saveUser(User user) {
        User savedUser = userRepository.save(user); // Redis 存的数据是 User 对象 因此Redis存的数据能正确反序列化回 User 对象
        redisTemplate.opsForValue().set("user:" + savedUser.getUsername(), savedUser, 10, TimeUnit.MINUTES);

        System.out.println("🚀 用户保存成功，更新 Redis 缓存：" + savedUser.getUsername());
        return savedUser;
    }

    // 通过定义的主键删除用户
    public void deleteUser(Long id){
        User user = userRepository.findById(id).orElse(null);
        if(user!=null){
            userRepository.deleteById(id);

            // **清理 Redis 缓存**
            String redisKey = "user:" + user.getUsername();
            redisTemplate.delete(redisKey);

            System.out.println("🗑️ 用户删除成功，清理 Redis 缓存：" + user.getUsername());
        }
    }
}
