# Plano de testes — Café na Mão

**Versão:** 1.2
**Data:** 13/11/2026
**Escopo:** Etapas 1 a 8 — front-end estático integrado à API REST, backend
Java/Spring Boot, PostgreSQL via Docker Compose, cobertura JaCoCo e CI.
**Objetivo:** definir a estratégia, os ambientes, os critérios e os casos de teste
para validar o fluxo de catálogo, carrinho, pagamento simulado, histórico de
pedidos, os endpoints da API e a integração entre as camadas.

## 1. Escopo

### 1.1 Incluído

- Página inicial, navegação e catálogo.
- Detalhes da bebida, quantidade e total parcial.
- Carrinho, persistência, edição, remoção e bloqueio de carrinho vazio.
- Pagamento simulado e confirmação do pedido.
- Histórico, status e limpeza do histórico.
- Acessibilidade, responsividade e verificações estáticas.
- Testes unitários com `node:test`.
- API REST de bebidas e pedidos com Spring Boot.
- Validação de dados, cálculo do total no servidor e códigos HTTP.
- Testes de integração com JUnit 5/MockMvc, incluindo CORS.
- Integração do front-end com a API e fallback local por `file://`.
- Cliente HTTP com normalização, timeout e tratamento de erros.
- Coleção Postman/Newman.
- Cobertura de código e gate JaCoCo.
- PostgreSQL com Docker Compose e persistência.
- Workflow de CI para front-end, backend e contrato.

### 1.2 Excluído

- Pagamento real, autenticação, contas e sincronização entre dispositivos.
- Integração física com a máquina de bebidas.
- Implantação pública, configuração de produção e testes de carga/segurança ofensiva.
- Matriz completa de navegadores e suíte E2E versionada nesta etapa.

## 2. Referências

- [Requisitos](requisitos.md)
- [Relatório de testes](relatorio-testes.md)
- [README](../README.md)
- [Progresso](../progresso.md)
- Skill do projeto em `projeto 1/.agents/skills/cafenamao/SKILL.md`.

## 3. Estratégia

1. **Testes unitários:** validar dados, quantidades, totais, carrinho, histórico,
   pagamento, status e cliente HTTP com `node:test`, usando entradas válidas,
   inválidas e limites.
2. **Testes de interface manuais:** percorrer o fluxo no navegador, incluindo teclado,
   foco, mensagens, diálogos e estados de carregamento/erro.
3. **Verificação de responsividade:** conferir 390, 768 e 1366 px, sem rolagem
   horizontal e sem elementos cortados.
4. **Verificações estáticas:** checar sintaxe, IDs, referências ARIA, links internos,
   ausência de `href="#"` e estilos inline.
5. **Testes de integração da API:** subir o contexto Spring Boot com H2 e exercitar
   os endpoints com JUnit 5/MockMvc, incluindo sucesso, validação, limites, 404,
   409 e CORS.
6. **Testes de contrato:** executar a coleção Postman/Newman contra a API em execução.
7. **Testes de integração no navegador:** abrir as páginas por `file://`, consumir a
   API real via CDP/Edge e conferir fallback local, persistência e console.
8. **Cobertura e infraestrutura:** executar `verify` com JaCoCo, subir PostgreSQL no
   Compose e validar o workflow de CI.
9. **Evolução futura:** versionar E2E e ampliar a matriz de navegadores.

Os testes priorizam comportamento observável. Não devem depender de serviços
externos, dados reais de cartão ou de uma máquina física.

## 4. Ambientes

| Item | Versão/configuração atual |
|---|---|
| Sistema | Windows 10/11 |
| Node.js | 24.16.0 |
| Navegador | Microsoft Edge 156 (headless via CDP) |
| Execução do front-end | `file://`, sem servidor local |
| Dependências npm | Nenhuma no projeto |
| Armazenamento do front-end | `localStorage` do navegador |
| Java | 22.0.2 (compilação com `--release 21`) |
| Spring Boot | 3.4.5 |
| Maven | Wrapper 3.9.9 |
| Banco padrão | H2 2.3.232 em memória |
| Perfil alternativo | PostgreSQL 16 Alpine via Docker Compose, porta host 5433 |
| Testes da API | JUnit 5 + MockMvc |
| Cobertura | JaCoCo 0.8.12, gate 80% linhas / 50% branches |
| Coleção de API | Postman/Newman |
| Docker | 29.2.1 e Compose v5.1.0 |
| CI | GitHub Actions executado e aprovado (run 43422700883) |

## 5. Dados de teste

