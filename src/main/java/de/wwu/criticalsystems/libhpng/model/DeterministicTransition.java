package de.wwu.criticalsystems.libhpng.model;

import javax.xml.bind.annotation.*;

@XmlRootElement( name = "deterministicTransition" )
public class DeterministicTransition extends Transition{

	public DeterministicTransition(){}
	
	public DeterministicTransition(String id, Boolean enabled, Double weight,
			Integer priority, Double firingTime, Double clock) {
		super(id, enabled);
		this.weight = weight;
		this.priority = priority;
		this.firingTime = firingTime;
		this.clock = clock;
	}
	
	public DeterministicTransition(DeterministicTransition transitionToCopy) {
		super(new String(transitionToCopy.getId()), new Boolean (transitionToCopy.getEnabled()));
		this.weight = new Double (transitionToCopy.getWeight());
		this.priority = new Integer (transitionToCopy.getPriority());
		this.firingTime = new Double (transitionToCopy.getFiringTime());
		this.clock = new Double (transitionToCopy.getClock());
	}

	public Double getWeight() {
		return weight;
	}	
	@XmlAttribute(name = "weight")
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	
	public Integer getPriority() {
		return priority;
	}	
	@XmlAttribute(name = "priority")
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	
	public Double getFiringTime() {
		return firingTime;
	}	
	@XmlAttribute(name = "discTime")
	public void setFiringTime(Double firingTime) {
		this.firingTime = firingTime;
	}
	
	public Double getClock() {
		return clock;
	}
	public void setClock(Double clock) {
		this.clock = clock;
	}
	
	private Double weight;
	private Integer priority;
	private Double firingTime;
	private Double clock;
}
