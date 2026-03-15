package com.victor.gestao_de_estoque.controller.request;

import jakarta.persistence.Column;
import lombok.Builder;

@Builder
public record UserRequest(
        @Column(nullable = false)
         String nome,

        @Column(nullable = false, unique = true)
         String email,

        @Column(nullable = false)
         String password
        ) {

}
