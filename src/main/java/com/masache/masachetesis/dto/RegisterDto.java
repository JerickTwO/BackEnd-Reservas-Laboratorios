package com.masache.masachetesis.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDto {
    private String username;
    private String password;
    private String nombreUsuario;
    private String apellidoUsuario;
    private String correoUsuario;
    private Long rolId;
}