| Dado | Valores |
|---|---|
| Catálogo | Espresso R$ 4,00; Café com Leite R$ 5,50; Cappuccino R$ 7,50; Mocha R$ 8,50; Chocolate Quente R$ 7,00; Chá de Canela R$ 5,00 |
| Quantidades válidas | 1 e 10 |
| Quantidades inválidas | 0, 11, 1,5, texto vazio ou não numérico |
| Carrinho de referência | 3 Espressos + 2 Cafés com Leite = 5 itens e R$ 23,00 |
| Pagamentos | `dinheiro`, `cartao-ficticio` |
| Status | `recebido`, `em-preparo`, `finalizado` |
| Chaves locais | `cafe-na-mao:carrinho:v1`, `cafe-na-mao:historico:v1` |
| Viewports | 390 px, 768 px e 1366 px |
| Bebidas da API | As seis bebidas do catálogo, com ids em slug e preços em centavos |
| Pedido de referência da API | 2 Espressos + 1 Cappuccino = 1550 centavos |
| Pagamentos da API | `dinheiro`, `cartao-ficticio` |
| Status da API | `recebido`, `em-preparo`, `finalizado` |
| Erros esperados | 400 validação/regra, 404 ausente, 409 conflito |
| URL da API | `http://localhost:8080/api` (override por `CAFE_NA_MAO_API_URL` ou `?api=`) |
| Pedido do E2E | 2 Espressos = 800 centavos; número `PED-501ECE45` |
| Persistência PostgreSQL | Pedido `PED-7D812006`; 6 bebidas; histórico limpo ao final |
| Cobertura JaCoCo | 93,59% linhas; 62,50% branches |

## 6. Critérios de entrada, saída, suspensão e retomada

### Entrada

- Código disponível e páginas abrindo no navegador.
- Node.js instalado e `npm test` executável.
- Navegador moderno disponível para a verificação manual.
- Java disponível e Maven Wrapper executável para o backend.
- Docker/Compose disponíveis para os casos de PostgreSQL.
- API iniciada antes dos casos de integração e de contrato.

### Saída

- Casos críticos executados sem falha.
- `npm test` com 65 testes do front-end aprovados.
- `.\mvnw.cmd verify` com 31 testes, JAR e gate JaCoCo aprovados.
- Nenhum erro de JavaScript no console durante o fluxo principal.
- Nenhuma rolagem horizontal nas larguras verificadas.
- Nenhum dado real de cartão no código, na interface ou no armazenamento.
- API retornando os códigos HTTP previstos para sucesso e erro.
- Fallback local observado sem perda silenciosa de pedido em modo API.
- PostgreSQL preservando dados após reiniciar a API.
- Workflow de CI válido como YAML, comandos executados localmente e três jobs
  aprovados no GitHub Actions.

### Suspensão

- Falha de ambiente que impeça abrir as páginas ou executar o Node.
- Regressão que impeça o fluxo principal e exija correção antes de continuar.
- Dependência externa necessária para uma verificação planejada.

### Retomada

Após corrigir a causa, repetir os testes unitários e os casos afetados, registrar
a evidência e atualizar o relatório.

## 7. Riscos e mitigação

| Risco | Impacto | Mitigação |
|---|---|---|
| `localStorage` indisponível ou cheio | Carrinho/histórico não persistem | Tratar falha, manter o carrinho e informar o usuário |
| Navegador sem suporte ao `<dialog>` | Detalhes ou limpeza não abrem | Usar navegador moderno e fallback de atributo `open` na limpeza |
| Diferenças de renderização | Rolagem horizontal ou foco inadequado | Verificar 390/768/1366 px e navegação por teclado |
| Dados corrompidos no armazenamento | Histórico inválido | Validar campos, recalcular total e ignorar registros inválidos |
| Teste manual depender do ambiente | Resultado não reproduzível | Registrar versão do navegador, viewport e passos |
| Divergência entre front-end e API | Regras diferentes | Cobrir as mesmas validações e manter os contratos documentados |
| H2 em memória apagar dados ao reiniciar | Perda de pedidos de demonstração | Documentar a limitação e usar PostgreSQL quando necessário |
| Porta 8080 ocupada | API não inicia | Identificar o processo, liberar a porta ou configurar outra |
| Falha ao salvar pedido | Pedido perdido | Salvar histórico antes de limpar o carrinho |
| API indisponível após o carregamento | Ação falha no modo API | Exibir erro, manter o carrinho e não criar pedido local silenciosamente |
| Timeout ou resposta inválida | Catálogo/histórico não sincronizam | Timeout no cliente, mensagem clara e fallback local na carga inicial |
| Porta 5432 ocupada por PostgreSQL nativo | Compose conecta no banco errado | Usar 5433 por padrão e permitir `DB_PORT`/`DB_URL` |
| Workflow divergir do ambiente local | CI falha no GitHub | Validar YAML e repetir os mesmos comandos localmente |
| Teste E2E temporário | Sem regressão automatizada versionada | Registrar a limitação e versionar E2E em etapa autorizada |

