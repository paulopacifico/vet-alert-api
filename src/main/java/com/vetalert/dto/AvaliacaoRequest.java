// @author Paulo Pacifico

package com.vetalert.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

public record AvaliacaoRequest(
        @Schema(example = "carprofen") @NotBlank String farmaco,
        @Schema(example = "CAO") @NotBlank String especie,
        @Schema(example = "1200") @NotNull @PositiveOrZero Integer totalRelatos,
        @Schema(example = "300") @NotNull @PositiveOrZero Integer relatosGraves,
        @ArraySchema(schema = @Schema(example = "Vomiting")) List<String> reacoesTop
) {
}
