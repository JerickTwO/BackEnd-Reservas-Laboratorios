package com.masache.masachetesis.service;

import com.masache.masachetesis.dto.JsonResponseDto;
import com.masache.masachetesis.dto.LoginDto;
import com.masache.masachetesis.dto.ProfilePasswordDto;
import com.masache.masachetesis.dto.RegisterDto;
import com.masache.masachetesis.models.Roles;
import com.masache.masachetesis.models.Usuario;
import com.masache.masachetesis.repositories.AdministradorRepository;
import com.masache.masachetesis.repositories.DocenteRepository;
import com.masache.masachetesis.repositories.UsuariosRepository;
import com.masache.masachetesis.security.jwt.JwtProvider;
import com.masache.masachetesis.security.jwt.JwtRevokedToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UsuariosRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtProvider jwtProvider;
    private final JwtRevokedToken jwtRevokedToken;
    private final RolesService rolesService;
    private final UsuariosRepository usuariosRepository;

    public List<Usuario> getAllActiveUsers() {
        log.info("Obteniendo todos los usuarios activos");
        return userRepository.findUsuariosByEstadoTrue();
    }

    // Authenticates a user and returns a JWT token
    public JsonResponseDto authenticate(LoginDto login) {
        try {
            String username = login.getUsername() != null ? login.getUsername().trim() : "";
            String pass = login.getPassword() != null ? login.getPassword().trim() : "";
            // Validate that username and password are not empty
            if (username.isEmpty() || pass.isEmpty()) {
                return new JsonResponseDto(false, HttpStatus.BAD_REQUEST.value(), "Usuario y contraseña son requeridos.", null, null);
            }

            // Search for the user in the database
            Usuario user = userRepository.findUsuarioByUsuarioAndEstadoTrue(username);
            if (user == null) {
                return new JsonResponseDto(false, HttpStatus.BAD_REQUEST.value(), "Usuario no encontrado.", null, null);
            }

            // Check if the password matches
            if (!encoder.matches(pass, user.getContrasena())) {
                return new JsonResponseDto(false, HttpStatus.BAD_REQUEST.value(), "Contraseña incorrecta.", null, null);
            }
            // Generate a JWT token for the user
            String jwt = jwtProvider.generateJwtByUsername(user);
            return new JsonResponseDto(true, HttpStatus.OK.value(), "Login exitoso", jwt, null);
        } catch (Exception e) {
            log.info("Error en el login: {}", e.getMessage());
            return new JsonResponseDto(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error en el login", null, null);
        }
    }

    // Retrieves user details from the token
    public JsonResponseDto getUserDetails(String token) {
        try {
            // Extract the username from the token
            String username = jwtProvider.getNombreUsuarioFromToken(token);

            // Fetch the user from the database
            Usuario user = userRepository.findUsuarioByUsuarioAndEstadoTrue(username);

            if (user == null) {
                return new JsonResponseDto(false, HttpStatus.NOT_FOUND.value(), "Usuario no encontrado.", null, null);
            }

            // Return the user's details
            return new JsonResponseDto(true, HttpStatus.OK.value(), "Detalles del usuario obtenidos con éxito", user, null);

        } catch (Exception e) {
            log.error("Error al obtener detalles del usuario: {}", e.getMessage());
            return new JsonResponseDto(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error al obtener detalles del usuario", null, null);
        }
    }

    // Revokes a specific token by adding it to the revoked token list
    public JsonResponseDto revokeToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                return new JsonResponseDto(false, HttpStatus.BAD_REQUEST.value(), "Token no proporcionado.", null, null);
            }

            jwtRevokedToken.revokeToken(token);
            log.info("Token revocado: {}", token);

            return new JsonResponseDto(true, HttpStatus.OK.value(), "Token revocado con éxito.", null, null);
        } catch (Exception e) {
            log.error("Error al revocar el token: {}", e.getMessage());
            return new JsonResponseDto(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error al revocar el token.", null, null);
        }
    }

    public JsonResponseDto registerUser(RegisterDto registerRequest) {
        try {
            String username = registerRequest.getUsername().trim();
            String password = registerRequest.getPassword().trim();
            String nombre = registerRequest.getNombreUsuario().trim();
            String apellido = registerRequest.getApellidoUsuario().trim();
            String correo = registerRequest.getCorreoUsuario().trim();
            Long idRole = registerRequest.getRolId();

            if (username.isEmpty() || password.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty() || idRole == null) {
                return new JsonResponseDto(false, HttpStatus.BAD_REQUEST.value(), "Todos los campos son requeridos.", null, null);
            }

            if (userRepository.existsByUsuario(username)) {
                return new JsonResponseDto(false, HttpStatus.BAD_REQUEST.value(), "El usuario ya está registrado.", null, null);
            }

            // Crear y guardar el nuevo usuario
            Usuario newUser = new Usuario();
            newUser.setUsuario(username);
            newUser.setContrasena(encoder.encode(password));
            newUser.setNombre(nombre);
            newUser.setApellido(apellido);
            newUser.setCorreo(correo);
            newUser.setEstado(true);
            newUser.setRol(rolesService.getRolById(2L).orElseThrow(() -> new IllegalArgumentException("Rol no encontrado")));

            userRepository.save(newUser);

            return new JsonResponseDto(true, HttpStatus.CREATED.value(), "Usuario registrado exitosamente.", null, null);
        } catch (Exception e) {
            log.error("Error en el registro de usuario: {}", e.getMessage());
            return new JsonResponseDto(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error en el registro de usuario.", null, null);
        }
    }

    public JsonResponseDto disableFirstLogin(String token) {
        try {
            // Obtener el nombre de usuario desde el token
            String username = jwtProvider.getNombreUsuarioFromToken(token);
            Usuario user = usuariosRepository.findByUsuarioAndAndEstadoIsTrue(username);

            if (user == null) {
                return new JsonResponseDto(false, HttpStatus.NOT_FOUND.value(), "Usuario no encontrado", null);
            }

            // Verificar si el usuario ya ha deshabilitado el primer login
            if (!user.isPrimerLogin()) {
                return new JsonResponseDto(false, HttpStatus.BAD_REQUEST.value(), "El usuario ya ha iniciado sesión antes", null);
            }

            // Cambiar el estado de firstLogin a false
            user.setPrimerLogin(false);
            usuariosRepository.save(user);

            return new JsonResponseDto(true, HttpStatus.OK.value(), "Primer inicio de sesión deshabilitado con éxito", null);
        } catch (Exception e) {
            log.error("Error al deshabilitar primer inicio de sesión: {}", e.getMessage());
            return new JsonResponseDto(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error al procesar la solicitud", null);
        }
    }

    public JsonResponseDto updatePassword(String token, ProfilePasswordDto profilePasswordDto) {
        try {
            // Extraer el username del token
            String username = jwtProvider.getNombreUsuarioFromToken(token);
            log.info("Usuario extraído del token: {}", username);

            // Buscar al usuario por username
            Usuario user = usuariosRepository.findByUsuarioAndAndEstadoIsTrue(username);
            if (user == null) {
                log.error("Usuario no encontrado: {}", username);
                return new JsonResponseDto(false, HttpStatus.NOT_FOUND.value(), "Usuario no encontrado.", null, "No se encontró un usuario con el nombre de usuario proporcionado.");
            }

            // Validar que la contraseña actual proporcionada coincida con la almacenada
            log.info("Validando contraseña actual...");
            if (!encoder.matches(profilePasswordDto.getCurrentPassword(), user.getContrasena())) {
                log.error("La contraseña actual es incorrecta.");
                return new JsonResponseDto(false, HttpStatus.UNAUTHORIZED.value(), "La contraseña actual es incorrecta.", null, "La contraseña proporcionada no coincide con la actual.");
            }

            // Validar que la nueva contraseña cumpla las reglas de seguridad
            if (profilePasswordDto.getNewPassword().length() < 8) {
                log.error("La nueva contraseña no cumple con las reglas de seguridad.");
                return new JsonResponseDto(false, HttpStatus.BAD_REQUEST.value(), "La nueva contraseña debe tener al menos 8 caracteres.", null, "La nueva contraseña es demasiado corta.");
            }

            // Validar que la nueva contraseña sea diferente a la actual
            if (encoder.matches(profilePasswordDto.getNewPassword(), user.getContrasena())) {
                log.error("La nueva contraseña no puede ser igual a la actual.");
                return new JsonResponseDto(false, HttpStatus.BAD_REQUEST.value(), "La nueva contraseña no puede ser igual a la actual.", null, "La nueva contraseña coincide con la contraseña actual.");
            }

            // Actualizar la contraseña en el modelo
            log.info("Actualizando contraseña...");
            user.setContrasena(encoder.encode(profilePasswordDto.getNewPassword()));

            // Guardar los cambios en la base de datos
            log.info("Guardando nueva contraseña...");
            usuariosRepository.save(user);

            // Devolver respuesta exitosa
            return new JsonResponseDto(true, HttpStatus.OK.value(), "Contraseña actualizada con éxito.", null, null);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error al actualizar la contraseña: {}", e.getMessage());
            return new JsonResponseDto(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error al actualizar la contraseña.", null, e.getMessage());
        }
    }

}
