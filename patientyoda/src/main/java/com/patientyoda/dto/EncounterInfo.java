package com.patientyoda.dto;

public record EncounterInfo(String date, String reasonDisplay, String typeDisplay) {

    public String toString(){
        return String.format(
                "Encounter Date: %s,Reason Display: %s,Type Display: %s\n",
                date, reasonDisplay, typeDisplay
        );
    }
}