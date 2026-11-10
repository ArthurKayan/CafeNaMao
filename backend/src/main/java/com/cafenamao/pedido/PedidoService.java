package com.cafenamao.pedido;

import com.cafenamao.bebida.Bebida;
import com.cafenamao.bebida.BebidaRepository;
import com.cafenamao.comum.RecursoNaoEncontradoException;
import com.cafenamao.comum.RegraNegocioException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PedidoService {

    private static final int QUANTIDADE_MINIMA = 1;
    private static final int QUANTIDADE_MAXIMA = 10;
    private static final int TENTATIVAS_NUMERO = 10;

    private final PedidoRepository pedidoRepository;
    private final BebidaRepository bebidaRepository;

    public PedidoService(PedidoRepository pedidoRepository, BebidaRepository bebidaRepository) {
        this.pedidoRepository = pedidoRepository;
        this.bebidaRepository = bebidaRepository;
    }

    public List<PedidoResponse> listar(String status) {
        List<Pedido> pedidos;

        if (status == null || status.isBlank()) {
            pedidos = pedidoRepository.findAllByOrderByCriadoEmDescNumeroDesc();
        } else {
            pedidos = pedidoRepository.findByStatusOrderByCriadoEmDescNumeroDesc(StatusPedido.de(status));
        }

        return pedidos.stream().map(PedidoResponse::de).toList();
    }

    public PedidoResponse buscar(String numero) {
        return PedidoResponse.de(localizar(numero));
    }

    @Transactional
    public PedidoResponse criar(PedidoRequest requisicao) {
        FormaPagamento pagamento = FormaPagamento.de(requisicao.pagamento());
        validarItens(requisicao.itens());

        Pedido pedido = new Pedido(gerarNumero(), Instant.now(), pagamento, StatusPedido.RECEBIDO);
        Set<String> bebidasAdicionadas = new HashSet<>();
        long total = 0;

        for (ItemPedidoRequest item : requisicao.itens()) {
            String bebidaId = item.bebidaId().trim();
            int quantidade = item.quantidade();

            if (!bebidasAdicionadas.add(bebidaId)) {
                throw new RegraNegocioException("A bebida " + bebidaId + " foi informada mais de uma vez.");
            }

            Bebida bebida = bebidaRepository.findById(bebidaId)
                    .orElseThrow(() -> new RegraNegocioException("Bebida não encontrada: " + bebidaId + "."));

            pedido.adicionarItem(new ItemPedido(
                    bebida.getId(),
                    bebida.getNome(),
                    bebida.getPrecoCentavos(),
                    quantidade));

            total += (long) bebida.getPrecoCentavos() * quantidade;
        }

        if (total > Integer.MAX_VALUE) {
            throw new RegraNegocioException("O total do pedido excede o limite permitido.");
        }

        pedido.setTotalCentavos((int) total);
        return PedidoResponse.de(pedidoRepository.save(pedido));
    }

    @Transactional
    public PedidoResponse atualizarStatus(String numero, String status) {
        Pedido pedido = localizar(numero);
        pedido.setStatus(StatusPedido.de(status));
        return PedidoResponse.de(pedidoRepository.save(pedido));
    }

    @Transactional
    public void excluir(String numero) {
        pedidoRepository.delete(localizar(numero));
    }

    @Transactional
    public void limpar() {
        pedidoRepository.deleteAll();
    }

    private void validarItens(List<ItemPedidoRequest> itens) {
        if (itens == null || itens.isEmpty()) {
            throw new RegraNegocioException("O pedido precisa ter pelo menos um item.");
        }

        for (ItemPedidoRequest item : itens) {
            if (item == null || item.bebidaId() == null || item.bebidaId().isBlank()) {
                throw new RegraNegocioException("O pedido contém uma bebida inválida.");
            }

            if (item.quantidade() == null
                    || item.quantidade() < QUANTIDADE_MINIMA
                    || item.quantidade() > QUANTIDADE_MAXIMA) {
                throw new RegraNegocioException("A quantidade deve estar entre 1 e 10.");
            }
        }
    }

    private Pedido localizar(String numero) {
        return pedidoRepository.findById(numero)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado: " + numero + "."));
    }

    private String gerarNumero() {
        for (int tentativa = 0; tentativa < TENTATIVAS_NUMERO; tentativa++) {
            String numero = "PED-" + UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 8)
                    .toUpperCase(Locale.ROOT);

            if (!pedidoRepository.existsById(numero)) {
                return numero;
            }
        }

        throw new IllegalStateException("Não foi possível gerar um número único para o pedido.");
    }
}
