package com.vitorcsouza.app.task_manager.domain.dto;

import com.vitorcsouza.app.task_manager.domain.model.Categoria;
import com.vitorcsouza.app.task_manager.domain.model.Tarefa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record TarefaDTORequest(
        @NotBlank(message = "O título da tarefa é obrigatório.")
        String titulo,

        @NotNull(message = "O ID da categoria é obrigatório.")
        UUID categoriaId
) {
    public Tarefa toEntity(Categoria categoria) {
        return Tarefa.builder()
                .titulo(titulo)
                .categoria(categoria)
                .build();
    }
}