## 8. Casos de teste

### CT01 — Página inicial e navegação

- **Requisito:** RF01.
- **Tipo:** manual.
- **Pré-condições:** abrir `index.html`.
- **Passos:** conferir título, hero, chamada para o menu, seção “Como funciona” e
  rodapé; clicar nos links de navegação; retornar à página inicial.
- **Dados:** páginas `index.html`, `menu.html`, `pedidos.html` e `sobre.html`.
- **Resultado esperado:** página carrega sem erro, link ativo é “Início” e todos os
  links internos abrem a página correspondente.

### CT02 — Catálogo de bebidas

- **Requisito:** RF02.
- **Tipo:** manual.
- **Pré-condições:** abrir `menu.html` e aguardar o carregamento simulado.
- **Passos:** conferir a quantidade de cards, nomes, categorias, descrições e preços.
- **Dados:** seis bebidas cadastradas em `js/dados.js`.
- **Resultado esperado:** seis cards corretos, com preços formatados em reais e botão
  “Ver detalhes” em cada um.

### CT03 — Estados do catálogo

- **Requisito:** RF03.
- **Tipo:** manual.
- **Pré-condições:** abrir o catálogo normal.
- **Passos:** abrir `menu.html?estado=vazio`; recarregar `menu.html?estado=erro`;
  clicar em “Tentar novamente”.
- **Dados:** parâmetros `estado=vazio` e `estado=erro`.
- **Resultado esperado:** carregamento visível, estado vazio com link para o início,
  estado de erro com mensagem e nova tentativa carregando o catálogo normal.

### CT04 — Formatação de preços

- **Requisito:** RF02, RNF04.
- **Tipo:** automatizado.
- **Pré-condições:** executar `node --test tests/dados.test.js`.
- **Passos:** validar preços e chamar `formatarPreco` com valores válidos e inválidos.
- **Dados:** `0`, `400`, `750`, `123456`, valores negativos, fracionários e texto.
- **Resultado esperado:** moeda brasileira com duas casas; valores inválidos rejeitados.

### CT05 — Detalhes e foco

- **Requisito:** RF04, RNF02.
- **Tipo:** manual.
- **Pré-condições:** abrir `menu.html` e selecionar uma bebida.
- **Passos:** abrir “Ver detalhes”; conferir conteúdo; navegar com `Tab`; pressionar
  `Esc`; reabrir pelo botão de origem.
- **Dados:** Espresso Curto.
- **Resultado esperado:** diálogo com descrição e preço, foco inicial na quantidade,
  fechamento por `Esc` e retorno do foco ao botão que abriu.

### CT06 — Quantidade e total parcial válidos

- **Requisito:** RF05, RNF04.
- **Tipo:** automatizado.
- **Pré-condições:** executar `node --test tests/detalhes.test.js`.
- **Passos:** validar quantidades nos limites e calcular o total para preço unitário.
- **Dados:** quantidades `1` e `10`, preço `400` centavos.
- **Resultado esperado:** valores aceitos e totais `400` e `4000` centavos.

### CT07 — Quantidade inválida

- **Requisito:** RF05.
- **Tipo:** automatizado e manual.
- **Pré-condições:** abrir detalhes ou executar os testes de detalhes.
- **Passos:** informar `0`, `11`, `1.5` e texto não numérico; tentar calcular/adicionar.
- **Dados:** valores fora do intervalo e não numéricos.
- **Resultado esperado:** mensagens claras, ausência de cálculo inválido e nenhuma
  alteração no carrinho.

### CT08 — Adicionar ao carrinho e persistir

- **Requisito:** RF06, RNF05.
- **Tipo:** automatizado e manual.
- **Pré-condições:** catálogo carregado e carrinho vazio.
- **Passos:** abrir detalhes, escolher quantidade 2, clicar em “Adicionar ao pedido”;
  conferir o resumo; recarregar `pedidos.html`.
- **Dados:** Espresso Curto, 400 centavos, quantidade 2.
- **Resultado esperado:** item presente, total `R$ 8,00` e carrinho recuperado do
  `localStorage`.

### CT09 — Itens repetidos e limite de quantidade

- **Requisito:** RF07.
- **Tipo:** automatizado.
- **Pré-condições:** executar `tests/carrinho.test.js`.
- **Passos:** adicionar 2, depois 3 unidades da mesma bebida; tentar ultrapassar 10.
- **Dados:** quantidades `2`, `3` e `6`.
- **Resultado esperado:** linha única com 5 unidades; soma que ultrapassa 10 é
  rejeitada sem alterar o carrinho.

