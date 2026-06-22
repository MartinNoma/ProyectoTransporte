package geolocalizacion.proyect.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import geolocalizacion.proyect.Model.Ubicacion;
import geolocalizacion.proyect.Repository.UbicacionRepository;

@Service
public class UbicacionService {

    private final UbicacionRepository ubicacionRepository;

    public UbicacionService(UbicacionRepository ubicacionRepository) {
        this.ubicacionRepository = ubicacionRepository;
    }

    public Ubicacion registrarUbicacion(Ubicacion ubicacion) {
        if (ubicacion.getFechaHora() == null) {
            ubicacion.setFechaHora(LocalDateTime.now());
        }
        return ubicacionRepository.save(ubicacion);
    }

    public List<Ubicacion> obtenerTodasLasUbicaciones() {
        return ubicacionRepository.findAll();
    }

    public void eliminarUbicacion(Long id) {
        ubicacionRepository.deleteById(id);
    }

    // AGREGADO: metodo para actualizar una ubicacion existente
    public Optional<Ubicacion> actualizarUbicacion(Long id, Ubicacion datosNuevos) {
        return ubicacionRepository.findById(id).map(ubicacionExistente -> {
            if (datosNuevos.getUnidadTransporte() != null) {
                ubicacionExistente.setUnidadTransporte(datosNuevos.getUnidadTransporte());
            }
            if (datosNuevos.getLatitud() != null) {
                ubicacionExistente.setLatitud(datosNuevos.getLatitud());
            }
            if (datosNuevos.getLongitud() != null) {
                ubicacionExistente.setLongitud(datosNuevos.getLongitud());
            }
            if (datosNuevos.getFechaHora() != null) {
                ubicacionExistente.setFechaHora(datosNuevos.getFechaHora());
            }
            return ubicacionRepository.save(ubicacionExistente);
        });
    }
}
