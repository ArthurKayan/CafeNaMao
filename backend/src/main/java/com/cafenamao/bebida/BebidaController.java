package com.cafenamao.bebida;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/bebidas")
public class BebidaController {

    private final BebidaService servico;

    public BebidaController(BebidaService servico) {
        this.servico = servico;
    }

    @GetMapping
    public List<BebidaResponse> listar() {
        return servico.listar();
    }

    @GetMapping("/{id}")
    public BebidaResponse buscar(@PathVariable String id) {
        return servico.buscar(id);
    }

    @PostMapping
    public ResponseEntity<BebidaResponse> criar(@Valid @RequestBody BebidaRequest requisicao) {
        BebidaResponse criada = servico.criar(requisicao);
        return ResponseEntity.created(URI.create("/api/bebidas/" + criada.id())).body(criada);
    }

    @PutMapping("/{id}")
    public BebidaResponse atualizar(
            @PathVariable String id,
            @Valid @RequestBody BebidaAtualizacaoRequest requisicao) {
        return servico.atualizar(id, requisicao);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        servico.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
