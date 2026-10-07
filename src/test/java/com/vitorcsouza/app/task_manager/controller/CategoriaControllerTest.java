package com.vitorcsouza.app.task_manager.controller;

import com.vitorcsouza.app.task_manager.domain.dto.CategoriaDTORequest;
import com.vitorcsouza.app.task_manager.domain.dto.CategoriaDTOResponse;
import com.vitorcsouza.app.task_manager.domain.service.CategoriaService;
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

@WebMvcTest(CategoriaController.class)
class CategoriaControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoriaService categoriaService;

    private UUID id;
    private CategoriaDTORequest validRequest;
    private CategoriaDTOResponse responseDTO;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        validRequest = new CategoriaDTORequest("Estudos");
        responseDTO = new CategoriaDTOResponse(
                id,
                "Estudos",
                LocalDateTime.now(),
                LocalDateTime.now(),
                List.of()
        );
    }

    @Nested
    @DisplayName("Testes do endpoint POST /api/v1/categoria")
    class CreateTests {

        @Test
        @DisplayName("Deve retornar 201 Created e header Location quando o payload for válido")
        void create_WhenValidDTO_ShouldReturn201Created() throws Exception {
            when(categoriaService.create(any(CategoriaDTORequest.class))).thenReturn(responseDTO);

            mockMvc.perform(post("/api/v1/categoria")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "http://localhost/api/v1/categoria/" + id))
                    .andExpect(jsonPath("$.id").value(id.toString()))
                    .andExpect(jsonPath("$.nome").value("Estudos"));

            verify(categoriaService, times(1)).create(any(CategoriaDTORequest.class));
        }

        @Test
        @DisplayName("Deve retornar 400 Bad Request quando o payload for inválido")
        void create_WhenInvalidDTO_ShouldReturn400BadRequest() throws Exception {
            CategoriaDTORequest invalidRequest = new CategoriaDTORequest(""); // Nome vazio

            mockMvc.perform(post("/api/v1/categoria")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verify(categoriaService, never()).create(any());
        }
    }

    @Nested
    @DisplayName("Testes do endpoint GET /api/v1/categoria/{id}")
    class GetByIdTests {

        @Test
        @DisplayName("Deve retornar 200 OK com a categoria quando o ID existir")
        void getById_WhenIdExists_ShouldReturn200Ok() throws Exception {
            when(categoriaService.findById(id)).thenReturn(responseDTO);

            mockMvc.perform(get("/api/v1/categoria/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id.toString()))
                    .andExpect(jsonPath("$.nome").value("Estudos"));

            verify(categoriaService, times(1)).findById(id);
        }

        @Test
        @DisplayName("Deve retornar 404 Not Found quando a categoria não for encontrada")
        void getById_WhenIdDoesNotExist_ShouldReturn404NotFound() throws Exception {
            when(categoriaService.findById(id))
                    .thenThrow(new EntityNotFoundException("Nenhuma categoria encontrada para o ID: " + id));

            mockMvc.perform(get("/api/v1/categoria/{id}", id))
                    .andExpect(status().isNotFound());

            verify(categoriaService, times(1)).findById(id);
        }
    }

    @Nested
    @DisplayName("Testes do endpoint GET /api/v1/categoria")
    class GetAllTests {

        @Test
        @DisplayName("Deve retornar 200 OK com página de categorias")
        void getAll_ShouldReturn200OkWithPage() throws Exception {
            var page = new PageImpl<>(List.of(responseDTO), PageRequest.of(0, 5), 1);
            when(categoriaService.findAll(any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/api/v1/categoria")
                            .param("page", "0")
                            .param("size", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(id.toString()))
                    .andExpect(jsonPath("$.content[0].nome").value("Estudos"))
                    .andExpect(jsonPath("$.content[0].tarefas").isArray())
                    .andExpect(jsonPath("$.page.totalElements").value(1))
                    .andExpect(jsonPath("$.page.totalPages").value(1))
                    .andExpect(jsonPath("$.page.size").value(5))
                    .andExpect(jsonPath("$.page.number").value(0));

            verify(categoriaService, times(1)).findAll(any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("Testes do endpoint PUT /api/v1/categoria/{id}")
    class UpdateTests {

        @Test
        @DisplayName("Deve retornar 200 OK com categoria atualizada quando ID existir e payload for válido")
        void update_WhenValidDTOAndIdExists_ShouldReturn200Ok() throws Exception {
            when(categoriaService.update(any(CategoriaDTORequest.class), eq(id))).thenReturn(responseDTO);

            mockMvc.perform(put("/api/v1/categoria/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id.toString()))
                    .andExpect(jsonPath("$.nome").value("Estudos"));

            verify(categoriaService, times(1)).update(any(CategoriaDTORequest.class), eq(id));
        }

        @Test
        @DisplayName("Deve retornar 400 Bad Request quando o payload de atualização for inválido")
        void update_WhenInvalidDTO_ShouldReturn400BadRequest() throws Exception {
            CategoriaDTORequest invalidRequest = new CategoriaDTORequest("");

            mockMvc.perform(put("/api/v1/categoria/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verify(categoriaService, never()).update(any(), any());
        }
    }

    @Nested
    @DisplayName("Testes do endpoint DELETE /api/v1/categoria/{id}")
    class DeleteTests {

        @Test
        @DisplayName("Deve retornar 204 No Content quando a exclusão for bem-sucedida")
        void delete_WhenIdExists_ShouldReturn204NoContent() throws Exception {
            doNothing().when(categoriaService).delete(id);

            mockMvc.perform(delete("/api/v1/categoria/{id}", id))
                    .andExpect(status().isNoContent());

            verify(categoriaService, times(1)).delete(id);
        }
    }
}