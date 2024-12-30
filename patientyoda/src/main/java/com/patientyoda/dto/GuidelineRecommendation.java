package com.patientyoda.dto;

/**
 * Represents a medical guideline recommendation with its source and details.
 * 
 * @param guidelineSource The origin of the guideline (e.g., 'NHLBI Asthma Guidelines').
 * @param recommendationSummary A concise summary of the relevant recommendation.
 * @param referenceSection Specific section or reference in the guideline.
 */
public record GuidelineRecommendation(
    String guidelineSource,
    String recommendationSummary,
    String referenceSection
) {
}