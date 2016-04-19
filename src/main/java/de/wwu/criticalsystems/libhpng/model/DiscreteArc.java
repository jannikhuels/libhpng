package de.wwu.criticalsystems.libhpng.model;

public abstract class DiscreteArc extends Arc{
 
	public DiscreteArc(String id, Double weight, Place connectedPlace,
			Transition connectedTransition, ArcType direction) {
		super(id, weight, connectedPlace, connectedTransition);
		this.direction = direction;
	}

	public ArcType getDirection() {
		return direction;
	}

	public void setDirection(ArcType direction) {
		this.direction = direction;
	}

	private ArcType direction;
	
}
