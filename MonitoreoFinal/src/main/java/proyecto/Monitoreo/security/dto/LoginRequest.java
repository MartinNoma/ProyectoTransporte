package proyecto.Monitoreo.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credenciales de login")
public class LoginRequest {

    @NotBlank(message = "username es obligatorio")
    @Schema(example = "admin")
    private String username;

    @NotBlank(message = "password es obligatorio")
    @Schema(example = "admin123")
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
