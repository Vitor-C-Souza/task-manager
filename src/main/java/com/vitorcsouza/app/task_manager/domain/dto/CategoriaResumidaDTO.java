package com.vitorcsouza.app.task_manager.domain.dto;

import com.vitorcsouza.app.task_manager.domain.model.Categoria;
import java.util.UUID;

public record CategoriaResumidaDTO(
        UUID id,
        String nome
) {
    public static CategoriaResumidaDTO toDto(Categoria categoria) {
        if (categoria == null) return null;
        return new CategoriaResumidaDTO(categoria.getId(), categoria.getNome());
    }
}