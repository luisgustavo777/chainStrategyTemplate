package org.example.estrategia;

import org.example.model.Pedido;

public interface EstrategiaValidacao {

    String nome();

    void validar(Pedido pedido);
}
