package com.cafenamao.config;

import com.cafenamao.bebida.Bebida;
import com.cafenamao.bebida.BebidaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DadosIniciais implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DadosIniciais.class);

    private final BebidaRepository bebidaRepository;

    public DadosIniciais(BebidaRepository bebidaRepository) {
        this.bebidaRepository = bebidaRepository;
    }

    @Override
    public void run(String... args) {
        if (bebidaRepository.count() > 0) {
            return;
        }

        bebidaRepository.saveAll(List.of(
                new Bebida("espresso-curto", "Espresso Curto",
                        "Café intenso e encorpado, preparado em dose curta.", "Café", 400),
                new Bebida("cafe-com-leite", "Café com Leite",
                        "Espresso suave com leite vaporizado e cremoso.", "Café", 550),
                new Bebida("cappuccino", "Cappuccino",
                        "Café, leite vaporizado e uma camada generosa de espuma.", "Café", 750),
                new Bebida("mocha", "Mocha",
                        "Espresso com chocolate, leite e finalização cremosa.", "Café", 850),
                new Bebida("chocolate-quente", "Chocolate Quente",
                        "Chocolate dissolvido lentamente em leite quente.", "Chocolate", 700),
                new Bebida("cha-de-canela", "Chá de Canela",
                        "Infusão aromática com canela e um toque de mel.", "Chá", 500)));

        log.info("Catálogo inicial do Café na Mão carregado com {} bebidas.", bebidaRepository.count());
    }
}