### CT10 — Resumo do carrinho no menu

- **Requisito:** RF08.
- **Tipo:** manual.
- **Pré-condições:** adicionar bebidas ao carrinho.
- **Passos:** retornar ao menu e conferir o resumo; clicar no link do pedido.
- **Dados:** 3 Espressos e 2 Cafés com Leite.
- **Resultado esperado:** resumo com `5 itens`, total `R$ 23,00` e link para
  `pedidos.html`.

### CT11 — Editar e remover itens

- **Requisito:** RF09.
- **Tipo:** automatizado e manual.
- **Pré-condições:** carrinho com pelo menos dois itens.
- **Passos:** alterar a quantidade de um item; conferir subtotal/total; remover o item;
  remover todos os itens.
- **Dados:** mudança para quantidade 5 e remoção de uma linha.
- **Resultado esperado:** totais recalculados, item removido e estado vazio exibido
  quando o carrinho fica sem itens.

### CT12 — Carrinho vazio bloqueia conclusão

- **Requisito:** RF10.
- **Tipo:** automatizado e manual.
- **Pré-condições:** carrinho vazio.
- **Passos:** abrir `pedidos.html` e tentar concluir; chamar a regra de domínio.
- **Dados:** carrinho `{ itens: [] }`.
- **Resultado esperado:** mensagem de carrinho vazio, nenhum pedido criado e nenhum
  histórico salvo.

### CT13 — Pagamento obrigatório

- **Requisito:** RF11.
- **Tipo:** manual.
- **Pré-condições:** carrinho com item e quantidade válida.
- **Passos:** clicar em “Concluir pedido” sem selecionar pagamento.
- **Dados:** carrinho com Espresso Curto.
- **Resultado esperado:** mensagem “Escolha uma forma de pagamento”, campo com
  `aria-invalid`, foco no primeiro rádio, carrinho mantido e histórico vazio.

### CT14 — Opções de pagamento e privacidade

- **Requisito:** RF11, RNF06.
- **Tipo:** manual.
- **Pré-condições:** abrir o resumo do carrinho.
- **Passos:** conferir as opções e os textos; inspecionar os campos do formulário.
- **Dados:** rótulos “Dinheiro” e “Cartão fictício”.
- **Resultado esperado:** apenas duas opções, nenhum campo de número/validade/CVV e
  aviso de pagamento simulado visível.

### CT15 — Confirmar pedido válido

- **Requisito:** RF12.
- **Tipo:** manual.
- **Pré-condições:** carrinho válido e pagamento “Dinheiro” selecionado.
- **Passos:** concluir o pedido; conferir a confirmação; abrir o histórico.
- **Dados:** 2 Espressos, total `R$ 8,00`.
- **Resultado esperado:** confirmação com número, data, itens, total, pagamento e
  status “Recebido”; histórico com um registro; carrinho removido do armazenamento.

### CT16 — Falha ao salvar histórico mantém o carrinho

- **Requisito:** RF12, RNF05.
- **Tipo:** manual.
- **Pré-condições:** carrinho válido e armazenamento local indisponível ou simulado
  como falha.
- **Passos:** selecionar pagamento e tentar concluir.
- **Dados:** carrinho válido e `localStorage` bloqueado.
- **Resultado esperado:** mensagem de falha, confirmação não exibida, pedido não salvo
  e carrinho preservado para nova tentativa.

### CT17 — Histórico persiste após recarregar

- **Requisito:** RF13.
- **Tipo:** manual.
- **Pré-condições:** pedido confirmado em CT15.
- **Passos:** recarregar `pedidos.html`; conferir histórico e carrinho.
- **Dados:** pedido salvo no `localStorage`.
- **Resultado esperado:** histórico mantém o pedido, status e total; carrinho aparece
  vazio e confirmação não fica visível após recarregar.

### CT18 — Histórico vazio e ordenação

- **Requisito:** RF13.
- **Tipo:** automatizado e manual.
- **Pré-condições:** sem histórico ou com dois pedidos de datas diferentes.
- **Passos:** abrir o histórico vazio; salvar dois pedidos; recarregar.
- **Dados:** pedidos `PED-00000001` e `PED-00000002`.
- **Resultado esperado:** estado vazio com mensagem e link; pedido mais recente em
  primeiro lugar.

### CT19 — Registros inválidos e duplicados no histórico

- **Requisito:** RF13.
- **Tipo:** automatizado.
- **Pré-condições:** executar `tests/historico.test.js`.
- **Passos:** carregar JSON inválido, objeto sem lista, item com total divergente,
  status inválido e número duplicado.
- **Dados:** registros corrompidos e registros válidos.
- **Resultado esperado:** registros inválidos ignorados; duplicados não repetidos;
  registros válidos preservados.

