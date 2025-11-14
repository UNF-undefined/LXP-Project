package com.example.projectlxp.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
@OpenAPIDefinition(
        info =
                @io.swagger.v3.oas.annotations.info.Info(
                        title = "LXP Project API docs",
                        version = "1.0"),
        tags = {
            @Tag(name = "Category", description = "카테고리 API"),
            @Tag(name = "Course", description = "강좌 API"),
            @Tag(name = "Section", description = "섹션 API"),
            @Tag(name = "Lecture", description = "강의 API"),
            @Tag(name = "Enrollment", description = "수강 API"),
            @Tag(name = "Review", description = "리뷰 API"),
            @Tag(name = "User", description = "유저 API")
        })
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        // Security Scheme 정의
        SecurityScheme securityScheme =
                new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .in(SecurityScheme.In.HEADER)
                        .name("Authorization");

        // Security Requirement 정의
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("BearerAuth");

        return new OpenAPI()
                .info(
                        new Info()
                                .title("Todolist API")
                                .description("Todolist Application API Documentation")
                                .version("v1.0"))
                .addSecurityItem(securityRequirement) // Security Requirement 추가
                .schemaRequirement("BearerAuth", securityScheme); // Security Scheme 추가
    }
}
