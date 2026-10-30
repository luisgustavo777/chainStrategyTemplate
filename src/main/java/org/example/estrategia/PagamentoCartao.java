package org.example.estrategia;

import java.math.BigDecimal;

import org.example.model.Pedido;

public class PagamentoCartao implements EstrategiaPagamento {

    private final BigDecimal limite;

    public PagamentoCartao(BigDecimal limite) {
        this.limite = limite;
    }

    @Override
    public String nome() {
        return "Cartão";
    }

    @Override
    public BigDecimal ajuste(Pedido pedido) {
        return new BigDecimal("0.00");
    }

    @Override
    public boolean autorizar(Pedido pedido) {
        return pedido.getTotal().compareTo(limite) <= 0;
    }
}
