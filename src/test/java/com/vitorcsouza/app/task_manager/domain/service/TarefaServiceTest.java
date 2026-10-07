package com.vitorcsouza.app.task_manager.domain.service;

import com.vitorcsouza.app.task_manager.domain.dto.TarefaDTORequest;
import com.vitorcsouza.app.task_manager.domain.dto.TarefaDTOResponse;
import com.vitorcsouza.app.task_manager.domain.model.Categoria;
import com.vitorcsouza.app.task_manager.domain.model.Tarefa;
import com.vitorcsouza.app.task_manager.domain.repository.CategoriaRepository;
import com.vitorcsouza.app.task_manager.domain.repository.TarefaRepository;
import com.vitorcsouza.app.task_manager.domain.service.impl.TarefaServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TarefaServiceTest {

    @Mock
    private TarefaRepository tarefaRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private TarefaServiceImpl tarefaService;

    private UUID id;
    private UUID categoriaId;
    private Categoria categoria;
    private Tarefa tarefa;
    private TarefaDTORequest request;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        categoriaId = UUID.randomUUID();

        categoria = new Categoria();
        categoria.setId(categoriaId);
        categoria.setNome("Estudos");

        tarefa = new Tarefa();
        tarefa.setId(id);
        tarefa.setTitulo("Estudar JUnit 5");
        tarefa.setConcluida(false);
        tarefa.setCategoria(categoria);

        request = new TarefaDTORequest("Estudar JUnit 5", categoriaId);
    }

    @Nested
    @DisplayName("Testes do método create")
    class CreateTests {

        @Test
        @DisplayName("Deve criar e retornar DTO quando a categoria existir")
        void deveCriarERetornarDTOQuandoACategoriaExistir() {
            // Arrange
            when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
            when(tarefaRepository.saveAndFlush(any(Tarefa.class))).thenReturn(tarefa);

            // Act
            TarefaDTOResponse response = tarefaService.create(request);

            // Assert
            assertNotNull(response);
            assertEquals("Estudar JUnit 5", response.titulo());

            verify(categoriaRepository).findById(categoriaId);
            verify(tarefaRepository).saveAndFlush(any(Tarefa.class));
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando a categoria não for encontrada.")
        void deveLancarExceptionQuandoACategoriaNaoForEncontrada() {
            // Arrange
            when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.empty());

            // Act + Assert
            assertThrows(EntityNotFoundException.class, () -> tarefaService.create(request));

            verify(categoriaRepository, times(1)).findById(categoriaId);
            verify(tarefaRepository, never()).saveAndFlush(any(Tarefa.class));
        }
    }

    @Nested
    @DisplayName("Testes do método update")
    class UpdateTests {

        @Test
        @DisplayName("Deve atualizar e retornar DTO quando tarefa e categoria existirem.")
        void deveAtualizarERetornarDTOQuandoTarefaExistir() {
            // Arrange
            when(tarefaRepository.findById(id)).thenReturn(Optional.of(tarefa));
            when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));

            // Act
            TarefaDTOResponse response = tarefaService.update(request, id);

            // Assert
            assertNotNull(response);
            assertEquals("Estudar JUnit 5", response.titulo());

            verify(categoriaRepository, times(1)).findById(categoriaId);
            verify(tarefaRepository, times(1)).findById(id);
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando a tarefa não for encontrada.")
        void deveLancarExceptionQuandoATarefaNaoForEncontrada() {
            // Arrange
            when(tarefaRepository.findById(id)).thenReturn(Optional.empty());

            // Act + Assert
            assertThrows(EntityNotFoundException.class, () -> tarefaService.update(request, id));

            verify(categoriaRepository, never()).findById(categoriaId);
            verify(tarefaRepository, times(1)).findById(id);
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException ao atualizar quando categoria não existir.")
        void deveLancarExceptionAtualizarCategoriaNaoExistir() {
            // Arrange
            when(tarefaRepository.findById(id)).thenReturn(Optional.of(tarefa));
            when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.empty());

            // Act + Assert
            assertThrows(EntityNotFoundException.class, () -> tarefaService.update(request, id));

            verify(tarefaRepository, times(1)).findById(id);
            verify(categoriaRepository, times(1)).findById(categoriaId);
        }
    }



    @Test
    @DisplayName("Deve lançar EntityNotFoundException quando buscar tarefa por ID inexistente")
    void deveLancarEntityNotFoundExceptionQuandoBuscarTarefaPorId() {
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