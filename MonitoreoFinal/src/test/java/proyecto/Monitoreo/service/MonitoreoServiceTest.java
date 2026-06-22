package proyecto.Monitoreo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import proyecto.Monitoreo.modelo.ModeloAlerta;
import proyecto.Monitoreo.repository.AlertaRepository;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MonitoreoServiceTest {

    @Mock
    private AlertaRepository repository;

    @InjectMocks
    private MonitoreoService service;

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
    @DisplayName("Guardar alerta exitosamente")
    void guardarAlerta_exitoso() {
        when(repository.save(any())).thenReturn(alerta);
        ModeloAlerta result = service.guardarAlerta(alerta);
        assertThat(result.getTipoAlerta()).isEqualTo("CRITICA");
        verify(repository).save(any());
    }

    @Test
    @DisplayName("Listar alertas retorna lista")
    void listarAlertas_retornaLista() {
        when(repository.findAll()).thenReturn(List.of(alerta));
        assertThat(service.listarAlertas()).hasSize(1);
    }

    @Test
    @DisplayName("Actualizar alerta existente")
    void actualizarAlerta_existente() {
        ModeloAlerta nuevos = new ModeloAlerta();
        nuevos.setEstado("RESUELTA");
        when(repository.findById(1L)).thenReturn(Optional.of(alerta));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));
        Optional<ModeloAlerta> result = service.actualizarAlerta(1L, nuevos);
        assertThat(result).isPresent();
        assertThat(result.get().getEstado()).isEqualTo("RESUELTA");
    }

    @Test
    @DisplayName("Actualizar alerta inexistente retorna vacio")
    void actualizarAlerta_noExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThat(service.actualizarAlerta(99L, alerta)).isEmpty();
    }

    @Test
    @DisplayName("Eliminar alerta existente retorna true")
    void eliminarAlerta_existente() {
        when(repository.existsById(1L)).thenReturn(true);
        assertThat(service.eliminarAlerta(1L)).isTrue();
        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar alerta inexistente retorna false")
    void eliminarAlerta_noExiste() {
        when(repository.existsById(99L)).thenReturn(false);
        assertThat(service.eliminarAlerta(99L)).isFalse();
        verify(repository, never()).deleteById(any());
    }
}
