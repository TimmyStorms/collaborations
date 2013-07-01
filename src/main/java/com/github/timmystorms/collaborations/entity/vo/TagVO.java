package com.github.timmystorms.collaborations.entity.vo;

import com.github.timmystorms.collaborations.entity.BaseTag;

public class TagVO implements BaseTag {
	
	private long id;
	
	private String name;

	@Override
	public String getName() {
		return name;
	}
	
	public long getId() {
		return id;
	}

}
