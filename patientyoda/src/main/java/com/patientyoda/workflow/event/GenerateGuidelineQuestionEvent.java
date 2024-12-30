package com.patientyoda.workflow.event;

import com.patientyoda.dto.ConditionBundles;
import com.patientyoda.dto.PatientInfo;

public record GenerateGuidelineQuestionEvent(PatientInfo patientInfo, ConditionBundles bundles) {
}
