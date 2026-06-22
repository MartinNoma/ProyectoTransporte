package proyecto.Monitoreo.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("Generar token valido para un usuario")
    void generarToken_esValido() {
        String token = jwtUtil.generateToken("admin");
        assertThat(token).isNotBlank();
        assertThat(jwtUtil.validateToken(token)).isTrue();
        assertThat(jwtUtil.extractUsername(token)).isEqualTo("admin");
    }

    @Test
    @DisplayName("Token manipulado no debe ser valido")
    void tokenManipulado_noEsValido() {
        String token = jwtUtil.generateToken("admin") + "manipulado";
        assertThat(jwtUtil.validateToken(token)).isFalse();
    }

    @Test
    @DisplayName("Token vacio no debe ser valido")
    void tokenVacio_noEsValido() {
        assertThat(jwtUtil.validateToken("")).isFalse();
    }

    @Test
    @DisplayName("Token con formato invalido no debe ser valido")
    void tokenFormatoInvalido_noEsValido() {
        assertThat(jwtUtil.validateToken("esto-no-es-un-jwt")).isFalse();
    }
}
