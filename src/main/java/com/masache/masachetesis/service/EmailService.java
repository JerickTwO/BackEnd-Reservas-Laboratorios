package com.masache.masachetesis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    /**
     * Enviar un correo electrónico simple.
     *
     * @param to      Dirección de correo del destinatario.
     * @param subject Asunto del correo.
     * @param text    Contenido del correo.
     */
    public void enviarCorreo(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            message.setFrom("fjmasache@espe.edu.ec"); // Cambia esto por tu dirección de correo.

            mailSender.send(message);
            logger.info("Correo enviado exitosamente a: {}", to);
        } catch (MailException e) {
            logger.error("Error al enviar correo a: {}. Detalles: {}", to, e.getMessage());
            throw new RuntimeException("No se pudo enviar el correo. Por favor, intente nuevamente.", e);
        }
    }
}
