# Relatório de testes — Café na Mão

**Versão:** 1.0  
**Data:** 10/11/2026  
**Escopo:** Etapas 1 a 5, com verificação complementar da Etapa 6.  
**Plano relacionado:** [plano-de-testes.md](plano-de-testes.md)

## 1. Resumo executivo

- **53 testes automatizados aprovados**, 0 reprovados, 0 ignorados.
- Verificação de sintaxe sem erros nos arquivos JavaScript e de teste.
- Checagens estáticas de IDs, referências ARIA, links internos, `href="#"` e
  estilos inline aprovadas.
- Fluxo principal verificado no Microsoft Edge 156 em modo headless, controlado
  por CDP, nas larguras de 390, 768 e 1366 px.
- Nenhum erro de JavaScript no console durante os fluxos verificados.
- Falha simulada de gravação no histórico manteve o carrinho e bloqueou a
  confirmação, conforme previsto em CT16.

A Etapa 5 está aprovada dentro do escopo front-end. As lacunas de automação de
DOM, API, backend, cobertura e CI estão registradas na seção 6.

## 2. Ambiente de execução

| Item | Valor |
|---|---|
| Sistema operacional | Windows |
| Node.js | 24.16.0 |
| Navegador | Microsoft Edge 156 (headless via CDP) |
| Execução das páginas | `file://` |
| Dependências npm | Nenhuma |
| Backend/API | Não implementado |
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
git diff --check
```

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

## 7. Limitações e lacunas

- Não há testes automatizados de DOM ou E2E no repositório; a interface é
  verificada manualmente com apoio de scripts temporários.
- A falha de armazenamento foi simulada substituindo a API de escrita, não por
  esgotamento real de cota do navegador.
- Não há verificação em múltiplos navegadores além do Edge.
- Não há leitor de tela real na validação; a acessibilidade foi conferida por
  foco, teclado, rótulos, semântica e regiões `aria-live`.
- Não há backend, API, banco, Postman/Newman, JUnit, JaCoCo, Docker Compose ou CI.
- Não há métrica de cobertura de código; a cobertura atual é descrita por
  comportamento e por arquivos de teste.
- O plano de testes e o relatório não substituem uma nova execução após mudanças
  futuras no código.

## 8. Próximos passos recomendados

1. Implementar o backend Java/Spring Boot e os endpoints REST em etapa autorizada.
2. Criar testes JUnit 5/MockMvc e relatório de cobertura com JaCoCo.
3. Criar coleção Postman com assertions e execução via Newman.
4. Adicionar testes automatizados de DOM/E2E para o fluxo principal.
5. Configurar GitHub Actions para rodar build, testes e cobertura.
6. Ampliar a verificação de acessibilidade e a matriz de navegadores.
7. Atualizar plano, relatório e `progresso.md` a cada nova etapa.

## 9. Conclusão

O escopo front-end das Etapas 1 a 5 está funcional e coberto por 53 testes
automatizados, verificações estáticas e roteiros manuais de interface. A Etapa 6
foi concluída com requisitos, plano, relatório e página Sobre. O projeto ainda não
possui backend, API, banco ou CI; essas lacunas estão fora do escopo atual e
devem ser tratadas em etapas posteriores com autorização explícita.
