package proyecto.Monitoreo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.Monitoreo.modelo.ModeloAlerta;

public interface AlertaRepository extends JpaRepository<ModeloAlerta, Long> {
}
