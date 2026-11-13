# AGENTS.md — Café na Mão

Aplicação acadêmica de pedidos de bebidas. O front-end é estático (HTML/CSS/JS puro,
sem build e sem servidor). O backend Java/Spring Boot expõe uma API REST em
`backend/`. Desde a Etapa 8, o front-end consuma a API quando ela está disponível e
mantém fallback local por `file://`, com carrinho e histórico em `localStorage`.

## Antes de trabalhar

- Leia `progresso.md` (fonte de verdade do estágio atual, decisões e próxima etapa) e `projeto 1/.agents/skills/cafenamao/SKILL.md`.
- Trabalhe em etapas pequenas; implemente **somente** a próxima etapa descrita em `progresso.md` e não avance sem autorização.
- Rode `git status` antes de editar e preserve alterações existentes.
- Não faça commit ou push sem solicitação explícita.
- Responda ao usuário em português brasileiro, explicando o código em termos simples.

## Comandos

### Front-end (raiz)

```powershell
npm test                              # 65 testes (node --test nativo)
node --test tests/api.test.js         # um arquivo de teste
node --check js/api.js                # checagem de sintaxe (faça para cada JS alterado)
```

### Backend (`backend/`)

```powershell
.\mvnw.cmd test                        # 31 testes JUnit 5/MockMvc
.\mvnw.cmd verify                      # testes + JAR + relatório/checagem JaCoCo
.\mvnw.cmd -DskipTests package        # gera target\cafe-na-mao-api-0.1.0.jar
.\mvnw.cmd spring-boot:run            # API em http://localhost:8080 (H2 em memória)
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=postgres
```

### PostgreSQL (`raiz`)

```powershell
docker compose up -d
docker compose ps
docker compose down
```

- O Compose usa a porta host `5433` por padrão (`DB_PORT`), pois `5432` pode estar ocupada por um PostgreSQL nativo. O backend também usa `5433` por padrão no perfil `postgres`; sobreponha com `DB_URL`, `DB_USER` e `DB_PASSWORD` se necessário.
- Não há lint/typecheck/formatter configurados. Use `node --check`, `npm test`, `.\mvnw.cmd verify` e o workflow em `.github/workflows/ci.yml`.
- Abra as páginas direto no navegador (`start index.html`, `start menu.html`). **Não** introduza servidor local para o front-end: os scripts são clássicos e precisam funcionar por `file://`.
- Não adicione dependências npm, bundlers ou frameworks sem pedir. Não existe `package-lock.json`.
- Não exija Maven global: use o wrapper (`mvnw`/`mvnw.cmd`).

## Arquitetura e convenções

### Front-end

- `js/dados.js` — catálogo (`bebidas`) + `formatarPreco`; expõe `window.CafeNaMaoDados` e também `module.exports` para os testes Node.
- `js/detalhes.js` — validação de quantidade e cálculo do total parcial.
- `js/carrinho.js` — regras do carrinho (adicionar, alterar, remover, total, pedido) e persistência em `localStorage`; expõe `window.CafeNaMaoCarrinho` e `module.exports`.
- `js/historico.js` — histórico de pedidos, pagamento simulado, status e persistência em `localStorage`; expõe `window.CafeNaMaoHistorico`, `normalizarPedido` e `module.exports`.
- `js/api.js` — cliente HTTP da API: URL base, timeout, `ErroApi`, normalização de bebidas/pedidos e operações de catálogo/pedidos. Usa `window.CAFE_NA_MAO_API_URL` ou `?api=` como override de `http://localhost:8080/api`.
- `js/menu.js` — renderização e estados do catálogo (`menu.html`); tenta a API, cai para `js/dados.js` e exibe a origem dos dados. Usa apenas scripts clássicos (IIFE, `"use strict"`), **não** módulos ES.
- `js/pedidos.js` — carrinho visual, pagamento simulado, confirmação, histórico e alteração de status em `pedidos.html`; usa API quando a sincronização inicial funciona e mantém modo local caso contrário.
- `menu.html` e `pedidos.html` carregam `js/api.js`; os badges `#origem-catalogo` e `#origem-historico` mostram `data-modo="api"` ou `"local"`.
- `tests/dados.test.js`, `tests/detalhes.test.js`, `tests/carrinho.test.js`, `tests/historico.test.js` e `tests/api.test.js` — testes com `node:test` + `node:assert/strict`.

