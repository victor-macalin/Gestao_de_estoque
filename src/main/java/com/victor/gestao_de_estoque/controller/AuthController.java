package com.victor.gestao_de_estoque.controller;

import com.victor.gestao_de_estoque.config.TokenService;
import com.victor.gestao_de_estoque.controller.request.LoginRequest;
import com.victor.gestao_de_estoque.controller.request.UserRequest;
import com.victor.gestao_de_estoque.controller.response.LoginResponse;
import com.victor.gestao_de_estoque.controller.response.UserResponse;
import com.victor.gestao_de_estoque.exception.UsernameOrPasswordInvalidException;
import com.victor.gestao_de_estoque.mapper.UserMapper;
import com.victor.gestao_de_estoque.model.User;
import com.victor.gestao_de_estoque.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

        private final UserService userService;
        private final AuthenticationManager authenticationManager;
        private final TokenService tokenService;

        @PostMapping("/register")
        public ResponseEntity<UserResponse> register(@RequestBody UserRequest userRequest) {
            User user = userService.save(UserMapper.toUser(userRequest));
            return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toResponse(user));
        }

        @PostMapping("/login")
        public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
            try {
                UsernamePasswordAuthenticationToken userAndPass = new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());
                Authentication authentication = authenticationManager.authenticate(userAndPass);

                User user = (User) authentication.getPrincipal();
                String token = tokenService.generateToken(user);
                return ResponseEntity.ok(new LoginResponse(token));
            }catch (BadCredentialsException e) {
                throw new UsernameOrPasswordInvalidException("Email ou senha invalidos");
            }



    }
}
