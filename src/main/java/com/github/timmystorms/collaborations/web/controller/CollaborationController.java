package com.github.timmystorms.collaborations.web.controller;

import org.neo4j.graphdb.GraphDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.timmystorms.collaborations.entity.Collaboration;
import com.github.timmystorms.collaborations.entity.util.Labels;
import com.github.timmystorms.collaborations.entity.vo.CollaborationVO;
import com.github.timmystorms.collaborations.service.CollaborationService;

@Controller
@RequestMapping(value = CollaborationController.CONTEXT_PATH)
public class CollaborationController {

	private static final String JSON_CONTENT_TYPE = "content-type=" + MediaType.APPLICATION_JSON_VALUE;
	
	public static final String CONTEXT_PATH = "/collaborations";
	
	@Autowired
	private CollaborationService service;
	
	@Autowired
	private GraphDatabaseService db;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody Collaboration getCollaboration(final @PathVariable Long id) {
		return service.findById(id);
	}
	
	@Transactional
	@RequestMapping(method = RequestMethod.POST, headers = JSON_CONTENT_TYPE)
	public @ResponseBody ResponseEntity<Collaboration> persist(final @RequestBody CollaborationVO values, 
			final UriComponentsBuilder builder) {
		final Collaboration collab = new Collaboration(db.createNode(Labels.COLLAB));
		collab.setName(values.getName());
		collab.setDescription(values.getDescription());
		collab.setStartDate(values.getStartDate());
		collab.setEndDate(values.getEndDate());
		// TODO add tags
        final HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path(CONTEXT_PATH + "/{id}").buildAndExpand(collab.getId()).toUri());
        return new ResponseEntity<Collaboration>(collab, headers, HttpStatus.CREATED);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = JSON_CONTENT_TYPE)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void update(final @RequestBody Collaboration collaboration, final @PathVariable String id)  {
		service.update(collaboration);
	}
	
	// TODO remove
	@RequestMapping(value = "/dummy", method = RequestMethod.POST)
	public @ResponseBody Collaboration saveDummyCollaboration() {
		return service.saveDummyCollaboration();
	}

}
