package com.masache.masachetesis.service;


import com.masache.masachetesis.dto.JsonResponseDto;
import com.masache.masachetesis.models.PasswordRecovery;
import com.masache.masachetesis.models.RecoveryCodeResponseDto;
import com.masache.masachetesis.models.Usuario;
import com.masache.masachetesis.repositories.PasswordRecoveryRepository;
import com.masache.masachetesis.repositories.UsuariosRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecoveryServiceImpl {

    private final UsuariosRepository userRepository;
    private final PasswordRecoveryRepository recoveryRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private static final Random RANDOM_GENERATOR = new Random();
    private int otpExpirationMinutes = 5;

    public JsonResponseDto generateRecoveryCode(String correo) {
        try {
            log.info("Verificando el correo: {}", correo);
            Usuario user = userRepository.buscarPorCorreoYEstado(correo);
            if (user == null) {
                return new JsonResponseDto(false, HttpStatus.NOT_FOUND.value(), "Usuario no encontrado.", null,null);
            }
            user.getPasswordRecoveries().forEach(r -> r.setIsActive(false));
            String recoveryCode = String.format("%06d", RANDOM_GENERATOR.nextInt(999999));
            String encryptedRecoveryCode = passwordEncoder.encode(recoveryCode);
            LocalDateTime expirationDate = LocalDateTime.now().plusMinutes(otpExpirationMinutes);
            PasswordRecovery newRecovery = PasswordRecovery.builder()
                    .user(user)
                    .recoveryCode(encryptedRecoveryCode)
                    .recoveryExpirationDate(expirationDate)
                    .isActive(true)
                    .build();
            user.getPasswordRecoveries().add(newRecovery);
            recoveryRepository.save(newRecovery);
            mailService.sendRecoveryEmail(user, recoveryCode);
            return new JsonResponseDto(true,HttpStatus.OK.value(), "Código de recuperación generado con éxito.",
                    new RecoveryCodeResponseDto(recoveryCode, expirationDate), null);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error al generar el código de recuperación: {}", e.getMessage());
            return new JsonResponseDto(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error interno del servidor.", null, null);
        }
    }

    public JsonResponseDto verifyRecoveryCode(String correo, String code) {
        try {
            Usuario user = userRepository.buscarPorCorreoYEstado(correo);
            if (user == null || user.getPasswordRecoveries().isEmpty()) {
                return new JsonResponseDto(false, HttpStatus.NOT_FOUND.value(), "Código no encontrado o usuario no existe.", null,null);
            }

            // Buscar un código activo
            PasswordRecovery validRecovery = user.getPasswordRecoveries().stream()
                    .filter(r -> r.getIsActive() && passwordEncoder.matches(code, r.getRecoveryCode()))
                    .findFirst()
                    .orElse(null);

            if (validRecovery == null || validRecovery.getRecoveryExpirationDate().isBefore(LocalDateTime.now())) {
                return new JsonResponseDto(false, HttpStatus.BAD_REQUEST.value(), "Código inválido o expirado.", null,null);
            }

            return new JsonResponseDto(true, HttpStatus.OK.value(), "Código válido.", null,null);
        } catch (Exception e) {
            log.error("Error al verificar el código de recuperación: {}", e.getMessage());
            return new JsonResponseDto(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error interno del servidor.", null,null);
        }
    }

    public JsonResponseDto resetPassword(String correo, String code, String newPassword) {
        try {
            log.info("Verificando el correo: {}", correo);
            String correoLimpio = correo.trim().toLowerCase();
            log.info("Verificando el correo: {}", correoLimpio);
            Usuario user = userRepository.buscarPorCorreoYEstado(correo);

            if (user == null) {
                return new JsonResponseDto(false, HttpStatus.NOT_FOUND.value(), "Usuario no encontrado.", null,null);
            }
            log.info(user.getUsuario(), user.getCorreo());

            PasswordRecovery validRecovery = user.getPasswordRecoveries().stream()
                    .filter(r -> r.getIsActive() && passwordEncoder.matches(code, r.getRecoveryCode()))
                    .findFirst()
                    .orElse(null);

            if (validRecovery == null || validRecovery.getRecoveryExpirationDate().isBefore(LocalDateTime.now())) {
                return new JsonResponseDto(false, HttpStatus.BAD_REQUEST.value(), "Código inválido o expirado.", null,null);
            }

            // Actualizar contraseña
            user.setContrasena(passwordEncoder.encode(newPassword));
            // la contrasena es:
            log.info(newPassword);
            validRecovery.setIsActive(false);
            log.info("Pase el recupertar contrasena");
            userRepository.save(user);
            log.info("Guarde el usuario");

            return new JsonResponseDto(true, HttpStatus.OK.value(), "Contraseña restablecida con éxito.", null,null);
        } catch (Exception e) {
            log.error("Error al restablecer la contraseña: {}", e.getMessage());
            return new JsonResponseDto(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error interno del servidor.", null,null);
        }
    }
}

