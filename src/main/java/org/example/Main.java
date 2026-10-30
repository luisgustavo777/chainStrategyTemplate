package org.example;

import java.math.BigDecimal;
import java.util.List;

import org.example.estrategia.AtivacaoAssinatura;
import org.example.estrategia.DescontoClienteNovo;
import org.example.estrategia.DescontoPorVolume;
import org.example.estrategia.EntregaDigital;
import org.example.estrategia.EntregaFisica;
import org.example.estrategia.EstrategiaFrete;
import org.example.estrategia.PagamentoBoleto;
import org.example.estrategia.PagamentoCartao;
import org.example.estrategia.PagamentoPix;
import org.example.estrategia.SemDesconto;
import org.example.estrategia.ValidacaoAntifraude;
import org.example.estrategia.ValidacaoEstoque;
import org.example.estrategia.ValidacaoItens;
import org.example.etapa.EtapaDesconto;
import org.example.etapa.EtapaEntrega;
import org.example.etapa.EtapaFrete;
import org.example.etapa.EtapaPagamento;
import org.example.etapa.EtapaPedido;
import org.example.etapa.EtapaValidacao;
import org.example.model.Cliente;
import org.example.model.Item;
import org.example.model.Pedido;
import org.example.model.TipoPedido;
import org.example.service.Estoque;

public class Main {

    public static void main(String[] args) {
        Estoque estoque = new Estoque().adicionar("Teclado", 10);
        BigDecimal limiteAntifraude = new BigDecimal("5000.00");

        EtapaPedido pipelineFisico = EtapaPedido.encadear(
                new EtapaValidacao(new ValidacaoItens()),
                new EtapaValidacao(new ValidacaoEstoque(estoque)),
                new EtapaValidacao(new ValidacaoAntifraude(limiteAntifraude)),
                new EtapaDesconto(new DescontoPorVolume()),
                new EtapaFrete(EstrategiaFrete.expresso()),
                new EtapaPagamento(new PagamentoPix()),
                new EtapaEntrega(new EntregaFisica(estoque)));

        EtapaPedido pipelineDigital = EtapaPedido.encadear(
                new EtapaValidacao(new ValidacaoItens()),
                new EtapaValidacao(new ValidacaoAntifraude(limiteAntifraude)),
                new EtapaDesconto(new DescontoClienteNovo()),
                new EtapaFrete(EstrategiaFrete.economico()),
                new EtapaPagamento(new PagamentoCartao(new BigDecimal("10000.00"))),
                new EtapaEntrega(new EntregaDigital()));

        EtapaPedido pipelineAssinatura = EtapaPedido.encadear(
                new EtapaValidacao(new ValidacaoItens()),
                new EtapaDesconto(new SemDesconto()),
                new EtapaPagamento(new PagamentoBoleto()),
                new EtapaEntrega(new AtivacaoAssinatura()));

        Pedido fisico = new Pedido("P-001", new Cliente("Ana", false), TipoPedido.FISICO,
                List.of(new Item("Teclado", new BigDecimal("100.00"), 5)));
        Pedido digital = new Pedido("P-002", new Cliente("Bruno", true), TipoPedido.DIGITAL,
                List.of(new Item("Curso de Java", new BigDecimal("300.00"), 1)));
        Pedido digitalRetido = new Pedido("P-003", new Cliente("Diego", true), TipoPedido.DIGITAL,
                List.of(new Item("Curso Avançado", new BigDecimal("6000.00"), 1)));
        Pedido assinatura = new Pedido("P-004", new Cliente("Carla", false), TipoPedido.ASSINATURA,
                List.of(new Item("Plano Pro", new BigDecimal("49.90"), 1)));

        pipelineFisico.processar(fisico);
        pipelineDigital.processar(digital);
        pipelineDigital.processar(digitalRetido);
        pipelineAssinatura.processar(assinatura);

        for (Pedido pedido : List.of(fisico, digital, digitalRetido, assinatura)) {
            System.out.println("=== " + pedido.getId() + " | " + pedido.getStatus() + " | total R$ " + pedido.getTotal());
            pedido.getHistorico().forEach(linha -> System.out.println("  - " + linha));
        }
    }
}
