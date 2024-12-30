package com.patientyoda.workflow.event;

import com.patientyoda.dto.PatientInfo;

public record CreateConditionBundlesEvent(PatientInfo patientInfo) {
}
