package com.cafenamao.pedido;

import com.cafenamao.comum.RegraNegocioException;

import java.util.Arrays;

public enum StatusPedido {

    RECEBIDO("recebido"),
    EM_PREPARO("em-preparo"),
    FINALIZADO("finalizado");

    private final String valor;

    StatusPedido(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static StatusPedido de(String valor) {
        if (valor == null) {
            throw new RegraNegocioException("O status é obrigatório.");
        }

        String normalizado = valor.trim().toLowerCase();
        return Arrays.stream(values())
                .filter(status -> status.valor.equals(normalizado))
                .findFirst()
                .orElseThrow(() -> new RegraNegocioException(
                        "O status é inválido. Use recebido, em-preparo ou finalizado."));
    }
}
