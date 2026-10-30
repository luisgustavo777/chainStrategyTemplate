package org.example.estrategia;

import java.math.BigDecimal;

import org.example.model.Pedido;

public interface EstrategiaPagamento {

    String nome();

    BigDecimal ajuste(Pedido pedido);

    boolean autorizar(Pedido pedido);
}
