# PROGRESSO — Café na Mão

Última atualização: 03/11/2026

Status atual: **Etapas 1 e 2 concluídas. Próxima etapa: Etapa 3 — Detalhes e quantidade.**

Este arquivo registra o estado real do projeto, as decisões tomadas, os testes executados e o próximo passo. Uma nova sessão deve conseguir continuar a partir dele, lendo também `projeto 1/.agents/skills/cafenamao/SKILL.md`.

## Histórico do registro

O registro anterior (commit `c359b67`) foi preservado e ampliado. Ele documentava:

- primeira etapa de desenvolvimento realizada com auxílio do OpenCode;
- front-end inicial com `index.html`, `menu.html`, `pedidos.html`, `sobre.html` e `style.css`;
- projeto versionado no GitHub;
- ferramentas usadas: VS Code, OpenCode, DeepSeek v4.1 Flash, HTML, CSS, JavaScript, Git e GitHub;
- orientação para trabalhar em etapas, explicar em português, testar e não avançar sem autorização.

Essas orientações continuam válidas. O estado detalhado das Etapas 1 e 2 está nas seções seguintes.

## 1. Como retomar o projeto

1. Leia `PROGRESSO.md` e a skill `cafe-na-mao`.
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

5. Implemente somente a próxima etapa descrita na seção 9.
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
├── menu.html                  Catálogo de bebidas
├── pedidos.html               Vazio; será trabalhado na Etapa 4
├── sobre.html                 Vazio; será trabalhado na Etapa 6
├── style.css                  Estilos globais e responsivos
├── js/
│   ├── dados.js               Dados das bebidas e formatação de preço
│   └── menu.js                Renderização e estados do catálogo
├── tests/
│   └── dados.test.js          Testes unitários dos dados
├── package.json               Script de testes; sem dependências
├── README.md                  Descrição inicial, ainda resumida
├── PROGRESSO.md               Este registro
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

## 6. Decisões técnicas registradas

1. HTML, CSS e JavaScript puro nesta fase para reduzir complexidade e facilitar a explicação na apresentação.
2. Scripts clássicos com padrão de proteção e namespace global (`window.CafeNaMaoDados`) em vez de módulos ES. Isso permite abrir o projeto por `file://` sem servidor local.
3. Nenhum botão “Ver detalhes” foi adicionado ainda. Ele pertence à Etapa 3 e deve ter comportamento real; criar agora resultaria em interface sem função.
4. Preços são inteiros em centavos. A formatação usa `Intl.NumberFormat("pt-BR")` e a validação rejeita valores negativos, fracionários ou não numéricos.
5. O carregamento tem atraso simulado de 450 ms para permitir observar o estado de carregamento sem depender de rede.
6. O catálogo não usa `localStorage`; carrinho e pedidos serão tratados na Etapa 4.
7. Os cards são renderizados com criação de elementos DOM e `textContent`, não com interpolação direta de dados em `innerHTML`.
8. O cabeçalho é fixo apenas acima de 860 px. Em telas menores ele fica estático para não consumir grande parte da tela.
9. Testes usam o executor nativo do Node.js, sem dependências externas e sem `package-lock.json`.
10. Não há pagamento real. O rodapé e o texto do catálogo informam que o pagamento é simulado.

## 7. Verificações executadas

### Testes automatizados

Comando:

```powershell
npm test
```

Resultado: **10 testes aprovados, 0 reprovados, 0 ignorados**.

Cobertura atual dos testes:

- quantidade mínima de bebidas;
- unicidade dos identificadores;
- presença de dados obrigatórios;
- preços inteiros e positivos;
- formatação de moeda;
- limite inferior de preço zero;
- separador de milhar;
- rejeição de preço negativo, fracionário e não numérico.

Também foram executados:

```powershell
node --check js/dados.js
node --check js/menu.js
node --check tests/dados.test.js
git diff --check
```

Resultado: sem erros de sintaxe ou de whitespace. O Git apenas informou avisos de conversão LF/CRLF, que não são falhas.

### Verificação no navegador

Feita com Microsoft Edge em modo headless e emulação de viewport. Essas verificações foram executadas na sessão atual, mas os scripts auxiliares não foram adicionados ao repositório.

