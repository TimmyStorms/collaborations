package com.github.timmystorms.collaborations.entity;

import static com.github.timmystorms.collaborations.entity.util.Properties.DESCRIPTION;
import static com.github.timmystorms.collaborations.entity.util.Properties.DESCRIPTION_JSON;
import static com.github.timmystorms.collaborations.entity.util.Properties.END;
import static com.github.timmystorms.collaborations.entity.util.Properties.END_JSON;
import static com.github.timmystorms.collaborations.entity.util.Properties.NAME;
import static com.github.timmystorms.collaborations.entity.util.Properties.NAME_JSON;
import static com.github.timmystorms.collaborations.entity.util.Properties.START;
import static com.github.timmystorms.collaborations.entity.util.Properties.START_JSON;
import static com.github.timmystorms.collaborations.entity.util.Properties.TAGS_JSON;
import static com.github.timmystorms.collaborations.entity.util.Properties.VISIBILITY;
import static com.github.timmystorms.collaborations.entity.util.Properties.VISIBILITY_JSON;

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
import com.github.timmystorms.collaborations.entity.util.Indexes;
import com.github.timmystorms.collaborations.entity.util.Relationships;

public class Collaboration extends NodeBacked {

	public Collaboration(final Node node) {
		super(node);
	}

	@JsonProperty(NAME_JSON)
	public String getName() {
		return (String) getNode().getProperty(NAME);
	}

	@JsonProperty(DESCRIPTION_JSON)
	public String getDescription() {
		return (String) getNode().getProperty(DESCRIPTION, null);
	}

	@JsonProperty(START_JSON)
	public long getStartDate() {
		return (Long) getNode().getProperty(START, 0l);
	}

	@JsonProperty(END_JSON)
	public long getEndDate() {
		return (Long) getNode().getProperty(END, 0l);
	}

	@JsonProperty(VISIBILITY_JSON)
	public Visibility getVisibility() {
		return Visibility.valueOf((String) getNode().getProperty(VISIBILITY));
	}

	@JsonProperty(TAGS_JSON)
	public Iterable<Tag> getTags() {
		return new IterableWrapper<Tag, Path>(Traversal.description().breadthFirst()
				.relationships(Relationships.HAS, Direction.OUTGOING).uniqueness(Uniqueness.NODE_GLOBAL)
				.evaluator(Evaluators.atDepth(1)).evaluator(Evaluators.excludeStartPosition()).traverse(getNode())) {
			@Override
			protected Tag underlyingObjectToObject(final Path path) {
				return new Tag(path.endNode());
			}
		};
	}

	public void setName(final String name) {
		final Index<Node> index = getNode().getGraphDatabase().index().forNodes(Indexes.NAMES);
		getNode().setProperty(NAME, name);
		if (StringUtils.isNotEmpty(getName())) {
			index.remove(getNode(), NAME, name);
		}
		index.add(getNode(), NAME, name);
	}

	public void setDescription(final String description) {
		if (StringUtils.isBlank(description)) {
			return;
		}
		getNode().setProperty(DESCRIPTION, description);
	}

	public void setStartDate(final long startDate) {
		if (startDate <= 0) {
			return;
		}
		getNode().setProperty(START, startDate);
	}

	public void setEndDate(final long endDate) {
		if (endDate <= 0) {
			return;
		}
		getNode().setProperty(END, endDate);
	}

	public void setVisibility(final Visibility visibility) {
		if (visibility == null) {
			return;
		}
		getNode().setProperty(VISIBILITY, visibility.name());
	}

	public void addTag(final Tag tag) {
		if (!Traversal.description().breadthFirst().relationships(Relationships.HAS, Direction.OUTGOING)
				.uniqueness(Uniqueness.NODE_GLOBAL).evaluator(Evaluators.atDepth(1))
				.evaluator(Evaluators.excludeStartPosition())
				.evaluator(Evaluators.includeWhereEndNodeIs(tag.getNode())).traverse(getNode()).iterator().hasNext()) {
			getNode().createRelationshipTo(tag.getNode(), Relationships.HAS);
		}
	}

	@Override
	public boolean equals(final Object o) {
		return o instanceof Collaboration && getNode().equals(((Collaboration) o).getNode());
	}
	
	public enum Visibility {
		VISIBLE, HIDDEN, DELETED;
	}

}
