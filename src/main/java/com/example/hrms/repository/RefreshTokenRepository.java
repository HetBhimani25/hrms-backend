package com.example.hrms.repository;

import com.example.hrms.entity.RefreshToken;
import com.example.hrms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {

    Optional<RefreshToken> findByToken(String token);
//    Optional<RefreshToken> findByUser(User user);
    List<RefreshToken> findByUser(User user);
    void deleteAllByUser(User user);
}
