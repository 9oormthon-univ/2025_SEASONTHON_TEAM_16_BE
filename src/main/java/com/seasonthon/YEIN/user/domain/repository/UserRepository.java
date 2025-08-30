package com.seasonthon.YEIN.user.domain.repository;

import com.seasonthon.YEIN.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