### CT20 — Histórico no celular, tablet e desktop

- **Requisito:** RF13, RNF01.
- **Tipo:** manual.
- **Pré-condições:** histórico com pelo menos um pedido.
- **Passos:** abrir `pedidos.html` em 390, 768 e 1366 px; rolar até o histórico;
  conferir cards, botão de limpar e seletor de status.
- **Dados:** um pedido no histórico.
- **Resultado esperado:** sem rolagem horizontal, textos legíveis e controles
  acessíveis em todas as larguras.

### CT21 — Alterar status do pedido

- **Requisito:** RF14.
- **Tipo:** automatizado e manual.
- **Pré-condições:** histórico com um pedido.
- **Passos:** alterar de “Recebido” para “Em preparo” e depois “Finalizado”; recarregar.
- **Dados:** os três status permitidos.
- **Resultado esperado:** etiqueta visual atualiza, armazenamento recebe o novo status
  e a alteração persiste após recarregar.

### CT22 — Status inválido ou pedido inexistente

- **Requisito:** RF14.
- **Tipo:** automatizado.
- **Pré-condições:** executar `tests/historico.test.js`.
- **Passos:** tentar alterar para status desconhecido e para número inexistente.
- **Dados:** `cancelado` e `PED-inexistente`.
- **Resultado esperado:** operação retorna falha e o histórico permanece inalterado.

### CT23 — Limpar histórico com confirmação

- **Requisito:** RF15.
- **Tipo:** automatizado e manual.
- **Pré-condições:** histórico com um pedido.
- **Passos:** abrir “Limpar histórico”; clicar em “Cancelar”; repetir e confirmar.
- **Dados:** um pedido salvo.
- **Resultado esperado:** cancelar mantém o registro; confirmar remove a chave do
  armazenamento, exibe histórico vazio e move o foco para o título.

### CT24 — Responsividade e identidade visual

- **Requisito:** RNF01, RNF09.
- **Tipo:** manual.
- **Pré-condições:** abrir as quatro páginas no navegador.
- **Passos:** verificar 390, 768 e 1366 px; conferir menu, cards, formulário, histórico,
  botões e contraste.
- **Dados:** viewports de 390, 768 e 1366 px.
- **Resultado esperado:** sem rolagem horizontal, sem elementos cortados e identidade
  neobrutalista preservada.

### CT25 — Acessibilidade e navegação por teclado

- **Requisito:** RNF02, RNF03.
- **Tipo:** manual.
- **Pré-condições:** abrir `menu.html` e `pedidos.html`.
- **Passos:** percorrer o fluxo usando apenas `Tab`, `Shift+Tab`, `Enter` e `Esc`;
  conferir foco, rótulos e mensagens; inspecionar `aria-live`.
- **Dados:** fluxo catálogo → detalhes → carrinho → histórico.
- **Resultado esperado:** foco visível, ordem lógica, diálogos acessíveis, rótulos
  associados, mensagens anunciadas e erros não comunicados apenas por cor.

### CT26 — Verificações estáticas e execução offline

- **Requisito:** RNF03, RNF07, RNF08, RNF10.
- **Tipo:** automatizado/estático.
- **Pré-condições:** Node.js e navegador disponíveis.
- **Passos:** executar `npm test`, `node --check` nos JavaScripts, checagem de IDs e
  links, `git diff --check` e abrir as páginas por `file://`.
- **Dados:** arquivos atuais do projeto e documentação em `docs/`.
- **Resultado esperado:** 65 testes aprovados, sem erro de sintaxe, IDs/links válidos,
  sem servidor local e documentação rastreável.

### CT27 — API lista e consulta bebidas

- **Requisito:** RF16.
- **Tipo:** automatizado e manual.
- **Pré-condições:** backend iniciado com H2 e carga inicial concluída.
- **Passos:** executar `GET /api/bebidas`; consultar `GET /api/bebidas/espresso-curto`;
  consultar um id inexistente.
- **Dados:** catálogo inicial com seis bebidas.
- **Resultado esperado:** lista com seis bebidas ordenada por nome, consulta com preço
  `400` e HTTP 404 para id inexistente.

### CT28 — API cria bebida e rejeita duplicidade

- **Requisito:** RF17.
- **Tipo:** automatizado.
- **Pré-condições:** executar `BebidaControllerIntegrationTest`.
- **Passos:** enviar uma bebida nova; repetir o mesmo id.
- **Dados:** id `cha-verde-teste`, preço `525` centavos.
- **Resultado esperado:** HTTP 201 com `Location`, depois HTTP 409 com mensagem de
  conflito.

### CT29 — API valida dados de bebida

