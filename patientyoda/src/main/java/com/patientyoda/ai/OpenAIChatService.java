package com.patientyoda.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OpenAIChatService<T> implements ChatService<T> {


    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final ChatClient chatClient;


    public OpenAIChatService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.defaultAdvisors(new SimpleLoggerAdvisor()).build();
    }

    @Override
    public T chat(Resource userPromptResource, Map<String,Object> userPromptData, Class<T> clazz) {
        PromptTemplate userPromptTemplate = new PromptTemplate(userPromptResource);
        Message userMessage = userPromptTemplate.createMessage(userPromptData);

        return chat(userMessage, clazz);
    }

    private T chat(Message message, Class<T> clazz) {
        return chatClient.prompt()
                .user(message.getContent())
                .call().entity(clazz);
    }


    private T chat(Message systemMessage, Message userMessage, Class<T> clazz) {
        return chatClient.prompt()
                .system(systemMessage.getContent())
                .user(userMessage.getContent())
                .call().entity(clazz);
    }

    @Override
    public T chat(Resource systemPromptResource, Map<String, Object> systemPromptData, Resource userPromptResource, Map<String, Object> userPromptData, Class<T> clazz) {
        PromptTemplate systemPromptTemplate = new PromptTemplate(systemPromptResource);
        Message systemMessage = systemPromptTemplate.createMessage(systemPromptData);

        PromptTemplate userPromptTemplate = new PromptTemplate(userPromptResource);
        Message userMessage = userPromptTemplate.createMessage(userPromptData);

        return chat(systemMessage, userMessage, clazz);
    }


}
