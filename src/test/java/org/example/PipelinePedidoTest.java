package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.example.estrategia.AtivacaoAssinatura;
import org.example.estrategia.DescontoClienteNovo;
import org.example.estrategia.DescontoPorVolume;
import org.example.estrategia.EntregaDigital;
import org.example.estrategia.EntregaFisica;
import org.example.estrategia.EstrategiaDesconto;
import org.example.estrategia.EstrategiaFrete;
import org.example.estrategia.EstrategiaPagamento;
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
import org.example.model.StatusPedido;
import org.example.model.TipoPedido;
import org.example.service.Estoque;
import org.junit.jupiter.api.Test;

class PipelinePedidoTest {

    private static final BigDecimal LIMITE_ANTIFRAUDE = new BigDecimal("5000.00");

    private final Estoque estoque = new Estoque().adicionar("Teclado", 10);

    private EtapaPedido pipelineFisico(EstrategiaDesconto desconto, EstrategiaFrete frete, EstrategiaPagamento pagamento) {
        return EtapaPedido.encadear(
                new EtapaValidacao(new ValidacaoItens()),
                new EtapaValidacao(new ValidacaoEstoque(estoque)),
                new EtapaValidacao(new ValidacaoAntifraude(LIMITE_ANTIFRAUDE)),
                new EtapaDesconto(desconto),
                new EtapaFrete(frete),
                new EtapaPagamento(pagamento),
                new EtapaEntrega(new EntregaFisica(estoque)));
    }

    private EtapaPedido pipelineDigital(EstrategiaPagamento pagamento) {
        return EtapaPedido.encadear(
                new EtapaValidacao(new ValidacaoItens()),
                new EtapaValidacao(new ValidacaoAntifraude(LIMITE_ANTIFRAUDE)),
                new EtapaDesconto(new DescontoClienteNovo()),
                new EtapaFrete(EstrategiaFrete.economico()),
                new EtapaPagamento(pagamento),
                new EtapaEntrega(new EntregaDigital()));
    }

    private Pedido pedido(TipoPedido tipo, Cliente cliente, String produto, String preco, int quantidade) {
        return new Pedido("T-1", cliente, tipo, List.of(new Item(produto, new BigDecimal(preco), quantidade)));
    }

    @Test
    void pedidoFisicoAplicaDescontoFretePagamentoEBaixaEstoque() {
        Pedido pedido = pedido(TipoPedido.FISICO, new Cliente("Ana", false), "Teclado", "100.00", 5);

        pipelineFisico(new DescontoPorVolume(), EstrategiaFrete.economico(), new PagamentoPix()).processar(pedido);

        assertEquals(StatusPedido.CONCLUIDO, pedido.getStatus());
        assertEquals(new BigDecimal("25.00"), pedido.getDesconto());
        assertEquals(new BigDecimal("15.00"), pedido.getFrete());
        assertEquals(new BigDecimal("-23.75"), pedido.getAjustePagamento());
        assertEquals(new BigDecimal("466.25"), pedido.getTotal());
        assertEquals(5, estoque.saldo("Teclado"));
    }

    @Test
    void historicoRegistraAsEtapasNaOrdemDaCadeia() {
        Pedido pedido = pedido(TipoPedido.FISICO, new Cliente("Ana", false), "Teclado", "100.00", 5);

        pipelineFisico(new DescontoPorVolume(), EstrategiaFrete.economico(), new PagamentoPix()).processar(pedido);

        List<String> etapas = pedido.getHistorico().stream().map(h -> h.substring(0, h.indexOf(':'))).toList();
        assertEquals(List.of("EtapaValidacao", "EtapaValidacao", "EtapaValidacao",
                "EtapaDesconto", "EtapaFrete", "EtapaPagamento", "EtapaEntrega"), etapas);
    }

    @Test
    void trocarAEstrategiaMudaOResultadoSemMexerNaCadeia() {
        Pedido comPix = pedido(TipoPedido.FISICO, new Cliente("Ana", false), "Teclado", "100.00", 1);
        Pedido comBoleto = pedido(TipoPedido.FISICO, new Cliente("Ana", false), "Teclado", "100.00", 1);

        pipelineFisico(new SemDesconto(), EstrategiaFrete.retiradaNaLoja(), new PagamentoPix()).processar(comPix);
        pipelineFisico(new SemDesconto(), EstrategiaFrete.retiradaNaLoja(), new PagamentoBoleto()).processar(comBoleto);

        assertEquals(new BigDecimal("95.00"), comPix.getTotal());
        assertEquals(new BigDecimal("103.50"), comBoleto.getTotal());
    }

