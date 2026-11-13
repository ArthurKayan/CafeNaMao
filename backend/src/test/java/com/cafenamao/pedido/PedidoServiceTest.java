package com.cafenamao.pedido;

import com.cafenamao.bebida.Bebida;
import com.cafenamao.bebida.BebidaRepository;
import com.cafenamao.comum.RecursoNaoEncontradoException;
import com.cafenamao.comum.RegraNegocioException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private BebidaRepository bebidaRepository;

    @InjectMocks
    private PedidoService pedidoService;

    @ParameterizedTest
    @ValueSource(ints = {1, 10})
    void aceitaQuantidadesNosLimites(int quantidade) {
        // Arrange
        Bebida mocha = new Bebida("mocha", "Mocha", "Café com chocolate", "café", 850);
        when(bebidaRepository.findById("mocha")).thenReturn(Optional.of(mocha));
        when(pedidoRepository.existsById(anyString())).thenReturn(false);
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));
        PedidoRequest requisicao = new PedidoRequest(
                "dinheiro",
                List.of(new ItemPedidoRequest("mocha", quantidade)));

        // Act
        PedidoResponse resposta = pedidoService.criar(requisicao);

        // Assert
        assertAll(
                () -> assertEquals(850 * quantidade, resposta.totalCentavos()),
                () -> assertEquals(quantidade, resposta.itens().get(0).quantidade()),
                () -> assertEquals("recebido", resposta.status()));
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 11})
    void rejeitaQuantidadesForaDosLimites(int quantidade) {
        // Arrange
        PedidoRequest requisicao = new PedidoRequest(
                "dinheiro",
                List.of(new ItemPedidoRequest("mocha", quantidade)));

        // Act
        RegraNegocioException erro = assertThrows(
                RegraNegocioException.class,
                () -> pedidoService.criar(requisicao));

        // Assert
        assertEquals("A quantidade deve estar entre 1 e 10.", erro.getMessage());
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void rejeitaBebidaRepetidaNoMesmoPedido() {
        // Arrange
        Bebida mocha = new Bebida("mocha", "Mocha", "Café com chocolate", "café", 850);
        when(bebidaRepository.findById("mocha")).thenReturn(Optional.of(mocha));
        when(pedidoRepository.existsById(anyString())).thenReturn(false);
        PedidoRequest requisicao = new PedidoRequest(
                "dinheiro",
                List.of(
                        new ItemPedidoRequest("mocha", 1),
                        new ItemPedidoRequest("mocha", 2)));

        // Act
        RegraNegocioException erro = assertThrows(
                RegraNegocioException.class,
                () -> pedidoService.criar(requisicao));

        // Assert
        assertEquals("A bebida mocha foi informada mais de uma vez.", erro.getMessage());
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void rejeitaBebidaInexistenteNoPedido() {
        // Arrange
        when(pedidoRepository.existsById(anyString())).thenReturn(false);
        when(bebidaRepository.findById("nao-existe")).thenReturn(Optional.empty());
        PedidoRequest requisicao = new PedidoRequest(
                "dinheiro",
                List.of(new ItemPedidoRequest("nao-existe", 1)));

        // Act
        RegraNegocioException erro = assertThrows(
                RegraNegocioException.class,
                () -> pedidoService.criar(requisicao));

        // Assert
        assertEquals("Bebida não encontrada: nao-existe.", erro.getMessage());
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void rejeitaTotalAcimaDoLimiteInteiro() {
        // Arrange
        Bebida cara = new Bebida("cara", "Cara", "Bebida de teste", "teste", 300_000_000);
        when(bebidaRepository.findById("cara")).thenReturn(Optional.of(cara));
        when(pedidoRepository.existsById(anyString())).thenReturn(false);
        PedidoRequest requisicao = new PedidoRequest(
                "dinheiro",
                List.of(new ItemPedidoRequest("cara", 10)));

        // Act
        RegraNegocioException erro = assertThrows(
                RegraNegocioException.class,
                () -> pedidoService.criar(requisicao));

        // Assert
        assertEquals("O total do pedido excede o limite permitido.", erro.getMessage());
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void atualizaStatusDoPedidoExistente() {
        // Arrange
        Pedido pedido = new Pedido(
                "PED-12345678",
                Instant.parse("2026-11-13T12:00:00Z"),
                FormaPagamento.DINHEIRO,
                StatusPedido.RECEBIDO);
        when(pedidoRepository.findById("PED-12345678")).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        PedidoResponse resposta = pedidoService.atualizarStatus("PED-12345678", "em-preparo");

        // Assert
        assertEquals("em-preparo", resposta.status());
        assertEquals(StatusPedido.EM_PREPARO, pedido.getStatus());
    }

    @Test
    void lancaErroAoBuscarPedidoInexistente() {
        // Arrange
        when(pedidoRepository.findById("PED-inexistente")).thenReturn(Optional.empty());

        // Act
        RecursoNaoEncontradoException erro = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> pedidoService.buscar("PED-inexistente"));

        // Assert
        assertEquals("Pedido não encontrado: PED-inexistente.", erro.getMessage());
    }
}
