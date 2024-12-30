package com.patientyoda.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.patientyoda.dto.ConditionInfo;
import com.patientyoda.dto.EncounterInfo;
import com.patientyoda.dto.MedicationInfo;
import com.patientyoda.dto.PatientInfo;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FhirService {


    private static final Logger LOGGER = LoggerFactory.getLogger(FhirService.class);


    public PatientInfo load(String jsonContent, boolean filterActive) throws IOException {
        // Parse JSON into a FHIR Bundle
        IParser jsonParser = FhirContext.forR4().newJsonParser();
        Bundle bundle = jsonParser.parseResource(Bundle.class, jsonContent);

        Patient patientResource = null;
        List<Condition> conditions = new ArrayList<>();
        List<Encounter> encounters = new ArrayList<>();
        List<MedicationRequest> medicationRequests = new ArrayList<>();

        // Extract resources by type
        for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
            Resource resource = entry.getResource();
            if (resource instanceof Patient) {
                patientResource = (Patient) resource;
            } else if (resource instanceof Condition) {
                conditions.add((Condition) resource);
            } else if (resource instanceof Encounter) {
                encounters.add((Encounter) resource);
            } else if (resource instanceof MedicationRequest) {
                medicationRequests.add((MedicationRequest) resource);
            }
        }

        if (patientResource == null) {
            throw new IllegalArgumentException("No Patient resource found in the provided file.");
        }

        // Extract patient demographics
        HumanName nameEntry = patientResource.getNameFirstRep();
        String givenName = nameEntry.getGivenAsSingleString();
        String familyName = nameEntry.getFamily();
        String birthDate = patientResource.getBirthDateElement().asStringValue();
        String gender = patientResource.getGender().toCode();

        // Define excluded conditions
        List<String> excludedConditions = List.of("Medication review due (situation)", "Risk activity involvement (finding)");
        List<ConditionInfo> conditionInfoList = conditions.stream()
                .filter(c -> !excludedConditions.contains(c.getCode().getCodingFirstRep().getDisplay()))
                .filter(c -> !filterActive || "active".equalsIgnoreCase(c.getClinicalStatus().getCodingFirstRep().getCode()))
                .map(c -> new ConditionInfo(
                        c.getCode().getCodingFirstRep().getCode(),
                        c.getCode().getCodingFirstRep().getDisplay(),
                        c.getClinicalStatus().getCodingFirstRep().getCode()
                ))
                .collect(Collectors.toList());

        // Parse encounters
        List<Encounter> recentEncounters = encounters.stream()
                .sorted(Comparator.comparing((Encounter e) -> e.getPeriod().getStart()).reversed())
                .limit(3)
                .toList();



        List<EncounterInfo> encounterInfoList = recentEncounters.stream()
                .map(e -> new EncounterInfo(
                        e.getPeriod().getStartElement().asStringValue(),
                        StringUtils.isNotBlank(e.getReasonCodeFirstRep().getCodingFirstRep().getDisplay())
                                ? e.getTypeFirstRep().getCodingFirstRep().getDisplay() : "None",
                        StringUtils.isNotBlank(e.getTypeFirstRep().getCodingFirstRep().getDisplay())
                                ? e.getTypeFirstRep().getCodingFirstRep().getDisplay() : "None"
                ))
                .collect(Collectors.toList());

        // Parse medications
        List<MedicationInfo> medicationInfoList = medicationRequests.stream()
                .filter(m -> "active".equalsIgnoreCase(m.getStatus().toCode()))
                .map(m -> {
                    String medName = m.getMedicationCodeableConcept().getCodingFirstRep().getDisplay();
                    String authored = m.getAuthoredOnElement().asStringValue();
                    String instructions = m.getDosageInstructionFirstRep().getText();
                    return new MedicationInfo(medName, authored, instructions);
                })
                .collect(Collectors.toList());

        // Build and return PatientInfo
        return new PatientInfo(
                givenName,
                familyName,
                birthDate,
                gender,
                conditionInfoList,
                encounterInfoList,
                medicationInfoList
        );
    }

}
