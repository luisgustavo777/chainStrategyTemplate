package org.example.estrategia;

import org.example.model.Item;
import org.example.model.Pedido;
import org.example.model.PedidoRejeitadoException;

public class ValidacaoItens implements EstrategiaValidacao {

    @Override
    public String nome() {
        return "validação de itens";
    }

    @Override
    public void validar(Pedido pedido) {
        if (pedido.getItens().isEmpty()) {
            throw new PedidoRejeitadoException("pedido sem itens");
        }
        for (Item item : pedido.getItens()) {
            if (item.quantidade() <= 0) {
                throw new PedidoRejeitadoException("quantidade inválida para " + item.nome());
            }
        }
    }
}
