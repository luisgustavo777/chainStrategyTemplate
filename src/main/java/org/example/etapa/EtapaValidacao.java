package org.example.etapa;

import org.example.estrategia.EstrategiaValidacao;
import org.example.model.Pedido;
import org.example.model.StatusPedido;

public class EtapaValidacao extends EtapaPedido {

    private final EstrategiaValidacao estrategia;

    public EtapaValidacao(EstrategiaValidacao estrategia) {
        this.estrategia = estrategia;
    }

    @Override
    protected String executar(Pedido pedido) {
        estrategia.validar(pedido);
        return "aprovado por " + estrategia.nome();
    }

    @Override
    protected void aposExecutar(Pedido pedido) {
        pedido.setStatus(StatusPedido.VALIDADO);
    }
}
