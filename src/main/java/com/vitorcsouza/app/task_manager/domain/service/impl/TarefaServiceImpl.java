package com.vitorcsouza.app.task_manager.domain.service.impl;

import com.vitorcsouza.app.task_manager.domain.dto.TarefaDTORequest;
import com.vitorcsouza.app.task_manager.domain.dto.TarefaDTOResponse;
import com.vitorcsouza.app.task_manager.domain.model.Categoria;
import com.vitorcsouza.app.task_manager.domain.model.Tarefa;
import com.vitorcsouza.app.task_manager.domain.repository.CategoriaRepository;
import com.vitorcsouza.app.task_manager.domain.repository.TarefaRepository;
import com.vitorcsouza.app.task_manager.domain.service.TarefaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TarefaServiceImpl implements TarefaService {

    private final TarefaRepository tarefaRepository;
    private final CategoriaRepository categoriaRepository;

    @Override
    @Transactional
    public TarefaDTOResponse create(TarefaDTORequest request) {
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com este ID: " + request.categoriaId()));

        Tarefa tarefa = request.toEntity(categoria);

        tarefaRepository.save(tarefa);

        return TarefaDTOResponse.toDto(tarefa);
    }

    @Override
    @Transactional
    public TarefaDTOResponse update(TarefaDTORequest request, UUID id) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada com este ID: " + id));

        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com este ID: " + request.categoriaId()));

        tarefa.setTitulo(request.titulo());
        tarefa.setCategoria(categoria);

        return TarefaDTOResponse.toDto(tarefa);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada com este ID: " + id));

        tarefaRepository.delete(tarefa);
    }

    @Override
    @Transactional(readOnly = true)
    public TarefaDTOResponse findById(UUID id) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada com este ID: " + id));
        return TarefaDTOResponse.toDto(tarefa);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TarefaDTOResponse> getAll(Pageable pageable) {
        Page<Tarefa> tarefas = tarefaRepository.findAll(pageable);
        return tarefas.map(TarefaDTOResponse::toDto);
    }

    @Override
    @Transactional
    public TarefaDTOResponse updateConcluiStatus(UUID id) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada com este ID: " + id));

        boolean status = Boolean.TRUE.equals(tarefa.getConcluida());

        tarefa.setConcluida(!status);

        return TarefaDTOResponse.toDto(tarefa);
    }
}
