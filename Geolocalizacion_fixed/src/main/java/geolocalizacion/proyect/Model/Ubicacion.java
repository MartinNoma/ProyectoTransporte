package geolocalizacion.proyect.Model;

import java.time.LocalDateTime;

import org.springframework.hateoas.RepresentationModel;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ubicaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Entidad que representa la ubicacion de una unidad de transporte")
public class Ubicacion extends RepresentationModel<Ubicacion> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID unico de la ubicacion", example = "1")
    private Long id;

    @Schema(description = "Identificador de la unidad de transporte", example = "BUS-001")
    private String unidadTransporte;

    @Schema(description = "Coordenada de latitud", example = "-33.4489")
    private Double latitud;

    @Schema(description = "Coordenada de longitud", example = "-70.6693")
    private Double longitud;

    @Schema(description = "Fecha y hora del registro", example = "2024-01-15T10:30:00")
    private LocalDateTime fechaHora;

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }
}
