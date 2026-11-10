# Café na Mão

Aplicação acadêmica de pedidos de bebidas para uma máquina de café. O projeto
simula o fluxo de escolher uma bebida, montar o carrinho, pagar de forma fictícia,
confirmar o pedido e acompanhar o histórico.

**Status atual:** Etapas 1 a 6 concluídas. O front-end funciona sem build, sem
servidor local e sem dependências npm. A Etapa 6 consolidou a documentação, a
página Sobre e o planejamento de testes.

## Funcionalidades

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

## Stack

- HTML semântico, CSS e JavaScript puro.
- Scripts clássicos com namespaces globais, sem módulos ES e sem framework.
- Testes com o executor nativo do Node.js (`node --test`).
- Persistência local com `localStorage`.
- Sem backend, banco de dados, Docker Compose ou pipeline de CI nesta fase.

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
├── docs/
│   ├── requisitos.md
│   ├── plano-de-testes.md
│   └── relatorio-testes.md
├── package.json
├── progresso.md
└── README.md
```

## Como executar

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

## Testes

Execute todos os testes:

```powershell
npm test
```

Execute um arquivo específico:

```powershell
node --test tests/historico.test.js
```

Verifique a sintaxe de um arquivo alterado:

```powershell
node --check js/pedidos.js
```

A suíte atual tem **53 testes aprovados** (dados, detalhes, carrinho e histórico).
Não há dependências npm além do próprio Node.js; `npm install` não é necessário.

## Documentação

- [Requisitos](docs/requisitos.md)
- [Plano de testes](docs/plano-de-testes.md)
- [Relatório de testes](docs/relatorio-testes.md)
- [Progresso do projeto](progresso.md)

## Acessibilidade e responsividade

O projeto usa HTML semântico, rótulos associados aos campos, foco visível,
navegação por teclado, `aria-live` para mensagens e suporte a
`prefers-reduced-motion`. As páginas foram verificadas em larguras de 390, 768 e
1366 px, sem rolagem horizontal.

## Limitações e escopo

- O pagamento é apenas simulado. Nenhum dado real de cartão é solicitado ou
  armazenado.
- Carrinho e histórico ficam no `localStorage` do navegador; não há sincronização
  entre dispositivos nem conta de usuário.
- Se o armazenamento local estiver bloqueado, o histórico não pode ser salvo e a
  conclusão do pedido é interrompida para não perder o carrinho.
- O status é alterado manualmente; não há progressão automática.
- Não há backend Java/Spring, API REST, banco de dados, Docker Compose ou CI.
- Não há testes automatizados de DOM ou de API nesta fase; essas verificações são
  manuais e estão registradas no relatório de testes.

## Uso de IA

O projeto foi desenvolvido com apoio de IA (OpenCode/DeepSeek) para implementação,
revisão e documentação. As alterações foram conferidas manualmente, testadas com
`npm test` e verificadas no navegador. O uso de IA não substitui a validação do
código nem a conferência dos resultados.

## Próximos passos

As próximas etapas previstas são a implementação do backend Java/Spring Boot, os
testes de API com Postman/Newman, a cobertura com JaCoCo e a automação de CI. Essas
etapas só devem começar após autorização explícita.
