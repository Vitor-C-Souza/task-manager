package com.vitorcsouza.app.task_manager.domain.repository;

import com.vitorcsouza.app.task_manager.domain.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {
}
