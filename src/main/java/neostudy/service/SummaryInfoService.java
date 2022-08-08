package neostudy.service;

import lombok.RequiredArgsConstructor;
import neostudy.dto.SummaryAppInfo;
import neostudy.feignclient.DealClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SummaryInfoService {

    private final DealClient dealClient;

    public SummaryAppInfo getSumInfoFromDealClient(Long id) {
        return dealClient.getSummaryAppInfoFromDeal(id);
    }

}
