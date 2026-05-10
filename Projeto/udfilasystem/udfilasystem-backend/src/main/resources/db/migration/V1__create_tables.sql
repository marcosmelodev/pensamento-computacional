-- V1: Criacao das tabelas principais do sistema udfilasystem
-- Compativel com MySQL 8.0+

CREATE TABLE IF NOT EXISTS usuarios (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome          VARCHAR(150)        NOT NULL,
    email         VARCHAR(255)        NOT NULL UNIQUE,
    senha_hash    VARCHAR(255)        NOT NULL,
    totp_secret   VARCHAR(100)        NOT NULL,
    totp_ativo    BOOLEAN             NOT NULL DEFAULT FALSE,
    ativo         BOOLEAN             NOT NULL DEFAULT TRUE,
    role          ENUM('USER','ADMIN') NOT NULL DEFAULT 'USER',
    criado_em     DATETIME(6)         NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em DATETIME(6)         NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT uq_email UNIQUE (email)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_ativo ON usuarios(ativo);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    token         VARCHAR(512)        NOT NULL UNIQUE,
    usuario_id    BIGINT              NOT NULL,
    expirado_em   DATETIME(6)         NOT NULL,
    revogado      BOOLEAN             NOT NULL DEFAULT FALSE,
    criado_em     DATETIME(6)         NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_refresh_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE INDEX idx_refresh_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_usuario ON refresh_tokens(usuario_id);

CREATE TABLE IF NOT EXISTS filas (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome          VARCHAR(100)        NOT NULL,
    descricao     TEXT,
    ativa         BOOLEAN             NOT NULL DEFAULT TRUE,
    max_capacidade INT                NOT NULL DEFAULT 100,
    criado_por    BIGINT              NOT NULL,
    criado_em     DATETIME(6)         NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em DATETIME(6)         NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_fila_criador FOREIGN KEY (criado_por) REFERENCES usuarios(id)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS entradas_fila (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    fila_id       BIGINT              NOT NULL,
    usuario_id    BIGINT              NOT NULL,
    posicao       INT                 NOT NULL,
    status        ENUM('AGUARDANDO','CHAMADO','ATENDIDO','CANCELADO') NOT NULL DEFAULT 'AGUARDANDO',
    entrou_em     DATETIME(6)         NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atendido_em   DATETIME(6),
    CONSTRAINT fk_entrada_fila FOREIGN KEY (fila_id) REFERENCES filas(id) ON DELETE CASCADE,
    CONSTRAINT fk_entrada_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE INDEX idx_entrada_fila ON entradas_fila(fila_id, status);
CREATE INDEX idx_entrada_usuario ON entradas_fila(usuario_id);

CREATE TABLE IF NOT EXISTS login_attempts (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    email         VARCHAR(255)        NOT NULL,
    ip_address    VARCHAR(50),
    sucesso       BOOLEAN             NOT NULL DEFAULT FALSE,
    tentado_em    DATETIME(6)         NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE INDEX idx_login_email_tempo ON login_attempts(email, tentado_em);
