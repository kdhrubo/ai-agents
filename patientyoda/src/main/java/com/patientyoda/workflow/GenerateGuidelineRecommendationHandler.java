package com.patientyoda.workflow;

import com.patientyoda.ai.ChatService;
import com.patientyoda.ai.rag.QueryService;
import com.patientyoda.dto.ConditionGuidelineBundle;
import com.patientyoda.dto.ConditionQuestionBundle;
import com.patientyoda.dto.GuidelineRecommendation;
import com.patientyoda.workflow.event.GenerateCaseSummaryEvent;
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
import java.util.stream.Collectors;

@Component
public class GenerateGuidelineRecommendationHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(GenerateGuidelineRecommendationHandler.class);

    @Value("classpath:/prompt/GUIDELINE_RECOMMENDATION_PROMPT.st")
    Resource promptTemplateResource;

    private final ApplicationEventPublisher applicationEventPublisher;
    private final ChatService<GuidelineRecommendation> chatService;
    private final QueryService queryService;

    public GenerateGuidelineRecommendationHandler(ApplicationEventPublisher applicationEventPublisher, ChatService<GuidelineRecommendation> chatService, QueryService queryService) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.chatService = chatService;
        this.queryService = queryService;
    }

    @EventListener
    public void handle(GenerateGuidelineRecommendationEvent event) {
        List<ConditionQuestionBundle> conditionQuestionBundles = event.conditionQuestionBundles();

        LOGGER.info("## 4. Generating guideline recommendations with help of knowledge base.");

        if(Objects.nonNull(conditionQuestionBundles)) {

            List<ConditionGuidelineBundle> conditionGuidelineBundles = new ArrayList<>();

            for (ConditionQuestionBundle conditionQuestionBundle : conditionQuestionBundles) {

                String guidelines =
                        conditionQuestionBundle.guidelineQueries().queries()
                                .stream().map(
                                        queryService::query
                                )
                                .collect(Collectors.joining("\n"));


                LOGGER.debug("Guidelines: {}" , guidelines);

                GuidelineRecommendation recommendation =
                        chatService.chat(promptTemplateResource,
                                Map.of("patient_info"
                                        ,event.patientInfo().demographics(),
                                        "condition_info", conditionQuestionBundle.conditionBundle().condition(),
                                        "guideline_text", guidelines
                                ),
                                GuidelineRecommendation.class);

                LOGGER.info("## 4 Recommendation Summary: {}" , recommendation.recommendationSummary());

                conditionGuidelineBundles.add(new ConditionGuidelineBundle(conditionQuestionBundle.conditionBundle(),
                        recommendation));
            }

            applicationEventPublisher.publishEvent(new GenerateCaseSummaryEvent(event.patientInfo(), conditionGuidelineBundles));
        }

    }

}
