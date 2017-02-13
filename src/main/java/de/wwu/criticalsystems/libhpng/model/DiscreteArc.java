package de.wwu.criticalsystems.libhpng.model;

import javax.xml.bind.annotation.*;

import de.wwu.criticalsystems.libhpng.errorhandling.ModelCopyingFailedException;

@XmlRootElement (name = "discreteArc")
public class DiscreteArc extends Arc{
	
	public DiscreteArc(){}
 
	public DiscreteArc(String id, Double weight, Place connectedPlace,
			Transition connectedTransition, DiscreteArcType direction) {
		super(id, weight, connectedPlace, connectedTransition);
		this.direction = direction;
	}
	
	public DiscreteArc(DiscreteArc arcToCopy) throws ModelCopyingFailedException {	
		super(new String(arcToCopy.getId()), new Double(arcToCopy.getWeight()), new String(arcToCopy.getFromNode()), new String(arcToCopy.getToNode()));
		this.direction = arcToCopy.getDirection();
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
