package proyecto.Monitoreo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import proyecto.Monitoreo.modelo.ModeloAlerta;
import proyecto.Monitoreo.repository.AlertaRepository;

import java.util.List;
import java.util.Optional;

@Service
public class MonitoreoService {

    @Autowired
    private AlertaRepository repository;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${horario.service.url:http://localhost:8082/api/horarios/actualizar}")
    private String horarioServiceUrl;

    public ModeloAlerta guardarAlerta(ModeloAlerta alerta) {
        ModeloAlerta guardada = repository.save(alerta);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<ModeloAlerta> req = new HttpEntity<>(guardada, headers);
            restTemplate.postForEntity(horarioServiceUrl, req, String.class);
        } catch (Exception e) {
            System.err.println("MS Horario no disponible: " + e.getMessage());
        }
        return guardada;
    }

    public List<ModeloAlerta> listarAlertas() {
        return repository.findAll();
    }

    public Optional<ModeloAlerta> actualizarAlerta(Long id, ModeloAlerta datos) {
        return repository.findById(id).map(existente -> {
            if (datos.getTipoAlerta() != null) existente.setTipoAlerta(datos.getTipoAlerta());
            if (datos.getDescripcion() != null) existente.setDescripcion(datos.getDescripcion());
            if (datos.getEstado() != null) existente.setEstado(datos.getEstado());
            return repository.save(existente);
        });
    }

    public boolean eliminarAlerta(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
