package prediccion.proyecto2.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import prediccion.proyecto2.model.Prediccion;
import prediccion.proyecto2.service.PrediccionService;

@RestController
@RequestMapping("/api/predicciones")
@Tag(name = "Predicciones", description = "API para gestionar predicciones de llegada de unidades de transporte")
public class PrediccionController {

    @Autowired
    private PrediccionService service;

    @Operation(summary = "Listar todas las predicciones", description = "Retorna todas las predicciones registradas")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
        @ApiResponse(responseCode = "204", description = "No hay predicciones registradas")
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Prediccion>>> listar() {
        List<Prediccion> lista = service.listarTodos();
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<EntityModel<Prediccion>> models = lista.stream()
            .map(p -> {
                EntityModel<Prediccion> model = EntityModel.of(p);
                model.add(linkTo(methodOn(PrediccionController.class).listar()).withRel("todas-predicciones"));
                return model;
            })
            .collect(Collectors.toList());
        Link selfLink = linkTo(methodOn(PrediccionController.class).listar()).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(models, selfLink));
    }

    @Operation(summary = "Crear nueva prediccion", description = "Registra una nueva prediccion y notifica al microservicio de Geolocalizacion")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Prediccion creada exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<EntityModel<Prediccion>> guardar(@RequestBody Prediccion prediccion) {
        try {
            Prediccion nueva = service.guardar(prediccion);
            EntityModel<Prediccion> model = EntityModel.of(nueva);
            model.add(linkTo(methodOn(PrediccionController.class).listar()).withRel("todas-predicciones"));
            return new ResponseEntity<>(model, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Eliminar prediccion", description = "Elimina una prediccion por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Prediccion eliminada correctamente"),
        @ApiResponse(responseCode = "404", description = "Prediccion no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(
            @Parameter(description = "ID de la prediccion a eliminar") @PathVariable Long id) {
        try {
            boolean eliminado = service.eliminar(id);
            if (eliminado) {
                return ResponseEntity.ok("Prediccion eliminada correctamente.");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno al intentar eliminar la prediccion.");
        }
    }

    @Operation(summary = "Actualizar prediccion", description = "Actualiza los datos de una prediccion existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Prediccion actualizada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Prediccion no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Prediccion>> actualizar(
            @Parameter(description = "ID de la prediccion a actualizar") @PathVariable Long id,
            @RequestBody Prediccion datosNuevos) {
        try {
            return service.actualizar(id, datosNuevos)
                    .map(p -> {
                        EntityModel<Prediccion> model = EntityModel.of(p);
                        model.add(linkTo(methodOn(PrediccionController.class).listar()).withRel("todas-predicciones"));
                        return ResponseEntity.ok(model);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
