# Requisitos — Café na Mão

**Versão:** 1.1  
**Data:** 10/11/2026  
**Escopo documentado:** Etapas 1 a 7 (front-end estático, carrinho, pagamento
simulado, histórico e API REST em Java/Spring Boot).  
**Fontes:** `progresso.md`, `README.md`, código em `js/`, `tests/`, `backend/src/`
e `backend/postman/`.

## 1. Objetivo

Registrar os requisitos funcionais e não funcionais implementados no Café na Mão,
servindo de base para o plano de testes, para o relatório de resultados e para a
evolução futura do projeto sem alterar o comportamento já validado. A versão 1.1
acrescenta os requisitos da API REST da Etapa 7, mantendo os identificadores
anteriores estáveis.

## 2. Contexto e atores

- **Cliente:** consulta bebidas, escolhe quantidade, monta o carrinho e confirma
  um pedido com pagamento simulado.
- **Consumidor da API:** consulta ou administra bebidas e pedidos por requisições
  HTTP, usando a API como fonte de verdade dos preços e do total.
- **Avaliador acadêmico:** verifica se o fluxo funciona, se os estados de interface
  são claros e se os testes e a documentação comprovam a qualidade.
- **Projeto acadêmico:** prioriza clareza, rastreabilidade, acessibilidade e
  demonstração de práticas de Garantia de Qualidade de Software.

## 3. Premissas do escopo atual

- O front-end roda por `file://`, sem servidor, build ou dependências npm.
- O backend roda separadamente com Spring Boot e Maven Wrapper.
- O perfil padrão do backend usa H2 em memória; existe perfil opcional para
  PostgreSQL, sem Docker Compose nesta etapa.
- Não há autenticação nem cobrança real.
- Preços são inteiros em centavos e nunca usam `float`.
- No backend, o servidor calcula o total a partir do preço atual da bebida; o
  cliente envia apenas `bebidaId` e `quantidade`.
- Carrinho e histórico do front-end usam `localStorage`; os pedidos da API usam JPA.
- O front-end ainda não consome a API REST.
- Nenhum dado real de cartão pode ser coletado, exibido ou armazenado.

## 4. Requisitos funcionais

| ID | Requisito | Critério de aceitação | Situação |
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

## 5. Requisitos não funcionais

| ID | Requisito | Critério de aceitação | Situação |
|---|---|---|---|
| RNF01 | Responsividade. | Páginas sem rolagem horizontal em 390, 768 e 1366 px. | Atendido |
| RNF02 | Acessibilidade. | Foco visível, navegação por teclado, rótulos, contraste e mensagens por `aria-live`. | Atendido com verificação manual |
| RNF03 | HTML semântico e DOM seguro. | Estrutura semântica, sem estilos inline, sem `href="#"` e dados inseridos com `textContent`/`createElement`. | Atendido |
| RNF04 | Precisão monetária. | Valores em centavos inteiros; formatação com `Intl.NumberFormat("pt-BR")`. | Atendido |
| RNF05 | Persistência local com fallback. | Carrinho e histórico usam `localStorage`; falhas de armazenamento são tratadas sem apagar o carrinho. | Atendido com ressalva |
| RNF06 | Privacidade do pagamento. | Nenhum número, validade ou código de cartão é solicitado, exibido ou salvo. | Atendido |
| RNF07 | Testabilidade. | Regras de domínio separadas em módulos testáveis com `node:test`; suíte sem dependências externas. | Atendido |
| RNF08 | Execução sem servidor. | O projeto abre por `file://` e não exige instalação de dependências. | Atendido |
| RNF09 | Identidade visual. | Paleta neobrutalista, bordas grossas, sombras duras e contraste legível. | Atendido |
| RNF10 | Documentação e rastreabilidade. | Requisitos, plano, relatório e progresso registram escopo, evidências e limitações. | Atendido |
| RNF11 | Execução do backend sem Maven global. | Maven Wrapper 3.9.9 permite `.\mvnw.cmd test` e `.\mvnw.cmd spring-boot:run` em ambiente com Java. | Atendido |
| RNF12 | Persistência configurável. | Perfil padrão usa H2 em memória; `application-postgres.properties` usa PostgreSQL com variáveis de ambiente. | Atendido com perfil PostgreSQL não executado |
| RNF13 | Testes de integração do backend. | JUnit 5 e MockMvc cobrem sucesso, validação, limites, 404, 409 e limpeza em 29 testes. | Atendido |
| RNF14 | Contrato de API documentado. | Coleção e ambiente Postman/Newman incluem operações principais e assertions. | Atendido (Newman aprovado) |
| RNF15 | Precisão monetária no backend. | Preços e totais usam inteiros em centavos; o servidor recalcula o total. | Atendido |

