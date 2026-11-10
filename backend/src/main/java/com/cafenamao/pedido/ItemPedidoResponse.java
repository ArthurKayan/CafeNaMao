package com.cafenamao.pedido;

public record ItemPedidoResponse(
        String bebidaId,
        String nome,
        Integer precoCentavos,
        Integer quantidade,
        Integer subtotalCentavos) {

    public static ItemPedidoResponse de(ItemPedido item) {
        return new ItemPedidoResponse(
                item.getBebidaId(),
                item.getNome(),
                item.getPrecoCentavos(),
                item.getQuantidade(),
                item.getSubtotalCentavos());
    }
}
