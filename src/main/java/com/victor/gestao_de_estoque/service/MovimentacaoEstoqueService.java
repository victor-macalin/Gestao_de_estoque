package com.victor.gestao_de_estoque.service;

import com.victor.gestao_de_estoque.controller.request.MovimentacaoEstoqueRequest;
import com.victor.gestao_de_estoque.model.MovimentacaoEstoque;
import com.victor.gestao_de_estoque.model.Produto;
import com.victor.gestao_de_estoque.enums.TipoMovimentacao;
import com.victor.gestao_de_estoque.exception.ResourceNotFound;
import com.victor.gestao_de_estoque.mapper.MovimentacaoEstoqueMapper;
import com.victor.gestao_de_estoque.repository.MovimentacaoEstoqueRepository;
import com.victor.gestao_de_estoque.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovimentacaoEstoqueService {

    private final MovimentacaoEstoqueRepository movimentacaoRepository;
    private final ProdutoRepository produtoRepository;

    public MovimentacaoEstoqueService(
            MovimentacaoEstoqueRepository movimentacaoRepository,
            ProdutoRepository produtoRepository
    ) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.produtoRepository = produtoRepository;
    }

    // Criar movimentação
    public MovimentacaoEstoque create(MovimentacaoEstoqueRequest request) {

        Produto produto = produtoRepository.findById(request.produtoId())
                .orElseThrow(() -> new ResourceNotFound("Produto não encontrado"));

        MovimentacaoEstoque movimentacao =
                MovimentacaoEstoqueMapper.toEntity(request, produto);

        return movimentacaoRepository.save(movimentacao);
    }

    // Listar todas movimentações
    public List<MovimentacaoEstoque> readAll() {
        return movimentacaoRepository.findAll();
    }

    // Listar movimentações por produto
    public List<MovimentacaoEstoque> readByProduto(Long produtoId) {

        produtoRepository.findById(produtoId)
                .orElseThrow(() -> new ResourceNotFound("Produto não encontrado"));

        return movimentacaoRepository.findByProdutoId(produtoId);
    }

    // Calcular estoque atual
    public Integer calcularEstoqueAtual(Long produtoId) {

        List<MovimentacaoEstoque> movimentacoes =
                movimentacaoRepository.findByProdutoId(produtoId);

        return movimentacoes.stream()
                .mapToInt(m ->
                        m.getTipo() == TipoMovimentacao.ENTRADA
                                ? m.getQuantidade()
                                : -m.getQuantidade()
                )
                .sum();
    }