## 6. Fora do escopo atual

Não fazem parte das Etapas 1 a 7 e não devem ser considerados implementados:

- integração do front-end com a API REST;
- Docker Compose, provisionamento de PostgreSQL e autenticação;
- contas de usuário, administração e sincronização entre dispositivos;
- pagamento real, emissão de cobrança ou integração financeira;
- integração física com a máquina de bebidas;
- manutenção de máquinas e painel operacional;
- testes automatizados de DOM/E2E, cobertura JaCoCo e pipeline de CI.

## 7. Rastreabilidade

### 7.1 Requisitos funcionais

| Requisito | Casos de teste | Evidência automatizada ou manual |
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

### 7.2 Requisitos não funcionais

| Requisito | Casos de teste | Evidência |
|---|---|---|
| RNF01 | CT24 | Navegador em 390, 768 e 1366 px. |
| RNF02 | CT25 | Navegação por teclado, foco, rótulos e regiões `aria-live`. |
| RNF03 | CT25, CT26 | Inspeção de HTML/DOM e checagens estáticas. |
| RNF04 | CT04, CT06, CT08 | Testes de dados e cálculo em centavos. |
| RNF05 | CT08, CT17, CT19, CT23 | Testes com armazenamento simulado e verificação manual. |
| RNF06 | CT14, CT16 | Inspeção do formulário e ausência de dados reais. |
| RNF07 | CT26 | `npm test` com 53 testes aprovados. |
| RNF08 | CT26 | Abertura das páginas por `file://`. |
| RNF09 | CT24, CT25 | Verificação visual e de contraste. |
| RNF10 | CT26 | Documentos em `docs/`, `README.md` e `progresso.md`. |
| RNF11 | CT39 | `.\mvnw.cmd test` e `.\mvnw.cmd -v` sem Maven global. |
| RNF12 | CT39 | Perfil padrão H2; arquivo `application-postgres.properties` presente. |
| RNF13 | CT31, CT32, CT33, CT34, CT35, CT36, CT37 | 29 testes JUnit/MockMvc aprovados. |
| RNF14 | CT38 | Newman com 18 requisições e 32 assertions aprovadas, sem falhas. |
| RNF15 | CT31 | Total em centavos calculado no servidor. |

## 8. Critérios de aceitação do escopo atual

- As páginas principais abrem sem servidor e sem erro no console.
- O fluxo de catálogo, detalhe, carrinho, pagamento, confirmação e histórico é
  concluído em um navegador moderno.
- Os 53 testes do front-end passam com `npm test`.
- Os 29 testes de integração do backend passam com `.\mvnw.cmd test`.
- A API responde com os códigos HTTP documentados para sucesso e erro.
- Não há dados reais de cartão no código, no armazenamento ou na interface.
- Layout sem rolagem horizontal nas larguras verificadas.
- As limitações e o que ainda não existe estão documentados sem ambiguidade.

## 9. Referências

- [README](../README.md)
- [Plano de testes](plano-de-testes.md)
- [Relatório de testes](relatorio-testes.md)
- [Progresso do projeto](../progresso.md)
