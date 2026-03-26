
ALTER TABLE movimentacao_estoque
DROP
CONSTRAINT fkntvhwvqiu1irabekxqg4nt7ka;


ALTER TABLE movimentacao_estoque
    ADD CONSTRAINT fkntvhwvqiu1irabekxqg4nt7ka
        FOREIGN KEY (produto_id)
            REFERENCES produto (id)
            ON DELETE CASCADE;