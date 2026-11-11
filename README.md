# GabGym — Backend 🏋️

API do **GabGym**, aplicação de acompanhamento de treinos, alimentação e evolução física. Este repositório cuida dos dados, das regras de negócio e da segurança do sistema.

## Tecnologias

**Java 21** · **Spring Boot** · **Spring Security + JWT** · **PostgreSQL** · **JPA/Hibernate** · **Flyway** · **Maven**

## Funcionalidades

- Autenticação e controle de acesso de usuários.
- Registro de alimentação, exercícios e diário de atividades.
- Metas pessoais e acompanhamento de progresso.
- Biblioteca de alimentos e exercícios.
- Portal do personal trainer, com vínculo e permissões entre personal e aluno.
- Testes automatizados para autenticação e autorização.

## Executar localmente

**Pré-requisitos:** Java 21, banco PostgreSQL e variáveis de ambiente configuradas conforme [`.env.example`](./.env.example).

```bash
# Linux / macOS
./mvnw spring-boot:run

# Windows (PowerShell)
.\mvnw.cmd spring-boot:run
```

A API utiliza a porta **8080** por padrão. Para testar:

```bash
./mvnw test
```

Variáveis principais: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET` e `CORS_ORIGIN`. **Nunca envie credenciais ou segredos ao GitHub.**

## Estrutura

- `controllers/` — endpoints da API.
- `entities/` e `repositories/` — dados e acesso ao banco.
- `security/` e `config/` — autenticação e configuração.
- `db/migration/` — versionamento do banco com Flyway.

**Frontend:** [gabgym_frontend](https://github.com/Ivano-Gabriel/gabgym_frontend)  
**Status:** projeto em desenvolvimento e evolução contínua.
