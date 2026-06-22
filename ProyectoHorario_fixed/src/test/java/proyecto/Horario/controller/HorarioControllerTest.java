package proyecto.Horario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import proyecto.Horario.model.AlertaDTO;
import proyecto.Horario.model.Modelohorario;
import proyecto.Horario.service.HorarioService;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de la capa web del microservicio Horario usando MockMvc en modo
 * "standalone" (no requiere levantar el contexto completo de Spring ni
 * conexion a base de datos).
 */
@ExtendWith(MockitoExtension.class)
class HorarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private HorarioService service;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @BeforeEach
    void setUp() {
        HorarioController controller = new HorarioController();
        ReflectionTestUtils.setField(controller, "service", service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void crear_deberiaRetornar201ConElHorarioCreado() throws Exception {
        Modelohorario entrada = new Modelohorario();
        entrada.setRuta("Ruta Centro");
        entrada.setHoraSalida(LocalTime.of(8, 0));

        Modelohorario guardado = new Modelohorario();
        guardado.setId(1L);
        guardado.setRuta("Ruta Centro");
        guardado.setHoraSalida(LocalTime.of(8, 0));
        guardado.setEstado("A_TIEMPO");

        when(service.crearHorario(any(Modelohorario.class))).thenReturn(guardado);

        mockMvc.perform(post("/api/horarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ruta").value("Ruta Centro"));
    }

    @Test
    void listar_deberiaRetornarListaDeHorarios() throws Exception {
        Modelohorario h1 = new Modelohorario();
        h1.setId(1L);
        h1.setRuta("Ruta A");
        Modelohorario h2 = new Modelohorario();
        h2.setId(2L);
        h2.setRuta("Ruta B");

        when(service.listarHorarios()).thenReturn(Arrays.asList(h1, h2));

        mockMvc.perform(get("/api/horarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].ruta").value("Ruta A"));
    }

    @Test
    void actualizar_deberiaRetornar200CuandoElHorarioExiste() throws Exception {
        Modelohorario actualizado = new Modelohorario();
        actualizado.setId(1L);
        actualizado.setRuta("Ruta Modificada");
        actualizado.setEstado("RETRASADO");

        when(service.actualizarHorario(eq(1L), any(Modelohorario.class)))
                .thenReturn(Optional.of(actualizado));

        mockMvc.perform(put("/api/horarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ruta").value("Ruta Modificada"));
    }

    @Test
    void actualizar_deberiaRetornar404CuandoNoExiste() throws Exception {
        when(service.actualizarHorario(eq(99L), any(Modelohorario.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/horarios/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Modelohorario())))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminar_deberiaRetornar200CuandoSeElimina() throws Exception {
        when(service.eliminarHorario(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/horarios/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Horario eliminado correctamente."));
    }

    @Test
    void eliminar_deberiaRetornar404CuandoNoExiste() throws Exception {
        when(service.eliminarHorario(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/horarios/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void actualizarPorAlerta_deberiaInvocarAlServicioYRetornarMensaje() throws Exception {
        when(service.actualizarHorariosPorAlerta(any(AlertaDTO.class)))
                .thenReturn("Horarios actualizados correctamente");

        AlertaDTO alerta = new AlertaDTO();
        alerta.setIdConductor(5L);
        alerta.setTipoAlerta("TRAFICO");
        alerta.setDescripcion("Congestion en la via principal");
        alerta.setEstado("ACTIVA");

        mockMvc.perform(post("/api/horarios/actualizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alerta)))
                .andExpect(status().isOk())
                .andExpect(content().string("Horarios actualizados correctamente"));
    }
}
