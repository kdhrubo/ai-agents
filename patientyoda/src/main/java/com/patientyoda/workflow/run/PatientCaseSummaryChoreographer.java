package com.patientyoda.workflow.run;

import com.patientyoda.workflow.event.StartCaseSummaryProcessingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class PatientCaseSummaryChoreographer implements CommandLineRunner {

    private final static Logger LOGGER = LoggerFactory.getLogger(PatientCaseSummaryChoreographer.class);

    private final ApplicationEventPublisher applicationEventPublisher;

    public PatientCaseSummaryChoreographer(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }


    @Override
    public void run(String... args) throws Exception {

        LOGGER.info("Starting patient case summary choreography");

        Resource patientInfoFile = new ClassPathResource("/data/almeta_buckridge.json");

        applicationEventPublisher.publishEvent(new StartCaseSummaryProcessingEvent(patientInfoFile, true));

        LOGGER.info("Completed patient case summary choreography");

    }
}
