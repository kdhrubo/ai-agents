package com.patientyoda.workflow.event;

import com.patientyoda.dto.ConditionQuestionBundle;
import com.patientyoda.dto.PatientInfo;

import java.util.List;

public record GenerateGuidelineRecommendationEvent(PatientInfo patientInfo,
                                                   List<ConditionQuestionBundle> conditionQuestionBundles) {

}
