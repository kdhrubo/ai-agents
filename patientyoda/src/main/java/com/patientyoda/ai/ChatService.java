package com.patientyoda.ai;

import org.springframework.ai.chat.messages.Message;
import org.springframework.core.io.Resource;

import java.util.Map;

public interface ChatService<T> {

    T chat(Resource userPromptResource, Map<String,Object> userPromptData, Class<T> clazz);
    T chat(
            Resource systemPromptResource, Map<String,Object> systemPromptData,
            Resource userPromptResource, Map<String,Object> userPromptData, Class<T> clazz);

}
