package com.example.projectlxp.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.projectlxp.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {}
