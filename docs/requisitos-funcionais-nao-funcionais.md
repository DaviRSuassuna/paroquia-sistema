# Requisitos Funcionais e Não Funcionais — Paróquia em Rede

## Nota de escopo

Este documento substitui integralmente a versão anterior, que ainda refletia o modelo de agrupamento (`Grupo`, `MembroGrupo`, `GrupoEscalado`), o sistema de presença/falta e o papel de Coordenador. Principais mudanças desde a última versão:

- Escalonamento passou de **grupos rotativos** para **disponibilidade individual** (sorteio dentro de quem está apto e disponível).
- **Presença e sistema de pontuação/punição foram removidos** do escopo.
- Papel **Coordenador foi removido** — só existia para presença/falta.
- Integração com **Gemini foi removida** — só existia para julgar `causaPlausivel` de indisponibilidade, o que também saiu de escopo.
- Reforço agora é em **ondas entre Pastorais** (ex: Acólitos → Coroinhas), não mais dentro da mesma pastoral por faixa etária.

---

## Requisitos Funcionais

### Módulo: Autenticação e Perfis

**RF01 — Login com múltiplo perfil.** Um `Usuario` pode ter mais de um `UsuarioPerfil` (ex: Admin Pastoral em mais de uma `Pastoral`). Se tiver apenas um perfil, o sistema entra direto, sem tela extra. Se tiver mais de um, o usuário escolhe qual perfil ativar na sessão, podendo trocar sem precisar deslogar.

**RF02 — Revalidação de perfil ativo por requisição.** Todo endpoint sensível confere se o `UsuarioPerfil` informado como ativo pertence de fato ao usuário autenticado, antes de liberar a ação. O backend não confia no perfil informado pelo front-end.

**RF03 — Três papéis.** `Perfil` = `SUPER_ADMIN` (acesso completo, todas as pastorais), `ADMIN_PASTORAL` (acesso completo à própria `Pastoral`, escopo via `UsuarioPerfil.pastoral`), `PADRE` (leitura + flag de solenidade, todas as pastorais).

### Módulo: Pastorais, Funções e Aptidão

**RF04 — Cadastro de Pastoral.** `Pastoral` tem nome, descrição e status ativo/inativo. Pastorais são independentes entre si — não há hierarquia ou herança entre elas.

**RF05 — Função pertence a uma ou mais Pastorais.** `Funcao` (ex: Cruciferário, Turiferário, Acólito do Missal) está vinculada a uma ou mais `Pastoral`s via relação N:N. Uma `Funcao` só pode ser exercida em `Missa`s cujo `TipoMissa` esteja em `tiposMissaPermitidos`; se `somenteSolene = true`, só em missas com `solene = true`.

**RF06 — Aptidão do Membro por Função.** A relação `Membro`–`Funcao` (N:N) representa quais funções aquele membro sabe exercer (ex: sabe carregar cruz, sabe usar o missal). Um membro só pode ter aptidão registrada para funções de pastorais das quais ele participa (`Membro`–`Pastoral`) — restrição natural da modelagem, sem necessidade de validação redundante. Essa aptidão é cadastro único (não muda semana a semana), diferente da escalação em si, que é por missa.

### Módulo: Disponibilidade

**RF07 — Formulário mensal por Pastoral.** `FormularioDisponibilidade` é aberto por `Pastoral`, com mês de referência, link do Google Forms, datas de abertura/fechamento e `minimoMissasMensal` (mínimo de missas que o membro deve se disponibilizar a fazer naquele mês).

**RF08 — Disponibilidade vinculada a padrão recorrente, não a missa concreta.** `Disponibilidade` se relaciona com `MissaRecorrente` (N:N), não com `Missa`. Isso permite abrir o formulário antes de as missas do mês serem geradas. Cada resposta pode conter `observacoes` em texto livre — sem processamento ou julgamento automático desse texto pelo sistema.

**RF09 — Geração de Missa a partir de MissaRecorrente.** O sistema gera instâncias de `Missa` (data e hora concretas) a partir dos padrões semanais cadastrados em `MissaRecorrente`, para um período (tipicamente um mês).

### Módulo: Escala

**RF10 — Geração de escala por sorteio.** Para cada `Missa` gerada, o sistema monta `Escala` sorteando aleatoriamente, para cada `Funcao` necessária, entre os `Membro`s que (a) têm aptidão para aquela `Funcao`, e (b) declararam disponibilidade para a `MissaRecorrente` correspondente naquele mês — respeitando o `minimoMissasMensal` de cada um.

**RF11 — Vaga em aberto.** Se não houver candidato apto e disponível suficiente para uma `Funcao` numa `Missa`, a `Escala` é criada com `Membro` nulo (vaga em aberto), disparando o fluxo de reforço (RF12+).

**RF12 — Cancelamento de Missa preserva histórico.** `Missa.cancelada = true` marca uma missa como cancelada sem apagar o registro nem as `Escala`s associadas.

### Módulo: Reforço (Convocação)

