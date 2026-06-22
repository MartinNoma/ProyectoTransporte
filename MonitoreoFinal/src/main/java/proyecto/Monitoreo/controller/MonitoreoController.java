package proyecto.Monitoreo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.Monitoreo.modelo.ModeloAlerta;
import proyecto.Monitoreo.service.MonitoreoService;
import java.util.List;

@RestController
@RequestMapping("/api/monitoreo")
@Tag(name = "2. Alertas", description = "CRUD de alertas - requiere token JWT")
@SecurityRequirement(name = "bearerAuth")
public class MonitoreoController {

    @Autowired
    private MonitoreoService service;

    @Operation(summary = "Crear alerta")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Creada"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos"),
        @ApiResponse(responseCode = "401", description = "Sin token")
    })
    @PostMapping("/alerta")
    public ResponseEntity<ModeloAlerta> crear(@Valid @RequestBody ModeloAlerta alerta) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardarAlerta(alerta));
    }

    @Operation(summary = "Listar todas las alertas")
    @ApiResponse(responseCode = "200", description = "Lista de alertas")
    @GetMapping("/alerta")
    public ResponseEntity<List<ModeloAlerta>> listar() {
        return ResponseEntity.ok(service.listarAlertas());
    }

    @Operation(summary = "Actualizar alerta por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Actualizada"),
        @ApiResponse(responseCode = "404", description = "No encontrada")
    })
    @PutMapping("/alerta/{id}")
    public ResponseEntity<ModeloAlerta> actualizar(
            @Parameter(description = "ID de la alerta", example = "1") @PathVariable Long id,
            @Valid @RequestBody ModeloAlerta datos) {
        return service.actualizarAlerta(id, datos)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar alerta por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Eliminada"),
        @ApiResponse(responseCode = "404", description = "No encontrada")
    })
    @DeleteMapping("/alerta/{id}")
    public ResponseEntity<String> eliminar(
            @Parameter(description = "ID de la alerta", example = "1") @PathVariable Long id) {
        if (service.eliminarAlerta(id)) return ResponseEntity.ok("Alerta eliminada correctamente.");
        return ResponseEntity.notFound().build();
    }
}
