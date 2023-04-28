package com.group.libraryapp.dto.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 유저가 없다면 null 반환
    // SELECT * FROM user WHERE name = ?
    User findByName(String name);

    boolean existsByName(String name);

    long countByAge(Integer age); // 해당 나이를 가진 명수 반환
}
