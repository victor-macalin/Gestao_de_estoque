package com.victor.gestao_de_estoque.service;

import com.victor.gestao_de_estoque.controller.request.ProdutoRequest;
import com.victor.gestao_de_estoque.controller.response.ProdutoResponse;
import com.victor.gestao_de_estoque.exception.ResourceNotFound;
import com.victor.gestao_de_estoque.mapper.ProdutoMapper;
import com.victor.gestao_de_estoque.model.Fornecedor;
import com.victor.gestao_de_estoque.model.MovimentacaoEstoque;
import com.victor.gestao_de_estoque.model.Produto;
import com.victor.gestao_de_estoque.repository.FornecedorRepository;
import com.victor.gestao_de_estoque.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final FornecedorRepository fornecedorRepository;
    private final MovimentacaoEstoqueService movimentacaoEstoqueService;

    // CREATE
    public Produto create(Produto produto) {
        Fornecedor fornecedor = null;

        if (produto.getFornecedor() != null && produto.getFornecedor().getId() != null) {

            fornecedor = fornecedorRepository
                    .findById(produto.getFornecedor().getId())
                    .orElseThrow(() -> new ResourceNotFound("Fornecedor não encontrado"));

            produto.setFornecedor(fornecedor);
        }

        return produtoRepository.save(produto);
    }

    //READ
    public List<Produto> read() {
        return produtoRepository.findAll();
    }

    // FIND BY ID
    public Produto findById(Long id) {
        return   produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Produto não encontrado"));
    }

    // UPDATE
    public Produto update(Long id, ProdutoRequest produtoRequest) {

        Fornecedor fornecedor = fornecedorRepository.findById(produtoRequest.fornecedorId())
                .orElseThrow(() -> new ResourceNotFound("Fornecedor não encontrado"));


        Produto produto1 = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Produto não encontrado"));


        produto1.setName(produtoRequest.name());
        produto1.setDescricao(produtoRequest.descricao());
        produto1.setPreco(produtoRequest.preco());
        produto1.setFornecedor(fornecedor);
        return produtoRepository.save(produto1);
    }

    //DELETE
    public void delete(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Produto não encontrado"));
        produtoRepository.delete(produto);
    }
    // DELETE ALL
    public void deleteAll () {
        produtoRepository.deleteAll();
    }
}
