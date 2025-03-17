package org.example.filter;

import io.jsonwebtoken.Claims;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.example.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
// 首次登录时：
//
//只会触发 /auth/login 控制器的代码（验证用户名和密码，生成 JWT）。
//不会触发 JwtAuthenticationFilter 的 doFilterInternal。
//后续请求时（带着 JWT 的请求）：
//
//会触发 JwtAuthenticationFilter 的 doFilterInternal 方法，用来验证客户端携带的 JWT 是否有效。
@Component   // 该类是一个 Spring Bean，Spring 会自动实例化并将该类注册到应用的上下文中。
public class JwtAuthenticationFilter extends OncePerRequestFilter {  // 这个文件Web 请求处理过程中由 Servlet 容器自动触发和调用的 检查请求头中的JWT token

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {  // 是的，doFilterInternal 代码是在用户登录成功后，客户端携带 JWT Token 发起后续请求时才会执行
        // 在用户后续访问受保护的接口（比如 /products 或 /cart）时，客户端会在请求头中携带 Authorization: Bearer <JWT_TOKEN>。
        // 从请求头中获取 Authorization 字段
        String header = request.getHeader("Authorization");
        System.out.println("[DEBUG] Authorization Header: " + header); // 输出请求头中的 Authorization 字段

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7); // 提取出 JWT Token（去掉 "Bearer " 前缀）
            System.out.println("[DEBUG] Extracted Token: " + token); // 输出解析出的 Token

            Claims claims;

            try {
                claims = jwtUtil.parseToken(token); // 解析 Token  也会看是否过期
                System.out.println("[DEBUG] Parsed Claims: " + claims); // 输出解析后的 Claims
            } catch (Exception e) {
                System.out.println("[ERROR] Token Parsing Failed: " + e.getMessage()); // 如果解析失败，打印错误信息
                chain.doFilter(request, response); // 直接跳过过滤器
                return;
            }

            if (claims != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                String username = claims.getSubject();
                System.out.println("[DEBUG] Token Subject (Username): " + username); // 输出解析出的用户名

                // 从数据库中获取用户的最新 Token
                User user = userRepository.findByUsername(username);
                if (user != null) {
                    System.out.println("[DEBUG] User Token in Database: " + user.getToken());
                    System.out.println("[DEBUG] Client Token: " + token);

                    if (token.equals(user.getToken())) {
                        System.out.println("[DEBUG] Token Matches Database Record"); // 确认 Token 和数据库中一致
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                username, null, null);
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    } else {
                        System.out.println("[WARN] Token Does Not Match Database Record"); // 如果不一致，输出警告
                    }
                } else {
                    System.out.println("[WARN] User Not Found in Database"); // 如果用户未找到，输出警告
                }
            }
        } else {
            System.out.println("[WARN] Authorization Header Missing or Malformed"); // 如果没有 Authorization 头，输出警告
        }

        // 将请求传递给下一个过滤器
        chain.doFilter(request, response);
    }
}
