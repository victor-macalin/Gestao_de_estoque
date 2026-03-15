package com.victor.gestao_de_estoque.controller.response;

import lombok.Builder;

@Builder
public record FornecedorResponse(

        Long id,
        String nome,
        String telefone,
        String email,
        String endereco

) {}
