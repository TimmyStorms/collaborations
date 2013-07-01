package com.github.timmystorms.collaborations.entity;

import com.github.timmystorms.collaborations.entity.vo.TagVO;

public interface BaseCollaboration {

	public abstract String getName();
	
	public abstract String getDescription();
	
	public abstract long getStartDate();
		
	public abstract long getEndDate();
	
	public abstract Visibility getVisibility();
	
	public abstract Iterable<TagVO> getTags();
	
	public enum Visibility {
		VISIBLE, HIDDEN, DELETED;
	}
	
}
