package org.example.service;

import java.util.HashMap;
import java.util.Map;

public class Estoque {

    private final Map<String, Integer> saldos = new HashMap<>();

    public Estoque adicionar(String produto, int quantidade) {
        saldos.merge(produto, quantidade, Integer::sum);
        return this;
    }

    public boolean disponivel(String produto, int quantidade) {
        return saldos.getOrDefault(produto, 0) >= quantidade;
    }

    public void baixar(String produto, int quantidade) {
        if (!disponivel(produto, quantidade)) {
            throw new IllegalStateException("Estoque insuficiente para " + produto);
        }
        saldos.merge(produto, -quantidade, Integer::sum);
    }

    public int saldo(String produto) {
        return saldos.getOrDefault(produto, 0);
    }
}
