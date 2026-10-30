package org.example.estrategia;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.example.model.Pedido;

public class DescontoPorVolume implements EstrategiaDesconto {

    private static final int QUANTIDADE_MINIMA = 5;
    private static final BigDecimal PERCENTUAL = new BigDecimal("0.05");

    @Override
    public String nome() {
        return "desconto por volume";
    }

    @Override
    public BigDecimal calcular(Pedido pedido) {
        if (pedido.quantidadeTotal() < QUANTIDADE_MINIMA) {
            return BigDecimal.ZERO.setScale(2);
        }
        return pedido.subtotal().multiply(PERCENTUAL).setScale(2, RoundingMode.HALF_UP);
    }
}
