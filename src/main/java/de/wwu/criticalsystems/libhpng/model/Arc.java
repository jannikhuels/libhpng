package de.wwu.criticalsystems.libhpng.model;

import javax.xml.bind.annotation.*;

@XmlTransient
public abstract class Arc {

	public Arc(String id, Double weight, Place connectedPlace, 
			Transition connectedTransition) {
		this.id = id;
		this.weight = weight;
		this.connectedPlace = connectedPlace;
		this.connectedTransition = connectedTransition;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	public Place getConnectedPlace() {
		return connectedPlace;
	}
	public void setConnectedPlace(Place connectedPlace) {
		this.connectedPlace = connectedPlace;
	}
	public Transition getConnectedTransition() {
		return connectedTransition;
	}
	public void setConnectedTransition(Transition connectedTransition) {
		this.connectedTransition = connectedTransition;
	}
	
	private String id;
	private Double weight;
	private Place connectedPlace;
	private Transition connectedTransition;
	
}
