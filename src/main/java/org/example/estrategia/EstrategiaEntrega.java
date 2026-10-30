package org.example.estrategia;

import org.example.model.Pedido;

public interface EstrategiaEntrega {

    String entregar(Pedido pedido);
}
