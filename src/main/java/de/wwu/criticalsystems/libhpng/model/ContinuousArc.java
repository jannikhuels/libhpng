package de.wwu.criticalsystems.libhpng.model;

import javax.xml.bind.annotation.*;

import de.wwu.criticalsystems.libhpng.errorhandling.ModelCopyingFailedException;

@XmlRootElement (name = "continuousArc")
public class ContinuousArc extends Arc{

	public ContinuousArc(){}
	
	public ContinuousArc(String id, Double weight, Place connectedPlace,
			Transition connectedTransition, ContinuousArcType direction, Double share,
			Integer priority) {
		super(id, weight, connectedPlace, connectedTransition);
		this.direction = direction;
		this.share = share;
		this.priority = priority;
	}
	
	public ContinuousArc(ContinuousArc arcToCopy) throws ModelCopyingFailedException {
		super(new String(arcToCopy.getId()), new Double(arcToCopy.getWeight()), new String(arcToCopy.getFromNode()), new String(arcToCopy.getToNode()));
		this.direction = arcToCopy.getDirection();
		this.share = new Double(arcToCopy.getShare());
		this.priority = new Integer(arcToCopy.getPriority());
	}

	public static enum ContinuousArcType{input, output}
	
	public ContinuousArcType getDirection() {
		return direction;
	}
	public void setDirection(ContinuousArcType direction) {
		this.direction = direction;
	}
	
	public Double getShare() {
		return share;
	}
	@XmlAttribute(name = "share")
	public void setShare(Double share) {
		this.share = share;
	}
	
	public Integer getPriority() {
		return priority;
	}
	@XmlAttribute(name = "priority")
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	
	private ContinuousArcType direction;
	private Double share;
	private Integer priority;		
}
