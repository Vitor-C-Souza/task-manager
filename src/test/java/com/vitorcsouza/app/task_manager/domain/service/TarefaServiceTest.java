package com.vitorcsouza.app.task_manager.domain.service;

import com.vitorcsouza.app.task_manager.domain.repository.TarefaRepository;
import com.vitorcsouza.app.task_manager.domain.service.impl.TarefaServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TarefaServiceTest {

    @Mock
    private TarefaRepository tarefaRepository;

    @InjectMocks
    private TarefaServiceImpl tarefaService;

    @Test
    @DisplayName("Deve lançar EntityNotFoundException quando buscar tarefa por ID inexistente")
    void deveLancarEntityNotFoundExceptionQuandoBuscarTarefaPorId()
    {
        // Arrange
        UUID id = UUID.randomUUID();
        when(tarefaRepository.findById(id)).thenReturn(Optional.empty());

        // Act + Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class, () -> tarefaService.findById(id));

        assertEquals("Tarefa não encontrada com este ID: " + id, exception.getMessage());
        verify(tarefaRepository).findById(id);
    }

}