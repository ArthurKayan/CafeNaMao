package com.cafenamao.bebida;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BebidaAtualizacaoRequest(
        @NotBlank(message = "O nome é obrigatório.")
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres.")
        String nome,

        @NotBlank(message = "A descrição é obrigatória.")
        @Size(max = 300, message = "A descrição deve ter no máximo 300 caracteres.")
        String descricao,

        @NotBlank(message = "A categoria é obrigatória.")
        @Size(max = 40, message = "A categoria deve ter no máximo 40 caracteres.")
        String categoria,

        @NotNull(message = "O preço em centavos é obrigatório.")
        @Min(value = 0, message = "O preço em centavos não pode ser negativo.")
        @Max(value = 1_000_000, message = "O preço em centavos excede o limite permitido.")
        Integer precoCentavos) {
}
