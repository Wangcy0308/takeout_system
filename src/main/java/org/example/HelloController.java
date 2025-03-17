package org.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

//    如果有人发一个 GET 请求到 /hello（比如在浏览器访问 http://localhost:8080/hello），
//    Java 会自动调用 sayHello() 这个方法。
//    然后返回 "Hello, World!" 这句话到浏览器
    @GetMapping("/hello")
    public String sayHello(){
        return "Hello, Spring Boot!";
    }
}
