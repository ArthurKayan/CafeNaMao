# PROGRESSO — Café na Mão

Última atualização: 10/11/2026
Última atualização: 10/11/2026

Status atual: **Etapas 1 a 7 concluídas. Próxima etapa: Etapa 8 — integração do front-end com a API, cobertura e infraestrutura (aguardando autorização).**

Este arquivo registra o estado real do projeto, as decisões tomadas, os testes executados e o próximo passo. Uma nova sessão deve conseguir continuar a partir dele, lendo também `projeto 1/.agents/skills/cafenamao/SKILL.md`.

## Histórico do registro

O registro anterior (commit `c359b67`) foi preservado e ampliado. Ele documentava:

- primeira etapa de desenvolvimento realizada com auxílio do OpenCode;
- front-end inicial com `index.html`, `menu.html`, `pedidos.html`, `sobre.html` e `style.css`;
- projeto versionado no GitHub;
- ferramentas usadas: VS Code, OpenCode, DeepSeek v4.1 Flash, HTML, CSS, JavaScript, Git e GitHub;
- orientação para trabalhar em etapas, explicar em português, testar e não avançar sem autorização.

Essas orientações continuam válidas. O estado detalhado das Etapas 1 a 6 está nas seções seguintes.
Essas orientações continuam válidas. O estado detalhado das Etapas 1 a 6 está nas seções seguintes.

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

5. Implemente somente a próxima etapa descrita na seção 14.
6. Ao final, atualize este arquivo com resultados, pendências e a nova próxima etapa.

Não é necessário servidor local nesta fase. Os scripts usam arquivos JavaScript clássicos, portanto funcionam também por `file://`.

## 2. Visão do projeto

O Café na Mão é uma aplicação acadêmica de pedidos de bebidas para uma máquina de café. O cliente deve consultar bebidas, escolher quantidade, montar um pedido e simular o pagamento. O projeto também precisa demonstrar práticas de Garantia de Qualidade de Software.

Stack atual:

- Frontend estático com HTML semântico, CSS e JavaScript puro.
- Testes unitários do front-end com o executor nativo do Node.js.
- Backend Java 22 com Spring Boot 3.4.5 e Maven Wrapper 3.9.9.
- API REST de bebidas e pedidos com H2 em memória no perfil padrão.
- Testes de integração com JUnit 5/MockMvc e contrato Postman/Newman.
- PostgreSQL, JaCoCo, Docker Compose e CI ainda não implementados.

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
├── pedidos.html               Carrinho, pagamento, confirmação e histórico
├── sobre.html                 Página Sobre o projeto
├── pedidos.html               Carrinho, pagamento, confirmação e histórico
├── sobre.html                 Página Sobre o projeto
├── style.css                  Estilos globais e responsivos
├── js/
│   ├── dados.js               Dados das bebidas e formatação de preço
│   ├── detalhes.js            Validação de quantidade e cálculo do total
│   ├── carrinho.js            Regras do carrinho, total e persistência
│   ├── historico.js           Histórico, pagamento simulado e status
│   ├── menu.js                Renderização, estados e painel de detalhes
│   └── pedidos.js             Carrinho na tela, pagamento e histórico
│   ├── carrinho.js            Regras do carrinho, total e persistência
│   ├── historico.js           Histórico, pagamento simulado e status
│   ├── menu.js                Renderização, estados e painel de detalhes
│   └── pedidos.js             Carrinho na tela, pagamento e histórico
├── tests/
│   ├── dados.test.js          Testes unitários dos dados
│   ├── detalhes.test.js       Testes de quantidade e total
│   ├── carrinho.test.js       Testes do carrinho e criação de pedido
│   └── historico.test.js      Testes do histórico, pagamento e status
├── backend/
│   ├── pom.xml                Spring Boot 3.4.5 e Java 21 (release)
│   ├── mvnw / mvnw.cmd        Maven Wrapper 3.9.9
│   ├── src/main/java/
│   │   └── com/cafenamao/
│   │       ├── bebida/        Entidade, DTOs, service e controller
│   │       ├── pedido/        Pedido, itens, enums, service e controller
│   │       ├── comum/         Exceções e tratamento padronizado de erros
│   │       └── config/        Carga inicial das seis bebidas
│   ├── src/main/resources/    application.properties e perfil PostgreSQL
│   ├── src/test/java/         29 testes JUnit 5/MockMvc
│   └── postman/               Coleção e ambiente Postman/Newman
├── docs/
│   ├── requisitos.md          Requisitos funcionais e não funcionais
│   ├── plano-de-testes.md     Plano e casos CT01 a CT39
│   └── relatorio-testes.md    Resultados, evidências e limitações
├── package.json               Script de testes; sem dependências
├── README.md                  Documentação principal do projeto
├── README.md                  Documentação principal do projeto
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

