// @author Paulo Pacifico

package com.vetalert.config;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TratadorDeErros {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> naoEncontrado(EntityNotFoundException excecao) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(corpo(excecao.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> invalido(MethodArgumentNotValidException excecao) {
        String detalhe = excecao.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Requisicao invalida");
        return ResponseEntity.badRequest().body(corpo(detalhe));
    }

    private Map<String, Object> corpo(String mensagem) {
        return Map.of("timestamp", LocalDateTime.now().toString(), "erro", mensagem);
    }
}
