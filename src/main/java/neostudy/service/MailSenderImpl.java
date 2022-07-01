package neostudy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
@RequiredArgsConstructor
public class MailSenderImpl implements MailSender {

    private final JavaMailSender javaMailSender;
    private final DocsGenerationService docsGenerationService;

    @Override
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("superbank@internet.ru");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }

    @Override
    public void sendEmailWithAttachment(String to, String subject, String text, Long id) {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper;

        File file1 = docsGenerationService.generateLoanAppFile(id);
        File file2 = docsGenerationService.generateLoanContractFile(id);
        File file3 = docsGenerationService.generateLoanPaymentFile(id);
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setFrom("superbank@internet.ru");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);
            helper.addAttachment(file1.getName(), file1);
            helper.addAttachment(file2.getName(), file2);
            helper.addAttachment(file3.getName(), file3);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        } finally {
            file1.delete();
            file2.delete();
            file3.delete();
        }
    }
}
