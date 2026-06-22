package proyecto.Monitoreo.modelo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Entity
@Table(name = "alertas")
public class ModeloAlerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID generado automaticamente", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "tipoAlerta es obligatorio")
    @Pattern(regexp = "CRITICA|ADVERTENCIA|INFO", message = "Debe ser CRITICA, ADVERTENCIA o INFO")
    @Schema(description = "Tipo de alerta", example = "CRITICA", allowableValues = {"CRITICA","ADVERTENCIA","INFO"})
    private String tipoAlerta;

    @NotBlank(message = "descripcion es obligatoria")
    @Schema(description = "Descripcion de la alerta", example = "Temperatura supera 90 grados")
    private String descripcion;

    @NotBlank(message = "estado es obligatorio")
    @Pattern(regexp = "ACTIVA|RESUELTA|PENDIENTE", message = "Debe ser ACTIVA, RESUELTA o PENDIENTE")
    @Schema(description = "Estado actual", example = "ACTIVA", allowableValues = {"ACTIVA","RESUELTA","PENDIENTE"})
    private String estado;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Fecha/hora auto-generada", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime fechaAlerta;

    public ModeloAlerta() { this.fechaAlerta = LocalDateTime.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTipoAlerta() { return tipoAlerta; }
    public void setTipoAlerta(String tipoAlerta) { this.tipoAlerta = tipoAlerta; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getFechaAlerta() { return fechaAlerta; }
    public void setFechaAlerta(LocalDateTime fechaAlerta) { this.fechaAlerta = fechaAlerta; }
}
