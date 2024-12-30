package com.patientyoda.workflow;

import com.patientyoda.fhir.FhirService;
import com.patientyoda.dto.PatientInfo;
import com.patientyoda.workflow.event.CreateConditionBundlesEvent;
import com.patientyoda.workflow.event.StartCaseSummaryProcessingEvent;
import com.patientyoda.workflow.exception.PatientInfoFetchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;


@Component
public class GetPatientInfoHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(GetPatientInfoHandler.class);

    private final ApplicationEventPublisher applicationEventPublisher;
    private final FhirService fhirService;

    public GetPatientInfoHandler(ApplicationEventPublisher applicationEventPublisher, FhirService fhirService) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.fhirService = fhirService;
    }

    @EventListener
    public void handle(StartCaseSummaryProcessingEvent event) {
        LOGGER.info("## 1. Processing StartCaseSummaryProcessingEvent");
        try {
            String json =
            event.resource().getContentAsString(Charset.defaultCharset());

            PatientInfo patientInfo = fhirService.load(json, true);

            LOGGER.info("## 1. PatientInfo - {}", patientInfo);

            applicationEventPublisher.publishEvent(new CreateConditionBundlesEvent(patientInfo));
        } catch (Exception e) {
            throw new PatientInfoFetchException("Failed to load patient info", e);
        }
        LOGGER.info("## 1. Completed processing StartCaseSummaryProcessingEvent");
    }
}
