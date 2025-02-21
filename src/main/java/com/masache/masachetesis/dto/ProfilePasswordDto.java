package com.masache.masachetesis.dto;

import lombok.Data;

@Data
public class ProfilePasswordDto {
    private String currentPassword; // generada
    private String newPassword; // nueva
}

