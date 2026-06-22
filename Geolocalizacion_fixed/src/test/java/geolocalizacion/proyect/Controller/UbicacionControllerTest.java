package geolocalizacion.proyect.Controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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

import geolocalizacion.proyect.Model.Ubicacion;
import geolocalizacion.proyect.Service.UbicacionService;

@WebMvcTest(UbicacionController.class)
@DisplayName("Pruebas Unitarias - UbicacionController")
class UbicacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UbicacionService ubicacionService;

    private ObjectMapper objectMapper;
    private Ubicacion ubicacion;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        ubicacion = new Ubicacion(1L, "BUS-001", -33.4489, -70.6693, LocalDateTime.now());
    }

    @Test
    @DisplayName("POST /api/ubicaciones - debe crear ubicacion y retornar 201")
    void registrarUbicacion_debeRetornar201() throws Exception {
        when(ubicacionService.registrarUbicacion(any(Ubicacion.class))).thenReturn(ubicacion);

        mockMvc.perform(post("/api/ubicaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ubicacion)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.unidadTransporte").value("BUS-001"))
                .andExpect(jsonPath("$._links").exists());
    }

    @Test
    @DisplayName("POST /api/ubicaciones - debe retornar 500 cuando falla el servicio")
    void registrarUbicacion_cuandoFalla_debeRetornar500() throws Exception {
        when(ubicacionService.registrarUbicacion(any())).thenThrow(new RuntimeException("Error BD"));

        mockMvc.perform(post("/api/ubicaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ubicacion)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("GET /api/ubicaciones - debe retornar lista con 200")
    void obtenerUbicaciones_debeRetornar200ConLista() throws Exception {
        List<Ubicacion> lista = Arrays.asList(ubicacion,
            new Ubicacion(2L, "BUS-002", -33.5, -70.8, LocalDateTime.now()));
        when(ubicacionService.obtenerTodasLasUbicaciones()).thenReturn(lista);

        mockMvc.perform(get("/api/ubicaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").exists());
    }

    @Test
    @DisplayName("GET /api/ubicaciones - debe retornar 204 cuando lista vacia")
    void obtenerUbicaciones_cuandoVacia_debeRetornar204() throws Exception {
        when(ubicacionService.obtenerTodasLasUbicaciones()).thenReturn(List.of());

        mockMvc.perform(get("/api/ubicaciones"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/ubicaciones/{id} - debe retornar 200 al eliminar")
    void eliminarUbicacion_debeRetornar200() throws Exception {
        doNothing().when(ubicacionService).eliminarUbicacion(1L);

        mockMvc.perform(delete("/api/ubicaciones/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/ubicaciones/{id} - debe retornar 500 cuando falla")
    void eliminarUbicacion_cuandoFalla_debeRetornar500() throws Exception {
        doThrow(new RuntimeException("Error")).when(ubicacionService).eliminarUbicacion(1L);

        mockMvc.perform(delete("/api/ubicaciones/1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("PUT /api/ubicaciones/{id} - debe actualizar y retornar 200")
    void actualizarUbicacion_cuandoExiste_debeRetornar200() throws Exception {
        when(ubicacionService.actualizarUbicacion(eq(1L), any(Ubicacion.class)))
            .thenReturn(Optional.of(ubicacion));

        mockMvc.perform(put("/api/ubicaciones/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ubicacion)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unidadTransporte").value("BUS-001"));
    }

    @Test
    @DisplayName("PUT /api/ubicaciones/{id} - debe retornar 404 cuando no existe")
    void actualizarUbicacion_cuandoNoExiste_debeRetornar404() throws Exception {
        when(ubicacionService.actualizarUbicacion(eq(99L), any(Ubicacion.class)))
            .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/ubicaciones/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ubicacion)))
                .andExpect(status().isNotFound());
    }
}
