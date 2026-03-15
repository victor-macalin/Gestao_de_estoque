package com.victor.gestao_de_estoque.model;

import com.victor.gestao_de_estoque.enums.TipoMovimentacao;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "movimentacao_estoque")
public class MovimentacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relação ManyToOne com Produto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    // Quantidade movimentada
    @NotNull
    @Positive
    private Integer quantidade;

    // Tipo da movimentação: ENTRADA ou SAÍDA
    @NotNull
    @Enumerated(EnumType.STRING)
    private TipoMovimentacao tipo;

    // Data e hora da movimentação com fuso horário
    @NotNull
    @Column(name = "data_movimentacao", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime data;

    // Observação opcional (ajustes, devoluções, etc.)
    private String observacao;

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public TipoMovimentacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimentacao tipo) {
        this.tipo = tipo;
    }

    public OffsetDateTime getData() {
        return data;
    }

    public void setData(OffsetDateTime data) {
        this.data = data;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
