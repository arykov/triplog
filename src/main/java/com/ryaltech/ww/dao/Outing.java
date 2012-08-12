package com.ryaltech.ww.dao;

import java.util.Date;

import com.ryaltech.orm.Entity;

public class Outing implements Entity {
	private long outingId;
	private long userId;
	private long riverId;
	private Date startTimeStamp;
	private Date endTimeStamp;
	private String notes;

	public long getOutingId() {
		return outingId;
	}

	public Outing setOutingId(long outingId) {
		this.outingId = outingId;
		return this;
	}

	public long getUserId() {
		return userId;
	}

	public Outing setUserId(long userId) {
		this.userId = userId;
		return this;
	}

	public long getRiverId() {
		return riverId;
	}

	public Outing setRiverId(long riverId) {
		this.riverId = riverId;
		return this;
	}

	public Date getStartTimeStamp() {
		return startTimeStamp;
	}

	public Outing setStartTimeStamp(Date startTimeStamp) {
		this.startTimeStamp = startTimeStamp;
		return this;
	}

	public Date getEndTimeStamp() {
		return endTimeStamp;
	}

	public Outing setEndTimeStamp(Date endTimeStamp) {
		this.endTimeStamp = endTimeStamp;
		return this;
	}

	@Override
	public long getId() {
		return getOutingId();
	}

	@Override
	public void setId(long id) {
		setOutingId(id);

	}

	public String getNotes() {
		return notes;
	}

	public Outing setNotes(String notes) {
		this.notes = notes;
		return this;
	}
	
	

}
