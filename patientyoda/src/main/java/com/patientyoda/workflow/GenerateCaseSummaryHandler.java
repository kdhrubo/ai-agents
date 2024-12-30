package com.patientyoda.workflow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.patientyoda.ai.ChatService;
import com.patientyoda.dto.CaseSummary;
import com.patientyoda.dto.PatientInfo;
import com.patientyoda.workflow.event.GenerateCaseSummaryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GenerateCaseSummaryHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateCaseSummaryHandler.class);

    @Value("classpath:/prompt/CASE_SUMMARY_USER_PROMPT.st")
    Resource userPromptTemplateResource;


    @Value("classpath:/prompt/CASE_SUMMARY_SYSTEM_PROMPT.st")
    Resource systemPromptTemplateResource;

    private final ChatService<CaseSummary> chatService;
    private final ObjectMapper objectMapper;

    public GenerateCaseSummaryHandler(ChatService<CaseSummary> chatService, ObjectMapper objectMapper) {
        this.chatService = chatService;
        this.objectMapper = objectMapper;
    }

    @EventListener
    public void handle(GenerateCaseSummaryEvent event) throws JsonProcessingException {
        LOGGER.info("## 5. Generate Case Summary");

        PatientInfo patientInfo = event.patientInfo();
        String conditionGuidelineList = event.getConditionGuideline();

        CaseSummary summary =
        this.chatService.chat(systemPromptTemplateResource, Map.of(),
                userPromptTemplateResource,
                Map.of("demographic_info", patientInfo.demographics(),
                        "condition_guideline_info", conditionGuidelineList
                ),
                CaseSummary.class);

        LOGGER.info("## 5. Summary: {}" ,
                objectMapper.writeValueAsString(summary));
    }

}
