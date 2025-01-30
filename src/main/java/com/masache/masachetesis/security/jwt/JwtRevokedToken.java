package com.masache.masachetesis.security.jwt;


import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class JwtRevokedToken {

    private final Map<String, LocalDateTime> revokedTokens = new ConcurrentHashMap<>();

    public void revokeToken(String token) {
        revokedTokens.put(token, LocalDateTime.now());
        log.debug("Token agregado a la lista de revocados: {}", token);
    }

    public boolean isTokenRevoked(String token) {
        return revokedTokens.containsKey(token);
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void cleanExpiredTokens() {
        revokedTokens.entrySet().removeIf(entry ->
                entry.getValue().isBefore(LocalDateTime.now().minusHours(3)));
        log.info("Tokens expirados eliminados. Tokens restantes: {}", revokedTokens.size());
    }

}
