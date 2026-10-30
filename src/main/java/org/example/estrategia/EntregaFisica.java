package org.example.estrategia;

import org.example.model.Item;
import org.example.model.Pedido;
import org.example.service.Estoque;

public class EntregaFisica implements EstrategiaEntrega {

    private final Estoque estoque;

    public EntregaFisica(Estoque estoque) {
        this.estoque = estoque;
    }

    @Override
    public String entregar(Pedido pedido) {
        for (Item item : pedido.getItens()) {
            estoque.baixar(item.nome(), item.quantidade());
        }
        return "estoque baixado e pedido enviado para expedição";
    }
}
