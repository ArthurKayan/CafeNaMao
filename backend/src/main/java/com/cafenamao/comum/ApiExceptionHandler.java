package com.cafenamao.comum;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> tratarNaoEncontrado(
            RecursoNaoEncontradoException excecao,
            HttpServletRequest requisicao) {
        return responder(HttpStatus.NOT_FOUND, "Recurso não encontrado", excecao.getMessage(), requisicao, List.of());
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<ErroResposta> tratarConflito(
            ConflitoException excecao,
            HttpServletRequest requisicao) {
        return responder(HttpStatus.CONFLICT, "Conflito de dados", excecao.getMessage(), requisicao, List.of());
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResposta> tratarRegraNegocio(
            RegraNegocioException excecao,
            HttpServletRequest requisicao) {
        return responder(HttpStatus.BAD_REQUEST, "Regra de negócio violada", excecao.getMessage(), requisicao, List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> tratarValidacao(
            MethodArgumentNotValidException excecao,
            HttpServletRequest requisicao) {
        List<CampoInvalido> campos = excecao.getBindingResult().getFieldErrors().stream()
                .map(erro -> new CampoInvalido(erro.getField(), erro.getDefaultMessage()))
                .toList();

        return responder(HttpStatus.BAD_REQUEST, "Dados inválidos", "Um ou mais campos estão inválidos.", requisicao, campos);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResposta> tratarCorpoInvalido(
            HttpMessageNotReadableException excecao,
            HttpServletRequest requisicao) {
        return responder(HttpStatus.BAD_REQUEST, "Corpo inválido", "O corpo da requisição está malformado ou contém valores inválidos.", requisicao, List.of());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResposta> tratarParametroInvalido(
            MethodArgumentTypeMismatchException excecao,
            HttpServletRequest requisicao) {
        return responder(HttpStatus.BAD_REQUEST, "Parâmetro inválido", "O parâmetro informado é inválido.", requisicao, List.of());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErroResposta> tratarRotaInexistente(
            NoResourceFoundException excecao,
            HttpServletRequest requisicao) {
        return responder(HttpStatus.NOT_FOUND, "Recurso não encontrado", "A rota informada não existe.", requisicao, List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResposta> tratarErroInesperado(
            Exception excecao,
            HttpServletRequest requisicao) {
        log.error("Erro inesperado ao processar a requisição", excecao);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", "Não foi possível concluir a operação.", requisicao, List.of());
    }

    private ResponseEntity<ErroResposta> responder(
            HttpStatus status,
            String erro,
            String mensagem,
            HttpServletRequest requisicao,
            List<CampoInvalido> campos) {
        ErroResposta corpo = new ErroResposta(
                Instant.now(),
                status.value(),
                erro,
                mensagem,
                requisicao.getRequestURI(),
                campos);
        return ResponseEntity.status(status).body(corpo);
    }
}