**RF13 — Configuração de escalonamento por Pastoral.** Uma `Pastoral` participa do fluxo de reforço somente se tiver uma `ConfiguracaoEscalonamento` associada (relação opcional 1:0..1). Essa configuração define `ordemConvocacaoReforco` (posição da pastoral na fila de escalonamento) e `intervaloMinutosEscalonamento` (tempo de espera antes de escalar para a próxima pastoral da fila). Pastorais sem essa configuração nunca recebem convocação de reforço.

**RF14 — Disparo da primeira onda.** Quando uma `Escala` fica com vaga em aberto, o sistema dispara `ConvocacaoReforco` (`onda = 1`) para todos os `Membro`s aptos à `Funcao` em falta, pertencentes à `Pastoral` de menor `ordemConvocacaoReforco` entre as pastorais elegíveis para aquela função.

**RF15 — Convocação individual via WhatsApp, sem login.** Cada `ConvocacaoReforco` gera um `token` único, enviado por link individual via WhatsApp (Evolution API, somente envio). O membro confirma com um único clique, sem necessidade de autenticação.

**RF16 — Confirmação atômica (primeiro-clique-vence).** O primeiro `Membro` a confirmar preenche a vaga (`Escala.membro` é setado, `ConvocacaoReforco.status = CONFIRMADO`). Os demais tokens da mesma onda são invalidados automaticamente (`status = INVALIDADO`).

**RF17 — Escalonamento para a próxima onda.** Se a vaga não for preenchida dentro do `intervaloMinutosEscalonamento` da `Pastoral` da onda atual, o sistema dispara nova `ConvocacaoReforco` (`onda = 2`, `3`...) para a próxima `Pastoral` elegível na fila (`ordemConvocacaoReforco` seguinte).

**RF18 — Expiração de token.** Um `ConvocacaoReforco` não respondido dentro do prazo de expiração (`dataExpiracao`) tem seu `status` alterado automaticamente para `EXPIRADO`.

### Módulo: LGPD

**RF19 — Anonimização manual por inatividade.** Um `Membro` sem nenhuma `Disponibilidade` registrada nos últimos 12 meses (contados a partir da resposta mais recente, ou do `dataCadastro` do próprio `Membro` se ele nunca respondeu) pode ser marcado como anonimizado (`Membro.anonimizado = true`, `dataAnonimizacao` preenchida) pelo Admin Pastoral. A ação é manual, nunca automática.

**RF20 — Exclusão sob pedido.** Pedido de exclusão explícito pelo titular dos dados é atendido de forma imediata, fora do ciclo trimestral, executado exclusivamente pelo Super Admin.

---

## Requisitos Não Funcionais

**RNF01 — LGPD: dado sensível.** O sistema trata dados de participação religiosa (pastoral, função, disponibilidade) como dado pessoal sensível, nos termos do Art. 5º, II da LGPD, exigindo tratamento de segurança e consentimento mais rigoroso que um cadastro comum.

**RNF02 — LGPD: purga trimestral.** No início de cada trimestre civil (janeiro, abril, julho, outubro), executar automaticamente: (a) exclusão de todo histórico de `Missa`/`Escala` com mais de 1 ano; (b) exclusão física de todo `Membro` que estiver `anonimizado` nesse momento.

**RNF03 — Segurança: least-privilege.** Três papéis com escopo definido — `SUPER_ADMIN` (tudo), `ADMIN_PASTORAL` (tudo dentro da própria pastoral: membros, aptidões, escala, configuração de escalonamento), `PADRE` (leitura + flag de solenidade, todas as pastorais). Nenhum papel acessa dado fora do seu escopo por padrão.

**RNF04 — Disponibilidade (infra).** O sistema roda em VM ARM do Oracle Cloud Free Tier (2 OCPUs, 12 GB RAM) e deve operar de forma estável dentro desses limites de recursos.

**RNF05 — Limite de envio WhatsApp.** O volume de mensagens enviadas via Evolution API deve respeitar o limite prático de aproximadamente 480 mensagens/mês do chip dedicado, evitando bloqueio do número. Evolution API é usada apenas para envio — não há leitura de resposta via webhook (confirmação de reforço acontece via link/token, não via resposta de mensagem).

**RNF06 — Performance.** Consultas de listagem (escala, membros, disponibilidade) devem responder em tempo hábil para uso em tela síncrona, sem necessidade de cache para o volume de uma única paróquia.

**RNF07 — Configurabilidade.** Regras de negócio variáveis são expressas como dado, não como lógica condicional fixa no código — exemplos já modelados: `minimoMissasMensal` (por formulário), `ordemConvocacaoReforco` e `intervaloMinutosEscalonamento` (por pastoral), `tiposMissaPermitidos` e `somenteSolene` (por função).

---

## Pendências abertas (não bloqueiam o RF, mas precisam de decisão antes da implementação correspondente)

- Prazo/gatilho exato para considerar uma vaga "em aberto" e disparar a primeira onda de reforço (quanto tempo antes da missa) — a definir.
- Prazo exato do lembrete de WhatsApp antes da missa — a definir.
- Quando um `Membro` pertence a mais de uma `Pastoral`, qual Admin Pastoral aciona a anonimização por inatividade (RF19), já que a inatividade é avaliada em todas as pastorais do membro — a definir.
- Se a exclusão física manual pelo Super Admin (RF20) precisa de fluxo de confirmação prévio antes de executar, por ser operação em cascata — a definir.
