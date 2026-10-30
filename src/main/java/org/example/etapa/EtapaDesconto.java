package org.example.etapa;

import java.math.BigDecimal;

import org.example.estrategia.EstrategiaDesconto;
import org.example.model.Pedido;

public class EtapaDesconto extends EtapaPedido {

    private final EstrategiaDesconto estrategia;

    public EtapaDesconto(EstrategiaDesconto estrategia) {
        this.estrategia = estrategia;
    }

    @Override
    protected String executar(Pedido pedido) {
        BigDecimal valor = estrategia.calcular(pedido);
        pedido.setDesconto(valor);
        return estrategia.nome() + " = R$ " + valor;
    }
}
