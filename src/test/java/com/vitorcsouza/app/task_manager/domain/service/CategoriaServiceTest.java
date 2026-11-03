package com.vitorcsouza.app.task_manager.domain.service;

import com.vitorcsouza.app.task_manager.domain.dto.CategoriaDTORequest;
import com.vitorcsouza.app.task_manager.domain.dto.CategoriaDTOResponse;
import com.vitorcsouza.app.task_manager.domain.model.Categoria;
import com.vitorcsouza.app.task_manager.domain.repository.CategoriaRepository;
import com.vitorcsouza.app.task_manager.domain.service.impl.CategoriaServiceImpl;
import com.vitorcsouza.app.task_manager.infra.exception.DatabaseException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {
    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaServiceImpl categoriaService;

    private UUID id;
    private Categoria categoria;
    private CategoriaDTORequest request;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();

        categoria = new Categoria();
        categoria.setId(id);
        categoria.setNome("Estudos");

        request = new CategoriaDTORequest("Estudos");
    }

    @Nested
    @DisplayName("Testes do método create")
    class CreateTests {

        @Test
        @DisplayName("Deve criar e retornar DTO da categoria com sucesso")
        void create_ShouldReturnDTOResponse() {
            when(categoriaRepository.saveAndFlush(any(Categoria.class))).thenReturn(categoria);

            CategoriaDTOResponse response = categoriaService.create(request);

            assertNotNull(response);
            assertEquals(request.nome(), response.nome());

            verify(categoriaRepository, times(1)).saveAndFlush(any(Categoria.class));
        }
    }

    @Nested
    @DisplayName("Testes do método update")
    class UpdateTests {

        @Test
        @DisplayName("Deve atualizar e retornar DTO quando a categoria existir")
        void update_WhenIdExists_ShouldReturnDTOResponse() {
            when(categoriaRepository.findById(id)).thenReturn(Optional.of(categoria));

            CategoriaDTOResponse response = categoriaService.update(request, id);

            assertNotNull(response);
            assertEquals(request.nome(), response.nome());

            verify(categoriaRepository, times(1)).findById(id);
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando a categoria não for encontrada")
        void update_WhenIdDoesNotExist_ShouldThrowException() {
            when(categoriaRepository.findById(id)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class, () -> categoriaService.update(request, id));

            assertEquals("Categoria não encontrada para o ID: " + id, exception.getMessage());
            verify(categoriaRepository, times(1)).findById(id);
        }
    }

    @Nested
    @DisplayName("Testes do método delete")
    class DeleteTests {

        @Test
        @DisplayName("Deve deletar a categoria quando o ID existir")
        void delete_WhenIdExists_ShouldDeleteCategoria() {
            when(categoriaRepository.findById(id)).thenReturn(Optional.of(categoria));

            categoriaService.delete(id);

            verify(categoriaRepository, times(1)).findById(id);
            verify(categoriaRepository, times(1)).delete(categoria);
            verify(categoriaRepository, times(1)).flush();
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException ao deletar quando o ID não for encontrado")
        void delete_WhenIdDoesNotExist_ShouldThrowException() {
            when(categoriaRepository.findById(id)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class, () -> categoriaService.delete(id));

            assertEquals("Categoria não encontrada para o ID: " + id, exception.getMessage());
            verify(categoriaRepository, times(1)).findById(id);
            verify(categoriaRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Deve lançar DatabaseException quando houver violação de integridade relacional")
        void delete_WhenDataIntegrityViolationOccurs_ShouldThrowDatabaseException() {
            when(categoriaRepository.findById(id)).thenReturn(Optional.of(categoria));
            doThrow(new DataIntegrityViolationException("FK constraint"))
                    .when(categoriaRepository).flush();

            DatabaseException exception = assertThrows(
                    DatabaseException.class, () -> categoriaService.delete(id));

            assertEquals("Não é possível excluir a categoria pois existem tarefas vinculadas a ela.", exception.getMessage());
            verify(categoriaRepository, times(1)).findById(id);
            verify(categoriaRepository, times(1)).delete(categoria);
            verify(categoriaRepository, times(1)).flush();
        }
    }

    @Nested
    @DisplayName("Testes do método findById")
    class FindByIdTests {

        @Test
        @DisplayName("Deve retornar DTO da categoria quando o ID existir")
        void findById_WhenIdExists_ShouldReturnDTOResponse() {
            when(categoriaRepository.findById(id)).thenReturn(Optional.of(categoria));

            CategoriaDTOResponse response = categoriaService.findById(id);

            assertNotNull(response);
            assertEquals(id, response.id());
            assertEquals("Estudos", response.nome());

            verify(categoriaRepository, times(1)).findById(id);
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando o ID não for encontrado")
        void findById_WhenIdDoesNotExist_ShouldThrowException() {
            when(categoriaRepository.findById(id)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class, () -> categoriaService.findById(id));

            assertEquals("Nenhuma categoria encontrada para o ID: " + id, exception.getMessage());
            verify(categoriaRepository, times(1)).findById(id);
        }
    }

    @Nested
    @DisplayName("Testes do método findAll")
    class FindAllTests {

        @Test
        @DisplayName("Deve retornar página de categorias com sucesso")
        void findAll_ShouldReturnPageOfCategoriaDTOResponse() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Categoria> categoriaPage = new PageImpl<>(List.of(categoria), pageable, 1);

            when(categoriaRepository.findAll(pageable)).thenReturn(categoriaPage);

            Page<CategoriaDTOResponse> response = categoriaService.findAll(pageable);

            assertNotNull(response);
            assertEquals(1, response.getTotalElements());
            verify(categoriaRepository, times(1)).findAll(pageable);
        }
    }

}