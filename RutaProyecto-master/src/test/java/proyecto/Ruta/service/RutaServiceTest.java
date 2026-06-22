package proyecto.Ruta.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import proyecto.Ruta.modelo.HorarioDTO;
import proyecto.Ruta.modelo.ModeloRuta;
import proyecto.Ruta.repository.RutaRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RutaServiceTest {

    @Mock
    private RutaRepository repository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RutaService service;

    private ModeloRuta ruta;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "horarioServiceUrl", "http://localhost:8082/api/horarios");

        ruta = new ModeloRuta();
        ruta.setId(1L);
        ruta.setNombreRuta("Ruta Centro");
        ruta.setOrigen("Terminal A");
        ruta.setDestino("Terminal B");
        ruta.setEstado("NORMAL");
        ruta.setTiempoEstimado(30);
    }

    @Test
    void listarRutas_deberiaRetornarTodasLasRutas() {
        when(repository.findAll()).thenReturn(Arrays.asList(ruta, new ModeloRuta()));

        List<ModeloRuta> resultado = service.listarRutas();

        assertEquals(2, resultado.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void actualizarEstadoRutas_deberiaMarcarTodasComoAjustadasYSumar15Minutos() {
        when(repository.findAll()).thenReturn(Arrays.asList(ruta));
        when(repository.save(any(ModeloRuta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String resultado = service.actualizarEstadoRutas();

        assertEquals("Rutas actualizadas correctamente", resultado);
        assertEquals("AJUSTADA", ruta.getEstado());
        assertEquals(45, ruta.getTiempoEstimado());
        verify(repository, times(1)).save(ruta);
    }

    @Test
    void obtenerHorariosActualizados_deberiaConsultarAlMicroservicioHorario() {
        HorarioDTO dto = new HorarioDTO();
        dto.setRuta("Ruta Centro");
        dto.setEstado("RETRASADO");
        dto.setAjusteMinutos(15);

        ResponseEntity<List<HorarioDTO>> respuestaSimulada = ResponseEntity.ok(List.of(dto));

        when(restTemplate.exchange(
                eq("http://localhost:8082/api/horarios"),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)))
                .thenReturn(respuestaSimulada);

        List<HorarioDTO> resultado = service.obtenerHorariosActualizados();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Ruta Centro", resultado.get(0).getRuta());
        assertEquals("RETRASADO", resultado.get(0).getEstado());
    }
}
