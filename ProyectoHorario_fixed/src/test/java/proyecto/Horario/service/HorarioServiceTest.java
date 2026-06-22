package proyecto.Horario.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import proyecto.Horario.model.AlertaDTO;
import proyecto.Horario.model.Modelohorario;
import proyecto.Horario.repository.HorarioRepository;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HorarioServiceTest {

    @Mock
    private HorarioRepository repository;

    @InjectMocks
    private HorarioService service;

    private Modelohorario horario;

    @BeforeEach
    void setUp() {
        horario = new Modelohorario();
        horario.setId(1L);
        horario.setRuta("Ruta Centro");
        horario.setHoraSalida(LocalTime.of(8, 0));
        horario.setEstado("A_TIEMPO");
        horario.setAjusteMinutos(0);
    }

    @Test
    void crearHorario_deberiaAsignarFechaDeActualizacionCuandoEsNula() {
        when(repository.save(any(Modelohorario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Modelohorario resultado = service.crearHorario(horario);

        assertNotNull(resultado.getUltimaActualizacion());
        verify(repository, times(1)).save(horario);
    }

    @Test
    void crearHorario_noDeberiaSobrescribirFechaDeActualizacionExistente() {
        var fechaOriginal = java.time.LocalDateTime.of(2026, 1, 1, 10, 0);
        horario.setUltimaActualizacion(fechaOriginal);
        when(repository.save(any(Modelohorario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Modelohorario resultado = service.crearHorario(horario);

        assertEquals(fechaOriginal, resultado.getUltimaActualizacion());
    }

    @Test
    void listarHorarios_deberiaRetornarTodosLosHorarios() {
        when(repository.findAll()).thenReturn(Arrays.asList(horario, new Modelohorario()));

        List<Modelohorario> resultado = service.listarHorarios();

        assertEquals(2, resultado.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void actualizarHorario_deberiaActualizarCamposCuandoExiste() {
        Modelohorario datosNuevos = new Modelohorario();
        datosNuevos.setRuta("Ruta Norte");
        datosNuevos.setEstado("RETRASADO");
        datosNuevos.setAjusteMinutos(10);

        when(repository.findById(1L)).thenReturn(Optional.of(horario));
        when(repository.save(any(Modelohorario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Modelohorario> resultado = service.actualizarHorario(1L, datosNuevos);

        assertTrue(resultado.isPresent());
        assertEquals("Ruta Norte", resultado.get().getRuta());
        assertEquals("RETRASADO", resultado.get().getEstado());
        assertEquals(10, resultado.get().getAjusteMinutos());
        assertNotNull(resultado.get().getUltimaActualizacion());
    }

    @Test
    void actualizarHorario_deberiaRetornarVacioCuandoNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        Optional<Modelohorario> resultado = service.actualizarHorario(99L, horario);

        assertTrue(resultado.isEmpty());
        verify(repository, never()).save(any());
    }

    @Test
    void eliminarHorario_deberiaRetornarTrueCuandoExiste() {
        when(repository.existsById(1L)).thenReturn(true);

        boolean resultado = service.eliminarHorario(1L);

        assertTrue(resultado);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void eliminarHorario_deberiaRetornarFalseCuandoNoExiste() {
        when(repository.existsById(99L)).thenReturn(false);

        boolean resultado = service.eliminarHorario(99L);

        assertFalse(resultado);
        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    void actualizarHorariosPorAlerta_deberiaMarcarTodosComoRetrasados() {
        Modelohorario otro = new Modelohorario();
        otro.setId(2L);
        otro.setHoraSalida(LocalTime.of(9, 0));

        when(repository.findAll()).thenReturn(Arrays.asList(horario, otro));
        when(repository.save(any(Modelohorario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String resultado = service.actualizarHorariosPorAlerta(new AlertaDTO());

        assertEquals("Horarios actualizados correctamente", resultado);
        assertEquals("RETRASADO", horario.getEstado());
        assertEquals(15, horario.getAjusteMinutos());
        assertEquals(LocalTime.of(8, 15), horario.getHoraSalida());
        assertEquals(LocalTime.of(9, 15), otro.getHoraSalida());
        verify(repository, times(2)).save(any(Modelohorario.class));
    }
}
