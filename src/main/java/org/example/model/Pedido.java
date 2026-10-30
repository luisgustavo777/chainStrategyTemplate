package org.example.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pedido {

    private final String id;
    private final Cliente cliente;
    private final TipoPedido tipo;
    private final List<Item> itens;
    private final List<String> historico = new ArrayList<>();
    private StatusPedido status = StatusPedido.CRIADO;
    private BigDecimal desconto = BigDecimal.ZERO;
    private BigDecimal frete = BigDecimal.ZERO;
    private BigDecimal ajustePagamento = BigDecimal.ZERO;
    private BigDecimal total = BigDecimal.ZERO;

    public Pedido(String id, Cliente cliente, TipoPedido tipo, List<Item> itens) {
        this.id = id;
        this.cliente = cliente;
        this.tipo = tipo;
        this.itens = List.copyOf(itens);
    }

    public BigDecimal subtotal() {
        return itens.stream()
                .map(Item::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int quantidadeTotal() {
        return itens.stream().mapToInt(Item::quantidade).sum();
    }

    public BigDecimal calcularTotal() {
        total = subtotal().subtract(desconto).add(frete).add(ajustePagamento);
        return total;
    }

    public void registrar(String mensagem) {
        historico.add(mensagem);
    }

    public String getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public TipoPedido getTipo() {
        return tipo;
    }

    public List<Item> getItens() {
        return itens;
    }

    public List<String> getHistorico() {
        return Collections.unmodifiableList(historico);
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public BigDecimal getFrete() {
        return frete;
    }

    public void setFrete(BigDecimal frete) {
        this.frete = frete;
    }

    public BigDecimal getAjustePagamento() {
        return ajustePagamento;
    }

    public void setAjustePagamento(BigDecimal ajustePagamento) {
        this.ajustePagamento = ajustePagamento;
    }

    public BigDecimal getTotal() {
        return total;
    }
}
