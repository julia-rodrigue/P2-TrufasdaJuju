CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS usuario (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    telefone VARCHAR(20) NOT NULL,
    data_nascimento DATE NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    tipo VARCHAR(20) NOT NULL DEFAULT 'CLIENTE',
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS imagem_produto (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome_arquivo VARCHAR(255) NOT NULL,
    caminho VARCHAR(255) NOT NULL,
    data_upload TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS produto (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    preco NUMERIC(10,2) NOT NULL,
    imagem_id UUID,
    FOREIGN KEY (imagem_id) REFERENCES imagem_produto(id)
);

CREATE TABLE IF NOT EXISTS pedido (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID,
    nome_cliente VARCHAR(150) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    data_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    forma_pagamento VARCHAR(50) NOT NULL,
    data_retirada DATE,
    status VARCHAR(50) DEFAULT 'PENDENTE',
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
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

-- ============================================================================
-- SEED IDEMPOTENTE
-- Este script roda em TODA inicializacao (spring.sql.init.mode=always) contra
-- um banco PERSISTENTE (Render). Por isso cada INSERT de seed so insere se o
-- registro ainda nao existir. Sem isso, as imagens eram duplicadas a cada boot
-- e a subquery (SELECT id ... WHERE caminho = ...) passava a retornar mais de
-- uma linha, fazendo o INSERT em produto falhar (statement #8).
-- ============================================================================

-- Imagens ja existentes no projeto (pasta static/img).
-- Insere cada imagem apenas se aquele caminho ainda nao estiver cadastrado.
INSERT INTO imagem_produto (nome_arquivo, caminho)
SELECT v.nome_arquivo, v.caminho
FROM (VALUES
    ('trufas.jpeg',         '/img/trufas.jpeg'),
    ('moussemaracuja.jpeg', '/img/moussemaracuja.jpeg'),
    ('moussesensacao.jpeg', '/img/moussesensacao.jpeg'),
    ('mousseninho.jpeg',    '/img/mousseninho.jpeg'),
    ('mousselimao.jpeg',    '/img/mousselimao.jpeg'),
    ('tortamorango.jpeg',   '/img/tortamorango.jpeg'),
    ('tortalimao.jpeg',     '/img/tortalimao.jpeg'),
    ('centotrufa.jpeg',     '/img/centotrufa.jpeg'),
    ('merengue.jpeg',       '/img/merengue.jpeg'),
    ('espetomorango.jpeg',  '/img/espetomorango.jpeg')
) AS v(nome_arquivo, caminho)
WHERE NOT EXISTS (
    SELECT 1 FROM imagem_produto ip WHERE ip.caminho = v.caminho
);

-- Produtos. Insere cada produto apenas se ainda nao existir um com o mesmo nome.
-- A subquery usa LIMIT 1 como protecao extra: mesmo que existam imagens
-- duplicadas, retorna no maximo uma linha e nunca quebra o INSERT.
INSERT INTO produto (nome, descricao, preco, imagem_id)
SELECT v.nome, v.descricao, v.preco,
       (SELECT id FROM imagem_produto WHERE caminho = v.caminho ORDER BY data_upload LIMIT 1)
FROM (VALUES
    ('Trufas',               'Trufas de chocolate sortidas',                                 4.00,  '/img/trufas.jpeg'),
    ('Mousse de maracuja',   'Mousse cremoso de maracuja',                                   8.00,  '/img/moussemaracuja.jpeg'),
    ('Mousse sensacao',      'Mousse sabor sensacao',                                        8.00,  '/img/moussesensacao.jpeg'),
    ('Mousse de ninho',      'Mousse cremoso sabor ninho',                                   8.00,  '/img/mousseninho.jpeg'),
    ('Mousse de limao',      'Mousse cremoso de limao',                                      8.00,  '/img/mousselimao.jpeg'),
    ('Torta de morango',     'Torta com massa crocante e recheio de morango',               10.00,  '/img/tortamorango.jpeg'),
    ('Torta de limao',       'Torta com massa crocante recheio de mousse de limao e glace', 10.00,  '/img/tortalimao.jpeg'),
    ('Cento de mini trufas', 'Cento de mini trufas sortidas',                               35.00,  '/img/centotrufa.jpeg'),
    ('Merengue',             'Merengue artesanal',                                           6.00,  '/img/merengue.jpeg'),
    ('Espeto de morango',    'Espeto de morango banhado em chocolate com confeitos',         9.00,  '/img/espetomorango.jpeg')
) AS v(nome, descricao, preco, caminho)
WHERE NOT EXISTS (
    SELECT 1 FROM produto p WHERE p.nome = v.nome
);

-- Usuario administrador padrao (login: admin@trufasdajuju.com / senha: admin123)
-- Hash SHA-256 de "admin123": 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9
INSERT INTO usuario (nome, email, cpf, telefone, data_nascimento, senha_hash, tipo) VALUES
('Administrador', 'admin@trufasdajuju.com', '00000000000', '(00) 00000-0000', '2000-01-01',
 '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMINISTRADOR')
ON CONFLICT (email) DO NOTHING;