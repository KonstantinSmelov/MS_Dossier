package neostudy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import neostudy.dto.EmailMessage;
import neostudy.feignclient.DealClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaListeners {

    private final ObjectMapper objectMapper;
    private final MailSender mailSender;
    private final DealClient dealClient;
    private final DocsGenerationService docsGenerationService;

    @Value("${PathToDocs}")
    private String path;

    @KafkaListener(topics = {"finish-registration", "create-documents", "send-documents",
            "send-ses", "credit-issued", "application-denied"}, groupId = "groupId")
    void listener(String str) throws JsonProcessingException {
        log.debug("listener(): приняли из кафки: {}", str);

        EmailMessage emailMessage = objectMapper.readValue(str, EmailMessage.class);
        log.debug("listener(): создали EmailMessage: {}", emailMessage);

        StringBuilder body;
        String[] pathToAttachments = new String[3];
        pathToAttachments[0] = path + "LoanApps/кредитное_предложение_" + emailMessage.getApplicationId() + ".txt";
        pathToAttachments[1] = path + "LoanContracts/кредитный_договор_" + emailMessage.getApplicationId() + ".txt";
        pathToAttachments[2] = path + "LoanPaymentList/платежи_по_договору_" + emailMessage.getApplicationId() + ".txt";

        switch (emailMessage.getTheme()) {
            case FINISH_REGISTRATION:
                body = new StringBuilder("Заявка номер " + emailMessage.getApplicationId() + " одобрена!\n");
                body.append("Для продолжения оформления пройдите по ссылке: ");
                body.append("http://localhost:8081/swagger-ui/#/deal-controller/choosingApplicationUsingPUT");
                mailSender.sendEmail(emailMessage.getAddress(), "Finish registration", body.toString());
                break;

            case CREATE_DOCUMENTS:
                body = new StringBuilder("Заявка номер " + emailMessage.getApplicationId() + " прошла проверку!\n");
                body.append("Для продолжения пройдите по ссылке для формирования пакета документов: ");
                body.append("http://localhost:8081/swagger-ui/#/deal-controller/sendDocsUsingPOST");
                mailSender.sendEmail(emailMessage.getAddress(), "Create documents", body.toString());
                break;

            case SEND_DOCUMENTS:
                body = new StringBuilder("Вам высланы доументы по заявке номер " + emailMessage.getApplicationId() + "\n");
                body.append("Проверьте правильность документов и пройдите по ссылке для запроса SES кода: ");
                body.append("http://localhost:8081/swagger-ui/#/deal-controller/singDocsUsingPOST");
                docsGenerationService.generatingAllDocs(emailMessage.getApplicationId());
                mailSender.sendEmailWithAttachment(emailMessage.getAddress(), "Your loan documents", body.toString(), pathToAttachments);
                break;

            case SEND_SES:
                Integer sesCode = dealClient.getSummaryAppInfoFromDeal(emailMessage.getApplicationId()).getSesCode();
                body = new StringBuilder("Для заявки номер " + emailMessage.getApplicationId() + " SES код: " + sesCode + "\n");
                body.append("Пройдите по ссылке и введите SES код: ");
                body.append("http://localhost:8081/swagger-ui/#/deal-controller/receiveSesCodeUsingPOST");
                mailSender.sendEmail(emailMessage.getAddress(), "Your SES code", body.toString());
                break;

            case CREDIT_ISSUED:
                body = new StringBuilder("Поздравляем! Кредит выдан!\n");
                body.append("Деньги скоро поступят Вам на счёт.");
                mailSender.sendEmail(emailMessage.getAddress(), "Credit Issued", body.toString());
                break;

            case APPLICATION_DENIED:
                body = new StringBuilder("Вы ввели неверный SES код для заявки номер "  + emailMessage.getApplicationId() + "\n");
                body.append("Пройдите по ссылке и повторите попытку ввода SES кода из письма с темой \"Your SES code\": ");
                body.append("http://localhost:8081/swagger-ui/#/deal-controller/receiveSesCodeUsingPOST");
                mailSender.sendEmail(emailMessage.getAddress(), "!!Wrong SES code!!", body.toString());
                break;
        }
    }
}
