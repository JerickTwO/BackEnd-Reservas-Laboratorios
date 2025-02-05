    package com.masache.masachetesis.service;

    import com.masache.masachetesis.dto.JsonResponseDto;
    import com.masache.masachetesis.dto.LoginDto;
    import com.masache.masachetesis.dto.RegisterDto;
    import com.masache.masachetesis.models.Roles;
    import com.masache.masachetesis.models.Usuario;
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

                // Validar que los campos obligatorios no estén vacíos
                if (username.isEmpty() || password.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty() || idRole == null) {
                    return new JsonResponseDto(false, HttpStatus.BAD_REQUEST.value(), "Todos los campos son requeridos.", null, null);
                }

                // Verificar si el usuario ya existe
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


    }