- **Requisito:** RF19.
- **Tipo:** automatizado.
- **Pré-condições:** executar `BebidaControllerIntegrationTest`.
- **Passos:** enviar id com formato inválido, nome vazio, descrição vazia, categoria
  vazia e preço negativo.
- **Dados:** payload inválido.
- **Resultado esperado:** HTTP 400 com a lista de campos inválidos.

### CT30 — API atualiza e exclui bebida

- **Requisito:** RF18.
- **Tipo:** automatizado.
- **Pré-condições:** executar `BebidaControllerIntegrationTest`.
- **Passos:** atualizar uma bebida; excluir uma bebida temporária; repetir as
  operações com id inexistente.
- **Dados:** `mocha` com preço `900`; bebida temporária.
- **Resultado esperado:** HTTP 200 na atualização, HTTP 204 na exclusão, HTTP 404 nas
  operações com id inexistente.

### CT31 — API cria pedido e calcula o total no servidor

- **Requisito:** RF20, RF21, RNF15.
- **Tipo:** automatizado e manual.
- **Pré-condições:** backend iniciado e bebidas carregadas.
- **Passos:** enviar `POST /api/pedidos` com 2 Espressos e 1 Cappuccino; conferir o
  corpo da resposta.
- **Dados:** `dinheiro`; total esperado `1550` centavos.
- **Resultado esperado:** HTTP 201, número `PED-…`, itens com cópia de nome/preço,
  status `recebido` e total `1550` calculado pelo servidor.

### CT32 — API rejeita pagamento inválido

- **Requisito:** RF22.
- **Tipo:** automatizado.
- **Pré-condições:** executar `PedidoControllerIntegrationTest`.
- **Passos:** criar pedido com pagamento `pix`.
- **Dados:** um item válido e pagamento desconhecido.
- **Resultado esperado:** HTTP 400 com mensagem sobre as formas aceitas.

### CT33 — API rejeita carrinho vazio

- **Requisito:** RF22.
- **Tipo:** automatizado.
- **Pré-condições:** executar `PedidoControllerIntegrationTest`.
- **Passos:** criar pedido com lista `itens` vazia.
- **Dados:** `itens: []`.
- **Resultado esperado:** HTTP 400 com campo `itens` inválido.

### CT34 — API rejeita quantidade fora de 1 a 10

- **Requisito:** RF22.
- **Tipo:** automatizado.
- **Pré-condições:** executar `PedidoControllerIntegrationTest`.
- **Passos:** criar pedidos com quantidade `0` e `11`.
- **Dados:** uma bebida por pedido.
- **Resultado esperado:** HTTP 400 nos dois casos com campo de quantidade inválido.

### CT35 — API rejeita bebida inexistente ou repetida

- **Requisito:** RF22.
- **Tipo:** automatizado.
- **Pré-condições:** executar `PedidoControllerIntegrationTest`.
- **Passos:** criar pedido com id inexistente; criar pedido com a mesma bebida duas
  vezes.
- **Dados:** `nao-existe` e `mocha` repetida.
- **Resultado esperado:** HTTP 400 com mensagens específicas.

### CT36 — API lista, filtra e altera status

- **Requisito:** RF23.
- **Tipo:** automatizado.
- **Pré-condições:** executar `PedidoControllerIntegrationTest`.
- **Passos:** criar pedidos; consultar por número; alterar status para `em-preparo`;
  filtrar por status; tentar status inválido.
- **Dados:** status `recebido`, `em-preparo`, `finalizado` e `perdido`.
- **Resultado esperado:** listagem do mais recente, consulta correta, status atualizado,
  filtro coerente, HTTP 400 para status inválido e HTTP 404 para pedido ausente.

### CT37 — API exclui pedido e limpa histórico

- **Requisito:** RF24.
- **Tipo:** automatizado.
- **Pré-condições:** executar `PedidoControllerIntegrationTest`.
- **Passos:** criar e excluir um pedido; limpar todos os pedidos; consultar a lista.
- **Dados:** dois pedidos criados.
- **Resultado esperado:** HTTP 204 nas exclusões, HTTP 404 após excluir um pedido e
  lista vazia após a limpeza.

### CT38 — Contrato Postman/Newman

- **Requisito:** RNF14.
- **Tipo:** contrato.
- **Pré-condições:** API em execução e Newman disponível.
- **Passos:** executar a coleção com o ambiente local; conferir as assertions de
  status, campos e totais.
- **Dados:** `backend/postman/CafeNaMao.postman_collection.json` e ambiente local.
- **Resultado esperado:** todas as requisições da coleção aprovadas. O JSON da coleção
  e do ambiente deve ser válido mesmo quando o Newman não estiver instalado.
- **Resultado obtido:** 18 requisições executadas, 0 falhas e 32 assertions aprovadas.

