# PROGRESSO — Café na Mão

**Última atualização:** 13/11/2026
**Status:** Etapas 1 a 8 concluídas e validadas no GitHub Actions. Próxima etapa
ainda não autorizada.
**Git:** commit `6e9a544` (`feat: implementa etapa 8`) enviado para `origin/main`.

Este arquivo registra o estado real do projeto, as decisões tomadas, as verificações
executadas, as pendências e o próximo passo. Para retomar, leia também
`projeto 1/.agents/skills/cafenamao/SKILL.md`.

## 1. Visão atual

O Café na Mão é uma aplicação acadêmica de pedidos de bebidas. O front-end é
estático, abre por `file://` e não exige build, servidor local ou dependências npm.
O backend é uma API REST Java/Spring Boot.

Desde a Etapa 8:

- o catálogo tenta a API e usa `js/dados.js` como fallback;
- pedidos, histórico, status e limpeza usam a API quando a sincronização inicial
  funciona;
- sem API, o front-end continua em modo local com aviso visível;
- falha ao criar pedido em modo API não cai silenciosamente para o fluxo local;
- o backend aceita chamadas de páginas `file://` por CORS com `Origin: null`;
- PostgreSQL é provisionado por Docker Compose;
- o backend tem cobertura JaCoCo com gate de qualidade;
- há um workflow de CI para front-end, backend e contrato Newman.

## 2. Etapas concluídas

### Etapas 1 a 6 — front-end e documentação

- Página inicial, catálogo, detalhes, quantidade e carrinho.
- Pagamento simulado, confirmação, histórico, status e limpeza.
- Persistência local do carrinho e do histórico em `localStorage`.
- Acessibilidade, responsividade, página Sobre, requisitos, plano e relatório.
- Testes unitários com `node:test`.

### Etapa 7 — API REST e testes de integração

- API de bebidas em `/api/bebidas`.
- API de pedidos em `/api/pedidos`, com total calculado no servidor.
- Erros padronizados, validação e carga inicial de seis bebidas.
- H2 em memória por padrão e perfil PostgreSQL.
- Coleção Postman/Newman.
- 29 testes JUnit 5/MockMvc na conclusão da etapa.

### Etapa 8 — integração, cobertura, banco e CI

- `js/api.js` criado com URL base, timeout, `ErroApi`, normalização e operações de
  catálogo/pedidos.
- `tests/api.test.js` criado com 12 testes.
- `menu.html`/`js/menu.js` integrados à API, com badge de origem e fallback local.
- `pedidos.html`/`js/pedidos.js` integrados para criação, histórico, status e
  limpeza, mantendo o modo local quando a API não está disponível.
- `style.css` recebeu os estilos do badge de origem.
- `WebConfig.java` adicionado com CORS para `/api/**` e `Origin: null`.
- `CorsIntegrationTest` adicionado com 2 testes.
- `backend/pom.xml` recebeu JaCoCo 0.8.12, relatório e gate de 80% de linhas e 50%
  de branches.
- `compose.yaml` criado com PostgreSQL 16 Alpine, healthcheck e volume.
- Porta host padrão do PostgreSQL alterada para **5433** para evitar conflito com o
  PostgreSQL nativo da máquina na 5432.
- `.github/workflows/ci.yml` criado com jobs de front-end, backend/JaCoCo e contrato
  Newman, incluindo publicação do JAR e do relatório de cobertura.
- Documentação atualizada: `AGENTS.md`, `README.md`, `sobre.html`,
  `docs/requisitos.md`, `docs/plano-de-testes.md` e
  `docs/relatorio-testes.md`.

## 3. Decisões técnicas registradas

1. O front-end continua sem build e sem servidor; a API é opcional na execução por
   `file://`.
2. A URL padrão é `http://localhost:8080/api`, com override por
   `window.CAFE_NA_MAO_API_URL` ou `?api=`.
3. O fallback local é usado na carga inicial de catálogo e histórico. Em modo API,
   falha de criação de pedido não vira pedido local silencioso.
4. O servidor continua sendo a fonte de verdade do preço e do total.
5. CORS é restrito a `/api/**`, mas aceita `Origin: null` para desenvolvimento com
   `file://`; não é uma configuração para produção pública.
6. PostgreSQL usa a porta host 5433 por padrão, com `DB_PORT`, `DB_URL`, `DB_USER` e
   `DB_PASSWORD` configuráveis.
7. O gate JaCoCo está no `verify`; cobertura abaixo de 80% de linhas ou 50% de
   branches deve falhar o build.
8. O CI usa Node 24 e Java 21, com Maven Wrapper e Newman via `npx`. O contrato
   sobe a API, espera o endpoint e executa o Newman na mesma etapa, encerrando o
   processo Java ao final.
9. Os testes E2E foram avaliados nesta etapa, mas permanecem temporários e não
   versionados: dependem do Edge/Windows e não há autorização para introduzir uma
   estrutura E2E ou dependência nova.

## 4. Verificações executadas

### Front-end

```powershell
npm test
```

Resultado final:

```text
tests 65
pass 65
fail 0
cancelled 0
skipped 0
```

Distribuição: 10 de dados, 10 de detalhes, 14 de carrinho, 19 de histórico e 12 do
cliente HTTP.

### Backend e cobertura

