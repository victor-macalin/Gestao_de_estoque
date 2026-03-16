package com.victor.gestao_de_estoque.mapper;

import com.victor.gestao_de_estoque.controller.request.UserRequest;
import com.victor.gestao_de_estoque.controller.response.UserResponse;
import com.victor.gestao_de_estoque.model.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {

    public User toUser (UserRequest userRequest) {
        return User
                .builder()
                .name(userRequest.name())
                .email(userRequest.email())
                .password(userRequest.password())
                .build();
    }

    public UserResponse toResponse (User user) {
        return UserResponse
                .builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }



}
