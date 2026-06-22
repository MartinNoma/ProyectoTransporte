package geolocalizacion.proyect.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import geolocalizacion.proyect.Model.Ubicacion;
import geolocalizacion.proyect.Repository.UbicacionRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - UbicacionService")
class UbicacionServiceTest {

    @Mock
    private UbicacionRepository ubicacionRepository;

    @InjectMocks
    private UbicacionService ubicacionService;

    private Ubicacion ubicacion;

    @BeforeEach
    void setUp() {
        ubicacion = new Ubicacion(1L, "BUS-001", -33.4489, -70.6693, LocalDateTime.now());
    }

    @Test
    @DisplayName("registrarUbicacion - debe asignar fechaHora cuando es null")
    void registrarUbicacion_cuandoFechaHoraEsNull_debeAsignarFechaActual() {
        Ubicacion sinFecha = new Ubicacion(null, "BUS-002", -33.5, -70.7, null);
        when(ubicacionRepository.save(any(Ubicacion.class))).thenAnswer(inv -> {
            Ubicacion u = inv.getArgument(0);
            u.setId(2L);
            return u;
        });

        Ubicacion resultado = ubicacionService.registrarUbicacion(sinFecha);

        assertNotNull(resultado.getFechaHora(), "FechaHora debe ser asignada automaticamente");
        verify(ubicacionRepository, times(1)).save(sinFecha);
    }

    @Test
    @DisplayName("registrarUbicacion - debe guardar correctamente con fechaHora existente")
    void registrarUbicacion_cuandoFechaHoraExiste_debeGuardarSinModificar() {
        LocalDateTime fechaFija = LocalDateTime.of(2024, 1, 15, 10, 0);
        ubicacion.setFechaHora(fechaFija);
        when(ubicacionRepository.save(ubicacion)).thenReturn(ubicacion);

        Ubicacion resultado = ubicacionService.registrarUbicacion(ubicacion);

        assertEquals(fechaFija, resultado.getFechaHora());
        verify(ubicacionRepository, times(1)).save(ubicacion);
    }

    @Test
    @DisplayName("obtenerTodasLasUbicaciones - debe retornar lista completa")
    void obtenerTodasLasUbicaciones_debeRetornarListaCompleta() {
        Ubicacion ubicacion2 = new Ubicacion(2L, "BUS-002", -33.5, -70.8, LocalDateTime.now());
        when(ubicacionRepository.findAll()).thenReturn(Arrays.asList(ubicacion, ubicacion2));

        List<Ubicacion> resultado = ubicacionService.obtenerTodasLasUbicaciones();

        assertEquals(2, resultado.size());
        assertEquals("BUS-001", resultado.get(0).getUnidadTransporte());
        verify(ubicacionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("obtenerTodasLasUbicaciones - debe retornar lista vacia cuando no hay datos")
    void obtenerTodasLasUbicaciones_cuandoNoHayDatos_debeRetornarListaVacia() {
        when(ubicacionRepository.findAll()).thenReturn(List.of());

        List<Ubicacion> resultado = ubicacionService.obtenerTodasLasUbicaciones();

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("eliminarUbicacion - debe llamar deleteById correctamente")
    void eliminarUbicacion_debeEliminarPorId() {
        doNothing().when(ubicacionRepository).deleteById(1L);

        ubicacionService.eliminarUbicacion(1L);

        verify(ubicacionRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("actualizarUbicacion - debe actualizar campos correctamente cuando existe")
    void actualizarUbicacion_cuandoExiste_debeActualizarCampos() {
        Ubicacion datosNuevos = new Ubicacion(null, "BUS-999", -34.0, -71.0, null);
        when(ubicacionRepository.findById(1L)).thenReturn(Optional.of(ubicacion));
        when(ubicacionRepository.save(any(Ubicacion.class))).thenReturn(ubicacion);

        Optional<Ubicacion> resultado = ubicacionService.actualizarUbicacion(1L, datosNuevos);

        assertTrue(resultado.isPresent());
        assertEquals("BUS-999", resultado.get().getUnidadTransporte());
        assertEquals(-34.0, resultado.get().getLatitud());
        verify(ubicacionRepository, times(1)).save(ubicacion);
    }

    @Test
    @DisplayName("actualizarUbicacion - debe retornar empty cuando no existe")
    void actualizarUbicacion_cuandoNoExiste_debeRetornarEmpty() {
        when(ubicacionRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Ubicacion> resultado = ubicacionService.actualizarUbicacion(99L, ubicacion);

        assertTrue(resultado.isEmpty());
        verify(ubicacionRepository, times(0)).save(any());
    }
}
