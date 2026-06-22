package prediccion.proyecto2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import prediccion.proyecto2.model.Prediccion;
import prediccion.proyecto2.repository.PrediccionRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PrediccionService {

    @Autowired
    private PrediccionRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    // CORREGIDO: URL externalizada via properties (antes hardcoded a localhost:8080).
    // Permite que en Docker apunte a http://geolocalizacion:8080 sin tocar codigo.
    @Value("${geolocalizacion.service.url:http://localhost:8080}")
    private String geolocalizacionBaseUrl;

    public List<Prediccion> listarTodos() {
        return repository.findAll();
    }

    public Prediccion guardar(Prediccion prediccion) {
        // fechaCreacion se asigna via @PrePersist en el modelo, pero lo dejamos
        // aqui tambien como respaldo
        if (prediccion.getFechaCreacion() == null) {
            prediccion.setFechaCreacion(LocalDateTime.now());
        }

        Prediccion nuevaPrediccion = repository.save(prediccion);

        // PUENTE: Enviar informacion al Microservicio 1 (Geolocalizacion)
        try {
            String urlGeolocalizacion = geolocalizacionBaseUrl + "/api/ubicaciones";

            Map<String, Object> datosParaEnviar = new HashMap<>();
            datosParaEnviar.put("unidadTransporte", nuevaPrediccion.getUnidadTransporte());
            datosParaEnviar.put("latitud", nuevaPrediccion.getLatitudPredicha());
            datosParaEnviar.put("longitud", nuevaPrediccion.getLongitudPredicha());

            restTemplate.postForEntity(urlGeolocalizacion, datosParaEnviar, String.class);
            System.out.println("Datos de prediccion enviados con exito al Microservicio de Geolocalizacion!");

        } catch (Exception e) {
            System.err.println("No se pudo enviar al Microservicio 1. Esta encendido? Error: " + e.getMessage());
        }

        return nuevaPrediccion;
    }

    // AGREGADO: metodo para eliminar una prediccion
    public boolean eliminar(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    // AGREGADO: metodo para actualizar una prediccion existente
    public Optional<Prediccion> actualizar(Long id, Prediccion datosNuevos) {
        return repository.findById(id).map(existente -> {
            if (datosNuevos.getUnidadTransporte() != null) {
                existente.setUnidadTransporte(datosNuevos.getUnidadTransporte());
            }
            if (datosNuevos.getLatitudPredicha() != null) {
                existente.setLatitudPredicha(datosNuevos.getLatitudPredicha());
            }
            if (datosNuevos.getLongitudPredicha() != null) {
                existente.setLongitudPredicha(datosNuevos.getLongitudPredicha());
            }
            if (datosNuevos.getTiempoEstimadoLlegada() != null) {
                existente.setTiempoEstimadoLlegada(datosNuevos.getTiempoEstimadoLlegada());
            }
            if (datosNuevos.getObservacion() != null) {
                existente.setObservacion(datosNuevos.getObservacion());
            }
            return repository.save(existente);
        });
    }
}