| Cenário | Resultado |
|---|---|
| `index.html` em 390 px | Sem rolagem horizontal; cabeçalho estático |
| `index.html` em 1366 px | Sem rolagem horizontal; cabeçalho fixo |
| `menu.html` em 390 px | 6 cards, primeiro preço `R$ 4,00`, sem rolagem horizontal |
| `menu.html` em 768 px | 6 cards, grade com 2 colunas, sem rolagem horizontal |
| `menu.html` em 1366 px | 6 cards, grade com 3 colunas, sem rolagem horizontal |
| Carregamento | 3 esqueletos e `aria-busy="true"`; depois 6 cards e `aria-busy="false"` |
| Lista vazia | 0 cards, mensagem visível e link “Voltar ao início” |
| Erro | 0 cards, mensagem visível e botão “Tentar novamente” |
| Nova tentativa | Após o clique, 6 cards renderizados e painel de erro oculto |

Também foram conferidos:

- todos os links internos apontam para arquivos existentes;
- não há `href="#"`;
- não há estilos inline;
- não há IDs duplicados dentro de uma mesma página.

## 8. Pendências e limitações conhecidas

- Os cards ainda não abrem detalhes nem permitem escolher quantidade; isso é a Etapa 3.
- `pedidos.html` e `sobre.html` continuam vazios.
- Não há carrinho, criação de pedido, histórico ou pagamento simulado.
- Não há backend Java, API REST, banco de dados ou Docker Compose.
- Não há plano de teste formal com 15 casos, coleção Postman/Newman, JUnit 5, JaCoCo ou GitHub Actions. Esses itens pertencem à Etapa 9.
- Não há testes automatizados de DOM no repositório; a verificação de interface foi feita manualmente com apoio de scripts temporários no Edge.
- O `README.md` ainda é resumido e deverá ser atualizado na etapa de documentação.
- Não existe arquivo de logo; a marca é textual.
- Nenhum dado real de cartão pode ser coletado ou armazenado.

## 9. Próxima etapa — Etapa 3: detalhes e quantidade

Objetivo: permitir abrir os detalhes de uma bebida, escolher uma quantidade válida e visualizar o total parcial. Não implementar o carrinho nesta etapa.

Tarefas previstas:

1. Adicionar a cada card um botão “Ver detalhes” com comportamento real.
2. Abrir um painel de detalhes acessível, preferencialmente com o elemento nativo `<dialog>`.
3. Mostrar nome, categoria, descrição, preço unitário e seletor de quantidade.
4. Definir limites de quantidade, por exemplo de 1 a 10, com validação e mensagem de erro.
5. Calcular o total parcial com inteiros em centavos e exibir em reais.
6. Permitir fechar com botão e tecla `Esc`, devolvendo o foco ao botão que abriu o painel.
7. Manter a interface responsiva e sem rolagem horizontal em celular e desktop.
8. Criar testes unitários para o cálculo do total e para quantidades inválidas.
9. Verificar o fluxo completo no navegador e atualizar este arquivo.

Arquivos prováveis: `menu.html`, `style.css`, `js/menu.js`, novo arquivo de detalhes se necessário, `tests/` e `PROGRESSO.md`.

Critérios de conclusão:

- o botão “Ver detalhes” abre o painel da bebida correta;
- quantidade abaixo do mínimo e acima do máximo é rejeitada com mensagem clara;
- o total parcial corresponde ao preço unitário multiplicado pela quantidade;
- teclado e leitor de tela conseguem operar e fechar o painel;
- não há erros no console nem rolagem horizontal;
- testes automatizados e verificação no navegador são registrados neste arquivo.

## 10. Checklist para a próxima sessão

- [ ] Ler a skill e este arquivo.
- [ ] Rodar `git status` e preservar alterações existentes.
- [ ] Executar `npm test` antes de começar.
- [ ] Implementar somente a Etapa 3.
- [ ] Executar testes unitários e verificação no navegador.
- [ ] Revisar o diff e procurar regressões nas Etapas 1 e 2.
- [ ] Atualizar `PROGRESSO.md` com resultados e nova próxima etapa.
- [ ] Não fazer commit ou push sem solicitação explícita do usuário.
