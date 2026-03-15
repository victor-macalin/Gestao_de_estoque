CREATE TABLE movimentacao_estoque
(
    id                BIGSERIAL PRIMARY KEY,

    produto_id        BIGINT                   NOT NULL,

    quantidade        INTEGER                  NOT NULL,

    tipo              VARCHAR(20)              NOT NULL,

    data_movimentacao TIMESTAMP WITH TIME ZONE NOT NULL,

    observacao        VARCHAR(255),

    CONSTRAINT fk_movimentacao_produto
        FOREIGN KEY (produto_id)
            REFERENCES produto (id)
            ON DELETE RESTRICT
);