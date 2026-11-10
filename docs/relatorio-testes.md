# Relatório de testes — Café na Mão

**Versão:** 1.1  
**Data:** 10/11/2026  
**Escopo:** Etapas 1 a 7 — front-end, documentação, backend, API REST e testes de integração.  
**Plano relacionado:** [plano-de-testes.md](plano-de-testes.md)

## 1. Resumo executivo

- **82 testes automatizados aprovados**, 0 reprovados, 0 ignorados: 53 no
  front-end e 29 no backend.
- Verificação de sintaxe sem erros nos arquivos JavaScript e de teste.
- Checagens estáticas de IDs, referências ARIA, links internos, `href="#"` e
  estilos inline aprovadas.
- Fluxo principal verificado no Microsoft Edge 156 em modo headless, controlado
  por CDP, nas larguras de 390, 768 e 1366 px.
- Nenhum erro de JavaScript no console durante os fluxos verificados.
- Falha simulada de gravação no histórico manteve o carrinho e bloqueou a
  confirmação, conforme previsto em CT16.
- API REST validada por testes JUnit 5/MockMvc e por execução HTTP real com H2.
- Coleção Postman executada com Newman: 18 requisições, 0 falhas e 32
  assertions aprovadas.

As Etapas 1 a 7 estão aprovadas dentro do escopo atual. As lacunas de integração
do front-end com a API, cobertura, Docker Compose e CI estão registradas na
seção 7.

## 2. Ambiente de execução

| Item | Valor |
|---|---|
| Sistema operacional | Windows |
| Node.js | 24.11.1 |
| Navegador | Microsoft Edge 156 (headless via CDP) |
| Execução das páginas | `file://` |
| Dependências npm | Nenhuma |
| Java | 22.0.2 (compilação com `--release 21`) |
| Spring Boot | 3.4.5 |
| Maven | Wrapper 3.9.9 |
| Banco padrão | H2 2.3.232 em memória |
| Banco alternativo | PostgreSQL (perfil não executado) |
| Testes da API | JUnit 5 + MockMvc |
| Contrato de API | Postman/Newman 6.2.3 |
| Data da verificação | 10/11/2026 |

## 3. Testes automatizados

Comando principal:

```powershell
npm test
```

Resultado:

```text
tests 53
pass 53
fail 0
cancelled 0
skipped 0
```

Distribuição:

| Arquivo | Testes | Foco |
|---|---:|---|
| `tests/dados.test.js` | 10 | Catálogo, unicidade, preços e formatação |
| `tests/detalhes.test.js` | 10 | Quantidade, limites e total parcial |
| `tests/carrinho.test.js` | 14 | Carrinho, totais, criação de pedido e persistência |
| `tests/historico.test.js` | 19 | Histórico, pagamento, status, corrupção e limpeza |
| **Total** | **53** | Regras de domínio e persistência |

Cobertura comportamental verificada:

- quantidades de 1 a 10 e rejeição de valores inválidos;
- cálculo e formatação de preços em centavos;
- adição, alteração, remoção, itens repetidos e limite por bebida;
- bloqueio de pedido com carrinho vazio;
- criação de pedido com cópia de itens e total;
- pagamento obrigatório e restrito às opções simuladas;
- rejeição de status, itens e totais inválidos;
- histórico vazio, ordenação, duplicidade e registros corrompidos;
- alteração de status e limpeza;
- comportamento com armazenamento indisponível.

### 3.1 Backend

Comando:

```powershell
cd backend
.\mvnw.cmd test
```

Resultado:

```text
BebidaControllerIntegrationTest: Tests run: 11, Failures: 0, Errors: 0
PedidoControllerIntegrationTest: Tests run: 18, Failures: 0, Errors: 0
Total: 29 testes aprovados
```

Distribuição:

| Arquivo | Testes | Foco |
|---|---:|---|
| `BebidaControllerIntegrationTest` | 11 | CRUD, validação, duplicidade, 404 e rota inexistente |
| `PedidoControllerIntegrationTest` | 18 | Criação, total, validação, status, filtro, exclusão e limpeza |
| **Total** | **29** | Contratos REST e regras de domínio |

Cobertura comportamental verificada:

- catálogo inicial com seis bebidas e consulta por id;
- criação, atualização e exclusão de bebidas, com 201/200/204;
- rejeição de bebida duplicada com 409 e de dados inválidos com 400;
- 404 para bebida, pedido e rota inexistentes;
- criação de pedido com total recalculado no servidor em centavos;
- rejeição de pagamento inválido, carrinho vazio, quantidade fora de 1 a 10 e
  bebida inexistente ou repetida;
- listagem ordenada, consulta por número, filtro por status e alteração de status;
- exclusão de pedido, limpeza do histórico e lista vazia.

### 3.2 Contrato Postman/Newman

Com a API em execução, a coleção foi executada com:

