package com.victor.gestao_de_estoque.service;

import com.victor.gestao_de_estoque.model.User;
import com.victor.gestao_de_estoque.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User save (User user) {
        return userRepository.save(user);
    }
}
