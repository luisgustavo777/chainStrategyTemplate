package org.example.model;

public class PedidoRejeitadoException extends RuntimeException {

    public PedidoRejeitadoException(String motivo) {
        super(motivo);
    }
}
