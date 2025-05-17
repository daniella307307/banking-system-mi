package com.daniella.bms.email;

import com.daniella.bms.enums.IEmailTemplate;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendEmail(String to, String username, String subject, IEmailTemplate emailTemplate, Map<String,Object> variables ) throws MessagingException{
        MimeMessage mimeMessage=mailSender.createMimeMessage();
        MimeMessageHelper helper=new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                StandardCharsets.UTF_8.name()
        );
        if(variables == null){
            variables=new HashMap<>();
        }
        variables.put("username",username);
        variables.put("supportEmail","daniellaganza30@gmail.com");
        variables.put("currentYear", LocalDate.now().getYear());
        Context context=new Context();
        context.setVariables(variables);
        String htmlContent = templateEngine.process(emailTemplate.getName(),context);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent,true);
        mailSender.send(mimeMessage);
    }
}
