package com.vitorcsouza.app.task_manager.domain.repository;

import com.vitorcsouza.app.task_manager.domain.model.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TarefaRepository extends JpaRepository<Tarefa, UUID> {
}
