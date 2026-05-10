-- V2: Adiciona tipo de setor na tabela filas e código de senha em entradas_fila
-- Executado automaticamente pelo Flyway na próxima inicialização do backend.

-- 1. Adiciona coluna 'tipo' na tabela filas (COORDENACAO | FINANCEIRO | SECRETARIA)
ALTER TABLE filas
    ADD COLUMN tipo ENUM('COORDENACAO', 'FINANCEIRO', 'SECRETARIA')
        NOT NULL DEFAULT 'COORDENACAO'
        AFTER descricao;

-- 2. Cria index para busca por tipo de setor
CREATE INDEX idx_filas_tipo ON filas(tipo, ativa);

-- 3. Insere as 3 filas padrão do sistema (exige um usuário admin com id=1 já existente)
--    Em produção, execute via endpoint POST /api/filas após autenticar como ADMIN.
-- INSERT INTO filas (nome, descricao, tipo, ativa, max_capacidade, criado_por)
-- VALUES
--     ('Coordenação', 'Assuntos acadêmicos, aproveitamentos e orientações', 'COORDENACAO', TRUE, 100, 1),
--     ('Financeiro',  'Boletos, isenções, renegociações e pagamentos',       'FINANCEIRO',  TRUE, 100, 1),
--     ('Secretaria',  'Documentos, declarações, matrículas e históricos',    'SECRETARIA',  TRUE, 100, 1);

-- 4. Adiciona coluna 'codigo' na tabela entradas_fila (ex: C01, F09, S14)
ALTER TABLE entradas_fila
    ADD COLUMN codigo VARCHAR(10) NOT NULL DEFAULT ''
        AFTER posicao;

-- 5. Cria index para busca por código (usado no painel do atendente)
CREATE INDEX idx_entrada_codigo ON entradas_fila(codigo);
