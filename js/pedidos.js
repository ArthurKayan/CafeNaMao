(function () {
  "use strict";

  const dados = window.CafeNaMaoDados;
  const detalhes = window.CafeNaMaoDetalhes;
  const carrinhoApi = window.CafeNaMaoCarrinho;
  const historicoApi = window.CafeNaMaoHistorico;
  const api = window.CafeNaMaoApi;

  const status = document.getElementById("status-pedido");
  const origemHistorico = document.getElementById("origem-historico");
  const carrinhoVazio = document.getElementById("carrinho-vazio");
  const carrinhoConteudo = document.getElementById("carrinho-conteudo");
  const lista = document.getElementById("lista-carrinho");
  const total = document.getElementById("total-carrinho");
  const pagamentoPedido = document.getElementById("pagamento-pedido");
  const erroPagamento = document.getElementById("erro-pagamento");
  const concluir = document.getElementById("concluir-pedido");
  const confirmacao = document.getElementById("confirmacao-pedido");
  const pedidoDetalhe = document.getElementById("pedido-detalhe");
  const pedidoData = document.getElementById("pedido-data");
  const pedidoPagamento = document.getElementById("pedido-pagamento");
  const pedidoStatus = document.getElementById("pedido-status");
  const pedidoItens = document.getElementById("pedido-itens");
  const pedidoTotal = document.getElementById("pedido-total");
  const historicoVazio = document.getElementById("historico-vazio");
  const listaHistorico = document.getElementById("lista-historico");
  const limparHistoricoBotao = document.getElementById("limpar-historico");
  const statusHistorico = document.getElementById("status-historico");
  const tituloHistorico = document.getElementById("titulo-historico");
  const dialogoLimpar = document.getElementById("dialogo-limpar-historico");
  const cancelarLimpeza = document.getElementById("cancelar-limpeza");
  const confirmarLimpeza = document.getElementById("confirmar-limpeza");

  if (
    !dados ||
    !detalhes ||
    !carrinhoApi ||
    !historicoApi ||
    !status ||
    !origemHistorico ||
    !carrinhoVazio ||
    !carrinhoConteudo ||
    !lista ||
    !total ||
    !pagamentoPedido ||
    !erroPagamento ||
    !concluir ||
    !confirmacao ||
    !pedidoDetalhe ||
    !pedidoData ||
    !pedidoPagamento ||
    !pedidoStatus ||
    !pedidoItens ||
    !pedidoTotal ||
    !historicoVazio ||
    !listaHistorico ||
    !limparHistoricoBotao ||
    !statusHistorico ||
    !tituloHistorico ||
    !dialogoLimpar ||
    !cancelarLimpeza ||
    !confirmarLimpeza
  ) {
    return;
  }

  let carrinho = carrinhoApi.carregarCarrinho();
  let modoApi = false;
  let historicoRemoto = [];
  let enviando = false;
  let promessaInicial = null;

  const formatadorData = new Intl.DateTimeFormat("pt-BR", {
    dateStyle: "short",
    timeStyle: "short",
  });

  const STATUS_DISPONIVEIS = [
    historicoApi.STATUS.RECEBIDO,
    historicoApi.STATUS.EM_PREPARO,
    historicoApi.STATUS.FINALIZADO,
  ];

  function anunciar(texto) {
    status.textContent = "";
    window.setTimeout(function () {
      status.textContent = texto;
    }, 20);
  }

  function anunciarHistorico(texto) {
    statusHistorico.textContent = "";
    window.setTimeout(function () {
      statusHistorico.textContent = texto;
    }, 20);
  }

  function definirOrigemHistorico(texto, modo) {
    origemHistorico.textContent = texto;
    origemHistorico.dataset.modo = modo;
    origemHistorico.hidden = false;
  }

  function subtotalItem(item) {
    return detalhes.calcularTotal(item.precoCentavos, item.quantidade);
  }

  function criarLinhaItem(item) {
    const linha = document.createElement("li");
    linha.className = "carrinho__item";
    linha.dataset.itemId = item.id;

    const informacoes = document.createElement("div");
    informacoes.className = "carrinho__item-info";

    const nome = document.createElement("h2");
    nome.textContent = item.nome;

    const preco = document.createElement("p");
    preco.className = "carrinho__item-preco";
    preco.textContent = "Preço unitário: " + dados.formatarPreco(item.precoCentavos);

    informacoes.append(nome, preco);

    const controles = document.createElement("div");
    controles.className = "carrinho__controles";

    const rotulo = document.createElement("label");
    rotulo.className = "carrinho__rotulo";
    rotulo.htmlFor = "quantidade-" + item.id;
    rotulo.textContent = "Quantidade";

    const campo = document.createElement("input");
    campo.type = "number";
    campo.id = "quantidade-" + item.id;
    campo.name = "quantidade-" + item.id;
    campo.inputMode = "numeric";
    campo.min = String(detalhes.QUANTIDADE_MINIMA);
    campo.max = String(detalhes.QUANTIDADE_MAXIMA);
    campo.step = "1";
    campo.value = String(item.quantidade);
    campo.setAttribute("data-quantidade-id", item.id);
    campo.setAttribute("aria-describedby", "erro-" + item.id);

    const erro = document.createElement("p");
    erro.className = "carrinho__erro";
    erro.id = "erro-" + item.id;
    erro.setAttribute("role", "alert");

    const subtotal = document.createElement("p");
    subtotal.className = "carrinho__subtotal";
    subtotal.id = "subtotal-" + item.id;
    subtotal.textContent = "Subtotal: " + dados.formatarPreco(subtotalItem(item));

    const remover = document.createElement("button");
    remover.type = "button";
    remover.className = "botao botao--secundario carrinho__remover";
    remover.textContent = "Remover";
    remover.setAttribute("data-remover-id", item.id);
    remover.setAttribute("aria-label", "Remover " + item.nome + " do pedido");

    controles.append(rotulo, campo, erro, subtotal, remover);
    linha.append(informacoes, controles);
    return linha;
  }

  function atualizarTotal() {
    total.textContent = dados.formatarPreco(carrinhoApi.calcularTotalCarrinho(carrinho));
  }

  function renderizarCarrinho() {
    if (carrinhoApi.carrinhoVazio(carrinho)) {
      lista.replaceChildren();
      carrinhoConteudo.hidden = true;
      carrinhoVazio.hidden = false;
      anunciar("Seu carrinho está vazio.");
      return;
    }

    const fragmento = document.createDocumentFragment();

    carrinho.itens.forEach(function (item) {
      fragmento.appendChild(criarLinhaItem(item));
    });

    lista.replaceChildren(fragmento);
    carrinhoVazio.hidden = true;
    carrinhoConteudo.hidden = false;
    atualizarTotal();
  }

  function tratarMudancaQuantidade(campo) {
    const id = campo.getAttribute("data-quantidade-id");
    const quantidade = Number(campo.value);
    const erro = detalhes.validarQuantidade(quantidade);
    const elementoErro = document.getElementById("erro-" + id);
    const item = carrinho.itens.find(function (existente) {
      return existente.id === id;
    });

    if (!item) {
      return;
    }

    if (erro) {
      elementoErro.textContent = erro;
      campo.setAttribute("aria-invalid", "true");
      return;
    }

    elementoErro.textContent = "";
    campo.removeAttribute("aria-invalid");
    carrinhoApi.alterarQuantidade(carrinho, id, quantidade);
    carrinhoApi.salvarCarrinho(carrinho);

    document.getElementById("subtotal-" + id).textContent =
      "Subtotal: " + dados.formatarPreco(subtotalItem(item));
    atualizarTotal();
    anunciar(item.nome + " atualizado para " + quantidade + " unidades.");
  }

  function removerDoCarrinho(id) {
    const item = carrinho.itens.find(function (existente) {
      return existente.id === id;
    });

    if (!item) {
      return;
    }

    carrinhoApi.removerItem(carrinho, id);
    carrinhoApi.salvarCarrinho(carrinho);
    renderizarCarrinho();

    if (!carrinhoApi.carrinhoVazio(carrinho)) {
      anunciar(item.nome + " removido do pedido.");
    }
  }

  function limparErroPagamento() {
    erroPagamento.textContent = "";
    pagamentoPedido.removeAttribute("aria-invalid");
  }

  function validarPagamento() {
    const selecionado = document.querySelector('input[name="pagamento"]:checked');

    if (!selecionado) {
      erroPagamento.textContent = "Escolha uma forma de pagamento para continuar.";
      pagamentoPedido.setAttribute("aria-invalid", "true");

      const primeiro = pagamentoPedido.querySelector('input[name="pagamento"]');

      if (primeiro) {
        primeiro.focus();
      }

      return "";
    }

    if (!historicoApi.pagamentoValido(selecionado.value)) {
      erroPagamento.textContent = "Escolha uma forma de pagamento válida.";
      pagamentoPedido.setAttribute("aria-invalid", "true");
      return "";
    }

    limparErroPagamento();
    return selecionado.value;
  }

  function validarQuantidades() {
    const campos = lista.querySelectorAll("input[data-quantidade-id]");
    const alteracoes = [];
    let primeiroInvalido = null;

    campos.forEach(function (campo) {
      const id = campo.getAttribute("data-quantidade-id");
      const quantidade = Number(campo.value);
      const erro = detalhes.validarQuantidade(quantidade);
      const elementoErro = document.getElementById("erro-" + id);
      const item = carrinho.itens.find(function (existente) {
        return existente.id === id;
      });

      if (!item) {
        return;
      }

      if (erro) {
        elementoErro.textContent = erro;
        campo.setAttribute("aria-invalid", "true");

        if (!primeiroInvalido) {
          primeiroInvalido = campo;
        }

        return;
      }

      elementoErro.textContent = "";
      campo.removeAttribute("aria-invalid");

      if (item.quantidade !== quantidade) {
        alteracoes.push({ id: id, quantidade: quantidade });
      }
    });

    if (primeiroInvalido) {
      anunciar("Corrija as quantidades destacadas antes de concluir.");
      primeiroInvalido.focus();
      return false;
    }

    if (alteracoes.length > 0) {
      alteracoes.forEach(function (alteracao) {
        carrinhoApi.alterarQuantidade(carrinho, alteracao.id, alteracao.quantidade);
      });
      carrinhoApi.salvarCarrinho(carrinho);
      atualizarTotal();
    }

    return true;
  }

  function preencherConfirmacao(pedido) {
    pedidoDetalhe.textContent = "Pedido " + pedido.numero + ".";
    pedidoData.textContent =
      "Realizado em " + formatadorData.format(new Date(pedido.criadoEm)) + ".";
    pedidoPagamento.textContent =
      "Pagamento: " + historicoApi.rotuloPagamento(pedido.pagamento) + " (simulado).";
    pedidoStatus.textContent = "Status: " + historicoApi.rotuloStatus(pedido.status) + ".";
    pedidoTotal.textContent = dados.formatarPreco(pedido.totalCentavos);

    const fragmento = document.createDocumentFragment();

    pedido.itens.forEach(function (item) {
      const linha = document.createElement("li");
      linha.textContent =
        item.quantidade +
        "x " +
        item.nome +
        " — " +
        dados.formatarPreco(item.precoCentavos * item.quantidade);
      fragmento.appendChild(linha);
    });

    pedidoItens.replaceChildren(fragmento);
  }

  function limparSelecaoPagamento() {
    pagamentoPedido.querySelectorAll('input[name="pagamento"]').forEach(function (radio) {
      radio.checked = false;
    });
    limparErroPagamento();
  }

  function exibirConfirmacao(registro) {
    const totalFormatado = dados.formatarPreco(registro.totalCentavos);

    carrinhoApi.limparCarrinhoSalvo();
    carrinho = carrinhoApi.criarCarrinhoVazio();

    carrinhoConteudo.hidden = true;
    carrinhoVazio.hidden = true;
    preencherConfirmacao(registro);
    confirmacao.hidden = false;
    confirmacao.focus();

    limparSelecaoPagamento();
    anunciar(
      "Pedido " + registro.numero + " confirmado. Total de " + totalFormatado + "."
    );
  }

  async function concluirPedidoNaApi(pagamento) {
    let registro;

    try {
      registro = await api.criarPedido(pagamento, carrinho.itens);
    } catch (erroApi) {
      anunciar(
        erroApi && erroApi.message
          ? erroApi.message
          : "Não foi possível salvar o pedido na API. O carrinho foi mantido."
      );
      return;
    }

    historicoRemoto = [registro].concat(
      historicoRemoto.filter(function (pedido) {
        return pedido.numero !== registro.numero;
      })
    );

    exibirConfirmacao(registro);
    renderizarHistorico(historicoRemoto);
  }

  function concluirPedidoLocal(pagamento) {
    let registro;

    try {
      const pedido = carrinhoApi.criarPedido(carrinho);
      registro = historicoApi.criarRegistroPedido(
        pedido,
        pagamento,
        historicoApi.STATUS.RECEBIDO
      );
    } catch (erroCriacao) {
      anunciar("Não foi possível concluir o pedido. Revise os dados e tente novamente.");
      return;
    }

    if (!historicoApi.salvarPedido(registro)) {
      anunciar(
        "Não foi possível salvar o pedido no histórico. O carrinho foi mantido."
      );
      return;
    }

    exibirConfirmacao(registro);
    renderizarHistorico();
  }

  async function concluirPedido() {
    if (enviando) {
      return;
    }

    if (promessaInicial) {
      await promessaInicial;
    }

    const erro = carrinhoApi.validarCarrinhoParaPedido(carrinho);

    if (erro) {
      anunciar(erro);
      return;
    }

    if (!validarQuantidades()) {
      return;
    }

    const pagamento = validarPagamento();

    if (!pagamento) {
      return;
    }

    if (modoApi && !api) {
      anunciar("A API não está disponível. Recarregue a página e tente novamente.");
      return;
    }

    enviando = true;
    concluir.disabled = true;

    try {
      if (modoApi) {
        await concluirPedidoNaApi(pagamento);
      } else {
        concluirPedidoLocal(pagamento);
      }
    } finally {
      enviando = false;

      if (!carrinhoApi.carrinhoVazio(carrinho)) {
        concluir.disabled = false;
      }
    }
  }

  function criarItemHistorico(pedido) {
    const item = document.createElement("li");
    item.className = "historico__pedido";
    item.dataset.numero = pedido.numero;
    item.dataset.status = pedido.status;

    const cabecalho = document.createElement("div");
    cabecalho.className = "historico__pedido-cabecalho";

    const titulo = document.createElement("h3");
    titulo.textContent = "Pedido " + pedido.numero;

    const statusPedido = document.createElement("p");
    statusPedido.className = "historico__status";
    statusPedido.setAttribute("data-status-rotulo", "");
    statusPedido.textContent = historicoApi.rotuloStatus(pedido.status);

    cabecalho.append(titulo, statusPedido);

    const data = document.createElement("p");
    data.className = "historico__data";
    data.textContent =
      "Realizado em " + formatadorData.format(new Date(pedido.criadoEm)) + ".";

    const itens = document.createElement("ul");
    itens.className = "historico__itens";

    pedido.itens.forEach(function (bebida) {
      const linha = document.createElement("li");
      linha.textContent =
        bebida.quantidade +
        "x " +
        bebida.nome +
        " — " +
        dados.formatarPreco(bebida.precoCentavos * bebida.quantidade);
      itens.appendChild(linha);
    });

    const rodape = document.createElement("div");
    rodape.className = "historico__rodape";

    const totalPedido = document.createElement("p");
    totalPedido.className = "historico__total";
    totalPedido.textContent = "Total: " + dados.formatarPreco(pedido.totalCentavos);

    const pagamento = document.createElement("p");
    pagamento.className = "historico__pagamento";
    pagamento.textContent =
      "Pagamento: " + historicoApi.rotuloPagamento(pedido.pagamento) + " (simulado)";

    const controle = document.createElement("div");
    controle.className = "historico__controle";

    const rotulo = document.createElement("label");
    rotulo.className = "historico__rotulo";
    rotulo.htmlFor = "status-" + pedido.numero;
    rotulo.textContent = "Status do pedido";

    const selecao = document.createElement("select");
    selecao.className = "historico__select";
    selecao.id = "status-" + pedido.numero;
    selecao.setAttribute("data-status-numero", pedido.numero);

    STATUS_DISPONIVEIS.forEach(function (opcao) {
      const elemento = document.createElement("option");
      elemento.value = opcao;
      elemento.textContent = historicoApi.rotuloStatus(opcao);
      elemento.selected = opcao === pedido.status;
      selecao.appendChild(elemento);
    });

    controle.append(rotulo, selecao);
    rodape.append(totalPedido, pagamento, controle);
    item.append(cabecalho, data, itens, rodape);

    return item;
  }

  function renderizarHistorico(pedidos) {
    const historico = Array.isArray(pedidos)
      ? pedidos
      : modoApi
        ? historicoRemoto
        : historicoApi.carregarHistorico();

    if (historico.length === 0) {
      listaHistorico.replaceChildren();
      historicoVazio.hidden = false;
      limparHistoricoBotao.hidden = true;
      return;
    }

    const fragmento = document.createDocumentFragment();

    historico.forEach(function (pedido) {
      fragmento.appendChild(criarItemHistorico(pedido));
    });

    listaHistorico.replaceChildren(fragmento);
    historicoVazio.hidden = true;
    limparHistoricoBotao.hidden = false;
  }

  async function carregarHistoricoRemoto() {
    const pedidos = await api.listarPedidos();
    modoApi = true;
    historicoRemoto = pedidos;
    definirOrigemHistorico("Histórico sincronizado com a API.", "api");
    renderizarHistorico(pedidos);
  }

  async function inicializarHistorico() {
    listaHistorico.setAttribute("aria-busy", "true");

    try {
      await carregarHistoricoRemoto();
    } catch (erroApi) {
      modoApi = false;
      historicoRemoto = [];
      definirOrigemHistorico("API indisponível — usando histórico local.", "local");
      renderizarHistorico();
    } finally {
      listaHistorico.setAttribute("aria-busy", "false");
    }
  }

  function abrirDialogoLimpeza() {
    if (typeof dialogoLimpar.showModal === "function") {
      dialogoLimpar.showModal();
      return;
    }

    dialogoLimpar.setAttribute("open", "");
  }

  function fecharDialogoLimpeza() {
    if (typeof dialogoLimpar.close === "function") {
      dialogoLimpar.close();
      return;
    }

    dialogoLimpar.removeAttribute("open");
  }

  async function limparTodoOHistorico() {
    confirmarLimpeza.disabled = true;

    try {
      if (modoApi) {
        await api.limparPedidos();
        historicoRemoto = [];
        historicoApi.limparHistorico();
      } else if (!historicoApi.limparHistorico()) {
        anunciarHistorico("Não foi possível limpar o histórico de pedidos.");
        return;
      }
    } catch (erroApi) {
      anunciarHistorico(
        erroApi && erroApi.message
          ? erroApi.message
          : "Não foi possível limpar o histórico de pedidos."
      );
      return;
    } finally {
      confirmarLimpeza.disabled = false;
    }

    fecharDialogoLimpeza();
    renderizarHistorico();
    tituloHistorico.focus();
    anunciarHistorico("Histórico de pedidos limpo.");
  }

  async function tratarMudancaStatus(evento) {
    const selecao = evento.target.closest("select[data-status-numero]");

    if (!selecao) {
      return;
    }

    const numero = selecao.getAttribute("data-status-numero");
    const item = selecao.closest(".historico__pedido");
    const statusAnterior = item ? item.dataset.status : "";
    let statusAplicado = selecao.value;
    selecao.disabled = true;

    try {
      if (modoApi) {
        const atualizado = await api.alterarStatus(numero, statusAplicado);
        statusAplicado = atualizado.status;
        historicoRemoto = historicoRemoto.map(function (pedido) {
          return pedido.numero === numero ? atualizado : pedido;
        });
      } else if (!historicoApi.alterarStatus(numero, statusAplicado)) {
        throw new Error("Status não atualizado.");
      }
    } catch (erroApi) {
      if (item) {
        selecao.value = statusAnterior;
      }

      anunciarHistorico("Não foi possível atualizar o status do pedido " + numero + ".");
      return;
    } finally {
      selecao.disabled = false;
    }

    if (item) {
      item.dataset.status = statusAplicado;

      const rotulo = item.querySelector("[data-status-rotulo]");

      if (rotulo) {
        rotulo.textContent = historicoApi.rotuloStatus(statusAplicado);
      }
    }

    anunciarHistorico(
      "Pedido " +
        numero +
        ": status atualizado para " +
        historicoApi.rotuloStatus(statusAplicado) +
        "."
    );
  }

  lista.addEventListener("change", function (evento) {
    const campo = evento.target;

    if (campo && campo.matches('input[data-quantidade-id]')) {
      tratarMudancaQuantidade(campo);
    }
  });

  lista.addEventListener("click", function (evento) {
    const botao = evento.target.closest("button[data-remover-id]");

    if (botao) {
      removerDoCarrinho(botao.getAttribute("data-remover-id"));
    }
  });

  pagamentoPedido.addEventListener("change", function (evento) {
    if (evento.target.matches('input[name="pagamento"]')) {
      limparErroPagamento();
    }
  });

  concluir.addEventListener("click", concluirPedido);

  listaHistorico.addEventListener("change", tratarMudancaStatus);

  limparHistoricoBotao.addEventListener("click", abrirDialogoLimpeza);
  cancelarLimpeza.addEventListener("click", fecharDialogoLimpeza);
  confirmarLimpeza.addEventListener("click", limparTodoOHistorico);

  window.addEventListener("pageshow", function (evento) {
    if (evento.persisted) {
      carrinho = carrinhoApi.carregarCarrinho();

      if (confirmacao.hidden) {
        renderizarCarrinho();
      }

      promessaInicial = inicializarHistorico();
    }
  });

  renderizarCarrinho();
  promessaInicial = inicializarHistorico();
})();
