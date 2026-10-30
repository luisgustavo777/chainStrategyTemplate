package org.example.etapa;

import org.example.model.Pedido;
import org.example.model.PedidoRejeitadoException;
import org.example.model.StatusPedido;

public abstract class EtapaPedido {

    private EtapaPedido proxima;

    public static EtapaPedido encadear(EtapaPedido primeira, EtapaPedido... demais) {
        EtapaPedido atual = primeira;
        for (EtapaPedido etapa : demais) {
            atual.proxima = etapa;
            atual = etapa;
        }
        return primeira;
    }

    public final void processar(Pedido pedido) {
        String nome = getClass().getSimpleName();
        try {
            if (aplicavel(pedido)) {
                pedido.registrar(nome + ": " + executar(pedido));
                aposExecutar(pedido);
            } else {
                pedido.registrar(nome + ": ignorada para pedido " + pedido.getTipo());
            }
        } catch (PedidoRejeitadoException e) {
            pedido.setStatus(StatusPedido.REJEITADO);
            pedido.registrar(nome + ": rejeitado - " + e.getMessage());
            return;
        }
        if (proxima != null) {
            proxima.processar(pedido);
        }
    }

    protected boolean aplicavel(Pedido pedido) {
        return true;
    }

    protected abstract String executar(Pedido pedido);

    protected void aposExecutar(Pedido pedido) {
    }
}
