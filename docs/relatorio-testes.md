# Relatório de testes — Café na Mão

**Versão:** 1.3
**Data:** 13/11/2026
**Escopo:** Etapas 1 a 8 — front-end integrado à API, backend, PostgreSQL,
cobertura, CI, documentação e adequação final à rubrica.
**Plano relacionado:** [plano-de-testes.md](plano-de-testes.md)

## 1. Resumo executivo

- **105 testes automatizados aprovados**, 0 reprovados, 0 ignorados: 65 no
  front-end e 40 no backend.
- **12 testes** cobrem o cliente HTTP, incluindo normalização, erros HTTP, falha de
  conexão e timeout.
- **2 testes** cobrem CORS com `Origin: null`.
- **9 testes unitários** cobrem `PedidoService` com Mockito, AAA explícito e
  valores-limite `0`, `1`, `10` e `11`.
- JaCoCo aprovado: **94,31% de linhas** (265 cobertas, 16 perdidas) e **70% de
  branches**; gate de 80% de linhas e 50% de branches atendido.
- Perfil `testes` do Docker Compose executou as suítes de front-end e backend em
  containers, sem banco externo.
- Newman: **18 requisições, 32 assertions e 0 falhas**.
- Integração validada no Edge headless via CDP: catálogo, criação de pedido,
  status, limpeza, fallback local e console sem erros.
- PostgreSQL via Docker Compose validado na porta 5433: pedido persistiu após
  reiniciar a API.
- Workflow de CI validado como YAML; job de contrato reproduzido localmente com
  18 requisições, 32 assertions e encerramento da API.
- Workflow executado no GitHub Actions com os três jobs aprovados (run
  43422700883).
- Página Sobre conferida em 390, 768 e 1366 px, sem rolagem horizontal.
- Verificação de sintaxe e `git diff --check` aprovados após ajuste de espaços.

As Etapas 1 a 8 estão aprovadas dentro do escopo. As lacunas restantes estão na
seção 7.

## 2. Ambiente de execução

| Item | Valor |
|---|---|
| Sistema operacional | Windows |
| Node.js | 24.16.0 |
| Navegador | Microsoft Edge 156 (headless via CDP) |
| Execução das páginas | `file://` |
| Dependências npm | Nenhuma no projeto |
| Java | 22.0.2 (compilação com `--release 21`) |
| Spring Boot | 3.4.5 |
| Maven | Wrapper 3.9.9 |
| Banco padrão | H2 2.3.232 em memória |
| Banco alternativo | PostgreSQL 16 Alpine via Docker Compose |
| Porta host do PostgreSQL | 5433 |
| Docker / Compose | 29.2.1 / v5.1.0 |
| Testes da API | JUnit 5 + MockMvc |
| Cobertura | JaCoCo 0.8.12 |
| Contrato de API | Postman/Newman |
| Data da verificação | 13/11/2026 |

## 3. Testes automatizados

### 3.1 Front-end

Comando:

```powershell
npm test
```

Resultado:

```text
tests 65
pass 65
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
| `tests/api.test.js` | 12 | Cliente HTTP, normalização, erros, conexão e timeout |
| **Total** | **65** | Regras de domínio, persistência e integração |

Cobertura comportamental:

- quantidades de 1 a 10 e rejeição de valores inválidos;
- cálculo e formatação de preços em centavos;
- adição, alteração, remoção, itens repetidos e limite por bebida;
- bloqueio de pedido com carrinho vazio;
- criação de pedido com cópia de itens e total;
- pagamento obrigatório e restrito às opções simuladas;
- histórico vazio, ordenação, duplicidade e registros corrompidos;
- alteração de status e limpeza;
- comportamento com armazenamento indisponível;
- normalização de bebidas e pedidos da API;
- criação, listagem, filtro, status e limpeza via cliente HTTP;
- erro HTTP, falha de conexão, timeout e resposta inválida.

### 3.2 Backend

Comando:

```powershell
cd backend
.\mvnw.cmd -B verify
```

Resultado:

```text
BebidaControllerIntegrationTest: Tests run: 11, Failures: 0, Errors: 0
PedidoControllerIntegrationTest: Tests run: 18, Failures: 0, Errors: 0
CorsIntegrationTest: Tests run: 2, Failures: 0, Errors: 0
PedidoServiceTest: Tests run: 9, Failures: 0, Errors: 0
Total: 40 testes aprovados
```

Distribuição:

| Arquivo | Testes | Foco |
|---|---:|---|
| `BebidaControllerIntegrationTest` | 11 | CRUD, validação, duplicidade, 404 e rota inexistente |
| `PedidoControllerIntegrationTest` | 18 | Criação, total, validação, status, filtro, exclusão e limpeza |
| `CorsIntegrationTest` | 2 | CORS para `file://`, incluindo `Origin: null` |
| `PedidoServiceTest` | 9 | Regras de serviço, limites, duplicidade, overflow e status |
| **Total** | **40** | Contratos REST, regras de domínio, CORS e unidade |

