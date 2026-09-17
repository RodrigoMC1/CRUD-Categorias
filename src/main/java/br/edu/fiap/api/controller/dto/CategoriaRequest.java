package br.edu.fiap.api.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CategoriaRequest(
        @Schema(description = "Nome da Categoria", example = "Periféricos")
        @NotBlank @Size(max = 120) String nome,
        @Schema(description = "Descrição", example = "Acessorios para computadores")
        @NotBlank @Size(max = 120) String descricao
        ) {
}
