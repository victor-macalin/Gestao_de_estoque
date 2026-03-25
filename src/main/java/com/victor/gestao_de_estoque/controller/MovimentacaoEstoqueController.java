package com.victor.gestao_de_estoque.controller;
import com.victor.gestao_de_estoque.controller.request.MovimentacaoEstoqueRequest;
import com.victor.gestao_de_estoque.controller.response.MovimentacaoEstoqueResponse;
import com.victor.gestao_de_estoque.model.MovimentacaoEstoque;
import com.victor.gestao_de_estoque.model.Produto;
import com.victor.gestao_de_estoque.service.MovimentacaoEstoqueService;
import com.victor.gestao_de_estoque.mapper.MovimentacaoEstoqueMapper;
import com.victor.gestao_de_estoque.service.ProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.PrivateKey;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/movimentacoes")
@RequiredArgsConstructor
public class MovimentacaoEstoqueController {

    private final MovimentacaoEstoqueService movimentacaoService;

    private final ProdutoService produtoService;


    // Criar nova movimentação
    @PostMapping
    public ResponseEntity<MovimentacaoEstoqueResponse> create(
            @RequestBody @Valid MovimentacaoEstoqueRequest request
    ) {

        MovimentacaoEstoque movimentacao = movimentacaoService.create(request);

        Integer estoqueAtual = movimentacaoService
                .calcularEstoqueAtual(movimentacao.getProduto().getId());

        MovimentacaoEstoqueResponse response =
                MovimentacaoEstoqueMapper.toResponse(movimentacao, estoqueAtual);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Listar todas movimentações
    @GetMapping
    public ResponseEntity<List<MovimentacaoEstoqueResponse>> readAll() {
        List<MovimentacaoEstoque> movimentacoes = movimentacaoService.readAll();
        List<MovimentacaoEstoqueResponse> responses = movimentacoes.stream()
                .map(mov -> {
                    Integer estoqueAtual = movimentacaoService.calcularEstoqueAtual(mov.getProduto().getId());
                    return MovimentacaoEstoqueMapper.toResponse(mov, estoqueAtual);
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // Listar movimentações por produto
    @GetMapping("/produto/{produtoId}")
    public ResponseEntity<List<MovimentacaoEstoqueResponse>> readByProduto(
            @PathVariable Long produtoId
    ) {
        List<MovimentacaoEstoque> movimentacoes = movimentacaoService.readByProduto(produtoId);
        List<MovimentacaoEstoqueResponse> responses = movimentacoes.stream()
                .map(mov -> {
                    Integer estoqueAtual = movimentacaoService.calcularEstoqueAtual(produtoId);
                    return MovimentacaoEstoqueMapper.toResponse(mov, estoqueAtual);
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}
