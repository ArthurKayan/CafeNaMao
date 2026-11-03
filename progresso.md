# PROGRESSO — Café na Mão

Última atualização: 03/11/2026

Status atual: **Etapas 1, 2 e 3 concluídas. Próxima etapa: Etapa 4 — Carrinho e criação de pedido.**

Este arquivo registra o estado real do projeto, as decisões tomadas, os testes executados e o próximo passo. Uma nova sessão deve conseguir continuar a partir dele, lendo também `projeto 1/.agents/skills/cafenamao/SKILL.md`.

## Histórico do registro

O registro anterior (commit `c359b67`) foi preservado e ampliado. Ele documentava:

- primeira etapa de desenvolvimento realizada com auxílio do OpenCode;
- front-end inicial com `index.html`, `menu.html`, `pedidos.html`, `sobre.html` e `style.css`;
- projeto versionado no GitHub;
- ferramentas usadas: VS Code, OpenCode, DeepSeek v4.1 Flash, HTML, CSS, JavaScript, Git e GitHub;
- orientação para trabalhar em etapas, explicar em português, testar e não avançar sem autorização.

Essas orientações continuam válidas. O estado detalhado das Etapas 1, 2 e 3 está nas seções seguintes.

## 1. Como retomar o projeto

1. Leia `progresso.md` e a skill `cafe-na-mao`.
2. Inspecione `git status` antes de alterar qualquer arquivo.
3. Execute os testes existentes:

```powershell
npm test
```

4. Abra as páginas diretamente no navegador:

```powershell
start index.html
start menu.html
```

5. Implemente somente a próxima etapa descrita na seção 10.
6. Ao final, atualize este arquivo com resultados, pendências e a nova próxima etapa.

Não é necessário servidor local nesta fase. Os scripts usam arquivos JavaScript clássicos, portanto funcionam também por `file://`.

## 2. Visão do projeto

O Café na Mão é uma aplicação acadêmica de pedidos de bebidas para uma máquina de café. O cliente deve consultar bebidas, escolher quantidade, montar um pedido e simular o pagamento. O projeto também precisa demonstrar práticas de Garantia de Qualidade de Software.

Stack atual:

- Frontend estático com HTML semântico, CSS e JavaScript puro.
- Testes unitários com o executor nativo do Node.js.
- Sem framework, sem build e sem dependências externas nesta fase.
- Backend Java/Spring Boot, PostgreSQL, Postman/Newman, JaCoCo e CI ainda não implementados.

Identidade visual:

- Neobrutalismo com bordas grossas, sombras duras e blocos de alto contraste.
- Carvão `#252525`, creme `#F5F0E8`, laranja `#FF6A00` e cinza `#D8D3CA`.
- Marca textual “Café na Mão”. Não existe arquivo de logo no repositório.
- Sem imagens ou fontes externas; a ilustração da xícara é feita com CSS.

## 3. Estrutura atual

```text
CaféNaMão/
├── index.html                 Página inicial
├── menu.html                  Catálogo de bebidas e painel de detalhes
├── pedidos.html               Vazio; será trabalhado na Etapa 4
├── sobre.html                 Vazio; será trabalhado na Etapa 6
├── style.css                  Estilos globais e responsivos
├── js/
│   ├── dados.js               Dados das bebidas e formatação de preço
│   ├── detalhes.js            Validação de quantidade e cálculo do total
│   └── menu.js                Renderização, estados e painel de detalhes
├── tests/
│   ├── dados.test.js          Testes unitários dos dados
│   └── detalhes.test.js       Testes de quantidade e total
├── package.json               Script de testes; sem dependências
├── README.md                  Descrição inicial, ainda resumida
├── progresso.md               Este registro
└── projeto 1/                 Skills e configurações de agentes
```

## 4. Etapa 1 — Fundação visual e página inicial

Status: concluída.

Implementado:

- `index.html` com `lang="pt-BR"`, viewport mobile, título e descrição.
- Estrutura semântica com `header`, `nav`, `main`, `section` e `footer`.
- Link “Pular para o conteúdo” e navegação com `aria-current="page"`.
- Página inicial com hero, chamada para o menu, seção “Como funciona” e rodapé.
- Ilustração de xícara feita somente com CSS.
- Cabeçalho fixo no desktop e estático em telas menores.
- Foco de teclado visível, áreas de toque com pelo menos 44 px e suporte a `prefers-reduced-motion`.

Arquivos: `index.html`, `style.css`.

## 5. Etapa 2 — Catálogo de bebidas

Status: concluída.

Implementado:

- `menu.html` com cabeçalho, navegação ativa, catálogo e rodapé.
- Seis bebidas simuladas em `js/dados.js`.
- Preços armazenados em centavos e formatados como moeda brasileira.
- Cards com número, categoria, nome, descrição e preço.
- Estado de carregamento com três esqueletos e `aria-busy="true"`.
- Estado de lista vazia com mensagem e link para a página inicial.
- Estado de erro com botão “Tentar novamente”.
- Região `aria-live` para anunciar mudanças de estado.
- Layout responsivo: 1 coluna no celular, 2 no tablet e 3 no desktop.
- Textos vindos dos dados são inseridos com `textContent`, evitando interpretação de HTML.

