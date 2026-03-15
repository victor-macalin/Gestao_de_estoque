package com.victor.gestao_de_estoque.service;

import com.victor.gestao_de_estoque.exception.ResourceNotFound;
import com.victor.gestao_de_estoque.model.Fornecedor;
import com.victor.gestao_de_estoque.repository.FornecedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;

    // CREATE
    public Fornecedor create(Fornecedor fornecedor) {
        return fornecedorRepository.save(fornecedor);
    }

    // READ ALL
    public List<Fornecedor> read() {
        List<Fornecedor> fornecedores = fornecedorRepository.findAll();

        if (fornecedores.isEmpty()) {
            throw new ResourceNotFound("Nenhum fornecedor encontrado");
        }

        return fornecedores;
    }

    // READ BY ID
    public Fornecedor findById(Long id) {
        return fornecedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Fornecedor não encontrado"));
    }

    // UPDATE
    public Fornecedor update(Long id, Fornecedor fornecedor) {

        Fornecedor fornecedorAtual = fornecedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Fornecedor não encontrado"));

        fornecedorAtual.setNome(fornecedor.getNome());
        fornecedorAtual.setTelefone(fornecedor.getTelefone());
        fornecedorAtual.setEmail(fornecedor.getEmail());
        fornecedorAtual.setEndereco(fornecedor.getEndereco());

        return fornecedorRepository.save(fornecedorAtual);
    }

    // DELETE
    public void delete(Long id) {

        Fornecedor fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Fornecedor não encontrado"));

        fornecedorRepository.delete(fornecedor);
    }
}
