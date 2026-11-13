# Café na Mão

Aplicação acadêmica de pedidos de bebidas para uma máquina de café. O projeto
simula o fluxo de escolher uma bebida, montar o carrinho, pagar de forma fictícia,
confirmar o pedido e acompanhar o histórico. O front-end é estático e o backend é
uma API REST em Java/Spring Boot.

**Status atual:** Etapas 1 a 8 concluídas e CI aprovado. O front-end consome a API
quando ela está disponível e mantém fallback local por `file://`. O backend usa H2
em memória por
padrão, tem perfil PostgreSQL com Docker Compose, cobertura JaCoCo e validação por
GitHub Actions.

## Funcionalidades

### Front-end

- Página inicial com navegação e apresentação do projeto.
- Catálogo com seis bebidas, descrições, categorias e preços.
- Estados de carregamento, catálogo vazio e erro com nova tentativa.
- Detalhes da bebida em diálogo acessível.
- Quantidade de 1 a 10 e cálculo do total parcial.
- Carrinho com adição, alteração, remoção e total em centavos.
- Resumo do carrinho na página do menu.
- Pagamento simulado obrigatório: “Dinheiro” ou “Cartão fictício”.
- Histórico de pedidos persistido no navegador.
- Alteração de status: “Recebido”, “Em preparo” e “Finalizado”.
- Limpeza do histórico com confirmação.
- Catálogo, pedidos, status e limpeza integrados à API quando disponível.
- Fallback local automático quando a API não responde, com badge de origem.
- Layout responsivo e navegação por teclado.

### API REST

- CRUD de bebidas com validação de id, nome, descrição, categoria e preço.
- Criação de pedidos com cálculo do total no servidor.
- Validação de carrinho vazio, quantidade de 1 a 10, bebida existente,
  bebida repetida e forma de pagamento.
- Consulta de pedidos com filtro por status e ordenação do mais recente.
- Alteração de status, exclusão individual e limpeza do histórico.
- Erros padronizados com códigos HTTP coerentes.
- Carga inicial das seis bebidas do catálogo.
- CORS restrito a `/api/**` e compatível com `Origin: null` de `file://`.
- Respostas normalizadas para o cliente em `js/api.js`.

## Stack

### Front-end

- HTML semântico, CSS e JavaScript puro.
- Scripts clássicos com namespaces globais, sem módulos ES e sem framework.
- Testes com o executor nativo do Node.js (`node --test`).
- Persistência local com `localStorage`.
- Cliente HTTP com timeout, normalização e tratamento de erros em `js/api.js`.

### Backend

- Java 21 (o ambiente local pode compilar com versão mais nova).
- Spring Boot 3.4.5, Spring Web, Validation e Data JPA.
- Maven Wrapper 3.9.9 (`mvnw.cmd`), sem exigir Maven instalado.
- H2 em memória para desenvolvimento e testes.
- Perfil `postgres` para PostgreSQL via Docker Compose.
- Testes de integração com JUnit 5 e MockMvc.
- JaCoCo 0.8.12 com relatório e gate de cobertura.
- Coleção Postman/Newman em `backend/postman/`.

### Infraestrutura e CI

- PostgreSQL 16 Alpine em `compose.yaml`, com healthcheck e volume.
- GitHub Actions com jobs de front-end, backend/JaCoCo e contrato Newman.
- Artefatos de cobertura e JAR publicados pelo workflow.

## Estrutura

```text
CaféNaMão/
├── index.html
├── menu.html
├── pedidos.html
├── sobre.html
├── style.css
├── js/
│   ├── dados.js
│   ├── detalhes.js
│   ├── carrinho.js
│   ├── historico.js
│   ├── api.js
│   ├── menu.js
│   └── pedidos.js
├── tests/
│   ├── dados.test.js
│   ├── detalhes.test.js
│   ├── carrinho.test.js
│   ├── historico.test.js
│   └── api.test.js
├── backend/
│   ├── pom.xml
│   ├── mvnw
│   ├── mvnw.cmd
│   ├── src/main/java/com/cafenamao/
│   │   ├── bebida/
│   │   ├── pedido/
│   │   ├── comum/
│   │   └── config/
│   ├── src/main/resources/
│   ├── src/test/java/com/cafenamao/
│   └── postman/
├── docs/
│   ├── requisitos.md
│   ├── plano-de-testes.md
│   └── relatorio-testes.md
├── .github/workflows/ci.yml
├── compose.yaml
├── package.json
├── progresso.md
└── README.md
```

