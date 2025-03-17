package org.example.repository;


import org.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface UserRepository extends JpaRepository<User,Long>{   // Long 主键类型 下面这三个是新写的方法 再service被调用 实际上还有很多已经有的函数
    User findByUsername(String username);  // JPA实际上会查询Username这个键 大小写均可查到

    User findByPhone(String phone);

    User findByUsernameAndPassword(String username, String password);

    @Modifying
    @Query("UPDATE User u SET u.token = :token WHERE u.username = :username")
    void updateUserToken(@Param("username") String username, @Param("token") String token);

    @Query("SELECT u.token FROM User u WHERE u.username = :username")
    String findUserTokenByUsername(@Param("username") String username);
}
