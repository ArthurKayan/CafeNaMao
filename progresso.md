# PROGRESSO — Café na Mão

Última atualização: 05/11/2026

Status atual: **Etapas 1, 2, 3 e 4 concluídas. Próxima etapa: Etapa 5 — Pagamento simulado e histórico de pedidos (aguardando autorização).**

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

5. Implemente somente a próxima etapa descrita na seção 11.
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
├── pedidos.html               Carrinho, resumo e confirmação do pedido
├── sobre.html                 Vazio; será trabalhado em etapa futura
├── style.css                  Estilos globais e responsivos
├── js/
│   ├── dados.js               Dados das bebidas e formatação de preço
│   ├── detalhes.js            Validação de quantidade e cálculo do total
│   ├── carrinho.js            Regras do carrinho, total e persistência
│   ├── menu.js                Renderização, estados e painel de detalhes
│   └── pedidos.js             Carrinho na tela, remoção e confirmação
├── tests/
│   ├── dados.test.js          Testes unitários dos dados
│   ├── detalhes.test.js       Testes de quantidade e total
│   └── carrinho.test.js       Testes do carrinho e criação de pedido
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

## 7. Etapa 4 — Carrinho e criação de pedido

Status: concluída.

Implementado:

- Botão “Adicionar ao pedido” no painel de detalhes, com a quantidade validada de 1 a 10.
- `js/carrinho.js` como módulo clássico compartilhado pelo navegador e pelos testes Node.
- Carrinho com identificador, nome, preço unitário congelado no momento da adição, quantidade e total geral em centavos.
- Adições repetidas da mesma bebida somam a quantidade em uma única linha do carrinho.
- Alteração de quantidade e remoção de itens em `pedidos.html`, com atualização imediata do subtotal e do total geral.
- Persistência do carrinho em `localStorage` (chave `cafe-na-mao:carrinho:v1`), com fallback silencioso quando o armazenamento não estiver disponível.
- Barra “Seu pedido” no menu com quantidade de itens, total e link para `pedidos.html`.
- Estado de carrinho vazio com link “Escolher bebidas”.
- Bloqueio da conclusão quando o carrinho está vazio, tanto na interface quanto na função `criarPedido`.
- Confirmação simulada com número do pedido, data, itens, total e foco movido para a confirmação.
- Mensagens anunciadas por `aria-live`, rótulos associados aos campos e navegação por teclado preservada.
- Testes unitários em `tests/carrinho.test.js`.

Arquivos: `menu.html`, `pedidos.html`, `style.css`, `js/carrinho.js`, `js/menu.js`, `js/pedidos.js`, `tests/carrinho.test.js`.

## 8. Decisões técnicas registradas

1. HTML, CSS e JavaScript puro nesta fase para reduzir complexidade e facilitar a explicação na apresentação.
2. Scripts clássicos com padrão de proteção e namespace global (`window.CafeNaMaoDados`) em vez de módulos ES. Isso permite abrir o projeto por `file://` sem servidor local.
3. O painel de detalhes usa o elemento nativo `<dialog>`, com foco inicial no campo de quantidade e retorno do foco ao botão de origem ao fechar.
4. Preços são inteiros em centavos. A formatação usa `Intl.NumberFormat("pt-BR")` e a validação rejeita valores negativos, fracionários ou não numéricos.
5. O carregamento tem atraso simulado de 450 ms para permitir observar o estado de carregamento sem depender de rede.
6. O catálogo em si não usa `localStorage`; a partir da Etapa 4 o carrinho é salvo em `localStorage` para atravessar as páginas.
7. Os cards são renderizados com criação de elementos DOM e `textContent`, não com interpolação direta de dados em `innerHTML`.
8. O cabeçalho é fixo apenas acima de 860 px. Em telas menores ele fica estático para não consumir grande parte da tela.
9. Testes usam o executor nativo do Node.js, sem dependências externas e sem `package-lock.json`.
10. Não há pagamento real. O rodapé e o texto do catálogo informam que o pagamento é simulado.
11. A quantidade fica limitada de 1 a 10; validação e cálculo do total estão em `js/detalhes.js`, isolados para permitir testes no Node.
12. O total parcial da Etapa 3 não cria carrinho: apenas multiplica o preço unitário pela quantidade, sempre em centavos inteiros.
13. O carrinho guarda uma cópia do nome e do preço unitário no momento da adição, para que o resumo não dependa de mudanças futuras no catálogo.
14. A quantidade máxima de 10 vale por bebida no carrinho. Tentar ultrapassar esse limite somando adições repetidas exibe a mensagem de validação e não altera o item.
15. O carrinho é salvo em `localStorage` para sobreviver à navegação entre `menu.html` e `pedidos.html`; se o navegador bloquear o armazenamento, a página continua funcionando durante a sessão.
16. A conclusão do pedido cria um objeto com número, data, cópia dos itens e total, limpa o carrinho salvo e exibe a confirmação na mesma página. Não há pagamento real nem envio ao backend.

## 9. Verificações executadas

### Testes automatizados

Comando:

```powershell
npm.cmd test
```

O `npm.ps1` está bloqueado pela política de execução deste ambiente, por isso foi usada a versão `.cmd`; o script executado é o mesmo (`node --test`).