```powershell
cd backend
.\mvnw.cmd -B verify
```

Resultado final:

- **31 testes aprovados**, 0 falhas, 0 erros, 0 ignorados;
- 11 testes de bebidas, 18 de pedidos e 2 de CORS;
- JAR gerado em `backend/target/cafe-na-mao-api-0.1.0.jar`;
- relatório JaCoCo gerado;
- gate atendido com **93,59% de linhas** (263/281) e **62,50% de branches**;
- `BUILD SUCCESS`.

### Contrato Postman/Newman

```powershell
npx --yes newman run backend\postman\CafeNaMao.postman_collection.json `
  -e backend\postman\CafeNaMao.postman_environment.json `
  --reporters cli --color off
```

Resultado: **18 requisições, 0 falhas, 32 assertions aprovadas**.

### E2E no navegador

Executado com Edge 156 headless via CDP, usando script temporário fora do
repositório.

- `menu.html` com API: badge `API` e 6 cards.
- `pedidos.html` com API: histórico sincronizado.
- Pedido `PED-501ECE45` criado com 2 Espressos, total `R$ 8,00`.
- Status alterado para `em-preparo` e persistido na API.
- Limpeza do histórico persistida na API.
- Sem API: catálogo local com 6 cards e mensagens de indisponibilidade.
- Console sem erros de JavaScript.

A página Sobre atualizada foi verificada separadamente em Edge headless, nas
larguras 390, 768 e 1366 px:

- título, H1, 3 cards de contexto e 3 blocos de escopo presentes;
- textos de integração, JaCoCo/Compose/CI visíveis;
- nenhum link quebrado ou `href="#"`;
- nenhuma rolagem horizontal e nenhum erro de console.

### CI no GitHub Actions

O job de contrato foi ajustado para iniciar a API, aguardar `GET /api/bebidas`,
executar o Newman e encerrar o Java na mesma etapa. O fluxo foi repetido com Git
Bash:

- 18 requisições e 32 assertions aprovadas, 0 falhas;
- porta 8080 liberada após o término;
- arquivo temporário de log removido.

Depois do push do commit `6e9a544`, o workflow foi executado no GitHub Actions e
concluiu com sucesso:

- Front-end: sucesso;
- Backend e cobertura: sucesso;
- Contrato Postman/Newman: sucesso.

Run: https://github.com/ArthurKayan/CafeNaMao/actions/runs/43422700883

### PostgreSQL

- `docker compose config` válido.
- Container PostgreSQL ficou `healthy`.
- Perfil `postgres` carregou 6 bebidas.
- Pedido `PED-7D812006` persistiu após reiniciar a API.
- `DELETE /api/pedidos` deixou o histórico remoto vazio.
- `docker compose down` executado; volume preservado.

### Verificações estáticas

```powershell
node --check js/api.js
node --check js/historico.js
node --check js/menu.js
node --check js/pedidos.js
node --check tests/api.test.js
git diff --check
npx --yes js-yaml .github/workflows/ci.yml
```

Resultado: sintaxe válida, YAML válido e `git diff --check` sem erros.

## 5. Tarefas pendentes

- Versionar um teste E2E estável, se isso for autorizado; a avaliação da Etapa 8
  concluiu que o roteiro temporário é suficiente para o escopo atual.
- Ampliar a matriz de navegadores e a verificação de acessibilidade.
- Revisar a configuração de CORS para um eventual ambiente publicado.
- Atualizar a documentação a cada nova etapa.
- Definir e autorizar a próxima etapa; não iniciar a Etapa 9 sem pedido explícito.

## 6. Arquivos modificados e criados

### Modificados

- `AGENTS.md`
- `README.md`
- `backend/pom.xml`
- `backend/src/main/resources/application-postgres.properties`
- `docs/plano-de-testes.md`
- `docs/requisitos.md`
- `docs/relatorio-testes.md`
- `js/historico.js`
- `js/menu.js`
- `js/pedidos.js`
- `menu.html`
- `pedidos.html`
- `sobre.html`
- `style.css`
- `progresso.md`

### Criados

- `.github/workflows/ci.yml`
- `backend/src/main/java/com/cafenamao/config/WebConfig.java`
- `backend/src/test/java/com/cafenamao/config/CorsIntegrationTest.java`
- `compose.yaml`
- `js/api.js`
- `tests/api.test.js`

## 7. Limitações conhecidas

- Não há testes de DOM/E2E versionados; o E2E foi temporário.
- A verificação de interface foi feita apenas no Edge.
- O CORS com `Origin: null` é adequado ao desenvolvimento local, não à produção.
- Fallback local e API não são sincronizados automaticamente.
- O H2 é em memória e perde dados ao reiniciar; PostgreSQL é a opção persistente.
- O pagamento é simulado; nenhum dado real de cartão é coletado.
- Não há autenticação, contas ou sincronização entre dispositivos.
- Não há integração física com a máquina de bebidas.

## 8. Próximo passo recomendado

A Etapa 8 está encerrada: integração, testes, cobertura, PostgreSQL, CI, E2E e
revisão visual foram concluídos e documentados, e o workflow passou no GitHub
Actions. O próximo passo é definir e autorizar o escopo da Etapa 9. Não fazer novos
commits ou pushes sem solicitação explícita.
