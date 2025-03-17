package org.example.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // JPA默认会采用字段名和变量名的驼峰命名和下划线命名之间的转换规则（userId -> user_id）。如果无法常规转化 需要用@column(name = "user_id")注解

    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // PENDING, PAID, CANCELED

    private LocalDateTime createdTime;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL, fetch = FetchType.EAGER)  //order 是 OrderDetail 中的一个字段）。
    private List<OrderDetail> orderDetails;

    public Order(){

    }

    public enum OrderStatus{
        PENDING,PAID, CANCELED, DELIVERED
    }

    // Getters 和 Setters
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) { // 必须
        this.userId = userId;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) { // 必须
        this.totalAmount = totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) { // 必须
        this.status = status;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) { // 必须
        this.createdTime = createdTime;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) { // 必须
        this.orderDetails = orderDetails;
    }
}

