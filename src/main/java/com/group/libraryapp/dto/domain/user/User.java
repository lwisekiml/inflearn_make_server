package com.group.libraryapp.dto.domain.user;

import javax.persistence.*;

@Entity
public class User {

    @Id // id라고 알려줌, 필드를 primary key로 간주
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment와 IDENTITY 전략과 매칭
    private Long id = null;

    // 객체의 필드와 Table의 필드를 매핑
    // DB컬럼의 name과 같은 이름이라 name = "name" 는 생략 가능하다. 컬럼 자체도 생략할 수 있다?
    @Column(nullable = false, length = 20, name = "name")
    private String name;

    private  Integer age;

    protected User() {} // JPA를 사용하기 위해서는 기본 생성자가 꼭 필요

    public User(String name, Integer age) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(String.format("잘못된 name(%s)이 들어왓습니다.", name));
        }
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public Long getId() {
        return id;
    }

    public void updateName(String name) {
        this.name = name;
    }
}
