// @author Paulo Pacifico

package com.vetalert.service;

import com.vetalert.domain.Avaliacao;
import com.vetalert.domain.NivelRisco;
import com.vetalert.dto.AvaliacaoRequest;
import com.vetalert.repository.AvaliacaoRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AvaliacaoService {

    private static final double PESO_GRAVIDADE = 70.0;
    private static final double PESO_VOLUME = 30.0;
    private static final double VOLUME_REFERENCIA = 1000.0;

    private final AvaliacaoRepository repository;

    public AvaliacaoService(AvaliacaoRepository repository) {
        this.repository = repository;
    }

    public Avaliacao avaliar(AvaliacaoRequest request) {
        int total = request.totalRelatos();
        int graves = Math.min(request.relatosGraves(), total);
        double score = calcularScore(total, graves);
        NivelRisco nivel = total == 0 ? NivelRisco.SEM_DADOS : classificar(score);

        Avaliacao avaliacao = new Avaliacao(
                request.farmaco().trim().toLowerCase(),
                request.especie().trim().toUpperCase(),
                total,
                graves,
                score,
                nivel,
                request.reacoesTop()
        );
        return repository.save(avaliacao);
    }

    public List<Avaliacao> listar(String farmaco, String especie) {
        return repository.buscar(normalizar(farmaco), normalizar(especie));
    }

    public Avaliacao buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Avaliacao " + id + " nao encontrada"));
    }

    public void remover(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Avaliacao " + id + " nao encontrada");
        }
        repository.deleteById(id);
    }

    private String normalizar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private double calcularScore(int total, int graves) {
        if (total == 0) {
            return 0.0;
        }
        double proporcaoGrave = (double) graves / total;
        double fatorVolume = Math.min(total / VOLUME_REFERENCIA, 1.0);
        double score = proporcaoGrave * PESO_GRAVIDADE + fatorVolume * PESO_VOLUME;
        return Math.round(score * 100.0) / 100.0;
    }

    private NivelRisco classificar(double score) {
        if (score < 30.0) {
            return NivelRisco.BAIXO;
        }
        if (score <= 60.0) {
            return NivelRisco.MODERADO;
        }
        return NivelRisco.ALTO;
    }
}