## 8. Etapa 5 — Pagamento simulado e histórico de pedidos

Status: concluída.

Implementado:

- Escolha obrigatória entre “Dinheiro” e “Cartão fictício”, sem solicitar ou armazenar dados reais de cartão.
- Bloqueio da conclusão quando nenhuma forma de pagamento foi escolhida ou quando há quantidade inválida, com mensagem e foco no campo problemático.
- `js/historico.js` como módulo clássico compartilhado pelo navegador e pelos testes Node.
- Histórico persistido em `localStorage` na chave `cafe-na-mao:historico:v1`, com fallback silencioso quando o armazenamento não estiver disponível.
- Registro com número, data, itens, total, forma de pagamento e status.
- Histórico exibido em `pedidos.html`, com o pedido mais recente primeiro e estado vazio.
- Alteração de status entre “Recebido”, “Em preparo” e “Finalizado”, com persistência imediata.
- Limpeza do histórico em diálogo de confirmação, sem remover o carrinho atual.
- O carrinho só é limpo depois que o pedido foi salvo no histórico; se a gravação falhar, o carrinho é mantido.
- Pagamento e status aparecem na confirmação do pedido e nos cards do histórico.
- Mensagens por `aria-live`, rótulos associados aos campos e foco devolvido ao título após limpar o histórico.
- Testes unitários em `tests/historico.test.js`.

Arquivos: `pedidos.html`, `style.css`, `js/historico.js`, `js/pedidos.js`, `tests/historico.test.js`.

## 9. Etapa 6 — Documentação, página Sobre e planejamento de testes

Status: concluída.

Implementado:

- `sobre.html` preenchida com a proposta, o fluxo atual, o pagamento simulado, o
  escopo implementado, o que está fora do escopo e os próximos passos.
- Estilos específicos da página Sobre em `style.css`, reutilizando a identidade
  neobrutalista e o layout responsivo.
- `README.md` reescrito com descrição, funcionalidades, stack, estrutura, execução,
  testes, documentação, acessibilidade, limitações, uso de IA e próximos passos.
- `docs/requisitos.md` com `RF01` a `RF15`, `RNF01` a `RNF10`, critérios de
  aceitação e rastreabilidade para os casos de teste.
- `docs/plano-de-testes.md` com escopo, estratégia, ambientes, dados, riscos,
  critérios de entrada/saída e casos `CT01` a `CT26`.
- `docs/relatorio-testes.md` com os resultados reais da suíte, verificações
  estáticas, evidências do navegador, situação dos 26 casos e lacunas conhecidas.
- Links internos entre README, documentos, progresso e páginas verificados.

Arquivos: `sobre.html`, `style.css`, `README.md`, `progresso.md`, `docs/requisitos.md`,
`docs/plano-de-testes.md`, `docs/relatorio-testes.md`.

## 10. Etapa 7 — Backend, API REST e testes de integração

Status: concluída.

Implementado:

- Projeto Maven em `backend/` com Spring Boot 3.4.5, Java `--release 21` e versão
  `0.1.0`; Maven Wrapper 3.9.9 no modo `only-script`.
- API REST de bebidas em `/api/bebidas`: listagem, consulta, criação, atualização
  e exclusão, com validação por Bean Validation e HTTP 201/200/204/400/404/409.
- API REST de pedidos em `/api/pedidos`: criação com total recalculado no
  servidor, listagem ordenada, consulta por número, filtro por status, alteração
  de status, exclusão e limpeza do histórico.
- Regras mantidas do front-end: preços em centavos, quantidade de 1 a 10,
  pagamento `dinheiro`/`cartao-ficticio` e status `recebido`/`em-preparo`/
  `finalizado`.
- Carga inicial idempotente das seis bebidas com os mesmos ids do catálogo.
- Tratamento global de erros com `timestamp`, `status`, `erro`, `mensagem`,
  `caminho` e `campos`; rota inexistente agora responde 404.
