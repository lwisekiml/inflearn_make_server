package com.group.libraryapp.service.user;

import com.group.libraryapp.dto.domain.user.User;
import com.group.libraryapp.dto.domain.user.UserRepository;
import com.group.libraryapp.dto.user.request.UserCreateRequest;
import com.group.libraryapp.dto.user.request.UserUpdateRequest;
import com.group.libraryapp.dto.user.response.UserResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceV2 {

    private final UserRepository userRepository;

    public UserServiceV2(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void saveUser(UserCreateRequest request) {
        userRepository.save(new User(request.getName(), request.getAge())); // User 객체 반환
//        User user = userRepository.save(new User(request.getName(), request.getAge()));
//        System.out.println("user.getId() = " + user.getId());

    }

    public List<UserResponse> getUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
//                .map(user -> new UserResponse(user.getId(), user.getName(), user.getAge()))
                .map(UserResponse::new) // UserResponse(User user)
                .collect(Collectors.toList());
    }

    public void updateUser(UserUpdateRequest request) {
        User user = userRepository.findById(request.getId())
                .orElseThrow(IllegalArgumentException::new); // User 가 비어있는 경우 에러를 던짐

        user.updateName(request.getName());
        userRepository.save(user);
    }

    public void deleteUser(String name) {
        // SELECT * FROM user WHERE name = ?
        User user = userRepository.findByName(name);
        if (user == null) {
            throw new IllegalArgumentException();
        }

        userRepository.delete(user);
    }
}
