package com.cafenamao.comum;

import java.time.Instant;
import java.util.List;

public record ErroResposta(
        Instant timestamp,
        int status,
        String erro,
        String mensagem,
        String caminho,
        List<CampoInvalido> campos) {
}
