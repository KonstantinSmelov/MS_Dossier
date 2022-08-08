package neostudy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import neostudy.dto.EmailMessage;
import neostudy.feignclient.DealClient;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaListeners {

    private final ObjectMapper objectMapper;
    private final MailSender mailSender;
    private final DealClient dealClient;

    @KafkaListener(topics = {"finish-registration", "create-documents", "send-documents",
            "send-ses", "credit-issued", "application-denied"}, groupId = "groupId")
    void listener(String str) throws JsonProcessingException {
        log.debug("listener(): приняли из кафки: {}", str);

        EmailMessage emailMessage = objectMapper.readValue(str, EmailMessage.class);
        log.debug("listener(): создали EmailMessage: {}", emailMessage);

        StringBuilder body;

        switch (emailMessage.getTheme()) {
            case FINISH_REGISTRATION:
                body = new StringBuilder("Заявка номер " + emailMessage.getApplicationId() + " одобрена!\n");
                body.append("Для продолжения оформления пройдите по ссылке: ");
                body.append("http://localhost:8090/swagger-ui/#/gateway-controller/choosingApplicationUsingPUT");
                mailSender.sendEmail(emailMessage.getAddress(), "Finish registration", body.toString());
                break;

            case CREATE_DOCUMENTS:
                body = new StringBuilder("Заявка номер " + emailMessage.getApplicationId() + " прошла проверку!\n");
                body.append("Для продолжения пройдите по ссылке для формирования пакета документов: ");
                body.append("http://localhost:8090/swagger-ui/#/gateway-controller/sendDocsUsingPOST");
                mailSender.sendEmail(emailMessage.getAddress(), "Create documents", body.toString());
                break;

            case SEND_DOCUMENTS:
                body = new StringBuilder("Вам высланы доументы по заявке номер " + emailMessage.getApplicationId() + "\n");
                body.append("Проверьте правильность документов и пройдите по ссылке для запроса SES кода: ");
                body.append("http://localhost:8090/swagger-ui/#/gateway-controller/singDocsUsingPOST");
                mailSender.sendEmailWithAttachment(emailMessage.getAddress(), "Your loan documents", body.toString(), emailMessage.getApplicationId());
                break;

            case SEND_SES:
                Integer sesCode = dealClient.getSummaryAppInfoFromDeal(emailMessage.getApplicationId()).getSesCode();
                body = new StringBuilder("Для заявки номер " + emailMessage.getApplicationId() + " SES код: " + sesCode + "\n");
                body.append("Пройдите по ссылке и введите SES код: ");
                body.append("http://localhost:8090/swagger-ui/#/gateway-controller/receiveSesCodeUsingPOST");
                mailSender.sendEmail(emailMessage.getAddress(), "Your SES code", body.toString());
                break;

            case CREDIT_ISSUED:
                body = new StringBuilder("Поздравляем! Кредит выдан!\n");
                body.append("Деньги скоро поступят Вам на счёт.");
                mailSender.sendEmail(emailMessage.getAddress(), "Credit Issued", body.toString());
                break;

            case APPLICATION_DENIED:
                body = new StringBuilder("Ваша заявка с номером " + emailMessage.getApplicationId() + " отклонена\n");
                mailSender.sendEmail(emailMessage.getAddress(), "Application denied!", body.toString());
                break;
        }
    }
}
