package org.example.estrategia;

import org.example.model.Item;
import org.example.model.Pedido;
import org.example.model.PedidoRejeitadoException;
import org.example.service.Estoque;

public class ValidacaoEstoque implements EstrategiaValidacao {

    private final Estoque estoque;

    public ValidacaoEstoque(Estoque estoque) {
        this.estoque = estoque;
    }

    @Override
    public String nome() {
        return "validação de estoque";
    }

    @Override
    public void validar(Pedido pedido) {
        for (Item item : pedido.getItens()) {
            if (!estoque.disponivel(item.nome(), item.quantidade())) {
                throw new PedidoRejeitadoException("sem estoque de " + item.nome());
            }
        }
    }
}
