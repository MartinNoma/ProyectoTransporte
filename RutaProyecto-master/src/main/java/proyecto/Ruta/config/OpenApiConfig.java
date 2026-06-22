package proyecto.Ruta.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion de Swagger / OpenAPI para el microservicio Ruta.
 *
 * Documentacion disponible en:
 *  - Swagger UI: http://localhost:8083/swagger-ui.html
 *  - OpenAPI JSON: http://localhost:8083/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI rutaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Microservicio Ruta API")
                        .description("Gestiona el listado de rutas, la consulta de horarios actualizados "
                                + "(obtenidos del microservicio Horario) y el ajuste del estado de las rutas.")
                        .version("1.0.0")
                        .contact(new Contact().name("Equipo Proyecto Ruta")));
    }
}
