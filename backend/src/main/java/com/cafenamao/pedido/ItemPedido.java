package com.cafenamao.pedido;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "itens_pedido")
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_numero", nullable = false)
    private Pedido pedido;

    @Column(name = "bebida_id", nullable = false, length = 60)
    private String bebidaId;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(name = "preco_centavos", nullable = false)
    private Integer precoCentavos;

    @Column(nullable = false)
    private Integer quantidade;

    protected ItemPedido() {
    }

    public ItemPedido(String bebidaId, String nome, Integer precoCentavos, Integer quantidade) {
        this.bebidaId = bebidaId;
        this.nome = nome;
        this.precoCentavos = precoCentavos;
        this.quantidade = quantidade;
    }

    public Long getId() {
        return id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public String getBebidaId() {
        return bebidaId;
    }

    public String getNome() {
        return nome;
    }

    public Integer getPrecoCentavos() {
        return precoCentavos;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public Integer getSubtotalCentavos() {
        return precoCentavos * quantidade;
    }
}