## Como executar o front-end

Não é necessário instalar dependências nem iniciar um servidor. Abra o arquivo
`index.html` diretamente no navegador.

No Windows/PowerShell:

```powershell
start index.html
start menu.html
start pedidos.html
start sobre.html
```

Para usar a API, inicie o backend antes de abrir `menu.html` ou `pedidos.html`. O
badge de origem indica `API` ou `local`. Sem backend, o conteúdo local de
`js/dados.js` e o histórico do `localStorage` continuam funcionando.

A URL padrão da API é `http://localhost:8080/api`. Para usar outra porta ou host:

```powershell
# Query string
start "menu.html?api=http://localhost:9090/api"

# Ou antes de carregar js/api.js
window.CAFE_NA_MAO_API_URL = "http://localhost:9090/api";
```

Para carregar os dados de demonstração do catálogo, abra `menu.html?estado=vazio`
ou `menu.html?estado=erro`. Os parâmetros valem apenas para a primeira carga;
“Tentar novamente” recarrega o catálogo normal.

## Como executar o backend

Na pasta `backend`, use o Maven Wrapper. Na primeira execução ele baixa o Maven
3.9.9 automaticamente.

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

A API fica disponível em `http://localhost:8080`. O console do H2 fica em
`http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:cafenamao`, usuário `sa`,
senha vazia).

Para gerar e executar o JAR:

```powershell
cd backend
.\mvnw.cmd verify
java -jar target\cafe-na-mao-api-0.1.0.jar
```

### PostgreSQL com Docker Compose

Na raiz:

```powershell
docker compose up -d
docker compose ps
```

O Compose publica o PostgreSQL na porta host **5433** por padrão, para evitar
conflito com um PostgreSQL nativo que possa ocupar a `5432`. O backend usa a mesma
porta no perfil `postgres`:

```powershell
cd backend
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=postgres
```

