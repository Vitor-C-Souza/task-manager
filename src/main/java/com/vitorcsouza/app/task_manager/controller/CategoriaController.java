package com.vitorcsouza.app.task_manager.controller;

import com.vitorcsouza.app.task_manager.domain.dto.CategoriaDTORequest;
import com.vitorcsouza.app.task_manager.domain.dto.CategoriaDTOResponse;
import com.vitorcsouza.app.task_manager.domain.service.CategoriaService;
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
@RequestMapping("/api/v1/categoria")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @PostMapping
    public ResponseEntity<CategoriaDTOResponse> create(@RequestBody @Valid CategoriaDTORequest dto, UriComponentsBuilder uriBuilder) {
        CategoriaDTOResponse response = categoriaService.create(dto);

        URI uri = uriBuilder.path("/api/v1/categoria/{id}").buildAndExpand(response.id()).toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTOResponse> getById(@PathVariable UUID id) {
        CategoriaDTOResponse response = categoriaService.findById(id);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping
    public ResponseEntity<Page<CategoriaDTOResponse>> getAll(@PageableDefault(size = 5) Pageable pageable) {
        Page<CategoriaDTOResponse> response = categoriaService.findAll(pageable);

        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaDTOResponse> update(@PathVariable UUID id, @RequestBody @Valid CategoriaDTORequest dto) {
        CategoriaDTOResponse updated = categoriaService.update(dto, id);

        return ResponseEntity.ok().body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        categoriaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
