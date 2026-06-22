package gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
    title = "API Gateway - Microservicios de Transporte",
    version = "1.0",
    description = "Gateway que centraliza el acceso a los microservicios de Geolocalizacion, Prediccion, "
                + "Horario, Ruta y Monitoreo. "
                + "Geolocalizacion: /geo/** -> http://localhost:8080/api/ubicaciones | "
                + "Prediccion: /pred/** -> http://localhost:8081/api/predicciones | "
                + "Horario: /horario/** -> http://localhost:8082/api/horarios | "
                + "Ruta: /ruta/** -> http://localhost:8083/api/rutas | "
                + "Monitoreo (requiere JWT): /monitoreo/** -> http://localhost:8084/api/monitoreo, "
                + "login en /api/auth/login"
))
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
