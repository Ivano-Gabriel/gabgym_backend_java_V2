# GABGYM piloto — Supabase USA + Render (2026-10-29)

## Projeto realmente provisionado

- Supabase project: `gabgym-piloto-us`
- Supabase ref: `klydlavdlvyqdykyyiqh`
- Região: `us-west-1` (Norte da Califórnia), perto do Render Oregon
- Organização: Nummo, **com banco separado** de `nummo-producao-sp`.
- Plano consultado na criação: **US$ 0/mês** (Free).
- Schema privado `gabgym` pré-criado no PostgreSQL. Papel técnico `gabgym_app` criado e com `USAGE` / `CREATE` nele.
- `anon` e `authenticated` **não** têm `USAGE` no schema. Nenhuma tabela de aluno criada até o deploy da Flyway.
- A migração administrativa é `gabgym_private_schema_app_role` no Supabase; `src/main/resources/db/migration/V1__baseline_gabgym.sql` é a baseline gerenciada pelo Spring/Flyway, executada posteriormente.

## Credenciais e configuração — executar privadamente

Transferências automáticas de credenciais entre contas ficaram indisponíveis. **NÃO cole os valores secretos em conversas, PRs, repositórios, logs nem issues.** Complete as etapas nos painéis privados.

1. Supabase > projeto `gabgym-piloto-us` > SQL Editor: defina **uma nova senha aleatória forte** para o papel técnico:
   `ALTER ROLE gabgym_app WITH PASSWORD 'SENHA_GERADA_EM_GERENCIADOR_PRIVADO';`
   Substitua o placeholder apenas no painel seguro e não salve essa query no Git.
2. Supabase > **Connect** > Session Pooler, porta `5432`: **copie o host real**, inclusive índice `aws-0`, `aws-1` etc. NÃO infira host só pela região.
3. Render > crie serviço separado de teste com plano **Free** (não altere o serviço antigo), repo `Ivano-Gabriel/gabgym_backend_java_V2`, branch `rescue/security-auth-superadmin-2026-10-29`, runtime **Docker**, região Oregon. Configure variáveis **antes** de publicar:
   - `DB_URL=jdbc:postgresql://HOST_REAL_DO_SESSION_POOLER:5432/postgres?sslmode=require`
   - `DB_USERNAME=gabgym_app.klydlavdlvyqdykyyiqh`
   - `DB_PASSWORD=...senha do papel criada no Supabase...`
   - `JWT_SECRET=...base64 de 48 bytes aleatórios, diferente do segredo antigo...`
   - `CORS_ORIGIN=https://gabgym-frontend.vercel.app` (troque pelo domínio exato validado)
   - `ADMIN_BOOTSTRAP_ENABLED=false` até configurar credenciais e MFA; `JPA_DDL_AUTO=validate`
   - `SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=3`.
4. Gere JWT secret no seu computador com `python -c "import secrets,base64; print(base64.b64encode(secrets.token_bytes(48)).decode())"`. Nunca use a chave de exemplo dos testes.
5. Primeiro lançamento: confira `/ping`; logs devem mostrar Flyway V1 aplicada ao schema `gabgym` e Hibernate validando sem erro. Confirme as tabelas pelo Supabase e rode os Security Advisors. Se falhar, não dê merge.
6. Bootstrap do admin **somente depois de confirmar banco e segredo**: `ADMIN_BOOTSTRAP_ENABLED=true`, `ADMIN_BOOTSTRAP_USERNAME=...`, `ADMIN_BOOTSTRAP_PASSWORD=...` (senha única com >=16 caracteres) e `ADMIN_TOTP_SECRET=...` (base32, >=20 bytes). Gere secret base32 com `python -c "import secrets,base64; print(base64.b32encode(secrets.token_bytes(20)).decode().rstrip('='))"`. Cadastre a mesma chave manualmente em app TOTP pelo modo 'inserir chave'. Quando a conta aparecer, apague a variável de senha e desative o bootstrap.
7. No Vercel de staging configure `REACT_APP_API_URL=https://URL_REAL_DO_RENDER/api`. Regere o build e valide login, logout, MFA, listagem de alunos e isolamento entre contas antes de qualquer merge.

## Regras operacionais

- O serviço Render original usa branch `main` com **autodeploy**. Um merge hoje dispararia publicação do backend antigo com configuração incompleta. Não fazer merge antes do smoke test e das variáveis finais.
- O novo serviço Render **ainda não foi criado**: a ligação automática de segredos foi bloqueada; nenhuma senha ou chave ficou escrita neste repo.
- Banco Supabase Free: pode pausar por pouca atividade, não fornece backup automático e está sujeito às cotas do plano. Fazer exportações regulares e nunca usar como única cópia de dados pessoais importantes.
- Não conectar os alunos diretamente ao Supabase Data API; manter o frontend falando exclusivamente com Spring Boot.
- Na mudança definitiva para São Paulo, criar **novo projeto/organização**, exportar e restaurar PostgreSQL, validar Flyway, fazer cutover com janela/backup, atualizar credenciais e confirmar integridade. Não pressupor migração de região in-place.
- Antes de dados reais de saúde, revisar LGPD, políticas de retenção, backups, MFA e controles de acesso.
