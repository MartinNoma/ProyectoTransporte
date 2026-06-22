package proyecto.Horario.model;

import lombok.Data;

@Data
public class AlertaDTO {
    private Long idConductor;
    private String tipoAlerta;
    private String descripcion;
    private String estado;
}
