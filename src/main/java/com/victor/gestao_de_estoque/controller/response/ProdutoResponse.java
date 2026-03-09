package com.victor.gestao_de_estoque.controller.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProdutoResponse(Long id, String name, String descricao, Integer quantidade, BigDecimal preco) {
}
