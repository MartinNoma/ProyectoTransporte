package prediccion.proyecto2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import prediccion.proyecto2.model.Prediccion;

@Repository
public interface PrediccionRepository extends JpaRepository<Prediccion, Long> {

}