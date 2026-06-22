package proyecto.Ruta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.Ruta.modelo.ModeloRuta;

public interface RutaRepository  extends JpaRepository<ModeloRuta,Long>{
}
