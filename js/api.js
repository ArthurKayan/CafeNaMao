(function (raiz) {
  "use strict";

  const emNode = typeof module !== "undefined" && module.exports;
  const historico = emNode ? require("./historico.js") : raiz.CafeNaMaoHistorico;
  const URL_PADRAO = "http://localhost:8080/api";
  const TIMEOUT_PADRAO = 2500;

  class ErroApi extends Error {
    constructor(mensagem, opcoes) {
      super(mensagem);
      const detalhes = opcoes || {};
      this.name = "ErroApi";
      this.codigo = detalhes.codigo || "erro-api";
      this.status = detalhes.status || 0;
      this.campos = Array.isArray(detalhes.campos) ? detalhes.campos : [];
    }
  }

  function atualizarUrlBase() {
    if (typeof window === "undefined") {
      return URL_PADRAO;
    }

    if (window.CAFE_NA_MAO_API_URL) {
      return String(window.CAFE_NA_MAO_API_URL);
    }

    const parametros = new URLSearchParams(window.location.search);
    const daUrl = parametros.get("api");
    return daUrl ? String(daUrl) : URL_PADRAO;
  }

  function normalizarBebida(bebida) {
    if (
      !bebida ||
      typeof bebida.id !== "string" ||
      bebida.id.trim() === "" ||
      typeof bebida.nome !== "string" ||
      bebida.nome.trim() === "" ||
      typeof bebida.descricao !== "string" ||
      typeof bebida.categoria !== "string" ||
      !Number.isInteger(bebida.precoCentavos) ||
      bebida.precoCentavos < 0
    ) {
      throw new ErroApi("A API retornou uma bebida inválida.", {
        codigo: "resposta-invalida",
      });
    }

    return {
      id: bebida.id,
      nome: bebida.nome,
      descricao: bebida.descricao,
      categoria: bebida.categoria,
      precoCentavos: bebida.precoCentavos,
    };
  }

  function normalizarPedido(pedido) {
    try {
      return historico.normalizarPedido({
        numero: pedido && pedido.numero,
        criadoEm: pedido && pedido.criadoEm,
        itens: Array.isArray(pedido && pedido.itens)
          ? pedido.itens.map(function (item) {
              return {
                id: item.bebidaId !== undefined ? item.bebidaId : item.id,
                nome: item.nome,
                precoCentavos: item.precoCentavos,
                quantidade: item.quantidade,
              };
            })
          : [],
        totalCentavos: pedido && pedido.totalCentavos,
        pagamento: pedido && pedido.pagamento,
        status: pedido && pedido.status,
      });
    } catch (erro) {
      throw new ErroApi("A API retornou um pedido inválido.", {
        codigo: "resposta-invalida",
      });
    }
  }

  function criarCliente(opcoes) {
    const configuracao = opcoes || {};
    const urlBase = String(configuracao.baseUrl || atualizarUrlBase()).replace(/\/+$/, "");
    const timeoutMs = configuracao.timeoutMs || TIMEOUT_PADRAO;
    const fetchImpl =
      configuracao.fetchImpl ||
      (typeof fetch === "function" ? fetch.bind(raiz) : null);

    async function requisitar(caminho, opcoesRequisicao) {
      if (!fetchImpl) {
        throw new ErroApi("A API não está disponível neste ambiente.", {
          codigo: "indisponivel",
        });
      }

      const opcoesFetch = Object.assign({}, opcoesRequisicao);
      const cabecalhos = Object.assign({}, opcoesFetch.headers || {});
      let corpo = opcoesFetch.body;

      if (corpo !== undefined && corpo !== null && typeof corpo !== "string") {
        corpo = JSON.stringify(corpo);
      }

      if (corpo !== undefined && corpo !== null) {
        cabecalhos["Content-Type"] = "application/json";
      }

      const controlador =
        typeof AbortController === "function" ? new AbortController() : null;

      let resposta;

      try {
        resposta = await new Promise(function (resolver, rejeitar) {
          let finalizado = false;
          const temporizador = setTimeout(function () {
            if (finalizado) {
              return;
            }

            finalizado = true;

            if (controlador) {
              controlador.abort();
            }

            rejeitar(
              new ErroApi("A API demorou mais que o esperado para responder.", {
                codigo: "timeout",
              })
            );
          }, timeoutMs);

          Promise.resolve(
            fetchImpl(
              urlBase + caminho,
              Object.assign({}, opcoesFetch, {
                headers: cabecalhos,
                body: corpo,
                signal: controlador ? controlador.signal : undefined,
              })
            )
          ).then(
            function (valor) {
              if (finalizado) {
                return;
              }

              finalizado = true;
              clearTimeout(temporizador);
              resolver(valor);
            },
            function (erro) {
              if (finalizado) {
                return;
              }

              finalizado = true;
              clearTimeout(temporizador);
              rejeitar(erro);
            }
          );
        });
      } catch (erro) {
        if (erro instanceof ErroApi) {
          throw erro;
        }

        if (erro && erro.name === "AbortError") {
          throw new ErroApi("A API demorou mais que o esperado para responder.", {
            codigo: "timeout",
          });
        }

        throw new ErroApi("Não foi possível conectar à API.", {
          codigo: "indisponivel",
        });
      }

      if (resposta.status === 204) {
        return null;
      }

      let corpoResposta = null;

      try {
        corpoResposta = await resposta.json();
      } catch (erro) {
        corpoResposta = null;
      }

      if (!resposta.ok) {
        throw new ErroApi(
          (corpoResposta && corpoResposta.mensagem) ||
            "A API não conseguiu concluir a operação.",
          {
            codigo: "http",
            status: resposta.status,
            campos: corpoResposta && corpoResposta.campos,
          }
        );
      }

      return corpoResposta;
    }

    async function listarBebidas() {
      const bebidas = await requisitar("/bebidas");

      if (!Array.isArray(bebidas)) {
        throw new ErroApi("A API retornou um catálogo inválido.", {
          codigo: "resposta-invalida",
        });
      }

      return bebidas.map(normalizarBebida);
    }

    async function criarPedido(pagamento, itens) {
      const resposta = await requisitar("/pedidos", {
        method: "POST",
        body: {
          pagamento: pagamento,
          itens: itens.map(function (item) {
            return {
              bebidaId: item.id,
              quantidade: item.quantidade,
            };
          }),
        },
      });

      return normalizarPedido(resposta);
    }

    async function listarPedidos(status) {
      const caminho =
        typeof status === "string" && status.trim() !== ""
          ? "/pedidos?status=" + encodeURIComponent(status)
          : "/pedidos";
      const pedidos = await requisitar(caminho);

      if (!Array.isArray(pedidos)) {
        throw new ErroApi("A API retornou um histórico inválido.", {
          codigo: "resposta-invalida",
        });
      }

      return pedidos.map(normalizarPedido);
    }

    async function alterarStatus(numero, status) {
      const resposta = await requisitar(
        "/pedidos/" + encodeURIComponent(numero) + "/status",
        {
          method: "PATCH",
          body: { status: status },
        }
      );

      return normalizarPedido(resposta);
    }

    async function limparPedidos() {
      await requisitar("/pedidos", { method: "DELETE" });
      return true;
    }

    return Object.freeze({
      urlBase,
      timeoutMs,
      listarBebidas,
      criarPedido,
      listarPedidos,
      alterarStatus,
      limparPedidos,
    });
  }

  const cliente = criarCliente();

  const api = Object.freeze({
    URL_PADRAO,
    ErroApi,
    criarCliente,
    normalizarBebida,
    normalizarPedido,
    cliente,
    listarBebidas: cliente.listarBebidas,
    criarPedido: cliente.criarPedido,
    listarPedidos: cliente.listarPedidos,
    alterarStatus: cliente.alterarStatus,
    limparPedidos: cliente.limparPedidos,
  });

  if (emNode) {
    module.exports = api;
  }

  if (typeof window !== "undefined") {
    window.CafeNaMaoApi = api;
  }
})(typeof globalThis !== "undefined" ? globalThis : this);
