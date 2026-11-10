package com.cafenamao.pedido;

import jakarta.validation.constraints.NotBlank;

public record AtualizarStatusRequest(
        @NotBlank(message = "O status é obrigatório.")
        String status) {
}
