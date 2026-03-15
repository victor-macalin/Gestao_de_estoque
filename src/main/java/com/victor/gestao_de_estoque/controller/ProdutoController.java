package com.victor.gestao_de_estoque.controller;

import com.victor.gestao_de_estoque.controller.request.ProdutoRequest;
import com.victor.gestao_de_estoque.controller.response.ProdutoResponse;
import com.victor.gestao_de_estoque.exception.ResourceNotFound;
import com.victor.gestao_de_estoque.mapper.ProdutoMapper;
import com.victor.gestao_de_estoque.model.Fornecedor;
import com.victor.gestao_de_estoque.model.Produto;
import com.victor.gestao_de_estoque.repository.FornecedorRepository;
import com.victor.gestao_de_estoque.repository.MovimentacaoEstoqueRepository;
import com.victor.gestao_de_estoque.service.ProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/produto")
@RequiredArgsConstructor
public class ProdutoController {
    private final ProdutoService produtoService;
    private final FornecedorRepository fornecedorRepository;
    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    @PostMapping
    public ResponseEntity<ProdutoResponse> create(@RequestBody @Valid ProdutoRequest produtoRequest) {
        Fornecedor fornecedor = null;
        if (produtoRequest.fornecedorId() != null) {
            fornecedor = fornecedorRepository.findById(produtoRequest.fornecedorId())
                    .orElseThrow(() -> new ResourceNotFound("Fornecedor nao encontrado"));
        }
        Produto produto = Produto
                .builder()
                    .name(produtoRequest.name())
                    .descricao(produtoRequest.descricao())
                    .preco(produtoRequest.preco())
                    .fornecedor(fornecedor)
                    .build();
        Produto produtoSave = produtoService.create(produto);
        ProdutoResponse produtoResponse = ProdutoMapper.toResponseEstoque(produtoSave);
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoResponse);
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponse>> read() {
        List<ProdutoResponse> produtoResponses = produtoService.read()
                .stream()
                .map(produto -> {
                    Integer estoqueAtual = movimentacaoEstoqueRepository.calcularEstoqueAtual(produto.getId());
                    return ProdutoMapper.toResponse(produto, estoqueAtual);
                })
                .toList();

        return ResponseEntity.ok(produtoResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> findById(@PathVariable Long id) {
        try {
            Produto produto = produtoService.findById(id);
            Integer estoqueAtual = movimentacaoEstoqueRepository.calcularEstoqueAtual(id);
            ProdutoResponse produtoResponse = ProdutoMapper.toResponse(produto, estoqueAtual);
            return ResponseEntity.ok(produtoResponse);

        } catch (ResourceNotFound resourceNotFound) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponse> update(@PathVariable Long id, @RequestBody @Valid ProdutoRequest produtoRequest) {
        Produto produtoUpdate = produtoService.update(id, produtoRequest);
        Integer estoqueAtual = movimentacaoEstoqueRepository.calcularEstoqueAtual(id);
        ProdutoResponse produtoSave = ProdutoMapper.toResponse(produtoUpdate, estoqueAtual);
        return ResponseEntity.ok(produtoSave);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        produtoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteALl () {
        produtoService.deleteAll();
     return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
