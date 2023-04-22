package com.group.libraryapp.dto.user.request;

public class UserCreateRequest {
    private String name;
    private Integer age; // 필수가 아니라 null일 수 있어서 Integer로 함

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}