Bebidas atuais:

| Bebida | Categoria | Preço |
|---|---|---:|
| Espresso Curto | Café | R$ 4,00 |
| Café com Leite | Café | R$ 5,50 |
| Cappuccino | Café | R$ 7,50 |
| Mocha | Café | R$ 8,50 |
| Chocolate Quente | Chocolate | R$ 7,00 |
| Chá de Canela | Chá | R$ 5,00 |

Arquivos: `menu.html`, `js/dados.js`, `js/menu.js`, `style.css`, `tests/dados.test.js`, `package.json`.

### Como testar os estados do catálogo

- Catálogo normal: abrir `menu.html`.
- Lista vazia: abrir `menu.html?estado=vazio`.
- Erro: abrir `menu.html?estado=erro` e clicar em “Tentar novamente”.

Os parâmetros de estado são apenas um recurso de demonstração/teste. Eles valem para a primeira carga; o botão de nova tentativa carrega os dados normais.

## 6. Etapa 3 — Detalhes e quantidade

Status: concluída.

Implementado:

- Botão “Ver detalhes” em cada card, com `aria-haspopup="dialog"` e `aria-controls`.
- Painel modal acessível usando o elemento nativo `<dialog>` em `menu.html`.
- Detalhes com categoria, nome, descrição, preço unitário e seletor de quantidade.
- Quantidade limitada de 1 a 10, com mensagem de erro para valores abaixo do mínimo, acima do máximo, fracionários ou não numéricos.
- Total parcial calculado com inteiros em centavos e exibido com `formatarPreco`.
- Fechamento pelo botão “Fechar” e pela tecla `Esc`, devolvendo o foco ao botão que abriu o painel.
- Painel responsivo e sem rolagem horizontal em 390 px, 768 px e 1366 px.
- Testes unitários em `tests/detalhes.test.js`.

Arquivos: `menu.html`, `js/detalhes.js`, `js/menu.js`, `style.css`, `tests/detalhes.test.js`.

## 7. Decisões técnicas registradas

1. HTML, CSS e JavaScript puro nesta fase para reduzir complexidade e facilitar a explicação na apresentação.
2. Scripts clássicos com padrão de proteção e namespace global (`window.CafeNaMaoDados`) em vez de módulos ES. Isso permite abrir o projeto por `file://` sem servidor local.
3. O painel de detalhes usa o elemento nativo `<dialog>`, com foco inicial no campo de quantidade e retorno do foco ao botão de origem ao fechar.
4. Preços são inteiros em centavos. A formatação usa `Intl.NumberFormat("pt-BR")` e a validação rejeita valores negativos, fracionários ou não numéricos.
5. O carregamento tem atraso simulado de 450 ms para permitir observar o estado de carregamento sem depender de rede.
6. O catálogo não usa `localStorage`; carrinho e pedidos serão tratados na Etapa 4.
7. Os cards são renderizados com criação de elementos DOM e `textContent`, não com interpolação direta de dados em `innerHTML`.
8. O cabeçalho é fixo apenas acima de 860 px. Em telas menores ele fica estático para não consumir grande parte da tela.
9. Testes usam o executor nativo do Node.js, sem dependências externas e sem `package-lock.json`.
10. Não há pagamento real. O rodapé e o texto do catálogo informam que o pagamento é simulado.
11. A quantidade fica limitada de 1 a 10; validação e cálculo do total estão em `js/detalhes.js`, isolados para permitir testes no Node.
12. O total parcial da Etapa 3 não cria carrinho: apenas multiplica o preço unitário pela quantidade, sempre em centavos inteiros.

## 8. Verificações executadas

### Testes automatizados

Comando:

```powershell
npm test
```

Resultado: **20 testes aprovados, 0 reprovados, 0 ignorados**.

Cobertura atual dos testes:

- quantidade mínima de bebidas;
- unicidade dos identificadores;
- presença de dados obrigatórios;
- preços inteiros e positivos;
- formatação de moeda;
- limite inferior de preço zero;
- separador de milhar;
- rejeição de preço negativo, fracionário e não numérico;
- quantidade válida dentro dos limites de 1 a 10;
- rejeição de quantidade abaixo do mínimo, acima do máximo, fracionária ou não numérica;
- cálculo do total em centavos, inclusive nos limites mínimo e máximo;
- rejeição de quantidade e preço inválidos no cálculo do total.

Também foram executados:

```powershell
node --check js/dados.js
node --check js/detalhes.js
node --check js/menu.js
node --check tests/dados.test.js
node --check tests/detalhes.test.js
git diff --check
```

Resultado: sem erros de sintaxe ou de whitespace. O Git apenas informou avisos de conversão LF/CRLF, que não são falhas.

