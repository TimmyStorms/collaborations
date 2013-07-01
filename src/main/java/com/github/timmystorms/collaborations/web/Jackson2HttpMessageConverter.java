package com.github.timmystorms.collaborations.web;

import java.io.IOException;

import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

public final class Jackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {

    @Override
    protected void writeInternal(final Object object, final HttpOutputMessage outputMessage) throws IOException,
            HttpMessageNotWritableException {
//    	getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    	getObjectMapper().writeValueAsString(object);
    }
}
