package gateway.config;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;

import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    @Bean
    @Order(1)
    public GlobalFilter loggingFilter() {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest().mutate()
                .header("X-Forwarded-By", "api-gateway")
                .header("X-Request-Id", java.util.UUID.randomUUID().toString())
                .build();

            long startTime = System.currentTimeMillis();
            System.out.printf("[GATEWAY] %s %s%n",
                request.getMethod(),
                request.getURI());

            return chain.filter(exchange.mutate().request(request).build())
                .then(Mono.fromRunnable(() -> {
                    long elapsed = System.currentTimeMillis() - startTime;
                    System.out.printf("[GATEWAY] Respuesta en %dms - Status: %s%n",
                        elapsed,
                        exchange.getResponse().getStatusCode());
                }));
        };
    }
}
