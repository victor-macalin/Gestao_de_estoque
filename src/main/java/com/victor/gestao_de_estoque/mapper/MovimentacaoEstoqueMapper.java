package com.victor.gestao_de_estoque.mapper;

import com.victor.gestao_de_estoque.controller.request.MovimentacaoEstoqueRequest;
import com.victor.gestao_de_estoque.controller.response.MovimentacaoEstoqueResponse;
import com.victor.gestao_de_estoque.model.MovimentacaoEstoque;
import com.victor.gestao_de_estoque.model.Produto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MovimentacaoEstoqueMapper {

    // Converte Request em Entidade
    public MovimentacaoEstoque toEntity(MovimentacaoEstoqueRequest request, Produto produto) {
        return MovimentacaoEstoque.builder()
                .produto(produto)
                .quantidade(request.quantidade())
                .tipo(request.tipo())
                .observacao(request.observacao())
                .data(java.time.OffsetDateTime.now()) // registra a hora da movimentação
                .build();
    }

    // Converte Entidade em Response, incluindo estoque atual
    public MovimentacaoEstoqueResponse toResponse(MovimentacaoEstoque movimentacao, Integer estoqueAtual) {
        return MovimentacaoEstoqueResponse.builder()
                .id(movimentacao.getId())
                .produtoId(movimentacao.getProduto().getId())
                .produtoNome(movimentacao.getProduto().getName())
                .quantidade(movimentacao.getQuantidade())
                .tipo(movimentacao.getTipo())
                .data(movimentacao.getData())
                .estoqueAtual(estoqueAtual) // calculado no service
                .preco(movimentacao.getProduto().getPreco())
                .observacao(movimentacao.getObservacao())
                .build();
    }
}
