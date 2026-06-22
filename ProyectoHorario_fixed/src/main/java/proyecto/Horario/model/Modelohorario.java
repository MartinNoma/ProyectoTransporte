package proyecto.Horario.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Data
@Table(name ="horarios")
public class Modelohorario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ruta;
    private LocalTime horaSalida;
    private String estado;
    private LocalDateTime ultimaActualizacion;
    private Integer ajusteMinutos;

}
