package com.patientyoda.dto;

/**
 * Record representing a condition summary with its display name and narrative summary.
 */
public record ConditionSummary(
    String conditionDisplay,  // Human-readable name of the condition
    String summary           // A concise narrative summarizing the condition's status
) {}