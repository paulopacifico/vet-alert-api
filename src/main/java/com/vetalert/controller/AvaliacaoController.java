// @author Paulo Pacifico

package com.vetalert.controller;

import com.vetalert.dto.AvaliacaoRequest;
import com.vetalert.dto.AvaliacaoResponse;
import com.vetalert.service.AvaliacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/avaliacoes")
@Tag(name = "Avaliacoes", description = "Calculo e historico de risco farmacologico")
public class AvaliacaoController {

    private final AvaliacaoService service;

    public AvaliacaoController(AvaliacaoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Calcula o score de risco a partir do resumo da openFDA")
    public AvaliacaoResponse criar(@Valid @RequestBody AvaliacaoRequest request) {
        return AvaliacaoResponse.de(service.avaliar(request));
    }

    @GetMapping
    @Operation(summary = "Lista avaliacoes com filtro opcional por farmaco e especie")
    public List<AvaliacaoResponse> listar(
            @RequestParam(required = false) String farmaco,
            @RequestParam(required = false) String especie) {
        return service.listar(farmaco, especie).stream()
                .map(AvaliacaoResponse::de)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma avaliacao pelo identificador")
    public AvaliacaoResponse buscar(@PathVariable Long id) {
        return AvaliacaoResponse.de(service.buscarPorId(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma avaliacao do historico")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}
