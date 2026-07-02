# Sistema de Gestão Paroquial

Sistema de gestão para paróquia, com automação de lembretes via WhatsApp: cadastro de membros, pastorais, funções litúrgicas, escalas de missa, controle de presença e alertas automáticos de falta.

Em desenvolvimento ativo. Deploy previsto para agosto de 2026.

## Sobre o projeto

Substitui a gestão manual de escalas e comunicação da paróquia. Cobre cadastro de membros, controle de pastorais e funções litúrgicas, montagem de escalas de missa, acompanhamento de presença e falta, e disparo automático de lembretes via WhatsApp antes de cada escala.

Proteção de dados pessoais (LGPD) é tratada como requisito estrutural desde o início do projeto, não como etapa final.

## Stack

- Frontend: React + TypeScript
- Backend: Java + Quarkus
- Banco de dados: PostgreSQL + Hibernate/Panache
- Autenticação: Keycloak
- WhatsApp: Evolution API
- Infraestrutura: Docker Compose, Oracle Cloud Free Tier (ARM)

## Estrutura do repositório

paroquia-sistema/
├── backend/          API Quarkus
├── frontend/         Aplicação React + TypeScript
├── infra/            Docker Compose, configs de deploy
│   └── env/          Template de variáveis de ambiente
├── docs/             Diagramas de arquitetura e domínio
└── README.md

## Rodando localmente

Pré-requisitos: JDK 21+, Quarkus CLI, Node.js 22 LTS, Podman (ou Docker) com Compose, PostgreSQL via container.

Backend:

cd backend
cp ../infra/env/.env.example .env
./mvnw quarkus:dev

API em http://localhost:8080, Swagger UI em http://localhost:8080/q/swagger-ui.

Frontend:

cd frontend
npm install
npm run dev

## Roadmap

O desenvolvimento segue um roteiro de 10 frentes, da fundação (ambiente, dados, segurança) até o deploy em produção. Acompanhamento detalhado no Notion: https://app.notion.com/p/38b2f03176c581569cefe283a380d7e5

## Segurança e dados

O sistema lida com dados pessoais de membros de uma comunidade real. Segredos (credenciais de banco, chaves de API) não são versionados; configure sempre via .env local, com base em infra/env/.env.example.

## Autoria

Desenvolvido por Davi R. Suassuna