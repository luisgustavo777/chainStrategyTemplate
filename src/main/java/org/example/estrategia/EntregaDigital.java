package org.example.estrategia;

import org.example.model.Pedido;

public class EntregaDigital implements EstrategiaEntrega {

    @Override
    public String entregar(Pedido pedido) {
        return "link de download enviado para " + pedido.getCliente().nome();
    }
}