### Backend

- `backend/src/main/java/com/cafenamao/bebida/` — entidade, repositório, serviço, DTOs e controller de bebidas (`/api/bebidas`).
- `backend/src/main/java/com/cafenamao/pedido/` — entidade, itens, enums de pagamento/status, repositório, serviço, DTOs e controller de pedidos (`/api/pedidos`).
- `backend/src/main/java/com/cafenamao/comum/` — exceções de domínio e `ApiExceptionHandler` com respostas de erro padronizadas.
- `backend/src/main/java/com/cafenamao/config/WebConfig.java` — CORS para `/api/**`, permitindo `Origin: null` de páginas abertas por `file://`.
- `backend/src/main/java/com/cafenamao/config/DadosIniciais.java` — carga inicial idempotente das seis bebidas.
- `backend/src/test/java/com/cafenamao/` — testes de integração com `@SpringBootTest`, `MockMvc` e `@Transactional`, incluindo `CorsIntegrationTest`.
- `backend/postman/` — coleção e ambiente Postman/Newman.
- O perfil padrão usa H2 em memória (`ddl-auto=create-drop`); `application-postgres.properties` usa PostgreSQL, com porta padrão `5433` e variáveis `DB_URL`, `DB_USER` e `DB_PASSWORD`.
- `backend/pom.xml` configura JaCoCo 0.8.12 com relatório e checagem mínima de 80% de linhas e 50% de branches.

### Infraestrutura e CI

- `compose.yaml` — PostgreSQL 16 Alpine, healthcheck, volume e porta host configurável.
- `.github/workflows/ci.yml` — jobs de front-end, backend/JaCoCo e contrato Newman. O workflow passou no GitHub Actions em 13/11/2026 (run 43422700883).
- `.github/workflows/ci.yml` publica o relatório JaCoCo e o JAR como artefatos.

### Regras gerais

- Preços são inteiros em **centavos**; arredondamentos/`float` são proibidos. No front-end, formate com `formatarPreco` (`Intl.NumberFormat("pt-BR")`).
- No backend, o servidor é a fonte de verdade do preço: o pedido recebe `bebidaId` e `quantidade`, não o preço do cliente.
- A integração não pode transformar falha da API em perda silenciosa: catálogo/histórico usam fallback local, mas a criação de pedido em modo API não deve cair para o fluxo local sem aviso.
- Insira dados de domínio no DOM com `textContent`/`createElement`, nunca interpolando em `innerHTML`.
- Nomes de domínio em português. HTML semântico, sem estilos inline, sem `href="#"`.
- Identidade visual neobrutalista: carvão `#252525`, creme `#F5F0E8`, laranja `#FF6A00`, cinza `#D8D3CA`; bordas grossas e sombras duras. Sem imagens/fontes externas nem arquivo de logo.
- A API expõe erros com `timestamp`, `status`, `erro`, `mensagem`, `caminho` e `campos`.
- Mantenha os contratos atuais estáveis e preserve o fallback por `file://`.

## Gotchas

- Estados do catálogo para teste manual: `menu.html?estado=vazio` e `menu.html?estado=erro`; valem só na primeira carga ("Tentar novamente" recarrega normal). Há atraso simulado de 450 ms.
- Para usar a API, inicie o backend antes e abra `menu.html`/`pedidos.html`; o badge de origem indica `API` ou `local`.
- Acessibilidade é requisito: foco visível, navegação por teclado, `aria-live` para estados, contraste e `prefers-reduced-motion`.
- Persistência do front-end: carrinho em `cafe-na-mao:carrinho:v1`; histórico em `cafe-na-mao:historico:v1`.
- O H2 é em memória: reiniciar o backend recria o catálogo e apaga pedidos. Use o perfil PostgreSQL + Compose para validar persistência.
- O endpoint `DELETE /api/pedidos` limpa todo o histórico; use apenas em testes/demonstração.
- Não há testes de DOM/E2E versionados; a integração foi verificada com script temporário via CDP no Edge. Não adicione dependências apenas para E2E sem autorização.
- A cobertura atual do backend é 93,59% de linhas e 62,50% de branches; os limites do build são 80% e 50%.
- Ao final de cada sessão, atualize `progresso.md` com resultados, pendências e a próxima etapa.
