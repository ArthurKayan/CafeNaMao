package com.cafenamao.pedido;

import java.time.Instant;
import java.util.List;

public record PedidoResponse(
        String numero,
        Instant criadoEm,
        List<ItemPedidoResponse> itens,
        Integer totalCentavos,
        String pagamento,
        String status) {

    public static PedidoResponse de(Pedido pedido) {
        return new PedidoResponse(
                pedido.getNumero(),
                pedido.getCriadoEm(),
                pedido.getItens().stream().map(ItemPedidoResponse::de).toList(),
                pedido.getTotalCentavos(),
                pedido.getPagamento().getValor(),
                pedido.getStatus().getValor());
    }
}