Resultado: **34 testes aprovados, 0 reprovados, 0 ignorados**.

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
- rejeição de quantidade e preço inválidos no cálculo do total;
- total do carrinho vazio igual a zero;
- total do carrinho com bebidas diferentes;
- soma de itens repetidos em uma única linha;
- alteração de quantidade e remoção de itens;
- bloqueio de pedido com carrinho vazio;
- criação de pedido com número, data, cópia dos itens e total;
- rejeição de quantidade inválida e de soma acima do limite;
- persistência do carrinho em armazenamento simulado.

Também foram executados:

```powershell
node --check js/dados.js
node --check js/detalhes.js
node --check js/carrinho.js
node --check js/menu.js
node --check js/pedidos.js
node --check tests/dados.test.js
node --check tests/detalhes.test.js
node --check tests/carrinho.test.js
```

Resultado: sem erros de sintaxe.

Foi executada também uma verificação estática complementar:

- todos os IDs usados por `getElementById` em `js/menu.js` e `js/pedidos.js` existem nas respectivas páginas;
- não há IDs duplicados em `index.html`, `menu.html`, `pedidos.html` ou `sobre.html`;
- não há `href="#"` nem estilos inline;
- todos os links internos apontam para arquivos existentes.

### Verificação no navegador

Executada com Microsoft Edge 156 em modo headless, controlado por CDP, com viewports de 390, 768 e 1366 px. O script auxiliar foi temporário e não foi adicionado ao repositório.

| Cenário | Resultado |
|---|---|
| `menu.html` em 390/768/1366 px | Sem rolagem horizontal |
| Detalhes com quantidade 3 | Total parcial `R$ 12,00`; foco inicial no campo |
| Adicionar ao pedido | Painel fecha e resumo mostra `3 itens · R$ 12,00` |
| Duas bebidas no carrinho | `5 itens · R$ 23,00` |
| Adicionar a mesma bebida novamente | Quantidade somada: `6 itens · R$ 27,00` e 2 linhas no armazenamento |
| `pedidos.html` em 390/768/1366 px | Sem rolagem horizontal; total inicial `R$ 27,00` |
| Alterar quantidade para 5 | Subtotal `R$ 20,00` e total `R$ 31,00` |
| Quantidade 11 | Mensagem “A quantidade máxima é 10.” e total inalterado |
| Remover item | Total recalculado para `R$ 20,00` e uma linha restante |
| Concluir pedido | Confirmação visível, número `PED-…`, total `R$ 20,00` e foco na confirmação |
| Após concluir | Carrinho salvo limpo; recarregar mostra “Seu carrinho está vazio.” |
| Teclado | `Esc` fecha o painel e devolve o foco ao botão de origem |
| Console | Nenhum erro de JavaScript durante o fluxo |
| Confirmação em 390/1366 px | Sem rolagem horizontal |

## 10. Pendências e limitações conhecidas

- A verificação automatizada foi feita no Edge headless; uma conferência visual rápida no navegador real ainda é recomendada antes da apresentação.
- `sobre.html` continua vazio.
- Não há histórico de pedidos nem pagamento simulado; o pedido é apenas confirmado e o carrinho é limpo.
- O carrinho depende de `localStorage` para atravessar páginas; em navegadores que bloqueiam armazenamento para `file://`, a navegação entre menu e pedidos pode perder o carrinho.
- Não há backend Java, API REST, banco de dados ou Docker Compose.
- Não há plano de teste formal com 15 casos, coleção Postman/Newman, JUnit 5, JaCoCo ou GitHub Actions. Esses itens pertencem à Etapa 9.
- Não há testes automatizados de DOM no repositório; a verificação de interface foi feita manualmente com apoio de scripts temporários no Edge.
- O `README.md` ainda é resumido e deverá ser atualizado na etapa de documentação.
- Não existe arquivo de logo; a marca é textual.
- Nenhum dado real de cartão pode ser coletado ou armazenado.

## 11. Próxima etapa — Etapa 5: pagamento simulado e histórico de pedidos

Status: aguardando autorização do usuário. Não iniciar sem pedido explícito.

Objetivo sugerido: simular a etapa de pagamento e permitir consultar os pedidos já confirmados, sem coletar dados reais de cartão e sem backend.

Tarefas previstas (a confirmar):

1. Definir um fluxo simples de pagamento simulado (por exemplo, escolha entre “dinheiro” e “cartão fictício”).
2. Salvar o pedido confirmado em `localStorage` como histórico, com número, data, itens e total.
3. Exibir o histórico em `pedidos.html` ou em uma seção própria, com estado vazio.
4. Manter preços em centavos, reutilizando `formatarPreco`.
5. Garantir acessibilidade, `aria-live` e responsividade sem rolagem horizontal.
6. Criar testes unitários para o armazenamento e a leitura do histórico.
7. Fazer a verificação completa no navegador antes de considerar a etapa concluída.

Critérios de conclusão sugeridos:

- pedido confirmado continua visível após recarregar a página;
- histórico vazio exibe mensagem clara;
- nenhum dado real de cartão é solicitado, exibido ou armazenado;
- testes automatizados e verificação no navegador são registrados neste arquivo.

## 12. Checklist para a próxima sessão

- [ ] Ler a skill e este arquivo.
- [ ] Rodar `npm test` antes de começar.
- [ ] Confirmar com o usuário o escopo da Etapa 5.
- [ ] Implementar somente a etapa autorizada.
- [ ] Executar testes unitários e verificação no navegador.
- [ ] Revisar o diff e procurar regressões nas Etapas 1 a 4.
- [ ] Atualizar `progresso.md` com resultados e nova próxima etapa.
- [ ] Não fazer commit ou push sem solicitação explícita do usuário.
