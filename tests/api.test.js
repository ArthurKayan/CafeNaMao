const test = require("node:test");
const assert = require("node:assert/strict");
const {
  ErroApi,
  criarCliente,
  normalizarBebida,
  normalizarPedido,
} = require("../js/api.js");

const bebidaApi = {
  id: "espresso-curto",
  nome: "Espresso Curto",
  descricao: "Café intenso e encorpado, preparado em dose curta.",
  categoria: "Café",
  precoCentavos: 400,
};

const pedidoApi = {
  numero: "PED-00000001",
  criadoEm: "2026-11-06T12:00:00.000Z",
  itens: [
    {
      bebidaId: "espresso-curto",
      nome: "Espresso Curto",
      precoCentavos: 400,
      quantidade: 2,
      subtotalCentavos: 800,
    },
  ],
  totalCentavos: 800,
  pagamento: "dinheiro",
  status: "recebido",
};

function criarResposta(status, corpo) {
  return {
    status: status,
    ok: status >= 200 && status < 300,
    json: async function () {
      return corpo;
    },
  };
}

test("normaliza uma bebida recebida da API", function () {
  assert.deepEqual(normalizarBebida(bebidaApi), bebidaApi);
});

test("rejeita bebida inválida recebida da API", function () {
  assert.throws(function () {
    normalizarBebida(Object.assign({}, bebidaApi, { precoCentavos: 4.5 }));
  }, /bebida inválida/i);
});

test("normaliza pedido da API para o formato do front-end", function () {
  const pedido = normalizarPedido(pedidoApi);

  assert.equal(pedido.numero, "PED-00000001");
  assert.equal(pedido.itens[0].id, "espresso-curto");
  assert.equal(pedido.totalCentavos, 800);
});

test("lista bebidas usando o cliente HTTP", async function () {
  const chamadas = [];
  const cliente = criarCliente({
    baseUrl: "http://localhost:8080/api/",
    fetchImpl: async function (url, opcoes) {
      chamadas.push({ url: url, opcoes: opcoes });
      return criarResposta(200, [bebidaApi]);
    },
  });

  const bebidas = await cliente.listarBebidas();

  assert.equal(chamadas[0].url, "http://localhost:8080/api/bebidas");
  assert.equal(chamadas[0].opcoes.method, undefined);
  assert.equal(bebidas[0].id, "espresso-curto");
});

test("cria pedido com bebidaId e quantidade, sem enviar preço", async function () {
  let corpoEnviado;
  const cliente = criarCliente({
    fetchImpl: async function (url, opcoes) {
      corpoEnviado = JSON.parse(opcoes.body);
      return criarResposta(201, pedidoApi);
    },
  });

  const pedido = await cliente.criarPedido("dinheiro", [
    { id: "espresso-curto", precoCentavos: 999, quantidade: 2 },
  ]);

  assert.deepEqual(corpoEnviado, {
    pagamento: "dinheiro",
    itens: [{ bebidaId: "espresso-curto", quantidade: 2 }],
  });
  assert.equal(pedido.totalCentavos, 800);
});

test("lista pedidos com filtro de status", async function () {
  let urlChamada;
  const cliente = criarCliente({
    fetchImpl: async function (url) {
      urlChamada = url;
      return criarResposta(200, [pedidoApi]);
    },
  });

  const pedidos = await cliente.listarPedidos("em-preparo");

  assert.match(urlChamada, /\/pedidos\?status=em-preparo$/);
  assert.equal(pedidos.length, 1);
});

test("altera status via PATCH", async function () {
  let metodo;
  let corpo;
  const cliente = criarCliente({
    fetchImpl: async function (url, opcoes) {
      metodo = opcoes.method;
      corpo = JSON.parse(opcoes.body);
      return criarResposta(200, Object.assign({}, pedidoApi, { status: "em-preparo" }));
    },
  });

  const pedido = await cliente.alterarStatus("PED-00000001", "em-preparo");

  assert.equal(metodo, "PATCH");
  assert.deepEqual(corpo, { status: "em-preparo" });
  assert.equal(pedido.status, "em-preparo");
});

test("limpa pedidos e aceita resposta 204", async function () {
  let metodo;
  const cliente = criarCliente({
    fetchImpl: async function (url, opcoes) {
      metodo = opcoes.method;
      return criarResposta(204, null);
    },
  });

  assert.equal(await cliente.limparPedidos(), true);
  assert.equal(metodo, "DELETE");
});

test("converte erro HTTP em ErroApi com status e campos", async function () {
  const cliente = criarCliente({
    fetchImpl: async function () {
      return criarResposta(400, {
        mensagem: "A quantidade deve estar entre 1 e 10.",
        campos: [{ campo: "itens[0].quantidade", mensagem: "A quantidade máxima é 10." }],
      });
    },
  });

  await assert.rejects(
    function () {
      return cliente.criarPedido("dinheiro", [{ id: "espresso-curto", quantidade: 11 }]);
    },
    function (erro) {
      assert.ok(erro instanceof ErroApi);
      assert.equal(erro.status, 400);
      assert.equal(erro.codigo, "http");
      assert.equal(erro.campos.length, 1);
      return true;
    }
  );
});

test("converte falha de conexão em API indisponível", async function () {
  const cliente = criarCliente({
    fetchImpl: async function () {
      throw new TypeError("Failed to fetch");
    },
  });

  await assert.rejects(
    function () {
      return cliente.listarBebidas();
    },
    function (erro) {
      assert.equal(erro.codigo, "indisponivel");
      return true;
    }
  );
});

test("converte demora excessiva em timeout", async function () {
  const cliente = criarCliente({
    timeoutMs: 10,
    fetchImpl: function () {
      return new Promise(function () {});
    },
  });

  await assert.rejects(
    function () {
      return cliente.listarBebidas();
    },
    function (erro) {
      assert.equal(erro.codigo, "timeout");
      return true;
    }
  );
});

test("rejeita resposta de catálogo que não é lista", async function () {
  const cliente = criarCliente({
    fetchImpl: async function () {
      return criarResposta(200, { itens: [] });
    },
  });

  await assert.rejects(function () {
    return cliente.listarBebidas();
  }, /catálogo inválido/i);
});
