package com.victor.gestao_de_estoque.mapper;

import com.victor.gestao_de_estoque.controller.request.FornecedorRequest;
import com.victor.gestao_de_estoque.controller.response.FornecedorResponse;
import com.victor.gestao_de_estoque.model.Fornecedor;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FornecedorMapper {

    public Fornecedor toEntity(FornecedorRequest request) {
        return Fornecedor.builder()
                .nome(request.nome())
                .telefone(request.telefone())
                .email(request.email())
                .endereco(request.endereco())
                .build();
    }

    public FornecedorResponse toResponse(Fornecedor fornecedor) {
        return FornecedorResponse.builder()
                .id(fornecedor.getId())
                .nome(fornecedor.getNome())
                .telefone(fornecedor.getTelefone())
                .email(fornecedor.getEmail())
                .endereco(fornecedor.getEndereco())
                .build();
    }
}
