package com.patientyoda.dto;

public record MedicationInfo(String name, String startDate, String instructions) {

    public String toString(){
        return String.format(
                "Name: %s,Start Date: %s,Instructions: %s\n",
                name, startDate, instructions
        );
    }
}