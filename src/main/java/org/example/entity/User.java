package org.example.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity  //告诉 Java 这是数据库里的表。  定义了User实体类 Spring知道这个实体类对应的数据库中的user表
public class User {
    @Id  // id是主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 每次新增记录时，自动生成下一个主键值。id:1,2,3……
    private Long id; // 主键

    private String username; // 包装类 可以为null  string则不可以为null
    private String password; // 一般这里面的变量要和数据库的名称一致 否则需要加一些标签对应
    private String phone;
    private String token; // 用来存储最新的 Token

    public User(){
    }

    public User(String username, String password, String phone){
        this.username = username;
        this.password = password;
        this.phone = phone;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // Getters 和 Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
