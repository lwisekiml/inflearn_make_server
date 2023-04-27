package com.group.libraryapp.dto.domain.user;

import javax.persistence.*;

@Entity
public class User {

    @Id // id라고 알려줌
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment와 IDENTITY 전략과 매칭
    private Long id = null;

    @Column(nullable = false, length = 20, name = "name") // DB컬럼의 name과 같은 이름이라 생략 가능
    private String name;

    private  Integer age;

    protected User() {}

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
