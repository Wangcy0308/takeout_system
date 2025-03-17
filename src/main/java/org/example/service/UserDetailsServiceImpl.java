package org.example.service;

import org.example.entity.User;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {  //UserDetails 是 Spring Security 提供的一个接口，用于封装用户的核心身份信息和权限信息
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles("USER") // 示例角色
                .build();
    }

}


//用户登录：
//
//用户通过 /auth/login 接口提交用户名和密码。
//Spring Security 的 AuthenticationManager 使用自定义的 UserDetailsServiceImpl 从数据库验证用户信息（用户名、密码）。
//如果认证成功，生成 JWT Token 并返回给客户端。
//用户访问受保护资源：
//
//每次请求，客户端在请求头中携带 Authorization: Bearer <token>。
//JwtAuthenticationFilter 负责拦截请求，解析并验证 JWT Token 的合法性。
//如果 Token 有效，从中提取用户信息，设置用户的认证状态到 Spring Security 的上下文中。
//受保护资源校验：
//
//通过 Spring Security 配置的规则，受保护的接口（非 /auth/login）必须经过认证。
//如果用户认证成功，Spring Security 会允许访问；否则拒绝。