package de.wwu.criticalsystems.libhpng.model;

import javax.xml.bind.annotation.*;

@XmlRootElement (name = "continuousArc")
public abstract class ContinuousArc extends Arc{

	public ContinuousArc(String id, Double weight, Place connectedPlace,
			Transition connectedTransition, ArcType direction, Double share,
			Integer priority) {
		super(id, weight, connectedPlace, connectedTransition);
		this.direction = direction;
		this.share = share;
		this.priority = priority;
	}

	public ArcType getDirection() {
		return direction;
	}
	public void setDirection(ArcType direction) {
		this.direction = direction;
	}
	public Double getShare() {
		return share;
	}
	public void setShare(Double share) {
		this.share = share;
	}
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	
	private ArcType direction;
	private Double share;
	private Integer priority;
		
}
