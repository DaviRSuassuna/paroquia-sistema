-- V1: entidades iniciais do dominio

CREATE TABLE membro (
    id                    BIGSERIAL PRIMARY KEY,
    nome_completo         VARCHAR(255) NOT NULL,
    data_nascimento       DATE NOT NULL,
    telefone              VARCHAR(20) NOT NULL,
    nome_responsavel      VARCHAR(255),
    telefone_responsavel  VARCHAR(20),
    ativo                 BOOLEAN NOT NULL DEFAULT TRUE,
    data_cadastro         TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE pastoral (
    id             BIGSERIAL PRIMARY KEY,
    nome           VARCHAR(255) NOT NULL UNIQUE,
    descricao      TEXT,
    ativo          BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao   TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE funcao (
    id               BIGSERIAL PRIMARY KEY,
    nome             VARCHAR(255) NOT NULL UNIQUE,
    descricao        TEXT,
    ativo            BOOLEAN NOT NULL DEFAULT TRUE,
    somente_solene   BOOLEAN NOT NULL DEFAULT FALSE,
    data_criacao     TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE funcao_tipo_missa_permitido (
    funcao_id   BIGINT NOT NULL REFERENCES funcao(id) ON DELETE CASCADE,
    tipo_missa  VARCHAR(20) NOT NULL,
    PRIMARY KEY (funcao_id, tipo_missa)
);

CREATE TABLE missa (
    id             BIGSERIAL PRIMARY KEY,
    data_hora      TIMESTAMP NOT NULL,
    tipo           VARCHAR(20) NOT NULL,
    solene         BOOLEAN NOT NULL DEFAULT FALSE,
    data_criacao   TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE escala (
    id             BIGSERIAL PRIMARY KEY,
    missa_id       BIGINT NOT NULL REFERENCES missa(id),
    membro_id      BIGINT NOT NULL REFERENCES membro(id),
    funcao_id      BIGINT NOT NULL REFERENCES funcao(id),
    presenca       BOOLEAN,
    dupla_funcao   BOOLEAN NOT NULL DEFAULT FALSE,
    data_criacao   TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT uk_escala_missa_membro_funcao UNIQUE (missa_id, membro_id, funcao_id)
);

-- Tabelas de juncao N:N

CREATE TABLE membro_pastoral (
    membro_id    BIGINT NOT NULL REFERENCES membro(id) ON DELETE CASCADE,
    pastoral_id  BIGINT NOT NULL REFERENCES pastoral(id) ON DELETE CASCADE,
    PRIMARY KEY (membro_id, pastoral_id)
);

CREATE TABLE membro_funcao (
    membro_id  BIGINT NOT NULL REFERENCES membro(id) ON DELETE CASCADE,
    funcao_id  BIGINT NOT NULL REFERENCES funcao(id) ON DELETE CASCADE,
    PRIMARY KEY (membro_id, funcao_id)
);

CREATE TABLE funcao_pastoral (
    funcao_id    BIGINT NOT NULL REFERENCES funcao(id) ON DELETE CASCADE,
    pastoral_id  BIGINT NOT NULL REFERENCES pastoral(id) ON DELETE CASCADE,
    PRIMARY KEY (funcao_id, pastoral_id)
);

-- Indices de apoio para consultas de escala/presenca
CREATE INDEX idx_escala_missa ON escala(missa_id);
CREATE INDEX idx_escala_membro ON escala(membro_id);
CREATE INDEX idx_missa_data_hora ON missa(data_hora);