package com.ryaltech.ww.dao;

import com.ryaltech.orm.Entity;

public class River implements Entity {
	private long riverId;
	private String riverName;

	public long getRiverId() {
		return riverId;
	}

	public void setRiverId(long riverId) {
		this.riverId = riverId;
	}

	public String getRiverName() {
		return riverName;
	}

	public River setRiverName(String riverName) {
		this.riverName = riverName;
		return this;
	}

	@Override
	public long getId() {

		return getRiverId();
	}

	@Override
	public void setId(long id) {
		setRiverId(id);

	}

}
