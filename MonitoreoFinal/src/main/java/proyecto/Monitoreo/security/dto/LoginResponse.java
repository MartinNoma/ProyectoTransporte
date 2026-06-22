package proyecto.Monitoreo.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta con token JWT")
public class LoginResponse {

    @Schema(example = "eyJhbGci...")
    private String token;
    private String tipo;
    private String usuario;

    public LoginResponse(String token, String tipo, String usuario) {
        this.token = token;
        this.tipo = tipo;
        this.usuario = usuario;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
}
