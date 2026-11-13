# Roteiro de apresentação — Café na Mão

**Duração sugerida:** 8 a 10 minutos de demonstração e 5 minutos de perguntas.

## 1. Preparação

Antes de começar:

- Abrir `menu.html` e `pedidos.html` no navegador.
- Deixar o backend parado para demonstrar o fallback local.
- Abrir em outra aba o workflow verde:
  `https://github.com/ArthurKayan/CafeNaMao/actions/runs/43423244358`.
- Abrir `backend/target/site/jacoco/index.html` após executar `.\mvnw.cmd verify`.
- Opcional: deixar o Docker Desktop iniciado para a demonstração do PostgreSQL.
- Limpar o histórico visual, se necessário, usando o próprio botão da interface.

## 2. Roteiro de demonstração

1. **Front-end sem backend**
   - Abrir `menu.html` e mostrar o badge de origem `local`.
   - Explicar que o catálogo usa `js/dados.js` e o carrinho/histórico usam
     `localStorage`.
2. **Fluxo principal**
   - Abrir os detalhes de uma bebida, testar quantidade inválida e depois adicionar
     uma quantidade válida.
   - Mostrar o total em centavos e o resumo do carrinho.
   - Em `pedidos.html`, escolher pagamento simulado e confirmar o pedido.
   - Abrir o histórico, alterar o status e recarregar para mostrar a persistência.
3. **Integração com a API**
   - Iniciar o backend em outro terminal:
     ```powershell
     cd backend
     .\mvnw.cmd spring-boot:run
     ```
   - Recarregar `menu.html` e mostrar o badge `API`.
   - Explicar que o servidor recalcula o preço e o total; o cliente envia apenas
     `bebidaId` e `quantidade`.
4. **Testes de API**
   - Mostrar a coleção Postman com CRUD, `{{base_url}}` e assertions.
   - Opcionalmente executar:
     ```powershell
     npx newman run backend/postman/CafeNaMao.postman_collection.json -e backend/postman/CafeNaMao.postman_environment.json
     ```
   - Informar o resultado: 18 requisições, 32 assertions e 0 falhas.
5. **CI e cobertura**
   - Mostrar os jobs verdes: Front-end, Backend e cobertura e Contrato
     Postman/Newman.
   - Abrir o relatório JaCoCo e destacar 94,31% de linhas e 70% de branches.
6. **Testes automatizados**
   - Mostrar `backend/src/test/java/com/cafenamao/pedido/PedidoServiceTest.java` e
     explicar o AAA e os valores-limite `0`, `1`, `10` e `11`.
   - Citar os 65 testes do front-end e os 40 do backend.
7. **Docker Compose opcional**
   - Explicar o perfil `testes`:
     ```powershell
     docker compose --profile testes run --rm testes-backend
     docker compose --profile testes run --rm testes-frontend
     ```
   - Explicar que o serviço `banco` usa PostgreSQL na porta 5433 e só é necessário
     para demonstrar persistência.

## 3. Explicação da arquitetura

- **Front-end estático:** HTML, CSS e JavaScript clássico, sem build e sem servidor.
- **Cliente HTTP `js/api.js`:** timeout, normalização e erros compreensíveis.
- **Fallback local:** catálogo e histórico continuam funcionando sem API; a criação
  de pedido em modo API não cai silenciosamente para o modo local.
- **Backend Spring Boot:** controllers, services, repositórios JPA, DTOs e tratamento
  centralizado de erros.
- **Preço:** inteiro em centavos; o servidor é a fonte de verdade.
- **Persistência:** H2 em memória no padrão e PostgreSQL via Compose no perfil
  `postgres`.
- **CI:** front-end, backend com JaCoCo e contrato Newman.

## 4. Decisões que podem ser questionadas

| Pergunta | Resposta curta |
|---|---|
| Por que H2 nos testes? | Elimina banco externo e deixa os testes reproduzíveis sem configuração. |
| Por que PostgreSQL no Compose? | Demonstra persistência real e separa desenvolvimento/teste de produção. |
| Por que existe fallback local? | O front-end precisa funcionar por `file://` mesmo sem backend. |
| Por que o CORS é amplo? | É configuração de desenvolvimento para `Origin: null`; não serve para produção. |
| Por que não há E2E versionado? | Foi uma lacuna consciente da Etapa 8; o roteiro E2E temporário validou o fluxo, mas não foi versionado por depender de Edge/Windows. |
| Por que o backend não está publicado? | O escopo acadêmico cobre API local; o GitHub Pages publica apenas o front-end. |
| Como o total é protegido? | O servidor ignora preço do cliente e recalcula com os preços atuais do catálogo. |
| O que acontece se a API falhar no checkout? | A interface mostra erro e mantém o carrinho; não cria pedido local silenciosamente. |

## 5. Encerramento

Resumir em uma frase: o projeto entrega o fluxo de pedidos, integração API/fallback,
testes unitários e de integração, contrato Newman, cobertura, CI verde e documentação
de escopo e limitações.
