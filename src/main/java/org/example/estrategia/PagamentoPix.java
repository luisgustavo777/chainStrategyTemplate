package org.example.estrategia;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.example.model.Pedido;

public class PagamentoPix implements EstrategiaPagamento {

    private static final BigDecimal PERCENTUAL = new BigDecimal("0.05");

    @Override
    public String nome() {
        return "Pix";
    }

    @Override
    public BigDecimal ajuste(Pedido pedido) {
        BigDecimal base = pedido.subtotal().subtract(pedido.getDesconto());
        return base.multiply(PERCENTUAL).setScale(2, RoundingMode.HALF_UP).negate();
    }

    @Override
    public boolean autorizar(Pedido pedido) {
        return true;
    }
}
