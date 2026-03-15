package com.victor.gestao_de_estoque.controller.request;

import com.victor.gestao_de_estoque.enums.TipoMovimentacao;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record MovimentacaoEstoqueRequest(
        @NotNull
        Long produtoId,              // ID do produto movimentado

        @NotNull
        Integer quantidade,          // Quantidade movimentada

        @NotNull
        TipoMovimentacao tipo,       // ENTRADA ou SAÍDA

        String observacao            // Observação opcional
) {}
