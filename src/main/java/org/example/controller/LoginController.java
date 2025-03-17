package org.example.controller;

import org.example.entity.User;
import org.example.repository.UserRepository;
import org.example.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController  // 所有方法会默认返回 JSON 数据
@RequestMapping("/auth")  // 定义了类的路由前缀，表示这个控制器下的所有方法的路由都以 /auth 开头
public class LoginController {
    @Autowired
    private AuthenticationManager authenticationManager;  // 用来处理用户的认证逻辑

    @Autowired
    private JwtUtil jwtUtil;  // 生成和验证 JWT

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)); // 默认情况下，Spring Security 使用 DaoAuthenticationProvider（如果你没有自定义认证逻辑）去检查数据库中是否有与提供的用户名和密码匹配的用户。如果用户名和密码正确，身份验证就会通过。

        // 生成 JWT
        String token = jwtUtil.generateToken(username);
        System.out.println("[DEBUG] Generated Token for User: " + token); // 打印生成的 Token

        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setToken(token); // 将生成的 JWT 存储到 token 字段
            userRepository.save(user); // 保存到数据库
            System.out.println("[DEBUG] Saved Token to Database for User: " + username); // 打印保存到数据库的信息
        }

        // 返回 JWT 给客户端
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return response;
    }


}
