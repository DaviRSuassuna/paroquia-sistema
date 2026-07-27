-- V1: entidades iniciais do dominio (Paroquia em Rede)
-- Reescrito para refletir o modelo de classes confirmado na Frente 1.

CREATE TABLE membro (
    id                               BIGSERIAL PRIMARY KEY,
    nome_completo                    VARCHAR(255) NOT NULL,
    data_nascimento                  DATE NOT NULL,
    telefone                         VARCHAR(20) NOT NULL,
    nome_responsavel                 VARCHAR(255),
    telefone_responsavel             VARCHAR(20),
    ativo                            BOOLEAN NOT NULL DEFAULT TRUE,
    anonimizado                      BOOLEAN NOT NULL DEFAULT FALSE,
    data_anonimizacao                TIMESTAMP,
    motivo_anonimizacao              TEXT,
    ultima_resposta_disponibilidade  TIMESTAMP,
    data_cadastro                    TIMESTAMP NOT NULL DEFAULT now(),
    data_atualizacao                 TIMESTAMP
);

CREATE TABLE pastoral (
    id                              BIGSERIAL PRIMARY KEY,
    nome                            VARCHAR(255) NOT NULL UNIQUE,
    descricao                       TEXT,
    ativo                           BOOLEAN NOT NULL DEFAULT TRUE,
    elegivel_limite_participacao    BOOLEAN NOT NULL DEFAULT FALSE,
    data_cadastro                   TIMESTAMP NOT NULL DEFAULT now(),
    data_atualizacao                TIMESTAMP
);

CREATE TABLE usuario (
    id                 BIGSERIAL PRIMARY KEY,
    keycloak_id        VARCHAR(255) NOT NULL UNIQUE,
    ativo              BOOLEAN NOT NULL DEFAULT TRUE,
    membro_id          BIGINT UNIQUE REFERENCES membro(id),
    data_cadastro      TIMESTAMP NOT NULL DEFAULT now(),
    data_atualizacao   TIMESTAMP
);

CREATE TABLE configuracao_escalonamento (
    id                                   BIGSERIAL PRIMARY KEY,
    pastoral_id                         BIGINT NOT NULL UNIQUE REFERENCES pastoral(id),
    ordem_convocacao_reforco            INTEGER,
    intervalo_minutos_escalonamento     INTEGER,
    permite_convocacao_reforco          BOOLEAN NOT NULL DEFAULT FALSE,
    dia_geracao_mensal                  INTEGER,
    prioridade_ativa                    BOOLEAN NOT NULL DEFAULT FALSE,
    limite_missa_semana_comum           INTEGER CHECK (limite_missa_semana_comum >= 0),
    limite_missa_semana_solene          INTEGER CHECK (limite_missa_semana_solene >= 0),
    limite_missa_dominical_comum        INTEGER CHECK (limite_missa_dominical_comum >= 0),
    limite_missa_dominical_solene       INTEGER CHECK (limite_missa_dominical_solene >= 0),
    data_cadastro                       TIMESTAMP NOT NULL DEFAULT now(),
    data_atualizacao                    TIMESTAMP
);

CREATE TABLE configuracao_sistema (
    id                                   BIGSERIAL PRIMARY KEY,
    prazo_antecedencia_reforco_horas     INTEGER NOT NULL,
    prazo_lembrete_horas                 INTEGER NOT NULL,
    data_cadastro                        TIMESTAMP NOT NULL DEFAULT now(),
    data_atualizacao                     TIMESTAMP
);

CREATE TABLE funcao (
    id               BIGSERIAL PRIMARY KEY,
    nome             VARCHAR(255) NOT NULL UNIQUE,
    descricao        TEXT,
    ativo            BOOLEAN NOT NULL DEFAULT TRUE,
    somente_solene   BOOLEAN NOT NULL DEFAULT FALSE,
    complexidade     VARCHAR(20) NOT NULL CHECK (complexidade IN ('SIMPLES', 'COMPLEXA')),
    tipo_minimo      VARCHAR(20) NOT NULL CHECK (tipo_minimo IN ('COMUM', 'DOMINICAL', 'ESPECIAL')),
    pastoral_id      BIGINT NOT NULL REFERENCES pastoral(id),
    data_cadastro    TIMESTAMP NOT NULL DEFAULT now(),
    data_atualizacao TIMESTAMP
);

