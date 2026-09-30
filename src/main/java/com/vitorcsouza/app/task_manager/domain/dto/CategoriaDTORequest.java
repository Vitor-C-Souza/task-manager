package com.vitorcsouza.app.task_manager.domain.dto;

import com.vitorcsouza.app.task_manager.domain.model.Categoria;
import jakarta.validation.constraints.NotBlank;

public record CategoriaDTORequest(
        @NotBlank(message = "O nome da categoria é obrigatório.")
        String nome
) {
        public Categoria toEntity() {
                return Categoria.builder()
                        .nome(nome)
                        .build();
        }
}