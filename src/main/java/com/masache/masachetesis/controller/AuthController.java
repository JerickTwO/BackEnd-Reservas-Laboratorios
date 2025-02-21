package com.masache.masachetesis.controller;

import com.masache.masachetesis.dto.*;
import com.masache.masachetesis.models.Administrador;
import com.masache.masachetesis.models.Docente;
import com.masache.masachetesis.models.Usuario;
import com.masache.masachetesis.repositories.AdministradorRepository;
import com.masache.masachetesis.repositories.DocenteRepository;
import com.masache.masachetesis.repositories.UsuariosRepository;
import com.masache.masachetesis.security.jwt.JwtProvider;
import com.masache.masachetesis.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtProvider jwtProvider;
    private final UsuariosRepository usuariosRepository;
    private final AdministradorRepository administradorRepository;
    private final DocenteRepository docenteRepository;

    @GetMapping("/listado")
    public List<UsuarioTablaDto> obtenerListaUnificada() {
        List<UsuarioTablaDto> listaUnificada = new ArrayList<>();

        // 1. Tomar todos los administradores
        List<Administrador> administradores = administradorRepository.findAll();
        for (Administrador admin : administradores) {
            UsuarioTablaDto dto = new UsuarioTablaDto();
            dto.setId(admin.getIdAdministrador());
            dto.setNombreCompleto(admin.getNombreAdministrador() + " " + admin.getApellidoAdministrador());
            dto.setCorreo(admin.getCorreoAdministrador());
            dto.setIdInstitucional(admin.getIdInstitucional());
            dto.setDepartamento(null); // Los administradores no tienen depto
            dto.setTipoUsuario("ADMINISTRADOR");
            listaUnificada.add(dto);
        }

        // 2. Tomar todos los docentes
        List<Docente> docentes = docenteRepository.findAll();
        for (Docente doc : docentes) {
            UsuarioTablaDto dto = new UsuarioTablaDto();
            dto.setId(doc.getIdDocente());
            dto.setNombreCompleto(doc.getNombreDocente() + " " + doc.getApellidoDocente());
            dto.setCorreo(doc.getCorreoDocente());
            dto.setIdInstitucional(doc.getIdInstitucional());
            dto.setDepartamento(
                    doc.getDepartamento() != null ? doc.getDepartamento().getNombreDepartamento() : null
            );
            dto.setTipoUsuario("DOCENTE");
            listaUnificada.add(dto);
        }
        return listaUnificada;
    }

    @GetMapping("/active-users")
    public ResponseEntity<JsonResponseDto> getAllActiveUsers() {
        try {
            List<Usuario> activeUsers = authService.getAllActiveUsers();
            JsonResponseDto response = new JsonResponseDto(true, 200, "Usuarios activos obtenidos correctamente", activeUsers, null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al obtener usuarios activos {}", e.getMessage());
            JsonResponseDto response = new JsonResponseDto(false, 500, "Error al obtener usuarios activos", null, null);
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<JsonResponseDto> login(@RequestBody LoginDto loginRequest) {
        log.info("Login request received: {}", loginRequest.getUsername());
        try {
            // Autenticar al usuario
            JsonResponseDto response = authService.authenticate(loginRequest);

            // Verificar si el login fue exitoso
            if (response.isRespuesta()) {
                // Obtener detalles del usuario desde el token
                String token = (String) response.getResultado();
                String username = jwtProvider.getNombreUsuarioFromToken(token);
                Usuario user = usuariosRepository.findUsuarioByUsuarioAndEstadoTrue(username);

                // Verificar si es el primer login
                if (user.isPrimerLogin()) {
                    // Incluir el token en la respuesta para que el frontend lo guarde
                    JsonResponseDto redirectionResponse = new JsonResponseDto(
                            true,
                            HttpStatus.OK.value(),
                            "Primer login, redirigiendo a cambio de contraseña.",
                            token,
                            "/actualizar-contrasena"
                    );
                    return ResponseEntity.status(HttpStatus.OK).body(redirectionResponse);
                }

                // Si no es el primer login, proceder normalmente
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

        } catch (Exception e) {
            log.error("Error en la autenticación: {}", e.getMessage());
            JsonResponseDto response = new JsonResponseDto(
                    false,
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error interno en la autenticación",
                    null,
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PostMapping("/register")
    public ResponseEntity<JsonResponseDto> register(@RequestBody RegisterDto registerRequest) {
        log.info("Register request received for user: {}", registerRequest.getUsername());
        try {
            JsonResponseDto response = authService.registerUser(registerRequest);
            return ResponseEntity.status(response.getCodigoHttp()).body(response);
        } catch (Exception e) {
            log.error("Error en el registro: {}", e.getMessage());
            JsonResponseDto response = new JsonResponseDto(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error en el registro de usuario", null, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/userDetails")
    public ResponseEntity<JsonResponseDto> userDetails(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = authorizationHeader.replace("Bearer ", "");
            JsonResponseDto response = authService.getUserDetails(token);
            return ResponseEntity.status(response.getCodigoHttp()).body(response);
        } catch (Exception e) {
            log.error("Error en la obtención de detalles del usuario: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new JsonResponseDto(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error al procesar la solicitud", null, null));
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<JsonResponseDto> logout(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.replace("Bearer ", "");
            JsonResponseDto response = authService.revokeToken(token);
            return ResponseEntity.status(response.getCodigoHttp()).body(response);
        }
        return ResponseEntity.badRequest().body(new JsonResponseDto(false, 400, "Encabezado Authorization no encontrado o malformado.", null, null));
    }

    @PostMapping("/disable-first-login")
    public ResponseEntity<JsonResponseDto> disableFirstLogin(
            @RequestHeader("Authorization") String authorizationHeader) {
        log.info("Disabling first login for user");
        try {
            String token = authorizationHeader.replace("Bearer ", "");
            JsonResponseDto response = authService.disableFirstLogin(token);
            return ResponseEntity.status(response.getCodigoHttp()).body(response);
        } catch (Exception e) {
            log.error("Error al deshabilitar primer inicio de sesión: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new JsonResponseDto(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error al procesar la solicitud", null));
        }
    }
    @PutMapping("/update-password")
    public ResponseEntity<JsonResponseDto> updatePassword(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody ProfilePasswordDto profilePasswordDto) {
        try {
            String token = authorizationHeader.replace("Bearer ", "");
            JsonResponseDto response = authService.updatePassword(token, profilePasswordDto);
            log.info(String.valueOf(response));
            if (response.isRespuesta()) {
                // Deshabilitar primer login si el cambio de contraseña fue exitoso
                authService.disableFirstLogin(token);
                return ResponseEntity.ok(new JsonResponseDto(true, HttpStatus.OK.value(), "Contraseña actualizada con éxito", null, null));
            } else {
                // Verificar que el código HTTP sea válido, si no, asignar 500
                int codigoHttp = response.getCodigoHttp() > 0 ? response.getCodigoHttp() : HttpStatus.INTERNAL_SERVER_ERROR.value();
                // Agregar detalles del error
                return ResponseEntity.status(codigoHttp)
                        .body(new JsonResponseDto(false, codigoHttp, response.getMensaje(), response.getResultado(), response.getDetalleError()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error en el controlador al actualizar la contraseña: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new JsonResponseDto(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error al actualizar la contraseña.", null, e.getMessage()));
        }
    }




}
