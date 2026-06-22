package proyecto.Monitoreo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import proyecto.Monitoreo.config.SecurityConfig;
import proyecto.Monitoreo.modelo.ModeloAlerta;
import proyecto.Monitoreo.security.JwtFilter;
import proyecto.Monitoreo.security.JwtUtil;
import proyecto.Monitoreo.service.MonitoreoService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MonitoreoController.class)
@Import({SecurityConfig.class, JwtFilter.class, JwtUtil.class})
class MonitoreoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MonitoreoService service;

    @Autowired
    private ObjectMapper mapper;

    private ModeloAlerta alerta;

    @BeforeEach
    void setUp() {
        alerta = new ModeloAlerta();
        alerta.setId(1L);
        alerta.setTipoAlerta("CRITICA");
        alerta.setDescripcion("Temperatura alta");
        alerta.setEstado("ACTIVA");
    }

    @Test
    @DisplayName("POST /alerta sin token - debe retornar 401")
    void crearAlerta_sinToken_retorna401() throws Exception {
        mockMvc.perform(post("/api/monitoreo/alerta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(alerta)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin")
    @DisplayName("POST /alerta con token - debe retornar 201")
    void crearAlerta_conToken_retorna201() throws Exception {
        when(service.guardarAlerta(any())).thenReturn(alerta);
        mockMvc.perform(post("/api/monitoreo/alerta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(alerta)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipoAlerta").value("CRITICA"));
    }

    @Test
    @WithMockUser(username = "admin")
    @DisplayName("POST /alerta con datos invalidos - debe retornar 400")
    void crearAlerta_datosInvalidos_retorna400() throws Exception {
        ModeloAlerta invalida = new ModeloAlerta();
        invalida.setTipoAlerta("NO_EXISTE");
        invalida.setDescripcion("");
        invalida.setEstado("ACTIVA");

        mockMvc.perform(post("/api/monitoreo/alerta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(invalida)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin")
    @DisplayName("GET /alerta - debe retornar lista")
    void listarAlertas_retornaLista() throws Exception {
        when(service.listarAlertas()).thenReturn(List.of(alerta));
        mockMvc.perform(get("/api/monitoreo/alerta"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("ACTIVA"));
    }

    @Test
    @DisplayName("GET /alerta sin token - debe retornar 401")
    void listarAlertas_sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/monitoreo/alerta"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin")
    @DisplayName("PUT /alerta/{id} existente - debe retornar 200")
    void actualizarAlerta_existente_retorna200() throws Exception {
        when(service.actualizarAlerta(eq(1L), any())).thenReturn(Optional.of(alerta));
        mockMvc.perform(put("/api/monitoreo/alerta/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(alerta)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin")
    @DisplayName("PUT /alerta/{id} inexistente - debe retornar 404")
    void actualizarAlerta_noEncontrada_retorna404() throws Exception {
        when(service.actualizarAlerta(eq(99L), any())).thenReturn(Optional.empty());
        mockMvc.perform(put("/api/monitoreo/alerta/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(alerta)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin")
    @DisplayName("DELETE /alerta/{id} existente - debe retornar 200")
    void eliminarAlerta_existente_retorna200() throws Exception {
        when(service.eliminarAlerta(1L)).thenReturn(true);
        mockMvc.perform(delete("/api/monitoreo/alerta/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Alerta eliminada correctamente."));
    }

    @Test
    @WithMockUser(username = "admin")
    @DisplayName("DELETE /alerta/{id} inexistente - debe retornar 404")
    void eliminarAlerta_noEncontrada_retorna404() throws Exception {
        when(service.eliminarAlerta(99L)).thenReturn(false);
        mockMvc.perform(delete("/api/monitoreo/alerta/99"))
                .andExpect(status().isNotFound());
    }
}
