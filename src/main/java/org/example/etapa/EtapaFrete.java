package org.example.etapa;

import java.math.BigDecimal;

import org.example.estrategia.EstrategiaFrete;
import org.example.model.Pedido;
import org.example.model.TipoPedido;

public class EtapaFrete extends EtapaPedido {

    private final EstrategiaFrete estrategia;

    public EtapaFrete(EstrategiaFrete estrategia) {
        this.estrategia = estrategia;
    }

    @Override
    protected boolean aplicavel(Pedido pedido) {
        return pedido.getTipo() == TipoPedido.FISICO;
    }

    @Override
    protected String executar(Pedido pedido) {
        BigDecimal valor = estrategia.calcular(pedido);
        pedido.setFrete(valor);
        return estrategia.nome() + " = R$ " + valor;
    }
}
