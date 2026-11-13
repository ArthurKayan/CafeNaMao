# Requisitos - Café na Mão

**Versão:** 1.2
**Última atualização:** 13/11/2026
**Escopo:** Etapas 1 a 8

## 1. Objetivo

Definir os requisitos funcionais e não funcionais do Café na Mão, aplicação
acadêmica de pedidos de bebidas para uma máquina de café. O sistema deve permitir
consultar bebidas, montar um pedido, simular o pagamento, acompanhar o histórico e
demonstrar práticas de Garantia de Qualidade de Software.

## 2. Contexto e atores

- **Cliente:** consulta o catálogo, escolhe bebidas e quantidades, simula o
  pagamento e acompanha os próprios pedidos no navegador.
- **Operador (implícito):** altera o status dos pedidos no histórico.
- **Equipe do projeto:** executa o backend, o banco e as verificações de qualidade.
- **Front-end:** páginas estáticas abertas por `file://` ou por um navegador local.
- **Backend:** API REST em Java/Spring Boot, fonte de verdade dos preços e pedidos.

## 3. Premissas do escopo atual

1. O pagamento é fictício; nenhum dado real de cartão é coletado.
2. O front-end continua sem build, sem servidor de aplicação e sem framework.
3. A API é consumida quando está disponível; sem ela, o front-end usa fallback
   local para catálogo e histórico.
4. O carrinho e o histórico local usam `localStorage`; o backend usa H2 em memória
   por padrão e PostgreSQL no perfil alternativo.
5. A integração preserva os contratos REST e os valores monetários em centavos.
6. O escopo público, autenticação e integração física com a máquina estão fora
   desta entrega.

## 4. Requisitos funcionais

