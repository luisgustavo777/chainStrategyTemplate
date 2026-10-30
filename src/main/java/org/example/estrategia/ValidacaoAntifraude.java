package org.example.estrategia;

import java.math.BigDecimal;

import org.example.model.Pedido;
import org.example.model.PedidoRejeitadoException;

public class ValidacaoAntifraude implements EstrategiaValidacao {

    private final BigDecimal limiteClienteNovo;

    public ValidacaoAntifraude(BigDecimal limiteClienteNovo) {
        this.limiteClienteNovo = limiteClienteNovo;
    }

    @Override
    public String nome() {
        return "análise antifraude";
    }

    @Override
    public void validar(Pedido pedido) {
        boolean suspeito = pedido.getCliente().novo()
                && pedido.subtotal().compareTo(limiteClienteNovo) > 0;
        if (suspeito) {
            throw new PedidoRejeitadoException("retido pelo antifraude (cliente novo com valor alto)");
        }
    }
}
