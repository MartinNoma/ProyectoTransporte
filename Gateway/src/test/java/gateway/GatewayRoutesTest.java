package gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica que el Gateway expone correctamente las rutas hacia los
 * microservicios Horario, Ruta y Monitoreo (ademas de las ya existentes).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class GatewayRoutesTest {

    @Autowired
    private RouteLocator routeLocator;

    @Test
    void deberiaContenerLasRutasDeHorarioRutaYMonitoreo() {
        List<String> routeIds = routeLocator.getRoutes()
                .map(route -> route.getId())
                .collectList()
                .block();

        Set<String> ids = routeIds.stream().collect(Collectors.toSet());

        assertTrue(ids.contains("horario-test") || ids.contains("horario-service"),
                "Debe existir una ruta hacia el microservicio Horario");
        assertTrue(ids.contains("ruta-test") || ids.contains("ruta-service"),
                "Debe existir una ruta hacia el microservicio Ruta");
        assertTrue(ids.contains("monitoreo-test") || ids.contains("monitoreo-service"),
                "Debe existir una ruta hacia el microservicio Monitoreo");
    }
}
