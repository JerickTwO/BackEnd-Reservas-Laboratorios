package com.masache.masachetesis.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.masache.masachetesis.dto.JsonResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
@Slf4j
public class JwtEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException e)
            throws IOException {
        JsonResponseDto resp = new JsonResponseDto(Boolean.FALSE, HttpStatus.UNAUTHORIZED.value(), "Unauthorized", null, null);
        res.setContentType("application/json");
        res.setStatus(HttpStatus.UNAUTHORIZED.value());
        res.getWriter().write(new ObjectMapper().writeValueAsString(resp));
        res.getWriter().flush();
        res.getWriter().close();
    }

}
