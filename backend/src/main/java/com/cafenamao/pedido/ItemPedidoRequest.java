package com.cafenamao.pedido;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ItemPedidoRequest(
        @NotBlank(message = "A bebida é obrigatória.")
        String bebidaId,

        @NotNull(message = "A quantidade é obrigatória.")
        @Min(value = 1, message = "A quantidade mínima é 1.")
        @Max(value = 10, message = "A quantidade máxima é 10.")
        Integer quantidade) {
}
