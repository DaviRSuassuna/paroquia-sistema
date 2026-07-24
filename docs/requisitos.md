# Requisitos Funcionais e Não Funcionais — Paróquia em Rede

## Nota de escopo

Este documento substitui integralmente a versão anterior. Principais mudanças desde a última revisão:

- Escalonamento por **disponibilidade individual**, sem grupos rotativos.
- **Presença, pontuação/punição e papel Coordenador** fora de escopo.
- **Gemini fora de escopo** — formulário 100% estruturado.
- Reforço em **ondas entre Pastorais**, por `ordemConvocacaoReforco`.
- **Cadastro estrutural centralizado no Super Admin**: `Membro`, `Funcao`, `MissaRecorrente` e vínculo `Membro`-`Pastoral`.
- **Geração de Missa/Escala e disparo de reforço são 100% automáticos**.
- Convocação de reforço pode ser **desativada por pastoral**.
- **Anonimização é sempre acionada pelo Super Admin**.
- **Prazos globais** configuráveis pelo Super Admin via `ConfiguracaoSistema`.
- **Missa especial (ESPECIAL)** criada manualmente por Super Admin e Padre.
- **Consistência `Missa.tipo` × `MissaRecorrente.tipo`** garantida por trigger.
- **`Funcao` pertence a exatamente uma `Pastoral`** (1:N, `NOT NULL`). Aptidão cruzada sempre manual.
- **`Funcao.complexidade`** (SIMPLES/COMPLEXA).
- **`Funcao.tipoMinimo`** substitui a tabela `FUNCAO_TIPO_MISSA` — elegibilidade por hierarquia estrita `COMUM < DOMINICAL < ESPECIAL`, não mais por conjunto arbitrário de tipos.
- **Prioridade Acólitos/Coroinhas e limites de participação**, restritos a `Pastoral`s com `elegivelLimiteParticipacao = true`, e só ativos quando `ConfiguracaoEscalonamento.prioridadeAtiva = true` (RF10.2).
- **Marcar Missa como solene cria vagas automáticas** para Funcoes exclusivas de solenidade.
- **Missa cancelada suprime toda automação futura.**
- **Exclusão sob pedido (RF26)** exige segunda confirmação explícita (nome completo do Membro).
- **RF15.1/RF15.2 aplicam-se apenas à geração automática**, não ao preenchimento manual de vaga (Missa especial).

---

## Requisitos Funcionais

### Módulo: Autenticação e Perfis

**RF01 — Login com múltiplo perfil.** Um `Usuario` pode ter mais de um `UsuarioPerfil`. Com um único perfil, entra direto; com mais de um, escolhe qual ativar, podendo trocar sem deslogar.

**RF02 — Revalidação de perfil ativo por requisição.** Todo endpoint sensível confere se o `UsuarioPerfil` ativo pertence de fato ao usuário autenticado.

**RF03 — Três papéis.** `SUPER_ADMIN`, `ADMIN_PASTORAL` (escopo próprio), `PADRE` (leitura geral + Missa especial + flags).

### Módulo: Cadastros Estruturais (exclusivo Super Admin)

**RF04 — Cadastro de Pastoral.** `Pastoral` tem nome (único), descrição, status ativo/inativo e `elegivelLimiteParticipacao`.

**RF05 — Cadastro de Membro.** Exclusivo do Super Admin. Menores de 18 anos exigem `telefoneResponsavel`.

**RF06 — Vínculo Membro-Pastoral.** Gerenciado exclusivamente pelo Super Admin.

**RF07 — Cadastro de Função.** Criação e edição de `Funcao` — nome, descrição, `tipoMinimo`, `somenteSolene`, `complexidade` — exclusiva do Super Admin. `tipoMinimo` (`COMUM`/`DOMINICAL`/`ESPECIAL`) define elegibilidade por hierarquia estrita: `COMUM < DOMINICAL < ESPECIAL` — uma Função serve em qualquer tipo de missa igual ou acima do seu próprio degrau. Cada `Funcao` pertence, obrigatoriamente, a exatamente uma `Pastoral` (`Funcao.pastoralId`, `NOT NULL`). A posse define quem administra; não impede aptidão cruzada (RF09).

**RF08 — Cadastro de Missa Recorrente.** Exclusivo do Super Admin. `tipo` restrito a `COMUM`/`DOMINICAL`.

### Módulo: Aptidão e Configuração Operacional