Para usar outro banco, sobrescreva:

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5433/cafenamao"
$env:DB_USER = "cafenamao"
$env:DB_PASSWORD = "cafenamao"
$env:DB_PORT = "5433"
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=postgres
```

As credenciais são apenas valores locais de desenvolvimento. Não há segredos reais.
Para encerrar o banco sem apagar o volume:

```powershell
docker compose down
```

## API REST

| Método | Caminho | Finalidade |
|---|---|---|
| `GET` | `/api/bebidas` | Listar bebidas ordenadas por nome |
| `GET` | `/api/bebidas/{id}` | Consultar uma bebida |
| `POST` | `/api/bebidas` | Criar bebida |
| `PUT` | `/api/bebidas/{id}` | Atualizar bebida |
| `DELETE` | `/api/bebidas/{id}` | Excluir bebida |
| `GET` | `/api/pedidos` | Listar pedidos; aceita `?status=` |
| `GET` | `/api/pedidos/{numero}` | Consultar um pedido |
| `POST` | `/api/pedidos` | Criar pedido com pagamento simulado |
| `PATCH` | `/api/pedidos/{numero}/status` | Alterar status do pedido |
| `DELETE` | `/api/pedidos/{numero}` | Excluir um pedido |
| `DELETE` | `/api/pedidos` | Limpar o histórico de pedidos |

Exemplo de criação de pedido:

```json
{
  "pagamento": "dinheiro",
  "itens": [
    { "bebidaId": "espresso-curto", "quantidade": 2 },
    { "bebidaId": "cappuccino", "quantidade": 1 }
  ]
}
```

O servidor consulta os preços atuais das bebidas e calcula o total. O cliente não
envia preço. Valores monetários continuam inteiros em centavos. Nenhum dado real de
cartão é solicitado ou armazenado.

## Testes

### Front-end

```powershell
npm test
node --test tests/api.test.js
```

### Backend

```powershell
cd backend
.\mvnw.cmd test
.\mvnw.cmd verify
```

O `verify` executa os testes, gera o JAR e aplica o gate JaCoCo de **80% de linhas**
e **50% de branches**. A medição atual é **93,59% de linhas** e **62,50% de
branches**.

A suíte tem **65 testes do front-end** e **31 testes de backend**, totalizando
**96 testes automatizados aprovados**. Não há dependências npm além do próprio
Node.js para o front-end.

### API com Postman/Newman

A coleção e o ambiente estão em `backend/postman/`. Com a API rodando:

```powershell
npx newman run backend/postman/CafeNaMao.postman_collection.json -e backend/postman/CafeNaMao.postman_environment.json
```

O Newman é uma ferramenta opcional de linha de comando e não é dependência do
projeto. A execução validada tem **18 requisições, 32 assertions e 0 falhas**.

### Integração no navegador

A Etapa 8 foi validada com Edge headless via CDP contra a API real. O roteiro
cobriu catálogo, criação de pedido, status persistido, limpeza do histórico,
fallback local e console sem erros. A página Sobre também foi conferida em 390,
768 e 1366 px, sem rolagem horizontal. Os scripts auxiliares foram temporários e
não foram versionados.

## CI

`.github/workflows/ci.yml` executa:

1. front-end com Node 24 e `npm test`;
2. backend com Java 21 e `./mvnw -B verify`, publicando JaCoCo e o JAR;
3. contrato com o JAR, espera da API e execução do Newman na mesma etapa, com
   encerramento do processo Java ao final.

O arquivo foi validado como YAML e o job de contrato foi reproduzido localmente
com Git Bash: 18 requisições, 32 assertions e porta 8080 liberada. O workflow foi
executado no GitHub Actions em 13/11/2026 e os três jobs passaram (run
[43422700883](https://github.com/ArthurKayan/CafeNaMao/actions/runs/43422700883)).

## Documentação

- [Requisitos](docs/requisitos.md)
- [Plano de testes](docs/plano-de-testes.md)
- [Relatório de testes](docs/relatorio-testes.md)
- [Progresso do projeto](progresso.md)

## Acessibilidade e responsividade

O front-end usa HTML semântico, rótulos associados aos campos, foco visível,
navegação por teclado, `aria-live` para mensagens e suporte a
`prefers-reduced-motion`. As páginas foram verificadas em larguras de 390, 768 e
1366 px, sem rolagem horizontal.

## Limitações e escopo

- O pagamento é apenas simulado. Nenhum dado real de cartão é solicitado ou
  armazenado.
- O fallback local e a API não são sincronizados automaticamente: ao usar o modo
  local, os dados ficam no `localStorage` daquele navegador.
- O H2 é em memória e perde os dados ao reiniciar. O perfil PostgreSQL via Compose
  é a opção para validar persistência.
- O CORS libera `Origin: null` para desenvolvimento com `file://`; não é uma
  configuração para produção pública.
- Não há autenticação, contas de usuário, sincronização entre dispositivos ou
  progressão automática de status.
- Não há testes de DOM/E2E versionados nem matriz de navegadores. A integração foi
  conferida com script temporário via CDP no Edge.
- Não há integração física com a máquina de bebidas.

## Uso de IA

O projeto foi desenvolvido com apoio de IA (OpenCode/DeepSeek) para implementação,
revisão e documentação. As alterações foram conferidas manualmente, testadas com
`npm test`, `.\mvnw.cmd verify` e Newman, além de chamadas HTTP reais e verificação
no navegador. O uso de IA não substitui a validação do código nem a conferência dos
resultados.

## Próximos passos

As próximas etapas dependem de autorização explícita. Candidatos: versionar um teste
E2E estável, ampliar acessibilidade e revisar a configuração para um ambiente
publicado.
