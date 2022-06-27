package neostudy.feignclient;

import neostudy.dto.SummaryAppInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name="deal-service-client", url = "${URL.toDeal}")
public interface DealClient {

    @PostMapping("/deal/application/{applicationId}")
    SummaryAppInfo getSummaryAppInfoFromDeal(@PathVariable Long applicationId);
}
