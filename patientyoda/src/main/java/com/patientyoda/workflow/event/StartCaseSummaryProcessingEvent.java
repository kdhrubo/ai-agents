package com.patientyoda.workflow.event;

import org.springframework.core.io.Resource;

public record StartCaseSummaryProcessingEvent(Resource resource, boolean activeFilter) {
}
