package de.wwu.criticalsystems.libhpng.model;

import javax.xml.bind.annotation.*;

@XmlRootElement (name = "discreteArc")
public class DiscreteArc extends Arc{
	
	public DiscreteArc(){}
 
	public DiscreteArc(String id, Double weight, Place connectedPlace,
			Transition connectedTransition, DiscreteArcType direction) {
		super(id, weight, connectedPlace, connectedTransition);
		this.direction = direction;
	}
	
	public static enum DiscreteArcType{input, output}

	public DiscreteArcType getDirection() {
		return direction;
	}

	public void setDirection(DiscreteArcType direction) {
		this.direction = direction;
	}

	private DiscreteArcType direction;
	
}
