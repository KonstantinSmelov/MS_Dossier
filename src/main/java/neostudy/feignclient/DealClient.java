package neostudy.feignclient;

import neostudy.dto.SummaryAppInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "deal-service-client", url = "${services.deal.url}")
public interface DealClient {

    @GetMapping("/deal/application/{applicationId}")
    SummaryAppInfo getSummaryAppInfoFromDeal(@PathVariable Long applicationId);
}
