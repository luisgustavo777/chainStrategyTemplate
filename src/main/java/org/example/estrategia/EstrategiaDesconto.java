package org.example.estrategia;

import java.math.BigDecimal;

import org.example.model.Pedido;

public interface EstrategiaDesconto {

    String nome();

    BigDecimal calcular(Pedido pedido);
}
