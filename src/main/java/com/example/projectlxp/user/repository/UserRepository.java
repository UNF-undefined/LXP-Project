package com.example.projectlxp.user.repository;

import com.example.projectlxp.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    /*
    * 1. 이메일(로그인 ID)을 기준으로 사용자를 조회 합니다.
    * 1. 회원가입 시 이메일 중복 체크
    * 2. 로그인 시 이메일로 회원 조회
    *
    * findBy[필드명] 규칙에 따라 자동으로 쿼리를 생성해줌
    * @Param email 조회할 사용자의 이메일
    * @return 사용자가 존재하면 Optioncal<User>를, 없으면 Optional.empty()를 반환
    *
    * */

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);


}
