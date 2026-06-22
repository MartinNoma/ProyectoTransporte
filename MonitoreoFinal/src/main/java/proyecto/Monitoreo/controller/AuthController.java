package proyecto.Monitoreo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import proyecto.Monitoreo.security.JwtUtil;
import proyecto.Monitoreo.security.dto.LoginRequest;
import proyecto.Monitoreo.security.dto.LoginResponse;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "1. Autenticacion", description = "Login para obtener token JWT")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    // Credenciales de demo en texto plano
    private static final String DEMO_USER = "admin";
    private static final String DEMO_PASS = "admin123";

    @Operation(summary = "Login", description = "Credenciales: admin / admin123")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        if (!DEMO_USER.equals(request.getUsername()) ||
            !DEMO_PASS.equals(request.getPassword())) {
            return ResponseEntity.status(401).body("Usuario o contrasena incorrectos");
        }
        String token = jwtUtil.generateToken(request.getUsername());
        return ResponseEntity.ok(new LoginResponse(token, "Bearer", request.getUsername()));
    }
}
