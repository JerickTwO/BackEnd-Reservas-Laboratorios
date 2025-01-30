package com.masache.masachetesis.service;

import com.masache.masachetesis.models.Usuario;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${otp.expiration.minutes}")
    private int otpExpirationMinutes;

    public void sendRecoveryEmail(Usuario user, String otp) {
        try {
            // upload the email template
            InputStream inputStream = new ClassPathResource("templates/recovery_template.html").getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder emailContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                emailContent.append(line).append("\n");
            }

            // prevent null values
            String username = user.getUsuario() != null ? user.getUsuario() : "Usuario";
            String formattedOtp = otp != null ? otp : "N/A";
            String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));


            String emailBody = emailContent.toString()
                    .replace("{{username}}", username)
                    .replace("{{otp_code}}", formattedOtp)
                    .replace("{{otp_expiration_minutes}}", String.valueOf(otpExpirationMinutes))
                    .replace("{{current_date_time}}", currentDate);

            // config the email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(user.getCorreo());
            helper.setSubject("Security Monitoring - Código de Recuperación");
            helper.setText(emailBody, true);

            mailSender.send(message);
            log.info("Correo enviado correctamente a: {}", user.getCorreo());

        } catch (Exception e) {
            log.error("Error al enviar el correo de recuperación: {}", e.getMessage(), e);
            throw new IllegalStateException("No se pudo enviar el correo de recuperación.", e);
        }
    }
}