| ID | Requisito | Critério observável | Situação |
|---|---|---|---|
| RF01 | Exibir página inicial com navegação e chamada para o menu. | A página abre com cabeçalho, hero, link para o menu, “Como funciona” e rodapé. | Implementado |
| RF02 | Exibir catálogo de bebidas. | São exibidas seis bebidas com nome, categoria, descrição e preço. | Implementado |
| RF03 | Exibir estados do catálogo. | Carregamento, catálogo vazio e erro com nova tentativa são visíveis e anunciados por `aria-live`. | Implementado |
| RF04 | Abrir detalhes de uma bebida. | O detalhe abre em `<dialog>`, mostra descrição e preço, recebe foco e fecha por botão ou `Esc`. | Implementado |
| RF05 | Validar quantidade e total parcial. | Quantidades de 1 a 10 são aceitas; valores inválidos mostram erro e o total é calculado em centavos. | Implementado |
| RF06 | Adicionar bebida ao carrinho. | O item é incluído com id, nome, preço, quantidade e o carrinho é persistido. | Implementado |
| RF07 | Somar itens repetidos e limitar a quantidade. | Adições repetidas atualizam a mesma linha; ultrapassar 10 por bebida é bloqueado. | Implementado |
| RF08 | Exibir resumo do carrinho no menu. | O resumo mostra quantidade total, valor total e link para `pedidos.html`. | Implementado |
| RF09 | Editar e remover itens do carrinho. | Alterar quantidade ou remover item atualiza subtotal, total e armazenamento. | Implementado |
| RF10 | Bloquear pedido com carrinho vazio. | A interface impede a conclusão e a regra de domínio também rejeita a criação. | Implementado |
| RF11 | Exigir pagamento simulado. | A conclusão exige “Dinheiro” ou “Cartão fictício”; nenhum campo de cartão real é exibido. | Implementado |
| RF12 | Confirmar pedido e salvar histórico. | O pedido recebe número, data, itens, total, pagamento e status; é salvo antes de limpar o carrinho. | Implementado |
| RF13 | Exibir histórico de pedidos. | O histórico persiste no navegador, mostra o mais recente primeiro e exibe estado vazio. | Implementado |
| RF14 | Alterar status do pedido. | O status pode ser “Recebido”, “Em preparo” ou “Finalizado” e permanece após recarregar. | Implementado |
| RF15 | Limpar histórico com confirmação. | Cancelar mantém os pedidos; confirmar remove o armazenamento e exibe o estado vazio. | Implementado |
| RF16 | Listar e consultar bebidas pela API. | `GET /api/bebidas` retorna o catálogo ordenado por nome; `GET /api/bebidas/{id}` retorna a bebida ou HTTP 404. | Implementado |
| RF17 | Criar bebida pela API. | `POST /api/bebidas` valida os campos, retorna HTTP 201 com `Location` e rejeita id duplicado com HTTP 409. | Implementado |
| RF18 | Atualizar e excluir bebida pela API. | `PUT /api/bebidas/{id}` atualiza os campos; `DELETE /api/bebidas/{id}` retorna HTTP 204; ids ausentes retornam HTTP 404. | Implementado |
| RF19 | Validar dados de bebida. | Id com formato de slug, nome/descrição/categoria preenchidos e preço inteiro entre 0 e 1.000.000 de centavos; erros retornam HTTP 400 com campos. | Implementado |
| RF20 | Criar pedido pela API. | `POST /api/pedidos` recebe `pagamento` e itens com `bebidaId` e `quantidade`; retorna HTTP 201 com número, itens, total, pagamento e status. | Implementado |
| RF21 | Calcular o total no servidor. | O backend consulta o preço atual da bebida, grava uma cópia de nome/preço e calcula o total em centavos; preço enviado pelo cliente não é aceito. | Implementado |
| RF22 | Validar pedido pela API. | Rejeita carrinho vazio, quantidade fora de 1 a 10, bebida inexistente, bebida repetida e pagamento diferente de `dinheiro`/`cartao-ficticio` com HTTP 400. | Implementado |
| RF23 | Consultar, filtrar e alterar status de pedidos. | `GET /api/pedidos` lista do mais recente e aceita `?status=`; `GET /api/pedidos/{numero}` consulta; `PATCH /api/pedidos/{numero}/status` altera o status. | Implementado |
| RF24 | Excluir pedido e limpar histórico pela API. | `DELETE /api/pedidos/{numero}` remove um pedido; `DELETE /api/pedidos` limpa o histórico; ambos retornam HTTP 204. | Implementado |
| RF25 | Consumir o catálogo da API no front-end. | `menu.html` tenta `GET /api/bebidas`, renderiza as bebidas normalizadas e exibe badge de origem; se a API falhar, usa `js/dados.js` e informa “API indisponível”. | Implementado |
| RF26 | Criar pedidos pela API no front-end. | Em modo API, o pedido é enviado com `bebidaId`/`quantidade`/`pagamento`, o total vem do servidor e o carrinho só é limpo após sucesso. Falha de criação não cai silenciosamente para o fluxo local. | Implementado |
| RF27 | Sincronizar histórico pela API no front-end. | Em modo API, listar, alterar status e limpar histórico usam os endpoints REST; sem sincronização inicial, o histórico local continua disponível e a origem é indicada. | Implementado |
| RF28 | Permitir chamadas de páginas `file://` à API. | Respostas CORS para `/api/**` aceitam `Origin: null`, métodos e cabeçalhos usados pelo front-end. | Implementado |
| RF29 | Tratar timeout e erros da API no cliente. | `js/api.js` aplica timeout, converte falhas em `ErroApi` compreensível e preserva mensagens de erro HTTP quando disponíveis. | Implementado |
| RF30 | Provisionar PostgreSQL para o backend. | `docker compose up -d` sobe PostgreSQL 16 com healthcheck/volume; backend no perfil `postgres` usa a porta host padrão 5433 e persiste os dados. | Implementado |
| RF31 | Automatizar build e testes em CI. | Workflow do GitHub Actions executa front-end, backend com `verify`/JaCoCo e contrato Newman, publicando relatório e JAR. | Atendido (run 43422700883) |

## 5. Requisitos não funcionais

