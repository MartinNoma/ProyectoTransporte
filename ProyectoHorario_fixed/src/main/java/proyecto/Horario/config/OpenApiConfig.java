package proyecto.Horario.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion de Swagger / OpenAPI para el microservicio Horario.
 *
 * Documentacion disponible en:
 *  - Swagger UI: http://localhost:8082/swagger-ui.html
 *  - OpenAPI JSON: http://localhost:8082/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI horarioOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Microservicio Horario API")
                        .description("Gestiona la creacion, consulta, actualizacion y eliminacion de horarios, "
                                + "asi como la recepcion de alertas provenientes de otros microservicios "
                                + "(por ejemplo, Monitoreo) para reajustar los horarios automaticamente.")
                        .version("1.0.0")
                        .contact(new Contact().name("Equipo Proyecto Horario")));
    }
}
