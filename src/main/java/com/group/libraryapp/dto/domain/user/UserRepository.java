package com.group.libraryapp.dto.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    // 유저가 없다면 null 반환
    // SELECT * FROM user WHERE name = ?
    User findByName(String name);


}
