package com.victor.gestao_de_estoque.controller.response;

import com.victor.gestao_de_estoque.enums.TipoMovimentacao;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Builder
public record MovimentacaoEstoqueResponse(
        Long id,                   // ID da movimentação
        Long produtoId,            // ID do produto
        String produtoNome,        // Nome do produto
        Integer quantidade,        // Quantidade movimentada
        TipoMovimentacao tipo,     // ENTRADA ou SAÍDA
        OffsetDateTime data,       // Data da movimentação
        Integer estoqueAtual,      // Estoque atual do produto após a movimentação
        BigDecimal preco,          // Preço do produto (opcional, útil para relatórios)
        String observacao          // Observação
) {}
