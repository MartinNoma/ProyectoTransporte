package geolocalizacion.proyect.Controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import geolocalizacion.proyect.Model.Ubicacion;
import geolocalizacion.proyect.Service.UbicacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/ubicaciones")
@Tag(name = "Ubicaciones", description = "API para gestionar ubicaciones de unidades de transporte")
public class UbicacionController {

    private final UbicacionService ubicacionService;

    public UbicacionController(UbicacionService ubicacionService) {
        this.ubicacionService = ubicacionService;
    }

    @Operation(summary = "Registrar nueva ubicacion", description = "Crea un nuevo registro de ubicacion para una unidad de transporte")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Ubicacion registrada exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<EntityModel<Ubicacion>> registrarUbicacion(@RequestBody Ubicacion ubicacion) {
        try {
            Ubicacion nuevaUbicacion = ubicacionService.registrarUbicacion(ubicacion);
            EntityModel<Ubicacion> model = EntityModel.of(nuevaUbicacion);
            model.add(linkTo(methodOn(UbicacionController.class).obtenerUbicaciones()).withRel("todas-ubicaciones"));
            return ResponseEntity.status(HttpStatus.CREATED).body(model);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Listar todas las ubicaciones", description = "Retorna la lista completa de ubicaciones registradas")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de ubicaciones obtenida exitosamente"),
        @ApiResponse(responseCode = "204", description = "No hay ubicaciones registradas"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Ubicacion>>> obtenerUbicaciones() {
        try {
            List<Ubicacion> ubicaciones = ubicacionService.obtenerTodasLasUbicaciones();
            if (ubicaciones.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            List<EntityModel<Ubicacion>> models = ubicaciones.stream()
                .map(u -> {
                    EntityModel<Ubicacion> model = EntityModel.of(u);
                    model.add(linkTo(methodOn(UbicacionController.class).obtenerUbicaciones()).withRel("todas-ubicaciones"));
                    return model;
                })
                .collect(Collectors.toList());
            Link selfLink = linkTo(methodOn(UbicacionController.class).obtenerUbicaciones()).withSelfRel();
            return ResponseEntity.ok(CollectionModel.of(models, selfLink));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Eliminar ubicacion", description = "Elimina una ubicacion por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ubicacion eliminada correctamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarUbicacion(
            @Parameter(description = "ID de la ubicacion a eliminar") @PathVariable Long id) {
        try {
            ubicacionService.eliminarUbicacion(id);
            return ResponseEntity.ok("Ubicacion eliminada correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno al intentar eliminar la ubicacion.");
        }
    }

    @Operation(summary = "Actualizar ubicacion", description = "Actualiza los datos de una ubicacion existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ubicacion actualizada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Ubicacion no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Ubicacion>> actualizarUbicacion(
            @Parameter(description = "ID de la ubicacion a actualizar") @PathVariable Long id,
            @RequestBody Ubicacion datosNuevos) {
        try {
            return ubicacionService.actualizarUbicacion(id, datosNuevos)
                    .map(u -> {
                        EntityModel<Ubicacion> model = EntityModel.of(u);
                        model.add(linkTo(methodOn(UbicacionController.class).obtenerUbicaciones()).withRel("todas-ubicaciones"));
                        return ResponseEntity.ok(model);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
