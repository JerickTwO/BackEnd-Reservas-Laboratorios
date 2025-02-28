package com.masache.masachetesis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyRecoveryCodeRequestDto {
    private String correo;
    private String code;
    private String newPassword;
}
