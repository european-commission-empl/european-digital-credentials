package eu.europa.ec.empl.edci.dss.job;

import eu.europa.esig.dss.tsl.job.TLValidationJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TLLoaderJob {

    @Autowired
    private TLValidationJob job;

    @Scheduled(initialDelayString = "0", fixedDelayString = "86400000")
    public void refresh() {
        job.onlineRefresh();
    }

}
