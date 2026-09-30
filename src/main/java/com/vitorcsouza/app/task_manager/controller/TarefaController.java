package com.vitorcsouza.app.task_manager.controller;

import com.vitorcsouza.app.task_manager.domain.dto.TarefaDTORequest;
import com.vitorcsouza.app.task_manager.domain.dto.TarefaDTOResponse;
import com.vitorcsouza.app.task_manager.domain.service.TarefaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tarefa")
@RequiredArgsConstructor
public class TarefaController {

    private final TarefaService tarefaService;

    @PostMapping
    public ResponseEntity<TarefaDTOResponse> create(@RequestBody @Valid TarefaDTORequest dto, UriComponentsBuilder uriBuilder) {
        TarefaDTOResponse response = tarefaService.create(dto);

        URI uri = uriBuilder.path("/api/v1/tarefa/{id}").buildAndExpand(response.id()).toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TarefaDTOResponse> getById(@PathVariable UUID id) {
        TarefaDTOResponse response = tarefaService.findById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<TarefaDTOResponse>> getAll(@PageableDefault(size = 5) Pageable pageable) {
        Page<TarefaDTOResponse> page = tarefaService.findAll(pageable);

        return ResponseEntity.ok(page);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TarefaDTOResponse> update(@PathVariable UUID id, @RequestBody @Valid TarefaDTORequest dto) {
        TarefaDTOResponse response = tarefaService.update(dto, id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TarefaDTOResponse> updateConcluidaStatus(@PathVariable UUID id) {
        TarefaDTOResponse tarefaDTOResponse = tarefaService.updateConcluidaStatus(id);

        return ResponseEntity.ok(tarefaDTOResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        tarefaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
