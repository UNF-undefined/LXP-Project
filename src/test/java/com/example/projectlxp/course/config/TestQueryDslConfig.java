package com.example.projectlxp.course.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.context.annotation.Bean;

import com.querydsl.jpa.impl.JPAQueryFactory;

public class TestQueryDslConfig {

    @PersistenceContext private EntityManager em;

    @Bean
    public JPAQueryFactory testJpaQueryFactory() {
        return new JPAQueryFactory(em);
    }
}
