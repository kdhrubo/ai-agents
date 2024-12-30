package com.patientyoda.workflow.event;

import com.patientyoda.dto.ConditionGuidelineBundle;
import com.patientyoda.dto.PatientInfo;

import java.util.ArrayList;
import java.util.List;

public record GenerateCaseSummaryEvent(PatientInfo patientInfo, List<ConditionGuidelineBundle> conditionGuidelineBundles) {

    public String getConditionGuideline() {

        List<String> conditionGuidelineList = new ArrayList<>();

        for(ConditionGuidelineBundle conditionGuidelineBundle : conditionGuidelineBundles) {
            String conditionGuideline = "**** Condition Info **** \n"
                    + conditionGuidelineBundle.conditionBundle().condition() + "\n"
                    +"*** Recommendation **** \n" + conditionGuidelineBundle.recommendation() + "\n";

            conditionGuidelineList.add(conditionGuideline);
        }

        return String.join("\n", conditionGuidelineList);
    }
}
