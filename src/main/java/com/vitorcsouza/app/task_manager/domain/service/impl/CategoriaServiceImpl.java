package com.vitorcsouza.app.task_manager.domain.service.impl;

import com.vitorcsouza.app.task_manager.domain.dto.CategoriaDTORequest;
import com.vitorcsouza.app.task_manager.domain.dto.CategoriaDTOResponse;
import com.vitorcsouza.app.task_manager.domain.model.Categoria;
import com.vitorcsouza.app.task_manager.domain.repository.CategoriaRepository;
import com.vitorcsouza.app.task_manager.domain.service.CategoriaService;
import com.vitorcsouza.app.task_manager.infra.exception.DatabaseException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Override
    @Transactional
    public CategoriaDTOResponse create(CategoriaDTORequest dto) {
        Categoria categoria = dto.toEntity();
        categoriaRepository.save(categoria);
        return CategoriaDTOResponse.toDto(categoria);
    }

    @Override
    @Transactional
    public CategoriaDTOResponse update(CategoriaDTORequest dto, UUID id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada para o ID: " + id));
        categoria.setNome(dto.nome());
        return CategoriaDTOResponse.toDto(categoria);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada para o ID: " + id));

        try {
            categoriaRepository.delete(categoria);
            categoriaRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Não é possível excluir a categoria pois existem tarefas vinculadas a ela.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaDTOResponse findById(UUID id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nenhuma categoria encontrada para o ID: " + id));

        return CategoriaDTOResponse.toDto(categoria);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoriaDTOResponse> findAll(Pageable pageable) {
        Page<Categoria> categorias = categoriaRepository.findAll(pageable);

        return categorias.map(CategoriaDTOResponse::toDto);
    }
}
