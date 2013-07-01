package com.github.timmystorms.collaborations.entity;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.neo4j.graphdb.Node;

import com.fasterxml.jackson.annotation.JsonIgnore;

abstract class NodeBacked {

	private Node node;

	public NodeBacked(final Node node) {
		this.node = node;
	}

	public Long getId() {
		return this.node.getId();
	}
	
	@Override
	public int hashCode() {
		return this.node.hashCode();
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	@JsonIgnore
	public Node getNode() {
		return this.node;
	}
	
}
