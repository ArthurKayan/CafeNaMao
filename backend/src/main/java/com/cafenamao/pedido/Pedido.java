package com.cafenamao.pedido;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @Column(nullable = false, length = 20)
    private String numero;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "total_centavos", nullable = false)
    private Integer totalCentavos;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FormaPagamento pagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusPedido status;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<ItemPedido> itens = new ArrayList<>();

    protected Pedido() {
    }

    public Pedido(String numero, Instant criadoEm, FormaPagamento pagamento, StatusPedido status) {
        this.numero = numero;
        this.criadoEm = criadoEm;
        this.pagamento = pagamento;
        this.status = status;
        this.totalCentavos = 0;
    }

    public void adicionarItem(ItemPedido item) {
        item.setPedido(this);
        itens.add(item);
    }

    public String getNumero() {
        return numero;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Integer getTotalCentavos() {
        return totalCentavos;
    }

    public void setTotalCentavos(Integer totalCentavos) {
        this.totalCentavos = totalCentavos;
    }

    public FormaPagamento getPagamento() {
        return pagamento;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }
}
