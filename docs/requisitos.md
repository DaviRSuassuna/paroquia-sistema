# Requisitos Funcionais e Não Funcionais — Paróquia em Rede

## Nota de escopo

Este documento substitui integralmente a versão anterior. Principais mudanças desde a última revisão:

- Escalonamento por **disponibilidade individual** (sorteio dentro de quem está apto e disponível), sem grupos rotativos.
- **Presença, pontuação/punição e papel Coordenador** fora de escopo.
- **Gemini fora de escopo** — formulário de disponibilidade é 100% estruturado (checkboxes), sem campo de texto livre.
- Reforço em **ondas entre Pastorais** (ex: Acólitos → Coroinhas), por `ordemConvocacaoReforco`, não mais por faixa etária dentro da mesma pastoral.
- **Cadastro estrutural centralizado no Super Admin**: `Membro`, `Funcao`, `MissaRecorrente` e o vínculo `Membro`-`Pastoral` só podem ser criados/editados pelo Super Admin — decisão para reduzir risco de erro de cadastro por administradores leigos.
- **Geração de Missa/Escala e disparo de reforço são 100% automáticos**, disparados por configuração (`diaGeracaoMensal`, `prazoAntecedenciaReforcoHoras`), não por ação manual do Admin Pastoral.
- Convocação de reforço pode ser **desativada por pastoral** (`permiteConvocacaoReforco`), deixando a escala incompleta quando desligada.
- **Anonimização (por inatividade ou por pedido) é sempre acionada pelo Super Admin**, nunca pelo Admin Pastoral — a saída de um membro da paróquia (não de uma pastoral isolada) é o critério.
- **Prazos globais do sistema** (gatilho de reforço, lembrete de WhatsApp) são configuráveis pelo Super Admin via `ConfiguracaoSistema` (singleton), não por pastoral.

---

## Requisitos Funcionais

### Módulo: Autenticação e Perfis

**RF01 — Login com múltiplo perfil.** Um `Usuario` pode ter mais de um `UsuarioPerfil` (ex: Admin Pastoral em mais de uma `Pastoral`). Com um único perfil, entra direto; com mais de um, escolhe qual ativar, podendo trocar sem deslogar.

**RF02 — Revalidação de perfil ativo por requisição.** Todo endpoint sensível confere se o `UsuarioPerfil` informado como ativo pertence de fato ao usuário autenticado.

**RF03 — Três papéis.** `SUPER_ADMIN` (acesso completo, todas as pastorais), `ADMIN_PASTORAL` (escopo da própria `Pastoral`), `PADRE` (leitura + flags, todas as pastorais).

### Módulo: Cadastros Estruturais (exclusivo Super Admin)

**RF04 — Cadastro de Pastoral.** `Pastoral` tem nome, descrição e status ativo/inativo. Pastorais são independentes entre si.

**RF05 — Cadastro de Membro.** Criação e edição de `Membro` é exclusiva do Super Admin, para reduzir erro de cadastro por perfis leigos (Admin Pastoral).

**RF06 — Vínculo Membro-Pastoral.** A associação de um `Membro` a uma ou mais `Pastoral`s é gerenciada exclusivamente pelo Super Admin.

**RF07 — Cadastro de Função.** Criação e edição de `Funcao` — nome, descrição, `tiposMissaPermitidos`, `somenteSolene` — é exclusiva do Super Admin. `Funcao` está vinculada a uma ou mais `Pastoral`s via relação N:N.

**RF08 — Cadastro de Missa Recorrente.** Criação e edição de `MissaRecorrente` (dia da semana, horário, tipo) é exclusiva do Super Admin. `MissaRecorrente` não pertence a uma `Pastoral` específica — é um padrão da paróquia como um todo; o vínculo com pastoral(is) acontece indiretamente via `Funcao` exigida em cada `Escala`.

### Módulo: Aptidão e Configuração Operacional (Admin Pastoral)

**RF09 — Aptidão do Membro por Função.** A relação `Membro`-`Funcao` (N:N) representa quais funções o membro sabe exercer (ex: carregar cruz, usar o missal) — cadastro único, gerenciado pelo Admin Pastoral, diferente da escalação em si (por missa).

