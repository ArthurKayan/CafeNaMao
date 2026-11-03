(function () {
  "use strict";

  const dados = window.CafeNaMaoDados;
  const lista = document.getElementById("lista-bebidas");
  const status = document.getElementById("status-catalogo");
  const estado = document.getElementById("estado-catalogo");
  const mensagem = document.getElementById("mensagem-estado");
  const acao = document.getElementById("acao-estado");

  if (!lista || !status || !estado || !mensagem || !acao) {
    return;
  }

  const parametros = new URLSearchParams(window.location.search);
  const estadoDaUrl = parametros.get("estado");
  let estadoSimulado = estadoDaUrl === "vazio" || estadoDaUrl === "erro" ? estadoDaUrl : null;

  function anunciar(texto) {
    status.textContent = "";
    window.setTimeout(function () {
      status.textContent = texto;
    }, 20);
  }

  function ocultarEstado() {
    estado.hidden = true;
    mensagem.textContent = "";
    acao.replaceChildren();
  }

  function criarEsqueleto() {
    const article = document.createElement("article");
    article.className = "bebida bebida--esqueleto";
    article.setAttribute("aria-hidden", "true");
    article.innerHTML =
      '<div class="bebida__topo">' +
      '<span class="esqueleto esqueleto--badge"></span>' +
      '<span class="esqueleto esqueleto--tag"></span>' +
      "</div>" +
      '<div class="esqueleto esqueleto--titulo"></div>' +
      '<div class="esqueleto esqueleto--linha"></div>' +
      '<div class="esqueleto esqueleto--linha"></div>' +
      '<div class="esqueleto esqueleto--linha-curta"></div>' +
      '<div class="esqueleto esqueleto--preco"></div>';
    return article;
  }

  function criarCardBebida(bebida, indice) {
    const article = document.createElement("article");
    article.className = "bebida";
    article.dataset.bebidaId = bebida.id;

    const topo = document.createElement("div");
    topo.className = "bebida__topo";

    const numero = document.createElement("span");
    numero.className = "bebida__numero";
    numero.setAttribute("aria-hidden", "true");
    numero.textContent = String(indice + 1).padStart(2, "0");

    const categoria = document.createElement("span");
    categoria.className = "bebida__categoria";
    categoria.textContent = bebida.categoria;

    topo.append(numero, categoria);

    const titulo = document.createElement("h2");
    titulo.id = "bebida-titulo-" + bebida.id;
    titulo.textContent = bebida.nome;
    article.setAttribute("aria-labelledby", titulo.id);

    const descricao = document.createElement("p");
    descricao.className = "bebida__descricao";
    descricao.textContent = bebida.descricao;

    const preco = document.createElement("p");
    preco.className = "bebida__preco";
    preco.textContent = dados.formatarPreco(bebida.precoCentavos);

    article.append(topo, titulo, descricao, preco);
    return article;
  }

  function renderizarBebidas(bebidas) {
    const fragmento = document.createDocumentFragment();

    bebidas.forEach(function (bebida, indice) {
      fragmento.appendChild(criarCardBebida(bebida, indice));
    });

    lista.replaceChildren(fragmento);
    lista.setAttribute("aria-busy", "false");
    ocultarEstado();
    anunciar(bebidas.length + " bebidas disponíveis.");
  }

  function mostrarCarregando() {
    ocultarEstado();
    lista.setAttribute("aria-busy", "true");

    const fragmento = document.createDocumentFragment();
    for (let indice = 0; indice < 3; indice += 1) {
      fragmento.appendChild(criarEsqueleto());
    }

    lista.replaceChildren(fragmento);
    anunciar("Carregando bebidas.");
  }

  function mostrarVazio() {
    lista.replaceChildren();
    lista.setAttribute("aria-busy", "false");

    mensagem.textContent = "Nenhuma bebida disponível no momento.";

    const link = document.createElement("a");
    link.className = "botao botao--secundario";
    link.href = "index.html";
    link.textContent = "Voltar ao início";

    acao.replaceChildren(link);
    estado.hidden = false;
    anunciar("Nenhuma bebida disponível no momento.");
  }

  function mostrarErro() {
    lista.replaceChildren();
    lista.setAttribute("aria-busy", "false");

    mensagem.textContent =
      "Não foi possível carregar o menu. Verifique sua conexão e tente novamente.";

    const botao = document.createElement("button");
    botao.className = "botao botao--primario";
    botao.type = "button";
    botao.textContent = "Tentar novamente";
    botao.addEventListener("click", carregarCatalogo);

    acao.replaceChildren(botao);
    estado.hidden = false;
    anunciar("Não foi possível carregar o menu.");
  }

  function carregarCatalogo() {
    const estadoDaCarga = estadoSimulado;
    estadoSimulado = null;

    mostrarCarregando();

    window.setTimeout(function () {
      if (estadoDaCarga === "erro" || !dados) {
        mostrarErro();
        return;
      }

      const bebidas = estadoDaCarga === "vazio" ? [] : dados.bebidas;

      if (bebidas.length === 0) {
        mostrarVazio();
        return;
      }

      renderizarBebidas(bebidas);
    }, 450);
  }

  carregarCatalogo();
})();
