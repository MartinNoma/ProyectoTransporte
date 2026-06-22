package proyecto.Horario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.Horario.model.Modelohorario;

public interface HorarioRepository extends JpaRepository<Modelohorario, Long> {
}
