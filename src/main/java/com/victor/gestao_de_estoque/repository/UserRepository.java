package com.victor.gestao_de_estoque.repository;

import com.victor.gestao_de_estoque.model.User;
import org.apache.catalina.UserDatabase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<UserDetails> findByEmail (String email);
}
