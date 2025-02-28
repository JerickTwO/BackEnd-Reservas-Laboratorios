package com.masache.masachetesis.controller;

import com.masache.masachetesis.dto.JsonResponseDto;
import com.masache.masachetesis.dto.VerifyRecoveryCodeRequestDto;
import com.masache.masachetesis.models.Usuario;
import com.masache.masachetesis.service.RecoveryServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Recuperación de contraseña", description = "Endpoints para la recuperación de cuentas de usuario")

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/recuperar")
public class RecoveryController {

    private final RecoveryServiceImpl recoveryService;

    @PostMapping("/recovery-password")
    public ResponseEntity<JsonResponseDto> generateRecoveryCode(@RequestBody Usuario userInfo) {
        try {
            JsonResponseDto response = recoveryService.generateRecoveryCode(userInfo.getCorreo());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("Error al generar código de recuperación: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new JsonResponseDto(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error al generar el código de recuperación.",null, null));
        }
    }


    @PostMapping("/verify-code")
    public ResponseEntity<JsonResponseDto> verifyRecoveryCode(@RequestBody VerifyRecoveryCodeRequestDto requestDto) {
        try {
            String email = requestDto.getCorreo();
            String code = requestDto.getCode();
            JsonResponseDto response = recoveryService.verifyRecoveryCode(email, code);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("Error al verificar el código de recuperación: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new JsonResponseDto(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error al verificar el código de recuperación.",null, null));
        }
    }
    @PostMapping("/reset-password")
    public ResponseEntity<JsonResponseDto> resetPassword(@RequestBody VerifyRecoveryCodeRequestDto requestDto) {
        try {
            String email = requestDto.getCorreo();
            String code = requestDto.getCode();
            String newPassword = requestDto.getNewPassword();
            JsonResponseDto response = recoveryService.resetPassword(email, code, newPassword);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("Error al restablecer la contraseña: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new JsonResponseDto(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error al restablecer la contraseña.",null, null));
        }
    }

}