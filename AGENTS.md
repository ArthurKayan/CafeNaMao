# AGENTS.md — Café na Mão

Aplicação acadêmica de pedidos de bebidas. Front-end estático (HTML/CSS/JS puro), sem build, sem dependências e sem servidor. Backend Java/Spring, banco e CI ainda **não existem** — não os assuma.

## Antes de trabalhar

- Leia `progresso.md` (fonte de verdade do estágio atual, decisões e próxima etapa) e `projeto 1/.agents/skills/cafenamao/SKILL.md`.
- Trabalhe em etapas pequenas; implemente **somente** a próxima etapa descrita em `progresso.md` e não avance sem autorização.
- Rode `git status` antes de editar e preserve alterações existentes.
- Não faça commit ou push sem solicitação explícita.
- Responda ao usuário em português brasileiro, explicando o código em termos simples.

## Comandos

```powershell
npm test                              # todos os testes (node --test nativo)
node --test tests/dados.test.js       # um arquivo de teste
node --test --test-name-pattern="preço"   # testes por nome
node --check js/menu.js               # checagem de sintaxe (faça para cada JS alterado)
git diff --check                      # whitespace
```

- Não há lint/typecheck/formatter configurados. Use `node --check` + `npm test`.
- Abra as páginas direto no navegador (`start index.html`, `start menu.html`). **Não** introduza servidor local: os scripts são clássicos e precisam funcionar por `file://`.
- Não adicione dependências npm, bundlers ou frameworks sem pedir. Não existe `package-lock.json`.

## Arquitetura e convenções

- `js/dados.js` — catálogo (`bebidas`) + `formatarPreco`; expõe `window.CafeNaMaoDados` e também `module.exports` para os testes Node.
- `js/detalhes.js` — validação de quantidade e cálculo do total parcial.
- `js/carrinho.js` — regras do carrinho (adicionar, alterar, remover, total, pedido) e persistência em `localStorage`; expõe `window.CafeNaMaoCarrinho` e `module.exports`.
- `js/historico.js` — histórico de pedidos, pagamento simulado, status e persistência em `localStorage`; expõe `window.CafeNaMaoHistorico` e `module.exports`.
- `js/menu.js` — renderização e estados do catálogo (`menu.html`); usa apenas scripts clássicos (IIFE, `"use strict"`), **não** módulos ES.
- `js/pedidos.js` — carrinho visual, pagamento simulado, confirmação, histórico e alteração de status em `pedidos.html`.
- `tests/dados.test.js`, `tests/detalhes.test.js`, `tests/carrinho.test.js` e `tests/historico.test.js` — testes com `node:test` + `node:assert/strict`.
- Páginas: `index.html`, `menu.html` e `pedidos.html` prontas para as etapas concluídas; `sobre.html` **vazia de propósito** (etapa futura; não preencha antes da hora).
- Preços são inteiros em **centavos**; arredondamentos/`float` são proibidos. Formate com `formatarPreco` (`Intl.NumberFormat("pt-BR")`).
- Insira dados de domínio no DOM com `textContent`/`createElement`, nunca interpolando em `innerHTML`.
- Nomes de domínio em português. HTML semântico, sem estilos inline, sem `href="#"`.
- Identidade visual neobrutalista: carvão `#252525`, creme `#F5F0E8`, laranja `#FF6A00`, cinza `#D8D3CA`; bordas grossas e sombras duras. Sem imagens/fontes externas nem arquivo de logo.

## Gotchas

- Estados do catálogo para teste manual: `menu.html?estado=vazio` e `menu.html?estado=erro`; valem só na primeira carga ("Tentar novamente" recarrega normal). Há atraso simulado de 450 ms.
- Acessibilidade é requisito: foco visível, navegação por teclado, `aria-live` para estados, contraste e `prefers-reduced-motion`.
- Persistência local: carrinho em `cafe-na-mao:carrinho:v1`; histórico em `cafe-na-mao:historico:v1` no mesmo esquema.
- Não há testes de DOM automatizados; verificação de UI é manual no navegador.
- Ao final de cada sessão, atualize `progresso.md` com resultados, pendências e a próxima etapa.
