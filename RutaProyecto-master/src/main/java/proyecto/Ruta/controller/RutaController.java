package proyecto.Ruta.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import proyecto.Ruta.modelo.HorarioDTO;
import proyecto.Ruta.modelo.ModeloRuta;
import proyecto.Ruta.service.RutaService;

import java.util.List;

@RestController
@RequestMapping("/api/rutas")
@Tag(name = "Rutas", description = "Operaciones de consulta y ajuste de rutas, integradas con el microservicio Horario")
public class RutaController {
    @Autowired
    private RutaService service;

    @GetMapping
    @Operation(summary = "Listar rutas", description = "Obtiene todas las rutas registradas")
    @ApiResponse(responseCode = "200", description = "Listado de rutas obtenido correctamente")
    public List<ModeloRuta> listar() {
        return service.listarRutas();
    }

    @GetMapping("/horarios")
    @Operation(summary = "Obtener horarios actualizados",
            description = "Consulta en tiempo real al microservicio Horario para obtener el estado actual de los horarios")
    @ApiResponse(responseCode = "200", description = "Horarios obtenidos correctamente desde el microservicio Horario")
    public List<HorarioDTO> horarios() {
        return service.obtenerHorariosActualizados();
    }

    @PutMapping("/actualizar")
    @Operation(summary = "Actualizar estado de las rutas",
            description = "Marca todas las rutas como AJUSTADA y suma 15 minutos al tiempo estimado")
    @ApiResponse(responseCode = "200", description = "Rutas actualizadas correctamente")
    public String actualizar() {
        return service.actualizarEstadoRutas();
    }
}
