# Diagrama ER — Paróquia em Rede

Diagrama entidade-relacionamento derivado do modelo de classes (`classes.puml`).

\`\`\`mermaid
erDiagram
    MEMBRO {
        bigint id PK
        datetime dataCadastro
        datetime dataAtualizacao
        string nomeCompleto
        date dataNascimento
        string telefone
        string nomeResponsavel
        string telefoneResponsavel
        boolean ativo
        boolean anonimizado
        datetime dataAnonimizacao
    }

    PASTORAL {
        bigint id PK
        datetime dataCadastro
        datetime dataAtualizacao
        string nome
        string descricao
        boolean ativo
    }

    CONFIGURACAO_ESCALONAMENTO {
        bigint id PK
        datetime dataCadastro
        datetime dataAtualizacao
        int ordemConvocacaoReforco
        int intervaloMinutosEscalonamento
    }

    FUNCAO {
        bigint id PK
        datetime dataCadastro
        datetime dataAtualizacao
        string nome
        string descricao
        boolean ativo
        boolean somenteSolene
        string tiposMissaPermitidos "set de TipoMissa"
    }

    MISSA_RECORRENTE {
        bigint id PK
        datetime dataCadastro
        datetime dataAtualizacao
        string diaSemana
        time horario
        string tipo "TipoMissa"
        boolean ativo
    }

    MISSA {
        bigint id PK
        datetime dataCadastro
        datetime dataAtualizacao
        datetime dataHora
        string tipo "TipoMissa"
        boolean solene
        boolean cancelada
    }

    ESCALA {
        bigint id PK
        datetime dataCadastro
        datetime dataAtualizacao
    }

    FORMULARIO_DISPONIBILIDADE {
        bigint id PK
        datetime dataCadastro
        datetime dataAtualizacao
        string mesReferencia "YearMonth"
        string linkGoogleForms
        datetime dataAbertura
        datetime dataFechamento
        int minimoMissasMensal
    }

    DISPONIBILIDADE {
        bigint id PK
        datetime dataCadastro
        datetime dataAtualizacao
        string observacoes
    }

    CONVOCACAO_REFORCO {
        bigint id PK
        datetime dataCadastro
        datetime dataAtualizacao
        string token
        string status "StatusConvocacao"
        int onda
        datetime dataEnvio
        datetime dataResposta
        datetime dataExpiracao
    }

    USUARIO {
        bigint id PK
        datetime dataCadastro
        datetime dataAtualizacao
        string keycloakId
        boolean ativo
    }

    USUARIO_PERFIL {
        bigint id PK
        datetime dataCadastro
        datetime dataAtualizacao
        string perfil "Perfil"
    }

    MEMBRO }o--o{ PASTORAL : "participa de"
    MEMBRO }o--o{ FUNCAO : "apto para"
    FUNCAO }o--o{ PASTORAL : "disponível em"

    PASTORAL ||--o| CONFIGURACAO_ESCALONAMENTO : "possui"
    PASTORAL ||--o{ CONVOCACAO_REFORCO : "onda de"

    MISSA_RECORRENTE ||--o{ MISSA : "gera"

    MISSA ||--o{ ESCALA : "possui"
    FUNCAO ||--o{ ESCALA : "define"
    MEMBRO |o--o{ ESCALA : "escalado em"

    PASTORAL ||--o{ FORMULARIO_DISPONIBILIDADE : "abre"
    FORMULARIO_DISPONIBILIDADE ||--o{ DISPONIBILIDADE : "recebe"
    MEMBRO ||--o{ DISPONIBILIDADE : "declara"
    DISPONIBILIDADE }o--o{ MISSA_RECORRENTE : "disponível para"

    ESCALA ||--o{ CONVOCACAO_REFORCO : "vaga em aberto"
    MEMBRO ||--o{ CONVOCACAO_REFORCO : "candidato"

    MEMBRO |o--o| USUARIO : "vinculado a"
    USUARIO ||--o{ USUARIO_PERFIL : "possui"
    USUARIO_PERFIL }o--o| PASTORAL : "escopo (se Admin Pastoral)"
\`\`\`