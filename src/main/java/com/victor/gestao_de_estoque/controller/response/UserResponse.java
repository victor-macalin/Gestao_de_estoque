package com.victor.gestao_de_estoque.controller.response;

import lombok.Builder;

@Builder
public record UserResponse(
        Long id,

        String name,

        String email,

        String password
) {
}
