(function () {
  "use strict";

  const dados = window.CafeNaMaoDados;
  const detalhes = window.CafeNaMaoDetalhes;
  const carrinhoApi = window.CafeNaMaoCarrinho;

  const status = document.getElementById("status-pedido");
  const carrinhoVazio = document.getElementById("carrinho-vazio");
  const carrinhoConteudo = document.getElementById("carrinho-conteudo");
  const lista = document.getElementById("lista-carrinho");
  const total = document.getElementById("total-carrinho");
  const concluir = document.getElementById("concluir-pedido");
  const confirmacao = document.getElementById("confirmacao-pedido");
  const pedidoDetalhe = document.getElementById("pedido-detalhe");
  const pedidoData = document.getElementById("pedido-data");
  const pedidoItens = document.getElementById("pedido-itens");
  const pedidoTotal = document.getElementById("pedido-total");

  if (
    !dados ||
    !detalhes ||
    !carrinhoApi ||
    !status ||
    !carrinhoVazio ||
    !carrinhoConteudo ||
    !lista ||
    !total ||
    !concluir ||
    !confirmacao ||
    !pedidoDetalhe ||
    !pedidoData ||
    !pedidoItens ||
    !pedidoTotal
  ) {
    return;
  }

  let carrinho = carrinhoApi.carregarCarrinho();

  const formatadorData = new Intl.DateTimeFormat("pt-BR", {
    dateStyle: "short",
    timeStyle: "short",
  });

  function anunciar(texto) {
    status.textContent = "";
    window.setTimeout(function () {
      status.textContent = texto;
    }, 20);
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

  function preencherConfirmacao(pedido) {
    pedidoDetalhe.textContent = "Pedido " + pedido.numero + ".";
    pedidoData.textContent =
      "Realizado em " + formatadorData.format(new Date(pedido.criadoEm)) + ".";
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

  function concluirPedido() {
    const erro = carrinhoApi.validarCarrinhoParaPedido(carrinho);

    if (erro) {
      anunciar(erro);
      return;
    }

    const pedido = carrinhoApi.criarPedido(carrinho);
    const totalFormatado = dados.formatarPreco(pedido.totalCentavos);

    carrinhoApi.limparCarrinhoSalvo();
    carrinho = carrinhoApi.criarCarrinhoVazio();

    carrinhoConteudo.hidden = true;
    carrinhoVazio.hidden = true;
    preencherConfirmacao(pedido);
    confirmacao.hidden = false;
    confirmacao.focus();
    anunciar("Pedido " + pedido.numero + " confirmado. Total de " + totalFormatado + ".");
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

  concluir.addEventListener("click", concluirPedido);

  window.addEventListener("pageshow", function (evento) {
    if (evento.persisted) {
      carrinho = carrinhoApi.carregarCarrinho();

      if (confirmacao.hidden) {
        renderizarCarrinho();
      }
    }
  });

  renderizarCarrinho();
})();
