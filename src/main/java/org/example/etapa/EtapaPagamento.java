package org.example.etapa;

import org.example.estrategia.EstrategiaPagamento;
import org.example.model.Pedido;
import org.example.model.PedidoRejeitadoException;
import org.example.model.StatusPedido;

public class EtapaPagamento extends EtapaPedido {

    private final EstrategiaPagamento estrategia;

    public EtapaPagamento(EstrategiaPagamento estrategia) {
        this.estrategia = estrategia;
    }

    @Override
    protected String executar(Pedido pedido) {
        pedido.setAjustePagamento(estrategia.ajuste(pedido));
        pedido.calcularTotal();
        if (!estrategia.autorizar(pedido)) {
            throw new PedidoRejeitadoException("pagamento recusado via " + estrategia.nome());
        }
        return "pago via " + estrategia.nome() + " = R$ " + pedido.getTotal();
    }

    @Override
    protected void aposExecutar(Pedido pedido) {
        pedido.setStatus(StatusPedido.PAGO);
    }
}
