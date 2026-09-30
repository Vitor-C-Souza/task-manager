package com.vitorcsouza.app.task_manager.domain.dto;

import com.vitorcsouza.app.task_manager.domain.model.Tarefa;
import java.time.LocalDateTime;
import java.util.UUID;

public record TarefaDTOResponse(
        UUID id,
        String titulo,
        Boolean concluida,
        CategoriaResumidaDTO categoria,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TarefaDTOResponse toDto(Tarefa tarefa) {
        return new TarefaDTOResponse(
                tarefa.getId(),
                tarefa.getTitulo(),
                tarefa.getConcluida(),
                CategoriaResumidaDTO.toDto(tarefa.getCategoria()),
                tarefa.getCreatedAt(),
                tarefa.getUpdatedAt()
        );
    }
}