- Perfil padrão H2 em memória (`cafe-na-mao-api`) e perfil PostgreSQL por
  variáveis `DB_URL`, `DB_USER` e `DB_PASSWORD`.
- 29 testes de integração JUnit 5/MockMvc: 11 de bebidas e 18 de pedidos.
- Coleção e ambiente Postman/Newman com 18 requisições e 32 assertions.
- Diretório `backend/target/` e artefatos de IDE ignorados em `backend/.gitignore`.

Arquivos principais: `backend/pom.xml`, `backend/src/main/java/com/cafenamao/`,
`backend/src/main/resources/application.properties`,
`backend/src/main/resources/application-postgres.properties`,
`backend/src/test/java/com/cafenamao/`, `backend/postman/`, `backend/mvnw`,
`backend/mvnw.cmd`.

Comandos executados:

```powershell
cd backend
.\mvnw.cmd -v
.\mvnw.cmd test
.\mvnw.cmd -DskipTests package
java -jar target\cafe-na-mao-api-0.1.0.jar
npx --yes newman run postman\CafeNaMao.postman_collection.json `
  -e postman\CafeNaMao.postman_environment.json --reporters cli --color off
```

Resultados:

- 29 testes aprovados, 0 reprovados, 0 ignorados.
- JAR `cafe-na-mao-api-0.1.0.jar` gerado com sucesso.
- Newman: 18 requisições, 0 falhas e 32 assertions aprovadas.
- Chamadas HTTP reais confirmaram 6 bebidas, pedido `PED-D103D8F7` com total
  `1550`, alteração para `em-preparo`, filtro com 1 pedido, 404 para bebida
  inexistente e limpeza do histórico com 204.
- Processo Java encerrado e porta 8080 liberada ao final.

## 11. Decisões técnicas registradas

1. HTML, CSS e JavaScript puro nesta fase para reduzir complexidade e facilitar a explicação na apresentação.
2. Scripts clássicos com padrão de proteção e namespace global (`window.CafeNaMaoDados`) em vez de módulos ES. Isso permite abrir o projeto por `file://` sem servidor local.
3. O painel de detalhes usa o elemento nativo `<dialog>`, com foco inicial no campo de quantidade e retorno do foco ao botão de origem ao fechar.
4. Preços são inteiros em centavos. A formatação usa `Intl.NumberFormat("pt-BR")` e a validação rejeita valores negativos, fracionários ou não numéricos.
5. O carregamento tem atraso simulado de 450 ms para permitir observar o estado de carregamento sem depender de rede.
6. O catálogo em si não usa `localStorage`; a partir da Etapa 4 o carrinho é salvo em `localStorage` para atravessar as páginas.
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
16. A conclusão do pedido cria um objeto com número, data, cópia dos itens, total, pagamento e status. O pedido é salvo no histórico antes de o carrinho ser limpo. Não há pagamento real nem envio ao backend.
17. A forma de pagamento é obrigatória e limitada a “dinheiro” ou “cartão fictício”. Nenhum número, validade ou código de cartão é solicitado ou armazenado.
18. Se o histórico não puder ser salvo, a confirmação não avança e o carrinho é mantido, evitando perda silenciosa do pedido.
19. O histórico valida os campos e recalcula o total a partir dos itens; registros corrompidos, com status/pagamento desconhecido ou total divergente são ignorados.
20. O status é alterado manualmente entre “Recebido”, “Em preparo” e “Finalizado”; não há progressão automática.
21. O histórico e o carrinho ficam apenas no `localStorage` do navegador. Não há conta de usuário, sincronização entre dispositivos ou backend.
22. A Etapa 6 documenta somente o que está implementado. Backend, API, banco, Docker, CI e pagamento real aparecem como escopo futuro, sem simular funcionalidades inexistentes.
23. Requisitos, plano, relatório e README usam links relativos e IDs estáveis (`RF`, `RNF` e `CT`) para manter a rastreabilidade dentro do repositório.
24. O relatório registra evidências reais e limitações: 53 testes automatizados, verificações estáticas e roteiros manuais no Edge em 390, 768 e 1366 px.
25. A página Sobre reutiliza os componentes visuais existentes e não adiciona novas funcionalidades ao fluxo de pedidos.
26. O backend usa Spring Boot 3.4.5 compilado com `--release 21`, executado em Java 22.0.2, para manter compatibilidade com Java 21 LTS.
27. O Maven Wrapper foi gerado em modo `only-script` e aponta para o Maven 3.9.9, permitindo build e testes sem Maven global.
28. O perfil padrão usa H2 em memória para desenvolvimento e testes; o PostgreSQL fica isolado em perfil com variáveis de ambiente e não foi executado nesta etapa.
29. O servidor é a fonte de verdade dos pedidos: o cliente envia `bebidaId` e `quantidade`, e o total é recalculado em centavos no backend.
30. Os ids e preços das bebidas no backend coincidem com `js/dados.js`, mas o front-end ainda não consome a API.
31. Os erros usam um formato único e estável, e rotas inexistentes são tratadas como 404 em vez de 500.
32. A coleção Postman usa variáveis de coleção e o ambiente local (`http://localhost:8080`) para permitir execução repetível via Newman.

