package com.patientyoda.dto;

import java.util.List;

public record ConditionBundle(ConditionInfo condition, List<EncounterInfo> encounters, List<MedicationInfo> medications) {


    public String getText() {

        return String.format(
                "Condition: %s\nEncounter: %s\nMedication: %s",
                condition, encounters, medications
        );
    }
}
