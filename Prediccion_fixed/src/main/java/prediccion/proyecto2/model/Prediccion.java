package prediccion.proyecto2.model;

import java.time.LocalDateTime;

import org.springframework.hateoas.RepresentationModel;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "predicciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Entidad que representa una prediccion de llegada de una unidad de transporte")
public class Prediccion extends RepresentationModel<Prediccion> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID unico de la prediccion", example = "1")
    private Long id;

    @Column(name = "unidad_transporte", nullable = false, length = 20)
    @Schema(description = "Identificador de la unidad de transporte", example = "BUS-001")
    private String unidadTransporte;

    @Column(name = "latitud_predicha")
    @Schema(description = "Latitud predicha de llegada", example = "-33.4489")
    private Double latitudPredicha;

    @Column(name = "longitud_predicha")
    @Schema(description = "Longitud predicha de llegada", example = "-70.6693")
    private Double longitudPredicha;

    @Column(nullable = false)
    @Schema(description = "Tiempo estimado de llegada", example = "2024-01-15T12:00:00")
    private LocalDateTime tiempoEstimadoLlegada;

    @Column(updatable = false)
    @Schema(description = "Fecha de creacion del registro (auto-generada)", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime fechaCreacion;

    @Column(length = 100)
    @Schema(description = "Observaciones adicionales", example = "Trafico moderado en ruta")
    private String observacion;

    @PrePersist
    public void prePersist() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
    }
}
