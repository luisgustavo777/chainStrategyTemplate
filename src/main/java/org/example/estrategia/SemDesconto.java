package org.example.estrategia;

import java.math.BigDecimal;

import org.example.model.Pedido;

public class SemDesconto implements EstrategiaDesconto {

    @Override
    public String nome() {
        return "sem desconto";
    }

    @Override
    public BigDecimal calcular(Pedido pedido) {
        return BigDecimal.ZERO.setScale(2);
    }
}