### Verificação no navegador

Feita com Microsoft Edge em modo headless e emulação de viewport. Essas verificações foram executadas na sessão atual, mas os scripts auxiliares não foram adicionados ao repositório.

| Cenário | Resultado |
|---|---|
| `index.html` em 390 px | Sem rolagem horizontal; cabeçalho estático |
| `index.html` em 1366 px | Sem rolagem horizontal; cabeçalho fixo |
| `menu.html` em 390 px | 6 cards, primeiro preço `R$ 4,00`, sem rolagem horizontal |
| `menu.html` em 768 px | 6 cards, grade com 2 colunas, sem rolagem horizontal |
| `menu.html` em 1366 px | 6 cards, grade com 3 colunas, sem rolagem horizontal |
| Carregamento | 3 esqueletos e `aria-busy="true"`; depois 6 cards e `aria-busy="false"` |
| Lista vazia | 0 cards, mensagem visível e link “Voltar ao início” |
| Erro | 0 cards, mensagem visível e botão “Tentar novamente” |
| Nova tentativa | Após o clique, 6 cards renderizados e painel de erro oculto |
| Detalhes | Painel aberto com nome, categoria, preço unitário e quantidade 1 |
| Quantidade 3 | Total parcial `R$ 12,00` para o Espresso Curto |
| Quantidade 0 | Total `—`, mensagem de mínimo e `aria-invalid="true"` |
| Quantidade 11 | Mensagem de máximo exibida |
| Fechar | Painel fechado e foco devolvido ao botão “Ver detalhes” |
| `menu.html` com painel em 390/768/1366 px | Sem rolagem horizontal; grade com 1, 2 e 3 colunas |

Também foram conferidos:

- todos os links internos apontam para arquivos existentes;
- não há `href="#"`;
- não há estilos inline;
- não há IDs duplicados dentro de uma mesma página.

## 9. Pendências e limitações conhecidas

- O painel de detalhes não cria pedido nem adiciona itens a um carrinho; isso é a Etapa 4.
- `pedidos.html` e `sobre.html` continuam vazios.
- Não há carrinho, criação de pedido, histórico ou pagamento simulado.
- Não há backend Java, API REST, banco de dados ou Docker Compose.
- Não há plano de teste formal com 15 casos, coleção Postman/Newman, JUnit 5, JaCoCo ou GitHub Actions. Esses itens pertencem à Etapa 9.
- Não há testes automatizados de DOM no repositório; a verificação de interface foi feita manualmente com apoio de scripts temporários no Edge.
- O `README.md` ainda é resumido e deverá ser atualizado na etapa de documentação.
- Não existe arquivo de logo; a marca é textual.
- Nenhum dado real de cartão pode ser coletado ou armazenado.

## 10. Próxima etapa — Etapa 4: carrinho e criação de pedido

Objetivo: transformar a escolha de quantidade em um carrinho e permitir concluir um pedido simulado. Não implementar pagamento real nem backend nesta etapa.

Tarefas previstas:

1. Adicionar ao painel de detalhes uma ação real de adicionar a bebida com a quantidade escolhida.
2. Manter um carrinho com itens, quantidades, preços unitários e total geral em centavos.
3. Permitir alterar quantidade e remover itens do carrinho.
4. Impedir conclusão de pedido com carrinho vazio, exibindo mensagem clara.
5. Preencher `pedidos.html` com o resumo do pedido e uma confirmação simulada.
6. Reaproveitar `formatarPreco` e a validação de quantidade; não duplicar regras.
7. Manter acessibilidade (foco, teclado, `aria-live`) e responsividade sem rolagem horizontal.
8. Criar testes unitários para o total do carrinho, itens repetidos e carrinho vazio.
9. Verificar o fluxo completo no navegador e atualizar este arquivo.

Arquivos prováveis: `menu.html`, `pedidos.html`, `style.css`, `js/menu.js`, novo módulo de carrinho, `tests/` e `progresso.md`.

Critérios de conclusão:

- adicionar a mesma bebida mais de uma vez soma corretamente as quantidades ou itens;
- total geral é a soma dos preços unitários multiplicados pelas quantidades;
- remover e alterar itens atualiza o total sem erros;
- carrinho vazio bloqueia a conclusão do pedido;
- interface funciona por teclado e sem rolagem horizontal em celular e desktop;
- testes automatizados e verificação no navegador são registrados neste arquivo.

## 11. Checklist para a próxima sessão

- [ ] Ler a skill e este arquivo.
- [ ] Rodar `git status` e preservar alterações existentes.
- [ ] Executar `npm test` antes de começar.
- [ ] Implementar somente a Etapa 4.
- [ ] Executar testes unitários e verificação no navegador.
- [ ] Revisar o diff e procurar regressões nas Etapas 1, 2 e 3.
- [ ] Atualizar `progresso.md` com resultados e nova próxima etapa.
- [ ] Não fazer commit ou push sem solicitação explícita do usuário.
