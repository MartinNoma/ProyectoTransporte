package prediccion.proyecto2.controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import prediccion.proyecto2.model.Prediccion;
import prediccion.proyecto2.service.PrediccionService;

@WebMvcTest(PrediccionController.class)
@DisplayName("Pruebas Unitarias - PrediccionController")
class PrediccionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PrediccionService service;

    private ObjectMapper objectMapper;
    private Prediccion prediccion;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        prediccion = new Prediccion(1L, "BUS-001", -33.4489, -70.6693,
            LocalDateTime.now().plusHours(1), LocalDateTime.now(), "Sin novedad");
    }

    @Test
    @DisplayName("GET /api/predicciones - debe retornar lista con 200")
    void listar_debeRetornar200ConLista() throws Exception {
        List<Prediccion> lista = Arrays.asList(prediccion,
            new Prediccion(2L, "BUS-002", -33.5, -70.8, LocalDateTime.now().plusHours(2), LocalDateTime.now(), null));
        when(service.listarTodos()).thenReturn(lista);

        mockMvc.perform(get("/api/predicciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").exists());
    }

    @Test
    @DisplayName("GET /api/predicciones - debe retornar 204 cuando lista vacia")
    void listar_cuandoVacia_debeRetornar204() throws Exception {
        when(service.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/predicciones"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /api/predicciones - debe crear prediccion y retornar 201")
    void guardar_debeRetornar201() throws Exception {
        when(service.guardar(any(Prediccion.class))).thenReturn(prediccion);

        mockMvc.perform(post("/api/predicciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(prediccion)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.unidadTransporte").value("BUS-001"))
                .andExpect(jsonPath("$._links").exists());
    }

    @Test
    @DisplayName("POST /api/predicciones - debe retornar 500 cuando falla el servicio")
    void guardar_cuandoFalla_debeRetornar500() throws Exception {
        when(service.guardar(any())).thenThrow(new RuntimeException("Error BD"));

        mockMvc.perform(post("/api/predicciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(prediccion)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("DELETE /api/predicciones/{id} - debe retornar 200 al eliminar")
    void eliminar_cuandoExiste_debeRetornar200() throws Exception {
        when(service.eliminar(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/predicciones/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/predicciones/{id} - debe retornar 404 cuando no existe")
    void eliminar_cuandoNoExiste_debeRetornar404() throws Exception {
        when(service.eliminar(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/predicciones/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/predicciones/{id} - debe actualizar y retornar 200")
    void actualizar_cuandoExiste_debeRetornar200() throws Exception {
        when(service.actualizar(eq(1L), any(Prediccion.class))).thenReturn(Optional.of(prediccion));

        mockMvc.perform(put("/api/predicciones/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(prediccion)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unidadTransporte").value("BUS-001"));
    }

    @Test
    @DisplayName("PUT /api/predicciones/{id} - debe retornar 404 cuando no existe")
    void actualizar_cuandoNoExiste_debeRetornar404() throws Exception {
        when(service.actualizar(eq(99L), any(Prediccion.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/predicciones/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(prediccion)))
                .andExpect(status().isNotFound());
    }
}