**RF10 — Configuração de escalonamento por Pastoral.** `ConfiguracaoEscalonamento` (1:0..1 com `Pastoral`), gerenciada pelo Admin Pastoral, define:
- `ordemConvocacaoReforco` — posição da pastoral na fila de escalonamento
- `intervaloMinutosEscalonamento` — tempo de espera antes de escalar para a próxima pastoral
- `permiteConvocacaoReforco` — liga/desliga o disparo de reforço pra essa pastoral
- `diaGeracaoMensal` — dia do mês em que Missa/Escala do mês seguinte são geradas automaticamente

Pastorais sem `ConfiguracaoEscalonamento` nunca recebem convocação de reforço.

### Módulo: Disponibilidade

**RF11 — Formulário mensal por Pastoral.** `FormularioDisponibilidade` é aberto por `Pastoral`, com mês de referência, link do Google Forms, datas de abertura/fechamento e `minimoMissasMensal`.

**RF12 — Disponibilidade vinculada a padrão recorrente.** `Disponibilidade` se relaciona com `MissaRecorrente` (N:N), não com `Missa` concreta — permite abrir o formulário antes de as missas do mês serem geradas. Coleta 100% estruturada (checkboxes de dias/padrões disponíveis), sem campo de texto livre.

**RF13 — Registro de última resposta.** O sistema grava em `Membro.ultimaRespostaDisponibilidade` a data da resposta mais recente a qualquer `FormularioDisponibilidade`, em qualquer pastoral. Esse campo alimenta o alerta de inatividade (RF24) e o cálculo de anonimização (RF25).

### Módulo: Missa e Escala (automático)

**RF14 — Geração automática de Missa.** No dia configurado (`diaGeracaoMensal`), o sistema gera automaticamente as instâncias de `Missa` do próximo mês a partir dos padrões ativos em `MissaRecorrente`.

**RF15 — Geração automática de Escala por sorteio.** Junto com a geração de `Missa`, o sistema monta `Escala` sorteando, para cada `Funcao` necessária, entre os `Membro`s aptos (RF09) e disponíveis (RF12) para o `MissaRecorrente` correspondente, respeitando `minimoMissasMensal`.

**RF16 — Vaga em aberto.** Sem candidato suficiente, `Escala` é criada com `Membro` nulo, disparando o fluxo de reforço (RF18+) se `permiteConvocacaoReforco = true`; senão, a vaga permanece em aberto e a escala é exportada incompleta.

**RF17 — Edição manual.** Admin Pastoral pode editar manualmente `Escala`/`Missa` geradas automaticamente, para correções pontuais.

**RF17.1 — Cancelamento de Missa.** Só Super Admin e Padre podem marcar `Missa.cancelada = true`. Cancelar preserva histórico e `Escala`s associadas (não apaga registros).

**RF17.2 — Flag de solenidade.** Só o Padre pode marcar `Missa.solene = true`.

### Módulo: Reforço (Convocação)

**RF18 — Gatilho da primeira onda.** `prazoAntecedenciaReforcoHoras` (definido em `ConfiguracaoSistema`, global) determina quantas horas antes da `Missa` o sistema verifica se ainda há vaga em aberto. Se sim, e `permiteConvocacaoReforco = true` na pastoral elegível, dispara `ConvocacaoReforco` (`onda = 1`) para todos os `Membro`s aptos à `Funcao` em falta, pertencentes à `Pastoral` de menor `ordemConvocacaoReforco` entre as elegíveis.

**RF19 — Convocação via WhatsApp, sem login.** Cada `ConvocacaoReforco` gera `token` único, enviado por link individual via WhatsApp (Evolution API, somente envio). Confirmação com um clique, sem autenticação.

**RF20 — Confirmação atômica.** O primeiro `Membro` a confirmar preenche a vaga; demais tokens da mesma onda são invalidados (`status = INVALIDADO`).

**RF21 — Escalonamento entre pastorais.** Sem confirmação dentro do `intervaloMinutosEscalonamento` da pastoral da onda atual, dispara nova onda para a próxima pastoral elegível na fila (`ordemConvocacaoReforco` seguinte).

**RF22 — Expiração de token.** `ConvocacaoReforco` não respondida no prazo tem `status` alterado para `EXPIRADO`.

**RF23 — Vaga sem preenchimento.** Se nenhuma onda for confirmada, a vaga permanece vazia — sem escalonamento adicional além das pastorais configuradas.

