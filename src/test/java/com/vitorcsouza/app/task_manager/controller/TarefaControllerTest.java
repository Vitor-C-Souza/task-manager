package com.vitorcsouza.app.task_manager.controller;

import com.vitorcsouza.app.task_manager.domain.dto.CategoriaResumidaDTO;
import com.vitorcsouza.app.task_manager.domain.dto.TarefaDTORequest;
import com.vitorcsouza.app.task_manager.domain.dto.TarefaDTOResponse;
import com.vitorcsouza.app.task_manager.domain.service.TarefaService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TarefaController.class)
class TarefaControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TarefaService tarefaService;

    private UUID id;
    private UUID categoriaId;
    private TarefaDTORequest validRequest;
    private TarefaDTOResponse responseDTO;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        categoriaId = UUID.randomUUID();

        validRequest = new TarefaDTORequest("Estudar JUnit 5", categoriaId);

        CategoriaResumidaDTO categoriaResumidaDTO = new CategoriaResumidaDTO(categoriaId, "Estudos");

        responseDTO = new TarefaDTOResponse(
                id,
                "Estudar JUnit 5",
                false,
                categoriaResumidaDTO,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Nested
    @DisplayName("Testes do endpoint POST /api/v1/tarefa")
    class CreateTests {

        @Test
        @DisplayName("Deve retornar 201 Created e header Location quando o payload for válido")
        void create_WhenValidDTO_ShouldReturn201Created() throws Exception {
            when(tarefaService.create(any(TarefaDTORequest.class))).thenReturn(responseDTO);

            mockMvc.perform(post("/api/v1/tarefa")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "http://localhost/api/v1/tarefa/" + id))
                    .andExpect(jsonPath("$.id").value(id.toString()))
                    .andExpect(jsonPath("$.titulo").value("Estudar JUnit 5"))
                    .andExpect(jsonPath("$.concluida").value(false))
                    .andExpect(jsonPath("$.categoria.id").value(categoriaId.toString()));

            verify(tarefaService, times(1)).create(any(TarefaDTORequest.class));
        }

        @Test
        @DisplayName("Deve retornar 400 Bad Request quando o título for em branco")
        void create_WhenTituloIsBlank_ShouldReturn400BadRequest() throws Exception {
            TarefaDTORequest invalidRequest = new TarefaDTORequest("", categoriaId);

            mockMvc.perform(post("/api/v1/tarefa")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verify(tarefaService, never()).create(any());
        }

        @Test
        @DisplayName("Deve retornar 400 Bad Request quando a categoriaId for nula")
        void create_WhenCategoriaIdIsNull_ShouldReturn400BadRequest() throws Exception {
            TarefaDTORequest invalidRequest = new TarefaDTORequest("Estudar JUnit 5", null);

            mockMvc.perform(post("/api/v1/tarefa")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verify(tarefaService, never()).create(any());
        }
    }

    @Nested
    @DisplayName("Testes do endpoint GET /api/v1/tarefa/{id}")
    class GetByIdTests {

        @Test
        @DisplayName("Deve retornar 200 OK com a tarefa quando o ID existir")
        void getById_WhenIdExists_ShouldReturn200Ok() throws Exception {
            when(tarefaService.findById(id)).thenReturn(responseDTO);

            mockMvc.perform(get("/api/v1/tarefa/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id.toString()))
                    .andExpect(jsonPath("$.titulo").value("Estudar JUnit 5"))
                    .andExpect(jsonPath("$.concluida").value(false));

            verify(tarefaService, times(1)).findById(id);
        }

        @Test
        @DisplayName("Deve retornar 404 Not Found quando a tarefa não for encontrada")
        void getById_WhenIdDoesNotExist_ShouldReturn404NotFound() throws Exception {
            when(tarefaService.findById(id))
                    .thenThrow(new EntityNotFoundException("Tarefa não encontrada com este ID: " + id));

            mockMvc.perform(get("/api/v1/tarefa/{id}", id))
                    .andExpect(status().isNotFound());

            verify(tarefaService, times(1)).findById(id);
        }
    }

    @Nested
    @DisplayName("Testes do endpoint GET /api/v1/tarefa")
    class GetAllTests {

        @Test
        @DisplayName("Deve retornar 200 OK com página de tarefas")
        void getAll_ShouldReturn200OkWithPage() throws Exception {
            var page = new PageImpl<>(List.of(responseDTO), PageRequest.of(0, 5), 1);
            when(tarefaService.findAll(any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/api/v1/tarefa")
                            .param("page", "0")
                            .param("size", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(id.toString()))
                    .andExpect(jsonPath("$.content[0].titulo").value("Estudar JUnit 5"))
                    .andExpect(jsonPath("$.page.totalElements").value(1))
                    .andExpect(jsonPath("$.page.totalPages").value(1))
                    .andExpect(jsonPath("$.page.size").value(5))
                    .andExpect(jsonPath("$.page.number").value(0));

            verify(tarefaService, times(1)).findAll(any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("Testes do endpoint PUT /api/v1/tarefa/{id}")
    class UpdateTests {

        @Test
        @DisplayName("Deve retornar 200 OK com tarefa atualizada quando ID existir e payload for válido")
        void update_WhenValidDTOAndIdExists_ShouldReturn200Ok() throws Exception {
            when(tarefaService.update(any(TarefaDTORequest.class), eq(id))).thenReturn(responseDTO);

            mockMvc.perform(put("/api/v1/tarefa/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id.toString()))
                    .andExpect(jsonPath("$.titulo").value("Estudar JUnit 5"));

            verify(tarefaService, times(1)).update(any(TarefaDTORequest.class), eq(id));
        }

        @Test
        @DisplayName("Deve retornar 400 Bad Request quando o payload de atualização for inválido")
        void update_WhenInvalidDTO_ShouldReturn400BadRequest() throws Exception {
            TarefaDTORequest invalidRequest = new TarefaDTORequest("", categoriaId);

            mockMvc.perform(put("/api/v1/tarefa/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verify(tarefaService, never()).update(any(), any());
        }
    }

    @Nested
    @DisplayName("Testes do endpoint PATCH /api/v1/tarefa/{id}")
    class UpdateConcluidaStatusTests {

        @Test
        @DisplayName("Deve retornar 200 OK com o status alterado quando o ID existir")
        void updateConcluidaStatus_WhenIdExists_ShouldReturn200Ok() throws Exception {
            TarefaDTOResponse statusAlteradoResponse = new TarefaDTOResponse(
                    id, "Estudar JUnit 5", true, responseDTO.categoria(), LocalDateTime.now(), LocalDateTime.now()
            );

            when(tarefaService.updateConcluidaStatus(id)).thenReturn(statusAlteradoResponse);

            mockMvc.perform(patch("/api/v1/tarefa/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id.toString()))
                    .andExpect(jsonPath("$.concluida").value(true));

            verify(tarefaService, times(1)).updateConcluidaStatus(id);
        }

        @Test
        @DisplayName("Deve retornar 404 Not Found ao alterar status de tarefa inexistente")
        void updateConcluidaStatus_WhenIdDoesNotExist_ShouldReturn404NotFound() throws Exception {
            when(tarefaService.updateConcluidaStatus(id))
                    .thenThrow(new EntityNotFoundException("Tarefa não encontrada com este ID: " + id));

            mockMvc.perform(patch("/api/v1/tarefa/{id}", id))
                    .andExpect(status().isNotFound());

            verify(tarefaService, times(1)).updateConcluidaStatus(id);
        }
    }

    @Nested
    @DisplayName("Testes do endpoint DELETE /api/v1/tarefa/{id}")
    class DeleteTests {

        @Test
        @DisplayName("Deve retornar 204 No Content quando a exclusão for bem-sucedida")
        void delete_WhenIdExists_ShouldReturn204NoContent() throws Exception {
            doNothing().when(tarefaService).delete(id);

            mockMvc.perform(delete("/api/v1/tarefa/{id}", id))
                    .andExpect(status().isNoContent());

            verify(tarefaService, times(1)).delete(id);
        }
    }
}