### CT39 — Build e testes do backend sem Maven global

- **Requisito:** RNF11, RNF12, RNF13.
- **Tipo:** automatizado/estático.
- **Pré-condições:** Java instalado e pasta `backend/`.
- **Passos:** executar `.\mvnw.cmd -v`, `.\mvnw.cmd verify` e
  `.\mvnw.cmd -DskipTests package`; conferir o perfil H2 e o arquivo PostgreSQL.
- **Dados:** Maven Wrapper 3.9.9; Spring Boot 3.4.5; H2 em memória.
- **Resultado esperado:** wrapper funcional, 31 testes aprovados, JAR gerado,
  JaCoCo dentro do gate, `application-postgres.properties` presente e H2 no perfil
  padrão.

### CT40 — Cliente HTTP normaliza, cria e trata erros

- **Requisito:** RF25, RF29, RNF16, RNF20.
- **Tipo:** automatizado.
- **Pré-condições:** executar `node --test tests/api.test.js`.
- **Passos:** normalizar bebidas e pedidos; listar catálogo; criar pedido; listar
  pedidos com filtro; alterar status; limpar histórico; simular erro HTTP, falha de
  conexão, timeout e resposta de catálogo inválida.
- **Dados:** respostas simuladas da API e pedido `PED-501ECE45`.
- **Resultado esperado:** 12 testes aprovados, erros convertidos em `ErroApi` e
  operações válidas retornando dados normalizados.

### CT41 — Menu consome a API e usa fallback local

- **Requisito:** RF25, RNF20.
- **Tipo:** integração manual/automatizada (E2E temporário).
- **Pré-condições:** API iniciada; abrir `menu.html` por `file://` no Edge via CDP.
- **Passos:** abrir o menu com a API ativa; conferir badge e cards; encerrar a API;
  recarregar a página; conferir badge, catálogo e mensagem de fallback; observar o
  console.
- **Dados:** seis bebidas do catálogo; URL padrão `http://localhost:8080/api`.
- **Resultado esperado:** modo `API` com 6 cards; modo `local` com 6 cards e aviso
  “API indisponível — usando catálogo local.”; nenhum erro de console.

### CT42 — Pedidos usam a API, persistem e voltam ao modo local

- **Requisito:** RF26, RF27, RNF20.
- **Tipo:** integração manual/automatizada (E2E temporário).
- **Pré-condições:** API iniciada e carrinho local com 2 Espressos.
- **Passos:** abrir `pedidos.html`; conferir badge `API`; concluir com pagamento
  “Dinheiro”; conferir número/total; alterar status para “Em preparo”; recarregar;
  limpar o histórico; confirmar na API; encerrar a API e recarregar a página.
- **Dados:** total `800` centavos; pedido `PED-501ECE45`.
- **Resultado esperado:** pedido criado com total do servidor, status persistido,
  histórico remoto vazio após limpeza e fallback local visível quando a API cai.
  Falha de criação em modo API mantém o carrinho e exibe erro, sem criar pedido local.

### CT43 — CORS aceita páginas abertas por file://

- **Requisito:** RF28, RNF19.
- **Tipo:** automatizado.
- **Pré-condições:** executar `CorsIntegrationTest`.
- **Passos:** enviar requisição para `/api/**` com `Origin: null`; conferir os
  cabeçalhos de resposta.
- **Dados:** origem `null`, métodos e cabeçalhos usados pelo cliente.
- **Resultado esperado:** os dois testes CORS passam e a resposta permite a origem
  `null`. A configuração é restrita ao desenvolvimento local, não à produção.

### CT44 — Cobertura JaCoCo e gate do build

- **Requisito:** RNF17.
- **Tipo:** automatizado/estrutural.
- **Pré-condições:** executar `.\mvnw.cmd verify` em `backend/`.
- **Passos:** rodar os testes, gerar o relatório JaCoCo e conferir o gate de linhas
  e branches; inspecionar o XML/HTML do relatório.
- **Dados:** 31 testes; limite de 80% de linhas e 50% de branches.
- **Resultado esperado:** build aprovado, `263` linhas cobertas e `18` perdidas
  (93,59%), branches em 62,50%, acima dos limites.

### CT45 — PostgreSQL via Compose e persistência após reinício

- **Requisito:** RF30, RNF12, RNF21.
- **Tipo:** integração de infraestrutura.
- **Pré-condições:** Docker em execução; portas livres; `docker compose up -d`.
- **Passos:** conferir healthcheck; iniciar o backend com perfil `postgres`; listar
  6 bebidas; criar um pedido; reiniciar a API; consultar o pedido; limpar o
  histórico; executar `docker compose down`.
