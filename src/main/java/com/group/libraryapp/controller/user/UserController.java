package com.group.libraryapp.controller.user;

import com.group.libraryapp.dto.user.request.UserCreateRequest;
import com.group.libraryapp.dto.user.request.UserUpdateRequest;
import com.group.libraryapp.dto.user.response.UserResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RestController
public class UserController {

    private final JdbcTemplate jdbcTemplate;

    public UserController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/user")
    public void saveUser(@RequestBody UserCreateRequest request) {
        String sql = "INSERT INTO user (name, age) VALUE (?, ?)";
        jdbcTemplate.update(sql, request.getName(), request.getAge());
    }

    @GetMapping("/user")
    public List<UserResponse> getUsers() {
        String sql = "SELECT * FROM user";

//        return jdbcTemplate.query(sql, new RowMapper<UserResponse>() {
//            @Override
//            public UserResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            long id = rs.getLong("id");
            String name = rs.getString("name");
            int age = rs.getInt("age");
            return new UserResponse(id, name, age);
        });
    }

    @PutMapping("/user")
    public void updateUser(@RequestBody UserUpdateRequest request) {
        String readSql = "SELECT * FROM user WHERE id = ?";
        // jdbcTemplate.query(readSql, (rs, rowNum) -> 0, request.getId())
        // user가 존재하면 List에 0이 있고, user가 존재하지 않는다면 빈 List가 반환된다.
        boolean isUserNotExist = jdbcTemplate.query(readSql, (rs, rowNum) -> 0, request.getId())
                                                .isEmpty();
        if (isUserNotExist) { // user가 존재하지 않는다면
            throw new IllegalArgumentException();
        }

        String sql = "UPDATE user SET name = ? WHERE id = ?";
        jdbcTemplate.update(sql, request.getName(), request.getId());
    }

    @DeleteMapping("/user")
    public void deleteUser(@RequestParam String name) {
        String readSql = "SELECT * FROM user WHERE name = ?";
        boolean isUserNotExist = jdbcTemplate.query(readSql, (rs, rowNum) -> 0, name).isEmpty();
        if (isUserNotExist) {
            throw new IllegalArgumentException();
        }

        String sql = "DELETE FROM user WHERE name = ?";
        jdbcTemplate.update(sql, name);
    }
}
