package com.victor.gestao_de_estoque.controller.request;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProdutoRequest(String name, String descricao, Integer quantidade, BigDecimal preco) {
}
