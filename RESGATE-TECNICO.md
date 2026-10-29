# GABGYM — Resgate técnico (PR de segurança)

> Proposta para revisão. Este branch NÃO foi publicado e não muda a aplicação em produção.

## O que foi protegido

- JWT HMAC usando **JWT_SECRET em base64** (mínimo de 32 bytes); geração e expiração no backend, versão de sessão, logout com revogação.
- BCrypt para senhas, autenticação de token com usuário ativo consultado no banco.
- Cadastro público aceita apenas `username` e `password`, com papel forçado `ALUNO`.
- `SUPER_ADMIN` não é registrável nem promovível por cliente.
- Autorização por proprietário real nos endpoints de perfis, metas e diários, nunca por `userId` informado no navegador.
- Perfil e listagem administrativos sem envio de hashes de senha.
- Conta de demonstração fixa removida do inicializador.
- CORS limitado às origens configuradas; rotas privadas exigem autenticação.
- Testes unitários e de integração HTTP no GitHub Actions.

## Modelo de permissões

| Recurso | Aluno | Superadmin |
| --- | --- | --- |
| Ler o próprio perfil, metas, diários | Sim | Pode consultar via rotas administrativas |
| Criar/atualizar o próprio perfil, metas, diários | Sim | Não edita dados de alunos |
| Consultar lista de alunos | Não | Sim |
| Verificar disponibilidade em `GET /ping` | Público | Público |

Apenas ler alunos é o escopo inicial do superadmin; possíveis edições devem ter auditoria, regras e autorização específicas.

## Instalação e segredo novo

1. Use um banco persistente ISOLADO do Nummo, inicialmente PostgreSQL em ambiente de teste. Faça backup do banco antigo ANTES de migrações.
2. Configure no Render via Environment: `DB_URL` (URL JDBC), `DB_USERNAME`, `DB_PASSWORD`, `CORS_ORIGIN` (URL exata do front), `JWT_SECRET` (base64 da saída de `openssl rand -base64 48`), e `PORT` opcional.
3. **Nunca armazene a chave no GitHub, no frontend, num PR ou na conversa.** A chave antiga que apareceu no histórico Git deve ser considerada comprometida e substituída. Trocar a chave invalida as sessões antigas.
4. Na primeira inicialização com banco configurado, habilite temporariamente `ADMIN_BOOTSTRAP_ENABLED=true`, defina `ADMIN_BOOTSTRAP_USERNAME` (3–40 chars) e `ADMIN_BOOTSTRAP_PASSWORD` (senha única forte, 16+ chars). Reinicie; após criar o primeiro admin, **remova as duas variáveis sensíveis e desative o bootstrap**.
5. Não compartilhe nem reutilize a senha do NUMMO. Configure também `ADMIN_TOTP_SECRET` (base32, ao menos 20 bytes aleatórios) em variáveis privadas e cadastre essa chave num app autenticador TOTP local. Sem esse segredo, o superadmin não consegue entrar. O código de seis dígitos é obrigatório no login administrativo.
6. Defina `REACT_APP_API_URL=https://NOME-DA-API.onrender.com/api` no ambiente de build do frontend Vercel.
7. Execute smoke tests: `GET /ping` => 200; registro aluno => 201; login => token; `GET /api/users/me` autorizado => 200; outro usuário => 403; `/api/admin/students` para aluno => 403; como admin => 200.

## Limitações antes de produção

- Ainda é **primeira etapa de segurança**, não uma auditoria concluída nem garantia de segurança absoluta.
- Banco usa `spring.jpa.hibernate.ddl-auto=update` temporariamente: antes de produção introduzir Flyway e `validate`; inspecionar e migrar os dados existentes sem apagá-los.
- Rate limit de login é local à instância e reinicia com a aplicação. Para maior escala, usar limitação distribuída e controles contra abuso.
- MFA TOTP inicial de superadmin depende de configuração privada; falta fluxo de recuperação segura, proteção contra dispositivos perdidos, recuperação de senha, logs estruturados/auditoria, backups e testes E2E completos.
- JWT fica no `sessionStorage` no frontend por compatibilidade; necessita de proteção contínua contra XSS. Futuramente avaliar cookies HttpOnly com CSRF e refresh seguro.
- A cópia de chaves antigas no histórico do Git exige rotação; apagar somente o arquivo atual não apaga commits antigos.
- O Render Free suspende instâncias por ociosidade e banco PostgreSQL Free expira; um `/ping` ou cron não resolve durabilidade do banco.
- Tratar perfis, peso, idade, saúde e registros de treino com minimização e proteção conforme LGPD.

## Validação de código

```bash
# Java 21 e Maven wrapper
chmod +x ./mvnw
./mvnw clean test
./mvnw clean verify
```

Os testes têm recursos `src/test/resources/application.properties` com H2, distintos de produção. O Dockerfile roda o `verify` durante o build; falhas bloqueiam imagem nova.
