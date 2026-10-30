package org.example.estrategia;

import java.math.BigDecimal;

import org.example.model.Pedido;

public class PagamentoBoleto implements EstrategiaPagamento {

    private static final BigDecimal TAXA = new BigDecimal("3.50");

    @Override
    public String nome() {
        return "Boleto";
    }

    @Override
    public BigDecimal ajuste(Pedido pedido) {
        return TAXA;
    }

    @Override
    public boolean autorizar(Pedido pedido) {
        return true;
    }
}
