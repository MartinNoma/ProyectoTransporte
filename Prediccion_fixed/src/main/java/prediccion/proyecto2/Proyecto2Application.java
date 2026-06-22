package prediccion.proyecto2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
    title = "Microservicio de Prediccion",
    version = "1.0",
    description = "API REST para gestionar predicciones de llegada de unidades de transporte"
))
public class Proyecto2Application {

    public static void main(String[] args) {
        SpringApplication.run(Proyecto2Application.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
