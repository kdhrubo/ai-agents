package com.patientyoda.dto;

// Java Records to hold parsed data
public record ConditionInfo(String code, String display, String clinicalStatus) {

    public String toString(){
        return String.format(
                "Code: %s,Display: %s,Clinical Status: %s\n",
                code, display, clinicalStatus
        );
    }

}