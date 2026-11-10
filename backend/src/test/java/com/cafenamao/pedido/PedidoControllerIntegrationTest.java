package com.cafenamao.pedido;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PedidoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Test
    void criaPedidoECalculaTotalNoServidor() throws Exception {
        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pagamento": "dinheiro",
                                  "itens": [
                                    { "bebidaId": "espresso-curto", "quantidade": 2 },
                                    { "bebidaId": "cappuccino", "quantidade": 1 }
                                  ]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.startsWith("/api/pedidos/PED-")))
                .andExpect(jsonPath("$.numero").value(org.hamcrest.Matchers.startsWith("PED-")))
                .andExpect(jsonPath("$.totalCentavos").value(1550))
                .andExpect(jsonPath("$.pagamento").value("dinheiro"))
                .andExpect(jsonPath("$.status").value("recebido"))
                .andExpect(jsonPath("$.itens.length()").value(2))
                .andExpect(jsonPath("$.itens[0].bebidaId").value("espresso-curto"))
                .andExpect(jsonPath("$.itens[0].nome").value("Espresso Curto"))
                .andExpect(jsonPath("$.itens[0].precoCentavos").value(400))
                .andExpect(jsonPath("$.itens[0].quantidade").value(2))
                .andExpect(jsonPath("$.itens[0].subtotalCentavos").value(800));
    }

    @Test
    void aceitaPagamentoComCartaoFicticio() throws Exception {
        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pagamento": "cartao-ficticio",
                                  "itens": [
                                    { "bebidaId": "mocha", "quantidade": 1 }
                                  ]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pagamento").value("cartao-ficticio"))
                .andExpect(jsonPath("$.totalCentavos").value(850));
    }

    @Test
    void rejeitaCarrinhoVazio() throws Exception {
        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pagamento": "dinheiro",
                                  "itens": []
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos[*].campo", hasItem("itens")));
    }

    @Test
    void rejeitaPagamentoInvalido() throws Exception {
        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pagamento": "pix",
                                  "itens": [
                                    { "bebidaId": "mocha", "quantidade": 1 }
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value(
                        "A forma de pagamento é inválida. Use dinheiro ou cartao-ficticio."));
    }

    @Test
    void rejeitaQuantidadeForaDosLimites() throws Exception {
        for (int quantidade : new int[]{0, 11}) {
            mockMvc.perform(post("/api/pedidos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "pagamento": "dinheiro",
                                      "itens": [
                                        { "bebidaId": "mocha", "quantidade": %d }
                                      ]
                                    }
                                    """.formatted(quantidade)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.campos[*].campo", hasItem("itens[0].quantidade")));
        }
    }

    @Test
    void rejeitaBebidaInexistente() throws Exception {
        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pagamento": "dinheiro",
                                  "itens": [
                                    { "bebidaId": "nao-existe", "quantidade": 1 }
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Bebida não encontrada: nao-existe."));
    }

    @Test
    void rejeitaBebidaRepetidaNoMesmoPedido() throws Exception {
        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pagamento": "dinheiro",
                                  "itens": [
                                    { "bebidaId": "mocha", "quantidade": 1 },
                                    { "bebidaId": "mocha", "quantidade": 2 }
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("A bebida mocha foi informada mais de uma vez."));
    }

    @Test
    void listaPedidosDoMaisRecenteParaOMaisAntigo() throws Exception {
        Pedido antigo = new Pedido("PED-AAAA0001", Instant.parse("2026-01-01T10:00:00Z"),
                FormaPagamento.DINHEIRO, StatusPedido.RECEBIDO);
        antigo.adicionarItem(new ItemPedido("espresso-curto", "Espresso Curto", 400, 1));
        antigo.setTotalCentavos(400);
        pedidoRepository.save(antigo);

        Pedido novo = new Pedido("PED-BBBB0002", Instant.parse("2026-01-02T10:00:00Z"),
                FormaPagamento.DINHEIRO, StatusPedido.RECEBIDO);
        novo.adicionarItem(new ItemPedido("mocha", "Mocha", 850, 1));
        novo.setTotalCentavos(850);
        pedidoRepository.save(novo);

        mockMvc.perform(get("/api/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].numero").value("PED-BBBB0002"))
                .andExpect(jsonPath("$[1].numero").value("PED-AAAA0001"));
    }

    @Test
    void buscaPedidoExistente() throws Exception {
        String numero = criarPedido("dinheiro", "[{\"bebidaId\":\"mocha\",\"quantidade\":1}]");

        mockMvc.perform(get("/api/pedidos/" + numero))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero").value(numero))
                .andExpect(jsonPath("$.totalCentavos").value(850));
    }

    @Test
    void retorna404ParaPedidoInexistente() throws Exception {
        mockMvc.perform(get("/api/pedidos/PED-INEXISTENTE"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Pedido não encontrado: PED-INEXISTENTE."));
    }

    @Test
    void alteraStatusDoPedido() throws Exception {
        String numero = criarPedido("dinheiro", "[{\"bebidaId\":\"mocha\",\"quantidade\":1}]");

        mockMvc.perform(patch("/api/pedidos/" + numero + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"status\": \"em-preparo\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("em-preparo"));

        mockMvc.perform(get("/api/pedidos/" + numero))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("em-preparo"));
    }

    @Test
    void rejeitaStatusInvalido() throws Exception {
        String numero = criarPedido("dinheiro", "[{\"bebidaId\":\"mocha\",\"quantidade\":1}]");

        mockMvc.perform(patch("/api/pedidos/" + numero + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"status\": \"perdido\" }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value(
                        "O status é inválido. Use recebido, em-preparo ou finalizado."));
    }

    @Test
    void retorna404AoAlterarStatusDePedidoInexistente() throws Exception {
        mockMvc.perform(patch("/api/pedidos/PED-INEXISTENTE/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"status\": \"finalizado\" }"))
                .andExpect(status().isNotFound());
    }

    @Test
    void filtraPedidosPorStatus() throws Exception {
        String recebido = criarPedido("dinheiro", "[{\"bebidaId\":\"mocha\",\"quantidade\":1}]");
        String emPreparo = criarPedido("dinheiro", "[{\"bebidaId\":\"mocha\",\"quantidade\":2}]");

        mockMvc.perform(patch("/api/pedidos/" + emPreparo + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"status\": \"em-preparo\" }"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/pedidos").param("status", "em-preparo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].numero").value(emPreparo));

        mockMvc.perform(get("/api/pedidos").param("status", "recebido"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].numero").value(recebido));
    }

    @Test
    void rejeitaFiltroDeStatusInvalido() throws Exception {
        mockMvc.perform(get("/api/pedidos").param("status", "perdido"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void excluiPedidoExistente() throws Exception {
        String numero = criarPedido("dinheiro", "[{\"bebidaId\":\"mocha\",\"quantidade\":1}]");

        mockMvc.perform(delete("/api/pedidos/" + numero))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/pedidos/" + numero))
                .andExpect(status().isNotFound());
    }

    @Test
    void retorna404AoExcluirPedidoInexistente() throws Exception {
        mockMvc.perform(delete("/api/pedidos/PED-INEXISTENTE"))
                .andExpect(status().isNotFound());
    }

    @Test
    void limpaTodosOsPedidos() throws Exception {
        criarPedido("dinheiro", "[{\"bebidaId\":\"mocha\",\"quantidade\":1}]");
        criarPedido("cartao-ficticio", "[{\"bebidaId\":\"cappuccino\",\"quantidade\":1}]");

        mockMvc.perform(delete("/api/pedidos"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    private String criarPedido(String pagamento, String itensJson) throws Exception {
        MvcResult resultado = mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pagamento": "%s",
                                  "itens": %s
                                }
                                """.formatted(pagamento, itensJson)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(resultado.getResponse().getContentAsString())
                .get("numero")
                .asText();
    }
}
