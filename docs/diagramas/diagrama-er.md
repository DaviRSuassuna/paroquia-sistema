# Diagrama ER — Paróquia em Rede

```mermaid
erDiagram
    PASTORAL ||--o| CONFIGURACAO_ESCALONAMENTO : possui
    PASTORAL ||--o{ FORMULARIO_DISPONIBILIDADE : envia
    PASTORAL ||--o{ CONVOCACAO_REFORCO : "onda de"
    PASTORAL ||--o{ MEMBRO_PASTORAL : vincula
    PASTORAL ||--o{ FUNCAO_PASTORAL : vincula
    PASTORAL ||--o{ USUARIO_PERFIL : escopo

    MEMBRO ||--o{ MEMBRO_PASTORAL : participa
    MEMBRO ||--o{ MEMBRO_FUNCAO : apto_para
    MEMBRO ||--o{ ESCALA : escalado_em
    MEMBRO ||--o{ DISPONIBILIDADE : declara
    MEMBRO ||--o{ CONVOCACAO_REFORCO : candidato
    MEMBRO |o--o| USUARIO : vinculado_a

    FUNCAO ||--o{ MEMBRO_FUNCAO : requer_aptidao
    FUNCAO ||--o{ FUNCAO_PASTORAL : vincula
    FUNCAO ||--o{ ESCALA : exigida_em

    MISSA_RECORRENTE ||--o{ MISSA : gera
    MISSA_RECORRENTE ||--o{ DISPONIBILIDADE_MISSA_RECORRENTE : referenciada_em

    MISSA ||--o{ ESCALA : possui

    ESCALA ||--o{ CONVOCACAO_REFORCO : "vaga em aberto"

    FORMULARIO_DISPONIBILIDADE ||--o{ DISPONIBILIDADE : recebe
    DISPONIBILIDADE ||--o{ DISPONIBILIDADE_MISSA_RECORRENTE : referencia

    USUARIO ||--o{ USUARIO_PERFIL : possui

    PASTORAL {
        bigint id PK
        varchar nome
        varchar descricao
        boolean ativo
        timestamp data_cadastro
        timestamp data_atualizacao
    }

    CONFIGURACAO_ESCALONAMENTO {
        bigint id PK
        bigint pastoral_id FK
        int ordem_convocacao_reforco
        int intervalo_minutos_escalonamento
        boolean permite_convocacao_reforco
        int dia_geracao_mensal
    }

    CONFIGURACAO_SISTEMA {
        bigint id PK "singleton - linha única"
        int prazo_antecedencia_reforco_horas
        int prazo_lembrete_horas
    }

    MEMBRO {
        bigint id PK
        varchar nome_completo
        date data_nascimento
        varchar telefone
        varchar nome_responsavel
        varchar telefone_responsavel
        boolean ativo
        boolean anonimizado
        timestamp data_anonimizacao
        varchar motivo_anonimizacao
        timestamp ultima_resposta_disponibilidade
        timestamp data_cadastro
        timestamp data_atualizacao
    }

    MEMBRO_PASTORAL {
        bigint membro_id FK
        bigint pastoral_id FK
    }

    FUNCAO {
        bigint id PK
        varchar nome
        varchar descricao
        boolean ativo
        boolean somente_solene
        timestamp data_cadastro
        timestamp data_atualizacao
    }

    FUNCAO_TIPO_MISSA {
        bigint funcao_id FK
        varchar tipo_missa
    }

    FUNCAO_PASTORAL {
        bigint funcao_id FK
        bigint pastoral_id FK
    }

    MEMBRO_FUNCAO {
        bigint membro_id FK
        bigint funcao_id FK
    }

    MISSA_RECORRENTE {
        bigint id PK
        varchar dia_semana
        time horario
        varchar tipo
        boolean ativo
        timestamp data_cadastro
        timestamp data_atualizacao
    }

    MISSA {
        bigint id PK
        bigint missa_recorrente_id FK
        timestamp data_hora
        varchar tipo
        boolean solene
        boolean cancelada
        timestamp data_cadastro
        timestamp data_atualizacao
    }

    ESCALA {
        bigint id PK
        bigint missa_id FK
        bigint funcao_id FK
        bigint membro_id FK "nullable - vaga em aberto"
        timestamp data_cadastro
        timestamp data_atualizacao
    }

    FORMULARIO_DISPONIBILIDADE {
        bigint id PK
        bigint pastoral_id FK
        varchar mes_referencia
        varchar link_google_forms
        timestamp data_abertura
        timestamp data_fechamento
        int minimo_missas_mensal
        timestamp data_cadastro
        timestamp data_atualizacao
    }

    DISPONIBILIDADE {
        bigint id PK
        bigint formulario_disponibilidade_id FK
        bigint membro_id FK
        timestamp data_cadastro
        timestamp data_atualizacao
    }

    DISPONIBILIDADE_MISSA_RECORRENTE {
        bigint disponibilidade_id FK
        bigint missa_recorrente_id FK
    }

    CONVOCACAO_REFORCO {
        bigint id PK
        bigint escala_id FK
        bigint membro_id FK
        bigint pastoral_id FK
        varchar token
        varchar status
        int onda
        timestamp data_envio
        timestamp data_resposta
        timestamp data_expiracao
    }

    USUARIO {
        bigint id PK
        bigint membro_id FK "nullable"
        varchar keycloak_id
        boolean ativo
        timestamp data_cadastro
        timestamp data_atualizacao
    }

    USUARIO_PERFIL {
        bigint id PK
        bigint usuario_id FK
        bigint pastoral_id FK "nullable"
        varchar perfil
    }
```

## Notas de modelagem

- `FUNCAO_TIPO_MISSA` normaliza o atributo multivalorado `Funcao.tiposMissaPermitidos` (`Set<TipoMissa>` no diagrama de classes) numa tabela associativa relacional.
- `CONFIGURACAO_SISTEMA` não tem FK — é singleton (linha única na tabela), diferente de `CONFIGURACAO_ESCALONAMENTO`, que é 1 por `PASTORAL`.
- `MISSA_RECORRENTE` não tem FK para `PASTORAL` — é um padrão da paróquia como um todo. O vínculo com pastoral(is) acontece indiretamente via `FUNCAO` exigida em cada `ESCALA`.
- Enums de domínio (`Perfil`, `TipoMissa`, `StatusConvocacao`) são mapeados como `varchar` + `CHECK constraint`, não enum nativo do Postgres.
