package neostudy.service;

public interface MailSender {

    void sendEmail(String to, String subject, String text);
    void sendEmailWithAttachment(String to, String subject, String text, String[] pathToAttachment);

}
