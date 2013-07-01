package com.github.timmystorms.collaborations.entity.vo;

import com.github.timmystorms.collaborations.entity.BaseCollaboration;

public class CollaborationVO implements BaseCollaboration {
	
	private String name;
	
	private String description;
	
	private long startDate;
	
	private long endDate;
	
	private String visibility;
	
	private Iterable<TagVO> tags;

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public long getStartDate() {
		return startDate;
	}

	public long getEndDate() {
		return endDate;
	}

	public Visibility getVisibility() {
		return Visibility.valueOf(visibility);
	}

	public Iterable<TagVO> getTags() {
		return tags;
	}

}