| ID | Requisito | Critério observável | Situação |
|---|---|---|---|
| RNF01 | Responsividade. | Páginas sem rolagem horizontal em 390, 768 e 1366 px. | Atendido |
| RNF02 | Acessibilidade. | Foco visível, navegação por teclado, rótulos, contraste e mensagens por `aria-live`. | Atendido com verificação manual |
| RNF03 | HTML semântico e DOM seguro. | Estrutura semântica, sem estilos inline, sem `href="#"` e dados inseridos com `textContent`/`createElement`. | Atendido |
| RNF04 | Precisão monetária. | Valores em centavos inteiros; formatação com `Intl.NumberFormat("pt-BR")`. | Atendido |
| RNF05 | Persistência local com fallback. | Carrinho e histórico usam `localStorage`; falhas de armazenamento são tratadas sem apagar o carrinho. | Atendido com ressalva |
| RNF06 | Privacidade do pagamento. | Nenhum número, validade ou código de cartão é solicitado, exibido ou salvo. | Atendido |
| RNF07 | Testabilidade. | Regras de domínio separadas em módulos testáveis com `node:test`; suíte sem dependências externas. | Atendido |
| RNF08 | Execução sem servidor. | O front-end abre por `file://` e não exige instalação de dependências. | Atendido |
| RNF09 | Identidade visual. | Paleta neobrutalista, bordas grossas, sombras duras e contraste legível. | Atendido |
| RNF10 | Documentação e rastreabilidade. | Requisitos, plano, relatório e progresso registram escopo, evidências e limitações. | Atendido |
| RNF11 | Execução do backend sem Maven global. | Maven Wrapper 3.9.9 permite `.\mvnw.cmd test`, `verify` e `spring-boot:run` em ambiente com Java. | Atendido |
| RNF12 | Persistência configurável. | Perfil padrão usa H2 em memória; perfil `postgres` usa PostgreSQL via Compose/Docker, com porta padrão 5433. | Atendido |
| RNF13 | Testes de integração do backend. | JUnit 5 e MockMvc cobrem sucesso, validação, limites, 404, 409, CORS e limpeza em 31 testes. | Atendido |
| RNF14 | Contrato de API documentado. | Coleção e ambiente Postman/Newman incluem operações principais e assertions. | Atendido (18 requisições, 32 assertions) |
| RNF15 | Precisão monetária no backend. | Preços e totais usam inteiros em centavos; o servidor recalcula o total. | Atendido |
| RNF16 | Testes do cliente HTTP e da integração. | 12 testes cobrem normalização, sucesso, erro HTTP, falha de conexão e timeout; E2E confirma os fluxos reais. | Atendido |
| RNF17 | Cobertura de código no backend. | JaCoCo gera relatório em `verify` e exige mínimo de 80% de linhas e 50% de branches. | Atendido (93,59% linhas e 62,50% branches) |
| RNF18 | CI reproduzível e com artefatos. | Workflow usa wrappers/versões fixas, falha com comando inválido e publica JaCoCo e JAR. | Atendido (3 jobs aprovados no GitHub Actions) |
| RNF19 | Integração local segura o suficiente para `file://`. | CORS é amplo apenas para `/api/**` em desenvolvimento; não há segredos reais nem autenticação simulada insegura. | Atendido com ressalva |
| RNF20 | Resiliência da integração. | Timeout configurável, fallback visível para catálogo/histórico e erro explícito na criação de pedido em modo API. | Atendido |
| RNF21 | Persistência real validada. | Pedido criado no perfil PostgreSQL continua existindo após reiniciar a API; limpeza também persiste. | Atendido |

## 6. Fora do escopo atual

- Pagamento real, cobrança financeira ou integração com adquirentes.
- Login, contas de usuário, autorização e sincronização entre dispositivos.
- Integração física com a máquina de bebidas.
- Implantação pública, domínio, TLS e configuração de produção.
- Matriz ampla de navegadores e testes E2E versionados.
- Testes de carga, estresse, penetração ou auditoria formal de segurança.
- Manutenção e administração remota de máquinas.

## 7. Rastreabilidade

### 7.1 Requisitos funcionais

| Requisito | Casos de teste | Evidência |
|---|---|---|
| RF01 | CT01 | Verificação manual da página inicial. |
| RF02 | CT02, CT04 | `tests/dados.test.js` e verificação manual do catálogo. |
| RF03 | CT03 | Verificação manual com `?estado=vazio` e `?estado=erro`. |
| RF04 | CT05 | Verificação manual do diálogo e foco. |
| RF05 | CT06, CT07 | `tests/detalhes.test.js`. |
| RF06 | CT08 | `tests/carrinho.test.js` e verificação de `localStorage`. |
| RF07 | CT09 | `tests/carrinho.test.js`. |
| RF08 | CT10 | Verificação manual do resumo no menu. |
| RF09 | CT11 | `tests/carrinho.test.js` e verificação manual. |
| RF10 | CT12 | `tests/carrinho.test.js` e verificação manual. |
| RF11 | CT13, CT14 | Verificação manual e validação de pagamento no histórico. |
| RF12 | CT15, CT16 | Verificação manual do fluxo e do bloqueio em falha de gravação. |
| RF13 | CT17, CT18, CT19, CT20 | `tests/historico.test.js` e verificação no navegador. |
| RF14 | CT21, CT22 | `tests/historico.test.js` e verificação manual. |
| RF15 | CT23 | `tests/historico.test.js` e verificação manual do diálogo. |
| RF16 | CT27 | `BebidaControllerIntegrationTest` e chamadas HTTP reais à API. |
| RF17 | CT28 | `BebidaControllerIntegrationTest` (201 e 409). |
| RF18 | CT30 | `BebidaControllerIntegrationTest` (PUT, DELETE e 404). |
| RF19 | CT29 | `BebidaControllerIntegrationTest` e Bean Validation. |
| RF20 | CT31 | `PedidoControllerIntegrationTest` e chamadas HTTP reais. |
| RF21 | CT31 | `PedidoControllerIntegrationTest` (total e cópia de itens). |
| RF22 | CT32, CT33, CT34, CT35 | `PedidoControllerIntegrationTest`. |
| RF23 | CT36 | `PedidoControllerIntegrationTest`. |
| RF24 | CT37 | `PedidoControllerIntegrationTest`. |
| RF25 | CT40, CT41 | `tests/api.test.js` e E2E do menu com API/fallback. |
| RF26 | CT42 | E2E de criação de pedido e inspeção do tratamento de erro sem fallback silencioso. |
| RF27 | CT42 | E2E de histórico, status e limpeza. |
| RF28 | CT43 | `CorsIntegrationTest`. |
| RF29 | CT40 | `tests/api.test.js` (erro HTTP, conexão e timeout). |
| RF30 | CT45 | Docker Compose + perfil PostgreSQL + reinício da API. |
| RF31 | CT46 | Workflow validado como YAML, executado localmente e aprovado no GitHub Actions. |

