package proyecto.Ruta.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import proyecto.Ruta.modelo.HorarioDTO;
import proyecto.Ruta.modelo.ModeloRuta;
import proyecto.Ruta.service.RutaService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de la capa web del microservicio Ruta usando MockMvc en modo
 * "standalone" (no requiere levantar el contexto completo de Spring ni
 * conexion a base de datos ni al microservicio Horario real).
 */
@ExtendWith(MockitoExtension.class)
class RutaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RutaService service;

    @BeforeEach
    void setUp() {
        RutaController controller = new RutaController();
        ReflectionTestUtils.setField(controller, "service", service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void listar_deberiaRetornarListaDeRutas() throws Exception {
        ModeloRuta r1 = new ModeloRuta();
        r1.setId(1L);
        r1.setNombreRuta("Ruta Centro");
        ModeloRuta r2 = new ModeloRuta();
        r2.setId(2L);
        r2.setNombreRuta("Ruta Norte");

        when(service.listarRutas()).thenReturn(Arrays.asList(r1, r2));

        mockMvc.perform(get("/api/rutas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombreRuta").value("Ruta Centro"));
    }

    @Test
    void horarios_deberiaRetornarHorariosDelMicroservicioHorario() throws Exception {
        HorarioDTO dto = new HorarioDTO();
        dto.setRuta("Ruta Centro");
        dto.setEstado("RETRASADO");
        dto.setAjusteMinutos(15);

        when(service.obtenerHorariosActualizados()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/rutas/horarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ruta").value("Ruta Centro"))
                .andExpect(jsonPath("$[0].estado").value("RETRASADO"))
                .andExpect(jsonPath("$[0].ajusteMinutos").value(15));
    }

    @Test
    void actualizar_deberiaRetornarMensajeDeConfirmacion() throws Exception {
        when(service.actualizarEstadoRutas()).thenReturn("Rutas actualizadas correctamente");

        mockMvc.perform(put("/api/rutas/actualizar"))
                .andExpect(status().isOk())
                .andExpect(content().string("Rutas actualizadas correctamente"));
    }
}
