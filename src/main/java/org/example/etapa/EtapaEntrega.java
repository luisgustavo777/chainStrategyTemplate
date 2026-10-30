package org.example.etapa;

import org.example.estrategia.EstrategiaEntrega;
import org.example.model.Pedido;
import org.example.model.StatusPedido;

public class EtapaEntrega extends EtapaPedido {

    private final EstrategiaEntrega estrategia;

    public EtapaEntrega(EstrategiaEntrega estrategia) {
        this.estrategia = estrategia;
    }

    @Override
    protected String executar(Pedido pedido) {
        return estrategia.entregar(pedido);
    }

    @Override
    protected void aposExecutar(Pedido pedido) {
        pedido.setStatus(StatusPedido.CONCLUIDO);
    }
}
