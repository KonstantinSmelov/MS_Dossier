package neostudy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import neostudy.dto.PaymentScheduleElement;
import neostudy.dto.SummaryAppInfo;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocsGenerationService {

    private final SummaryInfoService summaryInfoService;
    private SummaryAppInfo summaryInfo;

    public File generateLoanAppFile(Long id) {
        summaryInfo = summaryInfoService.getSumInfoFromDealClient(id);

        StringBuilder sb = new StringBuilder("\t\t КРЕДИТНАЯ ЗАЯВКА № " + id)
                .append("\n\t\t----------------------")
                .append("\nФИО: ").append(summaryInfo.getFullName())
                .append("\nДата рождения: ").append(summaryInfo.getBirthdate().toString())
                .append("\nПол: ").append(summaryInfo.getGender())
                .append("\nПаспорт: ").append(summaryInfo.getFullPassportData())
                .append("\nEmail: ").append(summaryInfo.getEmail())
                .append("\nСемейное положение: ").append(summaryInfo.getMartialStatus())
                .append("\nКол-во иждивенцев: ").append(summaryInfo.getDependentAmount())
                .append("\n\tДанные о работе:")
                .append("\n\tТекущий статус: ").append(summaryInfo.getEmployment().getEmploymentStatus())
                .append("\n\tЗарплата: ").append(summaryInfo.getEmployment().getSalary())
                .append("\n\tДолжность: ").append(summaryInfo.getEmployment().getPosition())
                .append("\n\tОпыт работы (общий): ").append(summaryInfo.getEmployment().getWorkExperienceTotal())
                .append("\n\tОпыт работы (текущий): ").append(summaryInfo.getEmployment().getWorkExperienceCurrent());

        File file = null;
        try {
            file = File.createTempFile("кредитная_заявка", ".txt", null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.debug("generateLoanAppFile(): создан временный файл: {}", file.getAbsoluteFile() + file.getName());

        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.debug("generateLoanAppFile(): заполнен файл: {}", file.getAbsoluteFile() + file.getName());
        return file;
    }

    public File generateLoanContractFile(Long id) {

        StringBuilder sb = new StringBuilder("\t\t КРЕДИТНЫЙ ДОГОВОР № " + id)
                .append("\n\t\t------------------------")
                .append("\nФИО: ").append(summaryInfo.getFullName())
                .append("\nПаспорт: ").append(summaryInfo.getFullPassportData())
                .append("\n\nДанные о кредите:")
                .append("\n\tСумма кредита: ").append(summaryInfo.getAmount().toString())
                .append("\n\tСрок кредита: ").append(summaryInfo.getTerm())
                .append("\n\tЕжемесячный платёж: ").append(summaryInfo.getMonthlyPayment())
                .append("\n\tПроцентная ставка: ").append(summaryInfo.getRate())
                .append("\n\tПолная стоимость кредита: ").append(summaryInfo.getPsk())
                .append("\n\tСтраховка включена: ").append(summaryInfo.getIsInsuranceEnabled())
                .append("\n\tЗарплатный клиент: ").append(summaryInfo.getIsInsuranceEnabled());


        File file = null;
        try {
            file = File.createTempFile("кредитный_договор", ".txt", null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.debug("generateLoanContractFile(): создан файл: {}", file.getAbsoluteFile() + file.getName());

        try (FileWriter fileWriter = new FileWriter(file)) {
            file.createNewFile();
            fileWriter.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.debug("generateLoanContractFile(): заполнен файл: {}", file.getAbsoluteFile() + file.getName());
        return file;
    }

    public File generateLoanPaymentFile(Long id) {

        StringBuilder sb = new StringBuilder("\t\t ПЛАТЕЖИ ПО ДОГОВОРУ № " + id)
                .append("\n\t\t------------------------");
        for (PaymentScheduleElement pse : summaryInfo.getPaymentScheduleElementList()) {
            sb.append("\nМесяц платежа № ").append(pse.getNumber())
                    .append("\n\tДата платежа: ").append(pse.getDate().toString())
                    .append("\n\tМесячный платёж: ").append(pse.getTotalPayment())
                    .append("\n\tПогашение процентов: ").append(pse.getInterestPayment())
                    .append("\n\tПогашение основного долга: ").append(pse.getDebtPayment())
                    .append("\n\tОстаток долга: ").append(pse.getRemainingDebt());
        }

        File file = null;
        try {
            file = File.createTempFile("платежи_по_договору", ".txt", null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.debug("generateLoanPaymentFile(): создан файл {}", file.getAbsoluteFile() + file.getName());

        try (FileWriter fileWriter = new FileWriter(file)) {
            file.createNewFile();
            fileWriter.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.debug("generateLoanPaymentFile(): заполнен файл {}", file.getAbsoluteFile() + file.getName());
        return file;
    }
}
