package com.omnigroup.omnibank.repository;

import com.omnigroup.omnibank.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByEmail(String email);

}
