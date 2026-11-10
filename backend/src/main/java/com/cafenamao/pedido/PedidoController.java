package com.cafenamao.pedido;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService servico;

    public PedidoController(PedidoService servico) {
        this.servico = servico;
    }

    @GetMapping
    public List<PedidoResponse> listar(@RequestParam(required = false) String status) {
        return servico.listar(status);
    }

    @GetMapping("/{numero}")
    public PedidoResponse buscar(@PathVariable String numero) {
        return servico.buscar(numero);
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> criar(@Valid @RequestBody PedidoRequest requisicao) {
        PedidoResponse criado = servico.criar(requisicao);
        return ResponseEntity.created(URI.create("/api/pedidos/" + criado.numero())).body(criado);
    }

    @PatchMapping("/{numero}/status")
    public PedidoResponse atualizarStatus(
            @PathVariable String numero,
            @Valid @RequestBody AtualizarStatusRequest requisicao) {
        return servico.atualizarStatus(numero, requisicao.status());
    }

    @DeleteMapping("/{numero}")
    public ResponseEntity<Void> excluir(@PathVariable String numero) {
        servico.excluir(numero);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> limpar() {
        servico.limpar();
        return ResponseEntity.noContent().build();
    }
}
