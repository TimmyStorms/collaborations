package com.github.timmystorms.collaborations.service;

import com.github.timmystorms.collaborations.entity.Collaboration;

public interface CollaborationService {
	
	Collaboration findById(Long id);
	
	Collaboration create(Collaboration collaboration);
	
	void update(Collaboration collaboration);
	
	// TODO remove
	Collaboration saveDummyCollaboration();

}