```powershell
npx --yes newman run postman\CafeNaMao.postman_collection.json `
  -e postman\CafeNaMao.postman_environment.json --reporters cli --color off
```

Resultado: **18 requisições executadas, 0 falhas e 32 assertions aprovadas**,
com duração de 3,1 s. A execução cobre bebidas e pedidos, incluindo 201, 200,
204, 400, 404 e 409.

## 4. Verificações estáticas

Foram executados:

```powershell
node --check js/dados.js
node --check js/detalhes.js
node --check js/carrinho.js
node --check js/historico.js
node --check js/menu.js
node --check js/pedidos.js
node --check tests/dados.test.js
node --check tests/detalhes.test.js
node --check tests/carrinho.test.js
node --check tests/historico.test.js
```

O comando `git diff --check` não pôde ser executado nesta sessão porque a pasta
não contém um repositório Git inicializado (`fatal: not a git repository`).

Também foi executada uma checagem auxiliar temporária, não versionada, que
confirmou:

- todos os IDs usados por `getElementById` existem nas páginas correspondentes;
- não há IDs duplicados nas páginas verificadas;
- referências de `aria-labelledby` e `aria-describedby` apontam para IDs válidos;
- `js/historico.js` é carregado na ordem correta em `pedidos.html`;
- não há `href="#"` nem estilos inline;
- os links internos das páginas apontam para arquivos existentes.

## 5. Verificação no navegador

### 5.1 Página inicial e página Sobre

| Cenário | Resultado |
|---|---|
| `index.html` com navegação ativa em “Início” | Aprovado |
| Link para `menu.html`, `pedidos.html` e `sobre.html` | Aprovado |
| `sobre.html` com navegação ativa em “Sobre” | Aprovado |
| Conteúdo da página Sobre: proposta, pagamento e qualidade | Aprovado |
| Três cards de contexto e três blocos de escopo | Aprovado |
| Chamada para o menu | Aprovado |
| Larguras 390, 768 e 1366 px | Sem rolagem horizontal |
| Console | Sem erros de JavaScript |

### 5.2 Fluxo de catálogo, carrinho e pagamento

| Cenário | Resultado |
|---|---|
| Catálogo em 390/768/1366 px | Sem rolagem horizontal |
| Detalhes com quantidade 3 | Total parcial correto e foco no campo |
| Adicionar ao pedido | Resumo atualizado no menu |
| Alterar quantidade e remover itens | Totais recalculados |
| Concluir sem pagamento | Bloqueado, carrinho mantido |
| Concluir com quantidade inválida | Bloqueado, foco no campo com erro |
| Concluir com pagamento escolhido | Pedido salvo no histórico e carrinho limpo |
| Recarregar após confirmação | Histórico persistente e carrinho vazio |
| Alterar status | Etiqueta e armazenamento atualizados |
| Cancelar limpeza do histórico | Histórico mantido |
| Confirmar limpeza | Histórico vazio e foco no título |
| Histórico em 390/768/1366 px | Sem rolagem horizontal |
| Falha simulada ao salvar histórico | Confirmação bloqueada e carrinho preservado |
| Console nos fluxos | Nenhum erro de JavaScript |

As capturas de tela da verificação foram geradas em diretório temporário e não
foram adicionadas ao repositório.

### 5.3 API em execução

O JAR foi iniciado com `java -jar target\cafe-na-mao-api-0.1.0.jar` e validado
por chamadas HTTP reais:

| Verificação | Resultado |
|---|---|
| `GET /api/bebidas` | 6 bebidas na carga inicial |
| `POST /api/pedidos` com 2 espressos e 1 cappuccino | `201`, número `PED-D103D8F7`, total `1550` e status `recebido` |
| `PATCH /api/pedidos/{numero}/status` | Status alterado para `em-preparo` |
| `GET /api/pedidos?status=em-preparo` | 1 pedido retornado |
| `GET /api/bebidas/nao-existe` | `404` com erro padronizado |
| `DELETE /api/pedidos` | `204` e histórico vazio |
| `GET /api/pedidos` após a limpeza | Lista vazia |

O processo Java foi encerrado ao final e a porta 8080 ficou livre.

## 6. Situação dos casos planejados

| Caso | Situação | Evidência principal |
|---|---|---|
| CT01 | Aprovado | Verificação manual da página inicial e navegação |
| CT02 | Aprovado | Catálogo verificado nas Etapas 2 e 3 |
| CT03 | Aprovado | Estados `vazio` e `erro` verificados na Etapa 2 |
| CT04 | Aprovado | `tests/dados.test.js` |
| CT05 | Aprovado | Verificação manual do diálogo e foco na Etapa 3 |
| CT06 | Aprovado | `tests/detalhes.test.js` |
| CT07 | Aprovado | Testes de detalhes e verificação manual |
| CT08 | Aprovado | Testes do carrinho e verificação no menu/pedidos |
| CT09 | Aprovado | `tests/carrinho.test.js` |
| CT10 | Aprovado | Resumo do menu verificado na Etapa 4 |
| CT11 | Aprovado | Testes do carrinho e interface de pedidos |
| CT12 | Aprovado | Testes do carrinho e estado vazio |
| CT13 | Aprovado | Verificação no navegador da Etapa 5 |
| CT14 | Aprovado | Inspeção das opções e textos de privacidade |
| CT15 | Aprovado | Fluxo completo de confirmação da Etapa 5 |
| CT16 | Aprovado | Falha de armazenamento simulada na Etapa 6 |
| CT17 | Aprovado | Recarregamento após confirmação |
| CT18 | Aprovado | `tests/historico.test.js` e verificação manual |
| CT19 | Aprovado | `tests/historico.test.js` |
| CT20 | Aprovado | Histórico em 390/768/1366 px |
| CT21 | Aprovado | Testes de histórico e alteração na interface |
| CT22 | Aprovado | `tests/historico.test.js` |
| CT23 | Aprovado | Testes de histórico e diálogo de limpeza |
| CT24 | Aprovado | Página Sobre, menu e pedidos em três larguras |
| CT25 | Aprovado | Verificação manual de teclado, foco e rótulos |
| CT26 | Aprovado | Suíte, checagens estáticas e execução por `file://` |
| CT27 | Aprovado | `BebidaControllerIntegrationTest` e chamadas HTTP reais |
| CT28 | Aprovado | `BebidaControllerIntegrationTest` (201 e 409) |
| CT29 | Aprovado | `BebidaControllerIntegrationTest` (400 e campos inválidos) |
| CT30 | Aprovado | `BebidaControllerIntegrationTest` (PUT/DELETE e 404) |
| CT31 | Aprovado | `PedidoControllerIntegrationTest` e HTTP real (total 1550) |
| CT32 | Aprovado | `PedidoControllerIntegrationTest` (pagamento inválido) |
| CT33 | Aprovado | `PedidoControllerIntegrationTest` (carrinho vazio) |
| CT34 | Aprovado | `PedidoControllerIntegrationTest` (quantidade 0 e 11) |
| CT35 | Aprovado | `PedidoControllerIntegrationTest` (bebida ausente/repetida) |
| CT36 | Aprovado | `PedidoControllerIntegrationTest` (status e filtro) |
| CT37 | Aprovado | `PedidoControllerIntegrationTest` (exclusão e limpeza) |
| CT38 | Aprovado | Newman: 18 requisições e 32 assertions sem falhas |
| CT39 | Aprovado | Wrapper, 29 testes e JAR gerado sem Maven global |