**RF09 — Aptidão do Membro por Função.** `Membro`-`Funcao` (N:N), individual (1 Membro + 1 Função por vez, sem atribuição em massa). Gerenciada pelo Admin Pastoral da pastoral do Membro, mesmo se a Função for de outra Pastoral — aptidão cruzada sempre manual, sem regra automática.

**RF10 — Configuração de escalonamento por Pastoral.** `ConfiguracaoEscalonamento` (1:0..1 com `Pastoral`), gerenciada pelo Admin Pastoral: `ordemConvocacaoReforco`, `intervaloMinutosEscalonamento`, `permiteConvocacaoReforco`, `diaGeracaoMensal`. Pastorais sem essa configuração nunca recebem reforço.

**RF10.1 — Limites de participação por tipo de missa (exclusivo Super Admin).** Campos `limiteMissaSemanaComum`, `limiteMissaSemanaSolene`, `limiteMissaDominicalComum`, `limiteMissaDominicalSolene`. Disponíveis só para `Pastoral`s com `elegivelLimiteParticipacao = true`. `NULL` = sem teto; se preenchido, `>= 0`.

**RF10.2 — Ativação da prioridade Acólito/Coroinha.** A aplicação de RF15.2 para uma `Pastoral` com `elegivelLimiteParticipacao = true` depende do campo `ConfiguracaoEscalonamento.prioridadeAtiva` (boolean, default `false`). O Super Admin só pode marcar `prioridadeAtiva = true` quando `ConfiguracaoEscalonamento` já existir para aquela `Pastoral`, com `ordemConvocacaoReforco`, `intervaloMinutosEscalonamento` e `diaGeracaoMensal` preenchidos. Enquanto `prioridadeAtiva = false` (incluindo quando `ConfiguracaoEscalonamento` nem existe), o sorteio trata essa `Pastoral` como qualquer outra — sem prioridade nem limites — caindo no comportamento padrão de RF15.

### Módulo: Disponibilidade

**RF11 — Formulário mensal por Pastoral.** `FormularioDisponibilidade`, `UNIQUE (pastoral_id, mes_referencia)`.

**RF12 — Disponibilidade vinculada a padrão recorrente.** `Disponibilidade`-`MissaRecorrente` (N:N). Estruturado, sem texto livre. `UNIQUE (formulario, membro)`.

**RF13 — Registro de última resposta.** `Membro.ultimaRespostaDisponibilidade` alimenta RF24/RF25.

### Módulo: Missa e Escala

**RF14 — Geração automática de Missa.** No dia configurado, gera `Missa` a partir de `MissaRecorrente`.

**RF15 — Geração automática de Escala por sorteio.** Sorteia, para cada `Funcao` necessária (RF15.3), entre `Membro`s aptos (RF09) e disponíveis (RF12), respeitando `minimoMissasMensal`.

**RF15.1 — Consolidação de funções simples por membro.** Aplica-se apenas à geração automática de Escala (RF14/RF15) — não ao preenchimento manual de vaga em Missa especial (RF17.4/RF17.5). O sistema prioriza reaproveitar `Membro`s já escalados na mesma `Missa` para `Funcao`s `SIMPLES`, antes de recrutar novo `Membro`. `COMPLEXA` é sempre exclusiva. Sem teto fixo — número ideal calculado dinamicamente, evitando que alguém fique com só 1 `SIMPLES` quando dá pra consolidar em 2. Algoritmo exato: Frente 5.

**RF15.2 — Prioridade Acólito/Coroinha por tipo de missa.** Aplica-se apenas à geração automática de Escala (RF14/RF15), entre `Pastoral`s com `elegivelLimiteParticipacao = true`, e somente quando `ConfiguracaoEscalonamento.prioridadeAtiva = true` (RF10.2). Não se aplica ao preenchimento manual (RF17.4/RF17.5):
1. Acólitos primeiro, em qualquer Função com aptidão cadastrada, até o limite (RF10.1). Solene: sem teto.
2. Coroinhas preenchem o restante, até o próprio limite. Solene: só se sobrar vaga.
3. Limites são metas flexíveis — podem ser ultrapassados antes de cair em vaga aberta/reforço.
4. Consolidação (RF15.1) e exclusividade `COMPLEXA` continuam valendo.

Valores de referência: Acólitos — semana comum: piso 1; semana solene: sem teto; domingo comum: 2; domingo solene: sem teto. Coroinhas — semana: teto 2; domingo: teto 3.

