# AGENTS.md — Café na Mão

Aplicação acadêmica de pedidos de bebidas. O front-end é estático (HTML/CSS/JS puro,
sem build e sem servidor). O backend Java/Spring Boot existe desde a Etapa 7 e expõe
uma API REST em `backend/`. O front-end ainda **não consome** essa API: carrinho e
histórico continuam no `localStorage` até uma etapa de integração autorizada.

## Antes de trabalhar

- Leia `progresso.md` (fonte de verdade do estágio atual, decisões e próxima etapa) e `projeto 1/.agents/skills/cafenamao/SKILL.md`.
- Trabalhe em etapas pequenas; implemente **somente** a próxima etapa descrita em `progresso.md` e não avance sem autorização.
- Rode `git status` antes de editar (quando o repositório Git estiver presente) e preserve alterações existentes.
- Não faça commit ou push sem solicitação explícita.
- Responda ao usuário em português brasileiro, explicando o código em termos simples.

## Comandos

### Front-end (raiz)

```powershell
npm test                              # 53 testes (node --test nativo)
node --test tests/dados.test.js       # um arquivo de teste
node --check js/menu.js               # checagem de sintaxe (faça para cada JS alterado)
```

### Backend (`backend/`)

```powershell
.\mvnw.cmd test                        # 29 testes JUnit 5/MockMvc
.\mvnw.cmd -DskipTests package        # gera target\cafe-na-mao-api-0.1.0.jar
.\mvnw.cmd spring-boot:run            # API em http://localhost:8080 (H2 em memória)
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=postgres
```

- Não há lint/typecheck/formatter configurados. Use `node --check`, `npm test` e `.\mvnw.cmd test`.
- Abra as páginas direto no navegador (`start index.html`, `start menu.html`). **Não** introduza servidor local para o front-end: os scripts são clássicos e precisam funcionar por `file://`.
- Não adicione dependências npm, bundlers ou frameworks sem pedir. Não existe `package-lock.json`.
- Não exija Maven global: use o wrapper (`mvnw`/`mvnw.cmd`).

## Arquitetura e convenções

### Front-end

- `js/dados.js` — catálogo (`bebidas`) + `formatarPreco`; expõe `window.CafeNaMaoDados` e também `module.exports` para os testes Node.
- `js/detalhes.js` — validação de quantidade e cálculo do total parcial.
- `js/carrinho.js` — regras do carrinho (adicionar, alterar, remover, total, pedido) e persistência em `localStorage`; expõe `window.CafeNaMaoCarrinho` e `module.exports`.
- `js/historico.js` — histórico de pedidos, pagamento simulado, status e persistência em `localStorage`; expõe `window.CafeNaMaoHistorico` e `module.exports`.
- `js/detalhes.js` — validação de quantidade e cálculo do total parcial.
- `js/carrinho.js` — regras do carrinho (adicionar, alterar, remover, total, pedido) e persistência em `localStorage`; expõe `window.CafeNaMaoCarrinho` e `module.exports`.
- `js/historico.js` — histórico de pedidos, pagamento simulado, status e persistência em `localStorage`; expõe `window.CafeNaMaoHistorico` e `module.exports`.
- `js/menu.js` — renderização e estados do catálogo (`menu.html`); usa apenas scripts clássicos (IIFE, `"use strict"`), **não** módulos ES.
- `js/pedidos.js` — carrinho visual, pagamento simulado, confirmação, histórico e alteração de status em `pedidos.html`.
- `tests/dados.test.js`, `tests/detalhes.test.js`, `tests/carrinho.test.js` e `tests/historico.test.js` — testes com `node:test` + `node:assert/strict`.

### Backend

- `backend/src/main/java/com/cafenamao/bebida/` — entidade, repositório, serviço, DTOs e controller de bebidas (`/api/bebidas`).
- `backend/src/main/java/com/cafenamao/pedido/` — entidade, itens, enums de pagamento/status, repositório, serviço, DTOs e controller de pedidos (`/api/pedidos`).
- `backend/src/main/java/com/cafenamao/comum/` — exceções de domínio e `ApiExceptionHandler` com respostas de erro padronizadas.
- `backend/src/main/java/com/cafenamao/config/DadosIniciais.java` — carga inicial idempotente das seis bebidas.
- `backend/src/test/java/com/cafenamao/` — testes de integração com `@SpringBootTest`, `MockMvc` e `@Transactional`.
- `backend/postman/` — coleção e ambiente Postman/Newman.
- O perfil padrão usa H2 em memória (`ddl-auto=create-drop`); `application-postgres.properties` usa variáveis `DB_URL`, `DB_USER` e `DB_PASSWORD`.

### Regras gerais

- Preços são inteiros em **centavos**; arredondamentos/`float` são proibidos. No front-end, formate com `formatarPreco` (`Intl.NumberFormat("pt-BR")`).
- No backend, o servidor é a fonte de verdade do preço: o pedido recebe `bebidaId` e `quantidade`, não o preço do cliente.
- Insira dados de domínio no DOM com `textContent`/`createElement`, nunca interpolando em `innerHTML`.
- Nomes de domínio em português. HTML semântico, sem estilos inline, sem `href="#"`.
- Identidade visual neobrutalista: carvão `#252525`, creme `#F5F0E8`, laranja `#FF6A00`, cinza `#D8D3CA`; bordas grossas e sombras duras. Sem imagens/fontes externas nem arquivo de logo.
- A API expõe erros com `timestamp`, `status`, `erro`, `mensagem`, `caminho` e `campos`.
- Mantenha os contratos atuais estáveis; a integração do front-end com a API é uma etapa futura.

## Gotchas

- Estados do catálogo para teste manual: `menu.html?estado=vazio` e `menu.html?estado=erro`; valem só na primeira carga ("Tentar novamente" recarrega normal). Há atraso simulado de 450 ms.
- Acessibilidade é requisito: foco visível, navegação por teclado, `aria-live` para estados, contraste e `prefers-reduced-motion`.
- Persistência do front-end: carrinho em `cafe-na-mao:carrinho:v1`; histórico em `cafe-na-mao:historico:v1`.
- O H2 é em memória: reiniciar o backend recria o catálogo e apaga pedidos. Não trate isso como persistência definitiva.
- O endpoint `DELETE /api/pedidos` limpa todo o histórico; use apenas em testes/demonstração.
- Não há testes automatizados de DOM no front-end; verificação de UI é manual no navegador.
- Ao final de cada sessão, atualize `progresso.md` com resultados, pendências e a próxima etapa.
