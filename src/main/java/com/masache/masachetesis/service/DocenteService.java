package com.masache.masachetesis.service;

import com.masache.masachetesis.models.Docente;
import com.masache.masachetesis.models.Roles;
import com.masache.masachetesis.models.Usuario;
import com.masache.masachetesis.repositories.DocenteRepository;
import com.masache.masachetesis.repositories.UsuariosRepository;
import com.masache.masachetesis.utils.GenerarPassword;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DocenteService {
    private final DocenteRepository docenteRepository;
    private final UsuariosRepository usuariosRepository;
    private final RolesService rolesService;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private static final Logger logger = LoggerFactory.getLogger(DocenteService.class);

    public List<Docente> getAllDocentesWithDepartamento() {
        return docenteRepository.findAllWithDepartamento();
    }

    public Optional<Docente> getDocenteById(Long id) {
        return docenteRepository.findById(id);
    }

    public Docente saveOrUpdateDocente(@Valid Docente docente) {
        try {
            if (docente.getIdDocente() != null && docente.getIdDocente() == 0) {
                docente.setIdDocente(null);
            }

            boolean nuevoRegistro = (docente.getIdDocente() == null);
            Docente savedDocente = docenteRepository.save(docente);

            if (nuevoRegistro) {
                crearUsuarioParaDocente(savedDocente);
            }

            return savedDocente;
        } catch (Exception e) {
            System.err.println("Error al guardar el docente: " + e.getMessage());
            throw new RuntimeException("Error al registrar docente.", e);
        }
    }

    @Transactional
    protected void crearUsuarioParaDocente(Docente docente) {
        try {
            if (usuariosRepository.existsByUsuario(docente.getCorreoDocente())) {
                throw new IllegalStateException("Ya existe un usuario con este correo.");
            }
            if (docenteRepository.existsByCorreoDocente(docente.getCorreoDocente())) {
                throw new IllegalStateException("Ya existe un docente con este correo.");
            }
            if (docenteRepository.existsByIdInstitucional(docente.getIdInstitucional())) {
                throw new IllegalStateException("Ya existe un docente con este DNI.");
            }

            String passwordGenerada = GenerarPassword.generarPasswordAleatoria(12);
            String passwordCodificada = passwordEncoder.encode(passwordGenerada);

            Roles rolDocente = rolesService.getRolById(2L)
                    .orElseThrow(() -> new IllegalArgumentException("Rol de Docente no encontrado"));

            Usuario usuario = Usuario.builder()
                    .usuario(docente.getCorreoDocente())
                    .nombre(docente.getNombreDocente())
                    .apellido(docente.getApellidoDocente())
                    .correo(docente.getCorreoDocente())
                    .contrasena(passwordCodificada)
                    .primerLogin(true)
                    .estado(true)
                    .rol(rolDocente)
                    .build();
            usuariosRepository.save(usuario);
            mailService.sendNewUserEmail(usuario, passwordGenerada);

        } catch (Exception e) {
            System.err.println("Error al crear usuario para el docente: " + e.getMessage());
            throw new RuntimeException("Error en la creación del usuario.", e);
        }
    }


    public void deleteDocente(Long id) {
        try {
            if (!docenteRepository.existsById(id)) {
                throw new IllegalStateException("El docente con ID " + id + " no existe.");
            }
            docenteRepository.deleteById(id);
        } catch (Exception e) {
            System.err.println("Error al eliminar el docente: " + e.getMessage());
            throw new RuntimeException("Error al eliminar docente.", e);
        }
    }
}
