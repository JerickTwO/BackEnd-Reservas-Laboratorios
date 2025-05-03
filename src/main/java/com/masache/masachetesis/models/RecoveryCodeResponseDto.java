package com.masache.masachetesis.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RecoveryCodeResponseDto {
    private String recoveryCode;
    private LocalDateTime expirationDate;
}
