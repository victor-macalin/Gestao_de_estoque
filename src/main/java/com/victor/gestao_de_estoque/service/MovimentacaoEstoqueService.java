package com.victor.gestao_de_estoque.service;

import com.victor.gestao_de_estoque.controller.request.MovimentacaoEstoqueRequest;
import com.victor.gestao_de_estoque.controller.response.ProdutoResponse;
import com.victor.gestao_de_estoque.mapper.ProdutoMapper;
import com.victor.gestao_de_estoque.model.MovimentacaoEstoque;
import com.victor.gestao_de_estoque.model.Produto;
import com.victor.gestao_de_estoque.enums.TipoMovimentacao;
import com.victor.gestao_de_estoque.exception.ResourceNotFound;
import com.victor.gestao_de_estoque.mapper.MovimentacaoEstoqueMapper;
import com.victor.gestao_de_estoque.repository.MovimentacaoEstoqueRepository;
import com.victor.gestao_de_estoque.repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(MovimentacaoEstoqueService.class);

    // Criar movimentação
    @Transactional
    public MovimentacaoEstoque create(MovimentacaoEstoqueRequest request) {
        logger.info("Recebido tipo de movimentação: {}", request.tipo());
        Produto produto = produtoRepository.findById(request.produtoId())
                .orElseThrow(() -> new ResourceNotFound("Produto nao encontrado"));
        Integer estoqueAtual = calcularEstoqueAtual(request.produtoId());

        logger.info("Estoque atual do produto: {}", estoqueAtual);
        switch (request.tipo()) {
            case SAIDA -> {
                logger.info("Validando saída. Quantidade: {}, Estoque atual: {}", request.quantidade(), estoqueAtual);

                if (request.quantidade() > 0 && request.quantidade() < estoqueAtual) {
                    MovimentacaoEstoque movimentacao = MovimentacaoEstoqueMapper.toEntity(request, produto);
                    return movimentacaoRepository.save(movimentacao);
                } else {
                    throw new RuntimeException("A saída é maior do que o estoque existente");
                }
            }
            case ENTRADA -> {
                logger.info("Validando entrada. Quantidade: {}", request.quantidade());
                if (request.quantidade() > 0) {
                    MovimentacaoEstoque movimentacao = MovimentacaoEstoqueMapper.toEntity(request, produto);
                    return movimentacaoRepository.save(movimentacao);
                } else {
                    throw new RuntimeException("O valor deve ser maior que zero");
                }
            }
        }
        throw new IllegalArgumentException("Tipo de movimentação inválido");
    }

    // Listar todas movimentações
    public List<MovimentacaoEstoque> readAll() {
        return movimentacaoRepository.findAll();
    }

    // Listar movimentações por produto
    public List<MovimentacaoEstoque> readByProduto(Long produtoId) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new ResourceNotFound("Produto não encontrado"));

        return movimentacaoRepository.findByProdutoId(produto.getId());
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
}
