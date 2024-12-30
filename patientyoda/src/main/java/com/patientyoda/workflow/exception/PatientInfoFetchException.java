package com.patientyoda.workflow.exception;

import org.springframework.core.NestedRuntimeException;

public class PatientInfoFetchException extends NestedRuntimeException {


    public PatientInfoFetchException(String msg, Throwable cause) {
        super(msg,cause);
    }
}
