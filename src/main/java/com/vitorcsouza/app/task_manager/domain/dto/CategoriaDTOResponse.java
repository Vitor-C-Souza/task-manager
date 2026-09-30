package com.vitorcsouza.app.task_manager.domain.dto;

import com.vitorcsouza.app.task_manager.domain.model.Categoria;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CategoriaDTOResponse(
        UUID id,
        String nome,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<TarefaDTOResponse> tarefas
) {
        public static CategoriaDTOResponse toDto(Categoria categoria) {
                List<TarefaDTOResponse> tarefasDto = categoria.getTarefaList() != null
                        ? categoria.getTarefaList().stream().map(TarefaDTOResponse::toDto).toList()
                        : List.of();

                return new CategoriaDTOResponse(
                        categoria.getId(),
                        categoria.getNome(),
                        categoria.getCreatedAt(),
                        categoria.getUpdatedAt(),
                        tarefasDto
                );
        }
}