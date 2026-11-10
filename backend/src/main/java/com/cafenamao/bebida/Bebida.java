package com.cafenamao.bebida;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bebidas")
public class Bebida {

    @Id
    @Column(nullable = false, length = 60)
    private String id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, length = 300)
    private String descricao;

    @Column(nullable = false, length = 40)
    private String categoria;

    @Column(name = "preco_centavos", nullable = false)
    private Integer precoCentavos;

    protected Bebida() {
    }

    public Bebida(String id, String nome, String descricao, String categoria, Integer precoCentavos) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.categoria = categoria;
        this.precoCentavos = precoCentavos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Integer getPrecoCentavos() {
        return precoCentavos;
    }

    public void setPrecoCentavos(Integer precoCentavos) {
        this.precoCentavos = precoCentavos;
    }
}