### 3.3 Testes unitários e padrão AAA

`PedidoServiceTest` não sobe o Spring: usa Mockito para isolar `PedidoRepository` e
`BebidaRepository`. Cada teste separa explicitamente:

- **Arrange:** criação dos mocks, bebida e requisição;
- **Act:** chamada ao método do serviço;
- **Assert:** verificação do total, status, mensagem ou exceção.

Casos cobertos: quantidades `1` e `10` aceitas; `0` e `11` rejeitadas; bebida
repetida; bebida inexistente; overflow do total; atualização de status; e busca de
pedido inexistente. Os testes de integração também seguem a mesma ordem lógica, com
preparação da requisição, execução via `MockMvc` e verificações encadeadas.

### 3.4 Cobertura JaCoCo

O `verify` gerou o JAR, o relatório JaCoCo e executou a checagem mínima:

```text
Analyzed bundle 'cafe-na-mao-api' with 26 classes
All coverage checks have been met.
BUILD SUCCESS
```

| Métrica | Resultado | Gate |
|---|---:|---:|
| Linhas | 94,31% (265/281) | 80% |
| Branches | 70% (28/40) | 50% |

### 3.5 Contrato Postman/Newman

Com a API em execução:

```powershell
npx --yes newman run backend\postman\CafeNaMao.postman_collection.json `
  -e backend\postman\CafeNaMao.postman_environment.json `
  --reporters cli --color off
```

Resultado: **18 requisições executadas, 0 falhas e 32 assertions aprovadas**.
A execução cobre bebidas e pedidos, incluindo 201, 200, 204, 400, 404 e 409.

## 4. Verificações estáticas

```powershell
node --check js/api.js
node --check js/historico.js
node --check js/menu.js
node --check js/pedidos.js
node --check tests/api.test.js
git diff --check
npx --yes js-yaml .github/workflows/ci.yml
```

Resultado:

- sintaxe válida nos arquivos verificados;
- `git diff --check` sem erros após remover espaços finais do plano;
- workflow de CI válido como YAML;
- IDs, referências ARIA e links internos preservados pelas alterações;
- nenhuma dependência npm adicionada ao projeto.

## 5. Verificação no navegador

### 5.1 Regressão das Etapas 1 a 7

| Cenário | Resultado |
|---|---|
| `index.html`, `menu.html`, `pedidos.html` e `sobre.html` | Abrem por `file://` |
| Catálogo, detalhes, carrinho e pagamento | Fluxo preservado |
| Histórico, status e limpeza | Fluxo preservado |
| Página Sobre atualizada | 3 cards, 3 blocos de escopo, textos de API/JaCoCo/CI e nenhum link quebrado |
| Larguras 390, 768 e 1366 px | Sem rolagem horizontal |
| Console | Sem erros de JavaScript |

### 5.2 Integração com a API

| Cenário | Resultado |
|---|---|
| `menu.html` com API ativa | Badge `API`, 6 cards da API |
| `pedidos.html` com API ativa | Badge `API`, histórico sincronizado |
| Criar pedido com 2 Espressos | `PED-501ECE45`, total `R$ 8,00` |
| Alterar status | `em-preparo` persistido na API |
| Limpar histórico | API retornou lista vazia |
| `menu.html` sem API | Badge `local`, 6 cards locais |
| `pedidos.html` sem API | Histórico local e aviso de indisponibilidade |
| Console durante o E2E | Nenhum erro |

O roteiro foi executado por script temporário via CDP no Edge, fora do
repositório. Criação de pedido em modo API mantém o carrinho e exibe erro quando
a chamada falha, sem criar um pedido local silenciosamente.

### 5.3 PostgreSQL e persistência

| Verificação | Resultado |
|---|---|
| `docker compose config` | Válido |
| `docker compose up -d` | Container `healthy` na porta 5433 |
| Perfil `postgres` | 6 bebidas carregadas |
| Criar pedido | `PED-7D812006` |
| Reiniciar a API | Pedido continuou disponível |
| `DELETE /api/pedidos` | Histórico remoto vazio |
| `docker compose down` | Volume preservado |

