package com.victor.gestao_de_estoque.repository;

import com.victor.gestao_de_estoque.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
