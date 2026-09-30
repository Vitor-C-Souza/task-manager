package com.vitorcsouza.app.task_manager.domain.service;

import com.vitorcsouza.app.task_manager.domain.dto.CategoriaDTORequest;
import com.vitorcsouza.app.task_manager.domain.dto.CategoriaDTOResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CategoriaService {
    CategoriaDTOResponse create(CategoriaDTORequest dto);
    CategoriaDTOResponse update(CategoriaDTORequest dto, UUID id);
    void delete(UUID id);
    CategoriaDTOResponse findById(UUID id);
    Page<CategoriaDTOResponse> findAll(Pageable pageable);
}
