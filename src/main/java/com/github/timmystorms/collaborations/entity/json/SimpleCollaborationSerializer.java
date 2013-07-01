package com.github.timmystorms.collaborations.entity.json;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.timmystorms.collaborations.entity.Collaboration;

public class SimpleCollaborationSerializer extends JsonSerializer<Set<Collaboration>> {

    @Transactional
    @Override
    public void serialize(final Set<Collaboration> collaborations, final JsonGenerator generator,
            final SerializerProvider provider) throws IOException, JsonProcessingException {
        final Set<SimpleCollaboration> simpleCollaborations = new HashSet<>();
        for (final Collaboration collaboration : collaborations) {
        	// TODO make sure hidden collaborations can be seen for owners!
            if (Collaboration.Visibility.VISIBLE.equals(collaboration.getVisibility())) {
                simpleCollaborations.add(new SimpleCollaboration(collaboration.getId(), collaboration.getName(),
                        collaboration.getVisibility()));
            }
        }
        generator.writeObject(simpleCollaborations);
    }

}
