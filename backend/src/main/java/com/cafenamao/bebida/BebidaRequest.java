package com.cafenamao.bebida;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BebidaRequest(
        @NotBlank(message = "O id é obrigatório.")
        @Size(max = 60, message = "O id deve ter no máximo 60 caracteres.")
        @Pattern(
                regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
                message = "O id deve conter apenas letras minúsculas, números e hífens.")
        String id,

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
