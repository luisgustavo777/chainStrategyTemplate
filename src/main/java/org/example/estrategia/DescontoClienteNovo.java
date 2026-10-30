package org.example.estrategia;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.example.model.Pedido;

public class DescontoClienteNovo implements EstrategiaDesconto {

    private static final BigDecimal PERCENTUAL = new BigDecimal("0.10");

    @Override
    public String nome() {
        return "desconto de primeira compra";
    }

    @Override
    public BigDecimal calcular(Pedido pedido) {
        if (!pedido.getCliente().novo()) {
            return BigDecimal.ZERO.setScale(2);
        }
        return pedido.subtotal().multiply(PERCENTUAL).setScale(2, RoundingMode.HALF_UP);
    }
}
