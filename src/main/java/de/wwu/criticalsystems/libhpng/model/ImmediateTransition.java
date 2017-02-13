package de.wwu.criticalsystems.libhpng.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "immediateTransition" )
public class ImmediateTransition extends Transition {

	public ImmediateTransition(){}
	
	public ImmediateTransition(String id, Boolean enabled, Double weight,
			Integer priority) {
		super(id, enabled);
		this.weight = weight;
		this.priority = priority;
	}
	
	public ImmediateTransition(ImmediateTransition transitionToCopy) {
		super(new String (transitionToCopy.getId()), new Boolean (transitionToCopy.getEnabled()));
		this.weight = new Double (transitionToCopy.getWeight());
		this.priority = new Integer (transitionToCopy.getPriority());
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

	private Double weight;
	private Integer priority;
}