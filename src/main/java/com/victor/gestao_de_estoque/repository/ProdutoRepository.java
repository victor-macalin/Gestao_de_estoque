package com.victor.gestao_de_estoque.repository;

import com.victor.gestao_de_estoque.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository <Produto, Long>{
}
