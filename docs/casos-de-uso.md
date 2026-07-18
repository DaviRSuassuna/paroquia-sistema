# Casos de Uso — Paróquia em Rede

## Super Admin

- Criar, editar e desativar Pastoral
- Criar/editar Membro
- Gerenciar vínculo Membro-Pastoral
- Criar/editar Funcao
- Criar/editar MissaRecorrente
- Criar/gerenciar Usuario e UsuarioPerfil (atribuir papéis a pessoas em pastorais diferentes)
- Cancelar Missa
- Anonimizar Membro (por inatividade de 12 meses, ou por pedido imediato de exclusão) — sempre com `motivoAnonimizacao` preenchido
- Configurar ConfiguracaoSistema (prazos globais: antecedência do reforço, antecedência do lembrete)

## Admin Pastoral (escopo: sua pastoral)

- Gerenciar aptidão Membro-Funcao
- Configurar Pastoral (ConfiguracaoEscalonamento: ordem de convocação, intervalo entre ondas, dia de geração mensal, ativar/desativar convocação de reforço)
- Enviar FormularioDisponibilidade mensal
- Revisar Disponibilidade recebida
- Editar Escala/Missa gerada automaticamente (correção manual)
- Visualizar alerta de membros sem resposta ao formulário há X meses

## Padre (somente leitura + flags)

- Visualizar Escala e Missa
- Marcar Missa como solene
- Cancelar Missa

## Membro (via link, sem login)

- Preencher FormularioDisponibilidade
- Confirmar ConvocacaoReforco

## Sistema (automático, sem ator humano)

- Gerar instâncias de Missa a partir de MissaRecorrente (no dia configurado em `diaGeracaoMensal`)
- Gerar Escala (sorteio dentro das restrições de aptidão e disponibilidade)
- Disparar ConvocacaoReforco — 1ª e 2ª onda, por ordem de pastoral, respeitando `prazoAntecedenciaReforcoHoras` e `permiteConvocacaoReforco`
- Enviar lembrete de missa aos escalados, respeitando `prazoLembreteHoras`
- Executar job trimestral de purga/anonimização (jan/abr/jul/out)