## 12. Verificações executadas

### Testes automatizados

Comando:

```powershell
npm test
```

O comando executa `node --test`. Em sessões anteriores, quando o `npm.ps1` estava bloqueado, foi usada a versão `npm.cmd`.

Resultado: **53 testes aprovados, 0 reprovados, 0 ignorados** (19 novos testes de histórico).
O comando executa `node --test`. Em sessões anteriores, quando o `npm.ps1` estava bloqueado, foi usada a versão `npm.cmd`.

Resultado: **53 testes aprovados, 0 reprovados, 0 ignorados** (19 novos testes de histórico).

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
- persistência do carrinho em armazenamento simulado;
- criação de registro com pagamento e status inicial;
- rejeição de pagamento, status, itens e total inválidos no histórico;
- leitura de histórico vazio, ausente ou com JSON inválido;
- gravação, ordenação do mais recente e substituição de número repetido;
- alteração de status existente e rejeição de status/pedido inválido;
- filtragem de registros corrompidos e números duplicados;
- limpeza do histórico;
- comportamento com armazenamento indisponível;
- rótulos em português para status e pagamento.
- rejeição de quantidade e preço inválidos no cálculo do total;
- total do carrinho vazio igual a zero;
- total do carrinho com bebidas diferentes;
- soma de itens repetidos em uma única linha;
- alteração de quantidade e remoção de itens;
- bloqueio de pedido com carrinho vazio;
- criação de pedido com número, data, cópia dos itens e total;
- rejeição de quantidade inválida e de soma acima do limite;
- persistência do carrinho em armazenamento simulado;
- criação de registro com pagamento e status inicial;
- rejeição de pagamento, status, itens e total inválidos no histórico;
- leitura de histórico vazio, ausente ou com JSON inválido;
- gravação, ordenação do mais recente e substituição de número repetido;
- alteração de status existente e rejeição de status/pedido inválido;
- filtragem de registros corrompidos e números duplicados;
- limpeza do histórico;
- comportamento com armazenamento indisponível;
- rótulos em português para status e pagamento.

Também foram executados:

```powershell
node --check js/dados.js
node --check js/detalhes.js
node --check js/carrinho.js
node --check js/historico.js
node --check js/carrinho.js
node --check js/historico.js
node --check js/menu.js
node --check js/pedidos.js
node --check js/pedidos.js
node --check tests/dados.test.js
node --check tests/detalhes.test.js
node --check tests/carrinho.test.js
node --check tests/historico.test.js
node --check tests/carrinho.test.js
node --check tests/historico.test.js
```

Resultado: sem erros de sintaxe.

Foi executada também uma verificação estática complementar:

- todos os IDs usados por `getElementById` em `js/menu.js` e `js/pedidos.js` existem nas respectivas páginas, inclusive os do pagamento e do histórico;
- não há IDs duplicados em `index.html`, `menu.html`, `pedidos.html` ou `sobre.html`;
- as referências de `aria-labelledby` e `aria-describedby` apontam para IDs existentes;
- `js/historico.js` é carregado depois de `js/carrinho.js` e antes de `js/pedidos.js`;
- não há `href="#"` nem estilos inline;
- todos os links internos apontam para arquivos existentes.

### Backend, API e contrato

Comandos:

```powershell
cd backend
.\mvnw.cmd -v
.\mvnw.cmd test
.\mvnw.cmd -DskipTests package
java -jar target\cafe-na-mao-api-0.1.0.jar
npx --yes newman run postman\CafeNaMao.postman_collection.json `
  -e postman\CafeNaMao.postman_environment.json --reporters cli --color off
