package proyecto.Horario.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.Horario.model.AlertaDTO;
import proyecto.Horario.model.Modelohorario;
import proyecto.Horario.service.HorarioService;

import java.util.List;

@RestController
@RequestMapping("/api/horarios")
@Tag(name = "Horarios", description = "Operaciones CRUD de horarios y actualizacion por alertas")
public class HorarioController {

    @Autowired
    private HorarioService service;

    // crear un horario nuevo
    @PostMapping
    @Operation(summary = "Crear un horario", description = "Registra un nuevo horario en el sistema")
    @ApiResponse(responseCode = "201", description = "Horario creado correctamente")
    @ApiResponse(responseCode = "500", description = "Error interno al crear el horario")
    public ResponseEntity<Modelohorario> crear(@RequestBody Modelohorario horario) {
        try {
            Modelohorario nuevo = service.crearHorario(horario);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Listar todos los horarios
    @GetMapping
    @Operation(summary = "Listar horarios", description = "Obtiene todos los horarios registrados")
    @ApiResponse(responseCode = "200", description = "Listado de horarios obtenido correctamente")
    public ResponseEntity<List<Modelohorario>> listar() {
        List<Modelohorario> lista = service.listarHorarios();
        return ResponseEntity.ok(lista);
    }

    // actualizar un horario individual por id
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un horario", description = "Actualiza los datos de un horario existente por su id")
    @ApiResponse(responseCode = "200", description = "Horario actualizado correctamente")
    @ApiResponse(responseCode = "404", description = "Horario no encontrado")
    @ApiResponse(responseCode = "500", description = "Error interno al actualizar el horario")
    public ResponseEntity<Modelohorario> actualizar(@PathVariable Long id,
                                                     @RequestBody Modelohorario datosNuevos) {
        try {
            return service.actualizarHorario(id, datosNuevos)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // eliminar un horario por id
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un horario", description = "Elimina un horario existente por su id")
    @ApiResponse(responseCode = "200", description = "Horario eliminado correctamente")
    @ApiResponse(responseCode = "404", description = "Horario no encontrado")
    @ApiResponse(responseCode = "500", description = "Error interno al eliminar el horario")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        try {
            boolean eliminado = service.eliminarHorario(id);
            if (eliminado) return ResponseEntity.ok("Horario eliminado correctamente.");
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el horario.");
        }
    }

    // Endpoint interno: recibe alertas del MS Monitoreo y actualiza todos los horarios
    @PostMapping("/actualizar")
    @Operation(summary = "Actualizar horarios por alerta",
            description = "Endpoint interno usado por otros microservicios (p.ej. Monitoreo) para "
                    + "notificar una alerta y forzar el reajuste de todos los horarios")
    @ApiResponse(responseCode = "200", description = "Horarios actualizados correctamente")
    public String actualizarPorAlerta(@RequestBody AlertaDTO alerta) {
        return service.actualizarHorariosPorAlerta(alerta);
    }
}
