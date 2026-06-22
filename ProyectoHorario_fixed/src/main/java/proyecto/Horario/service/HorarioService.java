package proyecto.Horario.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import proyecto.Horario.model.AlertaDTO;
import proyecto.Horario.model.Modelohorario;
import proyecto.Horario.repository.HorarioRepository;

@Service
public class HorarioService {

    @Autowired
    private HorarioRepository repository;

    // Crear un horario nuevo
    public Modelohorario crearHorario(Modelohorario horario) {
        if (horario.getUltimaActualizacion() == null) {
            horario.setUltimaActualizacion(LocalDateTime.now());
        }
        return repository.save(horario);
    }

    // Listar todos los horarios
    public List<Modelohorario> listarHorarios() {
        return repository.findAll();
    }

    // Actualizar un horario individual por id
    public Optional<Modelohorario> actualizarHorario(Long id, Modelohorario datosNuevos) {
        return repository.findById(id).map(existente -> {
            if (datosNuevos.getRuta() != null) existente.setRuta(datosNuevos.getRuta());
            if (datosNuevos.getHoraSalida() != null) existente.setHoraSalida(datosNuevos.getHoraSalida());
            if (datosNuevos.getEstado() != null) existente.setEstado(datosNuevos.getEstado());
            if (datosNuevos.getAjusteMinutos() != null) existente.setAjusteMinutos(datosNuevos.getAjusteMinutos());
            existente.setUltimaActualizacion(LocalDateTime.now());
            return repository.save(existente);
        });
    }

    // Eliminar un horario por id
    public boolean eliminarHorario(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    // Actualización masiva recibida desde Monitoreo
    public String actualizarHorariosPorAlerta(AlertaDTO alerta) {
        List<Modelohorario> horarios = repository.findAll();
        for (Modelohorario horario : horarios) {
            horario.setEstado("RETRASADO");
            horario.setAjusteMinutos(15);
            horario.setHoraSalida(horario.getHoraSalida().plusMinutes(15));
            horario.setUltimaActualizacion(LocalDateTime.now());
            repository.save(horario);
        }
        return "Horarios actualizados correctamente";
    }
}
