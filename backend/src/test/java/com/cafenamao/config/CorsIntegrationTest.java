package com.cafenamao.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CorsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void permiteRequisicaoDaPaginaAbertaPorFile() throws Exception {
        mockMvc.perform(get("/api/bebidas").header("Origin", "null"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "null"));
    }

    @Test
    void respondePreflightParaCriacaoDePedido() throws Exception {
        mockMvc.perform(options("/api/pedidos")
                        .header("Origin", "null")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "null"))
                .andExpect(header().string("Access-Control-Allow-Methods", "GET,POST,PUT,PATCH,DELETE,OPTIONS"));
    }
}
