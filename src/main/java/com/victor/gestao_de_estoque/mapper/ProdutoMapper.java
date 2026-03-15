package com.victor.gestao_de_estoque.mapper;

import com.victor.gestao_de_estoque.controller.response.ProdutoResponse;
import com.victor.gestao_de_estoque.model.Produto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProdutoMapper {
    public static ProdutoResponse toResponseEstoque(Produto produto) {

        String fornecedorNome = null;

        if (produto.getFornecedor() != null) {
            fornecedorNome = produto.getFornecedor().getNome();
        }

        return new ProdutoResponse(
                produto.getId(),
                produto.getName(),
                produto.getDescricao(),
                produto.getPreco(),
                fornecedorNome,
                0   // valor padrão
        );
    }


    public ProdutoResponse toResponse (Produto produto, Integer estoqueAtual) {
        String fornecedorNome = null;

        if (produto.getFornecedor() != null) {
            fornecedorNome = produto.getFornecedor().getNome();
        }
        return new ProdutoResponse (
                produto.getId(),
                produto.getName(),
                produto.getDescricao(),
                produto.getPreco(),
                fornecedorNome,
                estoqueAtual
                        );
    }
}
