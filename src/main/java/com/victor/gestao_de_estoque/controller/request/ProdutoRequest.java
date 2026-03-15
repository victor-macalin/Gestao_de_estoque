package com.victor.gestao_de_estoque.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProdutoRequest(
        @NotNull
        String name,

        String descricao,

        BigDecimal preco,

        Long fornecedorId

) {
}