    @Test
    void falhaNaValidacaoInterrompeACadeiaAntesDasEtapasSeguintes() {
        Pedido pedido = pedido(TipoPedido.FISICO, new Cliente("Ana", false), "Teclado", "100.00", 11);

        pipelineFisico(new DescontoPorVolume(), EstrategiaFrete.expresso(), new PagamentoPix()).processar(pedido);

        assertEquals(StatusPedido.REJEITADO, pedido.getStatus());
        assertEquals(BigDecimal.ZERO, pedido.getTotal());
        assertEquals(10, estoque.saldo("Teclado"));
        assertTrue(pedido.getHistorico().stream().noneMatch(h -> h.startsWith("EtapaDesconto")));
        assertTrue(pedido.getHistorico().stream().noneMatch(h -> h.startsWith("EtapaEntrega")));
        assertTrue(pedido.getHistorico().getLast().contains("sem estoque de Teclado"));
    }

    @Test
    void antifraudeRetemClienteNovoComValorAlto() {
        Pedido pedido = pedido(TipoPedido.DIGITAL, new Cliente("Diego", true), "Curso", "6000.00", 1);

        pipelineDigital(new PagamentoPix()).processar(pedido);

        assertEquals(StatusPedido.REJEITADO, pedido.getStatus());
        assertTrue(pedido.getHistorico().getLast().contains("antifraude"));
    }

    @Test
    void antifraudeLiberaClienteAntigoComMesmoValor() {
        Pedido pedido = pedido(TipoPedido.DIGITAL, new Cliente("Diego", false), "Curso", "6000.00", 1);

        pipelineDigital(new PagamentoPix()).processar(pedido);

        assertEquals(StatusPedido.CONCLUIDO, pedido.getStatus());
    }

    @Test
    void etapaDeFreteEhIgnoradaParaPedidoDigital() {
        Pedido pedido = pedido(TipoPedido.DIGITAL, new Cliente("Bruno", false), "Curso", "300.00", 1);

        pipelineDigital(new PagamentoBoleto()).processar(pedido);

        assertEquals(new BigDecimal("0"), pedido.getFrete().stripTrailingZeros());
        assertTrue(pedido.getHistorico().stream().anyMatch(h -> h.startsWith("EtapaFrete: ignorada")));
        assertEquals(new BigDecimal("303.50"), pedido.getTotal());
    }

    @Test
    void clienteNovoRecebeDescontoDePrimeiraCompra() {
        Pedido pedido = pedido(TipoPedido.DIGITAL, new Cliente("Bruno", true), "Curso", "300.00", 1);

        pipelineDigital(new PagamentoBoleto()).processar(pedido);

        assertEquals(new BigDecimal("30.00"), pedido.getDesconto());
        assertEquals(new BigDecimal("273.50"), pedido.getTotal());
    }

    @Test
    void cartaoSemLimiteRecusaOPagamentoENaoEntrega() {
        Pedido pedido = pedido(TipoPedido.DIGITAL, new Cliente("Bruno", false), "Curso", "300.00", 1);

        pipelineDigital(new PagamentoCartao(new BigDecimal("100.00"))).processar(pedido);

        assertEquals(StatusPedido.REJEITADO, pedido.getStatus());
        assertTrue(pedido.getHistorico().getLast().contains("pagamento recusado via Cartão"));
        assertFalse(pedido.getHistorico().stream().anyMatch(h -> h.startsWith("EtapaEntrega")));
    }

    @Test
    void assinaturaPercorreSomenteAsEtapasConfiguradas() {
        Pedido pedido = pedido(TipoPedido.ASSINATURA, new Cliente("Carla", false), "Plano", "49.90", 1);
        EtapaPedido pipeline = EtapaPedido.encadear(
                new EtapaValidacao(new ValidacaoItens()),
                new EtapaDesconto(new SemDesconto()),
                new EtapaPagamento(new PagamentoBoleto()),
                new EtapaEntrega(new AtivacaoAssinatura()));

        pipeline.processar(pedido);

        assertEquals(StatusPedido.CONCLUIDO, pedido.getStatus());
        assertEquals(4, pedido.getHistorico().size());
        assertEquals(new BigDecimal("53.40"), pedido.getTotal());
    }
}