- **Dados:** PostgreSQL 16 Alpine na porta host 5433; pedido `PED-7D812006`.
- **Resultado esperado:** container `healthy`, 6 bebidas, pedido persistido após o
  reinício, limpeza retornando 0 pedidos e volume preservado após `down`.

### CT46 — Workflow de CI e artefatos

- **Requisito:** RF31, RNF18.
- **Tipo:** estático, execução local dos comandos e execução remota no CI.
- **Pré-condições:** arquivo `.github/workflows/ci.yml` presente.
- **Passos:** validar o YAML; conferir jobs de front-end, backend e contrato;
  repetir localmente `npm test`, `.\mvnw.cmd verify` e Newman; conferir os caminhos
  dos artefatos JaCoCo e JAR.
- **Dados:** Node 24, Java 21, Maven Wrapper e coleção Postman existente.
- **Resultado esperado:** YAML válido, jobs/artefatos coerentes, comandos locais
  aprovados e workflow concluído com sucesso no GitHub Actions.
- **Resultado obtido:** job de contrato reproduzido localmente com 18 requisições,
  32 assertions e 0 falhas; API encerrada e porta 8080 liberada. No GitHub Actions,
  os jobs Front-end, Backend e cobertura e Contrato Postman/Newman passaram
  (run 43422700883).

### CT47 — Regressão, documentação e contagens

- **Requisito:** RNF07, RNF10, RNF13.
- **Tipo:** regressão/estático.
- **Pré-condições:** código e documentos atualizados da Etapa 8.
- **Passos:** executar `npm test`, `.\mvnw.cmd verify` e Newman; conferir README,
  requisitos, plano, relatório e progresso; procurar referências obsoletas a
  “integração futura” e “CI não implementado”.
- **Dados:** 65 testes front-end, 31 backend e 18 requisições Newman.
- **Resultado esperado:** 96 testes automatizados aprovados, contrato sem falhas e
  documentação coerente com o estado real.

## 9. Rastreabilidade resumida

| Área | Requisitos | Casos |
|---|---|---|
| Navegação e catálogo | RF01, RF02, RF03 | CT01, CT02, CT03 |
| Dados e moeda | RF02, RF04, RNF04 | CT04 |
| Detalhes e quantidade | RF04, RF05 | CT05, CT06, CT07 |
| Carrinho | RF06, RF07, RF08, RF09, RF10 | CT08 a CT12 |
| Pagamento e confirmação | RF11, RF12, RNF05, RNF06 | CT13 a CT16 |
| Histórico e status | RF13, RF14, RF15 | CT17 a CT23 |
| Qualidade não funcional do front-end | RNF01 a RNF10 | CT24, CT25, CT26 |
| Bebidas na API | RF16, RF17, RF18, RF19 | CT27 a CT30 |
| Pedidos na API | RF20, RF21, RF22, RF23, RF24 | CT31 a CT37 |
| Qualidade não funcional do backend | RNF11 a RNF15 | CT31, CT38, CT39 |
| Integração front-end/API | RF25, RF26, RF27, RF29, RNF16, RNF20 | CT40, CT41, CT42 |
| CORS para file:// | RF28, RNF19 | CT43 |
| Cobertura JaCoCo | RNF17 | CT44 |
| PostgreSQL e persistência | RF30, RNF12, RNF21 | CT45 |
| CI e artefatos | RF31, RNF18 | CT46 |
| Regressão e documentação | RNF07, RNF10, RNF13 | CT47 |

## 10. Métricas e aprovação

- **Casos planejados:** 47 (39 anteriores + 8 da Etapa 8).
- **Com suporte automatizado no front-end:** CT04, CT06, CT07, CT08, CT09, CT11,
  CT12, CT18, CT19, CT21, CT22, CT23, CT26, CT40 e CT47.
- **Com suporte automatizado no backend:** CT27 a CT39, CT43, CT44 e CT47
  (CT38 via Newman).
- **Contrato Postman/Newman:** CT38 (18 requisições e 32 assertions).
- **Integração/E2E:** CT41 e CT42 (script temporário via CDP no Edge).
- **Infraestrutura:** CT45 (PostgreSQL/Compose) e CT46 (CI/YAML, comandos locais e
  execução remota).
- **Verificação manual obrigatória:** CT01, CT02, CT03, CT05, CT10, CT13 a CT17,
  CT20, CT24 e CT25.
- **Aprovação:** todos os casos críticos aprovados, 65 testes do front-end, 31
  testes do backend e 18 requisições Newman aprovadas; cobertura acima do gate;
  ausência de erro no console, de rolagem horizontal nas larguras alvo e de dados
  reais de cartão.
- **Evidências:** registrar comandos, resultados e limitações em
  `docs/relatorio-testes.md`.