## 7. Limitações e lacunas

- Não há testes automatizados de DOM ou E2E no repositório; a interface é
  verificada manualmente com apoio de scripts temporários.
- A falha de armazenamento foi simulada substituindo a API de escrita, não por
  esgotamento real de cota do navegador.
- Não há verificação em múltiplos navegadores além do Edge.
- Não há leitor de tela real na validação; a acessibilidade foi conferida por
  foco, teclado, rótulos, semântica e regiões `aria-live`.
- O front-end ainda não consome a API; carrinho e histórico continuam no
  `localStorage`.
- O perfil PostgreSQL não foi executado porque o Docker daemon não estava em
  execução; apenas o perfil H2 foi validado.
- O H2 é em memória: reiniciar a API recria o catálogo e apaga os pedidos.
- Não há JaCoCo, Docker Compose, CI ou testes automatizados de DOM/E2E.
- Não há métrica de cobertura de código; a cobertura atual é descrita por
  comportamento e por arquivos de teste.
- A pasta não é um repositório Git nesta sessão, então `git diff --check` não foi
  executado.
- O plano de testes e o relatório não substituem uma nova execução após mudanças
  futuras no código.

## 8. Próximos passos recomendados

1. Integrar o front-end à API em etapa autorizada, preservando o fallback atual.
2. Adicionar cobertura de código com JaCoCo e publicar o relatório.
3. Provisionar PostgreSQL com Docker Compose e validar o perfil alternativo.
4. Adicionar testes automatizados de DOM/E2E para o fluxo principal.
5. Configurar GitHub Actions para rodar build, testes e cobertura.
6. Ampliar a verificação de acessibilidade e a matriz de navegadores.
7. Atualizar plano, relatório e `progresso.md` a cada nova etapa.

## 9. Conclusão

As Etapas 1 a 7 estão concluídas. O front-end continua funcional por `file://`
com 53 testes aprovados; o backend expõe a API REST de bebidas e pedidos com 29
testes de integração aprovados e validação HTTP real. A coleção Postman foi
executada com Newman sem falhas. A integração do front-end com a API, o perfil
PostgreSQL, JaCoCo, Docker Compose, CI e testes E2E permanecem como próximos
passos, fora do escopo desta etapa.
