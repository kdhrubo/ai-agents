package com.patientyoda.workflow;

import com.patientyoda.ai.ChatService;
import com.patientyoda.dto.ConditionBundle;
import com.patientyoda.dto.ConditionQuestionBundle;
import com.patientyoda.dto.GuidelineQueries;
import com.patientyoda.workflow.event.GenerateGuidelineQuestionEvent;
import com.patientyoda.workflow.event.GenerateGuidelineRecommendationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class GenerateGuidelineQuestionsHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(GenerateGuidelineQuestionsHandler.class);

    @Value("classpath:/prompt/GUIDELINE_QUERIES_PROMPT.st")
    Resource promptTemplateResource;

    private final ApplicationEventPublisher applicationEventPublisher;
    private final ChatService<GuidelineQueries> chatService;

    public GenerateGuidelineQuestionsHandler(ApplicationEventPublisher applicationEventPublisher, ChatService<GuidelineQueries> chatService) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.chatService = chatService;
    }

    @EventListener
    public void handle(GenerateGuidelineQuestionEvent event) {
        List<ConditionBundle> conditionBundleList = event.bundles().conditionBundles();

        if(Objects.nonNull(conditionBundleList)) {

            List<ConditionQuestionBundle> conditionQuestionBundles = new ArrayList<>();

            for (ConditionBundle conditionBundle : conditionBundleList) {
                GuidelineQueries guidelineQueries =
                        process(event, conditionBundle, conditionQuestionBundles);

                conditionQuestionBundles.add(new ConditionQuestionBundle(conditionBundle, guidelineQueries));
            }

            applicationEventPublisher.publishEvent(new
                    GenerateGuidelineRecommendationEvent(event.patientInfo(), conditionQuestionBundles));

        }

    }

    private GuidelineQueries process(GenerateGuidelineQuestionEvent event, ConditionBundle conditionBundle, List<ConditionQuestionBundle> conditionQuestionBundles) {
        LOGGER.info("## 3. Generating Questions for vector store.");

        GuidelineQueries guidelineQueries = chatService.chat(
                promptTemplateResource,
                Map.of("patient_info"
                        , event.patientInfo().demographics(),
                        "condition_info", conditionBundle.condition()
                ), GuidelineQueries.class);

        LOGGER.info("## 3. Guideline Queries: {}" , guidelineQueries);

        return guidelineQueries;
    }

}
