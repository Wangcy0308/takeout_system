package org.example.util;

import org.example.entity.User;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;

@Component
public class PasswordUpdater implements CommandLineRunner {  // 自动调用run方法
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {   // 加密所有的明文密码
        // 遍历数据库中的所有用户
        for (User user : userRepository.findAll()) {
            // 如果密码不是以 "$2a$" 开头，说明未加密
            if (!user.getPassword().startsWith("$2a$")) {
                // 使用 BCrypt 对密码进行加密
                String encryptedPassword = passwordEncoder.encode(user.getPassword()); // 对明文密码进行加密
                user.setPassword(encryptedPassword); // 更新密码
                userRepository.save(user); // 保存到数据库
                System.out.println("加密并更新用户密码：" + user.getUsername());
            }
        }
    }

}