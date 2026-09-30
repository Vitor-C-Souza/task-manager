package com.vitorcsouza.app.task_manager.domain.service;

import com.vitorcsouza.app.task_manager.domain.dto.TarefaDTORequest;
import com.vitorcsouza.app.task_manager.domain.dto.TarefaDTOResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TarefaService {
    TarefaDTOResponse create(TarefaDTORequest request);
    TarefaDTOResponse update(TarefaDTORequest request, UUID id);
    void delete(UUID id);
    TarefaDTOResponse findById(UUID id);
    Page<TarefaDTOResponse> getAll(Pageable pageable);
    TarefaDTOResponse updateConcluiStatus(UUID id);
}
