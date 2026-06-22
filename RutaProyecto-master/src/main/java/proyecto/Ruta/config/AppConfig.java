package proyecto.Ruta.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * RutaService depende de un RestTemplate inyectado por @Autowired, pero no
 * existia ningun bean que lo registrara en el contexto de Spring: la
 * aplicacion fallaba al iniciar con NoSuchBeanDefinitionException.
 * Este bean corrige ese problema.
 */
@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