**RF15.3 — Definição de Funções necessárias por Missa recorrente.** Para `Missa`s geradas automaticamente, toda `Funcao` ativa cujo `tipoMinimo` seja igual ou inferior ao `tipo` daquela `Missa` (hierarquia `COMUM < DOMINICAL < ESPECIAL`) gera uma linha em `Escala` — elegibilidade hierárquica é também obrigatoriedade. Funções `somenteSolene = true` são excluídas dessa geração inicial — só entram via RF17.8.

**RF16 — Vaga em aberto.** Sem candidato, `Escala` criada com `Membro` nulo, disparando reforço (RF18+) se ativo; senão, fica em aberto.

**RF17 — Edição manual.** Admin Pastoral edita vagas da própria Pastoral. Super Admin sem restrição.

**RF17.1 — Cancelamento de Missa.** Super Admin e Padre marcam `cancelada = true`. Preserva histórico.

**RF17.2 — Flag de solenidade.** Super Admin e Padre marcam `solene = true`.

**RF17.3 — Criação manual de Missa especial.** Super Admin e Padre, `missa_recorrente_id` obrigatoriamente nulo.

**RF17.4 — Definição manual de vagas de Escala.** Ao criar Missa especial, Super Admin e Padre definem manualmente as `Funcao`s necessárias. Como `ESPECIAL` é o topo da hierarquia de `tipoMinimo` (RF07), toda `Funcao` do sistema é elegível — não há restrição de seleção.

**RF17.5 — Preenchimento de vaga por pastoral.** Admin Pastoral preenche vagas da própria Pastoral. Super Admin sem restrição.

**RF17.6 — Consistência automática de tipo.** `Missa.tipo` sincronizado via trigger.

**RF17.7 — Preenchimento atômico de vaga.** UPDATE condicional (`WHERE membro_id IS NULL`).

**RF17.8 — Criação de vagas ao marcar solenidade.** Cria vaga pra `Funcao`s `somenteSolene`. Não se aplica se `cancelada = true` (RF17.9).

**RF17.9 — Missa cancelada não dispara automações.** Suprime RF17.8, RF18, RF23.1. Histórico preservado.

### Módulo: Reforço (Convocação)

**RF18 — Gatilho da primeira onda.** Dispara `ConvocacaoReforco` (`onda = 1`). Não se aplica se `cancelada = true`.

**RF19 — Convocação via WhatsApp, sem login.** Token único, confirmação com um clique.

**RF20 — Confirmação atômica.** Primeiro a confirmar preenche a vaga.

**RF21 — Escalonamento entre pastorais.** Nova onda pra próxima pastoral elegível.

**RF22 — Expiração de token.** Vira `EXPIRADO`.

**RF23 — Vaga sem preenchimento.** Permanece vazia.

**RF23.1 — Lembrete de missa.** Não se aplica se `cancelada = true`.

### Módulo: LGPD

**RF24 — Alerta de inatividade.**

**RF25 — Anonimização.** Sempre pelo Super Admin, com `motivoAnonimizacao`.

**RF26 — Exclusão sob pedido.** Exclusivo Super Admin, via RF25. Exige segunda confirmação: digitar `nomeCompleto` exato do `Membro`.

**RF27 — Purga trimestral automática.**

### Módulo: Configuração Global

**RF28 — Configuração global do sistema.** `ConfiguracaoSistema`, singleton.

---

## Requisitos Não Funcionais

**RNF01 — LGPD: dado sensível.**

**RNF02 — LGPD: purga trimestral.** Ver RF27.

**RNF03 — Segurança: least-privilege.**

**RNF04 — Disponibilidade (infra).** VM ARM Oracle Cloud Free Tier.

**RNF05 — Limite de envio WhatsApp.** ~480/mês.

**RNF06 — Performance.**

**RNF07 — Configurabilidade.** `minimoMissasMensal`, `ordemConvocacaoReforco`, `intervaloMinutosEscalonamento`, `permiteConvocacaoReforco`, `diaGeracaoMensal`, `tipoMinimo`, `somenteSolene`, `complexidade`, `prazoAntecedenciaReforcoHoras`, `prazoLembreteHoras`, `limiteMissaSemanaComum`, `limiteMissaSemanaSolene`, `limiteMissaDominicalComum`, `limiteMissaDominicalSolene`, `elegivelLimiteParticipacao`, `prioridadeAtiva`.

**RNF08 — Sem dependência de IA.**

---

## Pendências abertas

- **[Intencional, não é lacuna]** O algoritmo exato de sorteio/consolidação (RF15.1) e de prioridade Acólito/Coroinha (RF15.2) fica definido na Frente 5.