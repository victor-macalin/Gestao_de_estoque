package com.victor.gestao_de_estoque.mapper;

import com.victor.gestao_de_estoque.controller.request.ProdutoRequest;
import com.victor.gestao_de_estoque.controller.response.ProdutoResponse;
import com.victor.gestao_de_estoque.model.Produto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProdutoMapper {
    public Produto toProduto (ProdutoRequest produtoRequest) {
        return Produto
                .builder()
                .name(produtoRequest.name())
                .descricao(produtoRequest.descricao())
                .quantidade(produtoRequest.quantidade())
                .preco(produtoRequest.preco())
                .build();
    }

    public ProdutoResponse toResponse (Produto produto) {
        return ProdutoResponse
                .builder()
                .name(produto.getName())
                .descricao(produto.getDescricao())
                .quantidade(produto.getQuantidade())
                .preco(produto.getPreco())
                .build();
    }
}
