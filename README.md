# Café na Mão

Aplicação acadêmica de pedidos de bebidas para uma máquina de café. O projeto
simula o fluxo de escolher uma bebida, montar o carrinho, pagar de forma fictícia,
confirmar o pedido e acompanhar o histórico. A partir da Etapa 7 também existe uma
API REST em Java/Spring Boot com testes de integração.

**Status atual:** Etapas 1 a 7 concluídas. O front-end continua funcionando sem
build, sem servidor local e sem dependências npm. O backend roda com Maven Wrapper,
H2 em memória por padrão e perfil opcional para PostgreSQL.

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

## Stack

### Front-end

- HTML semântico, CSS e JavaScript puro.
- Scripts clássicos com namespaces globais, sem módulos ES e sem framework.
- Testes com o executor nativo do Node.js (`node --test`).
- Persistência local com `localStorage`.

### Backend

- Java 21 (o ambiente local pode compilar com versão mais nova).
- Spring Boot 3.4.5, Spring Web, Validation e Data JPA.
- Maven Wrapper 3.9.9 (`mvnw.cmd`), sem exigir Maven instalado.
- H2 em memória para desenvolvimento e testes.
- Perfil `postgres` opcional para PostgreSQL.
- Testes de integração com JUnit 5 e MockMvc.
- Coleção Postman/Newman em `backend/postman/`.

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
│   ├── menu.js
│   └── pedidos.js
├── tests/
│   ├── dados.test.js
│   ├── detalhes.test.js
│   ├── carrinho.test.js
│   └── historico.test.js
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

O projeto também funciona por `file://`. Para carregar os dados de demonstração do
catálogo, abra `menu.html?estado=vazio` ou `menu.html?estado=erro`. Os parâmetros
valem apenas para a primeira carga; “Tentar novamente” recarrega o catálogo normal.

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
.\mvnw.cmd -DskipTests package
java -jar target\cafe-na-mao-api-0.1.0.jar
```

Para usar PostgreSQL, crie um banco e informe as variáveis:

```powershell
cd backend
$env:DB_URL = "jdbc:postgresql://localhost:5432/cafenamao"
$env:DB_USER = "cafenamao"
$env:DB_PASSWORD = "cafenamao"
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=postgres
```

Os valores acima são apenas exemplos locais de desenvolvimento. Não há segredos
reais nem Docker Compose nesta etapa.

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

Execute todos os testes na raiz:

```powershell
npm test
```

Execute um arquivo específico:

```powershell
node --test tests/historico.test.js
```

### Backend

```powershell
cd backend
.\mvnw.cmd test
```

A suíte atual tem **53 testes do front-end** e **29 testes de integração do backend**,
totalizando **82 testes automatizados aprovados**. Não há dependências npm além do
próprio Node.js para o front-end.

### API com Postman/Newman

A coleção e o ambiente estão em `backend/postman/`. Com a API rodando:

```powershell
npx newman run backend/postman/CafeNaMao.postman_collection.json -e backend/postman/CafeNaMao.postman_environment.json
```

O Newman é uma ferramenta opcional de linha de comando e não é dependência do
projeto. A mesma coleção pode ser importada no Postman. A execução validada tem
18 requisições e 32 assertions aprovadas.

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
- O front-end ainda não consome a API REST; carrinho e histórico continuam no
  `localStorage` até a integração ser autorizada.
- O backend usa H2 em memória por padrão; o perfil PostgreSQL existe, mas não há
  Docker Compose nem banco provisionado nesta etapa.
- Não há autenticação, contas de usuário, sincronização entre dispositivos ou
  progressão automática de status.
- Não há JaCoCo, testes E2E/DOM automatizados nem pipeline de CI nesta etapa.
- Não há integração física com a máquina de bebidas.

## Uso de IA

O projeto foi desenvolvido com apoio de IA (OpenCode/DeepSeek) para implementação,
revisão e documentação. As alterações foram conferidas manualmente, testadas com
`npm test`, `.\mvnw.cmd test` e verificadas por chamadas HTTP reais à API. O uso de
IA não substitui a validação do código nem a conferência dos resultados.

## Próximos passos

As próximas etapas previstas são a integração do front-end com a API REST, a
cobertura com JaCoCo, o Docker Compose, os testes de API no fluxo de CI e a
automação com GitHub Actions. Essas etapas só devem começar após autorização
explícita.
