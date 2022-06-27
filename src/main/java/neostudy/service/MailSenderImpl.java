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
    public void sendEmailWithAttachment(String to, String subject, String text, String[] pathToAttachment) {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper;
        File file1 = new File(pathToAttachment[0]);
        File file2 = new File(pathToAttachment[1]);
        File file3 = new File(pathToAttachment[2]);
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setFrom("superbank@internet.ru");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);
            helper.addAttachment(file1.getName(), file1);
            helper.addAttachment(file2.getName(), file2);
            helper.addAttachment(file3.getName(), file3);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        javaMailSender.send(message);
    }
}