CREATE TABLE missa_recorrente (
    id               BIGSERIAL PRIMARY KEY,
    dia_semana       VARCHAR(20) NOT NULL CHECK (dia_semana IN
                        ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY')),
    horario          TIME NOT NULL,
    tipo             VARCHAR(20) NOT NULL CHECK (tipo IN ('COMUM', 'DOMINICAL', 'ESPECIAL')),
    ativo            BOOLEAN NOT NULL DEFAULT TRUE,
    data_cadastro    TIMESTAMP NOT NULL DEFAULT now(),
    data_atualizacao TIMESTAMP
);

CREATE TABLE missa (
    id                    BIGSERIAL PRIMARY KEY,
    data_hora             TIMESTAMP NOT NULL,
    tipo                  VARCHAR(20) NOT NULL CHECK (tipo IN ('COMUM', 'DOMINICAL', 'ESPECIAL')),
    solene                BOOLEAN NOT NULL DEFAULT FALSE,
    cancelada             BOOLEAN NOT NULL DEFAULT FALSE,
    missa_recorrente_id   BIGINT REFERENCES missa_recorrente(id),
    data_cadastro         TIMESTAMP NOT NULL DEFAULT now(),
    data_atualizacao      TIMESTAMP
);

CREATE TABLE escala (
    id               BIGSERIAL PRIMARY KEY,
    missa_id         BIGINT NOT NULL REFERENCES missa(id),
    membro_id        BIGINT REFERENCES membro(id),
    funcao_id        BIGINT NOT NULL REFERENCES funcao(id),
    data_cadastro    TIMESTAMP NOT NULL DEFAULT now(),
    data_atualizacao TIMESTAMP,
    CONSTRAINT uk_escala_missa_membro_funcao UNIQUE (missa_id, membro_id, funcao_id)
);

CREATE TABLE formulario_disponibilidade (
    id                     BIGSERIAL PRIMARY KEY,
    mes_referencia         VARCHAR(7) NOT NULL,
    link_google_forms      TEXT,
    data_abertura          TIMESTAMP,
    data_fechamento        TIMESTAMP,
    minimo_missas_mensal   INTEGER,
    pastoral_id            BIGINT NOT NULL REFERENCES pastoral(id),
    data_cadastro          TIMESTAMP NOT NULL DEFAULT now(),
    data_atualizacao       TIMESTAMP
);

CREATE TABLE disponibilidade (
    id                                BIGSERIAL PRIMARY KEY,
    formulario_disponibilidade_id     BIGINT NOT NULL REFERENCES formulario_disponibilidade(id),
    membro_id                         BIGINT NOT NULL REFERENCES membro(id),
    data_cadastro                     TIMESTAMP NOT NULL DEFAULT now(),
    data_atualizacao                  TIMESTAMP
);

CREATE TABLE convocacao_reforco (
    id               BIGSERIAL PRIMARY KEY,
    token            VARCHAR(255) NOT NULL UNIQUE,
    status           VARCHAR(20) NOT NULL CHECK (status IN ('PENDENTE', 'CONFIRMADO', 'EXPIRADO', 'INVALIDADO')),
    onda             INTEGER NOT NULL,
    data_envio       TIMESTAMP,
    data_resposta    TIMESTAMP,
    data_expiracao   TIMESTAMP,
    pastoral_id      BIGINT NOT NULL REFERENCES pastoral(id),
    escala_id        BIGINT NOT NULL REFERENCES escala(id),
    membro_id        BIGINT NOT NULL REFERENCES membro(id),
    data_cadastro    TIMESTAMP NOT NULL DEFAULT now(),
    data_atualizacao TIMESTAMP
);

CREATE TABLE usuario_perfil (
    id               BIGSERIAL PRIMARY KEY,
    perfil           VARCHAR(30) NOT NULL CHECK (perfil IN ('SUPER_ADMIN', 'ADMIN_PASTORAL', 'PADRE')),
    usuario_id       BIGINT NOT NULL REFERENCES usuario(id),
    pastoral_id      BIGINT REFERENCES pastoral(id),
    data_cadastro    TIMESTAMP NOT NULL DEFAULT now(),
    data_atualizacao TIMESTAMP
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

CREATE TABLE disponibilidade_missa_recorrente (
    disponibilidade_id     BIGINT NOT NULL REFERENCES disponibilidade(id) ON DELETE CASCADE,
    missa_recorrente_id    BIGINT NOT NULL REFERENCES missa_recorrente(id) ON DELETE CASCADE,
    PRIMARY KEY (disponibilidade_id, missa_recorrente_id)
);

-- Indices de apoio

CREATE INDEX idx_escala_missa ON escala(missa_id);
CREATE INDEX idx_escala_membro ON escala(membro_id);
CREATE INDEX idx_missa_data_hora ON missa(data_hora);
CREATE INDEX idx_funcao_pastoral ON funcao(pastoral_id);
CREATE INDEX idx_convocacao_reforco_status ON convocacao_reforco(status);
CREATE INDEX idx_convocacao_reforco_membro ON convocacao_reforco(membro_id);
CREATE INDEX idx_disponibilidade_membro ON disponibilidade(membro_id);
CREATE INDEX idx_disponibilidade_formulario ON disponibilidade(formulario_disponibilidade_id);
CREATE INDEX idx_usuario_perfil_usuario ON usuario_perfil(usuario_id);