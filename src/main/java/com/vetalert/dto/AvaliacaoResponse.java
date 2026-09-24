// @author Paulo Pacifico

package com.vetalert.dto;

import com.vetalert.domain.Avaliacao;
import com.vetalert.domain.NivelRisco;
import java.time.LocalDateTime;
import java.util.List;

public record AvaliacaoResponse(
        Long id,
        String farmaco,
        String especie,
        Integer totalRelatos,
        Integer relatosGraves,
        Double score,
        NivelRisco nivel,
        List<String> reacoesTop,
        LocalDateTime criadoEm
) {

    public static AvaliacaoResponse de(Avaliacao avaliacao) {
        return new AvaliacaoResponse(
                avaliacao.getId(),
                avaliacao.getFarmaco(),
                avaliacao.getEspecie(),
                avaliacao.getTotalRelatos(),
                avaliacao.getRelatosGraves(),
                avaliacao.getScore(),
                avaliacao.getNivel(),
                avaliacao.getReacoesTop(),
                avaliacao.getCriadoEm()
        );
    }
}
