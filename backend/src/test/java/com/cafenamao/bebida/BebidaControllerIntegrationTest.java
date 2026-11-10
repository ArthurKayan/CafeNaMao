package com.cafenamao.bebida;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BebidaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listaOCatalogoInicialOrdenadoPorNome() throws Exception {
        mockMvc.perform(get("/api/bebidas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$[0].nome").value("Café com Leite"))
                .andExpect(jsonPath("$[?(@.id == 'espresso-curto')]").isNotEmpty());
    }

    @Test
    void buscaBebidaExistente() throws Exception {
        mockMvc.perform(get("/api/bebidas/espresso-curto"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("espresso-curto"))
                .andExpect(jsonPath("$.nome").value("Espresso Curto"))
                .andExpect(jsonPath("$.precoCentavos").value(400));
    }

    @Test
    void retorna404ParaBebidaInexistente() throws Exception {
        mockMvc.perform(get("/api/bebidas/nao-existe"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Bebida não encontrada: nao-existe."));
    }

    @Test
    void criaBebidaComDadosValidos() throws Exception {
        mockMvc.perform(post("/api/bebidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "cha-verde-teste",
                                  "nome": "Chá Verde",
                                  "descricao": "Infusão leve de folhas de chá verde.",
                                  "categoria": "Chá",
                                  "precoCentavos": 525
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/bebidas/cha-verde-teste"))
                .andExpect(jsonPath("$.id").value("cha-verde-teste"))
                .andExpect(jsonPath("$.precoCentavos").value(525));
    }

    @Test
    void rejeitaBebidaDuplicadaCom409() throws Exception {
        mockMvc.perform(post("/api/bebidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "espresso-curto",
                                  "nome": "Outro Espresso",
                                  "descricao": "Bebida duplicada.",
                                  "categoria": "Café",
                                  "precoCentavos": 999
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.mensagem").value("Já existe uma bebida com o id espresso-curto."));
    }

    @Test
    void rejeitaBebidaComDadosInvalidos() throws Exception {
        mockMvc.perform(post("/api/bebidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "ID INVALIDO",
                                  "nome": "",
                                  "descricao": "",
                                  "categoria": "",
                                  "precoCentavos": -100
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos[*].campo", hasItem("id")))
                .andExpect(jsonPath("$.campos[*].campo", hasItem("nome")))
                .andExpect(jsonPath("$.campos[*].campo", hasItem("precoCentavos")));
    }

    @Test
    void atualizaBebidaExistente() throws Exception {
        mockMvc.perform(put("/api/bebidas/mocha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Mocha Especial",
                                  "descricao": "Espresso com chocolate e leite.",
                                  "categoria": "Café",
                                  "precoCentavos": 900
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("mocha"))
                .andExpect(jsonPath("$.nome").value("Mocha Especial"))
                .andExpect(jsonPath("$.precoCentavos").value(900));

        mockMvc.perform(get("/api/bebidas/mocha"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Mocha Especial"))
                .andExpect(jsonPath("$.precoCentavos").value(900));
    }

    @Test
    void retorna404AoAtualizarBebidaInexistente() throws Exception {
        mockMvc.perform(put("/api/bebidas/nao-existe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Bebida",
                                  "descricao": "Descrição",
                                  "categoria": "Café",
                                  "precoCentavos": 100
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void excluiBebidaExistente() throws Exception {
        mockMvc.perform(post("/api/bebidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "bebida-temporal",
                                  "nome": "Bebida Temporal",
                                  "descricao": "Será removida.",
                                  "categoria": "Teste",
                                  "precoCentavos": 100
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/bebidas/bebida-temporal"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/bebidas/bebida-temporal"))
                .andExpect(status().isNotFound());
    }

    @Test
    void retorna404AoExcluirBebidaInexistente() throws Exception {
        mockMvc.perform(delete("/api/bebidas/nao-existe"))
                .andExpect(status().isNotFound());
    }

    @Test
    void retorna404ParaRotaInexistente() throws Exception {
        mockMvc.perform(get("/api/rota-inexistente"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("A rota informada não existe."));
    }
}
