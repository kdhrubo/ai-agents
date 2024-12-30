package com.patientyoda.workflow;

import com.patientyoda.ai.ChatService;
import com.patientyoda.dto.ConditionBundles;
import com.patientyoda.workflow.event.CreateConditionBundlesEvent;
import com.patientyoda.workflow.event.GenerateGuidelineQuestionEvent;
import com.patientyoda.workflow.exception.CreateConditionBundlesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class CreateConditionBundlesHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(CreateConditionBundlesHandler.class);

    @Value("classpath:/prompt/CONDITION_BUNDLE.st")
    Resource promptTemplateResource;

    private final ApplicationEventPublisher applicationEventPublisher;
    private final ChatService<ConditionBundles> chatService;

    public CreateConditionBundlesHandler(ApplicationEventPublisher applicationEventPublisher, ChatService<ConditionBundles> chatService) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.chatService = chatService;
    }


    @EventListener
    public void handle(CreateConditionBundlesEvent event) {
        try {

            LOGGER.info("## 2.Contacting LLM to create condition bundles");

            ConditionBundles conditionBundles = chatService.chat(promptTemplateResource,
                    Map.of("patient_info"
                            ,event.patientInfo()),
                    ConditionBundles.class);

            LOGGER.info("## 2.Condition bundle created - {}", conditionBundles);


            applicationEventPublisher.publishEvent(new GenerateGuidelineQuestionEvent(event.patientInfo(), conditionBundles));

        } catch (Exception e) {
            throw new CreateConditionBundlesException("Failed to create conditions bundle", e);
        }
    }
}
