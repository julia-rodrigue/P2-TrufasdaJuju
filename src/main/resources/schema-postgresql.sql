CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS produto (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    preco NUMERIC(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS pedido (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome_cliente VARCHAR(150) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    data_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    forma_pagamento VARCHAR(50) NOT NULL,
    data_retirada DATE,
    status VARCHAR(50) DEFAULT 'PENDENTE'
);

CREATE TABLE IF NOT EXISTS item_pedido (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pedido_id UUID NOT NULL,
    produto_id UUID NOT NULL,
    quantidade INTEGER NOT NULL,
    preco_unitario NUMERIC(10,2) NOT NULL,
    FOREIGN KEY (pedido_id) REFERENCES pedido(id) ON DELETE CASCADE,
    FOREIGN KEY (produto_id) REFERENCES produto(id)
);

INSERT INTO produto (nome, descricao, preco) VALUES
('Trufa', 'Trufa recheada ', 4.00),
('Espeto', 'Espeto de morango', 9.00),
('Torta', 'Torta de limão', 10.00),
('Mousse', 'Mousse sabores', 8.00);

SELECT * FROM produto;
SELECT * FROM pedido;
SELECT * FROM item_pedido;