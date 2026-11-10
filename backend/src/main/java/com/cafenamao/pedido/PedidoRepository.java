package com.cafenamao.pedido;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, String> {

    List<Pedido> findAllByOrderByCriadoEmDescNumeroDesc();

    List<Pedido> findByStatusOrderByCriadoEmDescNumeroDesc(StatusPedido status);
}
