package com.masache.masachetesis.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JsonResponseDto {
    private boolean respuesta;
    private int codigoHttp;
    private String mensaje;
    private Object resultado;
    private String detalleError;

    public JsonResponseDto(boolean b, int value, String mensaje, Object resultado) {
    }
}
