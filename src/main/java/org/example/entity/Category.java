package org.example.entity;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增
    private Long id;

    private String name; // 分类名称

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.EAGER) // 一对多关系 当对分类（Category）进行操作时，自动对相关的商品（Product）进行相应的操作。如删除分类时，自动删除关联的所有商品。2.外键字段 是由 Product 类中的 category 属性管理。
    private List<Product> products; // 该分类下的商品列表

    public Category() {
        // 必须存在无参构造函数
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
