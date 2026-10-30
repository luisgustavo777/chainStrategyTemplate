package org.example.estrategia;

import org.example.model.Pedido;

public class AtivacaoAssinatura implements EstrategiaEntrega {

    @Override
    public String entregar(Pedido pedido) {
        return "assinatura ativada para " + pedido.getCliente().nome();
    }
}
