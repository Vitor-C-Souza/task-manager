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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

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

    @Nested
    @DisplayName("Testes do método delete")
    class DeleteTests {

        @Test
        @DisplayName("Deve remover a tarefa quando o ID existir.")
        void deveRemoverARetornarDTOQuandoTarefaExistir() {
            // Arrange
            when(tarefaRepository.findById(id)).thenReturn(Optional.of(tarefa));

            // Act
            tarefaService.delete(id);

            // Assert
            verify(tarefaRepository, times(1)).findById(id);
            verify(tarefaRepository, times(1)).delete(tarefa);
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando o ID não for encontrado.")
        void deveLancarExceptionQuandoATarefaNaoForEncontrada() {
            // Arrange
            when(tarefaRepository.findById(id)).thenReturn(Optional.empty());

            // Act + Assert
            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> tarefaService.delete(id));
            assertEquals("Tarefa não encontrada com este ID: " + id, exception.getMessage());

            verify(tarefaRepository, times(1)).findById(id);
            verify(tarefaRepository, never()).delete(tarefa);
        }
    }

    @Nested
    @DisplayName("Testes do método findById")
    class FindByIdTests {

        @Test
        @DisplayName("Deve retornar o DTO da tarefa quando o ID existir.")
        void deveRetornarDTOQuandoTarefaExistir() {
            // Arrange
            when(tarefaRepository.findById(id)).thenReturn(Optional.of(tarefa));

            // Act
            TarefaDTOResponse response = tarefaService.findById(id);

            // Assert

            assertNotNull(response);
            assertEquals("Estudar JUnit 5", response.titulo());


            verify(tarefaRepository, times(1)).findById(id);
        }


        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando buscar tarefa por ID inexistente")
        void deveLancarEntityNotFoundExceptionQuandoBuscarTarefaPorId() {
            // Arrange
            when(tarefaRepository.findById(id)).thenReturn(Optional.empty());

            // Act + Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class, () -> tarefaService.findById(id));

            assertEquals("Tarefa não encontrada com este ID: " + id, exception.getMessage());
            verify(tarefaRepository).findById(id);
        }
    }

    @Nested
    @DisplayName("Testes do método findAll")
    class FindAllTests {
        @Test
        @DisplayName("Deve retornar página de tarefas com sucesso")
        void findAll_ShouldReturnPageOfTarefaDTOResponse() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Tarefa> tarefaPage = new PageImpl<>(List.of(tarefa), pageable, 1);

            when(tarefaRepository.findAll(pageable)).thenReturn(tarefaPage);

            Page<TarefaDTOResponse> response = tarefaService.findAll(pageable);

            assertNotNull(response);
            assertEquals(1, response.getTotalElements());
            verify(tarefaRepository).findAll(pageable);
        }
    }

    @Nested
    @DisplayName("Testes do método updateConcluidaStatus")
    class UpdateConcluidaStatusTests {
        @Test
        @DisplayName("Deve alterar o status para true quando a tarefa estiver pendente.")
        void deveAlterarStatusParaTarefaEstiverPendente() {
            // Arrange
            when(tarefaRepository.findById(id)).thenReturn(Optional.of(tarefa));

            // Act
            TarefaDTOResponse response = tarefaService.updateConcluidaStatus(id);

            // Assert
            assertNotNull(response);
            assertEquals(true, response.concluida());

            verify(tarefaRepository, times(1)).findById(id);
        }

        @Test
        @DisplayName("Deve alterar o status para false quando a tarefa estiver concluída.")
        void deveAlterarStatusParaFalseQuandoAConcluida() {
            // Arrange
            tarefa.setConcluida(true);
            when(tarefaRepository.findById(id)).thenReturn(Optional.of(tarefa));

            // Act
            TarefaDTOResponse response = tarefaService.updateConcluidaStatus(id);

            // Assert
            assertNotNull(response);
            assertEquals(false, response.concluida());

            verify(tarefaRepository, times(1)).findById(id);
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando a tarefa não for encontrada.")
        void deveLancarExceptionQuandoATarefaNaoForEncontrada() {
            // Arrange
            when(tarefaRepository.findById(id)).thenReturn(Optional.empty());

            // Act + Assert
            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> tarefaService.updateConcluidaStatus(id));

            assertEquals("Tarefa não encontrada com este ID: " + id, exception.getMessage());
            verify(tarefaRepository, times(1)).findById(id);
        }
    }
}