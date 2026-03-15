package com.victor.gestao_de_estoque.controller;

import com.victor.gestao_de_estoque.controller.request.UserRequest;
import com.victor.gestao_de_estoque.controller.response.UserResponse;
import com.victor.gestao_de_estoque.mapper.UserMapper;
import com.victor.gestao_de_estoque.model.User;
import com.victor.gestao_de_estoque.repository.UserRepository;
import com.victor.gestao_de_estoque.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(name = "/auth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register (@RequestBody UserRequest userRequest) {
        User user = userService.save(UserMapper.toUser(userRequest));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toResponse(user));
    }


}
