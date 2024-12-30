package com.patientyoda.workflow.exception;

import org.springframework.core.NestedRuntimeException;

public class CreateConditionBundlesException extends NestedRuntimeException {

    public CreateConditionBundlesException(String msg, Throwable cause) {
        super(msg,cause);
    }
}