### 7.2 Requisitos não funcionais

| Requisito | Casos de teste | Evidência |
|---|---|---|
| RNF01 | CT24 | Navegador em 390, 768 e 1366 px. |
| RNF02 | CT25 | Navegação por teclado, foco, rótulos e regiões `aria-live`. |
| RNF03 | CT25, CT26 | Inspeção de HTML/DOM e checagens estáticas. |
| RNF04 | CT04, CT06, CT08 | Testes de dados e cálculo em centavos. |
| RNF05 | CT08, CT17, CT19, CT23 | Testes com armazenamento simulado e verificação manual. |
| RNF06 | CT14, CT16 | Inspeção do formulário e ausência de dados reais. |
| RNF07 | CT26, CT40, CT47 | `npm test` com 65 testes aprovados. |
| RNF08 | CT26 | Abertura das páginas por `file://`. |
| RNF09 | CT24, CT25 | Verificação visual e de contraste. |
| RNF10 | CT26, CT47 | Documentos em `docs/`, `README.md` e `progresso.md`. |
| RNF11 | CT39 | `.\mvnw.cmd test`, `verify` e `-v` sem Maven global. |
| RNF12 | CT39, CT45 | H2 no padrão e PostgreSQL via Compose na porta 5433. |
| RNF13 | CT31 a CT37, CT43 | 31 testes JUnit/MockMvc aprovados. |
| RNF14 | CT38 | Newman com 18 requisições e 32 assertions aprovadas. |
| RNF15 | CT31 | Total em centavos calculado no servidor. |
| RNF16 | CT40, CT41, CT42 | 12 testes do cliente + E2E real. |
| RNF17 | CT44 | Relatório JaCoCo e gate no `verify`. |
| RNF18 | CT46 | `ci.yml` com jobs e artefatos; execução remota aprovada. |
| RNF19 | CT43 | CORS `Origin: null` restrito a `/api/**`. |
| RNF20 | CT41, CT42 | Fallback e mensagens de erro observados no E2E. |
| RNF21 | CT45 | Persistência após reinício no PostgreSQL. |

## 8. Critérios de aceitação do escopo atual

1. Catálogo, criação de pedido, histórico, status e limpeza funcionam pela API e
   mantêm fallback local quando a API não está disponível.
2. Nenhuma falha de criação de pedido em modo API é convertida silenciosamente em
   pedido local.
3. A suíte front-end tem 65 testes aprovados e a backend tem 31 testes aprovados.
4. A coleção Newman tem 18 requisições e 32 assertions aprovadas.
5. A cobertura JaCoCo atende ao gate de 80% de linhas e 50% de branches.
6. O PostgreSQL do Compose fica saudável na porta 5433 e preserva um pedido após
   reiniciar a API.
7. O workflow de CI é YAML válido, seus comandos foram executados localmente e os
   três jobs passaram no GitHub Actions.
8. A documentação registra integração, evidências, limitações e a próxima etapa.
9. O front-end continua abrindo por `file://` sem build ou servidor local.

## 9. Referências

- `README.md`
- `docs/plano-de-testes.md`
- `docs/relatorio-testes.md`
- `progresso.md`
- `backend/postman/CafeNaMao.postman_collection.json`
- `.github/workflows/ci.yml`
