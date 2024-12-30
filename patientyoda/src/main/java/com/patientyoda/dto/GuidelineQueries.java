package com.patientyoda.dto;

import java.util.List;


/**
 * Represents a set of recommended queries to retrieve guideline sections relevant to the patient's conditions.
 */
public record GuidelineQueries(
    /**
     * A list of query strings that can be used to search a vector index of medical guidelines.
     */
    List<String> queries
) {


    @Override
    public String toString() {
        return String.join("\n", queries);
    }
}