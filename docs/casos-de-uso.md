# Casos de Uso — Paróquia em Rede

## Super Admin

- Criar, editar e desativar Pastoral (incluindo `elegivelLimiteParticipacao`)
- Criar/editar Membro
- Gerenciar vínculo Membro-Pastoral
- Criar/editar Funcao (incluindo pastoral dona, complexidade e tipoMinimo)
- Criar/editar MissaRecorrente
- Criar/gerenciar Usuario e UsuarioPerfil
- Criar Missa especial, sem vínculo com MissaRecorrente
- Definir manualmente as vagas de Escala ao criar uma Missa especial (qualquer Funcao, já que ESPECIAL é o topo da hierarquia)
- Preencher qualquer vaga de Escala, sem restrição de pastoral
- Marcar Missa como solene (dispara criação automática de vagas, exceto se cancelada)
- Cancelar Missa (suprime automações futuras)
- Anonimizar Membro
- Excluir Membro sob pedido (RF26) — aciona anonimização (RF25) e exige segunda confirmação explícita: digitar o nomeCompleto exato do Membro
- Configurar ConfiguracaoSistema
- Configurar limites de participação e ativar prioridade (`prioridadeAtiva`) — exclusivo, só para Pastorais elegíveis, e só quando os campos operacionais básicos estiverem preenchidos

## Admin Pastoral (escopo: sua pastoral)

- Gerenciar aptidão Membro-Funcao dos membros da própria pastoral, individualmente
- Configurar Pastoral (ConfiguracaoEscalonamento, exceto limites e ativação de prioridade, exclusivos do Super Admin)
- Enviar FormularioDisponibilidade mensal
- Revisar Disponibilidade recebida
- Editar/preencher vaga de Escala — restrito à própria pastoral
- Visualizar alerta de membros sem resposta ao formulário

## Padre (leitura + criação/gestão de Missa especial + flags)

- Visualizar Escala e Missa
- Criar Missa especial
- Definir manualmente as vagas de Escala (qualquer Funcao)
- Marcar Missa como solene (dispara criação automática de vagas, exceto se cancelada)
- Cancelar Missa (suprime automações futuras)

## Membro (via link, sem login)

- Preencher FormularioDisponibilidade
- Confirmar ConvocacaoReforco

## Sistema (automático, sem ator humano)

- Gerar instâncias de Missa a partir de MissaRecorrente
- Gerar Escala automaticamente, considerando: Funcoes elegíveis por hierarquia tipoMinimo como obrigatórias (RF15.3); consolidação de funções simples (RF15.1); prioridade Acólito/Coroinha e limites, só se elegível e com prioridadeAtiva = true (RF15.2/RF10.2)
- Criar vagas de Escala adicionais quando uma Missa é marcada como solene, salvo se cancelada
- Disparar ConvocacaoReforco, salvo se cancelada
- Enviar lembrete de missa aos escalados, salvo se cancelada
- Executar job trimestral de purga/anonimização