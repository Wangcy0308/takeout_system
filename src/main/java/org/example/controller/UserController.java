package org.example.controller;
//控制器的作用是提供 RESTful API，供前端调用。比如：
//注册用户的接口
//查询用户的接口
//删除用户的接口

import org.example.entity.User;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController  // Spring 会将返回值自动转换为 JSON 响应 转化为json格式
@RequestMapping("/users") //为控制器提供基础路径，所有接口的路径都以 /users 开头

public class UserController {
    @Autowired // @Autowired 的自动注入发生在 Spring 应用启动时
    private UserService userService;

    @GetMapping//假设你在访问 http://localhost:8080/users 时，Spring 会调用 getAllUsers 方法，返回所有用户的数据。响应的结果是json格式
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/{username}")
    public User getUserByUsername(@PathVariable String username){ //@pathvariable username 参数的值将从 URL 路径中提取。
        return userService.findByUsername(username);
    }

    @GetMapping("/phone/{phone}")
    public User getUserByPhone(@PathVariable String phone) {
        return userService.findByPhone(phone);
    }

    @PostMapping//假设发送一个 POST 请求到 http://localhost:8080/users Spring会调用createOrUpdateUser方法将json转化为User保存到数据库
    public User createOrUpdateUser(@RequestBody User user) { //请求体中的 JSON 数据会被自动解析为 User 对象
        return userService.saveUser(user);
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return "用户删除成功！";
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestParam String username, @RequestParam String password) {
        User user =  userService.findByUsernameAndPassword(username, password);
        if (user != null) {
            return ResponseEntity.ok(user); // 用户登录成功，返回用户信息
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 用户名或密码错误
        }
    }
}
