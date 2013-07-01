package com.github.timmystorms.collaborations.service.impl;

import org.neo4j.graphdb.GraphDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.timmystorms.collaborations.entity.Collaboration;
import com.github.timmystorms.collaborations.entity.Tag;
import com.github.timmystorms.collaborations.entity.Collaboration.Visibility;
import com.github.timmystorms.collaborations.entity.util.Labels;
import com.github.timmystorms.collaborations.service.CollaborationService;

@Service
@Transactional
public class CollaborationServiceImpl implements CollaborationService {
	
	@Autowired
	private GraphDatabaseService db;

	@Override
	public final Collaboration findById(final Long id) {
		return new Collaboration(db.getNodeById(id));
	}
	
	@Override
	public Collaboration create(final Collaboration collaboration) {
		final Collaboration newCollab = new Collaboration(db.createNode(Labels.COLLAB));
		
		return newCollab;
	}

	@Override
	public void update(final Collaboration collaboration) {
		
	}

	@Override
	public Collaboration saveDummyCollaboration() {
		final Collaboration collab = new Collaboration(db.createNode(Labels.COLLAB));
		collab.setName("Dummy Collaboration");
		collab.setDescription("This is a basic description of a dummy collaboration");
		collab.setStartDate(System.currentTimeMillis() - 10000);
		collab.setEndDate(System.currentTimeMillis());
		collab.setVisibility(Visibility.VISIBLE);
		collab.addTag(new Tag("dummy", db.createNode(Labels.TAG)));
		collab.addTag(new Tag("test", db.createNode(Labels.TAG)));
		return collab;
	}

}
