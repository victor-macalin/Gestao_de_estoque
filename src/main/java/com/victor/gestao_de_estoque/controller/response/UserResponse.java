package com.victor.gestao_de_estoque.controller.response;

import lombok.Builder;

@Builder
public record UserResponse(
        Long id,

        String nome,

        String email,

        String password
) {
}
