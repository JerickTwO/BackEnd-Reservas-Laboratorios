package com.masache.masachetesis.service;

import com.masache.masachetesis.models.Administrador;
import com.masache.masachetesis.models.Roles;
import com.masache.masachetesis.models.Usuario;
import com.masache.masachetesis.repositories.AdministradorRepository;
import com.masache.masachetesis.repositories.UsuariosRepository;
import com.masache.masachetesis.utils.GenerarPassword;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AdministradorService {
    private final AdministradorRepository administradorRepository;
    private final UsuariosRepository usuariosRepository;
    private final RolesService rolesService;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private static final Logger logger = LoggerFactory.getLogger(AdministradorService.class);

    public List<Administrador> getAllAdministradores() {
        return administradorRepository.findAll();
    }

    public Optional<Administrador> getAdministradorById(Long id) {
        return administradorRepository.findById(id);
    }

    @Transactional
    public Administrador saveOrUpdateAdministrador(@Valid Administrador administrador) {

        if (administrador.getIdAdministrador() != null && administrador.getIdAdministrador() == 0) {
            administrador.setIdAdministrador(null);
        }
        boolean nuevoRegistro = administrador.getIdAdministrador() == null;
        Administrador savedAdministrador = administradorRepository.save(administrador);

        if (nuevoRegistro) {
            crearUsuarioParaAdministrador(savedAdministrador);
        }

        return savedAdministrador;
    }

    @Transactional
    protected void crearUsuarioParaAdministrador(Administrador administrador) {
        try {
            if (usuariosRepository.existsByUsuario(administrador.getCorreoAdministrador())) {
                throw new IllegalStateException("Ya existe un usuario con este correo.");
            }
            String passwordGenerada = GenerarPassword.generarPasswordAleatoria(12);
            String passwordCodificada = passwordEncoder.encode(passwordGenerada);

            Roles rolAdministrador = rolesService.getRolById(1L)
                    .orElseThrow(() -> new IllegalArgumentException("Rol de Administrador no encontrado"));

            Usuario usuario = Usuario.builder()
                    .usuario(administrador.getCorreoAdministrador())
                    .nombre(administrador.getNombreAdministrador())
                    .apellido(administrador.getApellidoAdministrador())
                    .correo(administrador.getCorreoAdministrador())
                    .contrasena(passwordCodificada)
                    .primerLogin(true)
                    .estado(true)
                    .rol(rolAdministrador)
                    .build();
            usuariosRepository.save(usuario);
            mailService.sendNewUserEmail(usuario, passwordGenerada);

        } catch (Exception e) {
            throw new RuntimeException("Error en la creación del usuario.", e);
        }
    }

    public void deleteAdministrador(Long id) {
        if (!administradorRepository.existsById(id)) {
            throw new IllegalStateException("El administrador con ID " + id + " no existe.");
        }
        administradorRepository.deleteById(id);
    }
}
