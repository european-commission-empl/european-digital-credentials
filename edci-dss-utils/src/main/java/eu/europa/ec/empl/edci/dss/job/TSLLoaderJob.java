package eu.europa.ec.empl.edci.dss.job;

import eu.europa.esig.dss.tsl.service.TSLValidationJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class TSLLoaderJob {

//    @Value("${cron.tl.loader.enable}")
//    private boolean enable;

    @Autowired
    private TSLValidationJob job;

    @PostConstruct
    public void init() {
        job.initRepository();
    }

    @Scheduled(initialDelayString = "0", fixedDelayString = "86400000")
    public void refresh() {
        //if (enable) {
        job.refresh();
        //}
    }

}
