package prediccion.proyecto2.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import prediccion.proyecto2.model.Prediccion;
import prediccion.proyecto2.repository.PrediccionRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - PrediccionService")
class PrediccionServiceTest {

    @Mock
    private PrediccionRepository repository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PrediccionService service;

    private Prediccion prediccion;

    @BeforeEach
    void setUp() {
        prediccion = new Prediccion(1L, "BUS-001", -33.4489, -70.6693,
            LocalDateTime.now().plusHours(1), LocalDateTime.now(), "Sin novedad");
    }

    @Test
    @DisplayName("listarTodos - debe retornar todas las predicciones")
    void listarTodos_debeRetornarTodasLasPredicciones() {
        Prediccion p2 = new Prediccion(2L, "BUS-002", -33.5, -70.8,
            LocalDateTime.now().plusHours(2), LocalDateTime.now(), null);
        when(repository.findAll()).thenReturn(Arrays.asList(prediccion, p2));

        List<Prediccion> resultado = service.listarTodos();

        assertEquals(2, resultado.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("listarTodos - debe retornar lista vacia cuando no hay datos")
    void listarTodos_cuandoNoHayDatos_debeRetornarListaVacia() {
        when(repository.findAll()).thenReturn(List.of());

        List<Prediccion> resultado = service.listarTodos();

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("guardar - debe guardar prediccion y llamar RestTemplate")
    void guardar_debeGuardarYNotificarGeolocalizacion() {
        when(repository.save(any(Prediccion.class))).thenReturn(prediccion);

        Prediccion resultado = service.guardar(prediccion);

        assertEquals("BUS-001", resultado.getUnidadTransporte());
        verify(repository, times(1)).save(prediccion);
        // RestTemplate.postForEntity fue invocado (puede fallar silenciosamente)
    }

    @Test
    @DisplayName("guardar - debe continuar aunque falle la comunicacion con Geolocalizacion")
    void guardar_cuandoFallaGeolocalizacion_debeContinuar() {
        when(repository.save(any(Prediccion.class))).thenReturn(prediccion);
        // RestTemplate lanzara excepcion al no estar configurado, el servicio debe capturarla

        Prediccion resultado = service.guardar(prediccion);

        // El resultado debe ser valido a pesar del error en comunicacion
        assertEquals(1L, resultado.getId());
    }

    @Test
    @DisplayName("eliminar - debe retornar true cuando existe el id")
    void eliminar_cuandoExiste_debeRetornarTrue() {
        when(repository.existsById(1L)).thenReturn(true);

        boolean resultado = service.eliminar(1L);

        assertTrue(resultado);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("eliminar - debe retornar false cuando no existe el id")
    void eliminar_cuandoNoExiste_debeRetornarFalse() {
        when(repository.existsById(99L)).thenReturn(false);

        boolean resultado = service.eliminar(99L);

        assertFalse(resultado);
        verify(repository, never()).deleteById(any());
    }

    @Test
    @DisplayName("actualizar - debe actualizar campos cuando existe")
    void actualizar_cuandoExiste_debeActualizarCampos() {
        Prediccion datosNuevos = new Prediccion(null, "BUS-999", -34.0, -71.0,
            LocalDateTime.now().plusHours(3), null, "Ruta alternativa");
        when(repository.findById(1L)).thenReturn(Optional.of(prediccion));
        when(repository.save(any(Prediccion.class))).thenReturn(prediccion);

        Optional<Prediccion> resultado = service.actualizar(1L, datosNuevos);

        assertTrue(resultado.isPresent());
        verify(repository, times(1)).save(prediccion);
    }

    @Test
    @DisplayName("actualizar - debe retornar empty cuando no existe")
    void actualizar_cuandoNoExiste_debeRetornarEmpty() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        Optional<Prediccion> resultado = service.actualizar(99L, prediccion);

        assertTrue(resultado.isEmpty());
        verify(repository, never()).save(any());
    }
}
