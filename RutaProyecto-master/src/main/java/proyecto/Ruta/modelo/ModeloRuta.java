package proyecto.Ruta.modelo;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "rutas")
@Data
public class ModeloRuta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombreRuta;
    private String origen;
    private String destino;
    private String estado;
    private Integer tiempoEstimado;

}
