
window.CafeNaMaoPronto = (async function (raiz) {
  "use strict";

  const formatadorPreco = new Intl.NumberFormat("pt-BR", {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
  });

  const SQL = await initSqlJs({
      locateFile: arquivo =>
          `../node_modules/sql.js/dist/${arquivo}`
  });

  const resposta = await fetch("../dados/data.db");

  if (!resposta.ok) {
      throw new Error("Não foi possível carregar o banco de dados.");
  }

  const arquivoBanco = await resposta.arrayBuffer();

  const database = new SQL.Database(
      new Uint8Array(arquivoBanco)
  );

  function buscarBebidas() {
      const resultado = database.exec(
          "SELECT * FROM bebidas"
      );

      if (resultado.length === 0) {
          console.log("Deu problema no DB");
          return [];
      }

      const { columns, values } = resultado[0];

      return values.map(linha =>
          Object.fromEntries(
              columns.map((coluna, indice) => [
                  coluna,
                  linha[indice]
              ])
          )
      );
  }

  function formatarPreco(centavos) {
      if (!Number.isInteger(centavos) || centavos < 0) {
          throw new TypeError(
              "O preço deve ser um número inteiro de centavos maior ou igual a zero."
          );
      }

      return `R$ ${formatadorPreco.format(centavos / 100)}`;
  }

  const api = Object.freeze({
      buscarBebidas,
      formatarPreco
  });

  raiz.CafeNaMaoDados = api;

  return api;

})(typeof globalThis !== "undefined" ? globalThis : this);
