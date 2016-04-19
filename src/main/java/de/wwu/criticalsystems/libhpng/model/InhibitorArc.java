package de.wwu.criticalsystems.libhpng.model;

public class InhibitorArc extends GuardArc{

	public InhibitorArc(String id, Double weight, Place connectedPlace,
			Transition connectedTransition) {
		super(id, weight, connectedPlace, connectedTransition);
	}

}
