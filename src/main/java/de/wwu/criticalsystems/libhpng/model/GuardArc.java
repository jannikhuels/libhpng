package de.wwu.criticalsystems.libhpng.model;

public abstract class GuardArc extends Arc{

	public GuardArc(String id, Double weight, Place connectedPlace,
			Transition connectedTransition) {
		super(id, weight, connectedPlace, connectedTransition);
	}

}
