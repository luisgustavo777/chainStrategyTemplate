package org.example.estrategia;

import java.math.BigDecimal;

import org.example.model.Pedido;

public interface EstrategiaFrete {

    String nome();

    BigDecimal calcular(Pedido pedido);

    static EstrategiaFrete economico() {
        return new FreteFixo("frete econômico", new BigDecimal("15.00"));
    }

    static EstrategiaFrete expresso() {
        return new FreteFixo("frete expresso", new BigDecimal("35.00"));
    }

    static EstrategiaFrete retiradaNaLoja() {
        return new FreteFixo("retirada na loja", new BigDecimal("0.00"));
    }

    record FreteFixo(String nome, BigDecimal valor) implements EstrategiaFrete {

        @Override
        public BigDecimal calcular(Pedido pedido) {
            return valor;
        }
    }
}
