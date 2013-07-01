package com.github.timmystorms.collaborations.entity;

import static com.github.timmystorms.collaborations.entity.util.Properties.COLLABORATIONS_JSON;
import static com.github.timmystorms.collaborations.entity.util.Properties.NAME;
import static com.github.timmystorms.collaborations.entity.util.Properties.NAME_JSON;

import org.apache.commons.lang.StringUtils;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.helpers.collection.IterableWrapper;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.Uniqueness;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.timmystorms.collaborations.entity.json.SimpleCollaborationSerializer;
import com.github.timmystorms.collaborations.entity.util.Indexes;
import com.github.timmystorms.collaborations.entity.util.Relationships;

public class Tag extends NodeBacked {

	public Tag(final Node node) {
		super(node);
	}

	
	public Tag(final String name, final Node node) {
		super(node);
		setName(name);
	}

	@JsonProperty(NAME_JSON)
	public String getName() {
		return (String) getNode().getProperty(NAME);
	}

	public void setName(final String name) {
		final Index<Node> index = getNode().getGraphDatabase().index().forNodes(Indexes.NAMES);
		getNode().setProperty(NAME, name);
		if (StringUtils.isNotEmpty(getName())) {
			index.remove(getNode(), NAME, name);
		}
		index.add(getNode(), NAME, name);
	}

	@JsonProperty(COLLABORATIONS_JSON)
	@JsonSerialize(using = SimpleCollaborationSerializer.class)
	private Iterable<Collaboration> getCollaborations(int depth) {
		return new IterableWrapper<Collaboration, Path>(Traversal.description().breadthFirst()
				.relationships(Relationships.HAS, Direction.INCOMING).uniqueness(Uniqueness.NODE_GLOBAL)
				.evaluator(Evaluators.atDepth(1)).evaluator(Evaluators.excludeStartPosition()).traverse(getNode())) {
			@Override
			protected Collaboration underlyingObjectToObject(final Path path) {
				return new Collaboration(path.endNode());
			}
		};
	}
	
	@Override
	public boolean equals(final Object o) {
		return o instanceof Tag && getNode().equals(((Tag) o).getNode());
	}

}
