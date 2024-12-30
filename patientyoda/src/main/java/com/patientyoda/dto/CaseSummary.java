package com.patientyoda.dto;

import java.util.List;

/**
 * Record representing a comprehensive case summary for a patient.
 */
public record CaseSummary(
        String patientName,                  // The patient's name
        int age,                            // The patient's age in years
        String overallAssessment,           // High-level summary of all conditions
        List<ConditionSummary> conditionSummaries // List of condition-specific summaries
) {

    /**
     * Renders the case summary as a formatted string.
     * @return Formatted string representation of the case summary
     */
    public String render() {
        StringBuilder builder = new StringBuilder();
        builder.append("Patient Name: ").append(patientName).append("\n");
        builder.append("Age: ").append(age).append(" years\n\n");
        
        builder.append("Overall Assessment:\n");
        builder.append(overallAssessment).append("\n\n");
        
        if (!conditionSummaries.isEmpty()) {
            builder.append("Condition Summaries:\n");
            for (ConditionSummary csum : conditionSummaries) {
                builder.append("- ").append(csum.conditionDisplay()).append(":\n");
                builder.append("  ").append(csum.summary()).append("\n");
            }
        } else {
            builder.append("No specific conditions were summarized.\n");
        }
        
        return builder.toString();
    }
}