O PostgreSQL nativo ocupava a porta 5432 e causou falha de autenticação no
primeiro teste. A porta host padrão do Compose e do perfil `postgres` foi alterada
para 5433.

### 5.4 CI local e GitHub Actions

O job de contrato foi ajustado para iniciar a API, aguardar `GET /api/bebidas`,
executar o Newman e encerrar o Java na mesma etapa. O fluxo foi repetido com Git
Bash fora do runner:

| Verificação | Resultado |
|---|---|
| YAML do workflow | Válido |
| Inicialização e espera da API | OK |
| Newman | 18 requisições, 32 assertions, 0 falhas |
| Encerramento da API | Porta 8080 liberada |
| Log temporário | Removido |
| GitHub Actions | Front-end, Backend e cobertura e Contrato Postman/Newman aprovados |

O workflow foi executado no GitHub Actions em 13/11/2026 (run
[43422700883](https://github.com/ArthurKayan/CafeNaMao/actions/runs/43422700883)),
com os três jobs concluídos com sucesso.

### 5.5 Execução das suítes via Docker Compose

Com o Docker em execução, os testes foram repetidos em containers sem instalar Java,
Maven ou Node no host:

```powershell
docker compose --profile testes run --rm testes-backend
docker compose --profile testes run --rm testes-frontend
```

| Serviço | Resultado |
|---|---|
| `testes-backend` | 40 testes, JAR gerado, gate JaCoCo atendido |
| `testes-frontend` | 65 testes aprovados |
| Banco externo | Não necessário; H2 em memória |

## 6. Situação dos casos planejados

| Caso | Situação | Evidência principal |
|---|---|---|
| CT01 a CT26 | Aprovados | Regressão das Etapas 1 a 6 e `npm test` com 65 testes |
| CT27 a CT39 | Aprovados | 31 testes de integração, HTTP real e Newman |
| CT40 | Aprovado | `tests/api.test.js` com 12 testes |
| CT41 | Aprovado | E2E do menu com API e fallback local |
| CT42 | Aprovado | E2E de pedido, status, limpeza e histórico local |
| CT43 | Aprovado | `CorsIntegrationTest` com `Origin: null` |
| CT44 | Aprovado | JaCoCo: 94,31% linhas e 70% branches |
| CT45 | Aprovado | PostgreSQL via Compose e persistência após reinício |
| CT46 | Aprovado | YAML válido, job local com 18/32 e três jobs aprovados no GitHub Actions |
| CT47 | Aprovado | 65 + 40 testes, Newman e documentação revisada |
| CT48 | Aprovado | 9 testes unitários e suítes executadas pelo perfil Compose `testes` |

## 7. Limitações e lacunas

- Não há testes de DOM/E2E versionados; a integração foi verificada com script
  temporário via CDP no Edge.
- A verificação de navegador foi feita apenas no Edge, sem matriz de navegadores.
- Não há leitor de tela real na validação; acessibilidade foi conferida por foco,
  teclado, rótulos, semântica e regiões `aria-live`.
- O workflow de CI passou no GitHub Actions, mas não há matriz de sistemas
  operacionais nem de versões do Node/Java.
- O CORS amplo para `Origin: null` é adequado ao desenvolvimento por `file://`, não
  a uma implantação pública.
- O fallback local e a API não são sincronizados automaticamente; dados locais e
  remotos podem divergir após uma indisponibilidade.
- O H2 é em memória: reiniciar a API recria o catálogo e apaga pedidos.
- A falha de armazenamento foi simulada substituindo a API de escrita, não por
  esgotamento real de cota do navegador.
- O plano e este relatório precisam ser atualizados a cada nova etapa.

## 8. Próximos passos recomendados

1. Versionar um teste E2E estável para o fluxo principal, se isso for autorizado.
2. Ampliar acessibilidade e a matriz de navegadores.
3. Revisar CORS e configuração para um eventual ambiente publicado.
4. Atualizar plano, relatório e `progresso.md` a cada nova etapa.

## 9. Conclusão

As Etapas 1 a 8 estão concluídas e validadas no CI. O front-end funciona por
`file://`, consome a API quando disponível e mantém fallback local. A suíte tem 65
testes de front-end e 40 de backend, incluindo 9 testes unitários com AAA; a
cobertura atende ao gate JaCoCo, o contrato Newman passa, o PostgreSQL via Compose
preserva dados, o perfil `testes` executa as suítes em containers, o workflow foi
aprovado no GitHub Actions e a página Sobre foi revisada. Os testes E2E versionados
permanecem como próximo passo.