```

Resultados:

- `.\mvnw.cmd -v` confirmou Maven Wrapper 3.9.9 e Java 22.0.2.
- 29 testes JUnit 5/MockMvc aprovados: 11 de bebidas e 18 de pedidos.
- JAR `cafe-na-mao-api-0.1.0.jar` gerado em `backend/target/`.
- `GET /api/bebidas` retornou as 6 bebidas da carga inicial.
- `POST /api/pedidos` retornou `PED-D103D8F7`, total `1550` e status `recebido`.
- `PATCH` alterou o status para `em-preparo`; o filtro retornou 1 pedido.
- Bebida inexistente respondeu 404; `DELETE /api/pedidos` respondeu 204 e a lista
  final ficou vazia.
- Newman executou 18 requisições com 0 falhas e 32 assertions aprovadas.
- A coleção e o ambiente foram validados como JSON antes da execução.
- O processo Java foi encerrado e a porta 8080 ficou livre.

### Verificação no navegador

Executada com Microsoft Edge 156 em modo headless, controlado por CDP, com viewports de 390, 768 e 1366 px. O script auxiliar foi temporário e não foi adicionado ao repositório.
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
| Concluir sem pagamento | Bloqueia e mantém o carrinho visível |
| Concluir com quantidade 11 | Bloqueia a conclusão e move o foco para o campo |
| Concluir com pagamento escolhido | Confirmação exibida e pedido salvo no histórico |
| Após recarregar com histórico | Pedido persiste e carrinho fica vazio |
| Alterar status | Persiste no histórico e atualiza o cartão |
| Cancelar limpeza | Diálogo fecha sem apagar o histórico |
| Limpar histórico | Diálogo confirma, remove o storage e exibe histórico vazio |
| Teclado | `Esc` fecha o painel e devolve o foco ao botão de origem |
| Console | Nenhum erro de JavaScript durante o fluxo |
| Confirmação em 390/1366 px | Sem rolagem horizontal |
| Histórico em 390/768/1366 px | Sem rolagem horizontal |

### Verificação da Etapa 6

- `index.html` e `sobre.html` abertos no Edge 156 headless via CDP.
- Navegação principal abriu `menu.html`, `pedidos.html` e `sobre.html`; o link ativo
  foi conferido em cada página.
- A página Sobre exibiu três cards de contexto, três blocos de escopo, CTA e rodapé.
- Larguras de 390, 768 e 1366 px sem rolagem horizontal.
- Nenhum erro de JavaScript no console.
- `CT16` confirmado: com falha simulada em `Storage.prototype.setItem`, a confirmação
  não avançou, o carrinho foi preservado e nenhum pedido foi salvo.
- Links internos de README, documentos e páginas verificados; `RF01`–`RF15`,
  `RNF01`–`RNF10` e `CT01`–`CT26` presentes.
- `docs/` agora contém requisitos, plano e relatório; o README aponta para os três.
- Capturas temporárias da página Sobre em 1366 e 390 px foram inspecionadas e não
  foram versionadas.
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
| Concluir sem pagamento | Bloqueia e mantém o carrinho visível |
| Concluir com quantidade 11 | Bloqueia a conclusão e move o foco para o campo |
| Concluir com pagamento escolhido | Confirmação exibida e pedido salvo no histórico |
| Após recarregar com histórico | Pedido persiste e carrinho fica vazio |
| Alterar status | Persiste no histórico e atualiza o cartão |
| Cancelar limpeza | Diálogo fecha sem apagar o histórico |
| Limpar histórico | Diálogo confirma, remove o storage e exibe histórico vazio |
| Teclado | `Esc` fecha o painel e devolve o foco ao botão de origem |
| Console | Nenhum erro de JavaScript durante o fluxo |
| Confirmação em 390/1366 px | Sem rolagem horizontal |
| Histórico em 390/768/1366 px | Sem rolagem horizontal |

### Verificação da Etapa 6

- `index.html` e `sobre.html` abertos no Edge 156 headless via CDP.
- Navegação principal abriu `menu.html`, `pedidos.html` e `sobre.html`; o link ativo
  foi conferido em cada página.
- A página Sobre exibiu três cards de contexto, três blocos de escopo, CTA e rodapé.
- Larguras de 390, 768 e 1366 px sem rolagem horizontal.
- Nenhum erro de JavaScript no console.
- `CT16` confirmado: com falha simulada em `Storage.prototype.setItem`, a confirmação
  não avançou, o carrinho foi preservado e nenhum pedido foi salvo.
- Links internos de README, documentos e páginas verificados; `RF01`–`RF15`,
  `RNF01`–`RNF10` e `CT01`–`CT26` presentes.
- `docs/` agora contém requisitos, plano e relatório; o README aponta para os três.
- Capturas temporárias da página Sobre em 1366 e 390 px foram inspecionadas e não
  foram versionadas.

## 13. Pendências e limitações conhecidas

- A verificação foi feita no Edge headless; uma conferência visual rápida no
  navegador real ainda é recomendada antes da apresentação.
- O front-end ainda não consome a API; carrinho e histórico continuam no
  `localStorage` e não há sincronização entre dispositivos.
- O status do pedido é alterado manualmente na interface e não evolui
  automaticamente.
- O carrinho e o histórico dependem de `localStorage`; se o armazenamento for
  bloqueado, a navegação entre menu e pedidos pode não persistir o carrinho.
- O perfil PostgreSQL não foi executado porque o Docker daemon não estava em
  execução; apenas o perfil H2 em memória foi validado.
- O H2 é em memória: reiniciar a API recria o catálogo e apaga os pedidos.
- Não há JaCoCo, Docker Compose, CI ou GitHub Actions.
- Não há testes automatizados de DOM/E2E no repositório; a interface foi
  verificada manualmente com apoio de scripts temporários no Edge.
- A pasta não é um repositório Git nesta sessão, então `git diff --check` não foi
  executado.
- `docs/relatorio-testes.md` é um registro manual e precisa ser atualizado a cada
  mudança relevante no código ou nos testes.
- Riscos não bloqueantes no código: o evento `close` do `<dialog>` em `js/menu.js`
  e `salvarCarrinho` sem retorno explícito de falha. Os fluxos testados continuam
  funcionando; uma revisão pode ser feita em etapa própria.
- Não existe arquivo de logo; a marca é textual.
- Nenhum dado real de cartão pode ser coletado ou armazenado.

## 14. Próxima etapa — Etapa 8: integração, cobertura e infraestrutura

Status: aguardando autorização do usuário. Não iniciar sem pedido explícito.

Objetivo sugerido: integrar o front-end à API já implementada e evoluir a
qualidade e a infraestrutura sem quebrar as regras validadas.

Tarefas previstas (a confirmar):
Tarefas previstas (a confirmar):

1. Definir a estratégia de integração: consumo da API no navegador, fallback e
   tratamento de indisponibilidade.
2. Migrar catálogo, criação de pedidos, histórico e status para a API, mantendo os
   contratos atuais e os preços em centavos.
3. Preservar o funcionamento por `file://` quando a API não estiver disponível.
4. Adicionar cobertura de código com JaCoCo e publicar o relatório.
5. Provisionar PostgreSQL com Docker Compose e validar o perfil alternativo.
6. Configurar CI para executar as suítes de front-end e backend.
7. Avaliar testes automatizados de DOM/E2E e ampliar a matriz de navegadores.

Critérios de conclusão sugeridos:
Critérios de conclusão sugeridos:

- front-end integrado à API com estados de carregamento, erro e fallback claros;
- 53 testes do front-end e 29 testes do backend preservados ou ampliados;
- cobertura de backend medida e documentada;
- PostgreSQL validado via Docker Compose, se o ambiente permitir;
- pipeline de CI executando build, testes e cobertura;
- documentação, relatório e este arquivo atualizados.

## 15. Checklist para a próxima sessão

- [ ] Ler a skill e este arquivo.
- [ ] Rodar `npm test` e `.\mvnw.cmd test` antes de começar.
- [ ] Confirmar com o usuário o escopo da Etapa 8.
- [ ] Implementar somente a etapa autorizada.
- [ ] Executar testes unitários, testes de API e verificação no navegador.
- [ ] Revisar o diff e procurar regressões nas Etapas 1 a 7.
- [ ] Atualizar `docs/relatorio-testes.md` e `progresso.md` com os novos resultados.
- [ ] Não fazer commit ou push sem solicitação explícita do usuário.
