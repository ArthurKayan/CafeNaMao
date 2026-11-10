package com.cafenamao.bebida;

public record BebidaResponse(
        String id,
        String nome,
        String descricao,
        String categoria,
        Integer precoCentavos) {

    public static BebidaResponse de(Bebida bebida) {
        return new BebidaResponse(
                bebida.getId(),
                bebida.getNome(),
                bebida.getDescricao(),
                bebida.getCategoria(),
                bebida.getPrecoCentavos());
    }
}
