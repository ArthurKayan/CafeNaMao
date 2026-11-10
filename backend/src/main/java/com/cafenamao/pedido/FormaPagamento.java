package com.cafenamao.pedido;

import com.cafenamao.comum.RegraNegocioException;

import java.util.Arrays;

public enum FormaPagamento {

    DINHEIRO("dinheiro"),
    CARTAO_FICTICIO("cartao-ficticio");

    private final String valor;

    FormaPagamento(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static FormaPagamento de(String valor) {
        if (valor == null) {
            throw new RegraNegocioException("A forma de pagamento é obrigatória.");
        }

        String normalizado = valor.trim().toLowerCase();
        return Arrays.stream(values())
                .filter(pagamento -> pagamento.valor.equals(normalizado))
                .findFirst()
                .orElseThrow(() -> new RegraNegocioException(
                        "A forma de pagamento é inválida. Use dinheiro ou cartao-ficticio."));
    }
}
