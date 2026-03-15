package com.victor.gestao_de_estoque.controller.request;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record FornecedorRequest(

        @NotNull
        String nome,

        String telefone,

        @Email
        String email,

        String endereco

) {}