**RF23.1 — Lembrete de missa.** `prazoLembreteHoras` (em `ConfiguracaoSistema`) determina com quantas horas de antecedência o sistema envia lembrete via WhatsApp aos membros escalados.

### Módulo: LGPD

**RF24 — Alerta de inatividade.** Admin Pastoral visualiza alerta de membros sem resposta a formulário de disponibilidade há X meses, com base em `Membro.ultimaRespostaDisponibilidade`.

**RF25 — Anonimização.** Um `Membro` pode ser anonimizado (`anonimizado = true`, `dataAnonimizacao` e `motivoAnonimizacao` preenchidos) em dois cenários, ambos acionados **exclusivamente pelo Super Admin**:
- **Por inatividade**: sem resposta a `Disponibilidade` há 12 meses, em nenhuma pastoral. `motivoAnonimizacao` é preenchido automaticamente (ex: "Inatividade - 12 meses").
- **Por pedido imediato** (RF26): reutiliza a mesma rotina, pulando a checagem de 12 meses. `motivoAnonimizacao` é preenchido manualmente pelo Super Admin antes da confirmação — funciona como trava contra exclusão acidental.

Em ambos os casos, o registro (`id`) do `Membro` é preservado com dados anonimizados/randomizados; `Escala` e `ConvocacaoReforco` antigos continuam íntegros, evitando efeito cascata.

**RF26 — Exclusão sob pedido.** Pedido de exclusão explícito do titular é atendido de forma imediata, fora do ciclo trimestral, executado exclusivamente pelo Super Admin, via a mesma rotina de anonimização (ver RF25).

**RF27 — Purga trimestral automática.** No início de cada trimestre civil (jan/abr/jul/out): exclusão de histórico de `Missa`/`Escala` com mais de 1 ano, e exclusão física de todo `Membro` já anonimizado nesse momento.

### Módulo: Configuração Global

**RF28 — Configuração global do sistema.** `ConfiguracaoSistema` é uma instância única (singleton), gerenciada pelo Super Admin, definindo:
- `prazoAntecedenciaReforcoHoras` — gatilho da 1ª onda de reforço (RF18)
- `prazoLembreteHoras` — antecedência do lembrete de missa (RF23.1)

---

## Requisitos Não Funcionais

**RNF01 — LGPD: dado sensível.** Dados de participação religiosa são tratados como dado pessoal sensível (Art. 5º, II, LGPD).

**RNF02 — LGPD: purga trimestral.** Ver RF27.

**RNF03 — Segurança: least-privilege.** `SUPER_ADMIN` (tudo, incl. cadastros estruturais e anonimização), `ADMIN_PASTORAL` (escopo da própria pastoral: aptidão, configuração de escalonamento, edição manual de escala/missa), `PADRE` (leitura + flags, todas as pastorais).

**RNF04 — Disponibilidade (infra).** VM ARM do Oracle Cloud Free Tier (2 OCPUs, 12 GB RAM), operação estável dentro desses limites.

**RNF05 — Limite de envio WhatsApp.** Respeitar ~480 mensagens/mês via Evolution API (chip dedicado), evitando bloqueio. Somente envio — sem leitura de resposta via webhook.

**RNF06 — Performance.** Consultas de listagem (escala, membros, disponibilidade) respondem em tempo hábil para tela síncrona, sem cache necessário no volume de uma paróquia.

**RNF07 — Configurabilidade.** Regras de negócio variáveis são dado, não lógica fixa no código: `minimoMissasMensal`, `ordemConvocacaoReforco`, `intervaloMinutosEscalonamento`, `permiteConvocacaoReforco`, `diaGeracaoMensal`, `tiposMissaPermitidos`, `somenteSolene`, `prazoAntecedenciaReforcoHoras`, `prazoLembreteHoras`.

**RNF08 — Sem dependência de IA.** Nenhuma integração com Gemini ou parsing de texto livre; disponibilidade é 100% estruturada.

---

## Pendências abertas

- Se a exclusão imediata/anonimização sob pedido (RF26) precisa de uma segunda etapa de confirmação na tela (além do `motivoAnonimizacao` obrigatório), já que é uma decisão sobre dado de terceiro — a definir, embora o risco tenha caído bastante por não ser mais um DELETE físico direto.
