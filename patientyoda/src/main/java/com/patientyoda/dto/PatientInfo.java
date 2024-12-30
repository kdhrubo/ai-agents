package com.patientyoda.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public record PatientInfo(
            String givenName,
            String familyName,
            String birthDate,
            String gender,
            List<ConditionInfo> conditions,
            List<EncounterInfo> recentEncounters,
            List<MedicationInfo> currentMedications
    ) {
        public String demographics() {
            ToStringBuilder b = new ToStringBuilder(this);

            return String.format(
                    "Given name: %s\nFamily name: %s\nBirth date: %s\nGender: %s",
                    givenName, familyName, birthDate, gender
            );
        }



    }