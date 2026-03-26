CREATE TABLE produto
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255)   NOT NULL,
    descricao  TEXT,
    quantidade INTEGER        NOT NULL CHECK (quantidade > 0),
    preco      NUMERIC(10, 2) NOT NULL
);