package geolocalizacion.proyect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
    title = "Microservicio de Geolocalizacion",
    version = "1.0",
    description = "API REST para registrar y consultar ubicaciones de unidades de transporte"
))
public class ProyectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProyectApplication.class, args);
    }
}
