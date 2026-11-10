package com.cafenamao.bebida;

import com.cafenamao.comum.ConflitoException;
import com.cafenamao.comum.RecursoNaoEncontradoException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class BebidaService {

    private final BebidaRepository repositorio;

    public BebidaService(BebidaRepository repositorio) {
        this.repositorio = repositorio;
    }

    public List<BebidaResponse> listar() {
        return repositorio.findAll(Sort.by(Sort.Direction.ASC, "nome")).stream()
                .map(BebidaResponse::de)
                .toList();
    }

    public BebidaResponse buscar(String id) {
        return BebidaResponse.de(localizar(id));
    }

    @Transactional
    public BebidaResponse criar(BebidaRequest requisicao) {
        String id = requisicao.id().trim();

        if (repositorio.existsById(id)) {
            throw new ConflitoException("Já existe uma bebida com o id " + id + ".");
        }

        Bebida bebida = new Bebida(
                id,
                requisicao.nome().trim(),
                requisicao.descricao().trim(),
                requisicao.categoria().trim(),
                requisicao.precoCentavos());

        return BebidaResponse.de(repositorio.save(bebida));
    }

    @Transactional
    public BebidaResponse atualizar(String id, BebidaAtualizacaoRequest requisicao) {
        Bebida bebida = localizar(id);
        bebida.setNome(requisicao.nome().trim());
        bebida.setDescricao(requisicao.descricao().trim());
        bebida.setCategoria(requisicao.categoria().trim());
        bebida.setPrecoCentavos(requisicao.precoCentavos());
        return BebidaResponse.de(repositorio.save(bebida));
    }

    @Transactional
    public void excluir(String id) {
        repositorio.delete(localizar(id));
    }

    private Bebida localizar(String id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Bebida não encontrada: " + id + "."));
    }
}
