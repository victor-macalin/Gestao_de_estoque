package com.victor.gestao_de_estoque.controller;

import com.victor.gestao_de_estoque.controller.request.FornecedorRequest;
import com.victor.gestao_de_estoque.controller.response.FornecedorResponse;
import com.victor.gestao_de_estoque.exception.ResourceNotFound;
import com.victor.gestao_de_estoque.mapper.FornecedorMapper;
import com.victor.gestao_de_estoque.model.Fornecedor;
import com.victor.gestao_de_estoque.service.FornecedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fornecedores")
@RequiredArgsConstructor
public class FornecedorController {

    private final FornecedorService fornecedorService;

    @PostMapping
    public ResponseEntity<FornecedorResponse> create(
            @RequestBody @Valid FornecedorRequest fornecedorRequest) {

        Fornecedor fornecedor = FornecedorMapper.toEntity(fornecedorRequest);

        Fornecedor fornecedorSave = fornecedorService.create(fornecedor);

        FornecedorResponse response = FornecedorMapper.toResponse(fornecedorSave);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<FornecedorResponse>> read() {

        List<FornecedorResponse> fornecedores = fornecedorService.read()
                .stream()
                .map(FornecedorMapper::toResponse)
                .toList();

        return ResponseEntity.ok(fornecedores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FornecedorResponse> findById(@PathVariable Long id) {

        try {
            Fornecedor fornecedor = fornecedorService.findById(id);
            return ResponseEntity.ok(FornecedorMapper.toResponse(fornecedor));
        } catch (ResourceNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<FornecedorResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid FornecedorRequest fornecedorRequest) {

        Fornecedor fornecedor = FornecedorMapper.toEntity(fornecedorRequest);

        Fornecedor fornecedorUpdate = fornecedorService.update(id, fornecedor);

        FornecedorResponse response = FornecedorMapper.toResponse(fornecedorUpdate);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        fornecedorService.delete(id);

        return ResponseEntity.ok().build();
    }
}
